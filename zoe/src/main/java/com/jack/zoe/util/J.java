package com.jack.zoe.util;

import android.os.Environment;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class J {

    private static final String TAG = "JackZoe";

    public static void d(String format, Object... args) {
        String message = String.format(format, args);
        Log.d(TAG, message);
    }

    public static void printStackTrace() {
        Thread t = Thread.currentThread();
        StackTraceElement[] elements = t.getStackTrace();

        boolean firstPrint = true;
        for (StackTraceElement e : elements) {
            if (e.getMethodName().contains("StackTrace")) {
                continue;
            }

            if (firstPrint) {
                d("[<%d> %s] %s.%s [%s #%d]", t.getId(), t.getName(), e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
                firstPrint = false;
            } else {
                d("-> %s.%s [%s #%d]", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
            }
        }
    }

    private void logNotification(String eventName, StatusBarNotification sbn) {
        J.d("External Storage State: %s", Environment.getExternalStorageState());

        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {

        }
    }
}

