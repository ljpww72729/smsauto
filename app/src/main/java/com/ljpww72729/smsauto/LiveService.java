package com.ljpww72729.smsauto;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by LinkedME06 on 1/27/18.
 */

public class LiveService extends IntentService {
    private static final String TAG = "LiveService";

    public LiveService() {
        super("liveService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.i(TAG, "onStart: ");
        JobManager.scheduleJob(this);
        return Service.START_STICKY;
    }

}
