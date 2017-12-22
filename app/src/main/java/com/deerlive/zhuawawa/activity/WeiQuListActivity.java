package com.deerlive.zhuawawa.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.WeiQuRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DanmuMessage;
import com.deerlive.zhuawawa.view.dialog.CashDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class WeiQuListActivity extends BaseActivity implements OnRecyclerViewItemClickListener, CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.checkbox_all)
    CheckBox mCheckBoxAll;
    private String mToken;
    private CashDialog cashDialog;
    private ArrayList<DanmuMessage> mListData = new ArrayList();
    private WeiQuRecyclerListAdapter mAdapter = new WeiQuRecyclerListAdapter(this,mListData);

    public void goBack(View v){
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = SPUtils.getInstance().getString("token");
        mRefreshLayout.autoRefresh();
        initGameList();
        mCheckBoxAll.setOnCheckedChangeListener(this);
    }

    private void initGameList() {
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(10)));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getGameData(0);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getGameData(mListData.size());
            }
        });
        mAdapter.setOnRecyclerViewItemClickListener(this);
    }

    private void getGameData(final int limit_begin) {
        JSONObject params = new JSONObject();
        params.put("token",mToken);
        params.put("limit_begin",limit_begin);
        params.put("limit_num",10);
        Api.getNoTakenWawa(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                if(limit_begin == 0){
                    mListData.clear();
                }
                if(mRefreshLayout.isRefreshing()){
                    mRefreshLayout.finishRefresh();
                }
                if(mRefreshLayout.isLoading()){
                    mRefreshLayout.finishLoadmore();
                }
                JSONArray list = data.getJSONArray("info");
                for (int i = 0; i < list.size(); i++) {
                    DanmuMessage g = new DanmuMessage();
                    JSONObject t = list.getJSONObject(i);
                    g.setUserName(t.getString("name"));
                    g.setAvator(t.getString("img"));
                    g.setUid(t.getString("play_time"));
                    g.setMessageContent(t.getString("exchange_price"));
                    g.setId(t.getString("doll_id"));
                    g.setRemoteUid("0");
                    mListData.add(g);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                if(limit_begin == 0){
                    mListData.clear();
                    mAdapter.notifyDataSetChanged();
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

    @Override
    public int getLayoutResource() {
        return R.layout.activity_weiqu_list;
    }

    @Override
    public void onRecyclerViewItemClick(View view, int position) {
        String s = mListData.get(position).getRemoteUid();
        if("1".equals(s)){
            mListData.get(position).setRemoteUid("0");
        }
        if("0".equals(s)){
            mListData.get(position).setRemoteUid("1");
        }
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            for (int i = 0;i<mListData.size();i++){
                mListData.get(i).setRemoteUid("1");
            }
        }else {
            for (int i = 0;i<mListData.size();i++){
                mListData.get(i).setRemoteUid("0");
            }
        }
        mAdapter.notifyDataSetChanged();
    }
   private String doll_id = "";
    public void tiqu(View v){
        doll_id="";
        for(int i =0;i<mListData.size();i++){
            if("1".equals(mListData.get(i).getRemoteUid())){
                doll_id += mListData.get(i).getId();
                doll_id += ",";
            }
        }
        if(doll_id.length()>0){
            doll_id = doll_id.substring(0,doll_id.length()-1);
        }
        if(StringUtils.isTrimEmpty(doll_id)){
            toast(getString(R.string.data_empty_error));
        }else {
            cashDialog = new CashDialog(this);
            cashDialog.setYesOnclickListener("是", new CashDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    cashDialog.dismiss();
                    tiquDuihuan("1", doll_id);
                }
            });
            cashDialog.setNoOnclickListener("否", new CashDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    cashDialog.dismiss();
                }
            });
            cashDialog.show();
        }
    }
    public void duihuan(View v){
        doll_id="";
        for(int i =0;i<mListData.size();i++){
            if("1".equals(mListData.get(i).getRemoteUid())){
                doll_id += mListData.get(i).getId();
                doll_id += ",";
            }
        }
        if(doll_id.length()>0){
            doll_id = doll_id.substring(0,doll_id.length()-1);
        }
        if(StringUtils.isTrimEmpty(doll_id)){
            toast(getString(R.string.data_empty_error));
        }else {

            tiquDuihuan("0",doll_id);
        }
    }

    private void  tiquDuihuan(String type,String doll_id){
        JSONObject p = new JSONObject();
        p.put("token",mToken);
      /*  String doll_id = "";
        for(int i =0;i<mListData.size();i++){
            if("1".equals(mListData.get(i).getRemoteUid())){
                doll_id += mListData.get(i).getId();
                    doll_id += ",";
            }
        }
        if(doll_id.length()>0){
            doll_id = doll_id.substring(0,doll_id.length()-1);
        }

        if(StringUtils.isTrimEmpty(doll_id)){
            toast(getString(R.string.data_empty_error));
            return;
        }*/
        p.put("doll_id",doll_id);
        LogUtils.i("提取===",doll_id);

        p.put("type",type);
        Api.applyPostOrDuiHuanWaWa(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                toast(data.getString("descrp"));
                SPUtils.getInstance().put("balance",data.getString("balance"));
                getGameData(0);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }
}
