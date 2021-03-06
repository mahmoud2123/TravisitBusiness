package com.travisit.travisitbusiness.vvm.destination;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.travisit.travisitbusiness.databinding.FragmentAuthenticationBinding;
import com.travisit.travisitbusiness.databinding.FragmentSplashBinding;
import com.travisit.travisitbusiness.model.Business;
import com.travisit.travisitbusiness.utils.SharedPrefManager;
import com.travisit.travisitbusiness.vvm.AppActivity;
import com.travisit.travisitbusiness.vvm.vm.AuthenticationVM;
import com.travisit.travisitbusiness.R;

public class AuthenticationFragment extends Fragment {
    private AuthenticationVM vm;
    private FragmentAuthenticationBinding binding;
    public SharedPrefManager preferences;
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            if (getFieldText("email").length() == 0 ||
                    getFieldText("password").length() == 0 ){
                binding.fAuthBtnSignIn.setEnabled(false);
            } else {
                binding.fAuthBtnSignIn.setEnabled(true);
            }
        }
    };

    public static AuthenticationFragment newInstance() {
        return new AuthenticationFragment();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppActivity)getActivity()).changeBottomNavVisibility(View.GONE);
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = new SharedPrefManager(getActivity());
        vm = ViewModelProviders.of(this).get(AuthenticationVM.class);
        String gotToSignUpText = getResources().getString(R.string.go_to_sign_up);
        binding.fAuthTvGoToSignup.setText(Html.fromHtml(gotToSignUpText));
        handleUserInteractions(view);
    }
    private void handleUserInteractions(final View view) {
        binding.fAuthTvGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_from_auth_to_reg);
            }
        });
        binding.fAuthTvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_from_auth_to_forgot_password);
            }
        });
        binding.fAuthBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.signInBusiness(
                        getFieldText("email"),
                        getFieldText("password")
                );
                vm.businessMutableLiveData.observe(getActivity(), new Observer<Business>() {
                    @Override
                    public void onChanged(Business business) {
                        preferences.saveUser(business);
                        //TODO: CHECK USER STATUS TO NAVIGATE
                        Navigation.findNavController(view).navigate(R.id.action_from_auth_to_home);
                        //Navigation.findNavController(view).navigate(R.id.action_from_auth_to_complete_profile);

                    }
                });
            }
        });
        binding.fAuthTietEmailAddress.addTextChangedListener(watcher);
        binding.fAuthTietPassword.addTextChangedListener(watcher);

    }
    private String getFieldText(String fieldName){
        switch (fieldName){
            case "email": return binding.fAuthTietEmailAddress.getText().toString();
            case "password": return binding.fAuthTietPassword.getText().toString();
            default: return "invalid";
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
