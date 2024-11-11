package com.albsig.stundenmanager.ui.registration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.databinding.FragmentRegistrationBinding;
import com.albsig.stundenmanager.ui.login.LoginFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.platform.MaterialSharedAxis;
public class RegistrationFragment extends Fragment {


   @Nullable private FragmentRegistrationBinding binding;
   private FragmentTransaction fragmentTransaction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialSharedAxis forward = new MaterialSharedAxis(MaterialSharedAxis.Z, true);
        setReenterTransition(forward);

        MaterialSharedAxis backward = new MaterialSharedAxis(MaterialSharedAxis.Z, false);
        setReenterTransition(backward);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false );
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBtnRegistration();
        initBtnGoToLogin();

    }

    private void initBtnRegistration() {
        MaterialButton btnRegistration = binding.btnRegistration;
        btnRegistration.setOnClickListener( view1 -> {
        });
    }

    private void initBtnGoToLogin() {
        assert binding != null;
        MaterialButton btnGoToLogin = binding.btnGoToLogin;
        btnGoToLogin.setOnClickListener( view1 -> goToLogin() );
    }

    private void goToLogin() {
        LoginFragment newLoginFragment = new LoginFragment();

        fragmentTransaction = getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newLoginFragment, Constants.TAG_LOGIN);
        fragmentTransaction.commit();
    }
}
