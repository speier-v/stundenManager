package com.albsig.stundenmanager.data.remote;

import android.util.Log;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.albsig.stundenmanager.domain.model.session.SessionModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionRepositoryImpl implements SessionRepository {

    private static final String TAG = "SessionRepository";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseFunctions firebaseFunctions;

    public SessionRepositoryImpl() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public void getSessions(String uid, ResultCallback<List<SessionModel>> resultCallback) {
        Log.d(TAG, "Get sessions");
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.SESSIONS_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Get sessions failed");
                        resultCallback.onError(Result.error(task.getException()));
                        return;
                    }

                    if (task.getResult() == null) {
                        Log.d(TAG, "Get sessions failed");
                        resultCallback.onError(Result.error(new Exception("Session-List is null")));
                        return;
                    }

                    List<SessionModel> sessionModels = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        SessionModel sessionModel = document.toObject(SessionModel.class);
                        assert sessionModel != null;
                        sessionModel.setDocumentId(document.getId());
                        sessionModels.add(sessionModel);
                    }
                    Log.d(TAG, "Get sessions successful");
                    resultCallback.onSuccess(Result.success(sessionModels));
                });
    }

    @Override
    public void updateSession(JSONObject sessionData, ResultCallback<SessionModel> resultCallback) {

    }

    @Override
    public void deleteSession(String uid, String sessionId, ResultCallback<Boolean> resultCallback) {
        Log.d(TAG, "Delete session");
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.SESSIONS_COLLECTION)
                .document(sessionId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete session successful");
                    resultCallback.onSuccess(Result.success(true));
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "Delete session failed");
                    resultCallback.onError(Result.error(e));
                });
    }

    @Override
    public void getSession(String uid, ResultCallback<SessionModel> resultCallback) {

    }

    @Override
    public void createSession(JSONObject sessionData, ResultCallback<Boolean> resultCallback) {
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_SESSION)
                .call(sessionData)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Create session failed " + task.getException());
                        resultCallback.onError(Result.error(task.getException()));
                        return;
                    }

                    Log.d(TAG, "Create session successful");
                    resultCallback.onSuccess(Result.success(true));
                });

    }

    @Override
    public void createBreak(JSONObject breakData, ResultCallback<Boolean> resultCallback) {
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_BREAK)
                .call(breakData)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Create break failed " + task.getException());
                        resultCallback.onError(Result.error(task.getException()));
                        return;
                    }

                    Log.d(TAG, "Create break successful");
                    resultCallback.onSuccess(Result.success(true));
                });
    }

    @Override
    public void deleteBreak(String uid, String documentId, BreakModel breakModel, ResultCallback<Boolean> resultCallback) {
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.SESSIONS_COLLECTION)
                .document(documentId)
                .update("breaks", FieldValue.arrayRemove(breakModel))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Delete break successful");
                    resultCallback.onSuccess(Result.success(true));

                }).addOnFailureListener( e -> {
                    Log.d(TAG, "Delete break failed " + e.toString());
                    resultCallback.onError(Result.error(e));
                });
    }
}
