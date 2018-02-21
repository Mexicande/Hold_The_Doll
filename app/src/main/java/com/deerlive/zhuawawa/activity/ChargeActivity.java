package com.deerlive.zhuawawa.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.PayMethodRecyclerListAdapter;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.PayMethod;
import com.deerlive.zhuawawa.model.PayModel;
import com.deerlive.zhuawawa.pay.alipay.Alipay;
import com.deerlive.zhuawawa.pay.alipay.PayResult;
import com.deerlive.zhuawawa.pay.wechat.Wechat;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.view.GridSpaceItemDecoration;
import com.deerlive.zhuawawa.view.popup.EasyPopup;
import com.mancj.slideup.SlideUp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class ChargeActivity extends BaseActivity {

    @Bind(R.id.pay_method_list)
    RecyclerView mPayMethodList;
    @Bind(R.id.my_balance_text)
    TextView mMybalanceText;
    @Bind(R.id.image_Visiable)
    View imageVisiable;
    @Bind(R.id.cat_SlideImage)
    RelativeLayout catSlideImage;
    @Bind(R.id.linear_pay_weichat_container)
    ImageView linearPayWeichatContainer;
    @Bind(R.id.linear_pay_zfb_container)
    ImageView linearPayZfbContainer;
    private SwitchHandler mHandler = new SwitchHandler(this);

    private String mToken;
    private String mBalance;
    private int mCur = -1;
    private static final int SDK_PAY_FLAG = 1;
    private String payMethod = "wechat";
    private ArrayList<PayMethod.PricesBean> mPayMethodData = new ArrayList<>();
    private PayMethodRecyclerListAdapter mPaymethidAdapter;
    private String selectMoney;
    private String currentMoney;
    private String myPayWay = "";
    private String paytype_id = "";

    private SlideUp cat_SlideUp;


    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChargeActivity.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToken = SPUtils.getInstance().getString("token");
        mBalance = SPUtils.getInstance().getString("balance");
        mMybalanceText.setText(mBalance);
        initRecycler();
        initEasyPopup();
        initData();
        setListener();
    }

    private ImageView mImageViewBanner;
    private void initRecycler() {
        mPaymethidAdapter = new PayMethodRecyclerListAdapter(mPayMethodData);
        GridLayoutManager m = new GridLayoutManager(this, 2);
        mPayMethodList.addItemDecoration(new GridSpaceItemDecoration(SizeUtils.dp2px(15)));
        mPayMethodList.setLayoutManager(m);
        mPayMethodList.setAdapter(mPaymethidAdapter);
        LinearLayout item = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.image_layout, null);
        mImageViewBanner = (ImageView) item.findViewById(R.id.iv_chargeBanner);
        mPaymethidAdapter.addHeaderView(item);

    }

    private void setListener() {

        mPaymethidAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                // mCur =Integer.parseInt(mPayMethodData.get(position).getId());
                mCur = position;
                cat_SlideUp.show();
                //mCirclePop.showAtLocation(view, Gravity.BOTTOM, 0, 0);

                //mCirclePop.showAtAnchorView(view, VerticalGravity.ALIGN_BOTTOM, HorizontalGravity.CENTER,0, 0);
            }
        });

        imageVisiable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cat_SlideUp.hide();
            }
        });
    }

    private void initEasyPopup() {

        cat_SlideUp = new SlideUp.Builder(catSlideImage)
                .withListeners(new SlideUp.Listener.Slide() {
                    @Override
                    public void onSlide(float percent) {
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    ArrayList<PayModel> payModels = new ArrayList<>();

    private void initData() {

        Api.getPayMethod(this, new HashMap<String, String>(), new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                mPayMethodData.clear();
                PayMethod payMethod = JSON.parseObject(data.toString(), PayMethod.class);
                mPaymethidAdapter.addData(payMethod.getPrices());

                if(payMethod.getBanner().getCode()==200){
                    Glide.with(ChargeActivity.this)
                            .load(payMethod.getBanner().getPic().getImg())
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(mImageViewBanner);
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

        Map<String,String> p=new HashMap<>();
        p.put("token", mToken);
        Api.getPayType(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONArray list = data.getJSONArray("data");

                for (int i = 0; i < list.size(); i++) {
                    PayModel model = new PayModel();
                    model.setId(list.getJSONObject(i).getString("id"));
                    model.setIcon(list.getJSONObject(i).getString("icon"));
                    model.setName(list.getJSONObject(i).getString("name"));
                    model.setType(list.getJSONObject(i).getString("type"));
                    payModels.add(model);
                }

                insertPayItem(payModels);
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }

    private void insertPayItem(ArrayList<PayModel> payModels) {
      /* ImageView wechat = mCirclePop.getView(R.id.linear_pay_weichat_container);
        ImageView alipay = mCirclePop.getView(R.id.linear_pay_zfb_container);*/
        for (PayModel s : payModels) {
            if ("1".equals(s.getId())) {
                linearPayWeichatContainer.setVisibility(View.VISIBLE);
                linearPayWeichatContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beigin_pay("1");
                    }
                });
            }
            if ("2".equals(s.getId())) {
                linearPayZfbContainer.setVisibility(View.VISIBLE);
                linearPayZfbContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        beigin_pay("2");
                    }
                });

            }
        }
    }

    private void beigin_pay(String paytype_id) {
        if (mCur == -1) {
            toast(getResources().getString(R.string.data_error));
            return;
        }
        Map<String,String> params=new HashMap<>();
        params.put("token", mToken);
        params.put("item_id", mPayMethodData.get(mCur).getId());
        params.put("paytype_id", paytype_id);
        Api.beginPay(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                String className = data.getString("class_name");
                switch (className) {
                    case "alipay_app":
                        String payInfo = data.getString("request");
                        Alipay alipay = new Alipay(ChargeActivity.this);
                        alipay.pay(payInfo);
                        alipay.setHander(mHandler);
                        break;
                    case "wxpay_app":
                        JSONObject payInfo1 = data.getJSONObject("request");
                        Wechat wechat = new Wechat(ChargeActivity.this);
                        wechat.pay(payInfo1.toJSONString());
                        break;
                    default:
                        String payInfo2 = data.getString("request");
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri uri = Uri.parse(payInfo2);
                        intent.setData(uri);
                        startActivity(intent);
                        break;
                }

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });

    }


    public void goBack(View v) {
        finish();
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_charge;
    }


    private static class SwitchHandler extends Handler {

        private WeakReference<ChargeActivity> mWeakReference;

        SwitchHandler(ChargeActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ChargeActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        PayResult payResult = new PayResult((String) msg.obj);
                        /**
                         * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                         * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                         * docType=1) 建议商户依赖异步通知
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                        if (TextUtils.equals(resultStatus, "9000")) {
                            activity.cat_SlideUp.hide();
                            Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();

                            int b = Integer.parseInt(activity.mPayMethodData.get(activity.mCur).getDiamond_num()) + Integer.parseInt(activity.mBalance);
                            activity.mMybalanceText.setText(b + "");

                            SPUtils.getInstance().put("balance", activity.mBalance);
                        } else {
                            // 判断resultStatus 为非"9000"则代表可能支付失败
                            // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            if (TextUtils.equals(resultStatus, "8000")) {
                                Toast.makeText(activity, "支付结果确认中", Toast.LENGTH_SHORT).show();

                            } else {
                                // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                                Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }

            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            if(cat_SlideUp!=null&&cat_SlideUp.isVisible()){
                cat_SlideUp.hide();
            }else {
                return super.onKeyDown(keyCode, event);
            }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
