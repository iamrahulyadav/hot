package com.hotactress.hot.activities.helpers;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.hotactress.hot.R;


/**
 * Created by shubhamagrawal on 24/12/17.
 */

public class TranslucentAppCompatActivity extends AppCompatActivity {

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    @Override
    public void setContentView(int layoutResID) {
        setStatusBarTranslucent(true);
        super.setContentView(layoutResID);
    }

}
