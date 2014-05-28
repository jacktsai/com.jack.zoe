package com.jack.notifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
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

    private static WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams = createLayoutParams();
    private float startX, startY;
    private int touchSlop;

    private TextView modeView;
    private ImageView actionView;
    private Bitmap startIcon, stopIcon;
    private Animation stopAnimation;
    private Bitmap settingIcon;
    private Bitmap exitIcon;

    public FloatingMenu(final Context context, final Handler handler) {
        super(context);

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.floating_menu_layout, this);

        int iconWidth = (int)(displayMetrics.widthPixels * 0.125);
        int iconHeight = iconWidth;

        modeView = (TextView)findViewById(R.id.mode);
        modeView.setLayoutParams(new LinearLayout.LayoutParams(iconWidth, iconHeight));
        modeView.setHapticFeedbackEnabled(true);
        modeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(BackgroundService.MSG_MODE_TOGGLE);
            }
        });

        startIcon = createStartIcon(iconWidth, iconHeight);
        stopIcon = createStopIcon(iconWidth, iconHeight);
        stopAnimation = new AlphaAnimation(1.0f, 0.4f);
        stopAnimation.setDuration(1000);
        stopAnimation.setInterpolator(new LinearInterpolator());
        stopAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        stopAnimation.setRepeatMode(AlphaAnimation.REVERSE);
        actionView = (ImageView)findViewById(R.id.action);
        actionView.setHapticFeedbackEnabled(true);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(BackgroundService.MSG_ACTION_TOGGLE);
            }
        });

        settingIcon = createSettingIcon(iconWidth, iconHeight);
        ImageView settingView = (ImageView)findViewById(R.id.setting);
        settingView.setHapticFeedbackEnabled(true);
        settingView.setImageBitmap(settingIcon);
        settingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(BackgroundService.MSG_SETTING_CLICK);
            }
        });

        exitIcon = createExitIcon(iconWidth, iconHeight);
        ImageView exitView = (ImageView)findViewById(R.id.exit);
        exitView.setHapticFeedbackEnabled(true);
        exitView.setImageBitmap(exitIcon);
        exitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(BackgroundService.MSG_EXIT_CLICK);
            }
        });

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        windowManager.addView(this, layoutParams);
    }

    public void onDestroy() {
        windowManager.removeView(this);

        startIcon.recycle();
        stopIcon.recycle();
        settingIcon.recycle();
        exitIcon.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                J.d(TAG, "onTouchEvent_ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                J.d(TAG, "onTouchEvent_ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                J.d(TAG, "onTouchEvent_ACTION_MOVE");
                float x = event.getRawX();
                float y = event.getRawY();
                layoutParams.x = (int) (layoutParams.x - startX + x);
                layoutParams.y = (int) (layoutParams.y - startY + y);
                windowManager.updateViewLayout(this, layoutParams);
                startX = x;
                startY = y;
                return true;
            case MotionEvent.ACTION_CANCEL:
                J.d(TAG, "onTouchEvent_ACTION_CANCEL");
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                J.d(TAG, "onInterceptTouchEvent_ACTION_DOWN");
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                J.d(TAG, "onInterceptTouchEvent_ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                J.d(TAG, "onInterceptTouchEvent_ACTION_MOVE");
                float x = event.getRawX();
                float y = event.getRawY();
                if ((Math.abs(startX - x) >= touchSlop || Math.abs(startY - y) >= touchSlop)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                J.d(TAG, "onInterceptTouchEvent_ACTION_CANCEL");
                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    public void updateMode(String mode) {
        modeView.setText(mode);
    }

    public void updateAction(boolean running) {
        if (running) {
            actionView.setImageBitmap(stopIcon);
            actionView.setBackgroundResource(R.drawable.floating_menu_processing);
            actionView.startAnimation(stopAnimation);
        } else {
            actionView.clearAnimation();
            actionView.setImageBitmap(startIcon);
            actionView.setBackgroundResource(R.drawable.floating_menu_border1);
        }
    }

    private Bitmap createStartIcon(int width, int height) {
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
