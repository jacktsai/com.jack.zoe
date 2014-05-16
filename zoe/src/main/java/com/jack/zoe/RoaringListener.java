package com.jack.zoe;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.jack.zoe.util.J;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class RoaringListener extends NotificationListenerService {

    private static final String TAG = RoaringListener.class.getSimpleName();

    private boolean enabled;
    private String alert;
    private int volume;
    private Hashtable<String, List<String>> notificationIdMap = new Hashtable<String, List<String>>();
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        J.d(TAG, "onCreate");
        Settings.Roaring settings = Settings.getInstance(this).roaring;
        this.enabled = settings.get_enabled();
        this.alert = settings.get_alert();
        this.volume = settings.get_volume();
        this.setPackageNames(settings.get_package_names());
        settings.setOnChangeListener(new Settings.Roaring.OnChangeListener() {
            @Override
            public void onEnabledChange(boolean enabled) {
                RoaringListener.this.enabled = enabled;
            }

            @Override
            public void onAlertChange(String alert) {
                RoaringListener.this.alert = alert;
            }

            @Override
            public void onVolumeChange(int volume) {
                RoaringListener.this.volume = volume;
            }

            @Override
            public void onPackageNamesChange(Set<String> package_names) {
                setPackageNames(package_names);
            }
        });

        Context context = this.getApplicationContext();
        context.startService(new Intent(context, MadHeadTosObserver.class));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        J.d(TAG, "onDestroy");
        Context context = this.getApplicationContext();
        context.stopService(new Intent(context, MadHeadTosObserver.class));

        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        J.d(TAG, "Notification '%s' posted", sbn.getPackageName());

        if (this.enabled) {
            synchronized (this) {
                List<String> idList = notificationIdMap.get(sbn.getPackageName());

                if (idList != null) {
                    if (idList.isEmpty()) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(this.getApplicationContext(), Uri.parse(this.alert));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setVolume(1, 1);
                        mediaPlayer.start();

                        AudioManager audioManager = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, this.volume, 0);
                    }

                    String id = Integer.toString(sbn.getId());
                    if (!idList.contains(id)) {
                        idList.add(id);
                    }
                } else {
                    J.d(TAG, "Notification '%s' is ignored", sbn.getPackageName());
                }
            }
        } else {
            J.d(TAG, "Roaring is not enabled");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        J.d(TAG, "Notification '%s' removed", sbn.getPackageName());

        synchronized (this) {
            List<String> idList = notificationIdMap.get(sbn.getPackageName());

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

    private void setPackageNames(Set<String> packageNames) {
        synchronized (this) {
            notificationIdMap.clear();
            for (String packageName : packageNames) {
                notificationIdMap.put(packageName, new ArrayList<String>());
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}
