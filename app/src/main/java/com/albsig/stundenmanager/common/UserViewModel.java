package com.albsig.stundenmanager.common;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.domain.model.UserModel;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserModel> userModelResult = new MutableLiveData<>();
}
