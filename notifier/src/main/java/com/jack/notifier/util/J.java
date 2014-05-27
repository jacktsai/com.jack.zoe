package com.jack.notifier.util;

import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class J {

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
}

