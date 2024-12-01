package com.albsig.stundenmanager.ui.admindashboard;

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
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentAdminDashboardBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.adminlogin.AdminLoginFragment;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.ui.dashboard.SessionsAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

public class AdminDashboardFragment extends Fragment {

    private static final String TAG = "AdminDashboardFragment";
    private Context mContext;
    private FragmentAdminDashboardBinding binding;
    private SessionsAdapter sessionsAdapter;
    private AdminViewModel adminViewModel;
    private UserModel userModel;
    private Timestamp startTime;
    private Timestamp endTime;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
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
        initObserver();
        signOut();
    }

    private void initObserver() {
    }

    private void signOut() {
        MaterialButton fabSignOut = binding.btnSignOut;

        fabSignOut.setOnClickListener(view -> {
            adminViewModel.signOutAdmin();
            Toast.makeText(this.binding.getRoot().getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            goToAdminLoginFragment();
        });
    }

    private void goToAdminLoginFragment() {
        AdminLoginFragment adminLoginFragment = new AdminLoginFragment();

        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, adminLoginFragment, Constants.TAG_ADMIN_LOGIN);
        fragmentTransaction.commit();
    }
}