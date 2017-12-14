package com.deerlive.zhuawawa.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.view.supertextview.SuperButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeLoginActivity extends AppCompatActivity {

    @Bind(R.id.iv_login_top)
    ImageView ivLoginTop;
    @Bind(R.id.layout_login)
    LinearLayout layoutLogin;
    @Bind(R.id.bt_Login)
    SuperButton btLogin;
    @Bind(R.id.bt_unLogin)
    SuperButton btUnLogin;
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_login);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ivLoginTop.post(new Runnable() {

            @Override
            public void run() {
                width = ivLoginTop.getWidth();// 获取宽度
                height = ivLoginTop.getHeight();// 获取高度
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueAnimator animator = ValueAnimator.ofInt(0,-height);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int curValue = (int)animation.getAnimatedValue();
                        ivLoginTop.layout(ivLoginTop.getLeft(),curValue,ivLoginTop.getRight(),curValue+ivLoginTop.getHeight());
                    }
                });
                animator.setDuration(1000);
                animator.start();

              /*  ValueAnimator animator = doRepeatAnim();
                //克隆一个新的 ValueAnimator，然后开始动画
                ValueAnimator newAnimator = animator.clone();
                newAnimator.setStartDelay(1000);
                newAnimator.start();*/
            }
        });


    }
    private ValueAnimator doRepeatAnim(){
        ValueAnimator animator = ValueAnimator.ofInt(0,400);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int)animation.getAnimatedValue();
                ivLoginTop.layout(ivLoginTop.getLeft(),curValue,ivLoginTop.getRight(),curValue+ivLoginTop.getHeight());
            }
        });
        animator.setDuration(1000);
        /*animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);*/
        return animator;
    }

    private Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {


        public void onAnimationStart(Animator animation) {

        }

        public void onAnimationEnd(Animator animation) {

        }

        public void onAnimationCancel(Animator animation) {

        }

        public void onAnimationRepeat(Animator animation) {

        }
    };
}
