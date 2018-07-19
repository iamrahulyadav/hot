package com.hotactress.hot.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotactress.hot.R;

import butterknife.Unbinder;

public class VideoSearchFragment extends android.support.v4.app.Fragment {

    Unbinder mUnbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_search, container, false);
        return root;
    }
}
