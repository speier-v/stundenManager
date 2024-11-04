package com.albsig.stundenmanager.login;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {


    @Nullable private FragmentLoginBinding binding;
    private FragmentTransaction fragmentTransaction;
    private LoginListener loginListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            loginListener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement LoginListener");
        }
    }

    private void onLoginButtonClicked() {
        String userId = "userId"; // TODO replace with actual userId from firestore
        loginListener.onLoginSuccess(userId);
    }

    public interface LoginListener {
        void onLoginSuccess(String userId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void goToDashboard() {
        DashboardFragment dashboardFragment = new DashboardFragment();
        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment, Constants.TAG_DASHBOARD);

        fragmentTransaction.commit();
    }
}
