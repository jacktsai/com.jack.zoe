package com.jack.notifier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class MyFloatView extends ImageView {
    private static final String TAG = MyFloatView.class.getSimpleName();

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private int prevX, prevY;
    private float startX, startY, currX, currY;

    public MyFloatView(Context context) {
        super(context);
        this.setImageResource(R.drawable.ic_launcher);
        this.setBackgroundColor(Color.WHITE);
        this.windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.initializeLayoutParams();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevX = layoutParams.x;
                prevY = layoutParams.y;
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                currX = event.getRawX();
                currY = event.getRawY();
                updatePosition();
                break;
        }

        return true;
    }

    public void show() {
        if (!this.isShown()) {
            windowManager.addView(this, layoutParams);
        }
    }

    public void dismiss() {
        if (this.isShown()) {
            windowManager.removeView(this);
        }
    }

    private void initializeLayoutParams() {
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR; //WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.TRANSLUCENT; //PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 500;
        layoutParams.y = 950;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void updatePosition() {
        layoutParams.x = (int)(prevX - startX + currX);
        layoutParams.y = (int)(prevY - startY + currY);
        windowManager.updateViewLayout(this, layoutParams);
    }
}
