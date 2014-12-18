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
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.setting.SettingMainActivity;
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
		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
		mAbHttpUtil.setDebug(false);

		if (mainView == null) {
			mainView = inflater.inflate(R.layout.activity_main_baidu, null);
			// 获取地图控件引用
			mMapView = (MapView) mainView.findViewById(R.id.bmapView);
			mBaidumap = mMapView.getMap();
			initMap();

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

		// Marker点击事件
		mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub

				View popView = LayoutInflater.from(mActivity).inflate(
						R.layout.infowindow_interest_points, null);
				
				TextView name = (TextView)popView.findViewById(R.id.name);
				name.setText(marker.getExtraInfo().getString("name"));
				popView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mBaidumap.hideInfoWindow();
						Intent i = new Intent();
						i.setClass(mActivity, MarkerDetailActivity.class);
						startActivity(i);
						mActivity.overridePendingTransition(R.anim.fragment_in,
								0);
					}
				});
				//
				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(popView, ll, -120);
				mBaidumap.showInfoWindow(mInfoWindow);

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
				break;

			case R.id.publicItemsButton2:
				publicItemsButton2.setVisibility(View.INVISIBLE);
				publicItemsButton1.setVisibility(View.VISIBLE);
				publicItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_nor);
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
		// 绑定参数
		mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {

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
	BitmapDescriptor bikestoreBitmap = BitmapDescriptorFactory
			.fromResource(R.drawable.marker_icon_bikestore);
	
	List<MarkerBean>markerList = new ArrayList<MarkerBean>();
	private void processMarkerListResult(String resultString) {

		try {
			JSONObject resultObject = new JSONObject(resultString);
			String code = resultObject.getString("code");
			if ("0".equals(code)) {
				JSONArray dataArray = resultObject.getJSONArray("data");
				for (int i = 0; i < dataArray.length(); i++) {

					JSONObject jo = dataArray.getJSONObject(i);

					double lat = jo.getDouble("lat");
					double lng = jo.getDouble("lng");
					LatLng llA = new LatLng(lat, lng);
					OverlayOptions ooA = new MarkerOptions().position(llA)
							.icon(bikestoreBitmap).zIndex(9).draggable(false);

					Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
					Bundle b = new Bundle();
					b.putString("name", jo.getString("name"));
					b.putString("id", jo.getString("id"));
					mMarkerA.setExtraInfo(b);
					
					MarkerBean mb = new MarkerBean();
					mb.setMarkerId(jo.getString("id"));
					mb.setLatitude(lat);
					mb.setLongitude(lng);
					mb.setMarkerName(jo.getString("name"));
					mb.setImgurl1(jo.getString("imgUrl1"));
					mb.setImgurl2(jo.getString("imgUrl2"));
					mb.setImgurl3(jo.getString("imgUrl3"));
					mb.setImgurl4(jo.getString("imgUrl4"));
					mb.setImgurl5(jo.getString("imgUrl5"));
					mb.setDescription(jo.getString("remarks"));
					mb.setAddress(jo.getString("address"));
				}
			}
		} catch (Exception e) {

		}

	}

	/**
	 * 向地图添加marker
	 */
	private void addMarkersToMap() {

		BitmapDescriptor bikestoreBitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.marker_icon_bikestore);
		bitMapDescriptorList.add(bikestoreBitmap);

		LatLng llA = new LatLng(23.136136, 113.328772);
		OverlayOptions ooA = new MarkerOptions().position(llA)
				.icon(bikestoreBitmap).zIndex(9).draggable(false);
		Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
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
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);

			myCurrentLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());

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
