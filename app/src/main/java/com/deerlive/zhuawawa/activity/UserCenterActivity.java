package com.deerlive.zhuawawa.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.GlideCircleTransform;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class UserCenterActivity extends BaseActivity {

    @Bind(R.id.user_avator)
    ImageView mUserAvator;
    @Bind(R.id.user_name)
    TextView mUserName;
    @Bind(R.id.user_wqnum)
    TextView mUserWqnum;
    @Bind(R.id.user_id)
    TextView mUserId;
    @Bind(R.id.integration)
    TextView integration;
    @Bind(R.id.play_balance)
    TextView myBalanceText;
    @Bind(R.id.layout_integal)
    LinearLayout layoutIntegal;
    private String mmUserName;
    private String mmAvator;
    private String mmBalance;
    private String mToken;
    private String mId;


    public void goBack(View v) {
        finish();
    }

    public void goCharge(View v) {
        ChargeActivity.launch(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        layoutIntegal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordStoreActivity.launch(UserCenterActivity.this);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void initData() {

        mmUserName = SPUtils.getInstance().getString("user_nicename");
        mmAvator = SPUtils.getInstance().getString("avatar");
        mmBalance = SPUtils.getInstance().getString("balance");
        mToken = SPUtils.getInstance().getString("token");
        mId = SPUtils.getInstance().getString("id");

        myBalanceText.setText(mmBalance);
        mUserName.setText(mmUserName);
        Glide.with(this).load(mmAvator)
                .error(R.mipmap.logo)
                .transform(new GlideCircleTransform(this))
                .into(mUserAvator);
        mUserId.setText("ID:" + mId);
    }

    private void getUserInfo() {
        Map<String, String> p = new HashMap<>();
        p.put("token", mToken);
        p.put("id", mId);
        Api.getUserInfo(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject userinfo = data.getJSONObject("data");
                SPUtils.getInstance().put("balance", userinfo.getString("balance"));
                SPUtils.getInstance().put("id", userinfo.getString("id"));
                SPUtils.getInstance().put("avatar", userinfo.getString("avatar"));
                SPUtils.getInstance().put("user_nicename", userinfo.getString("user_nicename"));
                SPUtils.getInstance().put("signaling_key", userinfo.getString("signaling_key"));


                myBalanceText.setText(userinfo.getString("balance"));

                mUserWqnum.setText(userinfo.getString("not_token_num"));

                integration.setText(userinfo.getString("integration"));
                initData();
            }

            @Override
            public void requestFailure(int code, String msg) {

                toast(msg);
            }
        });
    }



    public void goWaWa(View v) {
        ActivityUtils.startActivity(WeiQuListActivity.class);
    }

    public void mesgList(View v) {
        ActivityUtils.startActivity(MessageActivity.class);
    }

    /**
     * 收货地址
     *
     * @param v
     */
    public void shouHuo(View v) {
        ActivityUtils.startActivity(ShouhuoActivity.class);
    }

    /**
     * 抓取记录
     *
     * @param v
     */
    public void zhuaRecord(View v) {
        ActivityUtils.startActivity(RecordZhuaListActivity.class);
    }

    /**
     * 金币记录
     *
     * @param v
     */
    public void coinRecord(View v) {
        ActivityUtils.startActivity(RecordCoinListActivity.class);
    }

    /**
     * 积分记录
     *
     * @param v
     */
    public void intager_record(View v) {

        ActivityUtils.startActivity(IntegarlCoinListActivity.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_usercenter;
    }

}
