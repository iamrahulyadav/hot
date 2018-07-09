package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hotactress.hot.R;
import com.hotactress.hot.models.Profile;
import com.hotactress.hot.utils.Gen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class ImageViewPagerAdapter extends PagerAdapter {

    Activity activity;
    ArrayList<Profile> images;
    LayoutInflater inflater;

    public ImageViewPagerAdapter(Activity activity, ArrayList<Profile> images) {
        this.activity = activity;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_pager_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        ImageView share = itemView.findViewById(R.id.share);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gen.shareImage(activity, images.get(position).getImage());
            }
        });
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);

        int height = dis.heightPixels;
        int width = dis.widthPixels;

        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);

        try {

            Picasso.get()
                    .load(images.get(position).getImage() + "&type=HQ")
                    .into(imageView);

        } catch (Exception ex) {

        }
        container.addView(itemView);
        return itemView;
    }
}
