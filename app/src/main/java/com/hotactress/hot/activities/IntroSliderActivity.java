package com.hotactress.hot.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotactress.hot.R;
import com.hotactress.hot.activities.helpers.TranslucentAppCompatActivity;
import com.hotactress.hot.adapters.IntroSliderAdapter;
import com.hotactress.hot.utils.Gen;


/**
 * Created by shubhamagrawal on 24/12/17.
 */

public class IntroSliderActivity extends TranslucentAppCompatActivity {

    private static final String TAG = IntroSliderActivity.class.getSimpleName();

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private IntroSliderAdapter introSliderAdapter;

    private TextView[] mDots;
    private Button mBackButton, mNextButton;
    private int mCurrentPage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        final Activity activity = this;

        mSlideViewPager = findViewById(R.id.slide_view_pager);
        mDotLayout = findViewById(R.id.dots_layout);

        introSliderAdapter = new IntroSliderAdapter(this, activity);
        mSlideViewPager.setAdapter(introSliderAdapter);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
        mBackButton = findViewById(R.id.intro_slider_back_button);
        mNextButton = findViewById(R.id.intro_slider_next_button);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPage == mDots.length - 1) {
                    Gen.saveAppStateToNotFirstLaunch();
                    Gen.startActivity(activity, true, RegisterActivity.class);
                }
                mSlideViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    public void addDotsIndicator(Integer position ) {
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i=0; i<4; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mCurrentPage = position;

            if(mCurrentPage == 0) {
                mNextButton.setEnabled(true);
                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.GONE);

                mNextButton.setText("Next");
                mBackButton.setText("");
            } else if (mCurrentPage == mDots.length - 1) {
                // mNextButton.setEnabled(false);
                mBackButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);

                mNextButton.setText("Finish");
                mBackButton.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
