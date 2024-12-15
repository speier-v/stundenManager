package com.albsig.stundenmanager.domain.model.session;

public class Shift {
    private String id;
    private String shiftType; // Morning, Late, or Night
    private String startDate;
    private String endDate;

    public Shift(String id, String shiftType, String startDate, String endDate) {
        this.id = id;
        this.shiftType = shiftType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public String getShiftType() {
        return shiftType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }
}

