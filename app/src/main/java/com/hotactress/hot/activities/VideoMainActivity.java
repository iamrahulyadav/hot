package com.hotactress.hot.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.Config;
import com.hotactress.hot.R;
import com.hotactress.hot.fragments.VideoDownloadFragment;
import com.hotactress.hot.fragments.VideoHomeFragment;
import com.hotactress.hot.fragments.VideoSearchFragment;
import com.hotactress.hot.models.Format;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Gen;
import com.hotactress.hot.utils.VolleySingelton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoMainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        VideoHomeFragment.VideoSelected {


    @Nullable
    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;


    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    private Unbinder mUnBinder;
    public static int SEARCH_FRAGMENT_TAG = 10000;
    public static int DOWNLOAD_FRAGMENT_TAG = 2000;
    public static int HOME_FRAGMENT_TAG = 30000;
    public static int STORY_LISTING_FRAGMENT_TAG = 40000;
    public AlertDialog.Builder builder;
    public VideoHomeFragment videoHomeFragment;
    public VideoDownloadFragment videoDownloadFragment;
    public VideoSearchFragment videoSearchFragment;
    public final HashMap<Integer, Format> formatHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video);
        setUpLauyout();
    }

    @Override
    protected void onDestroy() {
        mUnBinder.unbind();
        super.onDestroy();
    }

    private void setUpLauyout() {
        mUnBinder = ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // add Home frag activity by default
        videoHomeFragment = new VideoHomeFragment();
        videoDownloadFragment = new VideoDownloadFragment();
        videoSearchFragment = new VideoSearchFragment();
        addFragmentOnHomeActivity(videoHomeFragment, HOME_FRAGMENT_TAG);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a format");


    }

    /**
     * Method to add fragment on home view container
     *
     * @param fragment : frag class
     * @param tag      : tag of frag defined in Home class
     */
    private void addFragmentOnHomeActivity(Fragment fragment, int tag) {
        android.support.v4.app.Fragment fragExists = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        if (fragExists == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.addToBackStack(String.valueOf(tag));
            transaction.replace(R.id.home_frag_container, fragment, String.valueOf(tag));
            transaction.commit();
        }
    }

    /**
     * Method to remove all fragments from container
     */
    private void removeAllFragFromActivity() {
        FragmentManager fm = getSupportFragmentManager();

        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }

    }

    /**
     * Method invoked as item listener for bottom navigation view items
     *
     * @param item : item clecked
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_video_navigation_home:
                addFragmentOnHomeActivity(videoHomeFragment, HOME_FRAGMENT_TAG);
                break;
            case R.id.bottom_video_navigation_search:
                addFragmentOnHomeActivity(videoSearchFragment, SEARCH_FRAGMENT_TAG);
                break;
            case R.id.bottom_video_navigation_downloads:
                addFragmentOnHomeActivity(videoDownloadFragment, DOWNLOAD_FRAGMENT_TAG);
                break;
            default:
                break;
        }
        return false;
    }

    public void startVideoPlay(Format format) {
        String type = "";
        if (format.getHeight() > 0) {
            type = "video/mp4";
            Intent playVideo = new Intent(Intent.ACTION_VIEW);
            playVideo.setDataAndType(Uri.parse(format.getUrl()), type);
            startActivity(playVideo);
        }
    }

    public static void extractVideoQualities(final VideoMainActivity activity, final String youtubeUrl) {
        Gen.showLoader(activity);
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("video_url", youtubeUrl);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.VIDEO_META_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gen.hideLoader(activity);
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        List<Format> formatList = Format.createFormatForYoutube(jsonArray);

                        Log.d("", formatList.size() + "");
                        List<String> selectVideoFormatOptions = new ArrayList<>();
                        for (int i = 0, j = 0; i < formatList.size(); i++) {
                            if (formatList.get(i).isValidVideo()) {
                                selectVideoFormatOptions.add(formatList.get(i).displayString());
                                activity.formatHashMap.put(j, formatList.get(i));
                                j++;
                            }
                        }
                        CharSequence[] options = selectVideoFormatOptions.toArray(new CharSequence[selectVideoFormatOptions.size()]);
                        activity.builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Format format = activity.formatHashMap.get(which);
                                activity.startVideoPlay(format);
                            }
                        });
                        activity.builder.show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Gen.hideLoader(activity);
                    Log.e("", error.getMessage(), error);
                }
            });
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void videoSelected(Video video) {
        extractVideoQualities(this, video.youtubeUrl());
    }
}
