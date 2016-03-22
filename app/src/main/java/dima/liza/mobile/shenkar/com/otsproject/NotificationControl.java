package dima.liza.mobile.shenkar.com.otsproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Girya on 09/03/2016.
 */
public  class NotificationControl {
    public static void notificationNow(String title,String text,int drawable,int id, Context context,PendingIntent intent){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(drawable);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        if(intent!=null) {
            mBuilder.setContentIntent(intent);
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(id, mBuilder.build());

    }
}