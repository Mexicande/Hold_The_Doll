package com.deerlive.zhuawawa.fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.RecordZjRecyclerListAdapter;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DanmuMessage;
import com.deerlive.zhuawawa.utils.SizeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/10/30.
 * Author: XuDeLong
 * 抓奖抓住记录
 */

public class RecordFragment extends DialogFragment {
    @Bind(R.id.record_zhua_list)
    RecyclerView mRecordZhuaList;
    private String mArgument; //deviceid
    private ArrayList<DanmuMessage> mRecordZjDate = new ArrayList<>();
    private RecordZjRecyclerListAdapter mRecordZjAdapter;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mArgument = bundle.getString("params");
        } else {
            //toast(getResources().getString(R.string.net_error));
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zhongjiang_record, container, false);
        ButterKnife.bind(this, view);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mRecordZjAdapter = new RecordZjRecyclerListAdapter( mRecordZjDate);
        LinearLayoutManager m = new LinearLayoutManager(getActivity());
        m.setOrientation(LinearLayoutManager.VERTICAL);
        mRecordZhuaList.setLayoutManager(m);
        mRecordZhuaList.setAdapter(mRecordZjAdapter);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getData();
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.gravity = Gravity.BOTTOM;
        int i = SizeUtils.dp2px(220);
        windowParams.y = i;
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);
    }

    private void getData() {
        Map<String,String> params=new HashMap<>();

        params.put("deviceid", mArgument);
        params.put("limit_begin", "0");
        params.put("limit_num", 20+"");
        Api.getLatestDeviceRecord(getActivity(), params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONArray info = data.getJSONArray("info");
                mRecordZjDate.clear();
                for (int i = 0; i < info.size(); i++) {
                    JSONObject t = info.getJSONObject(i);
                    DanmuMessage item1 = new DanmuMessage();
                    item1.setUid(t.getString("uid"));
                    item1.setUserName(t.getString("user_nicename"));
                    item1.setAvator(t.getString("avatar"));
                    item1.setMessageContent(t.getString("play_time"));
                    mRecordZjDate.add(item1);
                }
                mRecordZjAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestFailure(int code, String msg) {
                //toast(msg);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        } else {
            getData();
        }
    }

    /**
     * 传入需要的参数，设置给arguments
     *
     * @param params
     * @return
     */
    public static RecordFragment newInstance(String params) {
        Bundle bundle = new Bundle();
        bundle.putString("params", params);
        RecordFragment contentFragment = new RecordFragment();
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.iv_off)
    public void onViewClicked() {
        dismiss();
    }
}
