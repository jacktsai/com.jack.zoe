package com.jack.zoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import com.jack.zoe.util.StringUtil;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RoaringPreferences {
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
        this.enabled = preferences.getBoolean("enabled", true);
        this.ringtone = Uri.parse(preferences.getString("ringtone", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()));

        this.notificationIdMap.clear();
        String[] packageNames = preferences.getString("packageNames", "").split(",");
        if (packageNames.length > 0) {
            for (String packageName : packageNames) {
                this.notificationIdMap.put(packageName, new ArrayList<String>());
            }
        } else {
            this.notificationIdMap.put("com.jack.notifier", new ArrayList<String>());
            this.notificationIdMap.put("com.madhead.tos.zh", new ArrayList<String>());
        }
    }

    public void save() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.putBoolean("enabled", this.enabled);
        editor.putString("ringtone", this.ringtone.toString());

        String packageNames = StringUtil.join(this.notificationIdMap.keySet(), ",");
        editor.putString("packageNames", packageNames);

        editor.commit();
    }
}
