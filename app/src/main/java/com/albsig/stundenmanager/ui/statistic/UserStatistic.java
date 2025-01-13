package com.albsig.stundenmanager.ui.statistic;

import java.util.Map;

public class UserStatistic {
    private String userName;
    private Map<String, Integer> expectedTimePerWeek;
    private Map<String, Integer> actualTimePerWeek;

    public UserStatistic(String userName, Map<String, Integer> expectedTimePerWeek, Map<String, Integer> actualTimePerWeek) {
        this.userName = userName;
        this.expectedTimePerWeek = expectedTimePerWeek;
        this.actualTimePerWeek = actualTimePerWeek;
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
}
