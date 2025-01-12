package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.WorkerModel;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface ShiftPlannerRepository {
    void getWorkers(ResultCallback<List<WorkerModel>> resultCallback);

    void createShift(JSONObject shift, ResultCallback<Boolean> resultCallback);
}
