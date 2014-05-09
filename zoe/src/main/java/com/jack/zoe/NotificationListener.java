package com.jack.zoe;

import android.app.Notification;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.Time;
import android.widget.RemoteViews;

import com.jack.zoe.util.*;

public class NotificationListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        this.printNotification("onNotificationPosted", sbn);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
//        this.printNotification("onNotificationRemoved", sbn);
    }

    private void printNotification(String eventName, StatusBarNotification sbn) {
        Notification n = sbn.getNotification();
        Time notifyTime = new Time();
        notifyTime.set(n.when);

        J.d("=== [%s] ===\n", eventName);
        J.d("Package Name: %s\n", sbn.getPackageName());
        J.d("Notify Time: %s\n", notifyTime.format("%m.%d %H:%M:%S"));
        J.d("TickerText: %s\n", n.tickerText);

        if (n.tickerView != null) {
            J.d("--- tickerView ---");
            printRemoteViews(n.tickerView);
        }
        if (n.contentView != null) {
            J.d("--- contentView ---");
            printRemoteViews(n.contentView);
        }
        if (n.bigContentView != null) {
            J.d("--- bigContentView ---");
            printRemoteViews(n.bigContentView);
        }
    }

    private void printRemoteViews(RemoteViews target) {
        J.d("Package:%s", target.getPackage());
        J.d("Layout:%d", target.getLayoutId());

        try {
            RemoteViewsWrapper wrapper = new RemoteViewsWrapper(target);
            for (ActionWrapper action : wrapper.getActionWrappers()) {
                J.d("Action:%s", action.getClassName());
                J.d("__viewId=%d\n", action.getViewId());

                if (action instanceof ReflectionActionWrapper) {
                    ReflectionActionWrapper reflectionAction = (ReflectionActionWrapper)action;
                    J.d("__methodName=%s\n", reflectionAction.getMethodName());
                    J.d("__value=%s\n", reflectionAction.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
