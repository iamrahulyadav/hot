package com.hotactress.hot.activities;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hotactress.hot.R;
import com.hotactress.hot.models.UserProfile;
import com.hotactress.hot.utils.AnalyticsManager;
import com.hotactress.hot.utils.Gen;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText displayName, email, password;
    Button signup;
    Activity activity;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        activity = this;
        AnalyticsManager.log(AnalyticsManager.Event.REGISTER_ACTIVITY_LAUNCHED, "", "");


        displayName = findViewById(R.id.register_display_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        signup = findViewById(R.id.register_sign_up);

        if(mAuth.getCurrentUser() != null) {
            Gen.startActivity(this, true, ChatMainActivity.class);
        }

        findViewById(R.id.register_login_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(activity, false, LoginActivity.class);
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.showLoader(activity);
                String displayNameString = displayName.getText().toString();
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();

                if(!(TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString)))
                    registerUser(displayNameString, emailString, passwordString);
            }
        });
    }

    private void registerUser(final String displayNameString, final String emailString, String passwordString) {
        mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Gen.hideLoader(activity);
                if(task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("users").child(uid);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    UserProfile userProfile = new UserProfile();
                    userProfile.setId(uid);
                    userProfile.setName(displayNameString);
                    userProfile.setEmail(emailString);
                    userProfile.setStatus("Hi there! I'm using Hot");
                    userProfile.setThumbImage("default");
                    userProfile.setImage("default");
                    userProfile.setDeviceToken(deviceToken);

                    Gen.saveInviteURLToLocalStorage(activity);

                    myRef.setValue(userProfile);

                    String invitedByUserId = Gen.getInvitedByUserFromLocalStorage();


                    Gen.startActivity(activity, true, ChatMainActivity.class);
                } else {
                    Gen.toast("Something went wrong while registering the user");
                }
            }
        });

    }
}
