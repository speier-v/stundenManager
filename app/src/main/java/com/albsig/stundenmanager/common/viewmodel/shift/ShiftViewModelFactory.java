package com.albsig.stundenmanager.common.viewmodel.shift;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.ShiftRepository;

public class ShiftViewModelFactory implements ViewModelProvider.Factory {

    private final ShiftRepository shiftRepository;

    public ShiftViewModelFactory(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShiftViewModel.class)) {
            return (T) new ShiftViewModel(shiftRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}