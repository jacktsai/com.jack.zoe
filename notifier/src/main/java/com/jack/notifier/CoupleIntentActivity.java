package com.jack.notifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CoupleIntentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_couple_intent);

        Intent intent = super.getIntent();
        String action = intent.getAction();

        Button button1 = (Button)super.findViewById(R.id.button1);
        button1.setOnClickListener(new ButtonOnClickListener());

        if (action == "android.intent.action.MAIN") {
            super.setTitle("Main");
            button1.setText("Go Action1");
        } else if (action == "com.jack.zoe.action1") {
            super.setTitle("Action1");
            button1.setText("Go Action2");
        } else if (action == "com.jack.zoe.action2") {
            super.setTitle("Action2");
            button1.setText("Go Action1");
        }
    }

    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String action = getIntent().getAction();
            if (action == "android.intent.action.MAIN") {
                startActivity(new Intent("com.jack.zoe.action1"));
            } else if (action == "com.jack.zoe.action1") {
                startActivity(new Intent("com.jack.zoe.action2"));
            } else if (action == "com.jack.zoe.action2") {
                startActivity(new Intent("com.jack.zoe.action1"));
            }
        }
    }
}
