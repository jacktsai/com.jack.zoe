package com.jack.notifier;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jack.notifier.util.J;

import java.util.logging.Logger;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        J.a(TAG, "onReceive, context = %s, intent = %s", context.toString(), intent.toString());
        context.startService(new Intent(context, MaskService.class));
    }
}
