package com.albsig.stundenmanager.data.remote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    private static final String TAG = "UserRepositoryImpl";
    private final Context context;
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFunctions firebaseFunctions;

    public UserRepositoryImpl(Context context) {
        this.context = context;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFunctions = FirebaseFunctions.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void registerUser(JSONObject userData, ResultCallback<UserModel> resultCallback) {
        Log.d(TAG, "registerUser: " + userData);
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_USER).call(userData).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Exception e = task.getException();
                assert e != null;
                resultCallback.onError(Result.error(new Exception("Process could not be made " + e)));
                return;
            }

            Object result = task.getResult().getData();
            if (result == null) {
                resultCallback.onError(Result.error(new Exception("User not found")));
                return;
            }

            Map<String, Object> userMap = (Map<String, Object>) result;
            UserModel userModel = new UserModel(userMap);
            resultCallback.onSuccess(Result.success(userModel));

        });
    }

    @Override
    public void updateUser(JSONObject userData, ResultCallback<UserModel> resultCallback) {

    }

    @Override
    public void loginUser(JSONObject userData, ResultCallback<UserModel> resultCallback) {
        String eMail;
        String password;

        try {
            eMail = userData.getString(Constants.USER_MODEL_EMAIL);
            password = userData.getString(Constants.USER_MODEL_PASSWORD);

            firebaseAuth.signInWithEmailAndPassword(eMail, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        resultCallback.onError(Result.error(task.getException()));
                        return;
                    }

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        return;
                    }
                    firebaseFirestore.collection(Constants.USERS_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            resultCallback.onError(Result.error(task1.getException()));
                            return;
                        }

                        DocumentSnapshot documentSnapshot = task1.getResult();
                        if (!documentSnapshot.exists()) {
                            resultCallback.onError(Result.error(new Exception("User not found")));
                            return;
                        }

                        UserModel userModel = new UserModel(user.getUid(), user.getEmail(), documentSnapshot);
                        resultCallback.onSuccess(Result.success(userModel));
                    });
                }
            });
        } catch (Exception e) {
            resultCallback.onError(Result.error(e));
        }
    }

    @Override
    public void signOutUser(ResultCallback<Boolean> resultCallback) {
        firebaseAuth.signOut();
        resultCallback.onSuccess(Result.success(true));
    }

    @Override
    public void createIllness(JSONObject illnessData, ResultCallback<Boolean> resultCallback) {
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_ILLNESS).call(illnessData).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Create illness failed " + task.getException());
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            Log.d(TAG, "Create illness successful");
            resultCallback.onSuccess(Result.success(true));
        });
    }

    @Override
    public void createVacation(JSONObject vacationData, ResultCallback<Boolean> resultCallback) {
        Log.d(TAG, "createVacation: " + vacationData);
        firebaseFunctions.getHttpsCallable(Constants.HTTP_CALLABLE_REF_CREATE_VACATION).call(vacationData).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Create vacation failed " + task.getException());
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            Log.d(TAG, "Create vacation successful");
            resultCallback.onSuccess(Result.success(true));
        });
    }

    @Override
    public void getVIList(String uid, ResultCallback<List<VIModel>> resultCallback) {
        List<VIModel> viList = new ArrayList<>();
        firebaseFirestore.collection(Constants.USERS_COLLECTION).document(uid).collection(Constants.VACATION_COLLECTION).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                resultCallback.onError(Result.error(task.getException()));
                return;
            }
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                Log.d(TAG, document.getId() + " => " + document.getData());
                String docId = document.getId();
                Timestamp startDate = document.getTimestamp("startDate");
                Timestamp endDate = document.getTimestamp("endDate");
                String approval = (String) document.get("approval");
                VIModel viModel = new VIModel(uid, docId, Constants.VACATION_COLLECTION, startDate, endDate, approval);
                viList.add(viModel);
            }

            firebaseFirestore.collection(Constants.USERS_COLLECTION).document(uid).collection(Constants.ILLNESS_COLLECTION).get().addOnCompleteListener(task2 -> {
                if (!task2.isSuccessful()) {
                    resultCallback.onError(Result.error(task2.getException()));
                    return;
                }

                for (DocumentSnapshot document : task2.getResult().getDocuments()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    String docId = document.getId();
                    Timestamp startDate = (Timestamp) document.get("startDate");
                    Timestamp endDate = (Timestamp) document.get("endDate");
                    String approval = (String) document.get("approval");
                    VIModel viModel = new VIModel(uid, docId, Constants.ILLNESS_COLLECTION, startDate, endDate, approval);
                    viList.add(viModel);
                }
                resultCallback.onSuccess(Result.success(viList));
            });
        });
    }


}
