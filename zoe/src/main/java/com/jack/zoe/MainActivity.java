package com.jack.zoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jack.zoe.util.J;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int[] picArray = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j };
    private int currentImageIndex = 0;
    private Timer imageScrollTimer;
    private ViewPager imagePager;
    private MessageAnimation messageAnimator;
    private int musicVolumeBefore;
    private MediaPlayer mp3Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle(R.string.main_title);
        super.setContentView(R.layout.activity_main);

        this.messageAnimator = new MessageAnimation();
        this.messageAnimator.start();

        AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        musicVolumeBefore = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int suggestedVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2;
        if (musicVolumeBefore < suggestedVolume) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, suggestedVolume, 0);
        }
        this.startBGM();

        PicturesAdapter adapter = new PicturesAdapter();
        this.imagePager = (ViewPager)super.findViewById(R.id.imagePager);
        this.imagePager.setAdapter(adapter);
        this.imagePager.setOffscreenPageLimit(adapter.getCount());
        this.imagePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentImageIndex = position;
                stopScrollImage();
                startScrollImage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        this.startScrollImage();
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
            case R.id.nlSetting:
                this.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                return true;

            case R.id.roaringSetting:
                this.startActivity(new Intent(this, RoaringSettingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        J.d(TAG, "onResume");
        this.mp3Player.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        J.d(TAG, "onPause");
        this.mp3Player.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        this.stopBGM();

        AudioManager audioManager = (AudioManager)this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolumeBefore, 0);

        this.messageAnimator.cancel();
        this.stopScrollImage();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (this.messageAnimator.getFinished()) {
                this.messageAnimator.start();

                this.mp3Player.seekTo(this.mp3Player.getDuration());

                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    private void startScrollImage() {
        this.imageScrollTimer = new Timer();
        this.imageScrollTimer.schedule(new ImageScrollTask(), 5000);
    }

    private void stopScrollImage() {
        this.imageScrollTimer.cancel();
    }

    private void startBGM() {
        int[] mp3Array = new int[] { R.raw.dance_of_the_dragonfly, R.raw.sundial_reams, R.raw.through_the_arbor };
        Random r = new Random();
        int selected = mp3Array[r.nextInt(mp3Array.length)];

        this.mp3Player = MediaPlayer.create(this, selected);
        this.mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startBGM();
                mp3Player.start();
            }
        });
        this.mp3Player.setVolume(1, 1);
    }

    private void stopBGM(){
        this.mp3Player.stop();
        this.mp3Player.release();
    }

    class PicturesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return picArray.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.fragment_image, null);

            ImageView image = (ImageView)view.findViewById(R.id.mainImage);
            image.setImageResource(picArray[position]);

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
    }

    class ImageScrollTask extends TimerTask {
        private Runnable runnable = new Runnable() {
            public void run() {
                this.scrollToNextImage();
            }

            private void scrollToNextImage() {
                int index = currentImageIndex;
                index = index + 1;
                if ( index >= picArray.length) {
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
