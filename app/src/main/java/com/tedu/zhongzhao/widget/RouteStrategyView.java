package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tedu.zhongzhao.R;
import com.tedu.base.util.AndroidUtils;

/**
 * 驾车路径规划偏好选择
 * Created by huangyx on 2018/5/1.
 */
public class RouteStrategyView extends LinearLayout implements View.OnClickListener {

    private View mCongstView, mCostView, mAvoidHightSpeedView, mHightSpeedView;

    private RouteStrategyCallback mCallback;

    RouteStrategyView(Context context) {
        this(context, null);
    }

    public RouteStrategyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RouteStrategyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.bg_drive_type);
        LayoutInflater.from(context).inflate(R.layout.wgt_route_strategy_layout, this, true);

        mCongstView = findViewById(R.id.route_congestion_view);
        mCongstView.setOnClickListener(this);
        mCongstView.setSelected(false);
        mCostView = findViewById(R.id.route_cost_view);
        mCostView.setOnClickListener(this);
        mCostView.setSelected(false);
        mAvoidHightSpeedView = findViewById(R.id.route_avoidhightspeed_view);
        mAvoidHightSpeedView.setOnClickListener(this);
        mAvoidHightSpeedView.setSelected(false);
        mHightSpeedView = findViewById(R.id.route_hightspeed_view);
        mHightSpeedView.setOnClickListener(this);
        mHightSpeedView.setSelected(false);

        findViewById(R.id.route_single_view).setOnClickListener(this);
        findViewById(R.id.route_multiple_view).setOnClickListener(this);
    }

    public void reset(){
        mCongstView.setSelected(false);
        mCostView.setSelected(false);
        mAvoidHightSpeedView.setSelected(false);
        mHightSpeedView.setSelected(false);
    }

    public void setCallback(RouteStrategyCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_congestion_view:
                mCongstView.setSelected(!mCongstView.isSelected());
                break;
            case R.id.route_cost_view:
                mCostView.setSelected(!mCostView.isSelected());
                if (mCostView.isSelected()) {
                    mHightSpeedView.setSelected(false);
                }
                break;
            case R.id.route_avoidhightspeed_view:
                mAvoidHightSpeedView.setSelected(!mAvoidHightSpeedView.isSelected());
                if (mAvoidHightSpeedView.isSelected()) {
                    mHightSpeedView.setSelected(false);
                }
                break;
            case R.id.route_hightspeed_view:
                mHightSpeedView.setSelected(!mHightSpeedView.isSelected());
                if (mHightSpeedView.isSelected()) {
                    mAvoidHightSpeedView.setSelected(false);
                    mCostView.setSelected(false);
                }
                break;
            case R.id.route_multiple_view:
                mCallback.onGetStrategy(mCongstView.isSelected(), mAvoidHightSpeedView.isSelected(),
                        mCostView.isSelected(), mHightSpeedView.isSelected(), true);
                break;
            case R.id.route_single_view:
                mCallback.onGetStrategy(mCongstView.isSelected(), mAvoidHightSpeedView.isSelected(),
                        mCostView.isSelected(), mHightSpeedView.isSelected(), false);
                break;
        }
    }

    public interface RouteStrategyCallback {
        void onGetStrategy(boolean congestion, boolean avoidhightspeed, boolean cost, boolean hightspeed, boolean multiple);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCallback = null;
    }
}
