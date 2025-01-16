package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;

import org.json.JSONObject;

import java.util.List;

public interface UserRepository {
    void registerUser(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void updateUser(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void getUsers(ResultCallback<List<UserModel>> resultCallback);

    void loginUser(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void signOutUser(ResultCallback<Boolean> resultCallback);

    void createIllness(JSONObject illnessData, ResultCallback<Boolean> resultCallback);

    void createVacation(JSONObject vacationData, ResultCallback<Boolean> resultCallback);

    void getVIList(String uid, ResultCallback<List<VIModel>> resultCallback);
}
