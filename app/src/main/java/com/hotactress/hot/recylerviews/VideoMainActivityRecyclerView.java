package com.hotactress.hot.recylerviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.hotactress.hot.R;
import com.hotactress.hot.fragments.VideoHomeFragment;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Gen;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoMainActivityRecyclerView extends RecyclerView.Adapter<VideoMainActivityRecyclerView.RecyclerviewHolder> {


    List<Video> dataArray;
    Activity activity;

    public VideoMainActivityRecyclerView(List<Video> data, Activity activity) {
        this.dataArray = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_vertical_recyclerview_cell, parent, false);
        return new RecyclerviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, final int position) {
        final Activity activity = this.activity;
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.childRecyclerView.setAdapter(new VideoMainPageHorizontalRecyclerView(dataArray, activity));
        holder.textView.setText(dataArray.get(position).getCategory());

        holder.viewAllTextView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VideoHomeFragment.VideoSelected) activity).videoSelected(dataArray.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataArray.size();
    }

    public class RecyclerviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.video_vertical_cell_recyler_view_cell_id)
        RecyclerView childRecyclerView;

        @BindView(R.id.video_vertical_cell_title_view_id)
        TextView textView;

        @BindView(R.id.video_vertical_cell_viewall_view_id)
        TextView viewAllTextView;

        RecyclerviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
