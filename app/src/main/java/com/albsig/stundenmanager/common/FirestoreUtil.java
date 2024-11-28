package com.albsig.stundenmanager.common;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUtil {

    private static final String TAG = "FirestoreUtil";
    private final FirebaseFirestore db;

    public FirestoreUtil() {
        this.db = FirebaseFirestore.getInstance();
    }

    public FirebaseFirestore getInstance() {
        return db;
    }

    public void addUser(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        db.collection(Constants.USERS_COLLECTION).document(userId).set(user).addOnSuccessListener(aVoid -> Log.d(TAG, "User added with ID: " + userId)).addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
    }

    public void startWorkSession(String userId, Timestamp customStartTime, FirestoreCallback callback) {
        CollectionReference workSessionsRef = db.collection(Constants.USERS_COLLECTION).document(userId).collection(Constants.SESSIONS_COLLECTION);

        Timestamp dayStart = getDayStartTimestamp(customStartTime);
        Timestamp dayEnd = getDayEndTimestamp(customStartTime);

        workSessionsRef.whereGreaterThanOrEqualTo("startTime", dayStart).whereLessThanOrEqualTo("startTime", dayEnd).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    callback.onFailure(new Exception("A work session for this day already exists."));
                } else {
                    Map<String, Object> workSession = new HashMap<>();
                    workSession.put("startTime", customStartTime);
                    workSession.put("endTime", null);
                    workSession.put("breaks", new ArrayList<Map<String, Timestamp>>());

                    workSessionsRef.add(workSession).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Work session started with ID: " + documentReference.getId());
                        callback.onSuccess(documentReference.getId());
                    }).addOnFailureListener(e -> {
                        Log.w(TAG, "Error starting work session", e);
                        callback.onFailure(e);
                    });
                }
            } else {
                Log.w(TAG, "Error checking for existing session", task.getException());
                callback.onFailure(task.getException());
            }
        });
    }

    public void endWorkSession(String userId, String sessionId, Timestamp timestamp) {
        DocumentReference sessionRef = db.collection(Constants.USERS_COLLECTION).document(userId).collection(Constants.SESSIONS_COLLECTION).document(sessionId);

        sessionRef.update("endTime", timestamp).addOnSuccessListener(aVoid -> Log.d(TAG, "Work session ended with ID: " + sessionId)).addOnFailureListener(e -> Log.w(TAG, "Error ending work session", e));
    }

    public void addBreakToSession(String userId, String sessionId, Timestamp breakStart, Timestamp breakEnd) {
        DocumentReference sessionRef = db.collection(Constants.USERS_COLLECTION).document(userId).collection(Constants.SESSIONS_COLLECTION).document(sessionId);

        Map<String, Timestamp> newBreak = new HashMap<>();
        newBreak.put("breakStart", breakStart);
        newBreak.put("breakEnd", breakEnd);

        sessionRef.update("breaks", FieldValue.arrayUnion(newBreak)).addOnSuccessListener(aVoid -> Log.d(TAG, "Break added to session with ID: " + sessionId)).addOnFailureListener(e -> Log.w(TAG, "Error adding break to session", e));
    }

    public void getAllSessions(String userId, FirestoreCallback callback) {
        CollectionReference sessionsRef = db.collection(Constants.USERS_COLLECTION).document(userId).collection(Constants.SESSIONS_COLLECTION);

        sessionsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                callback.onQuerySuccess(querySnapshot); // Pass back sessions
            } else {
                Log.w(TAG, "Error getting sessions", task.getException());
                callback.onFailure(task.getException());
            }
        });
    }

    public void deleteWorkSession(String userId, String sessionId) {
        DocumentReference sessionRef = db.collection(Constants.USERS_COLLECTION).document(userId).collection(Constants.SESSIONS_COLLECTION).document(sessionId);

        sessionRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "Work session deleted with ID: " + sessionId)).addOnFailureListener(e -> Log.w(TAG, "Error deleting work session", e));
    }

    public static void deleteBreak(String userId, String sessionId, int breakIndex, OnDeleteBreakListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users").document(userId).collection("sessions").document(sessionId).get().addOnSuccessListener(document -> {
            List<Map<String, Timestamp>> breaks = (List<Map<String, Timestamp>>) document.get("breaks");

            if (breaks != null && breakIndex < breaks.size()) {
                breaks.remove(breakIndex);
                firestore.collection("users").document(userId).collection("sessions").document(sessionId).update("breaks", breaks).addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                }).addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e);
                });
            }
        });
    }

    public interface OnDeleteBreakListener {
        void onSuccess();

        void onFailure(Exception e);
    }

    public interface FirestoreCallback {
        void onSuccess(String sessionId);

        void onQuerySuccess(QuerySnapshot querySnapshot);

        void onFailure(Exception e);
    }

    public Timestamp getDayStartTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }

    public Timestamp getDayEndTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp.toDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTime());
    }

    public Timestamp createCustomTimestamp(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTime());
    }
}