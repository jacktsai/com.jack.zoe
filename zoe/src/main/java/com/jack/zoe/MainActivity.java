package com.jack.zoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jack.zoe.util.J;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private PicturesAdapter adapter;
    private int currentImageIndex = 0;
    private Timer imageScrollTimer;
    private ViewPager imagePager;
    private MessageAnimation messageAnimator;
    private int musicVolumeBefore;
    private BackgroundMusic bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.main_title);
        this.setContentView(R.layout.activity_main);

        AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        musicVolumeBefore = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (musicVolumeBefore > 0) {
            int suggestedVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
            if (musicVolumeBefore < suggestedVolume) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, suggestedVolume, 0);
            }
        }

        this.bgm = new BackgroundMusic();
        this.bgm.playNext();

        this.adapter = new PicturesAdapter();
        this.imagePager = (ViewPager)super.findViewById(R.id.imagePager);
        this.imagePager.setAdapter(adapter);
        this.imagePager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentImageIndex = position;
                stopScrollImage();
                startScrollImage();
            }
        });
        this.startScrollImage();

        this.messageAnimator = new MessageAnimation();
        this.messageAnimator.start();
    }

    @Override
    protected void onDestroy() {
        this.messageAnimator.cancel();
        this.bgm.destroy();
        this.stopScrollImage();

        AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolumeBefore, 0);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = super.getMenuInflater();
        inflater.inflate(R.menu.main_activity_settings, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                this.startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScrollImage() {
        this.imageScrollTimer = new Timer();
        this.imageScrollTimer.schedule(new ImageScrollTask(), 5000);
    }

    private void stopScrollImage() {
        this.imageScrollTimer.cancel();
    }

    class BackgroundMusic {
        private int[] musicArray;
        private Random r = new Random();
        private MediaPlayer mp3Player;

        BackgroundMusic() {
            Field[] fields = R.raw.class.getDeclaredFields();
            musicArray = new int[fields.length];
            for (int i = 0; i < fields.length; i++) {
                try {
                    int resId = fields[i].getInt(null);
                    musicArray[i] = resId;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        public void playNext() {
            int selected = musicArray[r.nextInt(musicArray.length)];

            mp3Player = MediaPlayer.create(MainActivity.this, selected);
            mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playNext();
                }
            });
            mp3Player.setVolume(1, 1);
            mp3Player.start();
        }

        public void destroy() {
            mp3Player.stop();
            mp3Player.release();
        }
    }

    class PicturesAdapter extends PagerAdapter implements Settings.Sliding.OnChangeListener {
        private final String TAG = PicturesAdapter.class.getSimpleName();

        private int[] picArray = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j };
        Settings.Sliding sliding;
        private ArrayList<String> imageList = new ArrayList<String>();

        PicturesAdapter() {
            sliding = Settings.getInstance(MainActivity.this).sliding;
            String bucketName = sliding.get_bucket_name();
            if (sliding.get_use_external() && !TextUtils.isEmpty(bucketName)) {
                this.updateImageUri(bucketName);
            }
            sliding.setOnChangeListener(this);
        }

        @Override
        public int getCount() {
            if (sliding.get_use_external() && imageList.size() > 0) {
                return imageList.size();
            }

            return picArray.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.fragment_image, null);

            ImageView image = (ImageView)view.findViewById(R.id.mainImage);
            if (sliding.get_use_external() && imageList.size() > 0) {
                Bitmap bitmap = decodeSampledBitmapFile(imageList.get(position), 300, 300);
                image.setImageBitmap(bitmap);
            } else {
                image.setImageResource(picArray[position]);
            }

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void onUseExternalChange(boolean use_external) {
            this.notifyDataSetChanged();
        }

        @Override
        public void onBucketIdChange(String bucket_name) {
            this.updateImageUri(bucket_name);
            this.notifyDataSetChanged();
        }

        private void updateImageUri(String bucketName) {
            Uri baseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            imageList.clear();
            Cursor cursor = getContentResolver().query(
                    baseUri,
                    new String[] {MediaStore.Images.ImageColumns.DATA},
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "=?",
                    new String[] {bucketName},
                    null);
            try {
                while (cursor.moveToNext()) {
                    String filePath = cursor.getString(0);
                    imageList.add(filePath);
                    J.d(TAG, "image file %s added", filePath);
                }
            } finally {
                cursor.close();
            }
        }

        private Bitmap decodeSampledBitmapFile(String filePath, int reqWidth, int reqHeight) {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, options);
        }

        private int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }

    class ImageScrollTask extends TimerTask {
        private Runnable runnable = new Runnable() {
            public void run() {
                this.scrollToNextImage();
            }

            private void scrollToNextImage() {
                int index = currentImageIndex;
                index = index + 1;
                if ( index >= adapter.getCount()) {
                    index = 0;
                }

                imagePager.setCurrentItem(index);
            }
        };

        @Override
        public void run() {
            runOnUiThread(runnable);
        }
    }

    class MessageAnimation {

        class MessagePair {
            public String Message;
            public android.widget.TextView TextView;
        }

        final private MessagePair[] messagePairs;

        public MessageAnimation() {
            Resources resources = getResources();

            MessagePair greetings = new MessagePair();
            greetings.Message = resources.getString(R.string.greetings);
            greetings.TextView = (TextView)findViewById(R.id.greetings);

            MessagePair message1 = new MessagePair();
            message1.Message = resources.getString(R.string.message1);
            message1.TextView = (TextView)findViewById(R.id.message1);

            MessagePair message2 = new MessagePair();
            message2.Message = resources.getString(R.string.message2);
            message2.TextView = (TextView)findViewById(R.id.message2);

            MessagePair message3 = new MessagePair();
            message3.Message = resources.getString(R.string.message3);
            message3.TextView = (TextView)findViewById(R.id.message3);

            MessagePair message4 = new MessagePair();
            message4.Message = resources.getString(R.string.message4);
            message4.TextView = (TextView)findViewById(R.id.message4);

            MessagePair message5 = new MessagePair();
            message5.Message = resources.getString(R.string.message5);
            message5.TextView = (TextView)findViewById(R.id.message5);

            MessagePair signature = new MessagePair();
            signature.Message = resources.getString(R.string.signature);
            signature.TextView = (TextView)findViewById(R.id.signature);

            this.messagePairs = new MessagePair[] { greetings, message1, message2, message3, message4, message5, signature };
        }

        class MessageRunnable implements Runnable {
            final private char[] messageChars;
            final private TextView textView;

            private int displayedLength = 0;

            public MessageRunnable(String message, TextView textView) {
                this.messageChars = message.toCharArray();
                this.textView = textView;
            }

            public boolean getFinished() {
                return this.displayedLength == this.messageChars.length;
            }

            public void run() {
                this.displayedLength = this.displayedLength + 1;
                this.textView.setText(this.messageChars, 0, Math.min(this.messageChars.length, this.displayedLength));
            }
        }

        Timer timer;

        public boolean getFinished() {
            return this.timer == null;
        }

        public void start() {
            this.clearAllMessage();

            final TimerTask task = new TimerTask() {
                private MessageRunnable currentMessage;
                private int currentIndex = -1;

                @Override
                public void run() {
                    if (this.isRunning()) {
                        runOnUiThread(this.currentMessage);
                    } else {
                        if (this.hasNext()) {
                            currentMessage = this.createNextRunner();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                        } else {
                            MessageAnimation.this.cancel();
                        }
                    }
                }

                private boolean isRunning() {
                    return currentMessage != null && !currentMessage.getFinished();
                }

                private boolean hasNext() {
                    if (currentMessage != null && currentMessage.getFinished() ) {
                        if (currentIndex == messagePairs.length - 1) {
                            return false;
                        }
                    }

                    return true;
                }

                private MessageRunnable createNextRunner() {
                    currentIndex++;
                    String message = messagePairs[currentIndex].Message;
                    TextView textView = messagePairs[currentIndex].TextView;

                    return new MessageRunnable(message, textView);
                }
            };

            this.timer = new Timer();
            this.timer.schedule(task, 1000, 200);
        }

        public void cancel() {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer = null;
            }
        }

        private void clearAllMessage() {
            for (MessagePair messagePair : this.messagePairs) {
                messagePair.TextView.setText("");
            }
        }
    }
}
