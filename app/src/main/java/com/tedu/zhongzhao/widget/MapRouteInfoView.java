package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.NaviPath;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.utils.StringUtils;

/**
 * 路径信息View
 * Created by huangyx on 2018/6/30.
 */
public class MapRouteInfoView extends LinearLayout {

    private TextView mTitleView;
    private TextView mContentView;

    private int mPathId;
    private AMapNaviPath mPath;

    public MapRouteInfoView(Context context) {
        this(context, null);
    }

    public MapRouteInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapRouteInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(context).inflate(R.layout.wgt_route_info_layout, this, true);

        mTitleView = (TextView) findViewById(R.id.route_title_view);
        mContentView = (TextView) findViewById(R.id.route_info_view);

    }

    public void setPath(int pathId, AMapNaviPath path) {
        this.mPathId = pathId;
        this.mPath = path;
        mTitleView.setText("路径ID:" + pathId + " | 路径策略:" + path.getStrategy());
        mContentView.setText("长度:" + StringUtils.formatMileage(path.getAllLength()) + " | 预估时间:"
                + StringUtils.formatTime(path.getAllTime()) + " | 分段数:" + path.getStepsCount() + " | 费用:" + path.getTollCost() + "元");
    }

    public int getPathId() {
        return mPathId;
    }

    public AMapNaviPath getPath() {
        return mPath;
    }
}
