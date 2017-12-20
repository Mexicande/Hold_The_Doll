package com.deerlive.zhuawawa.fragment;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.deerlive.zhuawawa.MainActivity;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.utils.shareutils.LoginUtil;
import com.deerlive.zhuawawa.utils.shareutils.ShareConfig;
import com.deerlive.zhuawawa.utils.shareutils.ShareManager;
import com.deerlive.zhuawawa.utils.shareutils.login.LoginListener;
import com.deerlive.zhuawawa.utils.shareutils.login.LoginPlatform;
import com.deerlive.zhuawawa.utils.shareutils.login.LoginResult;
import com.deerlive.zhuawawa.utils.shareutils.login.result.QQToken;
import com.deerlive.zhuawawa.utils.shareutils.login.result.QQUser;
import com.hss01248.dialog.StyledDialog;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 */
public class LoginFragment extends DialogFragment {





    public onDismiassListener mListener;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.bt_getCode)
    Button btGetCode;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.iv_WeChat)
    ImageView ivWeChat;
    @Bind(R.id.iv_Sina)
    ImageView ivSina;
    @Bind(R.id.iv_QQ)
    ImageView ivQQ;
    @Bind(R.id.iv_LinkedIn)
    ImageView ivLinkedIn;


    private Platform mPlatForm;
    private MyHandler mHandler;
    private JSONObject params;
    Dialog mLoadingDialog;

    private LoginListener mLoginListener;
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.iv_back, R.id.iv_WeChat, R.id.iv_Sina, R.id.iv_QQ, R.id.iv_LinkedIn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                dismiss();
                break;
            case R.id.iv_WeChat:
                mPlatForm.setPlatformActionListener(mPlatListener);
                mPlatForm.authorize();
                mLoadingDialog = StyledDialog.buildLoading().setActivity(getActivity()).show();
                break;
            case R.id.iv_Sina:
                break;
            case R.id.iv_QQ:
                LoginUtil.login(getActivity(), LoginPlatform.QQ, mLoginListener);
                break;
            case R.id.iv_LinkedIn:
                break;
        }
    }

    public interface onDismiassListener {
        void verify();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onDismiassListener) {
            mListener = ((onDismiassListener) context);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_login, null);

        ButterKnife.bind(this, view);
        final Dialog dialog = new Dialog(getActivity(), R.style.style_dialog);
        dialog.setContentView(view);
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        mPlatForm = ShareSDK.getPlatform(Wechat.NAME);
        mHandler = new MyHandler();
        ShareConfig config = ShareConfig.instance()
                .qqId(getString(R.string.qq_id))
                .weiboId(getString(R.string.sina_id))
                .wxId(getString(R.string.wechat_id))
                .weiboRedirectUrl("XXXXXX")
                .wxSecret(getString(R.string.wechat_key));
        ShareManager.init(config);
        setLisenter();

        return dialog;

    }

    private void setLisenter() {

        mLoginListener = new LoginListener() {
            @Override
            public void loginSuccess(LoginResult result) {
                Toast.makeText(getActivity(),
                        result.getUserInfo() != null ? result.getUserInfo().getNickname()
                                : "" + "登录成功", Toast.LENGTH_SHORT).show();
                // 处理result
                switch (result.getPlatform()) {
                    case LoginPlatform.QQ:
                        QQUser user = (QQUser) result.getUserInfo();
                        QQToken token = (QQToken) result.getToken();
                        params = new JSONObject();
                        params.put("name",user.getNickname());
                        params.put("from","qq");
                        params.put("head_img",user.getHeadImageUrl());
                        params.put("openid",user.getOpenId());
                        params.put("access_token",token.getAccessToken());
                        params.put("qudao",Api.QUDAO);
                        mHandler.sendEmptyMessage(1);
                        break;
                    case LoginPlatform.WEIBO:
                        // 处理信息
                        break;
                    case LoginPlatform.WX:
                        break;
                }
                dismiss();
                ActivityUtils.startActivity(MainActivity.class);
                getActivity().finish();
            }

            @Override
            public void loginFailure(Exception e) {
                Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loginCancel() {
                Toast.makeText(getActivity(), "登录取消", Toast.LENGTH_SHORT).show();
            }
        };

}

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.verify();
    }

    private PlatformActionListener mPlatListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            mLoadingDialog.dismiss();
            PlatformDb db = platform.getDb();
            String name = db.getUserName();
            String from = "Wechat";
            String head_img = db.getUserIcon();
            String openid = db.getUserId();
            String access_token = db.getToken();
            String expires_date = db.getExpiresTime()+"";
            params = new JSONObject();
            params.put("name",name);
            params.put("from",from);
            params.put("head_img",head_img);
            params.put("openid",openid);
            params.put("access_token",access_token);
            params.put("expires_date",expires_date);
            params.put("qudao",Api.QUDAO);
            mHandler.sendEmptyMessage(1);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            mLoadingDialog.dismiss();

        }
        @Override
        public void onCancel(Platform platform, int i) {
            mLoadingDialog.dismiss();
        }

    };

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                LogUtils.d(params.toString());
                Api.doLogin(getActivity(), params, new OnRequestDataListener() {
                    @Override
                    public void requestSuccess(int code, JSONObject data) {
                        mLoadingDialog.dismiss();
                        SPUtils.getInstance().put("token",data.getString("token"));
                        JSONObject userinfo = data.getJSONObject("data");
                        SPUtils.getInstance().put("balance",userinfo.getString("balance"));
                        SPUtils.getInstance().put("id",userinfo.getString("id"));
                        SPUtils.getInstance().put("avatar",userinfo.getString("avatar"));
                        SPUtils.getInstance().put("user_nicename",userinfo.getString("user_nicename"));
                        SPUtils.getInstance().put("signaling_key",userinfo.getString("signaling_key"));
                        SPUtils.getInstance().put("bgm","1");
                        SPUtils.getInstance().put("yinxiao","1");
                        dismiss();
                        ActivityUtils.startActivity(MainActivity.class);
                        getActivity().finish();
                    }

                    @Override
                    public void requestFailure(int code, String msg) {
                        mLoadingDialog.dismiss();

                        ToastUtils.showShort(msg);
                    }
                });
            }
        }
    }


}