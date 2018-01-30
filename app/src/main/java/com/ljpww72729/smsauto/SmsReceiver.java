package com.ljpww72729.smsauto;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LinkedME06 on 1/27/18.
 */

public class SmsReceiver extends BroadcastReceiver {

    private Context mContext;
    static final String TAG = "SMSReceive";
    static final String smsuri = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        mContext = arg0;
        if (TextUtils.equals(arg1.getAction(), smsuri)) {
            Bundle bundle = arg1.getExtras();
            if (null != bundle) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] smg = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    smg[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    Log.i(TAG + "smg" + i, smg[i].toString());
                }
                if (smg.length > 0) {
                    try {
                        syncSms(smg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void syncSms(SmsMessage[] smsMessage) throws Exception {
        //创建电话管理
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //获取手机号码
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: 这里暂未处理
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            String phoneNumber = tm.getLine1Number();
            String messageBody = "";
            String originatingAddress = "";
            long messageTime = new Date().getTime();
            for (SmsMessage message : smsMessage) {
                messageBody += message.getMessageBody();
                messageTime = message.getTimestampMillis();
                originatingAddress = message.getOriginatingAddress();
            }
            Log.i(TAG + "MB", messageBody);
            Log.i(TAG + "OA", originatingAddress);
            Map<String, String> sms = new HashMap<>();
            sms.put("messageBody", messageBody);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatTime = simpleDateFormat.format(new Date(messageTime));
            sms.put("messageTime", formatTime);
            SimpleDateFormat.getDateInstance(SimpleDateFormat.YEAR_FIELD).format(new Date());
            SimpleDateFormat.getDateInstance(SimpleDateFormat.YEAR_FIELD).format(new Date());
            SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("yyyy-MM");
            String formatMonth = simpleDateFormatMonth.format(new Date());
            // 初始化;
            SyncReference ref = WilddogSync.getInstance().getReference("smsauto/" + phoneNumber
                    + "/" + formatMonth + "/" + originatingAddress);
            ref.push().setValue(sms);
        }

    }

}
