package com.jack.notifier;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.jack.notifier.util.J;

public class EmptyService extends Service {
    private static final String TAG = EmptyService.class.getSimpleName();

    private MyFloatView floatView;
    private View menu;

    @Override
    public void onCreate() {
        J.i(TAG, "onCreate");

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        menu = inflater.inflate(R.layout.float_menu, null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = 100;
        layoutParams.height = 100;

        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(menu, layoutParams);

//        floatView = new MyFloatView(this);
    }

    @Override
    public void onDestroy() {
        J.i(TAG, "onDestroy");
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(menu);
//        floatView.dismiss();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        J.i(TAG, "onStartCommand, flags=%d, startId=%d", flags, startId);

//        if (startId == 1) {
//            floatView.show();
//        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        J.i(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        J.i(TAG, "onBind");
        return new MyBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        J.i(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        J.i(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    class MyBinder extends Binder {

    }
}
