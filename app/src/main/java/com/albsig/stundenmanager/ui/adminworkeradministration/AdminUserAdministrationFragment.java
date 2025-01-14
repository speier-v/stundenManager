package com.albsig.stundenmanager.ui.adminworkeradministration;

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
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.databinding.FragmentWorkerAdministrationBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.admindashboard.AdminDashboardFragment;
import com.albsig.stundenmanager.ui.adminuserdetails.AdminUserDetailsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminUserAdministrationFragment extends Fragment implements UserAdministrationAdapter.OnUserClickListener {

    Context mContext;
    FragmentWorkerAdministrationBinding binding;
    AdminViewModel adminViewModel;
    RecyclerView rvWorkerList;
    UserAdministrationAdapter userAdministrationAdapter;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkerAdministrationBinding.inflate(inflater,  container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigateBack();
        initRecyclerView();
        initObserver();
    }

    private void initRecyclerView() {
        rvWorkerList = binding.rvWorkerList;
        userAdministrationAdapter = new UserAdministrationAdapter(this);
        rvWorkerList.setAdapter(userAdministrationAdapter);

    }

    private void initObserver() {
        adminViewModel.getUsers();
        adminViewModel.getUserList().observe(getViewLifecycleOwner(), userListResult -> {
            if (!userListResult.isSuccess()) {
                Toast.makeText(mContext, "Users could not loaded - " + userListResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (userListResult.getValue() == null) {
                Toast.makeText(mContext, "Users could not loaded", Toast.LENGTH_SHORT).show();
                return;
            }

            userAdministrationAdapter.updateData(userListResult.getValue());
        });
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBackNav;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, AdminDashboardFragment.class, null, Constants.TAG_ADMIN_DASHBOARD).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    @Override
    public void onUserClick(UserModel userModel) {
        adminViewModel.setUserModel(userModel);
        goToUserDetails();
    }

    private void goToUserDetails() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, AdminUserDetailsFragment.class, null, Constants.TAG_USER_DETAILS).setReorderingAllowed(true);
        fragmentTransaction.commit();
    }
}
