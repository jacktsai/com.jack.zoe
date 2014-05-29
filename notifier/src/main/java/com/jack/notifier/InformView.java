package com.jack.notifier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class InformView extends TextView {
    private static final String TAG = InformView.class.getSimpleName();

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public InformView(Context context) {
        super(context);

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = size.x;
        layoutParams.height = (int)(size.y * 0.4532);

        setText("運算中…");
        setTextSize(44);
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setBackgroundColor(0x4c000000);
    }

    public void show() {
        windowManager.addView(this, layoutParams);
    }

    public void dismiss() {
        windowManager.removeView(this);
    }
}
