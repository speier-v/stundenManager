package com.albsig.stundenmanager.time;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.FirestoreUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailTimeActivity extends AppCompatActivity {

    private String userId;
    private String sessionId;
    private static final String TAG = "StartTimeActivity";

    private FirestoreUtil firestoreUtil;
    private FirebaseFirestore db;

    private TextView startTimeTextView;
    private TextView endTimeTextView;
    private TextView breaksTextView;
    private Button addEndTimeButton;
    private Button addBreakButton;
    private RecyclerView breaksRecyclerView;
    private BreakAdapter breakAdapter;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_time);

        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        breaksTextView = findViewById(R.id.breaksTextView);
        addEndTimeButton = findViewById(R.id.addEndTimeButton);
        addBreakButton = findViewById(R.id.addBreakButton);
        breaksRecyclerView = findViewById(R.id.breaksRecyclerView);

        userId = getIntent().getStringExtra("userId");
        sessionId = getIntent().getStringExtra("sessionId");
        firestoreUtil = new FirestoreUtil();
        db = firestoreUtil.getInstance();

        breaksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        breakAdapter = new BreakAdapter(userId, sessionId);
        breaksRecyclerView.setAdapter(breakAdapter);

        loadSessionDetails();

        addEndTimeButton.setOnClickListener(v -> {
            showDatePicker();
        });

        addBreakButton.setOnClickListener(v -> {
            // TODO
        });
    }

    private void loadSessionDetails() {
        db.collection("users")
                .document(userId)
                .collection("sessions")
                .document(sessionId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Timestamp startTime = document.getTimestamp("startTime");
                        Timestamp endTime = document.getTimestamp("endTime");
                        List<Map<String, Timestamp>> breaks = (List<Map<String, Timestamp>>) document.get("breaks");

                        // Set startTime
                        if (startTime != null) {
                            String formattedStartTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(startTime.toDate());
                            startTimeTextView.setText(formattedStartTime);
                        } else {
                            startTimeTextView.setText("Not set");
                        }

                        // Set endTime
                        if (endTime != null) {
                            String formattedEndTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(endTime.toDate());
                            endTimeTextView.setText(formattedEndTime);
                            addEndTimeButton.setVisibility(View.GONE);
                        } else {
                            endTimeTextView.setText("Not set");
                            addEndTimeButton.setVisibility(View.VISIBLE);
                        }

                        /*
                        StringBuilder breaksDisplay = new StringBuilder();
                        if (breaks != null && !breaks.isEmpty()) {
                            for (int i = 0; i < breaks.size(); i++) {
                                Map<String, Timestamp> breakEntry = breaks.get(i);
                                Timestamp breakStart = breakEntry.get("startBreak");
                                Timestamp breakEnd = breakEntry.get("endBreak");

                                String formattedBreakStart = breakStart != null ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(breakStart.toDate()) : "Not set";
                                String formattedBreakEnd = breakEnd != null ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(breakEnd.toDate()) : "Not set";

                                breaksDisplay.append("Break ").append(i + 1).append(": ")
                                        .append(formattedBreakStart).append(" - ").append(formattedBreakEnd).append("\n");
                            }
                        } else {
                            breaksDisplay.append("No breaks recorded");
                        }
                        breaksTextView.setText(breaksDisplay.toString().trim());
                         */
                        breakAdapter.setBreaks(breaks);

                    } else {
                        Toast.makeText(this, "Failed to load session details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteSelected) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteSelected;

                    Timestamp customEndTime = createCustomTimestamp(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);

                    endSession(customEndTime);
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

    private void endSession(Timestamp customEndTime) {
        firestoreUtil.endWorkSession(userId, sessionId, customEndTime);
        loadSessionDetails();
    }
}