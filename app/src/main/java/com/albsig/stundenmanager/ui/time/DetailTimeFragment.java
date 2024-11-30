package com.albsig.stundenmanager.ui.time;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentDetailTimeBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.google.firebase.Timestamp;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class DetailTimeFragment extends Fragment implements BreakAdapter.OnBreakClickListener {

    private static final String TAG = "DetailTimeFragment";

    private Context mContext;
    private FragmentDetailTimeBinding binding;
    private UserViewModel userViewModel;
    private SessionViewModel sessionViewModel;
    private BreakAdapter breakAdapter;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private Timestamp selectedBreakStartTime;

    @Nullable
    private SessionModel sessionModel;
    @Nullable
    private UserModel userModel;

    @Override
    public void onBreakDeleted(BreakModel breakModel) {
        assert userModel != null;
        assert sessionModel != null;

        sessionViewModel.deleteBreak(userModel.getUid(), sessionModel.getDocumentId(), breakModel, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Toast.makeText(mContext, "Break deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(mContext, "Break not deleted - " + error.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSharedViewModel();
    }

    private void initSharedViewModel() {
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        sessionViewModel = new ViewModelProvider(requireActivity()).get(SessionViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailTimeBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        breakAdapter = new BreakAdapter(this);
        binding.breaksRecyclerView.setAdapter(breakAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
        setAddBreakButton();
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Toast.makeText(mContext, "failed to get user data" + result.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            userModel = result.getValue();
        });

        sessionViewModel.getSelectedSession().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                Toast.makeText(mContext, "failed to get session" + result.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            sessionModel = result.getValue();
            setDateTime(sessionModel);

            breakAdapter.setBreaks(sessionModel.getBreaks());
        });
    }

    private void setDateTime(SessionModel sessionModel) {
        assert sessionModel != null;
        String formattedStartTime = new SimpleDateFormat(Constants.FORMATTED_DATE_PATTERN, Locale.getDefault()).format(sessionModel.getStartTime().toDate());
        String formattedEndTime = new SimpleDateFormat(Constants.FORMATTED_DATE_PATTERN, Locale.getDefault()).format(sessionModel.getEndTime().toDate());
        binding.startTimeTextView.setText(formattedStartTime);
        binding.endTimeTextView.setText(formattedEndTime);
    }

    private void startProcessBreak() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //Create start date
        new DatePickerDialog(mContext, (viewDateStart, yearSelectedStart, monthSelectedStart, daySelectedStart) -> {
            //Create start Time
            new TimePickerDialog(mContext, (viewTimerStart, hourOfDayStart, minuteSelectedStart) -> {
                //Create end date
                new DatePickerDialog(mContext, (viewDateEnd, yearSelectedEnd, monthSelectedEnd, daySelectedEnd) -> {
                    //Create end time
                    new TimePickerDialog(mContext, (viewTimerEnd, hourOfDayEnd, minuteSelectedEnd) -> {
                        Timestamp startTimestamp = Helpers.createCustomTimestamp(yearSelectedStart, monthSelectedStart, daySelectedStart, hourOfDayStart, minuteSelectedStart);
                        Timestamp endTimestamp = Helpers.createCustomTimestamp(yearSelectedEnd, monthSelectedEnd, daySelectedEnd, hourOfDayEnd, minuteSelectedEnd);
                        createBreak(startTimestamp, endTimestamp);
                    }, hour, minute, true).show();
                }, year, month, day).show();

            }, hour, minute, true).show();
        }, year, month, day).show();
    }

    private void createBreak(Timestamp startTimestamp, Timestamp endTimestamp) {
        assert userModel != null;
        assert sessionModel != null;

        JSONObject breakData = new JSONObject(Map.of("uid", userModel.getUid(), "documentId", sessionModel.getDocumentId(), "startTime", startTimestamp.toDate().getTime(), "endTime", endTimestamp.toDate().getTime()));

        sessionViewModel.createBreak(breakData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Toast.makeText(mContext, "Break created successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(mContext, "Break not created - " + error.getError(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAddBreakButton() {
        binding.addBreakButton.setOnClickListener(v -> {
            startProcessBreak();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        sessionViewModel.removeSessionSnapshot();
    }
}