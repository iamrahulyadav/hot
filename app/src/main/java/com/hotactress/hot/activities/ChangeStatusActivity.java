package com.hotactress.hot.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.adapters.UserListAdapter;
import com.hotactress.hot.models.UserProfile;
import com.hotactress.hot.utils.Gen;

public class ChangeStatusActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mUpdate;
    private static final String TAG = ChangeStatusActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.change_status_include);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus = findViewById(R.id.change_status_status_text);
        mUpdate = findViewById(R.id.change_status_update);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = currentUser.getUid();


        final DatabaseReference myRef = database.getReference("users").child(uid).child("status");

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.setValue( mStatus.getText().toString());
                mStatus.setText("");
                Gen.toast("Status successfully updated!!");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
