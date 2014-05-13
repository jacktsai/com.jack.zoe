package com.jack.zoe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.jack.zoe.tos.TosFile;
import com.jack.zoe.util.J;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class MadHeadTosObserver extends Service {

    private static final String TAG = MadHeadTosObserver.class.getSimpleName();

    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        J.d2(TAG, "onCreate");

        TimerTask observerTask = new TimerTask() {
            private final Context context = getApplicationContext();

            @Override
            public void run() {
                if (TosFile.isChanged()) {
                    TosFile saveData = TosFile.snapshot(context);

                    try {
                        this.notifyFloorWaveInformation(saveData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void notifyFloorWaveInformation(TosFile tosFile) throws JSONException {
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(7533967);

                Iterable<TosFile.FloorWave> floorWaves = tosFile.CURRENT_FLOOR_WAVES();
                if (floorWaves == null) {
                    return;
                }

                StringBuilder messageBuilder = new StringBuilder();
                int lootCount = 0;
                for (TosFile.FloorWave wave : floorWaves) {
                    for (TosFile.FloorEnemy enemy : wave.enemies()) {
                        TosFile.LootItem lootItem = enemy.lootItem();
                        if (lootItem != null) {
                            if (lootCount > 0)
                                messageBuilder.append("\n");

                            messageBuilder.append(lootItem);
                            lootCount++;
                        }
                    }
                }

                if (lootCount > 0) {
                    Notification notification = new Notification.Builder(context)
                            .setTicker("神魔之塔關卡獎勵公告")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(true)
                            .build();
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_tos_loots);
                    views.setTextViewText(R.id.loot_title, String.format("%s的關卡獎勵如下", tosFile.GAME_LOCAL_USER()));
                    views.setTextViewText(R.id.loot_desc, messageBuilder.toString());
                    notification.bigContentView = views;

                    manager.notify(7533967, notification);
                }
            }
        };

        timer.schedule(observerTask, 1000, 5000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        J.d2(TAG, "onDestroy");

        this.timer.cancel();

        super.onDestroy();
    }
}
