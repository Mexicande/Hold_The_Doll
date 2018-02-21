package com.deerlive.zhuawawa.adapter;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.model.GiftStoreBean;
import com.deerlive.zhuawawa.utils.ScreenUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;

import java.util.List;

/**
 * Created by apple on 2018/1/27.
 */

public class IntegarStoreAdapter extends BaseQuickAdapter<GiftStoreBean.InfoBean.GiftBean,BaseViewHolder> {
    private int imageHeight;
    private int imageWidth;

    public IntegarStoreAdapter(int layoutResId, List<GiftStoreBean.InfoBean.GiftBean> data) {
        super(layoutResId, data);
        imageHeight = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15))/2;
        imageWidth = imageHeight ;
    }

    @Override
    protected void convert(BaseViewHolder helper, GiftStoreBean.InfoBean.GiftBean item) {
        helper.setText(R.id.gift_name,item.getName())
                .setText(R.id.gift_price,item.getIntegration()+"积分")
                 .addOnClickListener(R.id.duihuan);

        Glide.with(mContext).load(item.getList_img())
                .error(R.mipmap.logo)
                .into((ImageView)helper.getView(R.id.gift_img))
                ;
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight+ SizeUtils.dp2px(40));
        CardView view = helper.getView(R.id.item_game_container);
        view.setLayoutParams(params1);
    }
}
