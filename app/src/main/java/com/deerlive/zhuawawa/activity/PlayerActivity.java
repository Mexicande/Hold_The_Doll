package com.deerlive.zhuawawa.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.adapter.MessageRecyclerListAdapter;
import com.deerlive.zhuawawa.agora.MyEngineEventHandler;
import com.deerlive.zhuawawa.base.BaseActivity;
import com.deerlive.zhuawawa.common.Api;
import com.deerlive.zhuawawa.common.GlideCircleTransform;
import com.deerlive.zhuawawa.common.ScreenRecorder;
import com.deerlive.zhuawawa.common.SoundUtils;
import com.deerlive.zhuawawa.fragment.RecordFragment;
import com.deerlive.zhuawawa.fragment.RecordZjFragment;
import com.deerlive.zhuawawa.intf.OnRequestDataListener;
import com.deerlive.zhuawawa.model.DanmuMessage;
import com.deerlive.zhuawawa.model.DeviceAndBanner;
import com.deerlive.zhuawawa.model.LampBean;
import com.deerlive.zhuawawa.model.MessageType;
import com.deerlive.zhuawawa.utils.ActivityUtils;
import com.deerlive.zhuawawa.utils.KeyboardUtils;
import com.deerlive.zhuawawa.utils.LogUtils;
import com.deerlive.zhuawawa.utils.SPUtils;
import com.deerlive.zhuawawa.utils.SharedPreferencesUtil;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import io.agora.AgoraAPI;
import io.agora.AgoraAPIOnlySignal;
import io.agora.rtc.Constants;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class PlayerActivity extends BaseActivity implements View.OnTouchListener {



    private String mTag = "PlayerActivity";
    private String mIsOnline = "0";
    private RtcEngine mRtcEngine;
    private AgoraAPIOnlySignal m_agoraAPI;
    private static final int INIT_IM = 100;
    private static final int IM_ONLINE_NUM = 101;
    private static final int IM_ONLINE_LEAVE = 102;
    private static final int IM_ONLINE_JOIN = 103;
    private static final int IM_MSG = 104;
    private static final int IM_MSG_COMING = 105;
    private static final int UPLOAD_RECORD = 106;
    private static final int INIT_FRAME = 1000;
    private static final int SENJION = 11;
    private static final int REQUEST_CODE = 1;//录制
    private int mLocalUid;
    private String mUsername;
    private String mUserAvatar;
    private String mToken;
    private int mRemoteUid = 100000;
    private String mChannleName = "10000";
    private String mSignalKey = "";
    private String mChannelKey = "";
    private String mChannelStream = "";
    private String mChannelStatus;
    private String mmPlayPrice;
    private String mmPlayBalance;
    private String mCurMatchId = "";
    private String mCurPlayerId = "";
    private String mCurPlayerAvatar = "";
    private String mCurPlayerName = "";
    private int mOnlineNum = 0;
    private ArrayList<DanmuMessage> mDanmuData = new ArrayList<>();
    private MessageRecyclerListAdapter mDanmuAdapter;
    private String mBgm = "1";
    private String mYinXiao = "1";
    @Bind(R.id.player_container)
    FrameLayout mPlayerContainer;
    @Bind(R.id.caozuo_container)
    LinearLayout mCaozuoContainer;
    @Bind(R.id.look_container)
    RelativeLayout mLookContainer;
    @Bind(R.id.game_status_ing)
    LinearLayout mImageGameStatus;
    @Bind(R.id.danmu_list)
    RecyclerView mDanmuList;
    @Bind(R.id.mess_content)
    EditText mMessContent;
    @Bind(R.id.message_container)
    RelativeLayout mMessageContainer;
    @Bind(R.id.popup_window_container)
    FrameLayout mPopupWindowContainer;
    @Bind(R.id.popup_window_weizhuazhu_container)
    FrameLayout mWeiZhuaZhuContainer;
    //@Bind(R.id.popup_window_zhuazhu_container)
    //FrameLayout mZhuaZhuContainer;
    @Bind(R.id.play_price)
    TextView mPlayPrice;
    @Bind(R.id.play_balance)
    TextView mPlayBalance;
    @Bind(R.id.player_tips_container)
    LinearLayout mPlayerTipsContainer;
    @Bind(R.id.tips_player_avatar)
    ImageView mTipsPlayerAvatar;
    @Bind(R.id.tips_message)
    TextView mTipsMessage;
    @Bind(R.id.player_num)
    TextView mPlayerNum;
    @Bind(R.id.timer_try_again)
    TextView mTimerTryAgain;
    @Bind(R.id.go_timer_text)
    TextView mGoTimerText;
    @Bind(R.id.camera_change)
    RelativeLayout mCameraChange;
    @Bind(R.id.player_go)
    ImageView mPlayerGo;
    @Bind(R.id.image_caozuo_down)
    ImageView mImageCaozuoDown;
    @Bind(R.id.image_caozuo_up)
    ImageView mImageCaozuoUp;
    @Bind(R.id.image_caozuo_right)
    ImageView mImageCaozuoRight;
    @Bind(R.id.image_caozuo_left)
    ImageView mImageCaozuoLeft;
    private int goTimeLeft = 30;
    private int mTryAgainTimeLeft = 3;
    private SoundPool soundPool;
    private MediaPlayer mMediaPlayer;
    private HashMap<String, Integer> soundID = new HashMap<String, Integer>();
    private MediaProjectionManager mMediaProjectionManager;
    private int mNum=0;
    private LampBean lampBean=new LampBean();
    private SwitchHandler mHandler=new SwitchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle data = getIntent().getExtras();
        if (data == null) {
            toast(getString(R.string.net_error));
            finish();
            return;
        }
        SharedPreferencesUtil.cleanDate(this);
        initDefalut(data);
        initDanmu();
        enterPlayer();
        initTencentPlayer();
        myHandler.sendEmptyMessageDelayed(INIT_IM, 1000);//延迟初始化im
        if ("1".equals(mBgm)) {
            initBgm();
        }
        if ("1".equals(mYinXiao)) {
            soundPool = SoundUtils.getSoundPool();
            soundID.put("move", soundPool.load(this, R.raw.move, 1));
            soundID.put("daojishi", soundPool.load(this, R.raw.daojishi, 1));
            soundID.put("fail", soundPool.load(this, R.raw.fail, 1));
            soundID.put("go", soundPool.load(this, R.raw.go, 1));
            soundID.put("ready", soundPool.load(this, R.raw.ready, 1));
            soundID.put("success", soundPool.load(this, R.raw.success, 1));
        }
        //initScreenRecorder();
        initCaozuoListener();
    }

    private void initCaozuoListener() {
        mImageCaozuoDown.setOnTouchListener(this);
        mImageCaozuoLeft.setOnTouchListener(this);
        mImageCaozuoRight.setOnTouchListener(this);
        mImageCaozuoUp.setOnTouchListener(this);
    }


    private void initBgm() {
        Random r = new Random();
        int result = r.nextInt(3) + 1;
        String u = "bg" + result;
        mMediaPlayer = SoundUtils.playSoundByMedia(getResources().getIdentifier(u, "raw", getPackageName()));
    }

    TXLivePlayer mLivePlayer;
    TXCloudVideoView mPlayView;

    private void initTencentPlayer() {
        //mPlayerView即step1中添加的界面view
        mPlayView = (TXCloudVideoView) findViewById(R.id.player_surface);
        //创建player对象
        mLivePlayer = new TXLivePlayer(this);
        //关键player对象与界面view
        mLivePlayer.setPlayerView(mPlayView);
        mLivePlayer.startPlay(mChannelStream, TXLivePlayer.PLAY_TYPE_LIVE_RTMP);
        mLivePlayer.setPlayListener(new ITXLivePlayListener() {
            @Override
            public void onPlayEvent(int i, Bundle bundle) {


            }

            @Override
            public void onNetStatus(Bundle bundle) {

            }
        });

    }

    private void enterPlayer() {
        Map<String,String> p=new HashMap<>();
        p.put("token", mToken);
        p.put("deviceid", String.valueOf( mRemoteUid));
        Api.enterPlayer(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                JSONObject d = data.getJSONObject("data");
                JSONArray dm = data.getJSONArray("msg");
                mChannelStatus = d.getString("channel_status");
                mmPlayPrice = d.getString("price");
                mPlayPrice.setText("x"+mmPlayPrice);
                mCurPlayerId = d.getString("uid");
                mCurPlayerName = d.getString("user_nicename");
                mCurPlayerAvatar = d.getString("avatar");
                mCurMatchId = d.getString("match_id");
                changeChannelStatus(mChannelStatus, null);
                for (int i = 0; i < dm.size(); i++) {
                    JSONObject jo = dm.getJSONObject(i);
                    DanmuMessage m = new DanmuMessage();
                    m.setMessageType(MessageType.NORMAL);
                    m.setUserName(jo.getString("title"));
                    m.setMessageContent(jo.getString("msg"));
                    addDanmuList(m);
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
                // finish();

            }
        });
    }

    private void initDefalut(Bundle data) {
        mLocalUid = Integer.parseInt(SPUtils.getInstance().getString("id", "0"));
        mUsername = SPUtils.getInstance().getString("user_nicename");
        mSignalKey = SPUtils.getInstance().getString("signaling_key");
        mToken = SPUtils.getInstance().getString("token");
        mUserAvatar = SPUtils.getInstance().getString("avatar");
        mmPlayBalance = SPUtils.getInstance().getString("balance");
        mBgm = SPUtils.getInstance().getString("bgm");
        mYinXiao = SPUtils.getInstance().getString("yinxiao");
        mPlayBalance.setText(mmPlayBalance);
        DeviceAndBanner.InfoBean.DeviceBean item = (DeviceAndBanner.InfoBean.DeviceBean) data.getSerializable("item");
        if((item != null ? item.getDeviceid() : null) !=null){

            mRemoteUid = Integer.parseInt(item.getDeviceid());
            mChannleName = item.getDeviceid();
            mChannelStream = item.getChannel_stream();
        }

        getChannelKey();
    }

    private void getChannelKey() {
        Map<String,String> params=new HashMap<>();
        params.put("token", mToken);
        params.put("deviceid", mChannleName);
        Api.getChannelKey(this, params, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {
                mChannelKey = data.getString("info");
                initAgora();

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }









    /**
     * 在线人数查询
     *
     */
    private void getLamp() {
        Map<String,String> parms=new HashMap<>();
        parms.put("token",mToken);

        Api.getLamp(this, parms, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {

                lampBean = JSON.parseObject(data.toString(), LampBean.class);

                mPlayerNum.setText(lampBean.getRand() + getResources().getString(R.string.play_num));


               /* DanmuMessage d = new DanmuMessage();
                d.setUid(mLocalUid + "");
                d.setUserName(lampBean.getTourist().get(mNum).getName());
                d.setMessageType(MessageType.NORMAL);
                d.setMessageContent("来了");
                addDanmuList(d);

                Message message=Message.obtain();
                message.obj=d;
                message.what=SENJION;*/
                //myHandler.sendEmptyMessage(SENJION);
               // myHandler.sendMessageDelayed(message,5000);

                myHandler.sendEmptyMessageDelayed(SENJION,3000);

            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });
    }

    @OnClick(R.id.mess_send)
    public void messSend(View v) {
        String c = mMessContent.getText().toString();
        if (TextUtils.isEmpty(c)) {
            toast(getResources().getString(R.string.empty_tip));
            return;
        }
        DanmuMessage d = new DanmuMessage();
        d.setUid(mLocalUid + "");
        d.setUserName(mUsername);
        d.setMessageType(MessageType.NORMAL);
        d.setMessageContent(c);
        agoraSend(d);
        addDanmuList(d);
        mMessContent.setText("");
    }

    private void addDanmuList(DanmuMessage d) {
        mDanmuData.add(d);
        mDanmuAdapter.notifyItemInserted(mDanmuData.size());
        mDanmuList.smoothScrollToPosition(mDanmuData.size() - 1);
    }

    private void agoraSend(DanmuMessage d) {
        JSONObject t = new JSONObject();
        t.put("messageType", d.getMessageType());
        t.put("uid", d.getUid());
        t.put("messageContent", d.getMessageContent());
        t.put("userName", d.getUserName());
        t.put("remoteUid", d.getRemoteUid());
        t.put("userAvatar", mUserAvatar);
        m_agoraAPI.messageChannelSend(mChannleName, t.toJSONString(), "");
    }

    private void agoraSendP2p(int caozuo) {
        JSONObject t = new JSONObject();
        t.put("messageType", caozuo);
        t.put("uid", mLocalUid + "");
        t.put("remoteUid", mRemoteUid);
        t.put("playerUid", mLocalUid + "");
        t.put("playerUserName", mUsername);
        t.put("playerAvatar", mUserAvatar);
        t.put("matchID", mCurMatchId);
        if (m_agoraAPI != null) {
            m_agoraAPI.messageInstantSend(mRemoteUid + "", 0, t.toJSONString(), null);
        }
        if ("1".equals(mYinXiao)) {
            switch (caozuo) {
                case MessageType.GAME_LEFT:
                case MessageType.GAME_RIGHT:
                case MessageType.GAME_DOWN:
                case MessageType.GAME_UP:
                    if (soundPool != null) {
                        soundPool.play(soundID.get("move"), 1, 1, 0, 0, 1);
                    }
                    break;
                case MessageType.GAME_OK:
                    if (soundPool != null) {
                        soundPool.play(soundID.get("go"), 1, 1, 0, 0, 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void initDanmu() {
        LinearLayoutManager m = new LinearLayoutManager(this);
        m.setOrientation(LinearLayoutManager.VERTICAL);
        mDanmuList.setLayoutManager(m);
        mDanmuAdapter = new MessageRecyclerListAdapter(this, mDanmuData);
        mDanmuList.setAdapter(mDanmuAdapter);
        getLamp();

    }

    SurfaceView remoteVideoView;

    private void initAgora() {
        try {
            mRtcEngine = RtcEngine.create(this, getResources().getString(R.string.agora_appid), new MyEngineEventHandler(this, myHandler));
            mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            //mRtcEngine.disableVideo();
            mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P, true);
            mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER, "");
            mRtcEngine.setVideoQualityParameters(true);

            mRtcEngine.muteLocalAudioStream(true);
            mRtcEngine.muteAllRemoteAudioStreams(true);
            mRtcEngine.disableAudio();
            mRtcEngine.setEnableSpeakerphone(false);
            remoteVideoView = RtcEngine.CreateRendererView(getApplicationContext());
            remoteVideoView.setId(R.id.videoId);
            remoteVideoView.setZOrderOnTop(true);
            remoteVideoView.setZOrderMediaOverlay(true);
            mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteVideoView, VideoCanvas.RENDER_MODE_HIDDEN, mRemoteUid));
            //FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            //mPlayerContainer.addView(remoteVideoView,p);
            //mRtcEngine.joinChannel(mChannelKey, mChannleName, null, mLocalUid);


        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }



    private void initAgoraIm() {
        m_agoraAPI = AgoraAPIOnlySignal.getInstance(this, getResources().getString(R.string.agora_appid));
        int sdkVersion = m_agoraAPI.getSdkVersion();

        if (m_agoraAPI.isOnline() == 0) {
            m_agoraAPI.login2(getResources().getString(R.string.agora_appid), mLocalUid + "", mSignalKey, 0, "", 5, 5);
        } else {
            m_agoraAPI.channelJoin(mChannleName);
        }
        m_agoraAPI.callbackSet(new AgoraAPI.CallBack() {
            @Override
            public void onLoginSuccess(int uid, int fd) {
                log("login successfully");
                m_agoraAPI.channelJoin(mChannleName);
            }

            @Override
            public void onLoginFailed(int ecode) {
                log("login onLoginFailed");
            }

            @Override
            public void onLogout(int ecode) {
                log("onLogout");
            }

            @Override
            public void onLog(String txt) {
            }

            @Override
            public void onChannelJoined(String chanID) {
                //自己加入成功的回调
                log(chanID);
                Message m = myHandler.obtainMessage();
                m.what = IM_MSG_COMING;
                m.sendToTarget();
                m_agoraAPI.channelQueryUserNum(chanID);

            }

            @Override
            public void onChannelUserJoined(String account, int uid) {
                log(account + ":" + (long) (uid & 0xffffffffl) + " joined");
                Message m = myHandler.obtainMessage();
                m.what = IM_ONLINE_JOIN;
                m.sendToTarget();
            }

            @Override
            public void onChannelJoinFailed(String chanID, int ecode) {
                //自己加入失败的回调
                log("Join " + chanID + " failed : ecode " + ecode);
            }

            @Override
            public void onChannelUserLeaved(String account, int uid) {
                //所有人收到
                log(account + ":" + (long) (uid & 0xffffffffl) + " leaved");
                Message m = myHandler.obtainMessage();
                m.what = IM_ONLINE_LEAVE;
                m.sendToTarget();
            }

            @Override
            public void onChannelUserList(String[] accounts, int[] uids) {
                log("Channel user list:");
                for (int i = 0; i < accounts.length; i++) {
                    long uid = uids[i] & 0xffffffffl;
                    log(accounts[i] + ":" + (long) (uid & 0xffffffffl));
                }
            }


            @Override
            public void onMessageChannelReceive(String channelID, String account, int uid, String msg) {
                //所有成员
                log("onMessageChannelReceive " + channelID + " " + account + " : " + msg);
                if (!account.equals(mLocalUid + "")) {
                    //toast(msg);
                    Message m = myHandler.obtainMessage();
                    Bundle d = new Bundle();
                    d.putString("msg", msg);
                    m.setData(d);
                    m.what = IM_MSG;
                    m.sendToTarget();
                }
            }

            @Override
            public void onMessageInstantReceive(String account, int uid, String msg) {
                //收到对方消息点对点
                log("onMessageInstantReceive ");
                //toast(uid+"--"+account+"---"+msg);
                JSONObject data = (JSONObject) JSON.parse(msg);
                int t = data.getIntValue("messageType");
                Message m = myHandler.obtainMessage();
                m.what = t;
                m.sendToTarget();

            }

            @Override
            public void onMessageSendError(String messageID, int ecode) {
                super.onMessageSendError(messageID, ecode);
                log("onMessageSendError ");
            }

            @Override
            public void onMessageSendSuccess(String messageID) {
                super.onMessageSendSuccess(messageID);
                log("onMessageSendSuccess ");
            }

            @Override
            public void onChannelQueryUserNumResult(String channelID, int ecode, int num) {
                super.onChannelQueryUserNumResult(channelID, ecode, num);
                //查询在线人数
                Message m = myHandler.obtainMessage();
                m.what = IM_ONLINE_NUM;
                mOnlineNum = num;
                m.sendToTarget();

            }
        });
    }

    private void log(String msg) {
        Log.i("========", msg);
    }

    public void goBack(View v) {
        finish();
    }

    public void goCharge(View v) {
        ActivityUtils.startActivity(ChargeActivity.class);
    }

    @OnClick(R.id.game_status_ing)
    public void gameStatusIng(View v) {
        startRequestmatchId();
    }

    private void startRequestmatchId() {
        mCurMatchId = "";
        Map<String,String> p=new HashMap<>();

        p.put("token", mToken);
        p.put("deviceid", String.valueOf(mRemoteUid));

        Api.requestConnectDevice(this, p, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject data) {

                String status = data.getString("status");
                JSONObject j = data.getJSONObject("data");


                //成功申请到matchid  准备上机
                if ("1".equals(status)) {
                    mCurMatchId = j.getString("match_id");
                    if (!TextUtils.isEmpty(j.getString("balance"))) {
                        SPUtils.getInstance().put("balance", j.getString("balance"));
                        mmPlayBalance = j.getString("balance");
                    }
                    mPlayBalance.setText(mmPlayBalance);
                    mCurPlayerId = mLocalUid + "";
                    mCurPlayerName = mUsername;
                    mCurPlayerAvatar = mUserAvatar;
                    agoraSendP2p(MessageType.GAME_CONNECT);
                    mTipsMessage.setText(getString(R.string.is_connecting));
                    startTimerEnsture();
                    Glide.with(PlayerActivity.this).load(mCurPlayerAvatar)
                            .error(R.mipmap.logo)
                            .transform(new GlideCircleTransform(PlayerActivity.this))
                            .into(mTipsPlayerAvatar);
                    mImageGameStatus.setEnabled(false);
                    mPlayerTipsContainer.setVisibility(View.VISIBLE);
                  /*  gifDrawable.reset();
                    gifDrawable.start();*/

                    //播放器暂停 然后加入视频通信
                    //changeTongXinMode();
                }
                //有人在玩
                if ("0".equals(status)) {
                    mCurPlayerId = j.getString("uid");
                    mCurPlayerAvatar = j.getString("avatar");
                    mCurPlayerName = j.getString("user_nicename");
                    changeChannelStatus("3", null);
                }

            }

            @Override
            public void requestFailure(int code, String msg) {
                toast(msg);
            }
        });
    }

    public void showSend(View v) {
        if (mMessageContainer.getVisibility() == View.VISIBLE) {
            mMessageContainer.setVisibility(View.GONE);
        } else {
            mMessageContainer.setVisibility(View.VISIBLE);
            KeyboardUtils.showSoftInput(mMessContent);
        }
    }

    private static class SwitchHandler extends Handler {
        private WeakReference<PlayerActivity> mWeakReference;

        SwitchHandler(PlayerActivity activity) {
            mWeakReference = new WeakReference<PlayerActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            PlayerActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case REQUEST_CODE:
                        activity.myHandler.sendEmptyMessageDelayed(SENJION,3000);
                        break;

                }
            }

        }
    }



    RecordZjFragment rf;
    public void showRecord(View v) {


        RecordFragment recordFragment = RecordFragment.newInstance(mChannleName);

        recordFragment.show(getFragmentManager(),"recordFragment");

    }

    public void tryAgain(View v) {
        mWeiZhuaZhuContainer.setVisibility(View.GONE);
        if (mTryAgainDaoJishi != null) {
            mTryAgainDaoJishi.cancel();
        }
        startRequestmatchId();
    }

    public void caozuo_up(View v) {
        agoraSendP2p(MessageType.GAME_UP);
    }

    public void caozuo_down(View v) {
        agoraSendP2p(MessageType.GAME_DOWN);
    }

    public void caozuo_left(View v) {
        agoraSendP2p(MessageType.GAME_LEFT);
    }

    public void caozuo_right(View v) {
        agoraSendP2p(MessageType.GAME_RIGHT);
    }

    public void caozuo_stop(View v) {
        agoraSendP2p(MessageType.GAME_STOP_UP);
    }

    public void caozuo_ok(View v) {
        v.setClickable(false);
        agoraSendP2p(MessageType.GAME_GO);
        if (null != mPlayerDaoJishi) {
            mPlayerDaoJishi.cancel();
        }


    }


    public void caozuo_camera(View v) {
        agoraSendP2p(MessageType.GAME_CAMERA);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_player;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRtcEngine != null && "1".equals(mIsOnline)) {
            mRtcEngine.leaveChannel();
            mRtcEngine = null;
        }
        if (m_agoraAPI != null && m_agoraAPI.isOnline() == 1) {
            m_agoraAPI.channelLeave(mChannleName);
        }
        if (mLivePlayer != null) {
            mLivePlayer.stopPlay(true);
            mLivePlayer = null;
            mPlayView.onDestroy();
            mPlayView = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        myHandler.removeMessages(SENJION);
        myHandler.removeCallbacksAndMessages(null);
        //closeRecord();
    }

    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case INIT_FRAME:
                    FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    if (mPlayerContainer.findViewById(R.id.videoId) == null) {
                        mPlayerContainer.addView(remoteVideoView, p);
                    }
                    remoteVideoView.setLayoutParams(p);
                    if (soundPool != null) {
                        soundPool.play(soundID.get("ready"), 1, 1, 0, 0, 1);
                    }
                    break;
                case IM_MSG_COMING:
                    sendLaile();
                    break;
                case MessageType.GAME_CONNECTTED:
                    changeChannelStatus("5", null);
                   // startRecord();
                    break;
                case INIT_IM:
                    initAgoraIm();
                    break;
                case IM_ONLINE_NUM:
                   // mPlayerNum.setText(mOnlineNum + getResources().getString(R.string.play_num));
                    break;
                case IM_ONLINE_LEAVE:
                   // mPlayerNum.setText((mOnlineNum--) + getResources().getString(R.string.play_num));
                    break;
                case IM_ONLINE_JOIN:
                   // mPlayerNum.setText(String.valueOf(mNum++));
                    //mPlayerNum.setText((mOnlineNum++) + getResources().getString(R.string.play_num));
                    break;
                case UPLOAD_RECORD:
                    //uploadRecord();
                    break;
                case SENJION:
                    if(lampBean.getTourist()!=null||lampBean.getTourist().size()<mNum){
                        DanmuMessage d = new DanmuMessage();
                        d.setUid(mLocalUid + "");
                        d.setUserName(lampBean.getTourist().get(mNum).getName());
                        d.setMessageType(MessageType.NORMAL);
                        d.setMessageContent("来了");
                        addDanmuList(d);
                        mNum++;
                        mHandler.sendEmptyMessage(REQUEST_CODE);
                    }else {
                        myHandler.removeMessages(SENJION);
                    }


                    //uploadRecord();
                    break;
                case IM_MSG://收到广播消息
                    Bundle d = msg.getData();
                    if (d != null && active) {
                        JSONObject j = JSON.parseObject(d.getString("msg"));
                        switch (j.getIntValue("messageType")) {
                            case MessageType.NORMAL:
                                addDanmuItem(j);
                                break;
                            case MessageType.GAME_STATUS_BUSY:
                                changeChannelStatus("3", j);
                                break;
                            case MessageType.GAME_NO:
                                meiZhuazhu(j);
                                break;
                            case MessageType.GAME_OK:
                                zhuaZhu(j);
                                break;
                            case MessageType.GAME_DISCONNECTTED:
                                mCaozuoContainer.setVisibility(View.GONE);
                                mPlayerContainer.setVisibility(View.VISIBLE);
                                mImageGameStatus.setEnabled(false);
                                toast(getResources().getString(R.string.device_error));
                                break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void sendLaile() {
        DanmuMessage d = new DanmuMessage();
        d.setUid(mLocalUid + "");
        d.setUserName(mUsername);
        d.setMessageType(MessageType.NORMAL);
        d.setMessageContent(getResources().getString(R.string.coming));
        agoraSend(d);
        addDanmuList(d);
    }

    private void changeChannelStatus(String channel_status, JSONObject j) {
        if (active) {
            switch (channel_status) {
                //空闲
                case "2":
                    mCurPlayerId = "";
                    mCaozuoContainer.setVisibility(View.GONE);
                    mLookContainer.setVisibility(View.VISIBLE);
                    mImageGameStatus.setEnabled(true);
                    mPlayerTipsContainer.setVisibility(View.GONE);
                    break;
                //游戏中
                case "3":
                    if (j != null) {
                        mCurPlayerId = j.getString("playerUid");
                        mCurPlayerName = j.getString("playerUserName");
                        mCurPlayerAvatar = j.getString("playerAvatar");
                    }
                    if (mCurPlayerId.equals(mLocalUid + "")) {
                        mCaozuoContainer.setVisibility(View.VISIBLE);
                        mLookContainer.setVisibility(View.GONE);
                        mPlayerTipsContainer.setVisibility(View.GONE);
                        return;
                    }//如果是自己下面的就不需要
                    mImageGameStatus.setEnabled(false);
                    mCaozuoContainer.setVisibility(View.GONE);
                    mLookContainer.setVisibility(View.VISIBLE);
                    mPlayerTipsContainer.setVisibility(View.VISIBLE);
                    mTipsMessage.setText(mCurPlayerName + getResources().getString(R.string.playing));
                    Glide.with(this).load(mCurPlayerAvatar)
                            .error(R.mipmap.logo)
                            .transform(new GlideCircleTransform(this))
                            .into(mTipsPlayerAvatar);
                    break;
                //我成功上机，但是还没连接成功
                case "4":
                    //changeTongXinMode();
                    mCaozuoContainer.setVisibility(View.GONE);
                    mLookContainer.setVisibility(View.VISIBLE);
                    mImageGameStatus.setEnabled(true);
                    mPlayerTipsContainer.setVisibility(View.GONE);
                    //startTimerPlay();
//                mmPlayBalance = (Integer.parseInt(mmPlayBalance) - Integer.parseInt(mmPlayPrice))+"";
//                SPUtils.getInstance().put("balance",mmPlayBalance);
//                mPlayBalance.setText(mmPlayBalance);
                    break;
                //我成功上机
                case "5":
                    changeTongXinMode();
                    mCaozuoContainer.setVisibility(View.VISIBLE);
                    mLookContainer.setVisibility(View.GONE);
                    mImageGameStatus.setEnabled(false);
                    mPlayerTipsContainer.setVisibility(View.GONE);

                   /* if (gif_view.getVisibility() == View.GONE) {
                        gif_view.setVisibility(View.VISIBLE);
                    }*/

                    startTimerPlay();
                    if (TextUtils.isEmpty(mmPlayBalance) || TextUtils.isEmpty(mmPlayPrice)) {
                        toast(getString(R.string.data_error));
                        return;
                    }
                    mmPlayBalance = (Integer.parseInt(mmPlayBalance) - Integer.parseInt(mmPlayPrice)) + "";
                    SPUtils.getInstance().put("balance", mmPlayBalance);
                    mPlayBalance.setText(mmPlayBalance);
                    if (mTimerEnture != null) {
                        mTimerEnture.cancel();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void addDanmuItem(JSONObject j) {
        DanmuMessage dm = new DanmuMessage();
        dm.setUid(j.getString("uid"));
        dm.setUserName(j.getString("userName"));
        dm.setMessageType(MessageType.NORMAL);
        dm.setMessageContent(j.getString("messageContent"));
        addDanmuList(dm);
    }

    Dialog ds;

    private void zhuaZhu(JSONObject j) {
        mCurPlayerId = "";
        DanmuMessage d = new DanmuMessage();
        d.setUid(j.getString("playerUid"));
        d.setUserName(j.getString("playerUserName"));
        d.setMessageType(MessageType.GAME_OK);
        d.setMessageContent(getResources().getString(R.string.play_zhuazhu_tips));
        addDanmuList(d);
        showDanmuAnim(d);
        changeChannelStatus("2", null);

        //如果是自己在玩，游戏结束需要离开视频通信 播放rtmp
        if ((mLocalUid + "").equals(j.getString("playerUid"))) {
            changeVideoMode();
            //mZhuaZhuContainer.setVisibility(View.VISIBLE);
            if (ds == null) {
                ds = new MaterialDialog.Builder(this)
                        .widgetColor(getResources().getColor(R.color.colorPrimary))
                        .backgroundColorRes(R.color.trans)
                        .customView(R.layout.window_play_zhuazhong, false).show();
            } else {
                ds.show();
            }
            if (soundPool != null) {
                soundPool.play(soundID.get("success"), 1, 1, 0, 0, 1);
            }
            //closeRecord();
        }
    }

    private void meiZhuazhu(JSONObject j) {
        mCurPlayerId = "";
        DanmuMessage d = new DanmuMessage();
        d.setUid(j.getString("playerUid"));
        d.setUserName(j.getString("playerUserName"));
        d.setMessageType(MessageType.GAME_NO);
        d.setMessageContent(getResources().getString(R.string.play_meizhuazhu_tips));
        addDanmuList(d);
        showDanmuAnim(d);
        if ((mLocalUid + "").equals(j.getString("playerUid"))) {
            mWeiZhuaZhuContainer.setVisibility(View.VISIBLE);
            startTimerTryAgain();
            if (soundPool != null) {
                soundPool.play(soundID.get("fail"), 1, 1, 0, 0, 1);
            }
           // closeRecord();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //延迟三秒 如果这个时候还没人玩
                    if ("".equals(mCurPlayerId)) {
                        changeChannelStatus("2", null);
                    } else {
                        //已经有人接着玩
                        changeChannelStatus("3", null);
                    }
                }
            }, 3000);
        }
    }

    //下抓倒计时
    CountDownTimer mPlayerDaoJishi;

    private void startTimerPlay() {
        goTimeLeft = 30;
        mPlayerDaoJishi = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                goTimeLeft--;
                if (goTimeLeft > 0) {
                    mGoTimerText.setText(goTimeLeft + "");
                }
                if (goTimeLeft < 10 && goTimeLeft > 0) {
                    if (soundPool != null) {
                        soundPool.play(soundID.get("daojishi"), 1, 1, 0, 0, 1);
                    }
                }
            }

            @Override
            public void onFinish() {
                mGoTimerText.setText("0");
                agoraSendP2p(MessageType.GAME_GO);
            }
        }.start();
    }

    CountDownTimer mTryAgainDaoJishi;

    private void startTimerTryAgain() {
        mTryAgainTimeLeft = 3;
        mTryAgainDaoJishi = new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                if (mTryAgainTimeLeft-- > 0) {
                    mTimerTryAgain.setText(mTryAgainTimeLeft + "");
                }
            }

            @Override
            public void onFinish() {
                mWeiZhuaZhuContainer.setVisibility(View.GONE);
                changeChannelStatus("2", null);
                //如果是自己在玩，游戏结束需要离开视频通信 播放rtmp
                changeVideoMode();

            }
        }.start();
    }

    CountDownTimer mTimerEnture;

    private void startTimerEnsture() {
        mTimerEnture = new CountDownTimer(8000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                toast(getString(R.string.net_error));
                mImageGameStatus.setEnabled(true);
            }
        }.start();
    }

    private void changeVideoMode() {

        if (mLivePlayer != null) {
            mLivePlayer.setMute(false);
        }
        mIsOnline = "0";
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        remoteVideoView.setLayoutParams(p);
        mCameraChange.setVisibility(View.GONE);
        //gif_view.setVisibility(View.GONE);

    }

    private void changeTongXinMode() {
        mPlayerGo.setClickable(true);
        if (mLivePlayer != null) {
            mLivePlayer.setMute(true);
        }
        if (mRtcEngine != null) {
            mRtcEngine.setupRemoteVideo(new VideoCanvas(remoteVideoView, VideoCanvas.RENDER_MODE_HIDDEN, mRemoteUid));
            mRtcEngine.muteLocalVideoStream(true);
            mRtcEngine.joinChannel(mChannelKey, mChannleName, null, mLocalUid);
        }
        mIsOnline = "1";

        mCameraChange.setVisibility(View.VISIBLE);
    }

    public void resultYesGo(View v) {
        //mZhuaZhuContainer.setVisibility(View.GONE);
        if (ds != null) {
            ds.dismiss();
        }
        ActivityUtils.startActivity(WeiQuListActivity.class);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMessageContainer.getVisibility() == View.VISIBLE) {
            if (isShouldHideKeyboard(mMessageContainer, event)) {
                mMessageContainer.setVisibility(View.GONE);
            }
        }

        if (rf != null && rf.isVisible()) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.hide(rf);
            transaction.commit();
        }

        if (mWeiZhuaZhuContainer.getVisibility() == View.VISIBLE) {
            mWeiZhuaZhuContainer.setVisibility(View.GONE);
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }


    public void showDanmuAnim(DanmuMessage model) {
        if (active) {
            final View giftPop = View.inflate(this, R.layout.item_danmu_pop, null);
            ImageView giftAvatar = (ImageView) giftPop.findViewById(R.id.gift_pop_avatar);
            TextView giftUserName = (TextView) giftPop.findViewById(R.id.gift_pop_username);
            TextView giftContent = (TextView) giftPop.findViewById(R.id.gift_pop_content);
            Glide.with(this).load(mCurPlayerAvatar)
                    .error(R.mipmap.logo)
                    .into(giftAvatar);
            giftUserName.setText(model.getUserName());
            giftContent.setText(model.getMessageContent());
            mPlayerContainer.addView(giftPop);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.danmu_enter);
            giftPop.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            mPlayerContainer.removeView(giftPop);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void playerOnClick(View v) {
        Bundle d = new Bundle();
        d.putString("userId", mCurPlayerId);
        ActivityUtils.startActivity(d, UserCenterOtherActivity.class);
    }

    @Override
    public void onPause() {
        super.onPause();
       // closeRecord();
        myHandler.removeMessages(SENJION);
    }

    @Override
    public void onResume() {
        super.onResume();
        myHandler.removeMessages(SENJION);
    }

  /*  File file = null;//录制的文件名

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            Log.e("@@", "media projection is null");
            return;
        }
        // video size
        final int width = 480;
        final int height = 640;
        file = new File(Environment.getExternalStorageDirectory(),
                "record-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");
        final int bitrate = 2000000;
        mRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
        mRecorder.start();
    }*/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.image_caozuo_down:
                    caozuo_down(v);
                    mImageCaozuoDown.setImageResource(R.mipmap.iv_down_press);
                    break;
                case R.id.image_caozuo_up:
                    caozuo_up(v);
                    mImageCaozuoUp.setImageResource(R.mipmap.iv_up_press);
                    break;
                case R.id.image_caozuo_left:
                    caozuo_left(v);
                    mImageCaozuoLeft.setImageResource(R.mipmap.iv_left_press);
                    break;
                case R.id.image_caozuo_right:
                    caozuo_right(v);
                    mImageCaozuoRight.setImageResource(R.mipmap.iv_right_press);
                    break;
                default:
                    break;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            caozuo_stop(v);
            mImageCaozuoDown.setImageResource(R.mipmap.iv_down_default);
            mImageCaozuoUp.setImageResource(R.mipmap.iv_up_default);
            mImageCaozuoLeft.setImageResource(R.mipmap.iv_left_default);
            mImageCaozuoRight.setImageResource(R.mipmap.iv_right_default);
        }
        return true;
    }


}
