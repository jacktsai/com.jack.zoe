package com.jack.zoe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.jack.zoe.util.J;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RoaringListener extends NotificationListenerService {

    private Hashtable<String, List<String>> notificationIdMap = new Hashtable<String, List<String>>();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        SharedPreferences preferences = this.getPreferences();
        this.loadTargetPackageNames(preferences);

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
        List<String> idList = this.notificationIdMap.get(sbn.getPackageName());

        if (idList != null) {
            if (idList.isEmpty()) {
                mediaPlayer = MediaPlayer.create(super.getApplicationContext(), R.raw.alarm);
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

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        J.d("Notification '%s' removed", sbn.getPackageName());
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

    private SharedPreferences getPreferences() {
        return this.getSharedPreferences(this.getClass().getName(), Context.MODE_PRIVATE);
    }

    private void loadTargetPackageNames(SharedPreferences preferences) {
        this.notificationIdMap.clear();
        J.d("notificationIdMap cleared");

        int count = preferences.getInt("packageNameCount", 0);
        J.d("package name count is %d", count);

        for (int i = 0; i < count; i++) {
            String packageName = preferences.getString("packageName" + i, null);
            if (packageName != null) {
                this.notificationIdMap.put(packageName, new ArrayList<String>());
                J.d("package name '%s' added", packageName);
            }
        }
    }
}
