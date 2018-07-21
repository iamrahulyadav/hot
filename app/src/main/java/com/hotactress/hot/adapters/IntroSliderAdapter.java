package com.hotactress.hot.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotactress.hot.MyApplication;
import com.hotactress.hot.R;


/**
 * Created by shubhamagrawal on 24/12/17.
 */

public class IntroSliderAdapter extends PagerAdapter {

    Context context;
    Activity activity;
    LayoutInflater layoutInflater;

    public IntroSliderAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public int[] slide_images = {
            R.mipmap.hot_chat,
            R.mipmap.hot_photo,
            R.mipmap.hot_video,
            R.mipmap.hot_puzzle,
    };

    public String[] slide_headings = {
            "Hot Chat",
            "Hot Photos",
            "Hot Videos",
            "Hot Puzzle"
    };

    public String[] slide_descs = {
            "Chat with super hot girls and guys around you.",
            "View photos of super hot actresses and desi girls.",
            "Watch steamy videos of super hot chicks, actresses and much more.",
            "Solve puzzles and win exciting date with hot chicks around you."
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = view.findViewById(R.id.slide_layout_icon);
        TextView slideHeading = view.findViewById(R.id.slide_layout_heading);
        TextView slideDescription = view.findViewById(R.id.slide_layout_description);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
