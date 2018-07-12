package com.hotactress.hot.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.adapters.GridAdapter;
import com.hotactress.hot.adapters.ImageViewPagerAdapter;
import com.hotactress.hot.models.Profile;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;
import com.hotactress.hot.utils.WebViewClientImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "GridActivity";

    private WebView webView;
    List<String> urls;

    GridAdapter gridAdapter;
    ArrayList<Profile> profilesData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // NOTE: this is the start activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.containsKey(Constants.URL)) {
                String url = extras.getString(Constants.URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        }


        final Activity activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        String key1 = "popular";
        String key2 = "popularJSON";
        String key3 = "";

        Intent startingIntent = getIntent();

        if(startingIntent!=null && startingIntent.getStringExtra(Constants.KEY1)!=null && startingIntent.getStringExtra(Constants.KEY2)!=null && startingIntent.getStringExtra(Constants.KEY3)!=null) {
            key1 = startingIntent.getStringExtra(Constants.KEY1);
            key2 = startingIntent.getStringExtra(Constants.KEY2);
            key3 = startingIntent.getStringExtra(Constants.KEY3);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(key1).child(key2);
        DatabaseReference urlRef = database.getReference("urls");

        // TODO: Hack just because the data is saved in firebase in that way and I'm too lazy to run the data population script again :(
        if(key1.equals("actresses")) {
            myRef = database.getReference(key1).child("actressesJSON").child(key2);
        } else if(key1.equals("group")) {
            myRef = database.getReference("title").child("titleJSON").child(key2).child(key3);
        }

        // setup web view
        webView = findViewById(R.id.grid_web_view);
        setupWebView(webView);

        Gen.showLoader(activity);

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


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                List<Profile> profiles = new ArrayList<>();
                Gen.hideLoader(activity);
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    Profile profile = child.getValue(Profile.class);
                    profiles.add(profile);
                    i++;
                }

                profilesData.removeAll(profilesData);

                if(i>100) {
                    List<Profile> first100 = profiles.subList(0, 100);
                    Collections.shuffle(first100);
                    List<Profile> after100 = profiles.subList(101, i-1);

                    profilesData.addAll(first100);
                    profilesData.addAll(after100);
                } else {
                    profilesData.addAll(profiles);
                }

                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gridview);


        if(key1.equals("popular")) {
            // that means it is the first page
            gridAdapter = new GridAdapter(this, profilesData, "actresses");
        } else if(key1.equals("actresses")) {
            // that means it is the first page
            gridAdapter = new GridAdapter(this, profilesData, "group");
        } else {
            gridAdapter = new GridAdapter(this, profilesData, "slider");
            final String finalKey2 = key2;
            final String finalKey3 = key3;

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.AID, finalKey2);
                    bundle.putString(Constants.TITLEID, finalKey3);
                    bundle.putString(Constants.KEY1, i+"");
                    intent.putExtras(bundle);
                    Gen.startActivity(intent, false);
                }
            });
        }
        gridview.setAdapter(gridAdapter);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                     Gen.toast("Permission Granted!");
                } else {
                     Gen.toast("Permission Denied!");
                    // re-request permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                break;
        }
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
