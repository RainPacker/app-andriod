package com.tedu.zhongzhao.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AmapNaviPage;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.adapter.PoiMarkerAdapter;
import com.tedu.zhongzhao.map.PoiInfo;
import com.tedu.zhongzhao.ui.PageInfo;
import com.tedu.zhongzhao.ui.UiPageConstans;
import com.tedu.zhongzhao.ui.UiUtil;
import com.tedu.zhongzhao.utils.JsonUtil;
import com.tedu.zhongzhao.utils.TempIntentData;
import com.tedu.zhongzhao.utils.ToastUtil;
import com.tedu.zhongzhao.widget.MapRouteToolView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图Activity
 * Created by huangyx on 2018/4/15.
 */
public class MapActivity extends BaseActivity implements MapRouteToolView.MapRouteListener {

    private MapView mMapView;
    private AMap mAMap;
    private MapRouteToolView mapRouteToolView;
    private List<MarkerOptions> mMarkers;
    private Marker mShowWindowMarker;
    private PoiMarkerAdapter mMarkerAdapter;

    // 地图角度
    private float lastBearing;

    private PageInfo mPageInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_map_layout);

        mPageInfo = (PageInfo) TempIntentData.getData(getPageInfoKey());
        String act = getIntent().getStringExtra(BaseActivity.KEY_PAGE_ACT);
        if (TextUtils.isEmpty(act)) {
            act = mPageInfo.getAct();
        }
        setExitAni(act);
        UiUtil.initTitleBar(mTitleBarView, mPageInfo);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        // 显示室内地图
        mAMap.showIndoorMap(true);
        mAMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                if (UiPageConstans.VIEW_MAP_POI.equals(mPageInfo.getControl())) {
                    String param = getIntent().getStringExtra("annotations");
                    if (!TextUtils.isEmpty(param)) {
                        try {
                            JSONArray array = new JSONArray(param);
                            if (array.length() > 0) {
                                List<PoiInfo> pois = new ArrayList<PoiInfo>();
                                for (int i = 0; i < array.length(); i++) {
                                    PoiInfo p = JsonUtil.fromJson(array.getString(i), PoiInfo.class);
                                    if (p != null) {
                                        pois.add(p);
                                    }
                                }
                                showPoi(pois);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                startIvCompass(cameraPosition.bearing);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mShowWindowMarker = marker;
                return true;
            }
        });
        mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mapRouteToolView.isShowing()) {
                    return;
                }
                if (mShowWindowMarker != null) {
                    mShowWindowMarker.hideInfoWindow();
                    mShowWindowMarker = null;
                }
                hideRouteView();
            }
        });
        mapRouteToolView = (MapRouteToolView) findViewById(R.id.route_tool_view);
        mapRouteToolView.init(this);
        // 显示定位
        if (UiPageConstans.VIEW_MAP_LOCATION.equals(mPageInfo.getControl())
                || UiPageConstans.VIEW_MAP_POI.equals(mPageInfo.getControl())) {
            showLocation();
        } else if (UiPageConstans.VIEW_MAP_TYPE.equals(mPageInfo.getControl())) {
            // 地图类型
            String type = getIntent().getStringExtra("navitype");
            if ("1".equals(type)) { // 卫星地图
                mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            } else if ("2".equals(type)) { // 夜景模式地图
                mAMap.setMapType(AMap.MAP_TYPE_NIGHT);
            } else if ("3".equals(type)) {
                mAMap.setMapType(AMap.MAP_TYPE_NAVI);
            } else {
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        }
    }

    private void hideRouteView() {
        mapRouteToolView.gone();
    }

    private void setRouteTitle() {
        mTitleBarView.setTitle("路径规划");
        mTitleBarView.setLeftBtn(R.mipmap.icon_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideRouteView();
                reAddMarkers();
                UiUtil.initTitleBar(mTitleBarView, mPageInfo);
            }
        });
        mTitleBarView.setRightDrawable(R.mipmap.icon_empty);
        mTitleBarView.setRightListener(null);
    }

    private void showLocation() {
        MyLocationStyle locationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类: 连续定位、不会将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）。
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        locationStyle.interval(2000);
        //设置定位蓝点的Style
        mAMap.setMyLocationStyle(locationStyle);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.setMyLocationEnabled(true);
    }

    private void showPoi(List<PoiInfo> pois) {
        if (pois != null && !pois.isEmpty()) {
            if (mMarkerAdapter == null) {
                mMarkerAdapter = new PoiMarkerAdapter(this, pois, new PoiMarkerAdapter.PoiMarkerClickListener() {
                    @Override
                    public void toRoute(Marker marker) {
                        Location location = mAMap.getMyLocation();
                        if (location != null) {
                            setRouteTitle();
                            marker.hideInfoWindow();
                            mapRouteToolView.show();
                            mapRouteToolView.startRoute(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition());
                        }
                    }

                    @Override
                    public void toNavi(Marker marker) {
                        Location location = mAMap.getMyLocation();
                        if (location != null) {
                            marker.hideInfoWindow();
                            mapRouteToolView.gone();
                            mapRouteToolView.startNavi(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition());
                        }
                    }
                });
            }
            mAMap.setInfoWindowAdapter(mMarkerAdapter);
            if (mMarkers == null) {
                mMarkers = new ArrayList<MarkerOptions>();
            }
            for (PoiInfo p : pois) {
                MarkerOptions mo = new MarkerOptions();
                mo.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_poi_location));
                LatLng latLng = new LatLng(p.getLat(), p.getLon());
                mo.position(latLng);
                mo.title(p.getTitle());
                mo.snippet(p.getSubTitle());
                mo.visible(true);
                mo.draggable(false);
                mAMap.addMarker(mo);
                mMarkers.add(mo);
                mAMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(p.getLat(), p.getLon())));
            }
        }
    }

    @Override
    protected boolean doBackPressed() {
        if (mapRouteToolView.isShowing()) {
            mapRouteToolView.gone();
            reAddMarkers();
            UiUtil.initTitleBar(mTitleBarView, mPageInfo);
            return true;
        }
        if (mShowWindowMarker != null) {
            mShowWindowMarker.remove();
            mShowWindowMarker = null;
            return true;
        }
        return false;
    }

    private void clearMarkers() {
        mAMap.clear();
        mAMap.setInfoWindowAdapter(null);
    }

    private void reAddMarkers() {
        if (mMarkers != null) {
            for (MarkerOptions mo : mMarkers) {
                mAMap.addMarker(mo);
            }
            if (mMarkerAdapter != null) {
                mAMap.setInfoWindowAdapter(mMarkerAdapter);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        AmapNaviPage.getInstance().exitRouteActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private void startIvCompass(float bearing) {
        bearing = 360 - bearing;
        RotateAnimation ani = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ani.setFillAfter(true);
        mapRouteToolView.startBearingAnimation(ani);
        lastBearing = bearing;
    }

    @Override
    public void onStartCalculateRoute() {
        showLoading();
    }

    @Override
    public void onCalculateFail() {
        dismissLoading();
        ToastUtil.show(R.string.txt_route_fail);
    }

    @Override
    public void onCalculateSuccess(boolean isStartNavi) {
        dismissLoading();
        if (isStartNavi) {
            mapRouteToolView.gone();
            UiUtil.initTitleBar(mTitleBarView, mPageInfo);
            reAddMarkers();
        } else {
            clearMarkers();
        }
    }

    @Override
    public void animateCamera(float bearing) {
        if (mAMap != null) {
            mAMap.animateCamera(CameraUpdateFactory.changeBearing(bearing));
        }
    }

    @Override
    public AMap getAMap() {
        return mAMap;
    }
}
