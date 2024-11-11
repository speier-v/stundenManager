package com.albsig.stundenmanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.ui.dashboard.DashboardFragment;
import com.albsig.stundenmanager.databinding.ActivityMainBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    ActivityMainBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initLogin();
    }

    private void initLogin() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN)
                .setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onLoginSuccess(String userId) {
        this.userId = userId;

        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        dashboardFragment.setArguments(args);
        loadFragment(dashboardFragment);
    }

/*
    private void test() {
        MaterialTextView tv = binding.hello;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("test").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            tv.setText((CharSequence) d.get("testStr"));
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
*/
}