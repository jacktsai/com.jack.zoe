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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MadHeadTosObserver extends Service {

    private static final String TAG = MadHeadTosObserver.class.getSimpleName();
    private static final int NOTIFICATION_ID = 7533967;

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
                    TosFile tosFile = TosFile.snapshot(context);

                    try {
                        this.notifyFloorWaveInformation(tosFile);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            private void notifyFloorWaveInformation(TosFile tosFile) throws JSONException {
                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(NOTIFICATION_ID);

                Iterable<TosFile.FloorWave> floorWaves = tosFile.CURRENT_FLOOR_WAVES();
                if (floorWaves == null) {
                    return;
                }

                List<Integer> collectedIds = tosFile.USER_COLLECTED_MONSTER_IDS();

                StringBuilder messageBuilder = new StringBuilder();
                int lootCount = 0;
                for (TosFile.FloorWave wave : floorWaves) {
                    for (TosFile.FloorEnemy enemy : wave.enemies()) {
                        TosFile.LootItem lootItem = enemy.lootItem();
                        if (lootItem != null) {
                            if (lootCount > 0)
                                messageBuilder.append("\n");

                            String lootType = lootItem.type();

                            if (lootType.equals("money")) {
                                messageBuilder.append(String.format("金錢 %d", lootItem.amount()));
                            } else if (lootType.equals("monster")) {
                                TosFile.Card card = lootItem.card();
                                messageBuilder.append(String.format("卡號%d-%s", card.monsterId(), card.monsterName()));

                                if (collectedIds != null) {
                                    if (!collectedIds.contains(card.monsterId())) {
                                        messageBuilder.append("*NEW*");
                                    }
                                }
                            }

                            lootCount++;
                        }
                    }
                }

                if (lootCount > 0) {
                    Notification notification = new Notification.Builder(context)
                            .setTicker("神魔之塔關卡當前獎勵通知")
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setOngoing(true)
                            .build();
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_tos_loots);
                    views.setTextViewText(R.id.loot_title, String.format("%s的當前獎勵如下", tosFile.GAME_LOCAL_USER()));
                    views.setTextViewText(R.id.loot_desc, messageBuilder.toString());
                    notification.bigContentView = views;

                    manager.notify(NOTIFICATION_ID, notification);
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
