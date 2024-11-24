package com.albsig.stundenmanager.domain.model.session;

import com.albsig.stundenmanager.common.Constants;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class BreakModel {
    Timestamp breakStart;
    Timestamp breakEnd;

    public BreakModel() {}

    public BreakModel(Timestamp breakStart, Timestamp breakEnd) {
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
    }

    public BreakModel(DocumentSnapshot document) {
        String breakStart = document.getString(Constants.BREAK_FIELD_START_TIME);
        String breakEnd = document.getString(Constants.BREAK_FIELD_END_TIME);
    }

    public Timestamp getBreakStart() {
        return breakStart;
    }

    public Timestamp getBreakEnd() {
        return breakEnd;
    }
}
