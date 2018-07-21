package com.hotactress.hot.recylerviews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hotactress.hot.R;
import com.hotactress.hot.fragments.VideoHomeFragment;
import com.hotactress.hot.models.Video;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoMainPageHorizontalRecyclerView extends RecyclerView.Adapter<VideoMainPageHorizontalRecyclerView.RecyclerviewHolder> {

    private List<Video> mData;
    private Activity activity;

    VideoMainPageHorizontalRecyclerView(List<Video> data, Activity activity) {
        mData = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_horizontal_recyclerview_cell, parent, false);
        return new VideoMainPageHorizontalRecyclerView.RecyclerviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerviewHolder holder, final int position) {
        holder.video = mData.get(position);

        final Video story = holder.video;
        Picasso.get()
                .load(holder.video.getThumbnailUrls()[0])
                .error(R.drawable.picasso_placeholder)
                .placeholder(R.drawable.picasso_placeholder)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((VideoHomeFragment.VideoSelected) activity).videoSelected(mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class RecyclerviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.horizontal_cell_image_view_id)
        ImageView imageView;

        private Video video;

        RecyclerviewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
