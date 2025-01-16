package com.albsig.stundenmanager.ui.statistic;

import java.util.List;
import java.util.Map;

public class UserStatistic {
    private String userName;
    private Map<String, Integer> expectedTimePerWeek;
    private Map<String, Integer> actualTimePerWeek;
    private List<String> shiftDateLabels;

    public UserStatistic(String userName, Map<String, Integer> expectedTimePerWeek,
                         Map<String, Integer> actualTimePerWeek, List<String> shiftDateLabels) {
        this.userName = userName;
        this.expectedTimePerWeek = expectedTimePerWeek;
        this.actualTimePerWeek = actualTimePerWeek;
        this.shiftDateLabels = shiftDateLabels;
    }

    public String getUserName() {
        return userName;
    }

    public Map<String, Integer> getExpectedTimePerWeek() {
        return expectedTimePerWeek;
    }

    public Map<String, Integer> getActualTimePerWeek() {
        return actualTimePerWeek;
    }

    public List<String> getShiftDateLabels() {
        return shiftDateLabels;
    }
}
