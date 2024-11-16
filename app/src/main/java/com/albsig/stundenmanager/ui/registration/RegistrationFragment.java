package com.albsig.stundenmanager.ui.registration;
import android.content.Context;
import android.os.Bundle;
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
import com.albsig.stundenmanager.common.viewmodel.UserViewModel;
import com.albsig.stundenmanager.common.viewmodel.UserViewModelFactory;
import com.albsig.stundenmanager.databinding.FragmentRegistrationBinding;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationFragment extends Fragment {

    private static final String TAG = "RegistrationFragment";
    @Nullable private FragmentRegistrationBinding binding;
    private UserViewModel userViewModel;
    private Context mContext;
    private FragmentTransaction fragmentTransaction;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedViewModel();
    }

    private void initSharedViewModel() {
        userViewModel  = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false );
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
        initBtnRegistration();
        initBtnGoToLogin();
        initObserver();
    }

    private void initBtnRegistration() {
        assert binding != null;
        MaterialButton btnRegistration = binding.btnRegistration;
        btnRegistration.setOnClickListener( view1 -> {
            String email = String.valueOf(binding.etEmail.getText());
            String password = String.valueOf(binding.etPassword.getText());
            String name = String.valueOf(binding.etName.getText());
            String surname = String.valueOf(binding.etSurname.getText());
            String birthday = String.valueOf(binding.etBirthday.getText());
            String street = String.valueOf(binding.etStreet.getText());
            String zipCode = String.valueOf(binding.etZipCode.getText());
            String city = String.valueOf(binding.etCity.getText());

            JSONObject userData = new JSONObject(
                    Map.of(
                            "email", email,
                            "password", password,
                            "name", name,
                            "surname", surname,
                            "birthday", birthday,
                            "street", street,
                            "zipCode", zipCode,
                            "city", city
                    )
            );

            userViewModel.registerUser(userData);
        });
    }

    private void initBtnGoToLogin() {
        assert binding != null;
        MaterialButton btnGoToLogin = binding.btnGoToLogin;
        btnGoToLogin.setOnClickListener( view1 -> goToLogin() );
    }

    private void goToLogin() {
        LoginFragment newLoginFragment = new LoginFragment();

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newLoginFragment, Constants.TAG_LOGIN);
        fragmentTransaction.commit();
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Toast.makeText(mContext, "Registration failed" + result.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(mContext, "Registration Successful", Toast.LENGTH_SHORT).show();
            goToDashboard();
        });
    }

    private void goToDashboard() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment, Constants.TAG_DASHBOARD);

        fragmentTransaction.commit();
    }
}
