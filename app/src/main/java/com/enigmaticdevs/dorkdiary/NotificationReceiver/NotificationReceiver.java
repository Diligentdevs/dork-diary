package com.enigmaticdevs.dorkdiary.NotificationReceiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.enigmaticdevs.dorkdiary.Account.SplashActivity;
import com.enigmaticdevs.dorkdiary.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        intent=new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelId")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Dork Diary")
                .setContentText("Anything Dorky you wanna write about?")
                .setSmallIcon(R.drawable.create)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context,R.color.colorAccent));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());
    }
}
