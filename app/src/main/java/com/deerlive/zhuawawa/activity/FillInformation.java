package com.deerlive.zhuawawa.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.view.supertextview.SuperButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FillInformation extends BaseActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.et_userPhone)
    EditText etUserPhone;
    @Bind(R.id.bt_binding)
    SuperButton btBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_fill_information;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {

    }
}
