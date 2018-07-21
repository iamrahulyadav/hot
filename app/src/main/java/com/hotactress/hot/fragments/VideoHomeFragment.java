package com.hotactress.hot.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.hotactress.hot.R;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.recylerviews.VideoMainActivityRecyclerView;
import com.hotactress.hot.utils.FirebaseUtil;
import com.hotactress.hot.utils.Gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoHomeFragment extends Fragment {
    @BindView(R.id.parent_recycler_view)
    RecyclerView mMainPageRecyclerView;

    public List<Video> mainPageDataList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_home, container, false);
        setUpLayout(root);
        return root;
    }

    private void setUpLayout(View v) {
        ButterKnife.bind(this, v);
        mMainPageRecyclerView.setHasFixedSize(true);
        mMainPageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Gen.showLoader(getActivity());
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("videos");
//        reference.setValue("videos");
        FirebaseUtil.getVideosDataRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gen.hideLoader(getActivity());

                Map<String, List> map = (Map<String, List>) dataSnapshot.getValue();
                Map<String, List<Video>> videoMap = new HashMap<>();

                for (Map.Entry<String, List> entry : map.entrySet()) {
                    String category = entry.getKey();
                    List<Video> videoList = new ArrayList<>();
                    for (Object o : entry.getValue()) {
                        Map<String, String> mv = (Map<String, String>) o;
                        Video video = new Video(mv.get("videoId"), mv.get("title"), category);
                        videoList.add(video);
                    }
                    videoMap.put(category, videoList);
                }
                mMainPageRecyclerView.setAdapter(new VideoMainActivityRecyclerView(videoMap, getActivity()));
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Gen.hideLoader(getActivity());
            }
        });


    }

    public interface VideoSelected {
        void videoSelected(Video category);
    }

}
