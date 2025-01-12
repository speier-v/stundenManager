package com.albsig.stundenmanager.ui.user_overview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentUserOverviewBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.albsig.stundenmanager.ui.shifts.ShiftFragment;
import com.albsig.stundenmanager.ui.vacationillness.VIFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserOverviewFragment extends Fragment {

    private static final String TAG = "UserOverviewFragment";
    private Context mContext;
    private FragmentUserOverviewBinding binding;
    private UserViewModel userViewModel;
    private SessionViewModel sessionViewModel;
    private UserModel userModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserOverviewBinding.inflate(inflater, container, false);
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
        goToShifts();
        goToWorkingTimes();
        goToVacationOrIllness();
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), userModelResult -> {
            if (!userModelResult.isSuccess()) {
                Toast.makeText(mContext, "Login failed - " + userModelResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            userModel = userModelResult.getValue();
            binding.titleUserNameDash.setText(userModel.getSurname());
        });
    }

    private void signOut() {
        FloatingActionButton fabSignOut = binding.fabSignOut;

        fabSignOut.setOnClickListener(view -> {
            sessionViewModel.removeSessionsSnapshot();
            userViewModel.signOutUser();
            Toast.makeText(this.binding.getRoot().getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            goToLoginFragment();
        });
    }

    private void goToLoginFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN).setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private void goToVacationOrIllness() {
        Button btnVI = binding.btnVacationIllness;

        btnVI.setOnClickListener(view -> {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, VIFragment.class, null, Constants.TAG_VACATION_ILLNESS).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    private void goToShifts() {
        Button btnAssignedSessions = binding.btnToShifts;

        btnAssignedSessions.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, ShiftFragment.class, null, Constants.TAG_LOGIN).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    private void goToWorkingTimes() {
        Button btnAssignedSessions = binding.btnToWorkingTimes;

        btnAssignedSessions.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, DashboardFragment.class, null, Constants.TAG_DASHBOARD).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }
}