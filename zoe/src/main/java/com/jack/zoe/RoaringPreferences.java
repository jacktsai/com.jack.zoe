package com.jack.zoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import com.jack.zoe.util.J;
import com.jack.zoe.util.StringUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RoaringPreferences {
    private static final String TAG = RoaringPreferences.class.getSimpleName();

    private static RoaringPreferences sharedInstance;

    public static RoaringPreferences createInstance(Context context) {
        synchronized (RoaringPreferences.class) {
            sharedInstance = new RoaringPreferences(context);
            return sharedInstance;
        }
    }

    public static RoaringPreferences getInstance() {
        return sharedInstance;
    }

    private final SharedPreferences preferences;

    public boolean enabled;
    public Uri ringtone;
    public int volume;
    public Hashtable<String, List<String>> notificationIdMap = new Hashtable<String, List<String>>();

    RoaringPreferences(Context context) {
        this.preferences = context.getSharedPreferences(this.getClass().getName(), Context.MODE_PRIVATE);
        this.reload();
    }

    @Override
    protected void finalize() throws Throwable {
        this.save();
        super.finalize();
    }

    public void reload() {
        J.d(TAG, "reload");

        this.enabled = preferences.getBoolean("enabled", true);
        this.ringtone = Uri.parse(preferences.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));
        this.volume = preferences.getInt("volume", 5);

        this.notificationIdMap.clear();
        String packageNames = preferences.getString("packageNames", "com.jack.notifier,com.madhead.tos.zh");
        for (String packageName : packageNames.split(",")) {
            J.d(TAG, "package name '%s' is added", packageName);
            this.notificationIdMap.put(packageName, new ArrayList<String>());
        }
    }

    public void save() {
        J.d(TAG, "save");

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.putBoolean("enabled", this.enabled);
        editor.putString("ringtone", this.ringtone.toString());
        editor.putInt("volume", this.volume);

        String packageNames = StringUtil.join(this.notificationIdMap.keySet(), ",");
        editor.putString("packageNames", packageNames);

        editor.commit();
    }
}
