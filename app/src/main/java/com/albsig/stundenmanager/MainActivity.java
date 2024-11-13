package com.albsig.stundenmanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.UserViewModel;
import com.albsig.stundenmanager.common.UserViewModelFactory;
import com.albsig.stundenmanager.data.remote.UserRepositoryImpl;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.ActivityMainBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    ActivityMainBinding binding;
    private String userId;

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
    }

    private void initLogin() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN)
                .setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onLoginSuccess(String userId) {
        this.userId = userId;

        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        dashboardFragment.setArguments(args);
        loadFragment(dashboardFragment);
    }
}