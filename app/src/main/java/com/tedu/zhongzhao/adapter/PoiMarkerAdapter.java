package com.tedu.zhongzhao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.bumptech.glide.Glide;
import com.tedu.zhongzhao.R;
import com.tedu.zhongzhao.WorkApplication;
import com.tedu.zhongzhao.map.PoiInfo;

import java.util.List;

/**
 * POI Marker Adapter
 * Created by huangyx on 2018/4/29.
 */
public class PoiMarkerAdapter implements AMap.InfoWindowAdapter {

    private Context mContext;
    private List<PoiInfo> pois;
    private PoiMarkerClickListener listener;

    public PoiMarkerAdapter(Context context, List<PoiInfo> pois, PoiMarkerClickListener listener) {
        this.mContext = context;
        this.pois = pois;
        this.listener = listener;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = LayoutInflater.from(WorkApplication.getApplication()).inflate(R.layout.item_poi_marker_layout, null);
        ImageView logoView = (ImageView) view.findViewById(R.id.poi_img_view);
        String icon = null;
        PoiInfo poiInfo = null;
        if (pois != null && !pois.isEmpty()) {
            for (PoiInfo p : pois) {
                LatLng ll = marker.getPosition();
                if (p.getLat() == ll.latitude && p.getLon() == ll.longitude) {
                    icon = p.getImg();
                    poiInfo = p;
                    break;
                }
            }
        }
        Glide.with(mContext).load(icon).placeholder(R.mipmap.icon_defalut_poi).into(logoView);
        TextView titleView = (TextView) view.findViewById(R.id.poi_name_view);
        TextView introView = (TextView) view.findViewById(R.id.poi_intro_view);
        if (poiInfo != null) {
            titleView.setText(poiInfo.getTitle());
            introView.setText(poiInfo.getSubTitle() + "\n电话：" + poiInfo.getTel());
        } else {
            titleView.setText(marker.getTitle());
            introView.setText(marker.getSnippet());
        }
        view.findViewById(R.id.poi_route_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.toRoute(marker);
            }
        });
        view.findViewById(R.id.poi_navi_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.toNavi(marker);
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public interface PoiMarkerClickListener {
        void toRoute(Marker marker);

        void toNavi(Marker marker);
    }
}
