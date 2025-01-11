package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface ShiftPlannerRepository {
    void getWorkers(ResultCallback<List<UserModel>> resultCallback);

    void createShift(JSONObject shift, ResultCallback<Boolean> resultCallback);
}
