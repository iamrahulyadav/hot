package com.hotactress.hot.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import android.Manifest;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.hotactress.hot.MyApplication;
import com.hotactress.hot.R;
import com.hotactress.hot.activities.StartActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by shubhamagrawal on 06/07/18.
 */

public class Gen {

    private static final String TAG = "Gen";

    static String appURL = "For more hot images download the https://play.google.com/store/apps/details?id=com.hotactress.hot";
    static String shareTextMessage = "सुलझाएं हसीनो की पहेलियाँ और मौका पाएं ढेर सरे इनाम जीतने का. अगर आप अकल्मन्द हैं तो जरूर कोशिश करें \n" +
            "\n" +
            "Solve the puzzle of hot chicks and stand a chance to win a prize. Try if you are intelligent\n" +
            "\n" +
            "APP LINK: http://bit.ly/2uaTAE5";

    static String shareAppMessage = "इस app पर बहुत सारी हॉट लड़किया है जिससे आप chat कर सकते है. \n" +
            "मेरी referral link से डाउनलोड करिये ";

    private static final String NOTIFICATION_CHANNEL_ID = "1";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "1" ;

    public static String utmQueryUrl = "?utm_source=hot%20app&utm_medium=webview&utm_campaign=hot%20app";
    public static final List<String> urls = Arrays.asList("https://lolmenow.com", "https://lolmenow.com");
    public static FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MyApplication.getAppContext());

    private static FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();


    public static void sendNotificationToSelf(Activity activity) {

        final NotificationManager notificationManager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        String title = "आपको मैसेज आया है";
        String message = "किसी लड़की ने आपको मैसेज किया है, कृपया रजिस्टर की प्रक्रिया पूरी करें और उस मैसेज का रिप्लाई करे ";

        Intent notificationIntent = new Intent(activity, StartActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationIntent.addFlags(notificationIntent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        final String packageName = activity.getPackageName();
        Uri uri = Uri.parse(
                "android.resource://"
                        + packageName
                        + "/"
                        + R.raw.success_sound
        );


        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_image);
        final NotificationCompat.Builder builder = new  NotificationCompat.Builder(activity, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(activity.getResources().getColor(R.color.colorBlueDark))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(uri);

        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bm));
        notificationManager.notify(0, builder.build());
    }

    // NOTE: this is via server
    public static void sendNotification(String userId, String title, String message) {

        // TODO: if already sent in past one hour, don't send the notification
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("title", title);
        data.put("message", message);

        Task<String> notificationTask = mFunctions
                .getHttpsCallable("notify")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });

        notificationTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                } else {
                    Log.d(TAG, "notification sent successfully");
                }
            }
        });
    }

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
        share.putExtra(Intent.EXTRA_TEXT, shareTextMessage);
        Bundle b = new Bundle();
        if (packageName != null && packageName.length() > 0) {
            share.setPackage(packageName);
            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "whatsapp_share");
        }else{
            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "general_share");
        }
        try {
            firebaseAnalytics.logEvent(Constants.SHARE_ACTIVITY, b);
            Gen.startActivity(Intent.createChooser(share, "Share Image"), false);
        }catch (Exception ex){
            Log.d("dde", ex.getMessage(), ex);
        }
    }

    public static void shareImageWhatsapp(Activity context, String url) {
        shareImage(context, url, "com.whatsapp");
    }

    public static void shareApp(Activity activity) {
        AnalyticsManager.log(AnalyticsManager.Event.SHARE_CLICKED, "", "");

        String invitationLink = Gen.getInviteURLFromLocalStorage();
        String msg = shareAppMessage + invitationLink;

        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_image);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        OutputStream outstream;
        try {
            outstream = activity.getContentResolver().openOutputStream(uri);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, msg);
        Gen.startActivity(Intent.createChooser(share, "Share app"), false);
    }

    public static void downloadImage(final Activity activity, final int imageViewId, final String imageName) {
        final View content = activity.findViewById(imageViewId);
        content.setDrawingCacheEnabled(true);
        final Bitmap b = content.getDrawingCache();
        downloadImage(activity, b, imageName);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean validatePermission(Activity activity){
        return activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                activity.checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED ;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void askPermission(Activity activity){
        activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE}, 10);
    }

    public static int getResId(Activity activity, String resName) {
        return activity.getResources().getIdentifier(resName, "id", activity.getPackageName());
    }

    public static Boolean getBooleanValueFromLocalStorage(String key) {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getBoolean(key, false);
    }


    public static String getStringValueFromLocalStorage(String key) {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getString(key, "");
    }

    public static boolean isUserOpeningAppForTheFirstTime() {
//        return true;
        return !Gen.getBooleanValueFromLocalStorage(Constants.NOT_FIRST_TIME_LAUNCH);
    }

    public static SharedPreferences.Editor getSharedPreferenceEditor() {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        return editor;
    }

    public static void saveAppStateToNotFirstLaunch() {
        SharedPreferences.Editor editor = getSharedPreferenceEditor();

        editor.putBoolean(Constants.NOT_FIRST_TIME_LAUNCH, true);
        editor.commit();
    }

    public static void saveLogOutInLocalStorage() {
        SharedPreferences.Editor editor = getSharedPreferenceEditor();

        editor.putBoolean(Constants.LOGGED_IN, false);
        editor.commit();
    }

    public static void saveLogInInLocalStorage() {
        SharedPreferences.Editor editor = getSharedPreferenceEditor();

        editor.putBoolean(Constants.LOGGED_IN, true);
        editor.commit();
    }

    public static void saveLoggedInUserNameInLocalStorage(String name) {
        SharedPreferences.Editor editor = getSharedPreferenceEditor();

        editor.putString(Constants.LOGGED_IN_USER_NAME, name);
        editor.commit();
    }

    public static boolean isUserLoggedInInLocalStorage() {
        return Gen.getBooleanValueFromLocalStorage(Constants.LOGGED_IN);
    }

    public static String getLogeedInUserFromLocalStorage() {
        return Gen.getStringValueFromLocalStorage(Constants.LOGGED_IN_USER_NAME);
    }



    public static List<Bitmap> splitImage(Bitmap bitmap, int size, int blockSize) {
        int piecesNumber = size * size;

        List<Bitmap> pieces = new ArrayList<>(piecesNumber);

        // Calculate the with and height of the pieces
        int pieceWidth = bitmap.getWidth()/ size;
        int pieceHeight = bitmap.getHeight()/ size;

        // Create each bitmap piece and add it to the resulting array
        int yCoord = 0;
        for (int row = 0; row < size; row++) {
            int xCoord = 0;
            for (int col = 0; col < size; col++) {
                Bitmap b = Bitmap.createBitmap(bitmap, xCoord, yCoord, pieceWidth, pieceHeight);
                b = Bitmap.createScaledBitmap(b, blockSize, blockSize, true);
                pieces.add(b);
                xCoord += pieceWidth;
            }
            yCoord += pieceHeight;
        }

        return pieces;
    }

    public static Bitmap mergeMultipleBitmaps(List<Bitmap> parts){

        int imageBlockSize = (int) (Math.sqrt(parts.size()));
        int imageSize = imageBlockSize * parts.get(0).getWidth();

        Bitmap result = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        for (int i = 0; i < parts.size(); i++) {
            canvas.drawBitmap(parts.get(i), parts.get(i).getWidth() * (i % imageBlockSize), parts.get(i).getHeight() * (i / imageBlockSize), paint);
        }
        return result;
    }

    public static void shareImage(Activity activity, Bitmap bitmap, String packageName){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "title");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values);
        OutputStream outstream;
        try {
            outstream = activity.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
            outstream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, shareTextMessage);
        Bundle b = new Bundle();
        if (packageName != null && packageName.length() > 0) {
            share.setPackage(packageName);
            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "whatsapp_share");
        }else{
            b.putString(FirebaseAnalytics.Param.ITEM_NAME, "general_share");
        }
        try {
            firebaseAnalytics.logEvent(Constants.SHARE_ACTIVITY, b);
            Gen.startActivity(Intent.createChooser(share, "Share Image"), false);
        }catch (Exception ex){
            Log.d("dde", ex.getMessage(), ex);
        }
    }

    public static String downloadImage(final Activity activity, final Bitmap b, final String imageName){
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Download/HotApp");
        if (!file.exists()) file.mkdirs();
        final String uriSting = (file.getAbsolutePath() + "/" + imageName);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "download");
        firebaseAnalytics.logEvent(Constants.SHARE_ACTIVITY, bundle);

        AsyncJob.doInBackground(new AsyncJob.OnBackgroundJob() {
            @Override
            public void doOnBackground() {

                final FileOutputStream foStream;
                try {

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

        return uriSting;
    }

    public static Bitmap getBitmapFromURL(String src) {
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

    public static void saveInviteURLToLocalStorage(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Gen.validatePermission(activity))
                Gen.askPermission(activity);
        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        String link = "https://lolmenow.com/?" + Constants.INVITED_BY + "=" + uid;

        DynamicLink.Builder dynamicLinkBuilder = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDynamicLinkDomain("lolmenow.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.hotactress.hot")
                                .setMinimumVersion(1)
                                .build());


        dynamicLinkBuilder
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {

                        if(task.isSuccessful()) {
                            String inviteUrlForUser = task.getResult().getShortLink().toString();
                            SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(Constants.INVITE_URL_FOR_USER, inviteUrlForUser);
                            editor.commit();
                        } else {
                            Exception e = task.getException();
                            Log.d(TAG, e.getStackTrace().toString());
                            Gen.toast("Error saving invite URL");
                        }
                    }
                });
    }

    public static String getInviteURLFromLocalStorage() {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getString(Constants.INVITE_URL_FOR_USER, "");
    }

    public static void saveInvitedByUserToLocalStorage(String invitedByUserId) {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.INVITED_BY, invitedByUserId);
    }

    public static String getInvitedByUserFromLocalStorage() {
        SharedPreferences settings = MyApplication.getAppContext().getSharedPreferences(Constants.PREFS_NAME, 0);
        return settings.getString(Constants.INVITED_BY, "");
    }

    public static String getRealPathFromURI( Uri contentUri) {
        File file = new File(contentUri.getPath());//create path from uri
        final String[] split = file.getPath().split(":");//split the path.
        return split[1];//assign it to a string(your choice).
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap, int quanlity){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quanlity,stream);
        return stream.toByteArray();
    }
    public static String getYoutubeUrlForId(String videoId) {
        return String.format("https://youtube.com/watch?v=%s", videoId);
    }

    public static String[] getYoutubeCaptionUrls(String videoId){
        String img1 = String.format("https://img.youtube.com/vi/%s/0.jpg", videoId);
        String img2 = String.format("https://img.youtube.com/vi/%s/1.jpg", videoId);
        String img3 = String.format("https://img.youtube.com/vi/%s/2.jpg", videoId);
        String img4 = String.format("https://img.youtube.com/vi/%s/3.jpg", videoId);
        return new String[]{img1, img2, img3, img4};
    }

    public static void logFirebaseEvent(String eventName, String itemName){
        Bundle b = new Bundle();
        b.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MyApplication.getAppContext());
        firebaseAnalytics.logEvent(eventName, b);
    }

    public static File getDownloadDir(){
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Download/HotApp");
        if (!file.exists())
            file.mkdir();
        return file;
    }

}
