package com.jack.notifier;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jack.notifier.util.J;

public class MyFloatView extends ImageView {
    private static final String TAG = MyFloatView.class.getSimpleName();

    private static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        J.i(TAG, "window visible display frame: %s", frame.toString());

        return statusBarHeight;
    }

    private int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private float startX, startY;
    private float x, y;

    public MyFloatView(Activity activity) {
        super(activity);
        this.setImageResource(R.drawable.ic_launcher);
        this.setBackgroundColor(Color.WHITE);
        this.statusBarHeight = getStatusBarHeight(activity);
        this.windowManager = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
        this.initializeLayoutParams();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getRawX();
        y = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                updatePosition();
                break;
            case MotionEvent.ACTION_MOVE:
                updatePosition();
                break;
            case MotionEvent.ACTION_UP:
                updatePosition();
                startX = 0;
                startY = 0;
                break;
        }

        return true;
    }

    public void show() {
        windowManager.addView(this, layoutParams);
    }

    public void dismiss() {
        windowManager.removeView(this);
    }

    private void initializeLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR; //WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.TRANSLUCENT; //PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 500;
        layoutParams.y = 950;
        layoutParams.width = 150;
        layoutParams.height = 150;
    }

    private void updatePosition() {
        layoutParams.x = (int)(x - startX);
        layoutParams.y = (int)(y - startY - statusBarHeight);
        windowManager.updateViewLayout(this, layoutParams);
    }
}
