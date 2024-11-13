package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;

public interface UserRepository {
    void registerUser(String email, String password, String birthday, String name, String surname, String street, String zipCode, String city, ResultCallback<UserModel> resultCallback);

    void updateUser(String uid, String password, String name, String surname, String birthday, String street, String zipCode, String city, ResultCallback<UserModel> resultCallback);

    void loginUser(String email, String password, ResultCallback<UserModel> resultCallback);
}
