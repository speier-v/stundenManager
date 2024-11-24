package com.albsig.stundenmanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModelFactory;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModelFactory;
import com.albsig.stundenmanager.data.remote.SessionRepositoryImpl;
import com.albsig.stundenmanager.data.remote.UserRepositoryImpl;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.albsig.stundenmanager.databinding.ActivityMainBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initSharedViewModel();
        initLogin();
    }

    private void initSharedViewModel() {
        UserRepository userRepository = new UserRepositoryImpl(this);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(userRepository);
        new ViewModelProvider(this, userViewModelFactory).get(UserViewModel.class);

        SessionRepository sessionRepository = new SessionRepositoryImpl();
        SessionViewModelFactory sessionViewModelFactory = new SessionViewModelFactory(sessionRepository);
        new ViewModelProvider(this, sessionViewModelFactory).get(SessionViewModel.class);
    }

    private void initLogin() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN)
                .setReorderingAllowed(true);
        fragmentTransaction.commit();
    }
}