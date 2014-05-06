package com.jack.zoe;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private TimerTask pictureRollingTask;
    private MessageAnimation messageAnimator;
    private MediaPlayer mp3Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        this.startRollingPicture();

        this.messageAnimator = new MessageAnimation(this);
        this.messageAnimator.start();

        this.startBGM();
    }

    @Override
    protected void onDestroy() {
        this.stopBGM();
        this.messageAnimator.cancel();
        this.stopRollingPicture();
        super.onDestroy();
    }

    private void startRollingPicture() {
        this.pictureRollingTask = new TimerTask() {
            private int[] picArray = new int[] { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j };
            private int currentIndex = 0;
            private ImageView zoePicture = (ImageView)MainActivity.this.findViewById(R.id.zoePicture);

            private Runnable runnable = new Runnable() {
                public void run() {
                    displayPicture();
                    prepareNextPicture();
                }

                private void displayPicture() {
                    zoePicture.setImageResource(picArray[currentIndex]);
                }

                private void prepareNextPicture() {
                    currentIndex = currentIndex + 1;
                    if ( currentIndex >= picArray.length) {
                        currentIndex = 0;
                    }
                }
            };

            @Override
            public void run() {
                MainActivity.this.runOnUiThread(runnable);
            }
        };

        new Timer().schedule(pictureRollingTask, 100, 3000);
    }

    private void stopRollingPicture() {
        this.pictureRollingTask.cancel();
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
