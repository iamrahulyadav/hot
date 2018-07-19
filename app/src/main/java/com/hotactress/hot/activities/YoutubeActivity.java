package com.hotactress.hot.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.R;
import com.hotactress.hot.utils.Gen;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.Format;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class YoutubeActivity extends AppCompatActivity implements View.OnClickListener {

    public Button downLoadButton;
    public String currentVideoId;
    public String videoId = "OOQlCD5qMPY";
    List<String> videoDownloadOptions = new ArrayList<>();
    public AlertDialog.Builder builder;
    public VideoView videoView;
    public MediaController mediacontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a format");

//        youTubeView = findViewById(R.id.youtube_activity_youtube_player_view_id);
//        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        downLoadButton = findViewById(R.id.youtube_activity_download_button_id);
        videoView = findViewById(R.id.youtube_activity_video_view_id);
        downLoadButton.setOnClickListener(this);

        mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(videoView);
        videoView.setMediaController(mediacontroller);

    }


    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.youtube_activity_download_button_id) {
            String url = Gen.getYoutubeUrlForId(videoId);
            extractVideoQualities(this, url);

        }
    }

    public static void extractVideoQualities(final YoutubeActivity activity, final String youtubeUrl) {
        new YouTubeExtractor(activity) {
            @Override
            public void onExtractionComplete(final SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                activity.videoDownloadOptions = new ArrayList<>();
                for (int i = 0; i < ytFiles.size(); i++) {
                    int key = ytFiles.keyAt(i);
                    Format format = ytFiles.get(key).getFormat();
                    String readbleFormat = showReadableVideoFormat(format);
                    if (readbleFormat != null)
                        activity.videoDownloadOptions.add(readbleFormat);

                }

                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        CharSequence[] options = activity.videoDownloadOptions.toArray(new CharSequence[activity.videoDownloadOptions.size()]);
                        activity.builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                YtFile ytFile = ytFiles.valueAt(which);
                                activity.startVideoPlay(ytFile.getUrl());
                            }
                        });
                        activity.builder.show();
                    }
                });
            }
        }.extract(youtubeUrl, true, true);

    }

    public void startVideoPlay(String url) {
        Intent playVideo = new Intent(Intent.ACTION_VIEW);
        playVideo.setDataAndType(Uri.parse(url), "video/mp4");
        startActivity(playVideo);
    }

    public static String showReadableVideoFormat(Format format) {
        // 720p 3gp
        if (format.getHeight() > 0 && (format.getExt().equals("mp4") || format.getExt().equals("3gp"))) {
            return String.format("%sp  %s", format.getHeight(), format.getExt());
        } else {
            return null;
        }
    }
}
