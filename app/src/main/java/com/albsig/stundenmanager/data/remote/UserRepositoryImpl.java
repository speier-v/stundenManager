package com.albsig.stundenmanager.data.remote;

import android.widget.Toast;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public void registerUser(String email, String password, String birthday, String name, String surname, String street, String zipCode, String city, ResultCallback<UserModel> resultCallback) {
//        Map<String, Object> userData = new HashMap<>(
//                Map.of(
//                        "email", email,
//                        "password", password,
//                        "name", name,
//                        "surname", surname,
//                        "birthday", birthday,
//                        "street", street,
//                        "zipCode", zipCode,
//                        "city", city
//                )
//        );
//
//        FirebaseFunctions.getInstance()
//                .getHttpsCallable("createUser")
//                .call(userData)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        resultCallback.onSuccess(Result.success(user));
//                    } else {
//                        Exception e = task.getException();
//                        assert e != null;
//                    }
//                });
    }

    @Override
    public void updateUser(String uid, String password, String name, String surname, String birthday, String street, String zipCode, String city, ResultCallback<UserModel> resultCallback) {

    }

    @Override
    public void loginUser(String email, String password, ResultCallback<UserModel> resultCallback) {}
}
