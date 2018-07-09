package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotactress.hot.R;
import com.hotactress.hot.activities.GridActivity;
import com.hotactress.hot.activities.MainActivity;
import com.hotactress.hot.models.Profile;
import com.hotactress.hot.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.hotactress.hot.utils.Gen;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class GridAdapter extends BaseAdapter {
    private Activity activity;
    ArrayList<Profile> images;
    LayoutInflater inflater;
    String nextLaunchType;

    public GridAdapter(Activity activity, ArrayList<Profile> images, String nextLaunchType) {
        this.activity = activity;
        this.images = images;
        this.nextLaunchType = nextLaunchType;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        TextView textView;
        View itemView = view;

        if (itemView == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.grid_view_item, viewGroup, false);
        }

        imageView = itemView.findViewById(R.id.image_view_in_grid);
        textView = itemView.findViewById(R.id.text_view_in_grid);
        Profile profile = images.get(i);

        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);

        int height = dis.heightPixels;
        int width = dis.widthPixels;

        final String aid = profile.getAid();
        final String titleId = profile.getTitleid();

        imageView.setMinimumWidth(width/2);
        imageView.setMinimumHeight(width*3/4);

        if(!nextLaunchType.equals("slider")) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (nextLaunchType.equals("actresses")) {
                        Intent intent = new Intent(activity, GridActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY1, "actresses");
                        bundle.putString(Constants.KEY2, aid);
                        bundle.putString(Constants.KEY3, "actressJSON");
                        intent.putExtras(bundle);
                        Gen.startActivity(intent, false);
                    } else if (nextLaunchType.equals("group")) {
                        Intent intent = new Intent(activity, GridActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY1, "group");
                        bundle.putString(Constants.KEY2, aid);
                        bundle.putString(Constants.KEY3, titleId);
                        intent.putExtras(bundle);
                        Gen.startActivity(intent, false);
                    }
                }
            });
        }


        if(nextLaunchType.equals("slider")) {

        }
        if(!TextUtils.isEmpty(profile.getName()))
            textView.setText(profile.getName());
        else {
            textView.setText(profile.getTitle());
        }

        if(!TextUtils.isEmpty(profile.getImage())) {
            Picasso.get()
                    .load(images.get(i).getImage())
                    .into(imageView);
        }
        return itemView;
    }
}
