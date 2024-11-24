package com.albsig.stundenmanager.common.viewmodel.session;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

public class SessionViewModelFactory implements ViewModelProvider.Factory {

    private final SessionRepository sessionRepository;

    public SessionViewModelFactory(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SessionViewModel.class)) {
            return (T) new SessionViewModel(sessionRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}