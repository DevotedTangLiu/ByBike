/*
 * @(#) MainPageFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.CancelableCallback;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

/**
 * @author tangliu(mail) 2014-9-18
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class MainPageFragment2 extends Fragment implements LocationSource,
		AMapLocationListener {

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
	private UiSettings mUiSettings;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LatLng nowLocationLatLng; //定位的坐标

	/**
	 * 定位和缩放图标
	 */
	Button locate = null;
	Button zoom_up = null;
	Button zoom_down = null;
	private float zoom_level = 16;
	
	/**
	 * 筛选按钮
	 */
	ImageButton publicItemsButton1 ;
	ImageButton publicItemsButton2 ;
	ImageButton collectItemsButton1 ;
	ImageButton collectItemsButton2 ;
	ImageView publicItemBackground;
	ImageView collectItemBackground;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity.getTitleBar().setVisibility(View.GONE);

		if (mainView == null) {
			mainView = inflater.inflate(R.layout.activity_main_gaode, null);
			// 获取地图控件引用
			mMapView = (MapView) mainView.findViewById(R.id.bmapView);
			mMapView.onCreate(savedInstanceState);// 必须要写
			initMap();

			// 定位图标
			locate = (Button) mainView.findViewById(R.id.locate);
			locate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mAMapLocationManager.requestLocationData(
							LocationProviderProxy.AMapNetwork, -1, 5,
							MainPageFragment2.this);
				}
			});
			zoom_up = (Button) mainView.findViewById(R.id.zoom_up);
			zoom_up.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					zoom_level += 1;
					if (zoom_level >= 20)
						zoom_level = 20;
					if (zoom_level <= 3)
						zoom_level = 3;
					changeCamera(CameraUpdateFactory.zoomTo(zoom_level), null);
				}

			});
			zoom_down = (Button) mainView.findViewById(R.id.zoom_down);
			zoom_down.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					zoom_level -= 1;
					if (zoom_level >= 20)
						zoom_level = 20;
					if (zoom_level <= 3)
						zoom_level = 3;
					changeCamera(CameraUpdateFactory.zoomTo(zoom_level), null);
				}
			});
			
			Button chooseIcon = (Button)mainView.findViewById(R.id.chooseIcon);
			chooseIcon.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					chooseDialog = new Dialog(mActivity, R.style.Theme_dialog);
					LayoutInflater l = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = l.inflate(R.layout.items_choose_layout, null);
					publicItemsButton1 = (ImageButton) view.findViewById(R.id.publicItemsButton1);
					publicItemsButton2 = (ImageButton) view.findViewById(R.id.publicItemsButton2);
					collectItemsButton1 = (ImageButton) view.findViewById(R.id.collectItemsButton1);
					collectItemsButton2 = (ImageButton) view.findViewById(R.id.collectItemsButton2);
                    publicItemsButton1.setOnClickListener(onItemsChooseClickListener);
                    publicItemsButton2.setOnClickListener(onItemsChooseClickListener);
                    collectItemsButton1.setOnClickListener(onItemsChooseClickListener);
                    collectItemsButton2.setOnClickListener(onItemsChooseClickListener);

                    publicItemBackground = (ImageView) view.findViewById(R.id.publicItemBackground);
                    collectItemBackground = (ImageView) view.findViewById(R.id.collectItemBackground);
                    chooseDialog.setContentView(view);
                    chooseDialog.show();
				}
			});
		}

		ViewGroup parent = (ViewGroup) mainView.getParent();
		if (parent != null) {
			parent.removeView(mainView);
		}
		return mainView;

	}

	
	/**
	 * 初始化地图控件
	 */
	private void initMap() {
		if (aMap == null) {
			aMap = mMapView.getMap();
			mUiSettings = aMap.getUiSettings();
		}
		aMap.setOnMarkerClickListener(mOnMarkerAndInfoWindowListener);// 设置点击marker事件监听器
		aMap.setInfoWindowAdapter(mOnMarkerAndInfoWindowListener);// 设置自定义InfoWindow样式
		aMap.setOnInfoWindowClickListener(mOnMarkerAndInfoWindowListener);// 设置infoWindow点击事件

		aMap.setLocationSource(this);// 设置定位监听
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

		// 自定义定位图标
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.me3));
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
		myLocationStyle.strokeWidth(0.0f);// 设置圆形的边框粗细
		aMap.setMyLocationStyle(myLocationStyle);
		// 设置地图缩放级别
		changeCamera(CameraUpdateFactory.zoomTo(zoom_level), null);
		// 设置原生缩放按钮不可用不可见
		mUiSettings.setZoomControlsEnabled(false);
		// 设置比例尺不可见
		mUiSettings.setScaleControlsEnabled(false);
		// 设置默认定位按钮是否显示
		mUiSettings.setMyLocationButtonEnabled(false);

		addMarkersToMap();
	}

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		aMap.animateCamera(update, 300, callback);
	}

	
	
	/**
	 * 在地图上添加marker
	 */
//	BitmapDescriptor markerBitMap = BitmapDescriptorFactory.fromResource(R.drawable.me3);
	private void addMarkersToMap() {

	    MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(new LatLng(30.679879, 104.064855));
        markerOption.title("成都市").snippet("test marker");
        markerOption.draggable(false);
        markerOption.icon(BitmapDescriptorFactory
                .fromResource(R.drawable.me3));
        Marker marker2 = aMap.addMarker(markerOption);

		// drawMarkers();// 添加10个带有系统默认icon的marker
	}

	private void showAllMarkers(){
	 // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(30.679879, 104.064855)).include(nowLocationLatLng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
	}
	
	/**
	 * 自定义Listener，相应marker、infoWindow点击事件，设置infoWindow样式
	 */
	OnMarkerAndInfoWindowListener mOnMarkerAndInfoWindowListener = new OnMarkerAndInfoWindowListener();

	private class OnMarkerAndInfoWindowListener implements
			OnMarkerClickListener, InfoWindowAdapter, OnInfoWindowClickListener {

		@Override
		public boolean onMarkerClick(Marker marker) {
			// TODO Auto-generated method stub
			marker.showInfoWindow();
			return false;
		}

		@Override
		public View getInfoContents(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
		    View infoWindow = (LinearLayout) mActivity.getLayoutInflater().inflate(
					R.layout.infowindow_interest_points, null);
			render(marker, infoWindow);
			return infoWindow;
		}

		@Override
		public void onInfoWindowClick(Marker marker) {
			// TODO Auto-generated method stub
			marker.hideInfoWindow();
		}

	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {

	  
	    ImageView v = (ImageView) view.findViewById(R.id.badge);
	    v.setBackgroundResource(R.drawable.me3);
	    TextView t = (TextView)view.findViewById(R.id.name);
	    t.setText("大不了");
	    
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
		deactivate();
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
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(mActivity);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			mActivity.showProgressDialog("正在定位,请稍后...");
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, -1, 5, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	// 接收到地理位置变化之后的处理方法
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		mActivity.removeProgressDialog();
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				changeCamera(
						CameraUpdateFactory.changeLatLng(new LatLng(
								amapLocation.getLatitude(), amapLocation
										.getLongitude())), null);
				nowLocationLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
				showAllMarkers();
			}
		}

	}
	
	private OnItemsChooseClickListener onItemsChooseClickListener = new OnItemsChooseClickListener();
	private Dialog chooseDialog;
	private class OnItemsChooseClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.publicItemsButton1:
				publicItemsButton1.setVisibility(View.GONE);
				publicItemsButton2.setVisibility(View.VISIBLE);
				publicItemBackground.setBackgroundResource(R.drawable.slide_button_bak_sel);
				break;

			case R.id.publicItemsButton2:
				publicItemsButton2.setVisibility(View.GONE);
				publicItemsButton1.setVisibility(View.VISIBLE);
				publicItemBackground.setBackgroundResource(R.drawable.slide_button_bak_nor);
				break;
			case R.id.collectItemsButton1:
				collectItemsButton1.setVisibility(View.GONE);
				collectItemsButton2.setVisibility(View.VISIBLE);
				collectItemBackground.setBackgroundResource(R.drawable.slide_button_bak_sel);
				break;
			case R.id.collectItemsButton2:
				collectItemsButton2.setVisibility(View.GONE);
				collectItemsButton1.setVisibility(View.VISIBLE);
				collectItemBackground.setBackgroundResource(R.drawable.slide_button_bak_nor);
				break;
			default:
				break;
			}
			
		}
		
	}

}
