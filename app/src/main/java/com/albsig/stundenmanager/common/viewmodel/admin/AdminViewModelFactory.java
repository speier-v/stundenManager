package com.albsig.stundenmanager.common.viewmodel.admin;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

public class AdminViewModelFactory implements ViewModelProvider.Factory {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminViewModelFactory(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AdminViewModel.class)) {
            return (T) new AdminViewModel(adminRepository, userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}