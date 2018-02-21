package com.deerlive.zhuawawa.common;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.MainActivity;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.activity.LoginActivity;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.SPUtils;

import java.util.HashMap;

import butterknife.Bind;

public class SplashActivity extends BaseActivity {
    private static final int SHOW_TIME_MIN = 3000;
    @Bind(R.id.lauch_screen)
    ImageView mLauchScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTask();
        getLaunchScreen();
    }

    private void getLaunchScreen() {
        Api.getLaunchScreen(this, new HashMap<String, String>(), new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, final JSONObject data) {
                   Glide.with(getApplicationContext()).load(data.getString("info")).into(mLauchScreen);
                    mLauchScreen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(null != data.getString("url") && null != data.getString("title")){
                                Bundle temp = new Bundle();
                                temp.putString("title",data.getString("title"));
                                temp.putString("jump",data.getString("url"));
                                ActivityUtils.startActivity(temp,WebviewActivity.class);
                                SplashActivity.this.finish();
                            }
                        }
                    });
            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });
    }

    private void initTask() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(SPUtils.getInstance().contains("token")){
                    ActivityUtils.startActivity(MainActivity.class);
                }else{
                    ActivityUtils.startActivity(LoginActivity.class);
                }
                finish();
            }
        },SHOW_TIME_MIN);
    }


    @Override
    public int getLayoutResource() {
        return R.layout.activity_splash;
    }

}
