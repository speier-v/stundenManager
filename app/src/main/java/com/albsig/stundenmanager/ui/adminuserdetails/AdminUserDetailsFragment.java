package com.albsig.stundenmanager.ui.adminuserdetails;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.databinding.FragmentAdminUserDetailsBinding;
import com.albsig.stundenmanager.ui.adminworkeradministration.AdminUserAdministrationFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminUserDetailsFragment extends Fragment {

    private static final String TAG = "AdminUserDetailsFragment";
    Context mContext;
    FragmentAdminUserDetailsBinding binding;
    AdminViewModel adminViewModel;

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
        binding = FragmentAdminUserDetailsBinding.inflate(inflater,  container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigateBack();
        initObserver();
    }

    private void initObserver() {
        adminViewModel.getUserDetailModel().observe( getViewLifecycleOwner(), userModel -> {
            if (userModel == null) {
                return;
            }

            Log.d(TAG, userModel.toString());
        });
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBackNav;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, AdminUserAdministrationFragment.class, null, Constants.TAG_WORKER_ADMINISTRATION).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }
}
