package com.deerlive.zhuawawa.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deerlive.zhuawawa.R;
import com.deerlive.zhuawawa.intf.OnRecyclerViewItemClickListener;
import com.deerlive.zhuawawa.model.DeviceAndBanner;
import com.deerlive.zhuawawa.model.Game;
import com.deerlive.zhuawawa.utils.ScreenUtils;
import com.deerlive.zhuawawa.utils.SizeUtils;
import com.deerlive.zhuawawa.view.supertextview.SuperButton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GameRecyclerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;

    private static final int TYPE_FOOTER = 1;

    private static final int TYPE_ITEM = 2;

    private View mHeaderView;

    private View mFooterView;

    private Context mContext;
    private ArrayList<DeviceAndBanner.InfoBean.DeviceBean> mItems;
    private int imageWidth;
    private int imageHeight;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;
    public GameRecyclerListAdapter(Context mContext, ArrayList<DeviceAndBanner.InfoBean.DeviceBean> mVideoItems) {

        this.mContext = mContext;
        this.mItems = mVideoItems;
        imageHeight = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15))/2;
        imageWidth = imageHeight ;

    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        mOnRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            mHeaderView = headerView;
            notifyItemInserted(0);
        }

    }



    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            mFooterView = footerView;
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public boolean haveHeaderView() {
        return mHeaderView != null;
    }

    public boolean haveFooterView() {
        return mFooterView != null;
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new FooterHolder(mFooterView);
        } else if (viewType == TYPE_HEADER) {
            return new HeaderHolder(mHeaderView);
        } else {
            return new GameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {

        } else if (holder instanceof FooterHolder) {

        } else {
           final GameViewHolder temp = (GameViewHolder)holder;
            DeviceAndBanner.InfoBean.DeviceBean t = mItems.get(position-1);
            temp.mGameName.setText(t.getChannel_title());
            Glide.with(mContext).load(t.getThumb())
                    .error(R.mipmap.logo)
                    .into(temp.mGameIcon);
            temp.mGamePrice.setText(t.getPrice()+"币 / 次");
            setGameStatus(t.getChannel_status(),temp.mGameStatus);
            //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(imageWidth, imageHeight);
            FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight+ SizeUtils.dp2px(40));
            //temp.mGameIcon.setLayoutParams(params);
            temp.mContainer.setLayoutParams(params1);

            temp.mContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnRecyclerViewItemClickListener != null) {
                        int layoutPosition = temp.getLayoutPosition() - getHeadViewSize();
                        mOnRecyclerViewItemClickListener.onRecyclerViewItemClick(v, layoutPosition);
                    }
                }
            });
        }
    }

    private void setGameStatus(String mGameStatus,SuperButton v) {
        switch (mGameStatus){
            case "3": //游戏中
                v.setVisibility(View.VISIBLE);
                v.setText(mContext.getResources().getString(R.string.tv_playing));
                v.setShapeType(SuperButton.RECTANGLE).
                    setShapeSolidColor(mContext.getResources().getColor(R.color.colorPrimary))
                        .setUseShape();
                break;
            default:
                v.setVisibility(View.VISIBLE);
                v .setText(mContext.getResources().getString(R.string.tv_free));
                v.setShapeType(SuperButton.RECTANGLE)
                        .setShapeSolidColor(mContext.getResources().getColor(R.color.blue))
                        .setUseShape();
                break;
        }
    }


    private int getHeadViewSize() {
        return mHeaderView == null ? 0 : 1;
    }
    @Override
    public int getItemCount() {
        int count = (mItems == null ? 0 : mItems.size());
        if (mFooterView != null) {
            count++;
        }

        if (mHeaderView != null) {
            count++;
        }
        return count;
    }


    class HeaderHolder extends RecyclerView.ViewHolder {

        public HeaderHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    class GameViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_game_container)
        CardView mContainer;
        @Bind(R.id.game_icon)
        ImageView mGameIcon;
        @Bind(R.id.item_game_name)
        TextView mGameName;
        @Bind(R.id.game_price)
        TextView mGamePrice;
        @Bind(R.id.game_status)
        SuperButton mGameStatus;
        public GameViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
