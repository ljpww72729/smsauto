package com.ljpww72729.smsauto;

import android.Manifest;
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

/**
 * Created by LinkedME06 on 2/5/18.
 */

public class WilddogSyncManager {


    public static void syncConnected(Context mContext) {
        //创建电话管理
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
