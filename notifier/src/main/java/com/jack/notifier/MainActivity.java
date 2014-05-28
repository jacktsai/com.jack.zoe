package com.jack.notifier;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jack.notifier.util.J;
import com.jack.notifier.util.Su;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    class Section {
        String title;
        Fragment fragment;
    }

    class Adapter extends FragmentPagerAdapter {

        public Adapter() {
            super(getFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            return sections[position].fragment;
        }

        @Override
        public int getCount() {
            return sections.length;
        }
    }

    public class SectionFragment1 extends Fragment {
        private MediaPlayer player;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_main_1, container, false);

            view.findViewById(R.id.setupNLS).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });

            view.findViewById(R.id.launchSettings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            });

            view.findViewById(R.id.createNotification1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNotification((int)System.currentTimeMillis());
                }
            });

            view.findViewById(R.id.createNotification2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNotification(123456);
                }
            });

            view.findViewById(R.id.playAlarm).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player == null) {
                        startPlayAlarm();
                    } else {
                        stopPlayAlarm();
                    }
                }
            });

            SeekBar volume = (SeekBar)view.findViewById(R.id.volume);
            volume.setMax(10);
            volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Log.d("", String.format("progress = %d", progress));
                    if (player != null) {
                        float v = (float) progress / 10;
                        player.setVolume(v, v);
                        ((TextView)view.findViewById(R.id.volumeText)).setText(Integer.toString(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            return view;
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

    public class SectionFragment2 extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main_2, container, false);

            view.findViewById(R.id.pickupPicture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            });

            view.findViewById(R.id.queryAllImages).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queryAllImages();
                }
            });

            view.findViewById(R.id.queryAllBuckets).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    queryAllBuckets();
                }
            });

            view.findViewById(R.id.checkMediaScanner).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkMediaScanner();
                }
            });

            view.findViewById(R.id.showContentProviders).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showContentProviders();
                }
            });

            return view;
        }

        private void queryAllImages() {
            Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            final String[] IMAGE_PROJECTION = new String[] {
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATA
            };

            Cursor cursor = getContentResolver().query(baseUri, IMAGE_PROJECTION, null, null, null);
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

            Cursor cursor = getContentResolver().query(baseUri, IMAGE_PROJECTION, null, null, null);
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
            ContentResolver resolver = getContentResolver();
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
                Log.d(TAG, String.format("not scanningnow"));
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

    public class SectionFragment3 extends Fragment {
        private MyFloatView floatView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main_3, container, false);

            view.findViewById(R.id.startService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "startService_onClick");
                    startService(new Intent(MainActivity.this, EmptyService.class));
                }
            });

            view.findViewById(R.id.stopService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "stopService_onClick");
                    stopService(new Intent(MainActivity.this, EmptyService.class));
                }
            });

            final ServiceConnection connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    J.d(TAG, "onServiceConnected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    J.d(TAG, "onServiceDisconnected");
                }
            };

            view.findViewById(R.id.bindService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "bindService_onClick");
                    bindService(new Intent(MainActivity.this, EmptyService.class), connection, Context.BIND_AUTO_CREATE);
                }
            });

            view.findViewById(R.id.unbindService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "unbindService_onClick");
                    unbindService(connection);
                }
            });

            view.findViewById(R.id.createFloatView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "createFloatView_onClick");
                    createFloatView();
                }
            });

            view.findViewById(R.id.destroyFloatView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "destroyFloatView_onClick");
                    destroyFloatView();
                }
            });

            view.findViewById(R.id.startBgService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "startBgService_onClick");
                    startService(new Intent(MainActivity.this, BackgroundService.class));
                }
            });

            view.findViewById(R.id.stopBgService).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    J.d(TAG, "stopBgService_onClick");
                    stopService(new Intent(MainActivity.this, BackgroundService.class));
                }
            });

            return view;
        }

        private void createFloatView() {
            if (floatView == null) {
                floatView = new MyFloatView(MainActivity.this);
                floatView.show();
            }
        }

        private void destroyFloatView() {
            if (floatView != null) {
                floatView.dismiss();
                floatView = null;
            }
        }
    }

    private final Section[] sections;
    private ViewPager viewPager;

    public MainActivity() {
        Section section1 = new Section();
        section1.title = "通知模擬";
        section1.fragment = new SectionFragment1();

        Section section2 = new Section();
        section2.title = "內容提供者";
        section2.fragment = new SectionFragment2();

        Section section3 = new Section();
        section3.title = "系統服務";
        section3.fragment = new SectionFragment3();

        sections = new Section[] {section1, section2, section3};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        viewPager.setAdapter(new Adapter());

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        for (Section section : sections) {
            ActionBar.Tab tab = actionBar.newTab().setText(section.title).setTabListener(tabListener);
            actionBar.addTab(tab);
        }
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
}
