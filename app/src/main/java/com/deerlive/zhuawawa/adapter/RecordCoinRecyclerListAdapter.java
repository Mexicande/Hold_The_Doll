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


public class RecordCoinRecyclerListAdapter extends BaseQuickAdapter<DanmuMessage,BaseViewHolder> {

    public RecordCoinRecyclerListAdapter( List<DanmuMessage> data) {
        super(R.layout.item_record_coin, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DanmuMessage item) {
        helper.setText(R.id.coin_name,item.getUserName())
                .setText(R.id.coin_result,item.getMessageContent())
                .setText(R.id.coin_time,item.getUid());
    }
}
