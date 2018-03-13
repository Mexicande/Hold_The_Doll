package com.deerlive.zhuawawa;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.activity.ChargeActivity;
import com.deerlive.zhuawawa.activity.PlayerActivity;
import com.deerlive.zhuawawa.activity.RecordStoreActivity;
import com.deerlive.zhuawawa.activity.SettingActivity;
import com.deerlive.zhuawawa.activity.UserCenterActivity;
import com.deerlive.zhuawawa.adapter.GameRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.fragment.AdialogFragment;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DeviceAndBanner;
import com.deerlive.zhuawawa.model.PopupBean;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.AppUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.utils.TimeUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import cn.bingoogolapple.bgabanner.BGABanner;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    private ArrayList<DeviceAndBanner.InfoBean.DeviceBean> mGameData = new ArrayList();
    private ArrayList<DeviceAndBanner.BannerBean.PicBean> mBannerData = new ArrayList();
    private String token;
    private GameRecyclerListAdapter mGameAdapter = new GameRecyclerListAdapter(this, mGameData);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = SPUtils.getInstance().getString("token");

        showAdvertising();
        initGameList();
        initBanner();
        mRefreshLayout.autoRefresh();
        initData();
    }

    /**
     * 广告弹窗
     *
     */
    private void showAdvertising() {

        Api.getDialog(this, new HashMap<String, String>(), new OnRequestDataListener() {
                    @Override
                    public void requestSuccess(int code, JSONObject data) {

                        JSONObject list = data.getJSONObject("info");
                        list.getString("img");
                        String status = list.getString("status");
                        if("0".equals(status)){
                            String advertTime = SPUtils.getInstance().getString("AdvertTime", String.valueOf(1111111111111L));
                            boolean today = TimeUtils.isToday(advertTime);
                            if (!today) {
                                PopupBean popupBean = JSON.parseObject(list.toString(), PopupBean.class);
                                AdialogFragment adialogFragment= AdialogFragment.newInstance(popupBean);
                                adialogFragment.show(getSupportFragmentManager(),"adialogFragment");                            }

                        }
                    }

                    @Override
                    public void requestFailure(int code, String msg) {

                    }
                });
        long timeMillis = System.currentTimeMillis();
        SPUtils.getInstance().put("AdvertTime", timeMillis);
    }




    public void userCenter(View v) {
        ActivityUtils.startActivity(UserCenterActivity.class);
    }

    public void setCenter(View v) {
        ActivityUtils.startActivity(SettingActivity.class);
    }

    private void initData() {
        getGameData(0);
        checkUpdate();

    }
    private DeviceAndBanner deviceAndBanner;
    private void getGameData(final int limit_begin) {
        Map<String,String>params=new HashMap<>();
        params.put("limit_begin", String.valueOf(limit_begin));
        params.put("limit_num", 10+"");
        params.put("token", token);
        Api.getGameList(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if (limit_begin == 0) {
                    mGameData.clear();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
                 deviceAndBanner = JSON.parseObject(data.toString(), DeviceAndBanner.class);

                mGameData.addAll(deviceAndBanner.getInfo().getDevice());
                mGameAdapter.notifyDataSetChanged();
                mBannerData.clear();
                mBannerData.addAll(deviceAndBanner.getBanner().getPic());
                mConvenientBanner.setData(mBannerData,null);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                if (limit_begin == 0) {
                    mGameData.clear();
                    mGameAdapter.notifyDataSetChanged();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
            }
        });
    }



    private void initGameList() {
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mGameAdapter.haveHeaderView() && position == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(5)));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mGameAdapter);
        mGameAdapter.setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerViewItemClick(View view, int position) {
                Bundle d = new Bundle();
                d.putSerializable("item", mGameData.get(position));
                ActivityUtils.startActivity(d, PlayerActivity.class);
            }
        });

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getGameData(mGameData.size());
            }
        });
    }

    LinearLayout layoutInvite;
    LinearLayout layoutIntegral;
    LinearLayout layoutCharge;
    private BGABanner mConvenientBanner;

    private void initBanner() {
        LinearLayout temp = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.layout_home_banner, null);
        mConvenientBanner = (BGABanner) temp.findViewById(R.id.convenientBanner);
        layoutInvite = (LinearLayout) temp.findViewById(R.id.layout_invite);
        layoutIntegral = (LinearLayout) temp.findViewById(R.id.layout_integral);
        layoutCharge = (LinearLayout) temp.findViewById(R.id.layout_charge);
        mGameAdapter.addHeaderView(temp);


        mConvenientBanner.setAdapter(new BGABanner.Adapter<ImageView, DeviceAndBanner.BannerBean.PicBean>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, DeviceAndBanner.BannerBean.PicBean model, int position) {

                Glide.with(MainActivity.this)
                        .load(model.getImg())
                        .centerCrop()
                        .into(itemView);

            }
        });
        mConvenientBanner.setDelegate(new BGABanner.Delegate<ImageView, DeviceAndBanner.BannerBean.PicBean>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, DeviceAndBanner.BannerBean.PicBean model, int position) {
                if (!" ".equals(mBannerData.get(position).getJump())&&mBannerData.get(position).getJump()!=null) {
                    DeviceAndBanner.BannerBean.PicBean b = mBannerData.get(position);
                    Bundle temp = new Bundle();
                    temp.putString("title", b.getTitle());
                    temp.putString("jump", b.getJump());
                    ActivityUtils.startActivity(temp, WebviewActivity.class);
                }
            }
        });



        layoutInvite.setOnClickListener(this);
        layoutIntegral.setOnClickListener(this);
        layoutCharge.setOnClickListener(this);
    }


    /**
     * 通告
     */


    public void checkUpdate() {




        Map<String,String>params=new HashMap<>();

            String versionCode = AppUtils.getAppVersionCode()+"";
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

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.layout_invite:
                Bundle temp = new Bundle();
                temp.putString("title", getResources().getString(R.string.yaoqing_me));
                temp.putString("jump", Api.URL_GAME_YAOQING + "&token=" +token );
                ActivityUtils.startActivity(temp, WebviewActivity.class);
                break;
            case  R.id.layout_integral:
                RecordStoreActivity.launch(this);
                break;
            case  R.id.layout_charge:
                ChargeActivity.launch(this);
                break;
            default:
                break;

        }
    }
    private long mLastBackTime = 0;
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mLastBackTime) < 1000) {
            finish();
        } else {
            mLastBackTime = System.currentTimeMillis();
            ToastUtils.showShort( "再按一次退出");
        }


    }
}
