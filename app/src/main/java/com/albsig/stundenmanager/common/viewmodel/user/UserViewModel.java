package com.albsig.stundenmanager.common.viewmodel.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.firebase.Timestamp;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class UserViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository;
    private MutableLiveData<Result<UserModel>> userModelResult = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Result<UserModel>> getUserModel() {
        return userModelResult;
    }

    public void loginUser(JSONObject userData) {
        userRepository.loginUser(userData, new ResultCallback<UserModel>() {
            @Override
            public void onSuccess(Result<UserModel> response) {
                Log.d(TAG, "Login successful");
                userModelResult.setValue(response);
            }

            @Override
            public void onError(Result<UserModel> error) {
                Log.d(TAG, "Login failed " + error.getError().toString());
                userModelResult.setValue(error);
            }
        });
    }

    public void signOutUser() {
        Log.d(TAG, "Logout");
        userRepository.signOutUser(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Log.d(TAG, "Logout successful");
                userModelResult = new MutableLiveData<>();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Log.d(TAG, "Logout did not work. Try again later!");
            }
        });
    }

    public void registerUser(JSONObject userData) {
        userRepository.registerUser(userData, new ResultCallback<UserModel>() {

            @Override
            public void onSuccess(Result<UserModel> response) {
                Log.d(TAG, "Registration successful");
                userModelResult.setValue(response);
            }

            @Override
            public void onError(Result<UserModel> error) {
                Log.d(TAG, "Registration failed ");
            }

        });
    }

    public void createIllness(Timestamp startTimestamp, Timestamp endTimestamp, ResultCallback<Boolean> callback) {
        if( userModelResult.getValue() == null) {
            callback.onError(Result.error(new Exception("User not logged in")));
            return;
        }
        String uid = userModelResult.getValue().getValue().getUid();
        JSONObject illnessData = new JSONObject(
                Map.of(
                        "startDate", startTimestamp.toDate().getTime(),
                        "endDate", endTimestamp.toDate().getTime(),
                        "uid", uid
                )
        );

        userRepository.createIllness(illnessData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                callback.onError(error);
            }
        });
    }

    public void createVacation(Timestamp startTimestamp, Timestamp endTimestamp, ResultCallback<Boolean> callback) {
        if( userModelResult.getValue() == null) {
            callback.onError(Result.error(new Exception("User not logged in")));
            return;
        }
        String uid = userModelResult.getValue().getValue().getUid();
        JSONObject vacationData = new JSONObject(
                Map.of(
                        "startDate", startTimestamp.toDate().getTime(),
                        "endDate", endTimestamp.toDate().getTime(),
                        "uid", uid
                )
        );

        userRepository.createVacation(vacationData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                callback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                callback.onError(error);
            }
        });
    }

    public void getVIList(String uid, ResultCallback<List<VIModel>> resultCallback) {
        userRepository.getVIList(uid, new ResultCallback<List<VIModel>>() {

            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                resultCallback.onSuccess(response);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                resultCallback.onError(error);
            }
        });
    }
}
