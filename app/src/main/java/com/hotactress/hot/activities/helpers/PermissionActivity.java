package com.hotactress.hot.activities.helpers;

import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ServerValue;
import com.hotactress.hot.utils.FirebaseUtil;

/**
 * Created by shubhamagrawal on 24/12/17.
 */

public class PermissionActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseUtil.getUsersRefForUser(userId).child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            FirebaseUtil.getUsersRefForUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
    }
}
