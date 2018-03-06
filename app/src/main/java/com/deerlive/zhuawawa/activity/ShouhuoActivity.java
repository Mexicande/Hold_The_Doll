package com.deerlive.zhuawawa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.AdressAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.AddressBean;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.view.popup.EasyPopup;
import com.deerlive.zhuawawa.view.popup.HorizontalGravity;
import com.deerlive.zhuawawa.view.popup.VerticalGravity;
import com.hss01248.dialog.StyledDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class ShouhuoActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.layout_top_back)
    ImageView layoutTopBack;
    @Bind(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private String mToken;
    private EasyPopup mRvPop;
    private TextView tv_delete;
    private List<AddressBean.AddrBean> list = new ArrayList<>();
    private AdressAdapter adressAdapter;
    private View notDataView;

    private final int REQUESTION_CODE = 100;
    private final int RESULT_CODE = 200;

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText(R.string.user_address);
        ivAdd.setVisibility(View.VISIBLE);
        mToken = SPUtils.getInstance().getString("token");
        initRecycler();
        setListener();
    }

    private void setListener() {
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShouhuoActivity.this, AddAddressActivity.class);
                startActivityForResult(intent, REQUESTION_CODE);
            }
        });

        adressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                addAddress(list.get(position), position);
                CheckBox viewByPosition = (CheckBox) adressAdapter.getViewByPosition(recycler, position, R.id.checkbox_address);
                if (viewByPosition.isChecked()) {
                    viewByPosition.setChecked(false);
                } else {
                    viewByPosition.setChecked(true);
                }

            }
        });
        adressAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_editext:
                        Intent intent = new Intent(ShouhuoActivity.this, AddAddressActivity.class);
                        intent.putExtra("user", list.get(position));
                        startActivityForResult(intent, REQUESTION_CODE);
                        break;

                }


            }
        });
    }

    private void initRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adressAdapter = new AdressAdapter(null);
        recycler.setAdapter(adressAdapter);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initDate();
            }
        });
        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) recycler.getParent(), false);


        mRvPop = new EasyPopup(this)
                .setContentView(R.layout.layout_circle_comment)
                .setFocusAndOutsideEnable(true)
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                    }
                })
                .createPopup();


        adressAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                showPopup(view, position);
                return false;
            }
        });


    }

    private void showPopup(View view, final int position) {
        mRvPop.showAtAnchorView(view, VerticalGravity.ABOVE, HorizontalGravity.CENTER);
        tv_delete = mRvPop.getView(R.id.tv_comment);


        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRvPop.dismiss();
                deleteAddress(position);
            }
        });


    }

    private void deleteAddress(final int pos) {
        Log.i("position", pos + "");

        Map<String, String> p = new HashMap<>();
        p.put("token", mToken);
        p.put("id", list.get(pos).getId());
        Api.getDeleteAddress(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                Log.i("position", pos + "");
                adressAdapter.remove(pos);
                //list.remove(pos);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }

    private void initDate() {
        Map<String, String> p = new HashMap<>();
        p.put("token", mToken);
        Api.getShouHuoLocation(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                AddressBean adressBean = JSON.parseObject(data.toString(), AddressBean.class);
                if (list.size() != 0) {
                    list.clear();
                }
                if (mRefreshLayout.isRefreshing()) {
                    mRefreshLayout.finishRefresh();
                }
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.finishLoadmore();
                }
                list.addAll(adressBean.getAddr());
                adressAdapter.setNewData(list);
            }

            @Override
            public void requestFailure(int code, String msg) {
                if (list.size() == 0) {
                    adressAdapter.setEmptyView(notDataView);
                }
                toast(msg);
            }
        });
    }

    private void addAddress(AddressBean.AddrBean addrBean, final int position) {

        StyledDialog.buildLoading("加载中...").show();
        Map<String, String> p = new HashMap<>();

        p.put("token", mToken);
        p.put("name", addrBean.getName());
        p.put("mobile", addrBean.getMobile());
        p.put("address", addrBean.getAddress());
        p.put("city", addrBean.getCity());
        p.put("status", "0".equals(addrBean.getStatus()) ? "1" : "0");
        p.put("id", addrBean.getId());
        Api.setShouHuoLocation(ShouhuoActivity.this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                StyledDialog.dismissLoading();

                toast(data.getString("descrp"));
                RelativeLayout viewByPosition = (RelativeLayout) adressAdapter.getViewByPosition(recycler, position, R.id.layout);
                AddressBean.AddrBean addrBean1 = list.get(position);
                if ("0".equals(list.get(position).getStatus())) {
                    addrBean1.setStatus("1");
                    viewByPosition.setBackgroundColor(getResources().getColor(R.color.e_white));
                } else {
                    addrBean1.setStatus("0");
                    viewByPosition.setBackgroundColor(getResources().getColor(R.color.white));
                }
                list.set(position, addrBean1);
                List<AddressBean.AddrBean> mlist = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (i == position) {
                        list.get(i).setStatus("1");
                    } else {
                        list.get(i).setStatus("0");
                    }
                    mlist.add(list.get(i));
                }

                adressAdapter.setNewData(mlist);

            }

            @Override
            public void requestFailure(int code, String msg) {
                StyledDialog.dismissLoading();
                toast(msg);

            }
        });
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_shouhuo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTION_CODE) {
            if (resultCode == RESULT_CODE) {
                initDate();
            }
        }
    }
}
