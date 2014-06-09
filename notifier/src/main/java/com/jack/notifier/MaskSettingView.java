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
import android.widget.TextView;

import com.jack.notifier.util.J;

public class MaskSettingView {
    private static final String TAG = MaskSettingView.class.getSimpleName();

    public MaskSettingView(final Context context, final MaskView maskView) {
        final WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        final LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = layoutInflater.inflate(R.layout.view_mask_setting, null);

        Button close = (Button)rootView.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(rootView);
            }
        });

        final SeekBar alpha = (SeekBar)rootView.findViewById(R.id.alpha);
        final TextView alphaValue = (TextView)rootView.findViewById(R.id.alphaValue);
        final SeekBar red = (SeekBar)rootView.findViewById(R.id.red);
        final TextView redValue = (TextView)rootView.findViewById(R.id.redValue);
        final SeekBar green = (SeekBar)rootView.findViewById(R.id.green);
        final TextView greenValue = (TextView)rootView.findViewById(R.id.greenValue);
        final SeekBar blue = (SeekBar)rootView.findViewById(R.id.blue);
        final TextView blueValue = (TextView)rootView.findViewById(R.id.blueValue);

        SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar == alpha) {
                    alphaValue.setText(Integer.toString(progress));
                } else if (seekBar == red) {
                    redValue.setText(Integer.toString(progress));
                } else if (seekBar == green) {
                    greenValue.setText(Integer.toString(progress));
                } else if (seekBar == blue) {
                    blueValue.setText(Integer.toString(progress));
                }

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

        ColorDrawable colorDrawable = (ColorDrawable)maskView.getBackground();
        int color = colorDrawable.getColor();
        alpha.setProgress(Color.alpha(color));
        red.setProgress(Color.red(color));
        green.setProgress(Color.green(color));
        blue.setProgress(Color.blue(color));

        int maskViewFlags = maskView.getLayoutParams().flags;

        CheckBox layout_in_screen = (CheckBox)rootView.findViewById(R.id.layout_in_screen);
        layout_in_screen.setChecked((maskViewFlags & LayoutParams.FLAG_LAYOUT_IN_SCREEN) != 0);
        layout_in_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMaskViewFlag(maskView, LayoutParams.FLAG_LAYOUT_IN_SCREEN, isChecked);
            }
        });

        CheckBox fullscreen = (CheckBox)rootView.findViewById(R.id.fullscreen);
        fullscreen.setChecked((maskViewFlags & LayoutParams.FLAG_FULLSCREEN) != 0);
        fullscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMaskViewFlag(maskView, LayoutParams.FLAG_FULLSCREEN, isChecked);
            }
        });

        CheckBox layout_inset_decor = (CheckBox)rootView.findViewById(R.id.layout_inset_decor);
        layout_inset_decor.setChecked((maskViewFlags & LayoutParams.FLAG_LAYOUT_INSET_DECOR) != 0);
        layout_inset_decor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateMaskViewFlag(maskView, LayoutParams.FLAG_LAYOUT_INSET_DECOR, isChecked);
            }
        });

        LayoutParams layoutParams = new LayoutParams();
        layoutParams.type = LayoutParams.TYPE_PHONE;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;

        windowManager.addView(rootView, layoutParams);
    }

    private static void updateMaskViewFlag(MaskView maskView, int targetFlag, boolean isChecked) {
        LayoutParams layoutParams = maskView.getLayoutParams();
        int flags = layoutParams.flags;

        if (isChecked) {
            flags = flags | targetFlag;
        } else {
            flags = flags & ~targetFlag;
        }

        J.d(TAG, "mask view flags, old 0x%x, new 0x%x", layoutParams.flags, flags);
        layoutParams.flags = flags;
        maskView.update();
    }
}
