package com.hotactress.hot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hotactress.hot.R;
import com.hotactress.hot.utils.Gen;

/**
 * Created by shubhamagrawal on 16/07/18.
 */

public class ChatMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_main);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.chat_main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hot Chat");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Gen.startActivity(this, true, RegisterActivity.class);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.chat_logout){

        } else if(item.getItemId() == R.id.all_users) {

        } else if(item.getItemId() == R.id.account_settings) {

        }

        return true;
    }
}
