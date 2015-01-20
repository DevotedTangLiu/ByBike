package com.example.bybike.riding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
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
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BusLineOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.bybike.R;
import com.example.bybike.db.dao.MarkerBeanDao;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.marker.AddMarkerActivity;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.riding.MyOrientationListener.OnOrientationListener;

public class RidingActivity extends AbActivity {

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

	private LatLng currentLatLng;

	MarkerBeanDao markerBeanDao = null;

	private boolean showAllMarker = true;
	private boolean showPublicMarker = true;

	private final int GO_TO_ADD_MARKER = 1001;

	/**
	 * 筛选按钮
	 */
	ImageButton publicItemsButton1;
	ImageButton publicItemsButton2;
	ImageButton collectItemsButton1;
	ImageButton collectItemsButton2;
	ImageView publicItemBackground;
	ImageView collectItemBackground;

	/**
	 * 定义一个列表，存放定位点，在地图上绘制路线
	 */
	List<LatLng> points = new ArrayList<LatLng>();
	List<LatLng> back_points = new ArrayList<LatLng>();

	TextView distance;
	TextView speed;
	TextView carbonReduce;
	TextView timeUsed;
	DecimalFormat decimalformat = new DecimalFormat("#.00");
	
	List<MarkerBean>addMarkersList = new ArrayList<MarkerBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_riding);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
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

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();

		points.clear();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mCurrentLantitude = bundle.getDouble("latitude");
			mCurrentLongitude = bundle.getDouble("longtitude");
			currentLatLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
			points.add(currentLatLng);
		}

		distance = (TextView) findViewById(R.id.distance);
		speed = (TextView) findViewById(R.id.speed);
		carbonReduce = (TextView) findViewById(R.id.reduceCarbon);
		timeUsed = (TextView) findViewById(R.id.timeUsed);

		initMap();
		// 初始化传感器
		initOritationListener();
		initData();
	}

	private void initData() {
		distance.setText("0.0");
		speed.setText("0.0");
		carbonReduce.setText("0.0");
		timeUsed.setText("00:00:00");

		handler.postDelayed(runnable, 1000);

		queryMarkerList();
	}

	/**
	 * 初始化地图设置
	 */
	private View popView;
	private TextView name;
	private ImageView badge;
	private BitmapDescriptor bdsp;

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
		mLocClient = new LocationClient(getApplicationContext());
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
		// showProgressDialog("正在定位,请稍后...");
		// mLocClient.start();// 开始定位

		popView = LayoutInflater.from(this).inflate(
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
					i.setClass(RidingActivity.this, MarkerDetailActivity.class);
					// i.putExtra("detailInfo",
					// marker.getExtraInfo().getString("detailInfo"));
					i.putExtra("id", marker.getExtraInfo().getString("id"));
					startActivity(i);
					overridePendingTransition(R.anim.fragment_in, 0);
				}
				return true;
			}
		});

		if (currentLatLng != null) {

			MyLocationData locData = new MyLocationData.Builder()
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.latitude(currentLatLng.latitude)
					.longitude(currentLatLng.longitude).build();
			mBaidumap.setMyLocationData(locData);

			MapStatusUpdate u2 = MapStatusUpdateFactory
					.newLatLng(currentLatLng);
			mBaidumap.animateMapStatus(u2);
		} else {

			showProgressDialog("正在定位,请稍后...");
			mLocClient.start();// 开始定位

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
				} else {
					m.setIcon(marker_icon_others);
				}
				m.setAnchor(0.5f, 1.0f);
				m.setTitle("true");
			}
		}

	}

	public void clickHandler(View v) {

		switch (v.getId()) {
		case R.id.quitButton:
			showDialog("温馨提示", "确认要退出吗？\n退出后行程信息将不再保存。",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							RidingActivity.this.finish();
							overridePendingTransition(R.anim.fragment_in,
									R.anim.fragment_out);
						}
					});

			break;

		case R.id.addMarkerButton:
			Intent i = new Intent();
			i.setClass(RidingActivity.this, AddMarkerActivity.class);
			i.putExtra("latitude", currentLatLng.latitude);
			i.putExtra("longitude", currentLatLng.longitude);
			startActivityForResult(i, GO_TO_ADD_MARKER);
			overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			break;

		case R.id.pauseButton:
			v.setVisibility(View.GONE);
			findViewById(R.id.resumeButton).setVisibility(View.VISIBLE);
			isRiding = false;
			mLocClient.stop();// 停止定位
			break;
		case R.id.resumeButton:
			v.setVisibility(View.GONE);
			findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
			isRiding = true;
			mLocClient.start();// 开始定位
			break;

		case R.id.zoom_down:
			zoom_level -= 1;
			if (zoom_level >= 19)
				zoom_level = 19;
			if (zoom_level <= 3)
				zoom_level = 3;
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoom_level);
			mBaidumap.animateMapStatus(u);
			break;
		case R.id.zoom_up:
			zoom_level += 1;
			if (zoom_level >= 19)
				zoom_level = 19;
			if (zoom_level <= 3)
				zoom_level = 3;
			MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(zoom_level);
			mBaidumap.animateMapStatus(u2);
			break;
		case R.id.locate:
			showProgressDialog("正在定位,请稍后...");
			mLocClient.start();// 开始定位
			break;
		case R.id.chooseIcon:
			showChooseMarkerDialog();
			break;
		case R.id.stopButton:
			Intent intent = new Intent(RidingActivity.this,
					AddRouteBookActivity.class);
			intent.putExtra("distance", distance.getText().toString());
			intent.putExtra("speed", speed.getText().toString());
			intent.putExtra("carbonReduce", carbonReduce.getText().toString());
			intent.putExtra("timeUsed", totalSecs);
			JSONArray roadPoints = new JSONArray();
			for (int index = 0; index < back_points.size(); index++) {
				LatLng lt = back_points.get(index);
				JSONObject jo = new JSONObject();
				try {
					jo.put("lat", lt.latitude);
					jo.put("lng", lt.longitude);
					jo.put("sortNum", String.valueOf(index));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				roadPoints.put(jo);
			}
			intent.putExtra("roadPoints", roadPoints.toString());

			JSONArray markerBeanArray = new JSONArray();
			for (int index = 0; index < addMarkersList.size(); index++) {
				MarkerBean mb = addMarkersList.get(index);
				JSONObject jo = new JSONObject();
				try {
					jo.put("lat", mb.getLatitude());
					jo.put("lng", mb.getLongitude());
					jo.put("markerId", mb.getMarkerId());
					jo.put("markerName", mb.getMarkerName());
					jo.put("markerType", mb.getMarkerType());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				markerBeanArray.put(jo);
			}
			intent.putExtra("addMarkerList", markerBeanArray.toString());
			
			startActivity(intent);
			overridePendingTransition(R.anim.fragment_up, R.anim.fragment_down);
			break;
		default:
			break;
		}

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
					m.setVisible(false);
				}
				showAllMarker = false;
				break;
			case R.id.collectItemsButton1:
				collectItemsButton1.setVisibility(View.INVISIBLE);
				collectItemsButton2.setVisibility(View.VISIBLE);
				collectItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_sel);

				for (Marker m : markerList) {
					if ("public".equalsIgnoreCase(m.getExtraInfo().getString(
							"opertingType"))) {
						m.setVisible(true);
					}
				}
				showPublicMarker = true;
				break;
			case R.id.collectItemsButton2:
				collectItemsButton2.setVisibility(View.INVISIBLE);
				collectItemsButton1.setVisibility(View.VISIBLE);
				collectItemBackground
						.setBackgroundResource(R.drawable.slide_button_bak_nor);
				for (Marker m : markerList) {
					if ("public".equalsIgnoreCase(m.getExtraInfo().getString(
							"opertingType"))) {
						m.setVisible(false);
					}
				}
				showPublicMarker = false;
				break;
			default:
				break;
			}

		}

	}

	private void showChooseMarkerDialog() {
		// TODO Auto-generated method stub
		chooseDialog = new Dialog(this, R.style.Theme_dialog);
		LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = l.inflate(R.layout.items_choose_layout, null);
		publicItemsButton1 = (ImageButton) view
				.findViewById(R.id.publicItemsButton1);
		publicItemsButton2 = (ImageButton) view
				.findViewById(R.id.publicItemsButton2);
		collectItemsButton1 = (ImageButton) view
				.findViewById(R.id.collectItemsButton1);
		collectItemsButton2 = (ImageButton) view
				.findViewById(R.id.collectItemsButton2);
		publicItemsButton1.setOnClickListener(onItemsChooseClickListener);
		publicItemsButton2.setOnClickListener(onItemsChooseClickListener);
		collectItemsButton1.setOnClickListener(onItemsChooseClickListener);
		collectItemsButton2.setOnClickListener(onItemsChooseClickListener);

		publicItemBackground = (ImageView) view
				.findViewById(R.id.publicItemBackground);
		collectItemBackground = (ImageView) view
				.findViewById(R.id.collectItemBackground);

		if (showAllMarker) {
			publicItemsButton1.setVisibility(View.INVISIBLE);
			publicItemsButton2.setVisibility(View.VISIBLE);
			publicItemBackground
					.setBackgroundResource(R.drawable.slide_button_bak_sel);
		} else {
			publicItemsButton1.setVisibility(View.VISIBLE);
			publicItemsButton2.setVisibility(View.INVISIBLE);
			publicItemBackground
					.setBackgroundResource(R.drawable.slide_button_bak_nor);
		}
		if (showPublicMarker) {

			collectItemsButton1.setVisibility(View.INVISIBLE);
			collectItemsButton2.setVisibility(View.VISIBLE);
			collectItemBackground
					.setBackgroundResource(R.drawable.slide_button_bak_sel);
		} else {
			collectItemsButton1.setVisibility(View.VISIBLE);
			collectItemsButton2.setVisibility(View.INVISIBLE);
			collectItemBackground
					.setBackgroundResource(R.drawable.slide_button_bak_nor);
		}

		chooseDialog.setContentView(view);
		chooseDialog.show();

	}

	private double distanceInMeter = 0.0;

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			removeProgressDialog();
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			switch (location.getLocType()) {
			case 62:
				return;
			case 63:
				showToast("网络异常，请检查网络连接后重试");
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

			// 设置定位数据
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();

			myCurrentLatLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
			MapStatusUpdate u = MapStatusUpdateFactory
					.newLatLng(myCurrentLatLng);
			mBaidumap.animateMapStatus(u);
			//
			// 添加折线
			points.add(myCurrentLatLng);
			back_points.add(myCurrentLatLng);
			if (points.size() >= 2) {

				OverlayOptions ooPolyline = new PolylineOptions().width(10)
						.color(0xAAFF0000).points(points);
				mBaidumap.addOverlay(ooPolyline);

				// 计算两次定位之间的距离
				Double dis = DistanceUtil.getDistance(points.get(0),
						points.get(1));
				distanceInMeter += dis;

				String tmpDistance = decimalformat
						.format(distanceInMeter / 1000);
				if (tmpDistance.startsWith(".")) {
					tmpDistance = "0" + tmpDistance;
				}
				distance.setText(tmpDistance);

				String tmpSpeed = decimalformat
						.format((distanceInMeter / totalSecs) * 3.6);
				if (tmpSpeed.startsWith(".")) {
					tmpSpeed = "0" + tmpSpeed;
				}
				speed.setText(tmpSpeed);

				String tmpCarbon = decimalformat
						.format(distanceInMeter * 0.2 / 1000);
				if (tmpCarbon.startsWith(".")) {
					tmpCarbon = "0" + tmpCarbon;
				}
				carbonReduce.setText(tmpCarbon);
				points.remove(0);
			}
			if (back_points.size() >= 5000) {
				back_points.remove(0);
			}
			OverlayManager om = new BusLineOverlay(mBaidumap);
			om.zoomToSpan();
		}
	}

	Handler handler = new Handler();
	long totalSecs = 0;
	boolean isRiding = true;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (isRiding) {
				totalSecs++;
				int hours = (int) (totalSecs / 3600);
				int mins = (int) (totalSecs - hours * 3600) / 60;
				int secs = (int) (totalSecs - hours * 3600) % 60;
				timeUsed.setText(String.format("%1$02d:%2$02d:%3$02d", hours,
						mins, secs));
				handler.postDelayed(this, 1000);
			} else {
				handler.postDelayed(this, 1000);
			}

		}
	};

	/**
	 * 查找marker列表
	 */
	private void queryMarkerList() {

		markerBeanDao = new MarkerBeanDao(this);
		markerBeanDao.startReadableDatabase(false);
		List<MarkerBean> markers = markerBeanDao.queryList();

		for (MarkerBean m : markers) {
			double lat = m.getLatitude();
			double lng = m.getLongitude();
			LatLng llA = new LatLng(lat, lng);

			String markerType = m.getMarkerType();
			OverlayOptions ooA = null;
			if ("RantCar".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_rentbike).zIndex(9).draggable(false);
			} else if ("Repair".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_repair).zIndex(9).draggable(false);
			} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_scenery).zIndex(9).draggable(false);
			} else if ("Catering".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA).icon(marker_icon_meals)
						.zIndex(9).draggable(false);
			} else if ("Washroom".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_washroom).zIndex(9).draggable(false);
			} else if ("Parking".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_parking).zIndex(9).draggable(false);
			} else if ("Other".equalsIgnoreCase(markerType)) {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_others).zIndex(9).draggable(false);
			} else {
				ooA = new MarkerOptions().position(llA)
						.icon(marker_icon_others).zIndex(9).draggable(false);
			}
			Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
			Bundle b = new Bundle();
			b.putString("name", m.getMarkerName());
			b.putString("id", m.getMarkerId());
			b.putString("opertingType", m.getOperatingType());
			b.putString("markerType", markerType);
			mMarkerA.setExtraInfo(b);
			mMarkerA.setTitle("true");
			markerList.add(mMarkerA);
		}
		markerBeanDao.closeDatabase(false);
		markers.clear();

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

	private MyOrientationListener myOrientationListener;
	private int mXDirection;
	private double mCurrentLantitude;
	private double mCurrentLongitude;

	/**
	 * 初始化方向传感器
	 */
	private void initOritationListener() {
		myOrientationListener = new MyOrientationListener(
				getApplicationContext());
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mXDirection = (int) x;
						// 构造定位数据
						MyLocationData locData = new MyLocationData.Builder()
								// 此处设置开发者获取到的方向信息，顺时针0-360
								.direction(mXDirection)
								.latitude(mCurrentLantitude)
								.longitude(mCurrentLongitude).build();
						// 设置定位数据
						mBaidumap.setMyLocationData(locData);

					}
				});
	}

	@Override
	protected void onStart() {
		// 开启方向传感器
		myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeCallbacks(runnable);
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			if (requestCode == GO_TO_ADD_MARKER) {

				MarkerBean m = new MarkerBean();
				m.setMarkerId(data.getStringExtra("markerId"));
				m.setLatitude(data.getDoubleExtra("lat", 0.0));
				m.setLongitude(data.getDoubleExtra("lng", 0.0));
				m.setMarkerName(data.getStringExtra("markerName"));

				LatLng llA = new LatLng(m.getLatitude(), m.getLongitude());
				String markerType = data.getStringExtra("markerType");
				m.setMarkerType(markerType);

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
							.icon(marker_icon_meals).zIndex(9).draggable(false);
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
				} else {
					ooA = new MarkerOptions().position(llA)
							.icon(marker_icon_others).zIndex(9)
							.draggable(false);
				}
				Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
				Bundle b = new Bundle();
				b.putString("name", m.getMarkerName());
				b.putString("id", m.getMarkerId());
				b.putString("markerType", markerType);
				mMarkerA.setExtraInfo(b);
				mMarkerA.setTitle("true");
				markerList.add(mMarkerA);

				addMarkersList.add(m);
			}
			break;
		default:
			break;
		}
	}

}
