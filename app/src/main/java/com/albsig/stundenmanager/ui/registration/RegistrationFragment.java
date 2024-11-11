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
import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.databinding.FragmentRegistrationBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.platform.MaterialSharedAxis;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class RegistrationFragment extends Fragment {


    private static final String TAG = "RegistrationFragment";
   @Nullable private FragmentRegistrationBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialSharedAxis forward = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setReenterTransition(forward);

        MaterialSharedAxis backward = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        setReenterTransition(backward);
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

            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);
            data.put("name", name);
            data.put("surname", surname);
            data.put("birthday", birthday);
            data.put("street", street);
            data.put("zipCode", zipCode);
            data.put("city", city);

            FirebaseFunctions.getInstance()
                    .getHttpsCallable("createUser")
                    .call(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            goToLogin();
                        } else {
                            Exception e = task.getException();
                            assert e != null;
                            Toast.makeText(getContext(), "Registration failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
            });
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
}
