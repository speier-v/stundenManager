package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;

import org.json.JSONObject;

import java.util.List;

public interface AdminRepository {

    void loginAdmin(JSONObject userData, ResultCallback<UserModel> resultCallback);

    void getUsers(ResultCallback<List<UserModel>> resultCallback);
}
