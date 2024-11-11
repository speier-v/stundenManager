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

import com.albsig.stundenmanager.MainActivity;
import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.FragmentLoginBinding;
import com.albsig.stundenmanager.ui.registration.RegistrationFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private FragmentLoginBinding binding;
    private FragmentTransaction fragmentTransaction;
    private LoginListener loginListener;
    private FirebaseAuth mAuth;
    private Context mContext;

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

    private void onLoginButtonClicked() {

        if (binding.etEmail.getText() == null) {
            Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.etEmail.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = binding.etEmail.getText().toString();

        if (binding.etPassword.getText() == null) {
            Toast.makeText(getContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (binding.etPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String password = binding.etPassword.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(mContext, "Login not successful", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            loginListener.onLoginSuccess(user.getUid());
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Login not successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public interface LoginListener {
        void onLoginSuccess(String userId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
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
        binding.btnLogin.setOnClickListener(v -> onLoginButtonClicked());
        binding.btnGoToRegistration.setOnClickListener(v -> goToRegistration());
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

    private void goToRegistration() {
        RegistrationFragment newRegistrationFragment = new RegistrationFragment();

        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newRegistrationFragment, Constants.TAG_LOGIN);
        fragmentTransaction.commit();
    }
}
