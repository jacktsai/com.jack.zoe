package com.jack.zoe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.jack.zoe.tos.TosFile;
import com.jack.zoe.util.J;

import org.json.JSONException;

import java.util.List;
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
        J.d(TAG, "onCreate");

        final Context context = getApplicationContext();
        final Settings.ToS tos = Settings.getInstance(context).tos;
        final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        TimerTask observerTask = new TimerTask() {
            @Override
            public void run() {
                if (tos.get_show_current_loots()) {
                    if (TosFile.isChanged()) {
                        TosFile tosFile = TosFile.snapshot(context);

                        if (tosFile != null) {
                            try {
                                Notification notification = this.createNotification(tosFile);
                                nm.notify(7533967, notification);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    nm.cancel(7533967);
                }
            }

            private Notification createNotification(TosFile tosFile) throws JSONException {
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_tos);
                remoteViews.setImageViewResource(R.id.icon, R.drawable.ic_launcher);

                this.fillLoots(tosFile, remoteViews);

                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, TosLootsActivity.class), 0);

                Notification notification = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setOngoing(true)
                        .setContentIntent(contentIntent)
                        .build();
                notification.bigContentView = remoteViews;

                return notification;
            }

            private void fillLoots(TosFile tosFile, RemoteViews remoteViews) throws JSONException {
                StringBuilder messageBuilder = new StringBuilder();

                String floor = tosFile.CURRENT_FLOOR();
                if (floor != null && floor.length() > 0) {
                    Iterable<TosFile.FloorWave> floorWaves = tosFile.CURRENT_FLOOR_WAVES();
                    List<Integer> collectedIds = tosFile.USER_COLLECTED_MONSTER_IDS();
                    int lootCount = 0;

                    for (TosFile.FloorWave wave : floorWaves) {
                        for (TosFile.FloorEnemy enemy : wave.enemies()) {
                            TosFile.LootItem lootItem = enemy.lootItem();
                            if (lootItem != null) {
                                String lootType = lootItem.type();
                                if (lootType.equals("money") || lootType.equals("monster")) {
                                    if (lootCount > 0) {
                                        messageBuilder.append("\n");
                                    }

                                    if (lootType.equals("money")) {
                                        messageBuilder.append(String.format("金幣 %d", lootItem.amount()));
                                    } else if (lootType.equals("monster")) {
                                        TosFile.Card card = lootItem.card();
                                        messageBuilder.append(String.format("[%s]%d星%s-%s", card.number(), card.rarity(), card.race(), card.name()));
                                        if (collectedIds != null) {
                                            if (!collectedIds.contains(card.id())) {
                                                messageBuilder.append("*NEW*");
                                            }
                                        }
                                    }

                                    lootCount++;
                                }
                            }
                        }
                    }

                    if (lootCount > 0) {
                        remoteViews.setTextViewText(R.id.loot_desc, messageBuilder.toString());
                    }
                }
            }
        };

        timer.schedule(observerTask, 1000, 15000);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        J.d(TAG, "onDestroy");

        this.timer.cancel();

        super.onDestroy();
    }
}
