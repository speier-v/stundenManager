package com.albsig.stundenmanager.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.FragmentLoginBinding;
import com.albsig.stundenmanager.ui.registration.RegistrationFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private FragmentTransaction fragmentTransaction;
    private Context mContext;
    private UserViewModel userViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel  = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        fragmentTransaction = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assert binding != null;
        initObserver();
        binding.btnLogin.setOnClickListener(v -> onLoginButtonClicked());
        binding.btnGoToRegistration.setOnClickListener(v -> goToRegistration());
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), userModelResult -> {
            if (!userModelResult.isSuccess()) {
                Toast.makeText(mContext, "Login failed - " + userModelResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel userModel = userModelResult.getValue();
            if (userModel.getUid() == null) {
                Log.d(TAG, "UserModel uid is null");
                return;
            }

            Toast.makeText(mContext, "Login Successful", Toast.LENGTH_SHORT).show();
            goToDashboard();
        });
    }

    private void goToDashboard() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment, Constants.TAG_DASHBOARD);

        fragmentTransaction.commit();
    }

    private void onLoginButtonClicked() {
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString() : "";
        String checkMail = Helpers.checkLoginMail(email);
        if(!checkMail.isEmpty()) {
            Toast.makeText(getContext(), "E-Mail is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString() : "";
        String checkPassword = Helpers.checkLoginPassword(password);
        if(!checkPassword.isEmpty()){
            Toast.makeText(getContext(), "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject userData = new JSONObject();
        try {
            userData.put(Constants.USER_MODEL_EMAIL, email);
            userData.put(Constants.USER_MODEL_PASSWORD, password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        userViewModel.loginUser(userData);
    }

    private void goToRegistration() {
        RegistrationFragment newRegistrationFragment = new RegistrationFragment();

        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newRegistrationFragment, Constants.TAG_LOGIN);
        fragmentTransaction.commit();
    }
}
