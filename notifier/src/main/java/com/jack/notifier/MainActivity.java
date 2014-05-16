package com.jack.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends Activity {
    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        super.findViewById(R.id.setupNLS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });

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

        this.findViewById(R.id.playAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) {
                    startPlayAlarm();
                } else {
                    stopPlayAlarm();
                }
            }
        });

        SeekBar volume = (SeekBar)this.findViewById(R.id.volume);
        volume.setMax(10);
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("", String.format("progress = %d", progress));
                if (player != null) {
                    float v = (float) progress / 10;
                    Log.d("", String.format("v = %f", v));
                    player.setVolume(v, v);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        super.findViewById(R.id.launchSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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

    private void startPlayAlarm() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        player = MediaPlayer.create(MainActivity.this, alert);
        player.setVolume(0.1f, 0.1f);
        player.setLooping(true);
        player.start();
    }

    private void stopPlayAlarm() {
        player.stop();
        player.release();
        player = null;
    }
}
