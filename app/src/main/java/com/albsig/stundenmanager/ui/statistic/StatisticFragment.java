package com.albsig.stundenmanager.ui.statistic;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticFragment extends Fragment {

    private RecyclerView recyclerView;
    private StatisticAdapter adapter;

    private List<String[]> dummyUsers;
    private List<String[]> dummyShifts;
    private List<String[]> dummySessions;

    private EditText searchBar;
    private List<UserStatistic> allStatistics; // Original list
    private List<UserStatistic> filteredStatistics; // Filtered list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        initializeDummyData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //List<UserStatistic> statistics = fetchStatistics();
        searchBar = view.findViewById(R.id.search_bar);
        allStatistics = fetchStatistics();
        filteredStatistics = new ArrayList<>(allStatistics);

        adapter = new StatisticAdapter(filteredStatistics);
        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStatistics(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void filterStatistics(String query) {
        filteredStatistics.clear();

        if (query.isEmpty()) {
            // If the query is empty, reset to the full list
            filteredStatistics.addAll(allStatistics);
        } else {
            for (UserStatistic stat : allStatistics) {
                // Find matching labels and their indices
                List<String> matchingLabels = new ArrayList<>();
                List<Integer> matchingIndices = new ArrayList<>();

                List<String> labels = stat.getShiftDateLabels();
                for (int i = 0; i < labels.size(); i++) {
                    Log.d("hi", "checking label: "+labels.get(i));
                    if (labels.get(i).toLowerCase().contains(query.toLowerCase())) {
                        Log.d("hi", "contained");
                        matchingLabels.add(labels.get(i));
                        matchingIndices.add(i);
                    }
                }

                if (!matchingLabels.isEmpty()) {
                    // Filter expected and actual time maps based on matching indices
                    Map<String, Integer> filteredExpectedTime = new HashMap<>();
                    Map<String, Integer> filteredActualTime = new HashMap<>();

                    List<String> allWeeks = new ArrayList<>(stat.getExpectedTimePerWeek().keySet());
                    for (int index : matchingIndices) {
                        if (index < allWeeks.size()) {
                            String week = allWeeks.get(index);
                            if (stat.getExpectedTimePerWeek().containsKey(week)) {
                                filteredExpectedTime.put(week, stat.getExpectedTimePerWeek().get(week));
                            }
                            if (stat.getActualTimePerWeek().containsKey(week)) {
                                filteredActualTime.put(week, stat.getActualTimePerWeek().get(week));
                            }
                        }
                    }

                    // Create a new filtered UserStatistic
                    filteredStatistics.add(new UserStatistic(
                            stat.getUserName(),
                            filteredExpectedTime,
                            filteredActualTime,
                            matchingLabels
                    ));
                }
            }
        }

        // Notify the adapter of data changes
        adapter.notifyDataSetChanged();
    }


    private List<UserStatistic> fetchStatistics() {
        List<UserStatistic> statistics = new ArrayList<>();

        for (String[] user : dummyUsers) {
            String userId = user[0];
            String userName = user[1];

            // Filter shifts and sessions for the current user
            List<String[]> userShifts = new ArrayList<>();
            for (String[] shift : dummyShifts) {
                if (shift[0].equals(userId)) {
                    userShifts.add(shift);
                }
            }

            List<String[]> userSessions = new ArrayList<>();
            for (String[] session : dummySessions) {
                if (session[0].equals(userId)) {
                    userSessions.add(session);
                }
            }

            // Map sessions to shifts
            Map<String[], List<String[]>> shiftToSessionsMap = mapSessionsToShifts(userShifts, userSessions);

            // Calculate expected and actual time per week
            Map<String, Integer> expectedTimePerWeek = calculateWeeklyTimeForShifts(userShifts);
            Map<String, Integer> actualTimePerWeek = calculateWeeklyTimeForSessions(shiftToSessionsMap);

            // Ensure all weeks are aligned
            alignWeeklyTimes(expectedTimePerWeek, actualTimePerWeek);

            // Prepare date labels
            List<String> shiftDateLabels = new ArrayList<>();
            for (String[] shift : userShifts) {
                String formattedDate = formatShiftDate(shift[1], shift[2]);
                shiftDateLabels.add(formattedDate);
            }

            // Add user statistic
            statistics.add(new UserStatistic(userName, expectedTimePerWeek, actualTimePerWeek, shiftDateLabels));
        }

        return statistics;
    }

    private String formatShiftDate(String start, String end) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        try {
            Date startDate = inputFormat.parse(start);
            Date endDate = inputFormat.parse(end);
            return outputFormat.format(startDate) + " - " + outputFormat.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    private Map<String[], List<String[]>> mapSessionsToShifts(List<String[]> shifts, List<String[]> sessions) {
        Map<String[], List<String[]>> shiftToSessionsMap = new HashMap<>();

        for (String[] shift : shifts) {
            String shiftStart = shift[1];
            String shiftEnd = shift[2];

            // Find matching sessions for the shift
            List<String[]> matchingSessions = new ArrayList<>();
            for (String[] session : sessions) {
                String sessionStart = session[1];
                if (sessionBelongsToShift(sessionStart, shiftStart, shiftEnd)) {
                    matchingSessions.add(session);
                }
            }

            shiftToSessionsMap.put(shift, matchingSessions);
        }

        return shiftToSessionsMap;
    }

    private boolean sessionBelongsToShift(String sessionStart, String shiftStart, String shiftEnd) {
        com.google.firebase.Timestamp sessionStartTs = parseTimestamp(sessionStart);
        com.google.firebase.Timestamp shiftStartTs = parseTimestamp(shiftStart);
        com.google.firebase.Timestamp shiftEndTs = parseTimestamp(shiftEnd);

        return sessionStartTs.compareTo(shiftStartTs) >= 0 && sessionStartTs.compareTo(shiftEndTs) <= 0;
    }

    private Map<String, Integer> calculateWeeklyTimeForShifts(List<String[]> shifts) {
        Map<String, Integer> weeklyTime = new HashMap<>();
        for (String[] shift : shifts) {
            String shiftStart = shift[1];
            String shiftEnd = shift[2];
            String week = getWeekOfYear(parseDate(shiftStart));

            // Calculate duration in minutes
            com.google.firebase.Timestamp shiftStartTs = parseTimestamp(shiftStart);
            com.google.firebase.Timestamp shiftEndTs = parseTimestamp(shiftEnd);
            int duration = (int) (shiftEndTs.getSeconds() - shiftStartTs.getSeconds()) / 60;

            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + duration);
        }
        return weeklyTime;
    }

    private Map<String, Integer> calculateWeeklyTimeForSessions(Map<String[], List<String[]>> shiftToSessionsMap) {
        Map<String, Integer> weeklyTime = new HashMap<>();

        for (Map.Entry<String[], List<String[]>> entry : shiftToSessionsMap.entrySet()) {
            String[] shift = entry.getKey();
            List<String[]> sessions = entry.getValue();
            String week = getWeekOfYear(parseDate(shift[1])); // Use the shift's start date for the week

            // Calculate total session duration for this shift
            int totalSessionDuration = 0;
            for (String[] session : sessions) {
                totalSessionDuration += Integer.parseInt(session[2]); // Session duration in minutes
            }

            // Add the duration to the week's total
            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + totalSessionDuration);
        }

        return weeklyTime;
    }

    private void alignWeeklyTimes(Map<String, Integer> expectedTimePerWeek, Map<String, Integer> actualTimePerWeek) {
        for (String week : expectedTimePerWeek.keySet()) {
            if (!actualTimePerWeek.containsKey(week)) {
                actualTimePerWeek.put(week, 0); // Set actual time to 0 for weeks without sessions
            }
        }
    }

    private Map<String, Integer> calculateWeeklyTime(List<String[]> records) {
        Map<String, Integer> weeklyTime = new HashMap<>();
        for (String[] record : records) {
            String date = record[1];
            int duration = Integer.parseInt(record[2]);
            String week = getWeekOfYear(parseDate(date));

            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + duration);
        }
        return weeklyTime;
    }

    private Date parseDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private String getWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        return "Week " + week;
    }

    private Map<String, Integer> calculateWeeklyShiftTime(List<ShiftModel> shifts) {
        Map<String, Integer> weeklyTime = new HashMap<>();
        for (ShiftModel shift : shifts) {
            String week = getWeekOfYear(shift.getStart());
            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + shift.getDurationInMinutes());
        }
        return weeklyTime;
    }

    private Map<String, Integer> calculateWeeklySessionTime(List<SessionModel> sessions) {
        Map<String, Integer> weeklyTime = new HashMap<>();
        for (SessionModel session : sessions) {
            String week = getWeekOfYear(session.getStartTime().toDate());
            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + session.getDurationInMinutes());
        }
        return weeklyTime;
    }

    private void initializeDummyData() {
        // Dummy Users: {userId, userName}
        dummyUsers = new ArrayList<>();
        dummyUsers.add(new String[]{"1", "Alice"});
        dummyUsers.add(new String[]{"2", "Bob"});
        dummyUsers.add(new String[]{"3", "Charlie"});

        dummyShifts = new ArrayList<>();
        dummyShifts.add(new String[]{"1", "2025-01-01T08:00:00", "2025-01-01T16:00:00"}); // Shift 1: 8 AM - 4 PM
        dummyShifts.add(new String[]{"1", "2025-01-11T09:00:00", "2025-01-02T17:00:00"}); // Shift 2: 9 AM - 5 PM
        dummyShifts.add(new String[]{"2", "2025-01-01T07:00:00", "2025-01-01T15:00:00"}); // Shift 1: 7 AM - 3 PM

        // Dummy Sessions: {userId, startTimestamp, durationInMinutes}
        dummySessions = new ArrayList<>();
        dummySessions.add(new String[]{"1", "2025-01-01T09:00:00", "120"}); // Session 1: 9 AM - 11 AM
        dummySessions.add(new String[]{"1", "2025-01-01T14:00:00", "90"});  // Session 2: 2 PM - 3:30 PM
        dummySessions.add(new String[]{"1", "2025-01-02T10:30:00", "180"}); // Session 3: 10:30 AM - 1:30 PM
        dummySessions.add(new String[]{"2", "2025-01-01T08:30:00", "150"}); // Session 1: 8:30 AM - 11:00 AM
        dummySessions.add(new String[]{"2", "2025-01-01T13:00:00", "60"});  // Session 2: 1 PM - 2 PM
    }

    private Timestamp parseTimestamp(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            Date parsedDate = format.parse(dateString); // Parse the string into a Date object
            return new Timestamp(parsedDate); // Create a Firebase Timestamp
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle parse error
        }
    }
}
