package com.albsig.stundenmanager.domain.model.session;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.List;

public class ShiftModel implements Serializable {
    private String id;
    private String shiftType; // Morning, Late, or Night
    private String startDate;
    private String endDate;

    public ShiftModel() {}

    public ShiftModel(String id, String shiftType, String startDate, String endDate) {
        this.id = id;
        this.shiftType = shiftType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ShiftModel(String shiftType, DocumentSnapshot document) {
        this.id = document.getId();
        this.shiftType = shiftType;
        this.startDate = document.getString("startDate");
        this.endDate = document.getString("endDate");
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
