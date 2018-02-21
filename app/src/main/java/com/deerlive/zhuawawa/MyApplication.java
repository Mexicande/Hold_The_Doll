package com.deerlive.zhuawawa;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.deerlive.zhuawawa.utils.LogUtils;
import com.deerlive.zhuawawa.utils.Utils;
import com.hss01248.dialog.StyledDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.meituan.android.walle.WalleChannelReader;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import static com.deerlive.zhuawawa.common.Api.APP_VER;
import static com.deerlive.zhuawawa.common.Api.OS;
import static com.deerlive.zhuawawa.common.Api.OS_VER;
import static com.deerlive.zhuawawa.common.Api.QUDAO;


/**
 * Created by Administrator on 2017/10/23.
 * Author: XuDeLong
 */

public class MyApplication extends Application {
    private static MyApplication instance;
    String channel="test";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // LeakCanary.install(this);

        Utils.init(this);

        initOkGo();


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
      //  JPushInterface.init(this);
    }

    private void initOkGo() {
        HttpParams params=new HttpParams();

        params.put("os", OS);
        params.put("soft_ver", APP_VER);
        params.put("os_ver", OS_VER);
        params.put("qudao",QUDAO);

        OkGo.getInstance().init(this)
                .addCommonParams(params);
    }

    public static MyApplication getInstance(){
        return instance;
    }
}
