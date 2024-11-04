package com.albsig.stundenmanager.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.albsig.stundenmanager.common.FirestoreUtil;
import com.albsig.stundenmanager.databinding.FragmentDashboardBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    @Nullable
    private FragmentDashboardBinding binding;
    private SessionsAdapter sessionsAdapter;
    private FirebaseFirestore db;
    private FirestoreUtil firestoreUtil;
    private String userId;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private static String TAG = "DashboardFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        firestoreUtil = new FirestoreUtil();
        db = firestoreUtil.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        } else {
            Log.e("DashboardFragment", "No userId found in arguments");
        }

        FloatingActionButton fabAddSession = binding.fabAddSession;
        fabAddSession.setOnClickListener(v -> {
            showDatePicker();
        });

        setupRecyclerView();
        loadSessionDates();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.recyclerViewSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        sessionsAdapter = new SessionsAdapter(this.binding.getRoot().getContext(), new ArrayList<>(), new ArrayList<>(), userId);
        binding.recyclerViewSessions.setAdapter(sessionsAdapter);
    }

    private void loadSessionDates() {
        db.collection("users").document(userId).collection("sessions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> sessionDates = new ArrayList<>();
                        List<String> sessionIds = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            Timestamp startTime = document.getTimestamp("startTime");
                            if (startTime != null) {
                                String formattedDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startTime.toDate());
                                sessionDates.add(formattedDate);
                                sessionIds.add(document.getId());
                            }
                        }
                        sessionsAdapter.updateData(sessionDates, sessionIds);
                    } else {
                        Log.e("DashboardFragment", "Error getting session dates", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this.binding.getRoot().getContext(),
                (view, yearSelected, monthSelected, daySelected) -> {
                    selectedYear = yearSelected;
                    selectedMonth = monthSelected;
                    selectedDay = daySelected;
                    showTimePicker();
                }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.binding.getRoot().getContext(),
                (view, hourOfDay, minuteSelected) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteSelected;

                    Timestamp customStartTime = createCustomTimestamp(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

                    startWorkSessionWithCustomStartTime(customStartTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private Timestamp createCustomTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }

    private void startWorkSessionWithCustomStartTime(Timestamp customStartTime) {
        firestoreUtil.startWorkSession(userId, customStartTime, new FirestoreUtil.FirestoreCallback() {
            @Override
            public void onSuccess(String sessionId) {
                Log.d(TAG, "Started session with ID: " + sessionId);
                loadSessionDates();
            }

            @Override
            public void onQuerySuccess(QuerySnapshot querySnapshot) {
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(binding.getRoot().getContext(), "A work session for this day already exists.", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Error starting session", e);
            }
        });
    }
}