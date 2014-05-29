package com.jack.notifier;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.jack.notifier.util.J;

public class MaskViewSetting {
    private static final String TAG = MaskViewSetting.class.getSimpleName();

    public MaskViewSetting(final Context context, final MaskView maskView) {
        final WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = layoutInflater.inflate(R.layout.mask_view_setting, null);

        Button close = (Button)rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(rootView);
            }
        });

        final SeekBar alpha = (SeekBar)rootView.findViewById(R.id.alpha);
        final SeekBar red = (SeekBar)rootView.findViewById(R.id.red);
        final SeekBar green = (SeekBar)rootView.findViewById(R.id.green);
        final SeekBar blue = (SeekBar)rootView.findViewById(R.id.blue);
        final CheckBox layout_in_screen = (CheckBox)rootView.findViewById(R.id.layout_in_screen);
        final CheckBox layout_inset_decor = (CheckBox)rootView.findViewById(R.id.layout_inset_decor);

        ColorDrawable colorDrawable = (ColorDrawable)maskView.getBackground();
        int color = colorDrawable.getColor();
        alpha.setProgress(Color.alpha(color));
        red.setProgress(Color.red(color));
        green.setProgress(Color.green(color));
        blue.setProgress(Color.blue(color));

        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color = Color.argb(alpha.getProgress(), red.getProgress(), green.getProgress(), blue.getProgress());
                maskView.setBackgroundColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        alpha.setOnSeekBarChangeListener(onSeekBarChangeListener);
        red.setOnSeekBarChangeListener(onSeekBarChangeListener);
        green.setOnSeekBarChangeListener(onSeekBarChangeListener);
        blue.setOnSeekBarChangeListener(onSeekBarChangeListener);

        final LayoutParams maskViewLayoutParams = maskView.getLayoutParams();
        layout_in_screen.setChecked((maskViewLayoutParams.flags & LayoutParams.FLAG_LAYOUT_IN_SCREEN) != 0);
        layout_in_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int flags = maskViewLayoutParams.flags;
                if (isChecked) {
                    flags = flags | LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                } else {
                    flags = flags & ~LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                }

                J.d(TAG, "mask view flags, old 0x%x, new 0x%x", maskViewLayoutParams.flags, flags);
                maskViewLayoutParams.flags = flags;
                maskView.update();
            }
        });
        layout_inset_decor.setChecked((maskViewLayoutParams.flags & LayoutParams.FLAG_LAYOUT_INSET_DECOR) != 0);
        layout_inset_decor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int flags = maskViewLayoutParams.flags;
                if (isChecked) {
                    flags = flags | LayoutParams.FLAG_LAYOUT_INSET_DECOR;
                } else {
                    flags = flags & ~LayoutParams.FLAG_LAYOUT_INSET_DECOR;
                }

                J.d(TAG, "mask view flags, old 0x%x, new 0x%x", maskViewLayoutParams.flags, flags);
                maskViewLayoutParams.flags = flags;
                maskView.update();
            }
        });

        LayoutParams layoutParams = new LayoutParams();
        layoutParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;

        windowManager.addView(rootView, layoutParams);
    }
}
