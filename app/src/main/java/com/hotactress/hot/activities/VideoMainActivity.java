package com.hotactress.hot.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hotactress.hot.Config;
import com.hotactress.hot.R;
import com.hotactress.hot.fragments.VideoMoreOptionFragment;
import com.hotactress.hot.fragments.VideoHomeFragment;
import com.hotactress.hot.fragments.VideoSearchFragment;
import com.hotactress.hot.models.Format;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;
import com.hotactress.hot.utils.VolleySingelton;

import org.apache.commons.lang3.SerializationUtils;
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
        VideoHomeFragment.VideoSelected, VideoMoreOptionFragment.VideoOptionPressed {


    @Nullable
    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;


//    @BindView(R.id.bottom_navigation_view)
//    BottomNavigationView mBottomNavigationView;

    private Unbinder mUnBinder;
    public static int SEARCH_FRAGMENT_TAG = 10000;
    public static int MORE_OPTION_FRAGMENT_TAG = 2000;
    public static int HOME_FRAGMENT_TAG = 30000;
    public static int STORY_LISTING_FRAGMENT_TAG = 40000;
    public AlertDialog.Builder builder;
    public VideoHomeFragment videoHomeFragment;
    public VideoMoreOptionFragment videoMoreOptionFragment;
    public VideoSearchFragment videoSearchFragment;
    public List<Format> videoFormatList;

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
//        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // add Home frag activity by default
        videoHomeFragment = new VideoHomeFragment();
        videoMoreOptionFragment = new VideoMoreOptionFragment();
        videoSearchFragment = new VideoSearchFragment();
        addFragmentOnHomeActivity(videoHomeFragment, HOME_FRAGMENT_TAG, false);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a format");


    }

    /**
     * Method to add fragment on home view container
     *
     * @param fragment : frag class
     * @param tag      : tag of frag defined in Home class
     */
    private void addFragmentOnHomeActivity(Fragment fragment, int tag, boolean addToBackstack) {
        android.support.v4.app.Fragment fragExists = getSupportFragmentManager().findFragmentByTag(String.valueOf(tag));
        if (fragExists == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (addToBackstack)
                transaction.addToBackStack(String.valueOf(tag));
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

//    /**
//     * Method invoked as item listener for bottom navigation view items
//     *
//     * @param item : item clecked
//     */
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.bottom_video_navigation_home:
//                addFragmentOnHomeActivity(videoHomeFragment, HOME_FRAGMENT_TAG);
//                break;
//            case R.id.bottom_video_navigation_search:
//                addFragmentOnHomeActivity(videoSearchFragment, SEARCH_FRAGMENT_TAG);
//                break;
//            case R.id.bottom_video_navigation_downloads:
//                addFragmentOnHomeActivity(videoMoreOptionFragment, DOWNLOAD_FRAGMENT_TAG);
//                break;
//            default:
//                break;
//        }
//        return false;
//    }

    public void startVideoPlay(Format format) {
        String type = "";
        if (format.getHeight() > 0) {
            type = "video/mp4";
            Intent playVideo = new Intent(Intent.ACTION_VIEW);
            playVideo.setDataAndType(Uri.parse(format.getUrl()), type);
            startActivity(playVideo);
        }
    }

    public static void extractVideoQualities(final VideoMainActivity activity, final Video video) {
        Gen.showLoader(activity);
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("video_url", video.getUrl());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Config.VIDEO_META_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Gen.hideLoader(activity);
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        List<Format> formatList = Format.createFormatForYoutube(jsonArray);

                        Log.d("", formatList.size() + "");
                        activity.videoFormatList = new ArrayList<>();
                        for (Format format : formatList) {
                            if (format.isValidVideo())
                                activity.videoFormatList.add(format);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("video_serialized", SerializationUtils.serialize(video));
                        bundle.putByteArray("formats_serialized", SerializationUtils.serialize((ArrayList) activity.videoFormatList));
                        activity.videoMoreOptionFragment.setArguments(bundle);
                        activity.addFragmentOnHomeActivity(activity.videoMoreOptionFragment, MORE_OPTION_FRAGMENT_TAG, true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Gen.toast("Sorry this video cannot be played");
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Gen.hideLoader(activity);
                    Log.e("", error.getMessage(), error);
                    Gen.toast("Sorry this video cannot be played");

                }
            });
            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
            Gen.toast("Sorry this video cannot be played");

        }
    }

    @Override
    public void videoSelected(Video video) {
        Gen.logFirebaseEvent(Constants.VIDEO_SELECTED_ACTIVITY, video.getUrl());
        extractVideoQualities(this, video);
    }


    @Override
    public void onVideoPlayPressed(Video video, Format format) {
        Gen.logFirebaseEvent(Constants.VIDEO_PLAYED_ACTIVITY, video.getUrl());
        startVideoPlay(format);
    }

    @Override
    public void onVideoDownloadPressed(Video video, Format format) {

    }
}
