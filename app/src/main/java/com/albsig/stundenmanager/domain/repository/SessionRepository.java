package com.albsig.stundenmanager.domain.repository;

import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.session.SessionModel;

import org.json.JSONObject;

import java.util.List;

public interface SessionRepository {
    void addSession(JSONObject sessionData, ResultCallback<SessionModel> resultCallback);

    void getSessions(String uid, ResultCallback<List<SessionModel>> resultCallback);

    void updateSession(JSONObject sessionData, ResultCallback<SessionModel> resultCallback);

    void deleteSession(String sessionId, ResultCallback<Boolean> resultCallback);

    void getSession(String uid, ResultCallback<SessionModel> resultCallback);
}
