package com.deerlive.zhuawawa.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.GlideCircleTransform;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;

import butterknife.Bind;
import butterknife.OnClick;

public class UserCenterActivity extends BaseActivity {

    @Bind(R.id.user_balance)
    TextView mUserbalance;
    @Bind(R.id.user_avator)
    ImageView mUserAvator;
    @Bind(R.id.user_name)
    TextView mUserName;
    @Bind(R.id.user_wqnum)
    TextView mUserWqnum;
   /* @Bind(R.id.user_all_num)
    TextView mUserAllNum;*/
   /* @Bind(R.id.user_id)
    TextView mUserId;*/
    private String mmUserName;
    private String mmAvator;
    private String mmBalance;
    private String mToken;
    private String mId;


    public void goBack(View v){
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
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
        mUserName.setText(mmUserName);
        mUserbalance.setText(mmBalance);
        Glide.with(this).load(mmAvator)
                .error(R.mipmap.logo)
                .transform(new GlideCircleTransform(this))
                .into(mUserAvator);
       // mUserId.setText("ID:"+mId);
    }

    private void getUserInfo() {
        JSONObject p = new JSONObject();
        p.put("token",mToken);
        p.put("id",mId);
        Api.getUserInfo(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject userinfo = data.getJSONObject("data");
                SPUtils.getInstance().put("balance",userinfo.getString("balance"));
                SPUtils.getInstance().put("id",userinfo.getString("id"));
                SPUtils.getInstance().put("avatar",userinfo.getString("avatar"));
                SPUtils.getInstance().put("user_nicename",userinfo.getString("user_nicename"));
                SPUtils.getInstance().put("signaling_key",userinfo.getString("signaling_key"));
                mUserWqnum.setText(userinfo.getString("not_token_num"));
               // mUserAllNum.setText(getResources().getString(R.string.zq_all_num)+userinfo.getString("all_num"));
                initData();
            }

            @Override
            public void requestFailure(int code, String msg) {

                toast(msg);
            }
        });
    }

    public void goCharge(View v){
        ActivityUtils.startActivity(ChargeActivity.class);
    }

    public void goWaWa(View v){
        ActivityUtils.startActivity(WeiQuListActivity.class);
    }

    public void mesgList(View v){
        ActivityUtils.startActivity(MessageActivity.class);
    }

    public void shouHuo(View v){
        ActivityUtils.startActivity(ShouhuoActivity.class);
    }

    public void zhuaRecord(View v){
        ActivityUtils.startActivity(RecordZhuaListActivity.class);
    }

    public void coinRecord(View v){
        ActivityUtils.startActivity(RecordCoinListActivity.class);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_usercenter;
    }

}
