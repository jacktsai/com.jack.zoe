package com.jack.zoe;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.jack.zoe.util.J;

import java.io.IOException;


public class RoaringSettingActivity extends Activity {

    private static final String TAG = RoaringSettingActivity.class.getSimpleName();

    private RoaringPreferences preferences;
    private CheckBox enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        J.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_roaring_settings);

        preferences = RoaringPreferences.getInstance();
        enabled = (CheckBox)this.findViewById(R.id.enabled);
        enabled.setChecked(preferences.enabled);
        J.d(TAG, "enabled = %s", preferences.enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.enabled = isChecked;
            }
        });

        Button pickupRingtone = (Button)this.findViewById(R.id.pickupRingtone);
        pickupRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, preferences.ringtone);
                startActivityForResult(intent, 999);
            }
        });

        final AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);

        SeekBar roaringVolume = (SeekBar)this.findViewById(R.id.roaringVolume);
        roaringVolume.setMax(maxVolume);
        if (preferences.volume > maxVolume) {
            preferences.volume = maxVolume;
        }
        roaringVolume.setProgress(preferences.volume);
        roaringVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private MediaPlayer mediaPlayer;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                preferences.volume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(RoaringSettingActivity.this, preferences.ringtone);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1, 1);
                mediaPlayer.start();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 999:
                    preferences.ringtone = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onDestroy() {
        J.d(TAG, "onDestroy");
        preferences.save();

        super.onDestroy();
    }
}
