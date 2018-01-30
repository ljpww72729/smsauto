package com.ljpww72729.smsauto;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!PermissionsUtils.permissionsAlreadyGranted(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, 10001);
        }
        JobManager.scheduleJob(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
