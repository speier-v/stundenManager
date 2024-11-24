//package com.albsig.stundenmanager.ui.time;
//
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.albsig.stundenmanager.R;
//import com.albsig.stundenmanager.common.FirestoreUtil;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//public class DetailTimeActivity extends AppCompatActivity implements BreakAdapter.OnBreakDeletedListener {
//
//    private String userId;
//    private String sessionId;
//    private static final String TAG = "StartTimeActivity";
//
//    private FirestoreUtil firestoreUtil;
//    private FirebaseFirestore db;
//
//    private TextView startTimeTextView;
//    private TextView endTimeTextView;
//    private Button addEndTimeButton;
//    private Button addBreakButton;
//    private RecyclerView breaksRecyclerView;
//    private BreakAdapter breakAdapter;
//
//    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
//    private Timestamp selectedBreakStartTime;
//    public enum TimestampTarget {
//        END_SESaSION,
//        BREAK_START,
//        BREAK_END
//    }
//
//    @Override
//    public void onBreakDeleted() {
//        loadSessionDetails();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_detail_time);
//
//        startTimeTextView = findViewById(R.id.startTimeTextView);
//        endTimeTextView = findViewById(R.id.endTimeTextView);
//        addEndTimeButton = findViewById(R.id.addEndTimeButton);
//        addBreakButton = findViewById(R.id.addBreakButton);
//        breaksRecyclerView = findViewById(R.id.breaksRecyclerView);
//
//        userId = getIntent().getStringExtra("userId");
//        sessionId = getIntent().getStringExtra("sessionId");
//        firestoreUtil = new FirestoreUtil();
//        db = firestoreUtil.getInstance();
//
//        breaksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        breakAdapter = new BreakAdapter(userId, sessionId, this);
//        breaksRecyclerView.setAdapter(breakAdapter);
//
//        loadSessionDetails();
//
//        addEndTimeButton.setOnClickListener(v -> {
//            showDatePicker(TimestampTarget.END_SESSION);
//        });
//
//        addBreakButton.setOnClickListener(v -> {
//            showDatePicker(TimestampTarget.BREAK_START);
//        });
//    }
//
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
//}