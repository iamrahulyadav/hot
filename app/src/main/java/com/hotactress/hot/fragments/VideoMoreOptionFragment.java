package com.hotactress.hot.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;


import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.R;
import com.hotactress.hot.models.Format;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.SerializationUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoMoreOptionFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.fragment_video_more_option_image_view_id)
    public ImageView imageView;
    @BindView(R.id.fragment_video_more_option_title_view_id)
    public TextView titleTextView;
    @BindView(R.id.fragment_video_more_option_spinner_view_id)
    public Spinner spinner;
    @BindView(R.id.fragment_video_more_option_play_button_id)
    public Button playButton;
    @BindView(R.id.fragment_video_more_option_download_button_id)
    public Button downloadButton;
    @BindView(R.id.fragment_video_more_option_progress_bar_id)
    public ProgressBar progressBar;


    private Video video;
    private List<Format> formatList;
    private Integer option;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_more_option, container, false);
        ButterKnife.bind(this, root);
        readBundle(getArguments());
        setupLayout(root);
        return root;
    }

    private void setupLayout(View root) {
        Picasso.get()
                .load(video.getThumbnailUrls()[0])
                .error(R.drawable.picasso_placeholder)
                .into(imageView);

        titleTextView.setText(video.getTitle());

        // set spinner
        List<String> spinnerList = new ArrayList<>();
        for (Format format : formatList)
            spinnerList.add(format.displayString());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerList.toArray(new String[spinnerList.size()]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        playButton.setOnClickListener(this);
        downloadButton.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);

    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            video = SerializationUtils.deserialize(bundle.getByteArray("video_serialized"));
            formatList = SerializationUtils.deserialize(bundle.getByteArray("formats_serialized"));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == downloadButton.getId()) {
            Gen.logFirebaseEvent(Constants.VIDEO_PLAYED_ACTIVITY, video.getUrl());

            if (progressBar.getVisibility() == View.GONE)
                progressBar.setVisibility(View.VISIBLE);
            AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
                @Override
                public void doOnBackground() {
                    InputStream input = null;
                    OutputStream output = null;
                    int option = spinner.getSelectedItemPosition();
                    Format format = formatList.get(option);
                    String downloadFileName = String.format("%s.%s", video.getDownloadbleFilename(), format.getExt());
                    final String downloadFilePath = Gen.getDownloadDir().getAbsolutePath() + "/" + downloadFileName;
                    URL url = null;
                    try {
                        url = new URL(format.getUrl());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        final long fileLength = connection.getContentLength();


                        input = connection.getInputStream();
                        output = new FileOutputStream(downloadFilePath);
                        byte data[] = new byte[40960];
                        long total = 0;
                        int count;
                        while ((count = input.read(data)) != -1) {

                            total += count;
                            final int progress = (int) (total * 100 / fileLength);
                            if (fileLength > 0) {
                                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                                    @Override
                                    public void doInUIThread() {

                                        progressBar.setProgress(progress);
                                    }
                                });

                            }
                            output.write(data, 0, count);
                        }
                        AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                            @Override
                            public void doInUIThread() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    final Uri contentUri = Uri.parse("file://" + downloadFilePath);
                                    scanIntent.setData(contentUri);
                                    getActivity().sendBroadcast(scanIntent);
                                } else {
                                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + downloadFilePath));
                                    getActivity().sendBroadcast(intent);
                                }
                                Gen.toastLong("Video is successfully downloaded");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else if (v.getId() == playButton.getId()) {
            int option = spinner.getSelectedItemPosition();
            ((VideoOptionPressed) getActivity()).onVideoPlayPressed(video, formatList.get(option));
        }
    }


    public interface VideoOptionPressed {
        public void onVideoPlayPressed(Video video, Format format);

        public void onVideoDownloadPressed(Video video, Format format);
    }


}
