package com.hotactress.hot.utils;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.hotactress.hot.MyApplication;


/**
 * Created by shubhamagrawal on 19/04/17.
 */

public class AnalyticsManager {
    public interface Event{
        String ERROR = "Error";
        String ACTIVITY_LAUNCHED = "activity launched";
        String REGISTER_ACTIVITY_LAUNCHED = "REGISTER_ACTIVITY_LAUNCHED";
        String INTRO_SLIDER_ACTIVITY_LAUNCHED = "INTRO_SLIDER_ACTIVITY_LAUNCHED";
        String CHAT_MAIN_ACTIVITY_LAUNCHED = "CHAT_MAIN_ACTIVITY_LAUNCHED";
        String LOGIN_ACTIVITY_LAUNCHED = "LOGIN_ACTIVITY_LAUNCHED";
        String CHAT_ACTIVITY_LAUNCHED = "CHAT_ACTIVITY_LAUNCHED";
        String GRID_ACTIVITY_LAUNCHED = "GRID_ACTIVITY_LAUNCHED";
        String SHARE_CLICKED = "SHARE_CLICKED";
        String SHARE_BUTTON_CHAT_CLICKED = "SHARE_BUTTON_CHAT_CLICKED";
    }

    interface  Param{
        String STACK_TRACE = "Stack trace";
        String ERROR_BODY = "Error body";
    }

    public static FirebaseAnalytics getInstance(){
        return FirebaseAnalytics.getInstance(MyApplication.getAppContext());
    }

    public static void log(String eventName, Bundle extraInfo){
        getInstance().logEvent(eventName, extraInfo);
    }

    public static void log(String eventName, String key, String val){
        Bundle bundle = new Bundle();
        bundle.putString(key, val);
        log(eventName, bundle);
    }
}
