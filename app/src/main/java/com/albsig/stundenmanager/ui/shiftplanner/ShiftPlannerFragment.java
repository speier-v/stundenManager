package com.albsig.stundenmanager.ui.shiftplanner;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.data.remote.ShiftPlannerRepositoryImpl;
import com.albsig.stundenmanager.databinding.FragmentShiftPlannerBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.ShiftPlannerRepository;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShiftPlannerFragment extends Fragment implements WorkerAdapter.OnItemSelected {

    private Context mContext;
    private FragmentShiftPlannerBinding binding;
    private ShiftPlannerViewModel shiftPlannerViewModel;
    private BottomSheetDialog bottomSheetDialog;
    private WorkerAdapter workerAdapter;
    private List<UserModel> workers;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel() {
        ShiftPlannerRepository shiftPlannerRepository = new ShiftPlannerRepositoryImpl();
        ShiftPlannerViewModelFactory shiftPlannerViewModelFactory = new ShiftPlannerViewModelFactory(shiftPlannerRepository);
        shiftPlannerViewModel = new ViewModelProvider(this, shiftPlannerViewModelFactory).get(ShiftPlannerViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShiftPlannerBinding.inflate(inflater, container, false );
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
        shiftPlannerViewModel.getUser();
        initObserver();
        initBottomSheetDialog();
        initShiftButtons();
        initCreateButton();
        setWeekDate();
        binding.btnRight.setOnClickListener(v -> {
                shiftPlannerViewModel.weekUp();
                setWeekDate();
        });

        binding.btnLeft.setOnClickListener(v -> {
            shiftPlannerViewModel.weekDown();
            setWeekDate();
        });
    }

    private void setWeekDate() {
        LocalDateTime startDate = shiftPlannerViewModel.getStartDate();
        LocalDateTime endDate = shiftPlannerViewModel.getEndDate();
        String weekDateStr = startDate.getDayOfMonth() + "." + startDate.getMonth().getValue() + "." + startDate.getYear() + " - " + endDate.getDayOfMonth() + "." + endDate.getMonth().getValue() + "." + endDate.getYear();
        binding.weekDate.setText(weekDateStr);
    }

    @SuppressLint("InflateParams")
    private void initBottomSheetDialog() {
        bottomSheetDialog = new BottomSheetDialog(mContext);
        View view = getLayoutInflater().inflate(R.layout.dialog_pick_worker, null);
        bottomSheetDialog.setContentView(view);

        MaterialButton btnClose = bottomSheetDialog.findViewById(R.id.btnClose);
        if (btnClose == null)  {
            return;
        }
        btnClose.setOnClickListener(view1 -> { bottomSheetDialog.dismiss(); });

        RecyclerView rvWorkers = bottomSheetDialog.findViewById(R.id.rvWorkers);
        if (rvWorkers == null)  {
            return;
        }
        workerAdapter = new WorkerAdapter(this);
        rvWorkers.setAdapter(workerAdapter);
    }

    private void initObserver() {
        shiftPlannerViewModel.getResultLiveData().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Log.d("ShiftPlannerFragment", "Error: " + result.getError());
                Toast.makeText(mContext, "Could not get worker", Toast.LENGTH_SHORT).show();
                return;
            }

            List<UserModel> resWorkers = result.getValue();
            Toast.makeText(mContext, "Workers: " + resWorkers.size(), Toast.LENGTH_SHORT).show();
            Log.d("ShiftPlannerFragment", "Workers: " + resWorkers);
            workerAdapter.updateList(resWorkers);
        });

        shiftPlannerViewModel.getWorkersMorning().observe(getViewLifecycleOwner(), workers -> {
            if (workers.isEmpty()) {
                binding.tvBtnPickMorningShift.setText(R.string.pick);
                return;
            }

            String btnText = workers.size() + getString(R.string.picked);
            binding.tvBtnPickMorningShift.setText(btnText);
        });

        shiftPlannerViewModel.getWorkersLate().observe(getViewLifecycleOwner(), workers -> {
            if (workers.isEmpty()) {
                binding.tvBtnPickLateShift.setText(R.string.pick);
                return;
            }

            String btnText = workers.size() + getString(R.string.picked);
            binding.tvBtnPickLateShift.setText(btnText);
        });

        shiftPlannerViewModel.getWorkersNight().observe(getViewLifecycleOwner(), workers -> {
            if (workers.isEmpty()) {
                binding.tvBtnPickNightShift.setText(R.string.pick);
                return;
            }

            String btnText = workers.size() + getString(R.string.picked);
            binding.tvBtnPickNightShift.setText(btnText);
        });
    }

    private void initShiftButtons() {
        binding.tvBtnPickMorningShift.setOnClickListener(view1 -> {
            workerAdapter.setShift(Constants.MORNING_SHIFT);
            bottomSheetDialog.show();
        });
        binding.tvBtnPickLateShift.setOnClickListener(view1 -> {
            workerAdapter.setShift(Constants.LATE_SHIFT);
            bottomSheetDialog.show();
        });

        binding.tvBtnPickNightShift.setOnClickListener(view1 -> {
            workerAdapter.setShift(Constants.NIGHT_SHIFT);
            bottomSheetDialog.show();
        });
    }

    private void initCreateButton() {
        binding.btnCreate.setOnClickListener(view -> {
            shiftPlannerViewModel.createShift(new ResultCallback<Boolean>() {
                @Override
                public void onSuccess(Result<Boolean> response) {
                    workers = new ArrayList<>();
                    Toast.makeText(mContext, "Shift created", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Result<Boolean> error) {
                    Toast.makeText(mContext, "Could not create shift - " + error.getError(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onWorkerSelected(int shift, UserModel worker) {
        shiftPlannerViewModel.addWorkerToShift(shift, worker);
    }

    @Override
    public void onWorkerDeselected(int shift, UserModel userModel) {
        shiftPlannerViewModel.removeWorkerFromShift(shift, userModel);
    }
}
