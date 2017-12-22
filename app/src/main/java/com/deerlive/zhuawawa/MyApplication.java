package com.deerlive.zhuawawa;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.deerlive.zhuawawa.utils.LogUtils;
import com.hss01248.dialog.StyledDialog;
import com.meituan.android.walle.WalleChannelReader;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Administrator on 2017/10/23.
 * Author: XuDeLong
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    String channel="test";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Utils.init(this);

        LogUtils.init(getInstance());

        StyledDialog.init(this);
        //ShareSDK
        MobSDK.init(this,"22df8db419014","0f69e4d8a099b8426a58a16fccb8e88e");

        //Bugly
        CrashReport.initCrashReport(getApplicationContext(), "8063782a38", false);
        //Walle
        channel = WalleChannelReader.getChannel(this.getApplicationContext());
        //友盟
        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,"5a2e357aa40fa3791100004f"
                ,channel));
        //极光推送
        //JPushInterface.setDebugMode(true);
        JPushInterface.init(this);


    }
    public static MyApplication getInstance(){
        return instance;
    }
}
