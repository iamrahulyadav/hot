package com.hotactress.hot.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.hotactress.hot.MyApplication;

import java.io.File;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class Gen {

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

    public static void shareImage(Activity context, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, url);
        context.startActivity(intent );
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
}
