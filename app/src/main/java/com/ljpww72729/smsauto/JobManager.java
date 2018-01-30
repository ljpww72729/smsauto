package com.ljpww72729.smsauto;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by LinkedME06 on 1/28/18.
 */

public class JobManager {

    private static final String TAG = "JobManager";

    //将任务作业发送到作业调度中去
    public static void scheduleJob(Context context) {
        Log.i(TAG, "调度job");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int jobId = 1;
            JobInfo.Builder builder = new JobInfo.Builder(jobId, new ComponentName(context, MyJobService.class));
            builder.setPeriodic(10);
            builder.setPersisted(true);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            List<JobInfo> jobInfoList = jobScheduler.getAllPendingJobs();
            for (int i = 0; i < jobInfoList.size(); i++) {
                if (jobInfoList.get(i).getId() == jobId) {
                    return;
                }
            }
            int jobStatus = jobScheduler.schedule(builder.build());
            if (jobStatus == JobScheduler.RESULT_SUCCESS) {
                Log.i(TAG, "onCreate: Job Success!");
            } else {
                Log.i(TAG, "onCreate: Job Failed");
            }
        }
    }
}
