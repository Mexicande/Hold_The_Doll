package com.deerlive.zhuawawa.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.AppUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.view.supertextview.SuperTextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class SettingActivity extends BaseActivity {
    @Bind(R.id.checkbox_bgm)
    SuperTextView checkboxBgm;
    @Bind(R.id.checkbox_yinxiao)
    SuperTextView checkboxYinxiao;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.bt_version)
    SuperTextView btVersion;
    private String token;

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tvTitle.setText("设置");
        token = SPUtils.getInstance().getString("token");
        if ("1".equals(SPUtils.getInstance().getString("bgm"))) {
            checkboxBgm.setSwitchIsChecked(true);
        } else {
            checkboxBgm.setSwitchIsChecked(false);
        }
        if ("1".equals(SPUtils.getInstance().getString("yinxiao"))) {
            checkboxYinxiao.setSwitchIsChecked(true);
        } else {
            checkboxYinxiao.setSwitchIsChecked(false);
        }
        setListener();

    }

    private void setListener() {
        String versionName = AppUtils.getAppVersionName();
        btVersion.setRightString("v"+versionName);

        checkboxBgm.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put("bgm", "1");
                } else {
                    SPUtils.getInstance().put("bgm", "0");
                }
            }
        });
        checkboxYinxiao.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SPUtils.getInstance().put("yinxiao", "1");
                } else {
                    SPUtils.getInstance().put("yinxiao", "0");
                }
            }
        });

    }

    public void logout(View v) {
        SPUtils.getInstance().remove("token");
        ActivityUtils.finishAllActivities();
        ActivityUtils.startActivity(LoginActivity.class);
    }

    public void gamehelp(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.set_helps));
        temp.putString("jump", Api.URL_GAME_HELP + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void yaoqing(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.yaoqing_me));
        temp.putString("jump", Api.URL_GAME_YAOQING + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void yaoqingma(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.yaoqing_input));
        temp.putString("jump", Api.URL_GAME_YAOQINGMA + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void feedback(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.feadback));
        temp.putString("jump", Api.URL_GAME_FEEDBACK + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }

    public void checkUpdate(View v) {
        Map<String,String> params=new HashMap<>();
           String versionCode= AppUtils.getAppVersionCode()+"";
            params.put("ver_num", versionCode);

        Api.checkUpdate(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("data");
                if (!TextUtils.isEmpty(info.getString("package"))) {
                    checkUpgrade(info.getString("package"), info.getString("description"));
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }


    private void checkUpgrade(final String downloadUrl, String mes) {
        new MaterialDialog.Builder(this)
                .title(R.string.set_update)
                .content(mes)
                .positiveText(R.string.agree)
                .negativeText(R.string.disagree)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(downloadUrl);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
    }

  /*  public void aboutUs(View v) {
        Bundle temp = new Bundle();
        temp.putString("title", getResources().getString(R.string.about_us));
        temp.putString("jump", Api.URL_GAME_ABOUT + "&token=" + token);
        ActivityUtils.startActivity(temp, WebviewActivity.class);
    }*/


    @Override
    public int getLayoutResource() {
        return R.layout.activity_setting;
    }


}
