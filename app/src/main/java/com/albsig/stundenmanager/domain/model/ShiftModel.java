package com.albsig.stundenmanager.domain.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ShiftModel {
    private String documentId;
    private final Timestamp startDate;
    private final Timestamp endDate;
    private final List<String> morningShift;
    private final List<String> afternoonShift;
    private final List<String> eveningShift;

    public ShiftModel(Timestamp startDate, Timestamp endDate, List<String> morningShift, List<String> afternoonShift, List<String> eveningShift) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.morningShift = morningShift;
        this.afternoonShift = afternoonShift;
        this.eveningShift = eveningShift;
    }

    public ShiftModel(DocumentSnapshot document) {
        this.startDate = (Timestamp) document.get("startDate");
        this.endDate = (Timestamp) document.get("endDate");
        this.morningShift = (List<String>) document.get("morningShift");
        this.afternoonShift = (List<String>) document.get("lateShift");
        this.eveningShift = (List<String>) document.get("endShift");
        this.documentId = document.getId();
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public List<String> getMorningShift() {
        return morningShift;
    }

    public List<String> getAfternoonShift() {
        return afternoonShift;
    }

    public List<String> getEveningShift() {
        return eveningShift;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}

