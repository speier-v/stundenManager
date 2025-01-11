package com.albsig.stundenmanager.data.remote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.repository.ShiftPlannerRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftPlannerRepositoryImpl implements ShiftPlannerRepository {

    private static final String TAG = "ShiftPlannerRepository";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFunctions firebaseFunctions;

    public ShiftPlannerRepositoryImpl() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFunctions = FirebaseFunctions.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }


    @Override
    public void getWorkers(ResultCallback<List<UserModel>> resultCallback) {
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .whereEqualTo(Constants.USER_MODEL_ROLE, Constants.ROLE_USER)
                .get()
                .addOnCompleteListener( task -> {
                    if (!task.isSuccessful()) {
                        resultCallback.onError(Result.error(task.getException()));
                        return;
                    }

                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if (documents.isEmpty()) {
                        resultCallback.onError(Result.error(new Exception("No workers found")));
                        return;
                    }

                    List<UserModel>  workers = new ArrayList<>();
                    for (DocumentSnapshot doc: documents) {
                        Map<String, Object> userMap = doc.getData();
                        if (userMap == null) {
                            resultCallback.onError(Result.error(new Exception("User not found")));
                            return;
                        }

                        UserModel workerModel = new UserModel(doc.getId(), "", doc);
                        workers.add(workerModel);
                    }

                    Log.d(TAG, "getWorkers: " + workers);
                    resultCallback.onSuccess(Result.success(workers));
                });
    }

    @Override
    public void createShift(JSONObject shiftData, ResultCallback<Boolean> resultCallback) {
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_SHIFT).call(shiftData).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                resultCallback.onError(Result.error(task.getException()));
                return;
            }
            resultCallback.onSuccess(Result.success(true));
        });
    }
}
