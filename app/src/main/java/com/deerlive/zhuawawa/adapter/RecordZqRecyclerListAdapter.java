package com.deerlive.zhuawawa.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.common.GlideCircleTransform;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.model.DanmuMessage;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecordZqRecyclerListAdapter extends BaseQuickAdapter<DanmuMessage,BaseViewHolder> {

    public RecordZqRecyclerListAdapter( List<DanmuMessage> data) {
        super(R.layout.item_record_zq, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DanmuMessage item) {

        helper.setText(R.id.zq_name,item.getUserName())
                .setText(R.id.zq_result,item.getMessageContent())
                .setText(R.id.zq_time,item.getUid());
        Glide.with(mContext).load(item.getAvator())
                .into((ImageView) helper.getView(R.id.zq_avator));
    }
}
