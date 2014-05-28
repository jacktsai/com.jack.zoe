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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.jack.notifier.util.J;

public class EmptyService extends Service {
    private static final String TAG = EmptyService.class.getSimpleName();

    private MyFloatMenu floatMenu;

    @Override
    public void onCreate() {
        J.i(TAG, "onCreate");
        floatMenu = new MyFloatMenu(this);
    }

    @Override
    public void onDestroy() {
        J.i(TAG, "onDestroy");
        floatMenu.dismiss();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        J.i(TAG, "onStartCommand, flags=%d, startId=%d", flags, startId);
        floatMenu.show();
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
