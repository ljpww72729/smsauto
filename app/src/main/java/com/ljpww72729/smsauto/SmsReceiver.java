package com.ljpww72729.smsauto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
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
        if (!WilddogSyncManager.isServiceRunning(arg0, SmsSendService.class.getName())) {
            Intent intentService = new Intent(arg0, SmsSendService.class);
            arg0.startService(intentService);
        }
    }

    private void syncSms(SmsMessage[] smsMessage) throws Exception {
        String phoneNumber = WilddogSyncManager.getPhoneNumber(mContext.getApplicationContext());
        if (TextUtils.isEmpty(phoneNumber)) {
            return;
        }
        String messageBody;
        String originatingAddress = "";
        long messageTime = new Date().getTime();
        StringBuilder stringBuilder = new StringBuilder();
        for (SmsMessage message : smsMessage) {
            stringBuilder.append(message.getMessageBody());
            messageTime = message.getTimestampMillis();
            originatingAddress = message.getOriginatingAddress();
        }
        messageBody = stringBuilder.toString();
        Log.i(TAG + "MB", messageBody);
        Log.i(TAG + "OA", originatingAddress);
        Map<String, String> sms = new HashMap<>();
        sms.put("messageBody", messageBody);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = simpleDateFormat.format(new Date(messageTime));
        sms.put("messageTime", formatTime);
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
        String formatDay = simpleDateFormatDay.format(new Date());
        String[] formatDaySplit = formatDay.split("-");
        // 初始化;
        SyncReference ref = WilddogSync.getInstance().getReference("smsauto/" + phoneNumber
                + "/" + formatDaySplit[0] + "/" + formatDaySplit[1] + "/" + formatDaySplit[2] + "/" + originatingAddress);
        ref.push().setValue(sms);

    }


}
