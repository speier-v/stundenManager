package com.albsig.stundenmanager.ui.shifts;

import static com.albsig.stundenmanager.R.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.viewmodel.user.UserViewModel;
import com.albsig.stundenmanager.databinding.FragmentShiftsBinding;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.domain.model.session.Shift;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.albsig.stundenmanager.ui.user_overview.UserOverviewFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShiftFragment extends Fragment {

    private static final String TAG = "ShiftsFragment";
    private Context mContext;
    private FragmentShiftsBinding binding;
    private UserViewModel userViewModel;
    private UserModel userModel;
    private RecyclerView recyclerView;
    private ShiftAdapter shiftAdapter;
    private List<Shift> shiftList = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShiftsBinding.inflate(inflater, container, false);

        binding.recyclerShifts.setLayoutManager(new LinearLayoutManager(getContext()));
        shiftAdapter = new ShiftAdapter(shiftList);
        binding.recyclerShifts.setAdapter(shiftAdapter);

        return binding.getRoot();
    }

    private void fetchShifts(String userUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("shifts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Extract shift fields
                    List<DocumentReference> morningShift = (List<DocumentReference>) document.get("morningShift");
                    List<DocumentReference> lateShift = (List<DocumentReference>) document.get("lateShift");
                    List<DocumentReference> nightShift = (List<DocumentReference>) document.get("nightShift");

                    Timestamp startDate = document.getTimestamp("startDate");
                    Timestamp endDate = document.getTimestamp("endDate");

                    // Check if the user is in morning shifts
                    if (morningShift != null && isUserInShift(morningShift, userUid)) {
                        shiftList.add(new Shift(
                                document.getId(),
                                "Morning",
                                startDate.toDate().toString(),
                                endDate.toDate().toString()
                        ));
                    }

                    // Check if the user is in late shifts
                    if (lateShift != null && isUserInShift(lateShift, userUid)) {
                        shiftList.add(new Shift(
                                document.getId(),
                                "Late",
                                startDate.toDate().toString(),
                                endDate.toDate().toString()
                        ));
                    }

                    // Check if the user is in night shifts
                    if (nightShift != null && isUserInShift(nightShift, userUid)) {
                        shiftList.add(new Shift(
                                document.getId(),
                                "Night",
                                startDate.toDate().toString(),
                                endDate.toDate().toString()
                        ));
                    }
                }

                // Notify the adapter of data changes
                shiftAdapter.notifyDataSetChanged();

                Log.d(TAG, ""+shiftAdapter.getItemCount());
            } else {
               Log.d(TAG, "Couldn't fetch shifts");
           }
        });
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

    private Date addHoursToDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar.getTime();
    }

    private Date subtractHoursFromDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -hours);
        return calendar.getTime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initObserver();
        signOut();
        navigateBack();
    }

    private void initObserver() {
        userViewModel.getUserModel().observe(getViewLifecycleOwner(), userModelResult -> {
            if (!userModelResult.isSuccess()) {
                Toast.makeText(mContext, "Login failed - " + userModelResult.getError(), Toast.LENGTH_SHORT).show();
                return;
            }

            userModel = userModelResult.getValue();

            //String userUid = "VKePkgvLOpSbtkO3717h8rXMYTx2"; // Replace with actual UID
            //Log.d(TAG, "Calling with userUid "+userUid);
            fetchShifts(userModel.getUid());
        });
    }

    private void signOut() {
        FloatingActionButton fabSignOut = binding.fabSignOut;

        fabSignOut.setOnClickListener(view -> {
            userViewModel.signOutUser();
            Toast.makeText(this.binding.getRoot().getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            goToLoginFragment();
        });
    }

    private void goToLoginFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, LoginFragment.class, null, Constants.TAG_LOGIN).setReorderingAllowed(true);
        fragmentTransaction.commit();
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBackNav;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(id.fragment_container, UserOverviewFragment.class, null, Constants.TAG_DASHBOARD).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }
}

