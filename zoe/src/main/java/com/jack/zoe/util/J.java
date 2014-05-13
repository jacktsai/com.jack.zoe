package com.jack.zoe.util;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class J {

    private static final String DEFAULT_TAG = "JackZoe";

    public static void d(String format, Object... args) {
        String message = String.format(format, args);
        Log.d(DEFAULT_TAG, message);
    }

    public static void d2(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.d(tag, message);
    }

    public static void e(String format, Object... args) {
        String message = String.format(format, args);
        Log.e(DEFAULT_TAG, message);
    }

    public static void e2(String tag, String format, Object... args) {
        String message = String.format(format, args);
        Log.e(tag, message);
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

    public static void log(String format, Object... args) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalDir = Environment.getExternalStorageDirectory();
            File zoeDir = new File(externalDir, "com.jack.zoe");

            synchronized (J.class) {
                if (!zoeDir.exists()) {
                    if (!zoeDir.mkdir()) {
                        e("failed to create directory %s", zoeDir.getPath());
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

                    d(message);
                }
                catch (Exception e) {
                    e("failed to save file to %s", logFile.getPath());
                    e.printStackTrace();
                }
            }
        }
    }
}

