package com.hotactress.hot.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hotactress.hot.MyApplication;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class FirebaseUtil {

    public static DatabaseReference getMessagesRefForUser(String userId) {
        return FirebaseDatabase.getInstance().getReference("messages").child(userId);
    }

    public static DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public static DatabaseReference getUsersRefForUser(String userId) {
        return getUsersRef().child(userId);
    }


}
