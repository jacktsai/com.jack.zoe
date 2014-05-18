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

    public static class Sliding {
        private static final String TAG = Sliding.class.getSimpleName();

        public interface OnChangeListener {
            void onUseExternalChange(boolean use_external);
        }

        private OnChangeListener onChangeListener;
        private boolean useExternal;

        private Sliding(SharedPreferences preferences) {
            this.useExternal = preferences.getBoolean("use_external", false);
        }

        public void setOnChangeListener(OnChangeListener onChangeListener) {
            this.onChangeListener = onChangeListener;
        }

        public boolean get_use_external() {
            return this.useExternal;
        }

        public void set_use_external(boolean use_external) {
            if (this.useExternal != use_external) {
                J.d(TAG, "use external change to %s", Boolean.toString(use_external));
                this.useExternal = use_external;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onUseExternalChange(use_external);
                }
            }
        }
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

        private Roaring(SharedPreferences preferences) {
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

    public static class ToS {
        private static final String TAG = Settings.class.getSimpleName();

        public interface OnChangeListener {
            void onShowCurrentLootsChange(boolean enabled);
        }

        private OnChangeListener onChangeListener;
        private boolean showCurrentLoots;

        private ToS(SharedPreferences preferences) {
            this.showCurrentLoots = preferences.getBoolean("tos_show_current_loots", false);
        }

        public void setOnChangeListener(OnChangeListener onChangeListener) {
            this.onChangeListener = onChangeListener;
        }

        public boolean get_show_current_loots() {
            return this.showCurrentLoots;
        }

        public void set_show_current_loots(boolean showCurrentLoots) {
            if (this.showCurrentLoots != showCurrentLoots) {
                J.d(TAG, "showCurrentLoots change to %s", Boolean.toString(showCurrentLoots));
                this.showCurrentLoots = showCurrentLoots;
                if (this.onChangeListener != null) {
                    this.onChangeListener.onShowCurrentLootsChange(showCurrentLoots);
                }
            }
        }
    }

    public final Roaring roaring;
    public final Sliding sliding;
    public final ToS tos;

    private Settings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        roaring = new Roaring(preferences);
        sliding = new Sliding(preferences);
        tos = new ToS(preferences);
    }
}
