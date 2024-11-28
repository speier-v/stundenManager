package com.albsig.stundenmanager.common.viewmodel.session;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;

import org.json.JSONObject;

import java.util.List;

public class SessionViewModel extends ViewModel {

    private static final String TAG = "SessionViewModel";
    private final SessionRepository sessionRepository;
    private final MutableLiveData<Result<List<SessionModel>>> sessionsResult = new MutableLiveData<>();
    private final MutableLiveData<Result<SessionModel>> selectedSessionResult = new MutableLiveData<>();

    public SessionViewModel(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public LiveData<Result<List<SessionModel>>> getSessions() {
        return sessionsResult;
    }

    public void setSelectedSession(Result<SessionModel> session) {
        selectedSessionResult.setValue(session);
    }

    public LiveData<Result<SessionModel>> getSelectedSession() {
        return selectedSessionResult;
    }

    public void getSessions(String uid) {
        sessionRepository.getSessions(uid, new ResultCallback<List<SessionModel>>() {
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

    public void deleteSession(String uid, String documentId, ResultCallback<Boolean> resultCallback) {
        sessionRepository.deleteSession(uid, documentId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                getSessions(uid);
                resultCallback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                resultCallback.onError(error);
            }
        });

    }

    public void createSession(JSONObject sessionData, ResultCallback<Boolean> resultCallback) {
        sessionRepository.createSession(sessionData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                String uid = sessionData.optString("uid");
                getSessions(uid);
                resultCallback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                resultCallback.onError(error);
            }
        });
    }

    public void createBreak(JSONObject breakData, ResultCallback<Boolean> resultCallback) {
        sessionRepository.createBreak(breakData, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                String uid = breakData.optString("uid");
                getSessions(uid);
                resultCallback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                resultCallback.onError(error);
            }
        });
    }

    public void deleteBreak(String uid, String documentId, BreakModel breakModel, ResultCallback<Boolean> resultCallback) {
        sessionRepository.deleteBreak(uid, documentId, breakModel, new ResultCallback<Boolean>() {

            @Override
            public void onSuccess(Result<Boolean> response) {
                getSessions(uid);
                resultCallback.onSuccess(response);
            }

            @Override
            public void onError(Result<Boolean> error) {
                resultCallback.onError(error);
            }
        });
    }
}
