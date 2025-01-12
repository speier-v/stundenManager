package com.albsig.stundenmanager.data.remote;

import android.util.Log;

import androidx.annotation.Nullable;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.BreakModel;
import com.albsig.stundenmanager.domain.model.session.ShiftModel;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.ShiftRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShiftRepositoryImpl implements ShiftRepository {


    private static final String TAG = "ShiftRepository";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseFunctions firebaseFunctions;

    private ListenerRegistration shiftSnapshotListener;

    public ShiftRepositoryImpl() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFunctions = FirebaseFunctions.getInstance();
    }

    @Override
    public void addShiftSnapshotListener(String uid, ResultCallback<List<ShiftModel>> resultCallback) {
        if (shiftSnapshotListener != null) {
            Log.d(TAG, "Shift snapshot listener already exists");
            return;
        }

        shiftSnapshotListener = firebaseFirestore.collection("shifts").addSnapshotListener( (queryDocumentSnapshots, err) -> {
            if (err != null) {
                Log.d(TAG, "Listen failed " + err.toString());
                return;
            }

            if (queryDocumentSnapshots == null) {
                Log.d(TAG, "Collection is null");
                return;
            }

            List<ShiftModel> shiftModels = new ArrayList<>();
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                ShiftModel shiftModel = document.toObject(ShiftModel.class);
                assert shiftModel != null;
                shiftModels.add(shiftModel);
            }

            Log.d(TAG, "Collection data: " + queryDocumentSnapshots.getDocuments());
            resultCallback.onSuccess(Result.success(shiftModels));
        });
    }

    @Override
    public void removeShiftSnapshotListener() {
        shiftSnapshotListener.remove();
        shiftSnapshotListener = null;
    }

    @Override
    public void getShifts(String uid, ResultCallback<List<ShiftModel>> resultCallback) {
        Log.d(TAG, "Get shifts");

        firebaseFirestore.collection("shifts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ShiftModel> shiftModels = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Extract shift fields
                    List<DocumentReference> morningShift = (List<DocumentReference>) document.get("morningShift");
                    List<DocumentReference> lateShift = (List<DocumentReference>) document.get("lateShift");
                    List<DocumentReference> nightShift = (List<DocumentReference>) document.get("nightShift");

                    // Check if the user is in morning shifts
                    if (morningShift != null && isUserInShift(morningShift, uid)) {
                        ShiftModel shiftModel = new ShiftModel("Morning", document);
                        shiftModels.add(shiftModel);
                    }

                    // Check if the user is in late shifts
                    if (lateShift != null && isUserInShift(lateShift, uid)) {
                        ShiftModel shiftModel = new ShiftModel("Late", document);
                        shiftModels.add(shiftModel);
                    }

                    // Check if the user is in night shifts
                    if (nightShift != null && isUserInShift(nightShift, uid)) {
                        ShiftModel shiftModel = new ShiftModel("Night", document);
                        shiftModels.add(shiftModel);
                    }
                }

                resultCallback.onSuccess(Result.success(shiftModels));
            } else {
                Log.d(TAG, "Couldn't fetch shifts");
            }
        });

        /*
        firebaseFirestore.collection(Constants.USERS_COLLECTION).document(uid).collection(Constants.SESSIONS_COLLECTION).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Get shifts failed");
                resultCallback.onError(Result.error(task.getException()));
                return;
            }

            if (task.getResult() == null) {
                Log.d(TAG, "Get shifts failed");
                resultCallback.onError(Result.error(new Exception("Shift-List is null")));
                return;
            }

            List<ShiftModel> shiftModels = new ArrayList<>();
            for (DocumentSnapshot document : task.getResult()) {
                ShiftModel shiftModel = document.toObject(ShiftModel.class);
                assert shiftModel != null;
                shiftModels.add(shiftModel);
            }
            Log.d(TAG, "Get shifts successful");
            resultCallback.onSuccess(Result.success(shiftModels));
        });
         */
    }

    private boolean isUserInShift(List<DocumentReference> shiftList, String userUid) {
        if (shiftList == null) return false;

        for (DocumentReference userRef : shiftList) {
            if (userRef != null && userRef.getId().equals(userUid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateShift(JSONObject shiftData, ResultCallback<ShiftModel> resultCallback) {

    }
}
