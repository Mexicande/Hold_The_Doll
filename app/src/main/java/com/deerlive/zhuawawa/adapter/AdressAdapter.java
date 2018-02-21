package com.deerlive.zhuawawa.adapter;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.model.AddressBean;

import java.util.List;


/**
 * Created by apple on 2017/6/9.
 */

public class AdressAdapter extends BaseItemDraggableAdapter<AddressBean.AddrBean,BaseViewHolder> {

    public AdressAdapter(List<AddressBean.AddrBean> data) {
        super(R.layout.address_item, data);

    }

    @Override
    protected void convert(final BaseViewHolder helper, AddressBean.AddrBean item) {
        helper.setText(R.id.tv_userName,item.getName())
                .setText(R.id.tv_userPhone,item.getMobile())
                .setText(R.id.tv_address,item.getCity()+item.getAddress())
                .addOnClickListener(R.id.iv_editext)
                ;


        String status = item.getStatus();
        if("1".equals(status)){
            helper.setChecked(R.id.checkbox_address,true)
            .setBackgroundColor(R.id.layout,mContext.getResources().getColor(R.color.e_white));

        }else {
            helper.setChecked(R.id.checkbox_address,false)
            .setBackgroundColor(R.id.layout,mContext.getResources().getColor(R.color.white));
        }
    }
}