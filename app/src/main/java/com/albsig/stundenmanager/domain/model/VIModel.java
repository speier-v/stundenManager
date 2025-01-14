package com.albsig.stundenmanager.domain.model;

import com.google.firebase.Timestamp;

public class VIModel {
    private String uid;
    private String docId;
    private String type;
    private Timestamp startDate;
    private Timestamp endDate;
    private String approval;

    public VIModel(String uid, String docId,String type, Timestamp startDate, Timestamp endDate, String approval) {
        this.uid = uid;
        this.docId = docId;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approval = approval;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getApproval() {
        return approval;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public String getDocId() {
        return docId;
    }

    public String getUid() {
        return uid;
    }
}
