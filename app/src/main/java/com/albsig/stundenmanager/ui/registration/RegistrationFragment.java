package com.albsig.stundenmanager.ui.registration;

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

import com.example.globalgaming.R;
import com.example.globalgaming.common.Constants;
import com.example.globalgaming.common.helper.FormatHelpers;
import com.example.globalgaming.databinding.FragmentRegistrationBinding;
import com.example.globalgaming.ui.login.LoginFragment;
import com.example.globalgaming.ui.login.UserViewModel;
import com.example.globalgaming.ui.main.MainFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationFragment extends Fragment {


   @Nullable private FragmentRegistrationBinding binding;
   private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialSharedAxis forward = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setReenterTransition(forward);

        MaterialSharedAxis backward = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        setReenterTransition(backward);
        initSharedViewModel();
    }

    private void initSharedViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBtnRegistration();
        initBtnGoToLogin();
        setSharedObserver();

    }

    private void initBtnRegistration() {
        MaterialButton btnRegistration = binding.btnRegistration;
        btnRegistration.setOnClickListener( view1 -> {
            userViewModel.registerUser(createUserDataJson());
        });
    }

    private JSONObject createUserDataJson() {
        TextInputEditText etUserName = binding.etUserName;
        TextInputEditText etEmail = binding.etEmail;
        TextInputEditText etPassword = binding.etPassword;
        TextInputEditText etBirthday = binding.etBirthday;
        TextInputEditText etStreet = binding.etStreet;
        TextInputEditText etPostalCode = binding.etPostCode;
        TextInputEditText etCity = binding.etCity;

        JSONObject updatedUser = new JSONObject();
        try {
            updatedUser.put(Constants.USER_MODEL_USER_NAME, etUserName.getText().toString());
            updatedUser.put(Constants.USER_MODEL_PASSWORD, etPassword.getText().toString());
            updatedUser.put(Constants.USER_MODEL_BIRTHDAY, FormatHelpers.formatViewDateToDataDate(etBirthday.getText().toString()));
            updatedUser.put(Constants.USER_MODEL_EMAIL, etEmail.getText().toString());
            updatedUser.put(Constants.USER_MODEL_STREET, etStreet.getText().toString());
            updatedUser.put(Constants.USER_MODEL_POSTAL_CODE, Integer.parseInt(etPostalCode.getText().toString()));
            updatedUser.put(Constants.USER_MODEL_CITY, etCity.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return updatedUser;
    }

    private void initBtnGoToLogin() {
        assert binding != null;
        MaterialButton btnGoToLogin = binding.btnGoToLogin;
        btnGoToLogin.setOnClickListener( view1 -> goToLogin() );
    }

    private void goToLogin() {
        LoginFragment newLoginFragment = new LoginFragment();

        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newLoginFragment, Constants.TAG_LOGIN);
        fragmentTransaction.commit();
    }

    private void setSharedObserver() {
        userViewModel.getUserModelResult().observe(getViewLifecycleOwner(), userModelResult -> {
            if(userModelResult.isSuccess()) {
                Toast.makeText(getContext(), getResources().getString(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                goToMainFragment();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.error_wrong_user_input), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMainFragment() {
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainFragment, Constants.TAG_MAIN);
        fragmentTransaction.commit();
    }
}
