package com.jack.notifier;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MaskView extends View {
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public MaskView(Context context) {
        super(context);

        setBackgroundColor(0x4c000000);

        Point realSize = new Point();

        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(realSize);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = realSize.x;
        layoutParams.height = realSize.y;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.format = PixelFormat.TRANSLUCENT;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void show() {
        if (!isShown()) {
            windowManager.addView(this, layoutParams);
        }
    }

    public void update() {
        if (isShown()) {
            windowManager.updateViewLayout(this, layoutParams);
        }
    }

    public void hide() {
        if (isShown()) {
            windowManager.removeView(this);
        }
    }

    private int getStatusBarHeight() {
        Resources resources = getContext().getResources();
        int i = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (i > 0) {
            return resources.getDimensionPixelSize(i);
        }

        return 0;
    }

    private int getNavigationBarHeight() {
        Resources resources = getContext().getResources();
        int i = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (i > 0) {
            return resources.getDimensionPixelSize(i);
        }

        return 0;
    }
}
