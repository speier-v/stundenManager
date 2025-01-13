package com.albsig.stundenmanager.common.viewmodel.admin;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

import org.json.JSONObject;

import java.util.List;

public class AdminViewModel extends ViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private MutableLiveData<Result<UserModel>> userModelResult = new MutableLiveData<>();
    private MutableLiveData<Result<List<UserModel>>> userListResult = new MutableLiveData<>();

    private MutableLiveData<UserModel> userDetailModel = new MutableLiveData<>();
    private MutableLiveData<Result<List<VIModel>>> checkedVIList = new MutableLiveData<>();
    private MutableLiveData<Result<List<VIModel>>> viListToCheck = new MutableLiveData<>();

    public AdminViewModel(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    public LiveData<Result<UserModel>> getUserModel() {
        return userModelResult;
    }

    public LiveData<Result<List<UserModel>>> getUserList() {
        return userListResult;
    }

    public LiveData<Result<List<VIModel>>> getCheckedVIList() {
        return checkedVIList;
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

    public void getUsers() {
        adminRepository.getUsers(new ResultCallback<List<UserModel>>() {
            @Override
            public void onSuccess(Result<List<UserModel>> response) {
                userListResult.setValue(response);
            }

            @Override
            public void onError(Result<List<UserModel>> error) {

            }
        });
    }

    public void setUserModel(UserModel userModel) {
        userDetailModel.setValue(userModel);
    }

    public LiveData<UserModel> getUserDetailModel() {
        return userDetailModel;
    }

    public void clearUserDetailModel() {
        userDetailModel = new MutableLiveData<>();
    }

    public void getCheckedVIList(String uid) {
        adminRepository.getCheckedVIList(uid, new ResultCallback<List<VIModel>>() {

            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                checkedVIList.setValue(response);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                Log.d(TAG, "getCheckedVIList failed " + error.getError().toString());
            }
        });
    }

    public LiveData<Result<List<VIModel>>> getVIListToCheck() {
        return viListToCheck;
    }

    public void getVIListToCheck(String uid) {
        adminRepository.getVIListToCheck(uid, new ResultCallback<List<VIModel>>() {

            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                viListToCheck.setValue(response);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                Log.d(TAG, "getVIListToCheck failed " + error.getError().toString());
            }
        });
    }

    public void updateVIModel(String approvalType, String uid, String docId, ResultCallback<Boolean> callback) {
        adminRepository.updateVIModel(approvalType, uid, docId, new ResultCallback<Boolean>() {
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
}
