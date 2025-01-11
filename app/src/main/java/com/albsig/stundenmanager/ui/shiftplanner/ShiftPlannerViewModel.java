package com.albsig.stundenmanager.ui.shiftplanner;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.Helpers;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.ShiftPlannerRepository;
import com.google.firebase.Timestamp;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShiftPlannerViewModel extends ViewModel {

    private static final String TAG = "ShiftPlannerViewModel";
    private final ShiftPlannerRepository shiftPlannerRepository;
    private final MutableLiveData<Result<List<UserModel>>> resultLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<UserModel>> workersMorning = new MutableLiveData<>();
    private final MutableLiveData<List<UserModel>> workersLate = new MutableLiveData<>();
    private final MutableLiveData<List<UserModel>> workersNight = new MutableLiveData<>();
    private LocalDate relativeDate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ShiftPlannerViewModel(ShiftPlannerRepository shiftPlannerRepository) {
        this.shiftPlannerRepository = shiftPlannerRepository;
        relativeDate = LocalDate.now();
        setUpDays(relativeDate);
    }

    public void weekUp() {
        relativeDate = relativeDate.plusWeeks(1);
        setUpDays(relativeDate);
    }

    public void weekDown() {
        relativeDate = relativeDate.minusWeeks(1);
        setUpDays(relativeDate);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    private void setUpDays(LocalDate relativeDate) {
        LocalDate tmpDateStart = relativeDate.with(DayOfWeek.MONDAY);
        LocalDate tmpDateEnd = relativeDate.with(DayOfWeek.SATURDAY);
        startDate = tmpDateStart.atTime(6,0,0,0);
        endDate = tmpDateEnd.atTime(6,0,0,0);
    }

    public LiveData<Result<List<UserModel>>> getResultLiveData() {
        return resultLiveData;
    }

    public LiveData<List<UserModel>> getWorkersMorning() {
        return workersMorning;
    }

    public LiveData<List<UserModel>> getWorkersLate() {
        return workersLate;
    }

    public LiveData<List<UserModel>> getWorkersNight() {
        return workersNight;
    }

    public void getUser() {
        shiftPlannerRepository.getWorkers(new ResultCallback<List<UserModel>>() {
            @Override
            public void onSuccess(Result<List<UserModel>> response) {
                resultLiveData.setValue(response);
            }

            @Override
            public void onError(Result<List<UserModel>> error) {
                resultLiveData.setValue(error);
            }
        });
    }

    public void addWorkerToShift(int shift, UserModel worker) {
        List<UserModel> shiftList= new ArrayList<>();

        if (shift == Constants.MORNING_SHIFT) {
            if (workersMorning.getValue() != null) {
                shiftList = workersMorning.getValue();
            }

            shiftList.add(worker);
            workersMorning.setValue(shiftList);
        } else if (shift == Constants.LATE_SHIFT) {
            if (workersLate.getValue() != null) {
                shiftList = workersLate.getValue();
            }

            shiftList.add(worker);
            workersLate.setValue(shiftList);
        } else if (shift == Constants.NIGHT_SHIFT) {
            if (workersNight.getValue() != null) {
                shiftList = workersNight.getValue();
            }

            shiftList.add(worker);
            workersNight.setValue(shiftList);
        }
    }

    public void removeWorkerFromShift(int shift, UserModel userModel) {
        List<UserModel> shiftList= new ArrayList<>();

        if (shift == Constants.MORNING_SHIFT) {
            if (workersMorning.getValue() != null) {
                shiftList = workersMorning.getValue();
            }

            shiftList.remove(userModel);
            workersMorning.setValue(shiftList);
        } else if (shift == Constants.LATE_SHIFT) {
            if (workersLate.getValue() != null) {
                shiftList = workersLate.getValue();
            }

            shiftList.remove(userModel);
            workersLate.setValue(shiftList);
        } else if (shift == Constants.NIGHT_SHIFT) {
            if (workersNight.getValue() != null) {
                shiftList = workersNight.getValue();
            }

            shiftList.remove(userModel);
            workersNight.setValue(shiftList);
        }
    }

    public void createShift(ResultCallback<Boolean> callback) {
        if (workersMorning.getValue() == null || workersMorning.getValue().isEmpty()) {
            callback.onError(Result.error(new Exception("No workers selected for morning shift")));
            return;
        }

        if (workersLate.getValue() == null || workersLate.getValue().isEmpty()) {
            callback.onError(Result.error(new Exception("No workers selected for late shift")));
            return;
        }

        if (workersNight.getValue() == null || workersNight.getValue().isEmpty()) {
            callback.onError(Result.error(new Exception("No workers selected for night shift")));
            return;
        }

        List<String> tmpWorkersMorning = new ArrayList<>();
        List<String> tmpWorkersLate = new ArrayList<>();
        List<String> tmpWorkersNight = new ArrayList<>();

        for (UserModel worker : workersMorning.getValue()) {
            String idReference = "/" + Constants.USERS_COLLECTION + "/"  + worker.getUid();
            tmpWorkersMorning.add(idReference);
        }

        for (UserModel worker : workersLate.getValue()) {
            String idReference = "/" + Constants.USERS_COLLECTION + "/" + worker.getUid();
            tmpWorkersLate.add(idReference);
        }

        for (UserModel worker : workersNight.getValue()) {
            String idReference = "/" + Constants.USERS_COLLECTION + "/" + worker.getUid();
            tmpWorkersNight.add(idReference);
        }

        Timestamp tmpStartDate = Helpers.createCustomTimestamp(startDate.getYear(), startDate.getMonth().getValue(), startDate.getDayOfMonth(), startDate.getHour(), startDate.getMinute());
        Timestamp tmpEndDate = Helpers.createCustomTimestamp(endDate.getYear(), endDate.getMonth().getValue(), endDate.getDayOfMonth(), endDate.getHour(), endDate.getMinute());

        JSONObject shiftData = new JSONObject(
                Map.of(
                        "startDate", tmpStartDate.toDate().getTime(),
                        "endDate", tmpEndDate.toDate().getTime(),
                        "morningShift", tmpWorkersMorning,
                        "lateShift", tmpWorkersLate,
                        "nightShift", tmpWorkersNight
                )
        );

        Log.d(TAG, "Shift data: " + shiftData.toString());

        shiftPlannerRepository.createShift(shiftData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                workersMorning.setValue(new ArrayList<>());
                workersLate.setValue(new ArrayList<>());
                workersNight.setValue(new ArrayList<>());
                Log.d(TAG, "Shift created");
                callback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                Log.d(TAG, "Shift not created! " + error.getError());
                callback.onError(error);
            }
        });
    }
}
