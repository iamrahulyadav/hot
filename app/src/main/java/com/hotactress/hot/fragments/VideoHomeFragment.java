package com.hotactress.hot.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.R;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.recylerviews.VideoMainActivityRecyclerView;
import com.hotactress.hot.utils.Gen;

import java.util.List;

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

    private void setUpLayout(View v){
        ButterKnife.bind(this, v);
        mMainPageRecyclerView.setHasFixedSize(true);
        mMainPageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Gen.showLoader(getActivity());
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                mainPageDataList = Video.getData();
                AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                    @Override
                    public void doInUIThread() {
                        Gen.hideLoader(getActivity());
                        mMainPageRecyclerView.setAdapter(new VideoMainActivityRecyclerView(mainPageDataList, getActivity()));
                    }
                });
            }
        });

    }

    public interface VideoSelected{
        void videoSelected(Video category);
    }

}
