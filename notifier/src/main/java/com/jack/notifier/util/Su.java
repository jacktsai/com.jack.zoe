package com.jack.notifier.util;

import android.util.Log;

import java.io.DataOutputStream;

public class Su {
    private static final String TAG = Su.class.getSimpleName();

    public static boolean chmod(String filePath, int mode) {
        int result = -1;
        try {
            Log.d(TAG, String.format("try to chmod %s to %d", filePath, mode));
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(String.format("chmod %d %s\n", mode, filePath));
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            outputStream.close();
            result = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == 0) {
            Log.i(TAG, String.format("chmod %s %d succeed", filePath, mode));
            return true;
        } else {
            Log.i(TAG, String.format("chmod %s %d failed (%x)", filePath, mode, result));
            return false;
        }
    }
}
