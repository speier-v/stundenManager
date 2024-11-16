package com.albsig.stundenmanager.domain.model.session;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class SessionModel {
    String documentId;
    Timestamp startTime;
    Timestamp endTime;
    List<BreakModel> breaks;

    public SessionModel() {}

    public SessionModel(String documentId, Timestamp startDate, Timestamp endDate, List<BreakModel> breaks) {
        this.documentId = documentId;
        this.startTime = startDate;
        this.endTime = endDate;
        this.breaks = breaks;
    }

    public SessionModel(DocumentSnapshot document) {
        String documentId = document.getId();
        Timestamp startTime = document.getTimestamp(Constants.SESSION_FIELD_START_TIME );
        Timestamp endTime = document.getTimestamp(Constants.SESSION_FIELD_END_TIME);
    }

    public String getDocumentId() {
        return documentId;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public List<BreakModel> getBreaks() {
        return breaks;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
