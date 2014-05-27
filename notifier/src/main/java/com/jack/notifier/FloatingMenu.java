package com.jack.notifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jack.notifier.util.J;

public class FloatingMenu extends FrameLayout {
    private static final String TAG = FloatingMenu.class.getSimpleName();

    private static final WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams = createLayoutParams();
    private int prevX, prevY;
    private float startX, startY, currX, currY;
    private boolean moving = false;

    public FloatingMenu(final EmptyService context) {
        super(context);

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.floating_menu_layout, this);

        int iconWidth = (int)(displayMetrics.widthPixels * 0.125);
        int iconHeight = iconWidth;

        final TextView mode = (TextView)findViewById(R.id.mode);
        mode.setLayoutParams(new LinearLayout.LayoutParams(iconWidth, iconHeight));
        mode.setHapticFeedbackEnabled(true);
        mode.setText("A");
        mode.setOnClickListener(new View.OnClickListener() {
            private boolean currentMode = true;
            @Override
            public void onClick(View v) {
                if (currentMode) {
                    mode.setText("M");
                    currentMode = false;
                } else {
                    mode.setText("A");
                    currentMode = true;
                }
            }
        });

        final Bitmap performIcon = createPerformIcon(iconWidth, iconHeight);
        final Bitmap stopIcon = createStopIcon(iconWidth, iconHeight);
        final Animation stopAnimation = new AlphaAnimation(1.0f, 0.4f);
        stopAnimation.setDuration(1000);
        stopAnimation.setInterpolator(new LinearInterpolator());
        stopAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        stopAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        final ImageView perform = (ImageView)findViewById(R.id.perform);
        perform.setHapticFeedbackEnabled(true);
        perform.setImageBitmap(performIcon);
        perform.setOnClickListener(new View.OnClickListener() {
            private boolean performing = false;
            @Override
            public void onClick(View v) {
                if (performing) {
                    perform.clearAnimation();
                    perform.setImageBitmap(performIcon);
                    perform.setBackgroundResource(R.drawable.floating_menu_border1);
                    performing = false;
                } else {
                    perform.setImageBitmap(stopIcon);
                    perform.setBackgroundResource(R.drawable.floating_menu_processing);
                    perform.startAnimation(stopAnimation);
                    performing = true;
                }
            }
        });

        final Bitmap settingIcon = createSettingIcon(iconWidth, iconHeight);
        ImageView setting = (ImageView)findViewById(R.id.setting);
        setting.setHapticFeedbackEnabled(true);
        setting.setImageBitmap(settingIcon);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        final Bitmap exitIcon = createExitIcon(iconWidth, iconHeight);
        ImageView exit = (ImageView)findViewById(R.id.exit);
        exit.setHapticFeedbackEnabled(true);
        exit.setImageBitmap(exitIcon);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.stopSelf();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                prevX = layoutParams.x;
                prevY = layoutParams.y;
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                currX = event.getRawX();
                currY = event.getRawY();
                if ((Math.abs(startX - currX) >= 10 || Math.abs(startY - currY) >= 10) && event.getPointerCount() == 1) {
                    layoutParams.x = (int) (prevX - startX + currX);
                    layoutParams.y = (int) (prevY - startY + currY);
                    windowManager.updateViewLayout(this, layoutParams);
                    moving = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (moving) {
                    moving = false;
                    return true;
                }
                break;
        }

        return false;
    }

    public void show() {
        if (!isShown()) {
            windowManager.addView(this, layoutParams);
        }
    }

    public void dismiss() {
        if (isShown()) {
            windowManager.removeView(this);
        }
    }

    private Bitmap createPerformIcon(int width, int height) {
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(width / 3, height / 4 + height / 20);
        path.lineTo(width / 3, 3 * height / 4 - height / 20);
        path.lineTo(2*width/3 , height/2);
        path.lineTo(width / 3, height / 4 + height / 20);
        path.close();

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(6);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPath(path, paint);

        return bitmap;
    }

    private Bitmap createStopIcon(int width, int height) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(6);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawLine(2 * width / 5, height / 4 + height / 20, 2 * width / 5, 3 * height / 4 - height / 20, paint);
        canvas.drawLine(3 * width / 5, height / 4 + height / 20, 3 * width / 5, 3 * height / 4 - height / 20, paint);

        return bitmap;
    }

    private Bitmap createSettingIcon(int width, int height) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(6);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawLine(width / 4,  7 * height / 20, 3 * width / 4,  7 * height / 20, paint);
        canvas.drawLine(width / 4, 13 * height / 20, 3 * width / 4, 13 * height / 20, paint);
        canvas.drawLine(7 * width / 20, height / 4, 7 * width / 20, 9 * height / 20, paint);
        canvas.drawLine(13 * width / 20, 11 * height / 20, 13 * width / 20, 3 * height / 4, paint);

        return bitmap;
    }

    private Bitmap createExitIcon(int width, int height) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(6);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawLine(2 * width / 5, 2 * height / 5, 3 * width / 5, 3 * height / 5, paint);
        canvas.drawLine(3 * width / 5, 2 * height / 5, 2 * width / 5, 3 * height / 5, paint);

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(width / 2, height / 2, 3 * width / 10, paint);

        return bitmap;
    }
}
