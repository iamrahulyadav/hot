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
import com.hotactress.hot.utils.AnalyticsManager;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.Gen;

/**
 * Created by shubhamagrawal on 16/07/18.
 */

public class MoreAppsActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        activity = this;

        findViewById(R.id.wavely_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.openWebPage("https://play.google.com/store/apps/details?id=com.lolmenow.freesongsdownload", activity);
            }
        });

        findViewById(R.id.lolmenow_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.openWebPage("https://play.google.com/store/apps/details?id=com.lolmenow.lolmenews.app", activity);
            }
        });

        findViewById(R.id.laughing_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.openWebPage("https://play.google.com/store/apps/details?id=com.lolmenow.laughingcolors", activity);
            }
        });
    }
}
