package com.albsig.stundenmanager.common.viewmodel.shift;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.ShiftRepository;

import org.json.JSONObject;

import java.util.List;

public class ShiftViewModel extends ViewModel {

    private static final String TAG = "ShiftViewModel";
    private final ShiftRepository shiftRepository;
    private MutableLiveData<Result<List<ShiftModel>>> shiftResult = new MutableLiveData<>();

    public ShiftViewModel(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    public LiveData<Result<List<ShiftModel>>> getShifts() {
        return shiftResult;
    }

    public void addShiftSnapshot(String uid) {
        shiftRepository.addShiftSnapshotListener(uid, new ResultCallback<List<ShiftModel>>() {
            @Override
            public void onSuccess(Result<List<ShiftModel>> response) {
                Log.d(TAG, "Add snapshot shifts successful");
                for (ShiftModel shift : response.getValue()) {
                    Log.d(TAG, "Shift: " + shift.toString());
                }
                shiftResult.setValue(response);
            }

            @Override
            public void onError(Result<List<ShiftModel>> error) {
                shiftResult.setValue(error);
            }
        });
    }

    public void getShifts(String uid) {
        shiftRepository.getShifts(uid, new ResultCallback<List<ShiftModel>>() {
            @Override
            public void onSuccess(Result<List<ShiftModel>> response) {
                Log.d(TAG, "Get shifts successful");
                shiftResult.setValue(response);
            }

            @Override
            public void onError(Result<List<ShiftModel>> error) {
                Log.d(TAG, "Get shifts failed " + error.getError().toString());
                shiftResult.setValue(error);
            }
        });
    }

    public void removeShiftSnapshot() {
        shiftRepository.removeShiftSnapshotListener();
        shiftResult = new MutableLiveData<>();
    }
}
