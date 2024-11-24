package com.albsig.stundenmanager.ui.dashboard;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.FirestoreUtil;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentDashboardBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.albsig.stundenmanager.ui.time.DetailTimeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment implements SessionsAdapter.OnSessionClickListener {

    private static final String TAG = "DashboardFragment";
    private Context mContext;
    private FragmentDashboardBinding binding;
    private SessionsAdapter sessionsAdapter;
    private FirebaseFirestore db;
    private FirestoreUtil firestoreUtil;
    private UserViewModel userViewModel;
    private SessionViewModel sessionViewModel;
    private UserModel userModel;
    private Timestamp startTime;
    private Timestamp endTime;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        firestoreUtil = new FirestoreUtil();
        db = firestoreUtil.getInstance();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel  = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        sessionsAdapter = new SessionsAdapter(this);
        binding.recyclerViewSessions.setAdapter(sessionsAdapter);
    }

    private void loadSessionDates(List<SessionModel> sessionList) {
        sessionsAdapter.updateData(sessionList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sessionsAdapter.clearListener();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
        signOut();
        setAddSessionButton();
    }

    private void setAddSessionButton() {
        FloatingActionButton fabAddSession = binding.fabAddSession;
        fabAddSession.setOnClickListener(v -> {
            startCreateSession();
        });
    }

    private void startCreateSession() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //Create start date
        new DatePickerDialog(mContext,
                (viewDateStart, yearSelectedStart, monthSelectedStart, daySelectedStart) -> {
            //Create start Time
                    new TimePickerDialog(mContext,
                            (viewTimerStart, hourOfDayStart, minuteSelectedStart) -> {
                                //Create end date
                                new DatePickerDialog(mContext,
                                        (viewDateEnd, yearSelectedEnd, monthSelectedEnd, daySelectedEnd) -> {
                                    //Create end time
                                            new TimePickerDialog(mContext,
                                                    (viewTimerEnd, hourOfDayEnd, minuteSelectedEnd) -> {
                                                        Timestamp startTimestamp = Helpers.createCustomTimestamp(yearSelectedStart, monthSelectedStart, daySelectedStart, hourOfDayStart, minuteSelectedStart);
                                                        Timestamp endTimestamp = Helpers.createCustomTimestamp(yearSelectedEnd, monthSelectedEnd, daySelectedEnd, hourOfDayEnd, minuteSelectedEnd);
                                                        startWorkSessionWithCustomStartTime(startTimestamp, endTimestamp);
                                                    }, hour, minute, true).show();
                                        }, year, month, day).show();

                            }, hour, minute, true).show();
                }, year, month, day).show();
    }

    private void startWorkSessionWithCustomStartTime(Timestamp startTime, Timestamp endTime) {
        JSONObject sessionData = new JSONObject(
                Map.of(
                        "uid",userModel.getUid(),
                        "startTime", startTime.toDate().getTime(),
                        "endTime", endTime.toDate().getTime(),
                        "breaks", new ArrayList<Map<String, Timestamp>>()
                )
        );

        sessionViewModel.createSession(sessionData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Toast.makeText(binding.getRoot().getContext(), "Session created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(binding.getRoot().getContext(), "Session not created - " + error.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), userModelResult -> {
            if (!userModelResult.isSuccess()) {
                Toast.makeText(mContext, "Login failed - " + userModelResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            userModel = userModelResult.getValue();
            sessionsAdapter.setUid(userModel.getUid());
            sessionViewModel.getSessions(userModel.getUid());
        });

        sessionViewModel.getSessions().observe(getViewLifecycleOwner(), sessionsResult -> {
            if (!sessionsResult.isSuccess()) {
                Toast.makeText(mContext, "Session not found - " + sessionsResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            List<SessionModel> sessionList = sessionsResult.getValue();
            loadSessionDates(sessionList);
        });
    }

    private void signOut() {
        FloatingActionButton fabSignOut = binding.fabSignOut;

        fabSignOut.setOnClickListener(view -> {
            userViewModel.signOutUser();
            Toast.makeText(this.binding.getRoot().getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            goToLoginFragment();
        });
    }

    private void goToLoginFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN)
                .setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemDelete(String uid, String documentId) {
        sessionViewModel.deleteSession(uid, documentId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                if(response.getValue()) Toast.makeText(mContext, "Session deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(mContext, "Session not deleted - " + error.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(String uid, SessionModel session) {
        sessionViewModel.setSelectedSession(Result.success(session));
        DetailTimeFragment newDetailTimeFragment = new DetailTimeFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newDetailTimeFragment, Constants.TAG_DETAIL_TIME)
                .addToBackStack(null);
        fragmentTransaction.commit();
    }
}