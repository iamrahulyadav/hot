package com.hotactress.hot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.adapters.ImageViewPagerAdapter;
import com.hotactress.hot.models.Profile;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;
import com.hotactress.hot.utils.WebViewClientImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ViewPager viewPager;
    ImageViewPagerAdapter imageViewPagerAdapter;

    ArrayList<Profile> profilesData = new ArrayList<>();
    List<String> urls = new ArrayList<>();
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent startingIntent = getIntent();
        String titleId = startingIntent.getStringExtra(Constants.TITLEID);
        String aid = startingIntent.getStringExtra(Constants.AID);
        final String index = startingIntent.getStringExtra(Constants.KEY1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("title").child("titleJSON").child(aid).child(titleId);
        DatabaseReference urlRef = database.getReference("urls");

        viewPager = findViewById(R.id.viewPager);
        imageViewPagerAdapter = new ImageViewPagerAdapter(this, profilesData);

        viewPager.setAdapter(imageViewPagerAdapter);

        // setup web view
        webView = findViewById(R.id.main_web_view);
        setupWebView(webView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Gen.validatePermission(this))
                Gen.askPermission(this);
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Profile> profiles = new ArrayList<>();

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Profile profile = child.getValue(Profile.class);
                    profiles.add(profile);
                }

                profilesData.removeAll(profilesData);
                profilesData.addAll(profiles);
                imageViewPagerAdapter.notifyDataSetChanged();
                viewPager.setCurrentItem(Integer.parseInt(index));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        urlRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                urls = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String url = child.getValue(String.class);
                    urls.add(url);
                }
                loadUrlAsync(webView, urls);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void loadUrlAsync(WebView webView, List<String> urls){
        Collections.shuffle(urls);
        String url = urls.get(0) + Gen.utmQueryUrl;
        webView.loadUrl(url);
    }

    private void setupWebView(WebView webView){

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getPath());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webView.setWebViewClient(webViewClient);
    }
}
