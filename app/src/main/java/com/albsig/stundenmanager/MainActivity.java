package com.albsig.stundenmanager;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModelFactory;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModel;
import com.albsig.stundenmanager.common.viewmodel.session.SessionViewModelFactory;
import com.albsig.stundenmanager.common.viewmodel.shift.ShiftViewModel;
import com.albsig.stundenmanager.common.viewmodel.shift.ShiftViewModelFactory;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModelFactory;
import com.albsig.stundenmanager.data.remote.AdminRepositoryImpl;
import com.albsig.stundenmanager.data.remote.SessionRepositoryImpl;
import com.albsig.stundenmanager.data.remote.ShiftRepositoryImpl;
import com.albsig.stundenmanager.data.remote.UserRepositoryImpl;
import com.albsig.stundenmanager.domain.repository.AdminRepository;
import com.albsig.stundenmanager.domain.repository.SessionRepository;
import com.albsig.stundenmanager.domain.repository.ShiftRepository;
import com.albsig.stundenmanager.domain.repository.UserRepository;
import com.albsig.stundenmanager.databinding.ActivityMainBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.albsig.stundenmanager.ui.login.LoginListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoginListener {

    ActivityMainBinding binding;
    private static final String CHANNEL_ID = "notifications_channel";
    private FirebaseFirestore firestore;
    private ListenerRegistration listenerRegistration;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initSharedViewModel();
        initLogin();
        firestore = FirebaseFirestore.getInstance();
        requestNotificationPermission();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted.");
                createNotificationChannel();
            } else {
                Log.d("MainActivity", "Notification permission denied.");
            }
        }
    }

    private void initSharedViewModel() {
        UserRepository userRepository = new UserRepositoryImpl(this);
        UserViewModelFactory userViewModelFactory = new UserViewModelFactory(userRepository);
        new ViewModelProvider(this, userViewModelFactory).get(UserViewModel.class);

        AdminRepository adminRepository = new AdminRepositoryImpl(this);
        AdminViewModelFactory adminViewModelFactory = new AdminViewModelFactory(adminRepository, userRepository);
        new ViewModelProvider(this, adminViewModelFactory).get(AdminViewModel.class);

        SessionRepository sessionRepository = new SessionRepositoryImpl();
        SessionViewModelFactory sessionViewModelFactory = new SessionViewModelFactory(sessionRepository);
        new ViewModelProvider(this, sessionViewModelFactory).get(SessionViewModel.class);

        ShiftRepository shiftRepository = new ShiftRepositoryImpl();
        ShiftViewModelFactory shiftViewModelFactory = new ShiftViewModelFactory(shiftRepository);
        new ViewModelProvider(this, shiftViewModelFactory).get(ShiftViewModel.class);
    }

    private void initLogin() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN).setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("hi", "Starting Notification Channel");
            CharSequence name = "App Notifications";
            String description = "Notifications for app updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void startFirestoreListener(String userId) {
        Log.d("hi", "Starting Firestore Listener");
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }

        firestore.collection("shifts")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }

                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        Log.d("hi", querySnapshot.toString());
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            List<String> morningShift = (List<String>) document.get("morningShift");
                            List<String> lateShift = (List<String>) document.get("lateShift");
                            List<String> nightShift = (List<String>) document.get("nightShift");

                            Date start = document.getTimestamp("startDate").toDate();
                            Date end = document.getTimestamp("endDate").toDate();
                            Date now = new Date();

                            if (now.after(start) && now.before(end)) {
                                if (morningShift != null && isUserInShift(morningShift, userId)) {
                                    sendNotificationForShift("Morning", start);
                                } else if (lateShift != null && isUserInShift(lateShift, userId)) {
                                    sendNotificationForShift("Late", start);
                                } else if (nightShift != null && isUserInShift(nightShift, userId)) {
                                    sendNotificationForShift("Night", start);
                                }
                            }
                        }
                    }
                });
    }

    private boolean isUserInShift(List<String> shiftList, String userUid) {
        if (shiftList == null) return false;

        for (String userPath : shiftList) {
            if (userPath != null && userPath.startsWith("/users/")) {
                String userId = userPath.substring("/users/".length());
                if (userId.equals(userUid)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void sendNotificationForShift(String shiftType, Date shiftStartTime) {
        Log.d("hi", "Sending Notification For Shift");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(shiftStartTime);

        if ("Morning".equals(shiftType)) {
            calendar.add(Calendar.HOUR_OF_DAY, 0); // Morning starts at shiftStartTime
        } else if ("Late".equals(shiftType)) {
            calendar.add(Calendar.HOUR_OF_DAY, 8); // Late starts 8 hours after shiftStartTime
        } else if ("Night".equals(shiftType)) {
            calendar.add(Calendar.HOUR_OF_DAY, 16); // Night starts 16 hours after shiftStartTime
        }

        Date adjustedStartTime = calendar.getTime();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm, dd.mm.yyyy", Locale.getDefault());
        String notificationTime = timeFormat.format(adjustedStartTime);

        sendNotification("Shift Alert", "Your " + shiftType + " shift starts at " + notificationTime + ".");
    }

    private void sendNotification(String title, String content) {
        Log.d("hi", "send Notification");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission not granted.");
                return; // If permission is not granted, do not send the notification
            }
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onLoginSuccess(String userId) {
        startFirestoreListener(userId);
    }
}

