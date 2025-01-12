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
import com.albsig.stundenmanager.domain.model.WorkerModel;
import com.albsig.stundenmanager.domain.repository.ShiftPlannerRepository;
import com.google.firebase.Timestamp;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShiftPlannerViewModel extends ViewModel {

    private static final String TAG = "ShiftPlannerViewModel";
    private final ShiftPlannerRepository shiftPlannerRepository;
    private final MutableLiveData<Result<List<WorkerModel>>> resultLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<WorkerModel>> workersMorning = new MutableLiveData<>();
    private final MutableLiveData<List<WorkerModel>> workersLate = new MutableLiveData<>();
    private final MutableLiveData<List<WorkerModel>> workersNight = new MutableLiveData<>();
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

    public LiveData<Result<List<WorkerModel>>> getResultLiveData() {
        return resultLiveData;
    }

    public LiveData<List<WorkerModel>> getWorkersMorning() {
        return workersMorning;
    }

    public LiveData<List<WorkerModel>> getWorkersLate() {
        return workersLate;
    }

    public LiveData<List<WorkerModel>> getWorkersNight() {
        return workersNight;
    }

    public void getWorker() {
        shiftPlannerRepository.getWorkers(new ResultCallback<List<WorkerModel>>() {
            @Override
            public void onSuccess(Result<List<WorkerModel>> response) {
                resultLiveData.setValue(response);
            }

            @Override
            public void onError(Result<List<WorkerModel>> error) {
                resultLiveData.setValue(error);
            }
        });
    }

    public void addWorkerToShift(WorkerModel worker) {
        List<WorkerModel> shiftList= new ArrayList<>();

        if (worker.getShift() == Constants.MORNING_SHIFT) {
            if (workersMorning.getValue() != null) {
                shiftList = workersMorning.getValue();
            }

            shiftList.add(worker);
            workersMorning.setValue(shiftList);
        } else if (worker.getShift() == Constants.LATE_SHIFT) {
            if (workersLate.getValue() != null) {
                shiftList = workersLate.getValue();
            }

            shiftList.add(worker);
            workersLate.setValue(shiftList);
        } else if (worker.getShift() == Constants.NIGHT_SHIFT) {
            if (workersNight.getValue() != null) {
                shiftList = workersNight.getValue();
            }

            shiftList.add(worker);
            workersNight.setValue(shiftList);
        }
    }

    public void removeWorkerFromShift(WorkerModel worker) {
        List<WorkerModel> shiftList= new ArrayList<>();

        if (worker.getShift() == Constants.MORNING_SHIFT) {
            if (workersMorning.getValue() != null) {
                shiftList = workersMorning.getValue();
            }

            shiftList.remove(worker);
            workersMorning.setValue(shiftList);
        } else if (worker.getShift() == Constants.LATE_SHIFT) {
            if (workersLate.getValue() != null) {
                shiftList = workersLate.getValue();
            }

            shiftList.remove(worker);
            workersLate.setValue(shiftList);
        } else if (worker.getShift() == Constants.NIGHT_SHIFT) {
            if (workersNight.getValue() != null) {
                shiftList = workersNight.getValue();
            }

            shiftList.remove(worker);
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

        for (WorkerModel worker : workersMorning.getValue()) {
            tmpWorkersMorning.add(worker.getUserReference());
        }

        for (WorkerModel worker : workersLate.getValue()) {
            tmpWorkersLate.add(worker.getUserReference());
        }

        for (WorkerModel worker : workersNight.getValue()) {
            tmpWorkersNight.add(worker.getUserReference());
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
