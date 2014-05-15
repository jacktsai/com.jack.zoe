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

    private boolean enabled;
    private Uri ringtone;
    private Hashtable<String, List<String>> notificationIdMap = new Hashtable<String, List<String>>();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        SharedPreferences preferences = this.getPreferences();
        this.loadPreferences(preferences);

        Context context = this.getApplicationContext();
        context.startService(new Intent(context, MadHeadTosObserver.class));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Context context = this.getApplicationContext();
        context.stopService(new Intent(context, MadHeadTosObserver.class));

        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        J.d("Notification '%s' posted", sbn.getPackageName());

        if (this.enabled) {
            List<String> idList = this.notificationIdMap.get(sbn.getPackageName());

            if (idList != null) {
                if (idList.isEmpty()) {
                    mediaPlayer = MediaPlayer.create(super.getApplicationContext(), this.ringtone);
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

        if (this.enabled) {
            List<String> idList = this.notificationIdMap.get(sbn.getPackageName());

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

    private SharedPreferences getPreferences() {
        return this.getSharedPreferences(this.getClass().getName(), Context.MODE_PRIVATE);
    }

    private void loadPreferences(SharedPreferences preferences) {
        this.enabled = preferences.getBoolean("enabled", true);
        this.ringtone = Uri.parse(preferences.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));

        this.notificationIdMap.clear();
        J.d("notificationIdMap cleared");
        for (int i = 0; ; i++) {
            String packageName = preferences.getString("packageName" + i, null);
            if (packageName != null) {
                this.notificationIdMap.put(packageName, new ArrayList<String>());
                J.d("package name '%s' added", packageName);
            } else {
                if (i == 0) {
                    this.notificationIdMap.put("com.jack.notifier", new ArrayList<String>());
                    this.notificationIdMap.put("com.madhead.tos.zh", new ArrayList<String>());
                }

                break;
            }
        }
    }
}
