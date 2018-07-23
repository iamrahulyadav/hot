package com.hotactress.hot.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotactress.hot.R;
import com.hotactress.hot.activities.ChatActivity;
import com.hotactress.hot.activities.ChatMainActivity;
import com.hotactress.hot.activities.GridActivity;
import com.hotactress.hot.activities.PuzzleSolvingActivity;
import com.hotactress.hot.activities.VideoMainActivity;
import com.hotactress.hot.utils.Constants;
import com.hotactress.hot.utils.Gen;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivitiesFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.fragment_activities_video_image_id)
    public ImageView videoActivityImage;
    @BindView(R.id.fragment_activities_pics_image_id)
    public ImageView picsActivityImage;
    @BindView(R.id.fragment_activities_puzzle_image_id)
    public ImageView puzzleActivityImage;
    @BindView(R.id.fragment_activities_share_image_id)
    public ImageView shareActivityImage;

    @BindView(R.id.fragment_activities_video_view_id)
    public TextView videoTextView;
    @BindView(R.id.fragment_activities_pics_text_id)
    public TextView picsTextView;
    @BindView(R.id.fragment_activities_puzzle_text_id)
    public TextView puzzleTextView;
    @BindView(R.id.fragment_activities_share_view_id)
    public TextView shareTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_activities, container, false);
        ButterKnife.bind(this, root);
        videoTextView.setOnClickListener(this);
        videoActivityImage.setOnClickListener(this);
        picsActivityImage.setOnClickListener(this);
        picsTextView.setOnClickListener(this);
        puzzleActivityImage.setOnClickListener(this);
        puzzleTextView.setOnClickListener(this);
        shareTextView.setOnClickListener(this);
        shareActivityImage.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == videoActivityImage.getId() || v.getId() == videoTextView.getId())
            Gen.startActivity(getActivity(), false, VideoMainActivity.class);
        else if (v.getId() == picsActivityImage.getId() || v.getId() == picsTextView.getId())
            Gen.startActivity(getActivity(), false, GridActivity.class);
        else if (v.getId() == puzzleActivityImage.getId() || v.getId() == puzzleTextView.getId())
            Gen.startActivity(getActivity(), false, PuzzleSolvingActivity.class);
        else if (v.getId() == shareActivityImage.getId() || v.getId() == shareTextView.getId()) {
            Gen.logFirebaseEvent(Constants.SHARE_ACTIVITY, "cash_prize_clicked");
            Gen.shareApp(getActivity());
        }
    }
}
