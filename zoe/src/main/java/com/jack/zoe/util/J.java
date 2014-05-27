package com.jack.zoe.util;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class J {

    private static final String TAG = J.class.getSimpleName();

    public static void v(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.v(tag, message);
    }

    public static void d(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.d(tag, message);
    }

    public static void i(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.i(tag, message);
    }

    public static void w(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.w(tag, message);
    }

    public static void e(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.e(tag, message);
    }

    public static void a(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.wtf(tag, message);
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
                d(TAG, "[<%d> %s] %s.%s [%s #%d]", t.getId(), t.getName(), e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
                firstPrint = false;
            } else {
                d(TAG, "-> %s.%s [%s #%d]", e.getClassName(), e.getMethodName(), e.getFileName(), e.getLineNumber());
            }
        }
    }

    public static void log(String format, Object... args) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalDir = Environment.getExternalStorageDirectory();
            File zoeDir = new File(externalDir, "com.jack.zoe");

            synchronized (J.class) {
                if (!zoeDir.exists()) {
                    if (!zoeDir.mkdir()) {
                        e(TAG, "failed to create directory %s", zoeDir.getPath());
                        return;
                    }
                }

                Time now = new Time();
                now.setToNow();

                File logFile = new File(zoeDir, now.format("%y%m%d") + ".log");
                String message = String.format(format, args);
                String line = String.format("%s %s\n", now.format("%H:%M:%S"), message);

                try {
                    FileWriter writer = new FileWriter(logFile, logFile.exists());
                    writer.write(line);
                    writer.flush();
                    writer.close();

                    d(TAG, message);
                }
                catch (Exception e) {
                    e(TAG, "failed to save file to %s", logFile.getPath());
                    e.printStackTrace();
                }
            }
        }
    }
}

