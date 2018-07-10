package com.hotactress.hot;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hotactress.hot.activities.GridActivity;

import java.util.Map;

/**
 * Created by shubhamagrawal on 07/04/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    // https://firebase.google.com/docs/notifications/android/console-device#access_the_registration_token

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("message"));
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Map<String, String> data = remoteMessage.getData();
        String message=null, activity=null;

        String url = (String) data.get("url");

//        for(Map.Entry entry: data.entrySet()) {
//            String key = entry.getKey().toString();
//            if(key.equals(Constants.UPDATE_ORDER_NOTIFICATION)) {
//                Gen.playNotificationSound();
//                Gen.triggerOrderRefresh();
//                return;
//            } else if(key.equals(Constants.MESSAGE)) {
//                message = entry.getValue().toString();
//            } else if(key.equals(Constants.ACTIVITY)) {
//                activity = entry.getValue().toString();
//            }
//        }


        if(notification != null && notification.getBody() != null && notification.getTitle()!=null){
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(getResources().getColor(R.color.colorBlueDark))
                    .setContentTitle(notification.getTitle())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(notification.getBody()))
                    .setContentText(notification.getBody())
                    .setAutoCancel(true)
                    .setSound(uri);

            Intent notificationIntent;
            if(url != null) {
                notificationIntent = new Intent(Intent.ACTION_VIEW);

                notificationIntent.setData(Uri.parse(url));
            } else {
                notificationIntent = new Intent(this, GridActivity.class);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }

            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
