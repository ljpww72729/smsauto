package com.ljpww72729.smsauto;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.OnDisconnect;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;

/**
 * Created by LinkedME06 on 1/28/18.
 */

public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化
        WilddogOptions options = new WilddogOptions.Builder().setSyncUrl("https://wd8078052585upgyqs.wilddogio.com").build();
        WilddogApp.initializeApp(this, options);
        //创建一个 SyncReference 实例
        //创建电话管理
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            final String phoneNum = tm.getLine1Number();
            if (!TextUtils.isEmpty(phoneNum)) {
                // 监听连接状态
                SyncReference connectedRef = WilddogSync.getInstance().getReference(".info/connected");
                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connected = (boolean) dataSnapshot.getValue(Boolean.class);
                        if (connected) {
                            SyncReference presenceRef = WilddogSync.getInstance().getReference("smsauto/" + phoneNum + "/connected");
                            presenceRef.setValue("online");
                            OnDisconnect onDisconnectRef = presenceRef.onDisconnect();
                            onDisconnectRef.setValue("offline");
                        }
                    }

                    @Override
                    public void onCancelled(SyncError syncError) {
                    }
                });
            }
        }

    }
}
