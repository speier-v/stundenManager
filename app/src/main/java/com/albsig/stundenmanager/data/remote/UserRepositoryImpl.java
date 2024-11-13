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
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

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
        //        Map<String, Object> userData = new HashMap<>(
//                Map.of(
//                        "email", email,
//                        "password", password,
//                        "name", name,
//                        "surname", surname,
//                        "birthday", birthday,
//                        "street", street,
//                        "zipCode", zipCode,
//                        "city", city
//                )
//        );
//
//        FirebaseFunctions.getInstance()
//                .getHttpsCallable("createUser")
//                .call(userData)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        resultCallback.onSuccess(Result.success(user));
//                    } else {
//                        Exception e = task.getException();
//                        assert e != null;
//                    }
//                });
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

            firebaseAuth
                    .signInWithEmailAndPassword(eMail, password)
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
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
                            firebaseFirestore.collection(Constants.USERS_COLLECTION)
                                    .document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(task1 -> {
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
                                            }
                                    );
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
}
