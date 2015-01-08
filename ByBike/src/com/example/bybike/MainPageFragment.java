/*
 * @(#) MainPageFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.bybike.db.dao.MarkerBeanDao;
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.riding.MyOrientationListener;
import com.example.bybike.riding.MyOrientationListener.OnOrientationListener;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

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

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 定位相关
	private com.baidu.mapapi.map.MyLocationConfiguration.LocationMode mCurrentMode;
	LocationClient mLocClient;
	public MyLocationListener mMyLocationListener;
	// 当前位置marker图标
	LatLng myCurrentLatLng;
	BitmapDescriptor currentLocationBitmap;
	InfoWindow mInfoWindow;
	List<BitmapDescriptor> bitMapDescriptorList = new ArrayList<BitmapDescriptor>();
	// 定位和缩放图标
	Button locate = null;
	RelativeLayout zoom_up = null;
	RelativeLayout zoom_down = null;
	private float zoom_level = 17;

	private boolean firstLocated = true;
	private boolean showAllMarker = true;
	/**
	 * 筛选按钮
	 */
	ImageButton publicItemsButton1;
	ImageButton publicItemsButton2;
	ImageButton collectItemsButton1;
	ImageButton collectItemsButton2;
	ImageView publicItemBackground;
	ImageView collectItemBackground;

	MarkerBeanDao markerBeanDao = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mActivity.getTitleBar().setVisibility(View.GONE);
		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
		mAbHttpUtil.setDebug(false);

		bitMapDescriptorList.clear();
		bitMapDescriptorList.add(marker_icon_bikestore);
		bitMapDescriptorList.add(marker_icon_meals);
		bitMapDescriptorList.add(marker_icon_others);
		bitMapDescriptorList.add(marker_icon_parking);
		bitMapDescriptorList.add(marker_icon_rentbike);
		bitMapDescriptorList.add(marker_icon_repair);
		bitMapDescriptorList.add(marker_icon_scenery);
		bitMapDescriptorList.add(marker_icon_washroom);

		if (mainView == null) {
			mainView = inflater.inflate(R.layout.activity_main_baidu, null);
			// 获取地图控件引用
			mMapView = (MapView) mainView.findViewById(R.id.bmapView);
			mBaidumap = mMapView.getMap();
			initMap();
			initOritationListener();

			// 定位图标
			locate = (Button) mainView.findViewById(R.id.locate);
			locate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mLocClient.start();
				}
			});
			// 放大按钮
			zoom_up = (RelativeLayout) mainView.findViewById(R.id.zoom_up);
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
			// 缩小按钮
			zoom_down = (RelativeLayout) mainView.findViewById(R.id.zoom_down);
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

			// 筛选按钮
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
					
					if(showAllMarker){
						publicItemsButton1.setVisibility(View.INVISIBLE);
						publicItemsButton2.setVisibility(View.VISIBLE);
						publicItemBackground
								.setBackgroundResource(R.drawable.slide_button_bak_sel);
					}else{
						publicItemsButton1.setVisibility(View.VISIBLE);
						publicItemsButton2.setVisibility(View.INVISIBLE);
						publicItemBackground
								.setBackgroundResource(R.drawable.slide_button_bak_nor);
					}
					
					
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

	private View popView;
	private TextView name;
	private ImageView badge;
	private BitmapDescriptor bdsp;

	/**
	 * 初始化地图设置
	 */
	private void initMap() {

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

		// 修改定位图标为自定义bitmap
		currentLocationBitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.me);
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
		option.setNeedDeviceDirect(true);
		mLocClient.setLocOption(option);
		mActivity.showProgressDialog("正在定位,请稍后...");
		mLocClient.start();// 开始定位

		popView = LayoutInflater.from(mActivity).inflate(
				R.layout.infowindow_interest_points, null);
		name = (TextView) popView.findViewById(R.id.name);
		badge = (ImageView) popView.findViewById(R.id.badge);
		// Marker点击事件
		mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub

				String showingIcon = marker.getTitle();
				if ("true".endsWith(showingIcon)) {
					// 先把所有markerIcon还原
					resetMarkerListIcon();
					name.setText(marker.getExtraInfo().getString("name"));
					String markerType = marker.getExtraInfo().getString(
							"markerType");
					if ("RantCar".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_rentbike);
					} else if ("Repair".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_repair);
					} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_scenery);
					} else if ("Catering".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_meals);
					} else if ("Washroom".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_washroom);
					} else if ("Parking".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_parking);
					} else if ("Other".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_others);
					} else if ("CarShop".equalsIgnoreCase(markerType)) {
						badge.setImageResource(R.drawable.marker_icon_bikestore);
					} else {
						badge.setImageResource(R.drawable.marker_icon_others);
					}

					if (bdsp != null) {
						bdsp.recycle();
					}
					bdsp = BitmapDescriptorFactory.fromView(popView);
					marker.setTitle("false");
					marker.setIcon(bdsp);
					marker.setAnchor(0.2f, 1.0f);
				} else {
					Intent i = new Intent();
					i.setClass(mActivity, MarkerDetailActivity.class);
					// i.putExtra("detailInfo",
					// marker.getExtraInfo().getString("detailInfo"));
					i.putExtra("id", marker.getExtraInfo().getString("id"));
					startActivity(i);
					mActivity.overridePendingTransition(R.anim.fragment_in, 0);
				}
				return true;
			}
		});

		queryMarkerList();
		// addMarkersToMap();

	}

	/**
	 * 筛选按钮监听器
	 */
	private OnItemsChooseClickListener onItemsChooseClickListener = new OnItemsChooseClickListener();
	private Dialog chooseDialog;

	private class OnItemsChooseClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.publicItemsButton1:
				publicItemsButton1.setVisibility(View.INVISIBLE);
				publicItemsButton2.setVisibility(View.VISIBLE);
				publicItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_sel);
				for (Marker m : markerList) {
					m.setVisible(true);
				}
				showAllMarker = true;
				break;

			case R.id.publicItemsButton2:
				publicItemsButton2.setVisibility(View.INVISIBLE);
				publicItemsButton1.setVisibility(View.VISIBLE);
				publicItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_nor);
				for (Marker m : markerList) {
					if ("public".equalsIgnoreCase(m.getExtraInfo().getString(
							"opertingType"))) {
						m.setVisible(true);
					} else {
						m.setVisible(false);
					}
				}
				showAllMarker = false;
				break;
			case R.id.collectItemsButton1:
				collectItemsButton1.setVisibility(View.INVISIBLE);
				collectItemsButton2.setVisibility(View.VISIBLE);
				collectItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_sel);
				break;
			case R.id.collectItemsButton2:
				collectItemsButton2.setVisibility(View.INVISIBLE);
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
	 * 查找marker列表
	 */
	private void queryMarkerList() {

		if (!NetUtil.isConnnected(mActivity)) {
			return;
		}
		String urlString = Constant.serverUrl + Constant.getMarkerListUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(mActivity,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "100");
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processMarkerListResult(content);
			};

			// 开始执行前
			@Override
			public void onStart() {
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
			};

		});

	}

	/**
	 * 处理返回结果
	 * 
	 * @param resultString
	 */
	BitmapDescriptor marker_icon_bikestore = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_bikestore);
	BitmapDescriptor marker_icon_meals = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_meals);
	BitmapDescriptor marker_icon_others = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_others);
	BitmapDescriptor marker_icon_parking = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_parking);
	BitmapDescriptor marker_icon_rentbike = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_rentbike);
	BitmapDescriptor marker_icon_repair = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_repair);
	BitmapDescriptor marker_icon_scenery = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_scenery);
	BitmapDescriptor marker_icon_washroom = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_washroom);
	List<Marker> markerList = new ArrayList<Marker>();

	// List<MarkerBean>markerList = new ArrayList<MarkerBean>();
	private void processMarkerListResult(String resultString) {

		try {
			JSONObject resultObject = new JSONObject(resultString);
			String code = resultObject.getString("code");
			if ("0".equals(code)) {
				markerList.clear();
				JSONArray dataArray = resultObject.getJSONArray("data");
				// (1)获取数据库
				markerBeanDao = new MarkerBeanDao(mActivity);
				markerBeanDao.startWritableDatabase(false);
				markerBeanDao.deleteAll();
				for (int i = 0; i < dataArray.length(); i++) {

					JSONObject jo = dataArray.getJSONObject(i);

					double lat = jo.getDouble("lat");
					double lng = jo.getDouble("lng");
					LatLng llA = new LatLng(lat, lng);

					String opertingType = jo.getString("operatingType");
					String markerType = jo.getString("markerType");
					OverlayOptions ooA = null;
					if ("RantCar".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_rentbike).zIndex(9)
								.draggable(false);
					} else if ("Repair".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_repair).zIndex(9)
								.draggable(false);
					} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_scenery).zIndex(9)
								.draggable(false);
					} else if ("Catering".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_meals).zIndex(9)
								.draggable(false);
					} else if ("Washroom".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_washroom).zIndex(9)
								.draggable(false);
					} else if ("Parking".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_parking).zIndex(9)
								.draggable(false);
					} else if ("Other".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_others).zIndex(9)
								.draggable(false);
					} else if ("CarShop".equalsIgnoreCase(markerType)) {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_bikestore).zIndex(9)
								.draggable(false);
					} else {
						ooA = new MarkerOptions().position(llA)
								.icon(marker_icon_others).zIndex(9)
								.draggable(false);
					}

					Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
					Bundle b = new Bundle();
					b.putString("name", jo.getString("name"));
					b.putString("id", jo.getString("Id"));
					b.putString("opertingType", opertingType);
					b.putString("markerType", markerType);
					// b.putString("detailInfo", jo.toString());
					mMarkerA.setExtraInfo(b);
					mMarkerA.setTitle("true");

					markerList.add(mMarkerA);

					MarkerBean mb = new MarkerBean();
					mb.setMarkerId(jo.getString("Id"));
					mb.setLatitude(jo.getDouble("lat"));
					mb.setLongitude(jo.getDouble("lng"));
					mb.setMarkerName(jo.getString("name"));
					mb.setMarkerType(jo.getString("markerType"));
					mb.setOperatingType(jo.getString("operatingType"));

					markerBeanDao.insert(mb);

				}
				markerBeanDao.closeDatabase(false);
			}
		} catch (Exception e) {

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

			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);

			firstLocated = false;
			// 设置定位数据
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();

			myCurrentLatLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
			mActivity.currentLatLng = myCurrentLatLng;
			MapStatusUpdate u = MapStatusUpdateFactory
					.newLatLng(myCurrentLatLng);
			mBaidumap.animateMapStatus(u);

			mLocClient.stop();
		}
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

	private MyOrientationListener myOrientationListener;
	private int mXDirection;
	private double mCurrentLantitude;
	private double mCurrentLongitude;

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(
				mActivity.getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;
						if (!firstLocated) {
							// 构造定位数据
							MyLocationData locData = new MyLocationData.Builder()
									// 此处设置开发者获取到的方向信息，顺时针0-360
									.direction(mXDirection)
									.latitude(mCurrentLantitude)
									.longitude(mCurrentLongitude).build();
							// 设置定位数据
							mBaidumap.setMyLocationData(locData);
						}
					}
				});
	}

	@Override
	public void onStart() {
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	public void onStop() {
		// 关闭方向传感器
		myOrientationListener.stop();
		super.onStop();
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

	private void resetMarkerListIcon() {

		for (Marker m : markerList) {
			String showingIcon = m.getTitle();
			if ("false".endsWith(showingIcon)) {
				String markerType = m.getExtraInfo().getString("markerType");
				if ("RantCar".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_rentbike);
				} else if ("Repair".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_repair);
				} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_scenery);
				} else if ("Catering".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_meals);
				} else if ("Washroom".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_washroom);
				} else if ("Parking".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_parking);
				} else if ("Other".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_others);
				} else if ("CarShop".equalsIgnoreCase(markerType)) {
					m.setIcon(marker_icon_bikestore);
				} else {
					m.setIcon(marker_icon_others);
				}
				m.setAnchor(0.5f, 1.0f);
				m.setTitle("true");
			}
		}

	}

}
