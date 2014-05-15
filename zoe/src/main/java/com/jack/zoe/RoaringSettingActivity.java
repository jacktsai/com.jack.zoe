package com.jack.zoe;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;


public class RoaringSettingActivity extends Activity {

    private RoaringPreferences preferences;
    private CheckBox enabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_roaring_settings);

        preferences = RoaringPreferences.getInstance();
        enabled = (CheckBox)this.findViewById(R.id.enabled);
        enabled.setChecked(preferences.enabled);
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.enabled = isChecked;
            }
        });

    }

    @Override
    protected void onDestroy() {
        preferences.save();

        super.onDestroy();
    }
}
