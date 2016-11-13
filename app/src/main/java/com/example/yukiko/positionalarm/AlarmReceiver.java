package com.example.yukiko.positionalarm;

/**
 * Created by yukiko on 16/11/05.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent receivedIntent) {
        int notificationId = receivedIntent.getIntExtra("notificationId", 0);
        NotificationManager myNotification = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent bootIntent = new Intent(context, MapsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, bootIntent, 0);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("ここはあの場所…！")
                .setContentText(receivedIntent.getCharSequenceExtra("todo"))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);

        myNotification.notify(notificationId, builder.build());
    }
}
