package com.albsig.stundenmanager.ui.adminuserdetails;

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
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.databinding.FragmentAdminUserDetailsBinding;
import com.albsig.stundenmanager.ui.adminworkeradministration.AdminUserAdministrationFragment;
import com.albsig.stundenmanager.ui.vacationillness.VIAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminUserDetailsFragment extends Fragment implements ApprovalAdapter.OnUserApprovalClickListener {

    private static final String TAG = "AdminUserDetailsFragment";
    Context mContext;
    FragmentAdminUserDetailsBinding binding;
    AdminViewModel adminViewModel;
    RecyclerView rvApproval;
    RecyclerView rvVI;
    VIAdapter viAdapter;
    ApprovalAdapter approvalAdapter;

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
        initRecyclerVI();
        initRecyclerApproval();
    }

    private void initRecyclerApproval() {
        rvApproval = binding.rvApproval;
        approvalAdapter = new ApprovalAdapter(this);
        rvApproval.setAdapter(approvalAdapter);
    }

    private void initRecyclerVI() {
        rvVI = binding.rvVI;
        viAdapter = new VIAdapter();
        rvVI.setAdapter(viAdapter);
    }


    private void initObserver() {
        adminViewModel.getUserDetailModel().observe( getViewLifecycleOwner(), userModel -> {
            if (userModel == null) {
                return;
            }

            Log.d(TAG, userModel.toString());
            binding.tvName.setText(userModel.getName());
            binding.tvSurname.setText(userModel.getSurname());
            adminViewModel.getCheckedVIList(userModel.getUid());
            adminViewModel.getVIListToCheck(userModel.getUid());
        });

        adminViewModel.getCheckedVIList().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                return;
            }

            viAdapter.updateData(result.getValue());
        });

        adminViewModel.getVIListToCheck().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                return;
            }

            approvalAdapter.updateData(result.getValue());
        });
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBackNav;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, AdminUserAdministrationFragment.class, null, Constants.TAG_WORKER_ADMINISTRATION).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    @Override
    public void onUserApprovalUpdate(String approvalType, String uid, String docId) {
        adminViewModel.updateVIModel(approvalType, uid, docId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Toast.makeText(mContext, "Successfully updated with approval type: " + approvalType, Toast.LENGTH_SHORT).show();
                adminViewModel.getCheckedVIList(uid);
                adminViewModel.getVIListToCheck(uid);
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(mContext, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
