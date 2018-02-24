package com.ljpww72729.smsauto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
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
            final String phoneNum = getPhoneNumber(mContext);
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

    @SuppressLint("MissingPermission")
    public static String getPhoneNumber(Context mContext) {
        // 判断是否获取到本机手机号
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String phoneNumber = sharedPreferences.getString(ConstantsStr.PHONE_NUMBER, "");
        if (TextUtils.isEmpty(phoneNumber)) {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tm.getLine1Number();
            if (TextUtils.isEmpty(phoneNumber)) {
                return "";
            } else {
                phoneNumber = phoneNumber.replaceFirst("^\\+?86", "");
                sharedPreferences.edit().putString(ConstantsStr.PHONE_NUMBER, phoneNumber).apply();
                return phoneNumber;
            }
        } else {
            return phoneNumber.replaceFirst("^\\+?86", "");
        }
    }

}
