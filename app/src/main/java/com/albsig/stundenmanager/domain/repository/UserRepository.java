package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;

import org.json.JSONObject;

public interface UserRepository {
    void registerUser(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void updateUser(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void loginUser(JSONObject userData, ResultCallback<UserModel> resultCallback);
}
