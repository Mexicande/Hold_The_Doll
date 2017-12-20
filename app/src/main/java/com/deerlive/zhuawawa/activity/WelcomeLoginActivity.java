package com.deerlive.zhuawawa.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.fragment.LoginFragment;
import com.deerlive.zhuawawa.view.supertextview.SuperButton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeLoginActivity extends AppCompatActivity implements LoginFragment.onDismiassListener {

    @Bind(R.id.iv_login_top)
    ImageView ivLoginTop;
    @Bind(R.id.layout_login)
    LinearLayout layoutLogin;
    @Bind(R.id.bt_Login)
    SuperButton btLogin;
    @Bind(R.id.bt_unLogin)
    SuperButton btUnLogin;
    @Bind(R.id.layout_unLogin)
    RelativeLayout layoutUnLogin;
    private int height;
    private int width;

    private int un_hight;
    private int un_width;

    private int heightPixels;

    private int bt_hight;

    private LoginFragment loginFragment;


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

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
         heightPixels = metric.heightPixels;   // 屏幕高度（像素）



        ivLoginTop.post(new Runnable() {

            @Override
            public void run() {
                width = ivLoginTop.getWidth();// 获取宽度
                height = ivLoginTop.getHeight();// 获取高度
            }
        });

        btUnLogin.post(new Runnable() {
            @Override
            public void run() {
                un_width = btUnLogin.getWidth();// 获取宽度
                un_hight = btUnLogin.getHeight();// 获取高度
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueAnimator animator = ValueAnimator.ofInt(0, -height);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int curValue = (int) animation.getAnimatedValue();
                        ivLoginTop.layout(ivLoginTop.getLeft(), curValue, ivLoginTop.getRight(), curValue + ivLoginTop.getHeight());
                    }
                });
                animator.setDuration(700);
                animator.start();


              btLogin.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      btLogin.post(new Runnable() {

                          @Override
                          public void run() {
                              width = btLogin.getWidth();// 获取宽度
                              bt_hight = btLogin.getHeight();// 获取高度
                          }
                      });
                      ValueAnimator loginanimator = ValueAnimator.ofInt(bt_hight, heightPixels);

                      loginanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                          @Override
                          public void onAnimationUpdate(ValueAnimator animation) {
                              int curValue = (int) animation.getAnimatedValue();
                              btLogin.layout(btLogin.getLeft(), curValue, btLogin.getRight(), curValue + btLogin.getHeight());
                          }
                      });
                      loginanimator.setDuration(600);
                      loginanimator.start();
                      loginanimator.addListener(mAnimationListener);
                  }
              },200);


                ValueAnimator animatorDown = ValueAnimator.ofInt(un_hight, heightPixels);

                animatorDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int curValue = (int) animation.getAnimatedValue();
                        btUnLogin.layout(btUnLogin.getLeft(), curValue, btUnLogin.getRight(), curValue + btUnLogin.getHeight());
                    }
                });
                animatorDown.setDuration(700);
                animatorDown.start();

                //animatorDown.addListener(mAnimationListener);

            }
        });
     /*   btUnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueAnimator animator = ValueAnimator.ofInt(-height, 0);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int curValue = (int) animation.getAnimatedValue();
                        ivLoginTop.layout(ivLoginTop.getLeft(), curValue, ivLoginTop.getRight(), curValue + ivLoginTop.getHeight());
                    }
                });
                animator.setDuration(300);
                animator.start();
            }
        });*/
    }

    private Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {


        public void onAnimationStart(Animator animation) {

        }

        public void onAnimationEnd(Animator animation) {
            loginFragment=new LoginFragment();
            loginFragment.show(getFragmentManager(),"1212");

        }

        public void onAnimationCancel(Animator animation) {

        }

        public void onAnimationRepeat(Animator animation) {

        }
    };



    @Override
    public void verify() {
        ValueAnimator animator = ValueAnimator.ofInt(-height, 0);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                ivLoginTop.layout(ivLoginTop.getLeft(), curValue, ivLoginTop.getRight(), curValue + ivLoginTop.getHeight());
            }
        });
        animator.setDuration(500);
        animator.start();

        ValueAnimator animatorDown = ValueAnimator.ofInt(heightPixels, un_hight);

        animatorDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                btUnLogin.layout(btUnLogin.getLeft(), curValue, btUnLogin.getRight(), curValue + btUnLogin.getHeight());
            }
        });
        animatorDown.setDuration(600);
        animatorDown.start();

        ValueAnimator loginanimator = ValueAnimator.ofInt(heightPixels, bt_hight-50);

        loginanimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int) animation.getAnimatedValue();
                btLogin.layout(btLogin.getLeft(), curValue, btLogin.getRight(), curValue + btLogin.getHeight());
            }
        });
        loginanimator.setDuration(600);
        loginanimator.start();

    }
}
