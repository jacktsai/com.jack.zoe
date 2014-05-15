package com.jack.zoe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.jack.zoe.util.J;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RoaringListener extends NotificationListenerService {

    private RoaringPreferences preferences;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        this.preferences = RoaringPreferences.createInstance(this);

        Context context = this.getApplicationContext();
        context.startService(new Intent(context, MadHeadTosObserver.class));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Context context = this.getApplicationContext();
        context.stopService(new Intent(context, MadHeadTosObserver.class));

        this.preferences.save();

        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        J.d("Notification '%s' posted", sbn.getPackageName());

        if (preferences.enabled) {
            List<String> idList = preferences.notificationIdMap.get(sbn.getPackageName());

            if (idList != null) {
                if (idList.isEmpty()) {
                    mediaPlayer = MediaPlayer.create(super.getApplicationContext(), preferences.ringtone);
                    mediaPlayer.setVolume(1, 1);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }

                String id = Integer.toString(sbn.getId());
                if (!idList.contains(id)) {
                    idList.add(id);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        J.d("Notification '%s' removed", sbn.getPackageName());

        if (preferences.enabled) {
            List<String> idList = preferences.notificationIdMap.get(sbn.getPackageName());

            if (idList != null && !idList.isEmpty()) {
                String id = Integer.toString(sbn.getId());

                if (idList.remove(id) && idList.isEmpty()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        }
    }
}
