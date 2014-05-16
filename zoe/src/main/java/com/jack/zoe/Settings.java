package com.jack.zoe;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.jack.zoe.util.J;

import java.util.HashSet;
import java.util.Set;

public class Settings {

    private static Settings sharedInstance;

    public static Settings getInstance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new Settings(context);
        }

        return sharedInstance;
    }

    public static class Roaring {
        private static final String TAG = Settings.class.getSimpleName();

        public interface OnChangeListener {
            void onEnabledChange(boolean enabled);
            void onAlertChange(String alert);
            void onVolumeChange(int volume);
            void onPackageNamesChange(Set<String> package_names);
        }

        private OnChangeListener onChangeListener;
        private boolean enabled;
        private String alert;
        private int volume;
        private Set<String> packageNames;

        private Roaring(Context context) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            this.enabled = preferences.getBoolean("roaring_enabled", false);
            this.alert = preferences.getString("roaring_alert", RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());
            this.volume = preferences.getInt("roaring_volume", 5);

            Set<String> packageNames = preferences.getStringSet("roaring_package_names", null);
            if (packageNames == null) {
                packageNames = new HashSet<String>();
                packageNames.add("com.jack.notifier");
                packageNames.add("com.madhead.tos.zh");
            }
            this.packageNames = packageNames;
        }

        public void setOnChangeListener(OnChangeListener onChangeListener) {
            this.onChangeListener = onChangeListener;
        }

        public boolean get_enabled() {
            return this.enabled;
        }

        public void set_enabled(boolean enabled) {
            if (this.enabled != enabled) {
                J.d(TAG, "enabled change to %s", Boolean.toString(enabled));
                this.enabled = enabled;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onEnabledChange(enabled);
                }
            }
        }

        public String get_alert() {
            return this.alert;
        }

        public void set_alert(String alert) {
            if (this.alert != alert) {
                J.d(TAG, "alert change to %s", alert);
                this.alert = alert;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onAlertChange(alert);
                }
            }
        }

        public int get_volume() {
            return this.volume;
        }

        public void set_volume(int volume) {
            if (this.volume != volume) {
                J.d(TAG, "volume change to %d", volume);
                this.volume = volume;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onVolumeChange(volume);
                }
            }
        }

        public Set<String> get_package_names() {
            return this.packageNames;
        }

        public void set_package_names(Set<String> package_names) {
            String a = TextUtils.join(",", package_names);
            String b = TextUtils.join(",", this.packageNames);
            if (!a.equals(b)) {
                J.d(TAG, "package names change to %d", a);
                this.packageNames = package_names;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onPackageNamesChange(package_names);
                }
            }
        }
    }

    public final Roaring roaring;

    private Settings(Context context) {
        roaring = new Roaring(context);
    }
}
