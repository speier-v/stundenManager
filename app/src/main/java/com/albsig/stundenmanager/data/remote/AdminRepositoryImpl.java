package com.albsig.stundenmanager.data.remote;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.VIModel;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminRepositoryImpl implements AdminRepository {

    private static final String TAG = "AdminRepository";
    private final Context context;
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseAuth firebaseAuth;

    public AdminRepositoryImpl(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.context = context;
    }

    @Override
    public void loginAdmin(JSONObject userData, ResultCallback<UserModel> resultCallback) {
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
                        if (!userModel.getRole().equals(Constants.ROLE_ADMIN)) {
                            firebaseAuth.signOut();
                            resultCallback.onError(Result.error(new Exception("User is not an admin")));
                            return;
                        }

                        resultCallback.onSuccess(Result.success(userModel));
                    });
                }
            });
        } catch (Exception e) {
            resultCallback.onError(Result.error(e));
        }
    }

    @Override
    public void getUsers(ResultCallback<List<UserModel>> resultCallback) {
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .whereEqualTo(Constants.USER_MODEL_ROLE, Constants.ROLE_USER)
                .get().addOnCompleteListener(task -> {
            List<UserModel> users = new ArrayList<>();
            if (!task.isSuccessful()) {
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            task.getResult().forEach(documentSnapshot -> {
                UserModel userModel = new UserModel(documentSnapshot.getId(), "", documentSnapshot);
                users.add(userModel);
            });

            resultCallback.onSuccess(Result.success(users));
        });
    }

    @Override
    public void getCheckedVIList(String uid, ResultCallback<List<VIModel>> resultCallback) {
        List<VIModel> viList = new ArrayList<>();
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.VACATION_COLLECTION)
                .whereNotEqualTo(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_CHECK)
                .get()
                .addOnCompleteListener(task -> {
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

            firebaseFirestore.collection(Constants.USERS_COLLECTION)
                    .document(uid)
                    .collection(Constants.ILLNESS_COLLECTION)
                    .whereNotEqualTo(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_CHECK)
                    .get()
                    .addOnCompleteListener(task2 -> {
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

    @Override
    public void getVIListToCheck(String uid, ResultCallback<List<VIModel>> resultCallback) {
        List<VIModel> viList = new ArrayList<>();
        firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.VACATION_COLLECTION)
                .whereEqualTo(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_CHECK)
                .get()
                .addOnCompleteListener(task -> {
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

                    firebaseFirestore.collection(Constants.USERS_COLLECTION)
                            .document(uid)
                            .collection(Constants.ILLNESS_COLLECTION)
                            .whereEqualTo(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_CHECK)
                            .get()
                            .addOnCompleteListener(task2 -> {
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

    @Override
    public void updateVIModel(String approvalType, String uid, String docId, ResultCallback<Boolean> resultCallback) {
        DocumentReference docRef = firebaseFirestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .collection(Constants.VACATION_COLLECTION)
                .document(docId);

        if (Constants.APPROVAL_STATUS_APPROVED.equals(approvalType)) {
            docRef.update(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_APPROVED).addOnCompleteListener( task -> {
                if (!task.isSuccessful()) {
                    resultCallback.onError(Result.error(task.getException()));
                    return;
                }

                resultCallback.onSuccess(Result.success(true));
            });
            return;
        }

        docRef.update(Constants.FIELD_APPROVAL, Constants.APPROVAL_STATUS_DENIED).addOnCompleteListener( task -> {
            if (!task.isSuccessful()) {
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            resultCallback.onSuccess(Result.success(true));
        });
    }

    @Override
    public void getShifts(ResultCallback<List<ShiftModel>> resultCallback) {
        firebaseFirestore.collection(Constants.SHIFTS_COLLECTION).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            List<ShiftModel> shiftList = new ArrayList<>();
            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                ShiftModel shiftModel = new ShiftModel(document);
                assert shiftModel != null;
                shiftModel.setDocumentId(document.getId());
                shiftList.add(shiftModel);
            }

            resultCallback.onSuccess(Result.success(shiftList));
        });
    }
}
