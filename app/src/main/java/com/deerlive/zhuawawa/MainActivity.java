package com.deerlive.zhuawawa;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.Utils;
import com.deerlive.zhuawawa.activity.PlayerActivity;
import com.deerlive.zhuawawa.activity.SettingActivity;
import com.deerlive.zhuawawa.activity.UserCenterActivity;
import com.deerlive.zhuawawa.adapter.BannerItemViewHolder;
import com.deerlive.zhuawawa.adapter.GameRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.Banner;
import com.deerlive.zhuawawa.model.Game;
import com.deerlive.zhuawawa.view.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private ArrayList<Game> mGameData = new ArrayList();
    private ArrayList<Banner> mBannerData = new ArrayList();
    private ConvenientBanner mConvenientBanner;
    private GameRecyclerListAdapter mGameAdapter = new GameRecyclerListAdapter(this,mGameData);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }*/
        initGameList();
        initBanner();
        mRefreshLayout.autoRefresh();
        initData();
    }

  /*  @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.titleBar(R.id.toolbar)
                .navigationBarColor(R.color.shape2)
                .init();
    }*/

    public void userCenter(View v){
        ActivityUtils.startActivity(UserCenterActivity.class);
    }

    public void setCenter(View v){
        ActivityUtils.startActivity(SettingActivity.class);
    }

    private void initData() {
        getBannerData();
        getGameData(0);
        checkUpdate();
    }

    private void getGameData(final int limit_begin) {
        JSONObject params = new JSONObject();
        params.put("limit_begin",limit_begin);
        params.put("limit_num",10);
        Api.getGameList(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if(limit_begin == 0){
                    mGameData.clear();
                }
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.finishRefresh();
                }
                if(mRefreshLayout.isLoading()){
                    mRefreshLayout.finishLoadmore();
                }
                JSONArray list = data.getJSONArray("info");
                for (int i = 0; i < list.size(); i++) {
                    Game g = new Game();
                    JSONObject t = list.getJSONObject(i);
                    g.setGameUrl(t.getString("thumb"));
                    g.setGameName(t.getString("channel_title"));
                    g.setGameId(t.getString("deviceid"));
                    g.setGamePrice(t.getString("price"));
                    g.setGameStatus(t.getString("channel_status"));
                    g.setGamePlayUrl(t.getString("channel_stream"));
                    mGameData.add(g);
                }
                mGameAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                if(limit_begin == 0){
                    mGameData.clear();
                    mGameAdapter.notifyDataSetChanged();
                }
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.finishRefresh();
                }
                if(mRefreshLayout.isLoading()){
                    mRefreshLayout.finishLoadmore();
                }
            }
        });
    }

    private void getBannerData() {
        Api.getBanner(this, new JSONObject(), new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                mBannerData.clear();
                JSONArray info = data.getJSONArray("data");
                for (int i =0;i<info.size();i++){
                    JSONObject t = info.getJSONObject(i);
                    Banner item = new Banner();
                    item.setPic(t.getString("pic"));
                    item.setTitle(t.getString("title"));
                    item.setJump(t.getString("jump"));
                    mBannerData.add(item);
                }
                mConvenientBanner.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }

    private void initGameList() {
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(mGameAdapter.haveHeaderView() && position == 0){
                    return 2;
                }else {
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
                d.putSerializable("item",mGameData.get(position));
                ActivityUtils.startActivity(d,PlayerActivity.class);
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

    private void initBanner() {
        LinearLayout temp = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.layout_home_banner,null);
        mConvenientBanner = (ConvenientBanner)temp.findViewById(R.id.convenientBanner);
       /* int bannerWidth = ScreenUtils.getScreenWidth();
        int bannerHeight = bannerWidth * 2 / 5;
        mConvenientBanner.setLayoutParams(new LinearLayout.LayoutParams(bannerWidth, bannerHeight));*/
        mConvenientBanner.setPointViewVisible(true);
        mConvenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new BannerItemViewHolder();
            }
        }, mBannerData);
        mConvenientBanner.startTurning(3000);
        mGameAdapter.addHeaderView(temp);
        mConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                if(!" ".equals(mBannerData.get(position).getJump())){
                    Banner b = mBannerData.get(position);
                    Bundle temp = new Bundle();
                    temp.putString("title",b.getTitle());
                    temp.putString("jump",b.getJump());
                    ActivityUtils.startActivity(temp,WebviewActivity.class);
                }

            }
        });
    }


    public void checkUpdate(){
        JSONObject params = new JSONObject();
        try {
            String versionCode = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            params.put("ver_num",versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Api.checkUpdate(this,params , new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject info = data.getJSONObject("data");
                if(!StringUtils.isEmpty(info.getString("package"))){
                    checkUpgrade(info.getString("package"),info.getString("description"));
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
            }
        });
    }


    private void checkUpgrade(final String downloadUrl,String mes) {
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
}
