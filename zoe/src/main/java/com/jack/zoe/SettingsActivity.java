package com.jack.zoe;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;

import com.jack.zoe.preference.SeekBarPreference;

import java.util.List;
import java.util.Set;

public class SettingsActivity extends PreferenceActivity {

    private Settings settings;
    private Settings.Roaring roaring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = Settings.getInstance(this);
        this.roaring = this.settings.roaring;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.pref_general);

        this.setupRoaring();
    }

    private void setupRoaring() {
        this.setupRoaringEnabled();
        this.setupRoaringAlert();
        this.setupRoaringVolume();
    }

    private void setupRoaringEnabled() {
        CheckBoxPreference preference = (CheckBoxPreference)this.findPreference("roaring_enabled");
        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean enabled = (Boolean)newValue;
                roaring.set_enabled(enabled);
                return true;
            }
        };
        preference.setOnPreferenceChangeListener(changeListener);

        boolean enabled = roaring.get_enabled();
        preference.setDefaultValue(enabled);
    }

    private void setupRoaringAlert() {
        RingtonePreference preference = (RingtonePreference)this.findPreference("roaring_alert");
        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String alert = (String)newValue;
                Context context = preference.getContext();
                Ringtone ringtone = RingtoneManager.getRingtone(context, Uri.parse(alert));
                if (ringtone == null) {
                    preference.setSummary("有錯誤!");
                    return false;
                } else {
                    String title = ringtone.getTitle(context);
                    preference.setSummary(title);
                    roaring.set_alert(alert);
                    return true;
                }
            }
        };
        preference.setOnPreferenceChangeListener(changeListener);

        String alert = roaring.get_alert();
        preference.setDefaultValue(alert);
        changeListener.onPreferenceChange(preference, alert);
    }

    private void setupRoaringVolume() {
        SeekBarPreference preference = (SeekBarPreference)this.findPreference("roaring_volume");
        Preference.OnPreferenceChangeListener changeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int volume = (Integer)newValue;
                roaring.set_volume(volume);
                return true;
            }
        };
        preference.setOnPreferenceChangeListener(changeListener);

        int volume = roaring.get_volume();
        preference.setDefaultValue(volume);
    }
}
