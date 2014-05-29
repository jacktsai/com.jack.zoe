package com.jack.notifier;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.jack.notifier.util.J;

public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getSimpleName();

    public static final int MSG_EXIT_CLICK = 1;
    public static final int MSG_SETTING_CLICK = 2;
    public static final int MSG_ACTION_TOGGLE = 3;
    public static final int MSG_MODE_TOGGLE = 4;

    private String currMode = "M";
    private boolean running = false;
    private FloatingMenu floatingMenu;
    private InformView informView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            J.d(TAG, "handleMessage, what = %x", msg.what);
            switch (msg.what) {
                case MSG_EXIT_CLICK:
                    J.i(TAG, "message [MSG_EXIT_CLICK] received");
                    onExitClick();
                    break;
                case MSG_SETTING_CLICK:
                    J.i(TAG, "message [MSG_SETTING_CLICK] received");
                    onSettingClick();
                    break;
                case MSG_ACTION_TOGGLE:
                    J.i(TAG, "message [MSG_ACTION_TOGGLE] received");
                    onActionToggle();
                    break;
                case MSG_MODE_TOGGLE:
                    J.i(TAG, "message [MSG_MODE_TOGGLE] received");
                    onModeToggle();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        J.d(TAG, "onCreate");
        floatingMenu = new FloatingMenu(this, handler);
        floatingMenu.updateMode(currMode);
        floatingMenu.updateAction(running);

        informView = new InformView(this);
        J.i(TAG, "service [%s] created", this.getClass().getName());
    }

    @Override
    public void onDestroy() {
        J.d(TAG, "onDestroy");
        floatingMenu.onDestroy();
        J.i(TAG, "service [%s] destroyed", this.getClass().getName());
    }

    @Override
    public IBinder onBind(Intent intent) {
        J.d(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        J.d(TAG, "onStartCommand, intent = %s, flags = %x, startId = %d", intent.toString(), flags, startId);
        //int result = super.onStartCommand(intent, flags, startId);
        int result = Service.START_REDELIVER_INTENT;
        switch (result) {
            case Service.START_STICKY_COMPATIBILITY:
                J.d(TAG, "onStartCommand return START_STICKY_COMPATIBILITY");
                break;
            case Service.START_STICKY:
                J.d(TAG, "onStartCommand return START_STICKY");
                break;
            case Service.START_NOT_STICKY:
                J.d(TAG, "onStartCommand return START_NOT_STICKY");
                break;
            case Service.START_REDELIVER_INTENT:
                J.d(TAG, "onStartCommand return START_REDELIVER_INTENT");
                break;
        }

        return result;
    }

    private void onExitClick() {
        if (running) {
            onActionToggle();
        }

        stopSelf();
    }

    private void onSettingClick() {
    }

    private void onModeToggle() {
        if (currMode.equals("A")) {
            currMode = "M";
        } else if (currMode.equals("M")) {
            currMode = "A";
        }

        floatingMenu.updateMode(currMode);
    }

    private void onActionToggle() {
        if (running) {
            onStopAction();
            running = false;
        } else {
            onStartAction();
            running = true;
        }

        floatingMenu.updateAction(running);
    }

    private void onStartAction() {
        informView.show();
    }

    private void onStopAction() {
        informView.dismiss();
    }
}
