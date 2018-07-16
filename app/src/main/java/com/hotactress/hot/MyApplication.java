package com.hotactress.hot;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hotactress.hot.utils.Constants;

/**
 * Created by shubhamagrawal on 04/04/17.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    private static final String TAG = MyApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "fcm token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC);
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        Context context = instance.getApplicationContext();
        return context;
    }
}
