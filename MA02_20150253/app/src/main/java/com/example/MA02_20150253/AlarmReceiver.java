package com.example.MA02_20150253;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;


public class AlarmReceiver extends BroadcastReceiver {

    static final int NOTI = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notifier = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(context, MyCalendarActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
        Notification notification =  new Notification.Builder(context).setTicker("일정을 알려드립니다")
                .setContentTitle("일정을 잊으신건 아니죠??")
                .setContentText("캘린더를 확인하세요!")
                .setSmallIcon(R.drawable.chick_round)
                .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.chick_round))
                .setContentIntent(pendingIntent)
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notifier.notify(NOTI,notification);



    }
}
