package com.jack.notifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MyFloatMenu {
    private static final String TAG = MyFloatMenu.class.getSimpleName();

    /**
     * 為了攔截事件而建立中間層
     */
    private class Container extends LinearLayout {
        private boolean moving = false;

        public Container(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
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
                    moving = true;
                    return true;
                case MotionEvent.ACTION_UP:
                    if (moving) {
                        moving = false;
                        return true;
                    }
                    break;
            }

            return super.dispatchTouchEvent(event);
        }
    }

    private WindowManager windowManager;
    private Container container;
    private WindowManager.LayoutParams layoutParams;
    private int prevX, prevY;
    private float startX, startY, currX, currY;

    public MyFloatMenu(final Context context) {
        container = new Container(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menu = inflater.inflate(R.layout.float_menu, container);
        menu.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.stopService(new Intent(context, EmptyService.class));
            }
        });

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = LayoutParams.WRAP_CONTENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        if (!container.isShown()) {
            windowManager.addView(container, layoutParams);
        }
    }

    public void dismiss() {
        if (container.isShown()) {
            windowManager.removeView(container);
        }
    }

    private void updatePosition() {
        layoutParams.x = (int)(prevX - startX + currX);
        layoutParams.y = (int)(prevY - startY + currY);
        windowManager.updateViewLayout(container, layoutParams);
    }
}
