package com.deerlive.zhuawawa.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.AddressBean;
import com.deerlive.zhuawawa.model.CityEntityBean;
import com.deerlive.zhuawawa.utils.GetJsonDataUtil;
import com.deerlive.zhuawawa.utils.RegexUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.ToastUtils;
import com.deerlive.zhuawawa.view.supertextview.SuperTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 收货地址添加和编辑
 */
public class AddAddressActivity extends BaseActivity {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.ed_contact)
    EditText ed_Contact;
    @Bind(R.id.ed_UserName)
    EditText edUserName;
    @Bind(R.id.ed_address)
    EditText edAddress;
    @Bind(R.id.setting_default)
    SuperTextView settingDefault;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    private List<CityEntityBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private String address="";
    private String mToken;
    private AddressBean.AddrBean user;
    private final int RESULT_CODE=200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText(R.string.add_address);
        mToken = SPUtils.getInstance().getString("token");
        initDate();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 写子线程中的操作,解析省市区数据
                initJsonData();
            }
        });
        thread.start();
    }

    private void initDate() {
        user= (AddressBean.AddrBean) getIntent().getSerializableExtra("user");
        if(user!=null){
            edUserName.setHint(user.getName());
            edAddress.setHint(user.getAddress());
            ed_Contact.setHint(user.getMobile());

            if("1".equals(user.getStatus())){
                settingDefault.setSwitchIsChecked(true);
            }

            tvAddress.setText(user.getCity());
        }

    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_add_address;
    }

    public void goBack(View view) {
        finish();
    }


    @OnClick({R.id.bt_save, R.id.layout_city})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                verAddress();
                break;
            case R.id.layout_city:
                selectCity();
                break;
            default:
                break;
        }
    }

    private void verAddress() {
        String userName = edUserName.getText().toString();
        boolean zh = RegexUtils.isZh(userName);
        String userPhone = ed_Contact.getText().toString();
        boolean mobileSimple = RegexUtils.isMobileExact(userPhone);

        String etAddress = edAddress.getText().toString();
        if(!TextUtils.isEmpty(userName)&&zh){
            if(!TextUtils.isEmpty(userPhone)||mobileSimple){

                if(!TextUtils.isEmpty(etAddress)){
                    boolean switchIsChecked = settingDefault.getSwitchIsChecked();
                    if(switchIsChecked){
                        addAddress(1);
                    }else {
                        addAddress(2);
                    }

                }else {
                    ToastUtils.showShort("收货人地址异常");

                }
            }else {
                ToastUtils.showShort("联系方式不正确");

            }
        }else {
            ToastUtils.showShort("收货人姓名异常");
        }

    }

    private void addAddress(int isCheck) {

        Map<String,String> p = new HashMap<>();

        p.put("token", mToken);
        p.put("name", edUserName.getText().toString());
        p.put("mobile", ed_Contact.getText().toString());
        p.put("address", edAddress.getText().toString());
        p.put("city", address);
        p.put("status", String.valueOf(isCheck));
        if(user!=null&&user.getId()!=null&&!user.getId().isEmpty()){
            p.put("id", user.getId());
        }
        Api.setShouHuoLocation(AddAddressActivity.this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                toast(data.getString("descrp"));
                setResult(RESULT_CODE);
                finish();
            }
            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }

    /**
     * 添加省份数据
     *
     * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
     * PickerView会通过getPickerViewText方法获取字符串显示出来。
     */
    private void initJsonData() {//解析数据
        String jsondata = new GetJsonDataUtil().getJson(this);

        List<CityEntityBean> jsonBean = parseData(jsondata);
        options1Items = jsonBean;


        for (int i=0;i<jsonBean.size();i++){
            //遍历省份
            ArrayList<String> citylist = new ArrayList<>();
            //该省的城市列表（第二级）
            ArrayList<ArrayList<String>> provinceArealist = new ArrayList<>();
            //该省的所有地区列表（第三极）

            for (int c=0; c<jsonBean.get(i).getCity().size(); c++){
                //遍历该省份的所有城市
                String cityname = jsonBean.get(i).getCity().get(c).getName();
                citylist.add(cityname);
                //添加城市

                ArrayList<String> arrayList = new ArrayList<>();
                //该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCity().get(c).getArea() == null
                        ||jsonBean.get(i).getCity().get(c).getArea().size()==0) {
                    arrayList.add("");
                }else {

                    //该城市对应地区所有数据
                    //添加该城市所有地区数据
                    arrayList.addAll(jsonBean.get(i).getCity().get(c).getArea());


                }
                provinceArealist.add(arrayList);
                //添加该省所有地区数据
            }


            options2Items.add(citylist);



            options3Items.add(provinceArealist);
        }


    }


    public List<CityEntityBean> parseData(String result) {//Gson 解析
        List<CityEntityBean> detail = new ArrayList<>();


        try {

            JSONArray objects = JSONArray.parseArray(result);

            for (int i = 0; i < objects.size(); i++) {

                JSONObject objects1 = objects.getJSONObject(i);

                CityEntityBean jsonBean = JSON.parseObject(JSON.toJSONString(objects1), CityEntityBean.class);

                detail.add(jsonBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
    private OptionsPickerView pvOptions;
    private void selectCity() {

         pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                address = options1Items.get(options1).getPickerViewText() +
                        options2Items.get(options1).get(options2) +
                        options3Items.get(options1).get(options2).get(options3);

            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);

                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.returnData();
                                tvAddress.setText(address);
                                pvOptions.dismiss();
                            }
                        });

                    }
                })
                .setDividerColor(getResources().getColor(R.color.line))
                .setTextColorCenter(Color.parseColor("#82b2e3"))
                 //设置选中项文字颜色
                .setContentTextSize(20)
                .isDialog(true)
                .build();
        pvOptions.setPicker(options1Items);
        //一级选择器
        pvOptions.setPicker(options1Items, options2Items);
        //二级选择器
        pvOptions.setPicker(options1Items, options2Items, options3Items);
        //三级选择器
        pvOptions.show();

    }


}
