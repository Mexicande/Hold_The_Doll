package com.deerlive.zhuawawa.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.common.WebviewActivity;
import com.deerlive.zhuawawa.model.PopupBean;
import com.deerlive.zhuawawa.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author apple
 */
public class AdialogFragment extends DialogFragment {
    private static String popup=null;
    private PopupBean popupBean;
    @Bind(R.id.fl_content_container)
    ImageView flContentContainer;
    @Bind(R.id.iv_close)
    ImageView ivClose;

    public static AdialogFragment newInstance(PopupBean mPopupBean) {
        AdialogFragment instance = new AdialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(popup,mPopupBean);
        instance.setArguments(args);
        return instance;

    }

    public AdialogFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Base_AlertDialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adialog, container, false);
        final Window window = getDialog().getWindow();
        if(window!=null){
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(wlp);
        }
        ButterKnife.bind(this, view);
        initDialog();

        return view;
    }

    private void initDialog() {
        popupBean = (PopupBean) getArguments().getSerializable(popup);
        if(popupBean!=null&&popupBean.getImg()!=null){
            Glide.with(this)
                    .load(popupBean.getImg())
                    .into(flContentContainer);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.fl_content_container, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_content_container:
                if (!"".equals(popupBean.getDescription())&&popupBean.getDescription()!=null) {
                    Bundle temp = new Bundle();
                    temp.putString("title", popupBean.getName());
                    temp.putString("jump", popupBean.getDescription());
                    ActivityUtils.startActivity(temp, WebviewActivity.class);
                    dismiss();
                }
                break;
            case R.id.iv_close:
                dismiss();
                break;
            default:
                break;
        }
    }
}
