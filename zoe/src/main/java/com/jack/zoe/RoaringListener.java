package com.jack.zoe;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.jack.zoe.util.J;

import java.io.IOException;
import java.util.List;

public class RoaringListener extends NotificationListenerService {

    private static final String TAG = RoaringListener.class.getSimpleName();

    private RoaringPreferences preferences;
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        J.d(TAG, "onCreate");
        this.preferences = RoaringPreferences.createInstance(this);

        Context context = this.getApplicationContext();
        context.startService(new Intent(context, MadHeadTosObserver.class));

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        J.d(TAG, "onDestroy");
        Context context = this.getApplicationContext();
        context.stopService(new Intent(context, MadHeadTosObserver.class));

        this.preferences.save();

        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        J.d(TAG, "Notification '%s' posted", sbn.getPackageName());

        if (preferences.enabled) {
            List<String> idList = preferences.notificationIdMap.get(sbn.getPackageName());

            if (idList != null) {
                if (idList.isEmpty()) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(this.getApplicationContext(), preferences.ringtone);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(1, 1);
                    mediaPlayer.start();

                    AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, preferences.volume, 0);
                }

                String id = Integer.toString(sbn.getId());
                if (!idList.contains(id)) {
                    idList.add(id);
                }
            } else {
                J.d(TAG, "Notification '%s' is ignored", sbn.getPackageName());
            }
        } else {
            J.d(TAG, "Roaring is not enabled");
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        J.d(TAG, "Notification '%s' removed", sbn.getPackageName());

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
        } else {
            J.d(TAG, "Roaring is not enabled");
        }
    }
}
