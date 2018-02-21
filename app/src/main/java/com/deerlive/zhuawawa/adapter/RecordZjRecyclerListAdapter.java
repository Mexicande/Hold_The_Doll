package com.deerlive.zhuawawa.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.model.DanmuMessage;

import java.util.List;

public class RecordZjRecyclerListAdapter extends BaseQuickAdapter<DanmuMessage,BaseViewHolder> {

    public RecordZjRecyclerListAdapter(List<DanmuMessage> data) {
        super( R.layout.item_record_zj,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DanmuMessage item) {
        helper.setText(R.id.zj_name,item.getUserName())
                .setText(R.id.zj_time,item.getMessageContent());
        Glide.with(mContext).load(item.getAvator())
                .into((ImageView) helper.getView(R.id.zj_avator));
    }
}
