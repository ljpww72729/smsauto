package com.ljpww72729.smsauto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_PHONE_STATE};
    private TextView phoneNum;
    private Button setting;
    public static final int PERMISSION_CODE = 10001;
    public static final int FOR_RESULT_CODE = 10002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneNum = findViewById(R.id.phone_num);
        setting = findViewById(R.id.setting);
        if (!PermissionsUtils.permissionsAlreadyGranted(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        } else {
            checkPhoneNumber();
        }
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SetPhoneNumActivity.class);
                startActivityForResult(intent, FOR_RESULT_CODE);
            }
        });
        JobManager.scheduleJob(this);
    }

    @SuppressLint("MissingPermission")
    private void checkPhoneNumber() {
        // 判断是否获取到本机手机号
        String phoneNumber = WilddogSyncManager.getPhoneNumber(this);
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNum.setText(R.string.no_phone_number);
            setting.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, SetPhoneNumActivity.class);
            startActivityForResult(intent, FOR_RESULT_CODE);
        } else {
            setting.setVisibility(View.GONE);
            phoneNum.setText(phoneNumber);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                checkPhoneNumber();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FOR_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                setting.setVisibility(View.GONE);
                String phoneNumber = data.getStringExtra(ConstantsStr.PHONE_NUMBER);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                sharedPreferences.edit().putString(ConstantsStr.PHONE_NUMBER, phoneNumber).apply();
                phoneNum.setText(phoneNumber);
            } else {
                setting.setVisibility(View.VISIBLE);
                Toast.makeText(this, "无手机号无法同步数据！", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
