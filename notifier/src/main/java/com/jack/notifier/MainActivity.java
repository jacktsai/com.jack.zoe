package com.jack.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jack.notifier.util.J;
import com.jack.notifier.util.Su;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.findViewById(R.id.setupNLS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });

        this.findViewById(R.id.createNotification1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification((int)System.currentTimeMillis());
            }
        });

        this.findViewById(R.id.createNotification2).setOnClickListener(new View.OnClickListener() {
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
                    player.setVolume(v, v);
                    ((TextView)findViewById(R.id.volumeText)).setText(Integer.toString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.findViewById(R.id.launchSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        this.findViewById(R.id.pickupPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        this.findViewById(R.id.queryAllImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAllImages();
            }
        });

        this.findViewById(R.id.queryAllBuckets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAllBuckets();
            }
        });

        this.findViewById(R.id.checkMediaScanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMediaScanner();
            }
        });

        this.findViewById(R.id.showContentProviders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContentProviders();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri uri = data.getData();
                    Log.d(TAG, String.format("data = %s, path = %s", uri, getPath(uri)));
                    break;
            }
        }
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

    private String getPath(Uri uri) {
        String[]  projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.TITLE
        };
        CursorLoader loader = new CursorLoader(this, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        cursor.moveToFirst();

        String value1 = cursor.getString(0);
        int value2 = cursor.getInt(1);
        String value3 = cursor.getString(2);

        Log.d(TAG, String.format("%s, %d, %s", uri, value2, value3));

        return value1;
    }

    private void queryAllImages() {
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        final String[] IMAGE_PROJECTION = new String[] {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA
        };

        Cursor cursor = this.getContentResolver().query(baseUri, IMAGE_PROJECTION, null, null, null);
        while (cursor.moveToNext()) {
            String message = String.format("%s/%d %s %s", baseUri, cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            Log.d(TAG, message);
        }

        cursor.close();
    }

    private void queryAllBuckets() {
        Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .buildUpon()
                .appendQueryParameter("distinct", "true")
                .build();

        final String[] IMAGE_PROJECTION = new String[] {
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = this.getContentResolver().query(baseUri, IMAGE_PROJECTION, null, null, null);
        while (cursor.moveToNext()) {
            int bucketId = cursor.getInt(0);
            String bucketName = cursor.getString(1);
            String hashCode = String.valueOf(bucketName.toLowerCase().hashCode());
            String message = String.format("bucket %d %s %s", bucketId, bucketName, hashCode);
            Log.d(TAG, message);
        }

        cursor.close();
    }

    private void checkMediaScanner() {
        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.getMediaScannerUri(),
                new String[] {MediaStore.MEDIA_SCANNER_VOLUME},
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String message = String.format("scanner volume %s", cursor.getString(0));
                Log.d(TAG, message);
            }

            cursor.close();
        } else {
            Log.d(TAG, String.format("not scanning now"));
        }
    }

    private void showContentProviders() {
        for (PackageInfo packageInfo : getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            if (packageInfo.providers != null) {
                for (ProviderInfo providerInfo : packageInfo.providers) {
                    J.d(TAG, "provider: %s, %s", providerInfo.authority, providerInfo.readPermission);
                }
            }
        }
    }
}
