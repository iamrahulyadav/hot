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
import android.util.SparseArray;
import android.view.MenuItem;

import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.R;
import com.hotactress.hot.fragments.VideoDownloadFragment;
import com.hotactress.hot.fragments.VideoHomeFragment;
import com.hotactress.hot.fragments.VideoSearchFragment;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoMainActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
VideoHomeFragment.VideoSelected{


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
    public List<String> videoDownloadOptions;
    public AlertDialog.Builder builder;
    public VideoHomeFragment videoHomeFragment;
    public VideoDownloadFragment videoDownloadFragment;
    public VideoSearchFragment videoSearchFragment;



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

//    @Override
//    public void sendCategorySelected(String category) {
//        addFragmentOnHomeActivity(new StoryListFragment(), STORY_LISTING_FRAGMENT_TAG);
//        ((TextView)findViewById(R.id.custom_toolbar_text_view_id)).setText(category);
//    }

    public static String showReadableVideoFormat(Format format) {
        // 720p 3gp
        if (format.getHeight() > 0 &&
                (format.getExt().equals("mp4") || format.getExt().equals("3gp"))
                && format.getAudioBitrate() > 0) {
            return String.format("%sp  %s", format.getHeight(), format.getExt());
        } else {
            return null;
        }
    }

    public void startVideoPlay(String url) {
        Intent playVideo = new Intent(Intent.ACTION_VIEW);
        playVideo.setDataAndType(Uri.parse(url), "video/mp4");
        startActivity(playVideo);
    }

    public static void extractVideoQualities(final VideoMainActivity activity, final String youtubeUrl) {
        Gen.showLoader(activity);
        new YouTubeExtractor(activity) {
            @Override
            public void onExtractionComplete(final SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                activity.videoDownloadOptions = new ArrayList<>();
                final HashMap<Integer, Integer> map = new HashMap<>();
                for (int i = 0, j=0; ytFiles!=null && i < ytFiles.size(); i++) {
                    int key = ytFiles.keyAt(i);
                    Format format = ytFiles.get(key).getFormat();
                    String readbleFormat = showReadableVideoFormat(format);
                    if (readbleFormat != null) {
                        activity.videoDownloadOptions.add(readbleFormat);
                        map.put(j, i);
                        j++;
                    }
                }

                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        Gen.hideLoader(activity);
                        if (activity.videoDownloadOptions.size()>0) {
                            CharSequence[] options = activity.videoDownloadOptions.toArray(new CharSequence[activity.videoDownloadOptions.size()]);
                            activity.builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int index = map.get(which);
                                    YtFile ytFile = ytFiles.valueAt(index);
                                    activity.startVideoPlay(ytFile.getUrl());
                                }
                            });
                            activity.builder.show();
                        }else {
                            Gen.toast(String.format("Issue with %s", youtubeUrl));
                        }
                    }
                });
            }


        }.extract(youtubeUrl, true, true);

    }

    @Override
    public void videoSelected(Video video) {
        extractVideoQualities(this, video.youtubeUrl() );
    }
}
