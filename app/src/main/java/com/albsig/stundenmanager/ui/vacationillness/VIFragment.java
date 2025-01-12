package com.albsig.stundenmanager.ui.vacationillness;

import android.app.DatePickerDialog;
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
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentVacactionIllnessBinding;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.ui.user_overview.UserOverviewFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.List;

public class VIFragment extends Fragment {

    FragmentVacactionIllnessBinding binding;
    Context mContext;
    Timestamp startTimestamp;
    Timestamp endTimestamp;
    UserViewModel userViewModel;
    RecyclerView rvList;
    VIAdapter viAdapter;
    String uid;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVacactionIllnessBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCheckboxes();
        navigateBack();
        initButtonsDate();
        initCreateBtn();
        initList();
        setVIList();
    }

    private void initButtonsDate() {
        binding.btnStartTime.setOnClickListener(v -> startProcessStartDate());
        binding.btnEndTime.setOnClickListener(v -> startProcessEndDate());
    }

    private void initCreateBtn() {
        binding.btnCreate.setOnClickListener(v->{
            if(startTimestamp == null || endTimestamp == null) {
                Toast.makeText(mContext, "Start and end date must be set", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!binding.checkIllness.isChecked() && !binding.checkVacation.isChecked()) {
                Toast.makeText(mContext, "Please select a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            if(binding.checkIllness.isChecked()) {
                userViewModel.createIllness(startTimestamp, endTimestamp, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Result<Boolean> response) {
                        Toast.makeText(mContext, "Illness created successfully", Toast.LENGTH_SHORT).show();
                        startTimestamp = null;
                        endTimestamp = null;
                        binding.checkIllness.setChecked(false);
                        binding.checkVacation.setChecked(false);
                        binding.valueStartTime.setText("");
                        binding.valueEndTime.setText("");
                        getVIList();
                    }

                    @Override
                    public void onError(Result<Boolean> error) {
                        Toast.makeText(mContext, "Error creating illness", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if(binding.checkVacation.isChecked()) {
                userViewModel.createVacation(startTimestamp, endTimestamp, new ResultCallback<Boolean>() {
                    @Override
                    public void onSuccess(Result<Boolean> response) {
                        Toast.makeText(mContext, "Vacation created successfully", Toast.LENGTH_SHORT).show();
                        startTimestamp = null;
                        endTimestamp = null;
                        binding.checkIllness.setChecked(false);
                        binding.checkVacation.setChecked(false);
                        binding.valueStartTime.setText("");
                        binding.valueEndTime.setText("");
                    }

                    @Override
                    public void onError(Result<Boolean> error) {
                        Toast.makeText(mContext, "Error creating vacation", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startProcessStartDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Create start date
        new DatePickerDialog(mContext, (viewDateStart, yearSelectedStart, monthSelectedStart, daySelectedStart) -> {
            Timestamp tmpTimestamp = Helpers.createCustomTimestamp(yearSelectedStart, monthSelectedStart, daySelectedStart,0 ,0 );
            if(endTimestamp != null && endTimestamp.toDate().getTime() < tmpTimestamp.toDate().getTime()) {
                Toast.makeText(mContext, "End date must be after start date", Toast.LENGTH_SHORT).show();
                return;
            }
            startTimestamp = tmpTimestamp;
            binding.valueStartTime.setText(startTimestamp.toDate().toString());
        }, year, month, day).show();
    }

    private void startProcessEndDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Create start date
        new DatePickerDialog(mContext, (viewDateStart, yearSelectedStart, monthSelectedStart, daySelectedStart) -> {
            Timestamp tmpTimestamp = Helpers.createCustomTimestamp(yearSelectedStart, monthSelectedStart, daySelectedStart,0 ,0 );
            if(startTimestamp != null && startTimestamp.toDate().getTime() > tmpTimestamp.toDate().getTime()) {
                Toast.makeText(mContext, "Start date must be before end date", Toast.LENGTH_SHORT).show();
                return;
            }
            endTimestamp = tmpTimestamp;
            binding.valueEndTime.setText(endTimestamp.toDate().toString());
        }, year, month, day).show();
    }

    private void initCheckboxes() {
        binding.checkVacation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkIllness.setChecked(false);
            }
        });

        binding.checkIllness.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkVacation.setChecked(false);
            }
        });
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBack;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, UserOverviewFragment.class, null, Constants.TAG_DASHBOARD).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    private void initList() {
        rvList = binding.rvVI;
        viAdapter = new VIAdapter();
        rvList.setAdapter(viAdapter);
    }

    private void setVIList() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), userModelResult -> {
            if (userModelResult.getValue() == null) {
                Toast.makeText(mContext, "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            uid = userModelResult.getValue().getUid();

            getVIList();
        });
    }

    private void getVIList() {
        userViewModel.getVIList(uid, new ResultCallback<List<VIModel>>() {
            @Override
            public void onSuccess(Result<List<VIModel>> response) {
                if (response.getValue().isEmpty()) {
                    return;
                }

                List<VIModel> resList = response.getValue();
                viAdapter.updateData(resList);
            }

            @Override
            public void onError(Result<List<VIModel>> error) {
                Toast.makeText(mContext, "Error getting VI list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
