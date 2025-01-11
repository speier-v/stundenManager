package com.albsig.stundenmanager.ui.shiftplanner;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.domain.repository.ShiftPlannerRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

public class ShiftPlannerViewModelFactory implements ViewModelProvider.Factory {

    private ShiftPlannerRepository shiftPlannerRepository;

    public ShiftPlannerViewModelFactory(ShiftPlannerRepository shiftPlannerRepository) {
        this.shiftPlannerRepository = shiftPlannerRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShiftPlannerViewModel.class)) {
            return (T) new ShiftPlannerViewModel(shiftPlannerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}