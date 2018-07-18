package com.hotactress.hot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.hotactress.hot.R;
import com.hotactress.hot.utils.Gen;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null) {
            Gen.startActivity(this, true, ChatMainActivity.class);
        } else {
            Gen.startActivity(this, true, RegisterActivity.class);
        }
    }
}
