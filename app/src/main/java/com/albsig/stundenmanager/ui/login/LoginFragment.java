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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.MainActivity;
import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.UserViewModel;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.FragmentLoginBinding;
import com.albsig.stundenmanager.ui.registration.RegistrationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.rpc.Help;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private FragmentTransaction fragmentTransaction;
    private LoginListener loginListener;
    private Context mContext;
    private UserViewModel userViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof LoginListener) {
            loginListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginListener");
        }
    }

    public interface LoginListener {
        void onLoginSuccess(String userId);
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
                Toast.makeText(getContext(), "Login failed - " + userModelResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            UserModel userModel = userModelResult.getValue();
            Log.d(TAG, "Login Successful" + userModel.getUid() + " " + userModel.getName());
        });
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

    private void updateUI(@Nullable FirebaseUser user) {
        if (user == null) {
            Toast.makeText(mContext, "Login not successful", Toast.LENGTH_SHORT).show();
            return;
        }
        DashboardFragment dashboardFragment = new DashboardFragment();
        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment, Constants.TAG_DASHBOARD);

        fragmentTransaction.commit();
    }
}
