package com.hotactress.hot.activities;
import com.appsee.Appsee;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.hotactress.hot.R;
import com.hotactress.hot.fragments.ChatsFragment;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        Appsee.start();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d(TAG, "getInvitation: no data");
                            return;
                        }

                        // Get the deep link
                        Uri deepLink = data.getLink();
                        // get the inviteBy id from deepLink
                        String invitedByUserId = deepLink.getQueryParameter(Constants.INVITED_BY);

                        // check the current user id and invitedByUserId in the database and update the count
                        Gen.saveInvitedByUserToLocalStorage(invitedByUserId);

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(Gen.isUserOpeningAppForTheFirstTime()) {
            Gen.startActivity(this, true, IntroSliderActivity.class);
        }
        else if(Gen.isUserLoggedInInLocalStorage()) {
            Gen.startActivity(this, true, ChatMainActivity.class);
        } else {
            Gen.startActivity(this, true, RegisterActivity.class);
        }
    }
}
