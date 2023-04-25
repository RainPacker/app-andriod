package com.tedu.zhongzhao.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.activity.RouteNaviActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 路径规划工具
 * Created by huangyx on 2018/6/27.
 */
public class MapRouteToolView extends RelativeLayout implements View.OnClickListener, AMapNaviListener,
        ViewPager.OnPageChangeListener {
    private final static int ROUTE_TYPE_NONE = 0;
    private final static int ROUTE_TYPE_RIDE = 1;
    private final static int ROUTE_TYPE_WALK = 2;
    private final static int ROUTE_TYPE_DRIVER = 3;
    private final static int ROUTE_TYPE_DRIVER_SINGLE = 4;
    private final static int ROUTE_TYPE_DRIVER_NAVI = 5;

    private View mTopView;
    private View mWalkView;
    private View mRideView;
    private View mDriveView;
    private ImageView mCompassView;
    private RouteStrategyView mStrategyView;
    private ViewPager mViewPager;
    private RouteAdapter mRouteAdapter;

    private MapRouteListener mListener;

    private AMapNavi aMapNavi;
    private int mStrategy = 0;
    private int mRouteType = ROUTE_TYPE_NONE;

    private LatLng mStart, mEnd;
    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();

    public MapRouteToolView(Context context) {
        this(context, null);
    }

    public MapRouteToolView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapRouteToolView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.wgt_map_route_tool_view, this, true);

        mTopView = findViewById(R.id.map_top_layout);
        mTopView.setVisibility(View.GONE);
        mWalkView = findViewById(R.id.map_route_walk_view);
        mWalkView.setOnClickListener(this);
        mRideView = findViewById(R.id.map_route_ride_view);
        mRideView.setOnClickListener(this);
        mDriveView = findViewById(R.id.map_route_drive_view);
        mDriveView.setOnClickListener(this);
        mCompassView = (ImageView) findViewById(R.id.compass_view);
        mCompassView.setOnClickListener(this);
        mStrategyView = (RouteStrategyView) findViewById(R.id.drive_strategy_view);
        mStrategyView.setVisibility(GONE);
        mStrategyView.setCallback(new RouteStrategyView.RouteStrategyCallback() {
            @Override
            public void onGetStrategy(boolean congestion, boolean avoidhightspeed, boolean cost, boolean hightspeed, boolean multiple) {
                if (aMapNavi != null) {
                    int strategy = aMapNavi.strategyConvert(congestion, avoidhightspeed, congestion, hightspeed, multiple);
                    if (!multiple) {
                        routeDrive(ROUTE_TYPE_DRIVER_SINGLE, strategy);
                    } else {
                        routeDrive(ROUTE_TYPE_DRIVER, strategy);
                    }
                }
            }
        });
        mViewPager = (ViewPager) findViewById(R.id.route_view_pager);
        mViewPager.setVisibility(GONE);
        mViewPager.addOnPageChangeListener(this);
    }

    public void init(MapRouteListener listener) {
        this.mListener = listener;
        if (aMapNavi == null) {
            aMapNavi = AMapNavi.getInstance(getContext().getApplicationContext());
            aMapNavi.addAMapNaviListener(this);
        }
    }

    /**
     * 进入导航
     *
     * @param start 开始位置
     * @param end   结束位置
     */
    public void startNavi(LatLng start, LatLng end) {
        reset();
        if (mListener != null) {
            mListener.onStartCalculateRoute();
        }
        mRouteType = ROUTE_TYPE_DRIVER_NAVI;
        List<NaviLatLng> startNavi = new ArrayList<NaviLatLng>();
        startNavi.add(new NaviLatLng(start.latitude, start.longitude));
        List<NaviLatLng> endNavi = new ArrayList<NaviLatLng>();
        endNavi.add(new NaviLatLng(end.latitude, end.longitude));
        aMapNavi.calculateDriveRoute(startNavi, endNavi, null, PathPlanningStrategy.DRIVING_DEFAULT);
    }

    public void startRoute(LatLng start, LatLng end) {
        reset();
        mTopView.setVisibility(VISIBLE);
        this.mStart = start;
        this.mEnd = end;
        routeDrive(ROUTE_TYPE_DRIVER, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT);
    }

    /**
     * 开车路径规划
     *
     * @param type     单路径或多路径
     * @param strategy 策略
     */
    private void routeDrive(int type, int strategy) {
        reset(false);
        mDriveView.setSelected(true);
        mStrategyView.setVisibility(VISIBLE);
        if (mListener != null) {
            mListener.onStartCalculateRoute();
        }
        mRouteType = type;
        mStrategy = strategy;
        List<NaviLatLng> startNavi = new ArrayList<NaviLatLng>();
        startNavi.add(new NaviLatLng(mStart.latitude, mStart.longitude));
        List<NaviLatLng> endNavi = new ArrayList<NaviLatLng>();
        endNavi.add(new NaviLatLng(mEnd.latitude, mEnd.longitude));
        aMapNavi.calculateDriveRoute(startNavi, endNavi, null, strategy);
    }

    private void routeWalk() {
        if (mRouteType != ROUTE_TYPE_WALK) {
            reset();
            mWalkView.setSelected(true);
            mRouteType = ROUTE_TYPE_WALK;
            if (mListener != null) {
                mListener.onStartCalculateRoute();
            }
            aMapNavi.calculateWalkRoute(new NaviLatLng(mStart.latitude, mStart.longitude), new NaviLatLng(mEnd.latitude, mEnd.longitude));
        }
    }

    private void routeRide() {
        if (mRouteType != ROUTE_TYPE_RIDE) {
            reset();
            mRideView.setSelected(true);
            mRouteType = ROUTE_TYPE_RIDE;
            if (mListener != null) {
                mListener.onStartCalculateRoute();
            }
            aMapNavi.calculateRideRoute(new NaviLatLng(mStart.latitude, mStart.longitude), new NaviLatLng(mEnd.latitude, mEnd.longitude));
        }
    }

    private void reset() {
        reset(true);
    }

    private void reset(boolean resetStrategy) {
        mDriveView.setSelected(false);
        mWalkView.setSelected(false);
        mRideView.setSelected(false);
        if (resetStrategy) {
            mStrategyView.reset();
            mStrategyView.setVisibility(GONE);
        }
        mRouteType = ROUTE_TYPE_NONE;
        mStrategy = -1;
        clearRoutes();
    }

    public void show() {
        mTopView.setVisibility(VISIBLE);
    }

    public boolean isShowing() {
        return mTopView.getVisibility() == VISIBLE;
    }

    public void gone() {
        reset();
        mTopView.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.map_route_walk_view:
                routeWalk();
                break;
            case R.id.map_route_ride_view:
                routeRide();
                break;
            case R.id.map_route_drive_view:
                if (mRouteType != ROUTE_TYPE_DRIVER) {
                    reset();
                    mDriveView.setSelected(true);
//                    routeDrive(ROUTE_TYPE_DRIVER, PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT);
                    mStrategyView.setVisibility(VISIBLE);
                }
                break;
            case R.id.compass_view:
                if (mListener != null) {
                    mListener.animateCamera(0);
                }
                break;
        }
    }

    /**
     * 指南针角度动画
     *
     * @param ani
     */
    public void startBearingAnimation(Animation ani) {
        if (ani != null) {
            mCompassView.startAnimation(ani);
        }
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        if (mListener != null) {
            AMap aMap = mListener.getAMap();
            aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
            RouteOverLay routeOverLay = new RouteOverLay(aMap, path, getContext());
            routeOverLay.setTrafficLine(false);
            routeOverLay.addToMap();
            routeOverlays.put(routeId, routeOverLay);
        }
    }

    private void clearRoutes() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
            routeOverlay.destroy();
        }
        routeOverlays.clear();
        mViewPager.setVisibility(GONE);
    }

    private void showRoute(int[] ints) {
        List<MapRouteInfoView> views = new ArrayList<MapRouteInfoView>();
        if (ints.length == 1) {
            AMapNaviPath path = aMapNavi.getNaviPath();
            if (path != null) {
                drawRoutes(ints[0], path);
                MapRouteInfoView view = new MapRouteInfoView(getContext());
                view.setPath(ints[0], path);
                views.add(view);
            }
        } else {
            HashMap<Integer, AMapNaviPath> paths = aMapNavi.getNaviPaths();
            for (int i = 0; i < ints.length; i++) {
                AMapNaviPath path = paths.get(ints[i]);
                if (path != null) {
                    drawRoutes(ints[i], path);
                    MapRouteInfoView view = new MapRouteInfoView(getContext());
                    view.setPath(ints[i], path);
                    views.add(view);
                }
            }
        }
        mRouteAdapter = new RouteAdapter(views);
        mViewPager.setAdapter(mRouteAdapter);
        mViewPager.setVisibility(VISIBLE);
        mViewPager.setCurrentItem(0, false);
        onPageSelected(0);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {
        if (mListener != null) {
            mListener.onCalculateFail();
        }
    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        if (mRouteType != ROUTE_TYPE_NONE) {
//            clearRoutes();
            if (mRouteType == ROUTE_TYPE_DRIVER_SINGLE || mRouteType == ROUTE_TYPE_DRIVER_NAVI) {
                aMapNavi.selectRouteId(ints[0]);
                Intent gpsintent = new Intent(getContext().getApplicationContext(), RouteNaviActivity.class);
                getContext().startActivity(gpsintent);
                if (mListener != null) {
                    mListener.onCalculateSuccess(true);
                }
            } else {
                if (mListener != null) {
                    mListener.onCalculateSuccess(false);
                }
                showRoute(ints);
            }
        }
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        if (mListener != null) {
            mListener.onCalculateFail();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearRoutes();
        if (aMapNavi != null) {
            aMapNavi.removeAMapNaviListener(this);
            aMapNavi.destroy();
        }
        mViewPager.removeOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MapRouteInfoView view = mRouteAdapter.getView(position);
        int routeId = view.getPathId();
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay rol = routeOverlays.get(routeOverlays.keyAt(i));
            if (rol != null) {
                rol.setTransparency(0.4f);
                rol.setZindex(1);
            }
        }
        RouteOverLay curr = routeOverlays.get(routeId);
        if (curr != null) {
            curr.setTransparency(1);
            curr.setZindex(100);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public interface MapRouteListener {
        void onStartCalculateRoute();

        void onCalculateFail();

        void onCalculateSuccess(boolean isStartNavi);

        void animateCamera(float bearing);

        AMap getAMap();
    }

    /**
     * 路径规则Adapter
     * Created by huangyx on 2018/6/29.
     */
    public class RouteAdapter extends PagerAdapter {

        private List<MapRouteInfoView> mViews;

        public RouteAdapter(List<MapRouteInfoView> views) {
            mViews = views;
        }

        @Override
        public int getCount() {
            return mViews == null ? 0 : mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public MapRouteInfoView getView(int position) {
            return mViews.get(position);
        }
    }
}
