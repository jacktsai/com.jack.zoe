package com.jack.notifier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class InformUI extends TextView {
    private static final String TAG = InformUI.class.getSimpleName();

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public InformUI(Context context) {
        super(context);

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.height = (int)(displayMetrics.heightPixels * 0.4532);

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
