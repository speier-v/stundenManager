package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;

import org.json.JSONObject;

import java.util.List;

public interface ShiftRepository {

    void getShifts(String uid, ResultCallback<List<ShiftModel>> resultCallback);

    void updateShift(JSONObject shiftData, ResultCallback<ShiftModel> resultCallback);

    void addShiftSnapshotListener(String uid, ResultCallback<List<ShiftModel>> resultCallback);

    void removeShiftSnapshotListener();
}
