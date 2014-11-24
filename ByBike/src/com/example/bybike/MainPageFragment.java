/*
 * @(#) MainPageFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.bybike.marker.MarkerDetailActivity;

/**
 * @author tangliu(mail) 2014-9-18
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class MainPageFragment extends Fragment {

	private NewMainActivity mActivity = null;
	/**
	 * 缓存Fragment的view,避免每次切换页面时重新加载页面
	 */
	private View mainView;

	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 定位相关
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode;
	LocationClient mLocClient;
	public MyLocationListener mMyLocationListener;
	// 当前位置marker图标
	private Marker currentLocationMarker;
	BitmapDescriptor currentLocationBitmap;
	InfoWindow mInfoWindow;
	List<BitmapDescriptor> bitMapDescriptorList = new ArrayList<BitmapDescriptor>();
	// 定位和缩放图标
	Button locate = null;
	Button zoom_up = null;
	Button zoom_down = null;
	private float zoom_level = 17;
	/**
	 * 筛选按钮
	 */
	ImageButton publicItemsButton1;
	ImageButton publicItemsButton2;
	ImageButton collectItemsButton1;
	ImageButton collectItemsButton2;
	ImageView publicItemBackground;
	ImageView collectItemBackground;

	// 定义一个链表List存放每次的定位结果，然后在地图上绘制路线
	List<LatLng> points = new ArrayList<LatLng>();
	List<LatLng> back_points = new ArrayList<LatLng>();

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mActivity.getTitleBar().setVisibility(View.GONE);

		if (mainView == null) {
			mainView = inflater.inflate(R.layout.activity_main_baidu, null);
			// 获取地图控件引用
			mMapView = (MapView) mainView.findViewById(R.id.bmapView);
			mBaidumap = mMapView.getMap();
			// 默认初始地图放大级别为17级
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoom_level);
			mBaidumap.animateMapStatus(u);
			// 隐藏自带的地图缩放控件
			int childCount = mMapView.getChildCount();
			View zoom = null;
			for (int i = 0; i < childCount; i++) {
				View child = mMapView.getChildAt(i);
				if (child instanceof ZoomControls) {
					zoom = child;
					break;
				}
			}
			zoom.setVisibility(View.GONE);
			// 隐藏指南针
			mBaidumap.getUiSettings().setCompassEnabled(false);

			// 修改为自定义marker
			currentLocationBitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.me3);
			mCurrentMode = com.baidu.mapapi.map.MyLocationConfiguration.LocationMode.NORMAL;
			mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, currentLocationBitmap));
			// 开启定位图层
			mBaidumap.setMyLocationEnabled(true);
			mLocClient = new LocationClient(mActivity.getApplicationContext());
			mMyLocationListener = new MyLocationListener();
			mLocClient.registerLocationListener(mMyLocationListener);
			// 设置定位参数
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
			option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
			option.setIsNeedAddress(true);
			mLocClient.setLocOption(option);
			mActivity.showProgressDialog("正在定位...");
			mLocClient.start();// 开始定位

			locate = (Button) mainView.findViewById(R.id.locate);
			locate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mLocClient.start();
				}
			});
			zoom_up = (Button) mainView.findViewById(R.id.zoom_up);
			zoom_up.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					zoom_level += 1;
					if (zoom_level >= 19)
						zoom_level = 19;
					if (zoom_level <= 3)
						zoom_level = 3;
					MapStatusUpdate u = MapStatusUpdateFactory
							.zoomTo(zoom_level);
					mBaidumap.animateMapStatus(u);
				}
			});
			zoom_down = (Button) mainView.findViewById(R.id.zoom_down);
			zoom_down.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					zoom_level -= 1;
					if (zoom_level >= 19)
						zoom_level = 19;
					if (zoom_level <= 3)
						zoom_level = 3;
					MapStatusUpdate u = MapStatusUpdateFactory
							.zoomTo(zoom_level);
					mBaidumap.animateMapStatus(u);
				}
			});

			Button chooseIcon = (Button) mainView.findViewById(R.id.chooseIcon);
			chooseIcon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					chooseDialog = new Dialog(mActivity, R.style.Theme_dialog);
					LayoutInflater l = (LayoutInflater) mActivity
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = l.inflate(R.layout.items_choose_layout, null);
					publicItemsButton1 = (ImageButton) view
							.findViewById(R.id.publicItemsButton1);
					publicItemsButton2 = (ImageButton) view
							.findViewById(R.id.publicItemsButton2);
					collectItemsButton1 = (ImageButton) view
							.findViewById(R.id.collectItemsButton1);
					collectItemsButton2 = (ImageButton) view
							.findViewById(R.id.collectItemsButton2);
					publicItemsButton1
							.setOnClickListener(onItemsChooseClickListener);
					publicItemsButton2
							.setOnClickListener(onItemsChooseClickListener);
					collectItemsButton1
							.setOnClickListener(onItemsChooseClickListener);
					collectItemsButton2
							.setOnClickListener(onItemsChooseClickListener);

					publicItemBackground = (ImageView) view
							.findViewById(R.id.publicItemBackground);
					collectItemBackground = (ImageView) view
							.findViewById(R.id.collectItemBackground);
					chooseDialog.setContentView(view);
					chooseDialog.show();
				}
			});

			// Marker点击事件
			mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(final Marker marker) {
					// TODO Auto-generated method stub
					if (marker == currentLocationMarker) {

						View popView = LayoutInflater.from(mActivity).inflate(
								R.layout.infowindow_interest_points, null);
						OnInfoWindowClickListener listener = null;
						listener = new OnInfoWindowClickListener() {
							public void onInfoWindowClick() {
								mBaidumap.hideInfoWindow();
								Intent i = new Intent();
								i.setClass(mActivity,
										MarkerDetailActivity.class);
								startActivity(i);
								mActivity.overridePendingTransition(
										R.anim.fragment_in, 0);
							}
						};

						LatLng ll = marker.getPosition();
						mInfoWindow = new InfoWindow(BitmapDescriptorFactory
								.fromView(popView), ll, -150, listener);
						mBaidumap.showInfoWindow(mInfoWindow);
					}

					return true;
				}
			});

		}

		ViewGroup parent = (ViewGroup) mainView.getParent();
		if (parent != null) {
			parent.removeView(mainView);
		}
		return mainView;

	}

	private OnItemsChooseClickListener onItemsChooseClickListener = new OnItemsChooseClickListener();
	private Dialog chooseDialog;

	private class OnItemsChooseClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.publicItemsButton1:
				publicItemsButton1.setVisibility(View.GONE);
				publicItemsButton2.setVisibility(View.VISIBLE);
				publicItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_sel);
				break;

			case R.id.publicItemsButton2:
				publicItemsButton2.setVisibility(View.GONE);
				publicItemsButton1.setVisibility(View.VISIBLE);
				publicItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_nor);
				break;
			case R.id.collectItemsButton1:
				collectItemsButton1.setVisibility(View.GONE);
				collectItemsButton2.setVisibility(View.VISIBLE);
				collectItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_sel);
				break;
			case R.id.collectItemsButton2:
				collectItemsButton2.setVisibility(View.GONE);
				collectItemsButton1.setVisibility(View.VISIBLE);
				collectItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_nor);
				break;
			default:
				break;
			}

		}

	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			mActivity.removeProgressDialog();
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			switch (location.getLocType()) {
			case 62:
				return;
			case 63:
				mActivity.showToast("网络异常，请检查网络连接后重试");
				return;
			case 162:
				return;
			case 163:
				return;
			case 164:
				return;
			case 165:
				return;
			case 166:
				return;
			case 167:
				return;
			default:
				break;
			}

			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);

			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaidumap.animateMapStatus(u);
			//
			// 添加折线
			// points.add(ll);
			// back_points.add(ll);
			// if (points.size() >= 2) {
			//
			// OverlayOptions ooPolyline = new PolylineOptions().width(10)
			// .color(0xAAFF0000).points(points);
			// mBaidumap.addOverlay(ooPolyline);
			//
			// String dis = String.valueOf(DistanceUtil.getDistance(
			// points.get(0), points.get(1)));
			// String speed = String.valueOf(location.getSpeed());
			// mActivity.showToast("速度：" + speed + "       距离：" + dis);
			// points.remove(0);
			// }
			// if (back_points.size() >= 100) {
			// back_points.clear();
			// }
			// OverlayManager om=new BusLineOverlay(mBaidumap);
			// om.zoomToSpan();
			mLocClient.stop();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(mActivity.getApplicationContext());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaidumap.setMyLocationEnabled(false);
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mMapView = null;

		for (BitmapDescriptor d : bitMapDescriptorList) {
			d.recycle();
		}

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
}
