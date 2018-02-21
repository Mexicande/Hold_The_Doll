package com.deerlive.zhuawawa.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.IntegarStoreAdapter;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.intf.User_integration;
import com.deerlive.zhuawawa.model.GiftStoreBean;
import com.deerlive.zhuawawa.model.GrabBean;
import com.deerlive.zhuawawa.model.eventbean.IntegarStore;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.SpaceItemDecoration;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntegarFragment extends Fragment {


    @Bind(R.id.recyclerview)
    RecyclerView recyclerview;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private IntegarStoreAdapter integarStoreAdapter;
    private String mToken;



    public IntegarFragment() {
        // Required empty public constructor
    }
    private List<GiftStoreBean.InfoBean.GiftBean> mDateList=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_integar, container, false);
        ButterKnife.bind(this, view);
        mToken = SPUtils.getInstance().getString("token");
        initView();
        refreshLayout.autoRefresh();
        return view;

    }

    private void initView() {
        integarStoreAdapter=new IntegarStoreAdapter(R.layout.integar_store_item,null);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        recyclerview.addItemDecoration(new SpaceItemDecoration(SizeUtils.dp2px(5)));
        recyclerview.setLayoutManager(manager);
        recyclerview.setAdapter(integarStoreAdapter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initDate(0);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                initDate(mDateList.size());
            }
        });
        integarStoreAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                convertApply(mDateList.get(position).getId());
            }
        });
        getHead();

    }

    /**
     * 积分兑换
     *
     */
    private void convertApply(String id) {

        Map<String ,String> params=new HashMap<>();
        params.put("token", mToken);
        params.put("id", id);
        Api.convertApply(getActivity(), params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                String descrp = data.getString("descrp");


                String integration = data.getString("integration");

                try {
                    org.json.JSONObject jsonObject=new org.json.JSONObject(integration);

                    int user_integration = jsonObject.getInt("user_integration");

                    EventBus.getDefault().post(new IntegarStore(user_integration));

                } catch (JSONException e) {
                    e.printStackTrace();
                }




                ToastUtils.showShort(descrp);

            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showShort(msg);

            }
        });


    }

    private ImageView banner1,banner2;
    private void getHead() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.integar_head_item, null);
        banner1= (ImageView) view.findViewById(R.id.iv_banner1);
        banner2= (ImageView) view.findViewById(R.id.iv_banner2);
        integarStoreAdapter.addHeaderView(view);

    }

    private void initDate(final int limit_begin) {

        Map<String ,String> params=new HashMap<>();
        params.put("token", mToken);
        params.put("limit_begin", String.valueOf(limit_begin));
        params.put("limit_num", String.valueOf(10));

        Api.getStoreIntegar(getActivity(), params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                GiftStoreBean grabBean = JSON.parseObject(data.toString(), GiftStoreBean.class);
                if(limit_begin==0){
                    mDateList.clear();
                    mDateList.addAll(grabBean.getInfo().getGift());
                    integarStoreAdapter.setNewData(mDateList);
                }else {
                    mDateList.addAll(grabBean.getInfo().getGift());
                    integarStoreAdapter.setNewData(mDateList);
                }

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }
                if (refreshLayout.isLoading()) {
                    refreshLayout.finishLoadmore();
                }
                EventBus.getDefault().post(new IntegarStore(grabBean.getIntegrations().getUser_integration()));
                if(grabBean.getBanner().getPic()!=null){

                    if(grabBean.getBanner().getPic().size()==1){
                        Glide.with(getActivity()).load(grabBean.getBanner().getPic().get(0).getImg())
                                .into(banner1);
                    }else if(grabBean.getBanner().getPic().size()==2){
                        Glide.with(getActivity()).load(grabBean.getBanner().getPic().get(0).getImg())
                                .into(banner1);
                        Glide.with(getActivity()).load(grabBean.getBanner().getPic().get(1).getImg())
                                .into(banner2);
                    }
                }

            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showShort(msg);



                if (refreshLayout.isRefreshing()) {
                    refreshLayout.finishRefresh();
                }
                if (refreshLayout.isLoading()) {
                    refreshLayout.finishLoadmore();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
