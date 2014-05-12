package com.jack.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        super.findViewById(R.id.createNotification1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification((int)System.currentTimeMillis());
            }
        });

        super.findViewById(R.id.createNotification2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification(123456);
            }
        });

        final Button playAlarm = (Button)super.findViewById(R.id.playAlarm);
        playAlarm.setOnClickListener(new View.OnClickListener() {
            private MediaPlayer player;

            @Override
            public void onClick(View v) {
                if (player == null) {
                    player = MediaPlayer.create(MainActivity.this, R.raw.alarm);
                    player.setVolume(1, 1);
                    player.setLooping(true);
                    player.start();
                } else {
                    player.stop();
                    player.release();
                    player = null;
                }
            }
        });
    }

    private void createNotification(int id) {
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        builder.setContentTitle("ContentTitle");
        builder.setContentText("ContentText");
        builder.setContentInfo("ContentInfo");
        builder.setSubText("SubText");
        builder.setTicker("TickerText");
        builder.setSmallIcon(R.drawable.ic_launcher);

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());
    }
}
