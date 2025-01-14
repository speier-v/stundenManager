package com.albsig.stundenmanager.ui.statistic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        initializeDummyData();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<UserStatistic> statistics = fetchStatistics();
        adapter = new StatisticAdapter(statistics);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<UserStatistic> fetchStatistics() {
        List<UserStatistic> statistics = new ArrayList<>();

        for (String[] user : dummyUsers) {
            String userId = user[0];
            String userName = user[1];

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

            Map<String, Integer> expectedTimePerWeek = calculateWeeklyTime(userShifts);
            Map<String, Integer> actualTimePerWeek = calculateWeeklyTime(userSessions);

            statistics.add(new UserStatistic(userName, expectedTimePerWeek, actualTimePerWeek));
        }
        return statistics;
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

        // Dummy Shifts: {userId, date, durationInMinutes}
        dummyShifts = new ArrayList<>();
        dummyShifts.add(new String[]{"1", "2025-01-01", "240"});
        dummyShifts.add(new String[]{"1", "2025-01-05", "300"});
        dummyShifts.add(new String[]{"1", "2025-01-08", "360"});
        dummyShifts.add(new String[]{"2", "2025-01-01", "180"});
        dummyShifts.add(new String[]{"2", "2025-01-15", "540"});
        dummyShifts.add(new String[]{"3", "2025-01-01", "420"});
        dummyShifts.add(new String[]{"3", "2025-01-09", "240"});
        dummyShifts.add(new String[]{"3", "2025-01-17", "300"});

        // Dummy Sessions: {userId, date, durationInMinutes}
        dummySessions = new ArrayList<>();
        dummySessions.add(new String[]{"1", "2025-01-01", "200"});
        dummySessions.add(new String[]{"1", "2025-01-05", "290"});
        dummySessions.add(new String[]{"1", "2025-01-09", "350"});
        dummySessions.add(new String[]{"2", "2025-01-01", "150"});
        dummySessions.add(new String[]{"2", "2025-01-15", "480"});
        dummySessions.add(new String[]{"3", "2025-01-01", "400"});
        dummySessions.add(new String[]{"3", "2025-01-10", "220"});
        dummySessions.add(new String[]{"3", "2025-01-16", "290"});
    }
}
