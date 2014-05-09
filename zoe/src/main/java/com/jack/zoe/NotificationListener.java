package com.jack.zoe;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = NotificationListener.class.getSimpleName();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        this.logNotification("onNotificationPosted", sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        this.logNotification("onNotificationRemoved", sbn);
    }

    private void logNotification(String eventName, StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        Time notifyTime = new Time();
        notifyTime.set(n.when);

        Log.i(TAG, String.format("=== [%s] ===\n", eventName));
        Log.i(TAG, String.format("Package Name: %s\n", sbn.getPackageName()));
        Log.i(TAG, String.format("Notify Time: %s\n", notifyTime.format("%m.%d %H:%M:%S")));
        Log.i(TAG, String.format("TickerText: %s\n", n.tickerText));
//        Log.i(TAG, String.format("Content Intent: %s\n", n.contentIntent));
//        Log.i(TAG, String.format("Delete Intent: %s\n", n.deleteIntent));
//        Log.i(TAG, String.format("Full Screen Intent: %s\n", n.fullScreenIntent));

        if (n.tickerView != null) {
            Log.i(TAG, String.format("--- tickerView ---"));
            this.logRemoteViews(n.tickerView);
        }
        if (n.contentView != null) {
            Log.i(TAG, String.format("--- contentView ---"));
            this.logRemoteViews(n.contentView);
        }
        if (n.bigContentView != null) {
            Log.i(TAG, String.format("--- bigContentView ---"));
            this.logRemoteViews(n.bigContentView);
        }
    }

    private void logRemoteViews(RemoteViews target) {
        Log.i(TAG, String.format("Package:%s Layout:%d", target.getPackage(), target.getLayoutId()));

        try {

            Object mActions = target.getClass().getDeclaredField("mActions").get(target);
            Object[] array = (Object[])mActions.getClass().getDeclaredField("array").get(mActions);
            for (int i = 0; i < array.length; i++) {
                Object action = array[i];
                Log.i(TAG, String.format("Action #%d [%s]", i + 1, action.getClass().getName()));
                this.logActionClass(action);
                this.logReflectionActionClass(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logActionClass(Object action) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> classAction = Class.forName("android.widget.RemoteViews$Action");

        if (classAction.isAssignableFrom(action.getClass())) {
            int viewId = classAction.getDeclaredField("viewId").getInt(action);
            Log.i(TAG, String.format("__[%s] viewId=%d\n", classAction.getSimpleName(), viewId));
        }
    }

    private void logReflectionActionClass(Object action) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> classReflectionAction = Class.forName("android.widget.RemoteViews$ReflectionAction");

        if (classReflectionAction.isAssignableFrom(action.getClass())) {
            String methodName = (String)classReflectionAction.getDeclaredField("methodName").get(action);
            Object value = classReflectionAction.getDeclaredField("value").get(action);
            Log.i(TAG, String.format("__[%s] %s %s\n", classReflectionAction.getSimpleName(), methodName, value));
        }
    }
}
