package com.jack.zoe.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.jack.zoe.GalleryPicker;
import com.jack.zoe.util.J;

public class GalleryPreference extends Preference implements PreferenceManager.OnActivityResultListener {
    private static final String TAG = GalleryPreference.class.getSimpleName();

    private int requestCode;

    public GalleryPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GalleryPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryPreference(Context context) {
        this(context, null);
    }

    @Override
    protected void onClick() {
        Activity activity = (Activity)this.getContext();
        Intent intent = new Intent(activity, GalleryPicker.class);
        activity.startActivityForResult(intent, this.requestCode);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String bucketName = data.getStringExtra(GalleryPicker.EXTRA_GALLERY_PICKED_BUCKET_NAME);
                    J.d(TAG, "selected bucket name is '%s'", bucketName);
                    if (this.callChangeListener(bucketName)) {
                        this.persistString(bucketName);
                    }
                    this.setSummary(bucketName);
                }
            }
        }
        return false;
    }

    @Override
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        this.registerOnActivityResultListener(preferenceManager);

        String bucketName = this.getPersistedString("");
        if (TextUtils.isEmpty(bucketName)) {
            this.setSummary("<無>");
        } else {
            this.setSummary(bucketName);
        }
    }

    private void registerOnActivityResultListener(PreferenceManager preferenceManager) {
        Class<?> pmClass = PreferenceManager.class;
        try {
            pmClass.getDeclaredMethod("registerOnActivityResultListener", PreferenceManager.OnActivityResultListener.class).invoke(preferenceManager, this);
            this.requestCode = (Integer)pmClass.getDeclaredMethod("getNextRequestCode").invoke(preferenceManager);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
