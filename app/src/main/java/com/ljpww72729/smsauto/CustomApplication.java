package com.ljpww72729.smsauto;

import android.app.Application;

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
        WilddogSyncManager.syncConnected(this);

    }
}
