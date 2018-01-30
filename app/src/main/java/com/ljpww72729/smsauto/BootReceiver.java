package com.ljpww72729.smsauto;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by LinkedME06 on 1/27/18.
 */

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED") ||
                TextUtils.equals(intent.getAction(), "android.intent.action.SCREEN_ON")) {
            if (!isServiceRunning(context, LiveService.class.getName())) {
                Intent intentService = new Intent(context, LiveService.class);
                context.startService(intentService);
            }
        }
    }

    /**
     * 判断服务是否在运行中
     *
     * @param context     即为Context对象
     * @param serviceName 即为Service的全名
     * @return 是否在运行中
     */
    private boolean isServiceRunning(Context context, String serviceName) {
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