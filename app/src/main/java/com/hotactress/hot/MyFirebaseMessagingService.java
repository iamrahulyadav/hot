package com.hotactress.hot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hotactress.hot.activities.StartActivity;
import com.hotactress.hot.utils.VolleySingelton;

import java.util.Map;

/**
 * Created by shubhamagrawal on 07/04/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID = "1";
    private static final CharSequence NOTIFICATION_CHANNEL_NAME = "1" ;

    // https://firebase.google.com/docs/notifications/android/console-device#access_the_registration_token

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));

        final NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Map<String, String> data = remoteMessage.getData();

        if(data.size() > 0) {
            String title, message, imageUrl, url, fromUserId;

            title = data.get("title");
            message = data.get("message");
            imageUrl = data.get("imageUrl");
            url = data.get("url");

            Intent notificationIntent;
            if(url != null) {
                notificationIntent = new Intent(Intent.ACTION_VIEW);

                notificationIntent.setData(Uri.parse(url));
            } else {
                notificationIntent = new Intent(this, StartActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }

            notificationIntent.addFlags(notificationIntent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

            final String packageName = getPackageName();
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri uri = Uri.parse(
                    "android.resource://"
                            + getPackageName()
                            + "/"
                            + R.raw.success_sound
            );



            final NotificationCompat.Builder builder = new  NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(getResources().getColor(R.color.colorBlueDark))
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(uri);

            if(imageUrl != null) {

                ImageRequest imageRequest = new ImageRequest(imageUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(response));
                        notificationManager.notify(0, builder.build());
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                VolleySingelton.getInstance().getRequestQueue().add(imageRequest);
            } else {
                notificationManager.notify(0, builder.build());
            }

        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
