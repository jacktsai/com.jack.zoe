package com.jack.notifier;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MyFloatMenu implements View.OnTouchListener {
    private static final String TAG = MyFloatMenu.class.getSimpleName();

    private WindowManager windowManager;
    private View view;
    private WindowManager.LayoutParams layoutParams;
    private int prevX, prevY;
    private float startX, startY, currX, currY;

    public MyFloatMenu(final Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.float_menu, null);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.stopService(new Intent(context, EmptyService.class));
            }
        });
        view.setOnTouchListener(this);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void show() {
        windowManager.addView(view, layoutParams);
    }

    public void dismiss() {
        windowManager.removeView(view);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
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

    private void updatePosition() {
        layoutParams.x = (int)(prevX - startX + currX);
        layoutParams.y = (int)(prevY - startY + currY);
        windowManager.updateViewLayout(view, layoutParams);
    }
}
