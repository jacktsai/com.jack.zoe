package com.jack.zoe.util;

import java.io.DataOutputStream;

public class Su {
    private static final String TAG = Su.class.getSimpleName();

    public static boolean chmod(String filePath, int mode) {
        int result = -1;
        try {
            J.d(TAG, "try to chmod %s to %d", filePath, mode);
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
            J.i(TAG, "chmod %s %d succeed", filePath, mode);
            return true;
        } else {
            J.i(TAG, "chmod %s %d failed (%x)", filePath, mode, result);
            return false;
        }
    }
}
