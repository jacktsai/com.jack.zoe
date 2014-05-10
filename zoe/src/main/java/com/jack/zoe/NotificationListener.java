package com.jack.zoe;

import android.app.Notification;
import android.media.MediaPlayer;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.format.Time;
import android.widget.RemoteViews;

import com.jack.zoe.util.*;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

public class NotificationListener extends NotificationListenerService {

    private MediaPlayer mediaPlayer;
    private List<String> nlsIds = new ArrayList<String>();

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        this.printNotification("onNotificationPosted", sbn);

        if (this.isSpecifiedTarget(sbn)) {

            if (nlsIds.isEmpty()) {
                mediaPlayer = MediaPlayer.create(super.getApplicationContext(), R.raw.alarm);
                mediaPlayer.setVolume(1, 1);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }

            String id = Integer.toString(sbn.getId());
            nlsIds.add(id);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String id = Integer.toString(sbn.getId());

        if (!nlsIds.isEmpty()) {

            if (nlsIds.remove(id) && nlsIds.isEmpty()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    private void printNotification(String eventName, StatusBarNotification sbn) {
        Notification n = sbn.getNotification();

        J.log("=== [%s] ===", eventName);
        J.log("ID: 0x%x", sbn.getId());
        J.log("Package Name: %s", sbn.getPackageName());
        J.log("TickerText: %s", n.tickerText);

        if (n.contentView != null) {
            printRemoteViews(n.contentView);
        }
    }

    private void printRemoteViews(RemoteViews target) {
//        J.log("Package: %s", target.getPackage());
//        J.log("Layout: %d", target.getLayoutId());

        try {
            RemoteViewsWrapper wrapper = new RemoteViewsWrapper(target);
            for (ActionWrapper action : wrapper.getActionWrappers()) {
//                J.log("Action: %s", action.getClassName());
//                J.log("__viewId=%d\n", action.getViewId());

                if (action instanceof ReflectionActionWrapper) {
                    ReflectionActionWrapper reflectionAction = (ReflectionActionWrapper)action;
//                    J.log("__methodName=%s\n", reflectionAction.getMethodName());
//                    J.log("__value=%s\n", reflectionAction.getValue());

                    String methodName = reflectionAction.getMethodName();
                    if (methodName.equals("setText"))
                    {
                        String viewName = null;
                        switch (action.getViewId()) {
                            case 0x1020016:
                                viewName = "ContentTitle";
                                break;
                            case 0x1020046:
                                viewName = "ContentText/SubText";
                                break;
                            case 0x10202dc:
                                viewName = "ContentInfo";
                                break;
                            case 0x1020015:
                                viewName = "ContentText";
                                break;
                        }

                        if (viewName != null) {
                            J.log("%s: %s", viewName, reflectionAction.getValue());
                        } else {
                            J.log("%s %s on 0x%x", methodName, reflectionAction.getValue(), reflectionAction.getViewId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSpecifiedTarget(StatusBarNotification sbn) {
        Notification n = sbn.getNotification();

        if (sbn.getPackageName().equals("com.jack.notifier") && n.contentView != null) {
            try {
                RemoteViewsWrapper wrapper = new RemoteViewsWrapper(n.contentView);

                for (ActionWrapper action : wrapper.getActionWrappers()) {
                    if (action instanceof ReflectionActionWrapper) {
                        ReflectionActionWrapper reflectionAction = (ReflectionActionWrapper)action;

                        if (reflectionAction.getViewId() == 0x1020016 && reflectionAction.getValue().equals("ContentTitle")) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
