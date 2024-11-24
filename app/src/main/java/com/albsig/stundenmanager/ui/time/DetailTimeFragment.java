package com.albsig.stundenmanager.ui.time;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentDetailTimeBinding;
import com.google.firebase.Timestamp;

public class DetailTimeFragment extends Fragment implements BreakAdapter.OnBreakDeletedListener {

    private static final String TAG = "DetailTimeFragment";

    private Context mContext;
    private FragmentDetailTimeBinding binding;
    private UserViewModel userViewModel;
    private SessionViewModel sessionViewModel;
    private BreakAdapter breakAdapter;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private Timestamp selectedBreakStartTime;

    public enum TimestampTarget {
        END_SESaSION,
        BREAK_START,
        BREAK_END
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedViewModel();
//        loadSessionDetails();
    }

    private void initSharedViewModel() {
        userViewModel  = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    }

//    private void loadSessionDetails() {
//        db.collection("users")
//                .document(userId)
//                .collection("sessions")
//                .document(sessionId)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        DocumentSnapshot document = task.getResult();
//                        Timestamp startTime = document.getTimestamp("startTime");
//                        Timestamp endTime = document.getTimestamp("endTime");
//                        List<Map<String, Timestamp>> breaks = (List<Map<String, Timestamp>>) document.get("breaks");
//
//                        if (startTime != null) {
//                            String formattedStartTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(startTime.toDate());
//                            startTimeTextView.setText(formattedStartTime);
//                        } else {
//                            startTimeTextView.setText("Not set");
//                        }
//
//                        if (endTime != null) {
//                            String formattedEndTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(endTime.toDate());
//                            endTimeTextView.setText(formattedEndTime);
//                            addEndTimeButton.setVisibility(View.GONE);
//                        } else {
//                            endTimeTextView.setText("Not set");
//                            addEndTimeButton.setVisibility(View.VISIBLE);
//                        }
//
//                        breakAdapter.setBreaks(breaks);
//                    } else {
//                        Toast.makeText(this, "Failed to load session details", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void showDatePicker(TimestampTarget target) {
//        final Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                (view, yearSelected, monthSelected, daySelected) -> {
//                    selectedYear = yearSelected;
//                    selectedMonth = monthSelected;
//                    selectedDay = daySelected;
//                    showTimePicker(target);
//                }, year, month, day);
//
//        datePickerDialog.show();
//    }
//
//    private void showTimePicker(TimestampTarget target) {
//        final Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                (view, hourOfDay, minuteSelected) -> {
//                    selectedHour = hourOfDay;
//                    selectedMinute = minuteSelected;
//
//                    Timestamp customTimestamp = createCustomTimestamp(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
//
//                    handleTimestampSelection(target, customTimestamp);
//                }, hour, minute, true);
//
//        timePickerDialog.show();
//    }
//
//    private Timestamp createCustomTimestamp(int year, int month, int day, int hour, int minute) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.MONTH, month);
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        return new Timestamp(calendar.getTime());
//    }
//
//    private void handleTimestampSelection(TimestampTarget target, Timestamp timestamp) {
//        switch (target) {
//            case END_SESSION:
//                endSession(timestamp);
//                break;
//            case BREAK_START:
//                selectedBreakStartTime = timestamp;
//                showDatePicker(TimestampTarget.BREAK_END);
//                break;
//            case BREAK_END:
//                addBreak(selectedBreakStartTime, timestamp);
//                break;
//        }
//    }
//
//    private void endSession(Timestamp customEndTime) {
//        firestoreUtil.endWorkSession(userId, sessionId, customEndTime);
//        loadSessionDetails();
//    }
//
//    private void addBreak(Timestamp startBreak, Timestamp endBreak) {
//        firestoreUtil.addBreakToSession(userId, sessionId, startBreak, endBreak);
//        loadSessionDetails();
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailTimeBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        breakAdapter = new BreakAdapter(this);
        binding.breaksRecyclerView.setAdapter(breakAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEndTimeListener();
        setBreakButtonListener();
        initObserver();
    }

    private void setEndTimeListener() {
//        addEndTimeButton.setOnClickListener(v -> {
//            showDatePicker(TimestampTarget.END_SESSION);
//        });
    }

    private void setBreakButtonListener() {
//        addBreakButton.setOnClickListener(v -> {
//            showDatePicker(TimestampTarget.BREAK_START);
//        });
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Toast.makeText(mContext, "failed to get user data" + result.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(mContext, "SuccessUser", Toast.LENGTH_SHORT).show();
        });

        sessionViewModel.getSessions().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Toast.makeText(mContext, "failed to get sessions" + result.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(mContext, "SuccessSession", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onBreakDeleted() {

    }
}