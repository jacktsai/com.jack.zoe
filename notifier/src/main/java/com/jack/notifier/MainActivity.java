package com.jack.notifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        Button createNotification = (Button)super.findViewById(R.id.createNotification);
        createNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Notification.Builder builder = new Notification.Builder(MainActivity.this);
                builder.setContentTitle("通知測試");
                builder.setContentText("測試用內容");
                builder.setTicker("這是一個通知測試，請慢慢享用～");
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setAutoCancel(false);

                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                manager.notify((int)System.currentTimeMillis(), builder.build());
            }
        });
    }
}
