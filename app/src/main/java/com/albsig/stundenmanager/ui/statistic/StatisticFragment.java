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
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.data.remote.SessionRepositoryImpl;
import com.albsig.stundenmanager.data.remote.ShiftRepositoryImpl;
import com.albsig.stundenmanager.data.remote.UserRepositoryImpl;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.ShiftRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;
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
    private List<UserModel> users;
    private List<ShiftModel> shifts;
    private List<SessionModel> sessions;
    private UserRepository userRepository;
    private ShiftRepository shiftRepository;
    private SessionRepository sessionRepository;

    private EditText searchBar;
    private List<UserStatistic> allStatistics; // Original list
    private List<UserStatistic> filteredStatistics; // Filtered list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        userRepository = new UserRepositoryImpl(view.getContext());
        shiftRepository = new ShiftRepositoryImpl();
        sessionRepository = new SessionRepositoryImpl();
        //initializeDummyData();
        initializeData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        if (users == null || sessions == null || shifts == null) {
            //Log.d("hi", "return because of null value: "+users+", "+sessions+", "+shifts);
            return statistics;
        }
        //Log.d("hi", "continuing with values: "+users+", "+sessions+", "+shifts);

        for (UserModel user : users) {
            //Log.d("hi", "loop with user: " + user.getName());
            String userId = user.getUid();
            String userName = user.getName();

            // Filter shifts and sessions for the current user
            List<ShiftModel> userShifts = new ArrayList<>();
            for (ShiftModel shift : shifts) {
                if (shift.getUid().equals(userId)) {
                    userShifts.add(shift);
                }
            }
            //Log.d("hi", "found following shifts for user: " + shifts);

            List<SessionModel> userSessions = new ArrayList<>();
            for (SessionModel session : sessions) {
                if (session.getUid().equals(userId)) {
                    userSessions.add(session);
                }
                //Log.d("hi", "loop for session: " + session);
            }

            // DONE
            Map<ShiftModel, List<SessionModel>> shiftToSessionsMap = mapSessionsToShifts(userShifts, userSessions);
            //Log.d("hi", "matched following sessions to shift: " + shiftToSessionsMap);

            // DONE
            Map<String, Integer> expectedTimePerWeek = calculateWeeklyTimeForShifts(userShifts);
            //Log.d("hi", "calculated following expected time per week: " + expectedTimePerWeek);

            // DONE
            Map<String, Integer> actualTimePerWeek = calculateWeeklyTimeForSessions(shiftToSessionsMap);
            //Log.d("hi", "calculated following actual time per week");

            // DONE
            alignWeeklyTimes(expectedTimePerWeek, actualTimePerWeek);

            // DONE
            List<String> shiftDateLabels = new ArrayList<>();
            for (ShiftModel shift : userShifts) {
                Log.d("hi", "startDate: "+shift.getStart()+", endDate: "+shift.getEnd());
                String formattedDate = formatShiftDate(shift.getStart(), shift.getEnd());
                Log.d("hi", formattedDate);
                shiftDateLabels.add(formattedDate);
            }
            //Log.d("hi", "assigned the following labels: " + shiftDateLabels);

            // DONE
            statistics.add(new UserStatistic(userName, expectedTimePerWeek, actualTimePerWeek, shiftDateLabels));
        }
        Log.d("hi", "returning following statistics: "+statistics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allStatistics = statistics;
        filteredStatistics = new ArrayList<>(allStatistics);

        adapter = new StatisticAdapter(filteredStatistics);
        recyclerView.setAdapter(adapter);
        return statistics;
    }

    private String formatShiftDate(Date startDate, Date endDate) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
        return outputFormat.format(startDate) + " \n - \n " + outputFormat.format(endDate);
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

    private Map<ShiftModel, List<SessionModel>> mapSessionsToShifts(List<ShiftModel> shifts, List<SessionModel> sessions) {
        Map<ShiftModel, List<SessionModel>> shiftToSessionsMap = new HashMap<>();

        for (ShiftModel shift : shifts) {
            Date shiftStart = shift.getStart();
            Date shiftEnd = shift.getEnd();

            // Find matching sessions for the shift
            List<SessionModel> matchingSessions = new ArrayList<>();
            for (SessionModel session : sessions) {
                Date sessionStart = session.getStartTime().toDate();
                if (sessionBelongsToShift(sessionStart, shiftStart, shiftEnd)) {
                    matchingSessions.add(session);
                }
            }

            shiftToSessionsMap.put(shift, matchingSessions);
        }

        return shiftToSessionsMap;
    }

    private boolean sessionBelongsToShift(Date sessionStart, Date shiftStart, Date shiftEnd) {
        com.google.firebase.Timestamp sessionStartTs = new Timestamp(sessionStart);
        com.google.firebase.Timestamp shiftStartTs = new Timestamp(shiftStart);
        com.google.firebase.Timestamp shiftEndTs = new Timestamp(shiftEnd);

        return sessionStartTs.compareTo(shiftStartTs) >= 0 && sessionStartTs.compareTo(shiftEndTs) <= 0;
    }

    private Map<String, Integer> calculateWeeklyTimeForShifts(List<ShiftModel> shifts) {
        Map<String, Integer> weeklyTime = new HashMap<>();
        for (ShiftModel shift : shifts) {
            Date shiftStart = shift.getStart();
            Date shiftEnd = shift.getEnd();
            String week = getWeekOfYear(shiftStart);

            // Calculate duration in minutes
            com.google.firebase.Timestamp shiftStartTs = new Timestamp(shiftStart);
            com.google.firebase.Timestamp shiftEndTs = new Timestamp(shiftEnd);
            int duration = (int) (shiftEndTs.getSeconds() - shiftStartTs.getSeconds()) / 60;

            weeklyTime.put(week, weeklyTime.getOrDefault(week, 0) + duration);
        }
        return weeklyTime;
    }

    private Map<String, Integer> calculateWeeklyTimeForSessions(Map<ShiftModel, List<SessionModel>> shiftToSessionsMap) {
        Map<String, Integer> weeklyTime = new HashMap<>();

        for (Map.Entry<ShiftModel, List<SessionModel>> entry : shiftToSessionsMap.entrySet()) {
            ShiftModel shift = entry.getKey();
            List<SessionModel> sessions = entry.getValue();
            String week = getWeekOfYear(shift.getStart()); // Use the shift's start date for the week

            // Calculate total session duration for this shift
            int totalSessionDuration = 0;
            for (SessionModel session : sessions) {
                totalSessionDuration += session.getDurationInMinutes(); // Session duration in minutes
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

    private void initializeData() {
        userRepository.getUsers(new ResultCallback<List<UserModel>>() {
            @Override
            public void onSuccess(Result<List<UserModel>> result) {
                if (result.isSuccess()) {
                    users = result.getValue();
                    // Handle the list of users (e.g., update UI or recyclerView)
                    //updateUI(users);
                    Log.d("hi", "fetched users: "+users);
                    allStatistics = fetchStatistics();
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle the error if the result is not successful
                    Log.d("error", String.valueOf(result.getError()));
                }
            }

            @Override
            public void onError(Result<List<UserModel>> result) {
                // Handle error (e.g., show an error message)
                Log.d("error", String.valueOf(result.getError()));
            }
        });

        shiftRepository.getAllShifts(new ResultCallback<List<ShiftModel>>() {
            @Override
            public void onSuccess(Result<List<ShiftModel>> result) {
                if (result.isSuccess()) {
                    shifts = result.getValue();
                    // Handle the list of users (e.g., update UI or recyclerView)
                    //updateUI(users);
                    Log.d("hi", "fetched shifts: "+shifts);
                    allStatistics = fetchStatistics();
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle the error if the result is not successful
                    Log.d("error", String.valueOf(result.getError()));
                }
            }

            @Override
            public void onError(Result<List<ShiftModel>> result) {
                // Handle error (e.g., show an error message)
                Log.d("error", String.valueOf(result.getError()));
            }
        });

        sessionRepository.getAllSessions(new ResultCallback<List<SessionModel>>() {
            @Override
            public void onSuccess(Result<List<SessionModel>> result) {
                if (result.isSuccess()) {
                    sessions = result.getValue();
                    // Handle the list of users (e.g., update UI or recyclerView)
                    //updateUI(users);
                    Log.d("hi", "fetched sessions: "+sessions);
                    allStatistics = fetchStatistics();
                    adapter.notifyDataSetChanged();
                } else {
                    // Handle the error if the result is not successful
                    Log.d("error", String.valueOf(result.getError()));
                }
            }

            @Override
            public void onError(Result<List<SessionModel>> result) {
                // Handle error (e.g., show an error message)
                Log.d("error", String.valueOf(result.getError()));
            }
        });
    }
}
