package com.albsig.stundenmanager.common.viewmodel.session;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;

import org.json.JSONObject;

import java.util.List;

public class SessionViewModel extends ViewModel {

    private static final String TAG = "SessionViewModel";
    private final SessionRepository sessionRepository;
    private final MutableLiveData<Result<List<SessionModel>>> sessionsResult = new MutableLiveData<>();

    public SessionViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public LiveData<Result<List<SessionModel>>> getSessions() {
        return sessionsResult;
    }

    public void getSessions(String uid) {
        sessionRepository.getSessions(uid,  new ResultCallback<List<SessionModel>>() {
            @Override
            public void onSuccess(Result<List<SessionModel>> response) {
                Log.d(TAG, "Get sessions successful");
                sessionsResult.setValue(response);
            }

            @Override
            public void onError(Result<List<SessionModel>> error) {
                Log.d(TAG, "Get sessions failed " + error.getError().toString());
                sessionsResult.setValue(error);
            }
        });
    }
}
