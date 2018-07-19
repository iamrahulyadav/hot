package com.hotactress.hot.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hotactress.hot.R;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.Gen;

import org.w3c.dom.Text;

/**
 * Created by shubhamagrawal on 16/07/18.
 */

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    Button login;
    Activity activity;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        activity = this;

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login);

        if(mAuth.getCurrentUser() != null) {
            Gen.startActivity(this, true, ChatMainActivity.class);
        }

        findViewById(R.id.login_register_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.startActivity(activity, false, RegisterActivity.class);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.showLoader(activity);
                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();


                if(!(TextUtils.isEmpty(emailString) || TextUtils.isEmpty(passwordString)))
                    loginUser(emailString, passwordString);
            }
        });
    }

    private void loginUser(String emailString, String passwordString) {
        mAuth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Gen.hideLoader(activity);
                if(task.isSuccessful()) {
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    FirebaseUtil.getUsersRefForUser(current_user_id).child("deviceToken").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Gen.startActivity(activity, true, ChatMainActivity.class);
                        }
                    });

                } else {
                    Gen.toast("Something went wrong while login the user");
                }
            }
        });

    }
}
