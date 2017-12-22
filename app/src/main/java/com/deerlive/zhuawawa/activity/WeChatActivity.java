package com.deerlive.zhuawawa.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.view.popup.EasyPopup;
import com.deerlive.zhuawawa.view.popup.HorizontalGravity;
import com.deerlive.zhuawawa.view.popup.VerticalGravity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeChatActivity extends AppCompatActivity {

    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.iv_qr)
    ImageView ivQr;
    private EasyPopup mCirclePop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);
        ButterKnife.bind(this);
        setListener();
    }

    private void setListener() {
        mCirclePop = new EasyPopup(WeChatActivity.this)
                .setContentView(R.layout.layout_right_pop)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                .setBackgroundDimEnable(true)
                .createPopup();
        ivQr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCirclePop.showAtAnchorView(v, VerticalGravity.CENTER, HorizontalGravity.CENTER, 0, 0);;
                return false;
            }
        });

        TextView tvComment = mCirclePop.getView(R.id.save);
        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iv_qr);
                saveImage(bitmap);
                bitmap.recycle();
                mCirclePop.dismiss();
            }
        });

    }

    private void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "SchoolPicture");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fos != null) {
            Toast toast = Toast.makeText(this, "保存成功请到相册查看", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        // 最后通知图库更新
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
}
