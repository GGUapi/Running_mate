package com.example.signuploginrealtime.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.signuploginrealtime.HelpClass;
import com.example.signuploginrealtime.UserMainActivity;
import com.example.signuploginrealtime.UserProfile;
import com.example.signuploginrealtime.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    static String LOG_TAG = "ProfileFragmentDebug";
    private FragmentProfileBinding binding;
    TextView profileName, profileEmail, profileUsername, profilePassword;
    TextView titleName, titleUsername;
    Button editButton;
    private static String currentProfileName;
    String oldPassword;

    public ProfileFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        profileName = binding.profileName;
        profileEmail = binding.profileEmail;
        profileUsername = binding.profileUsername;
        profilePassword = binding.profilePassword;
        titleName = binding.titleName;
        titleUsername = binding.titleUsername;
        editButton = binding.editButton;
        editButton.setOnClickListener(this::onClickEditProfile);
        changeEditTextState(false);
        setupDataChangeListener();
        return root;
    }

    private UserMainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserMainActivity) {
            Log.d(LOG_TAG, "set UserMainActivity");
            mainActivity = (UserMainActivity) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void changeEditTextState(boolean enabled_edit) {
        profileName.setEnabled(enabled_edit);
        profileEmail.setEnabled(false);
        profileUsername.setEnabled(enabled_edit);
        profilePassword.setEnabled(enabled_edit);
    }

    private void onClickEditProfile(View view) {
        editButton.setText("Save Profile");
        editButton.setOnClickListener(this::onClickSaveProfile);
        oldPassword = profilePassword.getText().toString();
        changeEditTextState(true);
    }

    private void onClickSaveProfile(View view) {
        mainActivity.showLoading();
        Context context = getActivity().getApplicationContext();
        editButton.setText("Edit Profile");
        editButton.setOnClickListener(this::onClickEditProfile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        String name = profileName.getText().toString();
        String email = profileEmail.getText().toString();
        String username = profileUsername.getText().toString();
        String password = profilePassword.getText().toString();
        UserProfile newProfile = new UserProfile(name, email, username, password);

        if (!oldPassword.equals(password)) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    Log.d(LOG_TAG, e.getMessage());
                                    Toast.makeText(context, "更新密碼失敗", Toast.LENGTH_SHORT).show();
                                    newProfile.setPassword(oldPassword);
                                    profilePassword.setText(oldPassword);
                                }
                                userProfileRef.setValue(newProfile);
                                changeEditTextState(false);
                                mainActivity.hideLoading();
                            }
                        });
                    } else {
                        Toast.makeText(context, "更新密碼失敗", Toast.LENGTH_SHORT).show();
                        newProfile.setPassword(oldPassword);
                        profilePassword.setText(oldPassword);
                        userProfileRef.setValue(newProfile);
                        changeEditTextState(false);
                        mainActivity.hideLoading();
                    }
                }
            });

        } else {
            userProfileRef.setValue(newProfile);
            changeEditTextState(false);
            mainActivity.hideLoading();
        }
    }

    public void setupDataChangeListener() {
        mainActivity.showLoading();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userProfileRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        ValueEventListener dataChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile newProfile = snapshot.getValue(UserProfile.class);
                HelpClass.getInstance().setProfile(newProfile);
                titleName.setText(newProfile.getName());
                titleUsername.setText(newProfile.getUsername());
                profileName.setText(newProfile.getName());
                profileEmail.setText(newProfile.getEmail());
                profileUsername.setText(newProfile.getUsername());
                profilePassword.setText(newProfile.getPassword());
                currentProfileName = titleUsername.getText().toString();
                mainActivity.hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(LOG_TAG, error.getMessage());
            }
        };
        userProfileRef.addValueEventListener(dataChangeListener);
    }



    public static String getCurrentProfileName() {
        return currentProfileName;
    }

    public void onDataChange(@NonNull DataSnapshot snapshot) {
        UserProfile newProfile = snapshot.getValue(UserProfile.class);
        currentProfileName = newProfile.getName();
    }
}