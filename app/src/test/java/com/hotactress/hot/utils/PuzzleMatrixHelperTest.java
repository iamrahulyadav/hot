package com.hotactress.hot.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

public class PuzzleMatrixHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void move() {
        String url = "https://scontent.fdel6-1.fna.fbcdn.net/v/t1.0-9/37058824_1182422758816297_821840703599411200_o.jpg?_nc_cat=0&oh=162a0ac2aa23fae828d0b0a9e3f4d203&oe=5BDCB4C4";
        Bitmap bitmap = getBitmapFromURL(url);
        bitmap = trimBitmapFromBottom(bitmap);
        saveBitmapToLocation(bitmap, "/User/ansal/Downloads/sadcasm.jpeg");

    }

    private static Bitmap trimBitmapFromBottom(Bitmap imageBitmap) {
        try {

            int imageWidth = imageBitmap.getWidth();
            int imageHeight = imageBitmap.getHeight();
            int sizeToTrim = 200;
            Bitmap result = Bitmap.createBitmap(imageBitmap, 0, 0, imageWidth, imageHeight - sizeToTrim);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveBitmapToLocation(Bitmap bitmap, String dest){
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e("trivia", e.getMessage(), e);
            return null;
        }
    }

}