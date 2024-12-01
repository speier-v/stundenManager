package com.albsig.stundenmanager.common.viewmodel.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

import org.json.JSONObject;

public class AdminViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private MutableLiveData<Result<UserModel>> userModelResult = new MutableLiveData<>();

    public AdminViewModel(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Result<UserModel>> getUserModel() {
        return userModelResult;
    }

    public void loginAdmin(JSONObject userData) {
        adminRepository.loginAdmin(userData, new ResultCallback<UserModel>() {
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

    public void signOutAdmin() {
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
}
