package com.jack.zoe;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private static String TAG = "Zoe";

    private int[] picArray = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j };
    private int currentImageIndex = 0;
    private Timer imageScrollTimer;
    private TimerTask imageScrollTask;
    private ViewPager imagePager;
    private MessageAnimation messageAnimator;
    private MediaPlayer mp3Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        this.messageAnimator = new MessageAnimation(this);
        this.messageAnimator.start();

        this.startBGM();

        PicturesAdapter adapter = new PicturesAdapter();
        this.imagePager = (ViewPager)super.findViewById(R.id.pictures);
        this.imagePager.setAdapter(adapter);
        this.imagePager.setOffscreenPageLimit(adapter.getCount());
        this.imagePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, String.format("onPageSelected %d", position));
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

    class PicturesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return picArray.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView view = new ImageView(MainActivity.this);
            view.setImageResource(picArray[position]);
            view.setMaxHeight(300);
            view.setBackgroundColor(Color.DKGRAY);

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
            MainActivity.this.runOnUiThread(runnable);
        }
    }

    @Override
    protected void onDestroy() {
        this.stopBGM();
        this.messageAnimator.cancel();
        this.stopScrollImage();
        super.onDestroy();
    }

    private void startScrollImage() {
        this.imageScrollTask = new ImageScrollTask();
        this.imageScrollTimer = new Timer();
        this.imageScrollTimer.schedule(this.imageScrollTask, 5000);
    }

    private void stopScrollImage() {
        this.imageScrollTimer.cancel();
        this.imageScrollTask.cancel();
    }

    private void startBGM() {
        int[] mp3Array = new int[] { R.raw.dance_of_the_dragonfly, R.raw.sundial_reams, R.raw.through_the_arbor };
        Random r = new Random();
        int selectedMp3 = mp3Array[r.nextInt(3)];

        this.mp3Player = MediaPlayer.create(this, selectedMp3);
        this.mp3Player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                startBGM();
            }
        });
        this.mp3Player.start();
    }

    private void stopBGM(){
        this.mp3Player.stop();
        this.mp3Player.release();
    }
}
