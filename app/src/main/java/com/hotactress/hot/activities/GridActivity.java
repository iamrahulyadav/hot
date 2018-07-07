package com.hotactress.hot.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class GridActivity extends AppCompatActivity {

    private static final String TAG = "GridActivity";

    GridAdapter gridAdapter;
    ArrayList<Profile> profilesData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        String key1 = "popular";
        String key2 = "popularJSON";

        Intent startingIntent = getIntent();

        if(startingIntent!=null && startingIntent.getStringExtra(Constants.KEY1)!=null && startingIntent.getStringExtra(Constants.KEY2)!=null) {
            key1 = startingIntent.getStringExtra(Constants.KEY1);
            key2 = startingIntent.getStringExtra(Constants.KEY2);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(key1).child(key2);

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
        } else {
            gridAdapter = new GridAdapter(this, profilesData, "slider");
        }
        gridview.setAdapter(gridAdapter);
    }
}
