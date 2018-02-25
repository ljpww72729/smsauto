package com.ljpww72729.smsauto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LinkedME06 on 2/5/18.
 */

public class WilddogSyncManager {


    public static void syncConnected(Context mContext) {
        //创建电话管理
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
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
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
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
                    initSendSmsStructure(phoneNumber);
                    return phoneNumber;
                }
            } else {
                return phoneNumber.replaceFirst("^\\+?86", "");
            }
        }
        return "";
    }

    public static void initSendSmsStructure(String phoneNumber) {
        // 初始化数据
        SyncReference sendSync = WilddogSync.getInstance().getReference("smsauto/" + phoneNumber
                + "/sendMessage");
        Map<String, Object> sendMessage = new HashMap<>();
        sendMessage.put("send", false);
        sendMessage.put("sendResult", "无");
        SmsInfo smsInfo = new SmsInfo();
        sendMessage.put("smsInfo", smsInfo);
        sendSync.setValue(sendMessage);
    }


    /**
     * 判断服务是否在运行中
     *
     * @param context     即为Context对象
     * @param serviceName 即为Service的全名
     * @return 是否在运行中
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (!TextUtils.isEmpty(serviceName)) {
            ActivityManager activityManager
                    = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ArrayList<ActivityManager.RunningServiceInfo> runningServiceInfoList
                    = (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(100);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServiceInfoList) {
                if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

}
