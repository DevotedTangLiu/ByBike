/*
 * @(#) MainPageFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import java.util.Map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;

/**
 * @author tangliu(mail) 2014-9-18
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class MainPageFragment2 extends Fragment {

    private NewMainActivity mActivity = null;
    /**
     * 缓存Fragment的view,避免每次切换页面时重新加载页面
     */
    private View mainView;
    /**
     * 高德地图相关
     */
    private MapView mMapView;
    private AMap aMap;
    private LocationManagerProxy mLocationManagerProxy;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity.getTitleBar().setVisibility(View.GONE);

        if (mainView == null) {
            mainView = inflater.inflate(R.layout.activity_main_gaode, null);
            // 获取地图控件引用
            mMapView = (MapView) mainView.findViewById(R.id.bmapView);
            mMapView.onCreate(savedInstanceState);// 必须要写
            if (aMap == null) {
                aMap = mMapView.getMap();
            }
            // 定位管理初始化
            mLocationManagerProxy = LocationManagerProxy.getInstance(mActivity);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用destroy()方法
            // 其中如果间隔时间为-1，则定位只定一次
            mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60 * 1000, 15, onLocationListener);
            mLocationManagerProxy.setGpsEnable(true);
            
        }

        ViewGroup parent = (ViewGroup) mainView.getParent();
        if (parent != null) {
            parent.removeView(mainView);
        }
        return mainView;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (NewMainActivity) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        deactivate();
    }

    public void showTitleBar() {
        // TODO Auto-generated method stub
        if (mActivity != null) {
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    
    /**
     * 停止定位
     */

    public void deactivate() {
        onLocationListener = null;
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(onLocationListener);
            mLocationManagerProxy.destroy();
        }
        mLocationManagerProxy = null;
    }
    
    //定位结果监听器
    private OnLocationListener onLocationListener = new OnLocationListener();
    public class OnLocationListener implements
    AMapLocationListener {
        
        /*
          * @param location
          * @see android.location.LocationListener#onLocationChanged(android.location.Location)
          */
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
           
        }
        
        /*
          * @param provider
          * @param status
          * @param extras
          * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
          */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            
        }

        /*
          * @param provider
          * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
          */
        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            
        }

        
        /*
          * @param provider
          * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
          */
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            
        }

        /*
          * @param arg0
          * @see com.amap.api.location.AMapLocationListener#onLocationChanged(com.amap.api.location.AMapLocation)
          */
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            // TODO Auto-generated method stub
            if(amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0){
                //获取位置信息
                Double geoLat = amapLocation.getLatitude();
                Double geoLng = amapLocation.getLongitude();   
            }
            
        }
        
    }
}
