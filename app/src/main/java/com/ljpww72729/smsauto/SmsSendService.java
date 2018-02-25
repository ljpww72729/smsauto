package com.ljpww72729.smsauto;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.WilddogSync;

import java.util.regex.Pattern;

/**
 * 发送短信service
 *
 * Created by LinkedME06 on 2/24/18.
 */

public class SmsSendService extends Service {

    private static final String TAG = "SmsSendService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String phoneNumber = WilddogSyncManager.getPhoneNumber(this.getApplicationContext());
        if (!TextUtils.isEmpty(phoneNumber)) {
            final SyncReference sendSync = WilddogSync.getInstance().getReference("smsauto/" + phoneNumber
                    + "/sendMessage");
            sendSync.child("send").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        boolean send = (boolean) dataSnapshot.getValue();
                        if (send) {
                            sendSync.child("sendResult").setValue("发送中...");
                            SyncReference smsInfoSync = WilddogSync.getInstance().getReference("smsauto/" + phoneNumber
                                    + "/sendMessage/smsInfo");
                            smsInfoSync.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        SmsInfo smsInfo = (SmsInfo) dataSnapshot.getValue(SmsInfo.class);
                                        String address = smsInfo.getAddress();
                                        String message = smsInfo.getMessage();
                                        Log.i(TAG, "收件人: " + address + "，信息:" + message);
                                        if (validAddress(address) && !TextUtils.isEmpty(message)) {
                                            address = address.replaceAll("[- ]", address);
                                            sendSms(address, message);
                                        } else {
                                            sendSync.child("sendResult").setValue("发送失败，请核对收件人及信息");
                                        }
                                    } else {
                                        sendSync.child("sendResult").setValue("发送失败");
                                    }
                                }

                                @Override
                                public void onCancelled(SyncError syncError) {

                                }
                            });
                            sendSync.child("send").setValue(false);
                        }
                    } else {
                        // 创建发送数据结构
                        WilddogSyncManager.initSendSmsStructure(phoneNumber);
                    }
                }

                @Override
                public void onCancelled(SyncError syncError) {

                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SmsSendService.this);
                boolean tile_switch = sharedPreferences.getBoolean("tile_switch", false);
                while (tile_switch) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return Service.START_STICKY;
    }

    private boolean validAddress(String address) {
        if (TextUtils.isEmpty(address) || address.trim().length() == 0) {
            return false;
        }
        address = address.replaceAll("[- ]", address);
        return Pattern.matches("^\\+?\\d+", address);
    }

    /**
     * 发送短信
     *
     * @param number  收件人
     * @param message 信息
     */
    private void sendSms(String number, String message) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
        SyncReference sendSync = WilddogSync.getInstance().getReference("smsauto/" + WilddogSyncManager.getPhoneNumber(this)
                + "/sendMessage");
        sendSync.child("sendResult").setValue("已发送");
        Log.i(TAG, "sendSms: 已发送");
    }
}
