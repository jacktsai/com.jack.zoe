package com.jack.notifier;

import android.content.Context;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

public class MyFloatView extends ImageView {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    private float x, y;

    public MyFloatView(Context context) {
        super(context);

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
    }
}
