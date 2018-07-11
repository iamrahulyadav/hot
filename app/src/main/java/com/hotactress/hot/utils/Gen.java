package com.hotactress.hot.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.hotactress.hot.MyApplication;
import com.hotactress.hot.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class Gen {

    static String appURL = "https://play.google.com/store/apps/details?id=com.hotactress.hot";

    public static void startActivity(Intent intent, boolean clearStack) {
        if (clearStack) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        MyApplication.getAppContext().startActivity(intent);
    }

    public static void startActivity(Activity source, boolean clearStack, Class<?> destination) {
        Intent intent = new Intent(MyApplication.getAppContext(), destination);
        startActivity(intent, clearStack);
    }

    public static void startActivity(Activity source, boolean clearStack, Class<?> destination, String key, String value) {
        Intent intent = new Intent(MyApplication.getAppContext(), destination);
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        intent.putExtras(bundle);
        startActivity(intent, clearStack);
    }

    public static void shareImage(Activity context, String url, String packageName) {
        View content = context.findViewById(R.id.imageView);
        content.setDrawingCacheEnabled(true);
        Bitmap icon = content.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);


        OutputStream outstream;
        try {
            outstream = context.getContentResolver().openOutputStream(uri);
            icon.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, appURL);
        if (packageName != null && packageName.length() > 0)
            share.setPackage(packageName);
        MyApplication.getAppContext().startActivity(Intent.createChooser(share, "Share Image"));
    }

    public static void shareImageWhatsapp(Activity context, String url) {
        shareImage(context, url, "com.whatsapp");
    }

    public static void downloadImage(final Activity activity, final int imageViewId, final String imageName) {
        final View content = activity.findViewById(imageViewId);
        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {
                content.setDrawingCacheEnabled(true);
                Bitmap b = content.getDrawingCache();
                final FileOutputStream foStream;
                try {
                    File file = new File(Environment.getExternalStorageDirectory().getPath(), "Download/HotApp");
                    if (!file.exists())  file.mkdirs();

                    final String uriSting = (file.getAbsolutePath() + "/" + imageName);

                    foStream = new FileOutputStream(uriSting);
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, bytearrayoutputstream);
                    foStream.write(bytearrayoutputstream.toByteArray());
                    foStream.close();
                    AsyncJob.doOnMainThread(new AsyncJob.OnMainThreadJob() {
                        @Override
                        public void doInUIThread() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                final Uri contentUri = Uri.parse("file://" + uriSting);
                                scanIntent.setData(contentUri);
                                activity.sendBroadcast(scanIntent);
                            } else {
                                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + uriSting));
                                activity.sendBroadcast(intent);
                            }
                            Gen.toastLong("Image is successfully downloaded");
                        }
                    });
                } catch (Exception e) {
                    Log.d("saveImage", "Exception 2, Something went wrong!");
                    e.printStackTrace();
                }
            }
        });

    }

    public static void toast(String text) {
        Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String text) {
        Toast.makeText(MyApplication.getAppContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void downloadFile(Activity activity, String uRl) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/hot");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("/hot", "fileName.jpg");

        mgr.enqueue(request);

    }

    public static void showLoader(Activity activity) {
        ViewGroup view = (ViewGroup) activity.getWindow().getDecorView().getRootView();

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View loader = activity.findViewById(R.id.loading_indicator);
        if (loader == null) {
            assert inflater != null;
            inflater.inflate(R.layout.loading_indicator, view, true);
            loader = activity.findViewById(R.id.loading_indicator);
        }
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loader.setVisibility(View.VISIBLE);
    }

    public static void hideLoader(Activity activity) {
        View loader = activity.findViewById(R.id.loading_indicator);
        if (loader != null) {
            loader.setVisibility(View.GONE);
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
