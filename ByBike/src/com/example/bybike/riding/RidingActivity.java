package com.example.bybike.riding;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
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
import com.example.bybike.R;
import com.example.bybike.marker.AddMarkerActivity;
import com.example.bybike.marker.MarkerDetailActivity;

public class RidingActivity extends AbActivity {

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

	/**
	 * 筛选按钮
	 */
	ImageButton publicItemsButton1;
	ImageButton publicItemsButton2;
	ImageButton collectItemsButton1;
	ImageButton collectItemsButton2;
	ImageView publicItemBackground;
	ImageView collectItemBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_riding);
		getTitleBar().setVisibility(View.GONE);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();

		System.out.println(getIntent().getExtras().get("longitude"));
		currentLatLng = new LatLng(getIntent().getExtras()
				.getDouble("latitude"), getIntent().getExtras().getDouble(
				"longitude"));
		initMap();
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

		// Marker点击事件
		mBaidumap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(final Marker marker) {
				// TODO Auto-generated method stub

				View popView = LayoutInflater.from(RidingActivity.this)
						.inflate(R.layout.infowindow_interest_points, null);
				popView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mBaidumap.hideInfoWindow();
						Intent i = new Intent();
						i.setClass(RidingActivity.this,
								MarkerDetailActivity.class);
						startActivity(i);
						overridePendingTransition(R.anim.fragment_in,
								R.anim.fragment_out);
					}
				});

				LatLng ll = marker.getPosition();
				mInfoWindow = new InfoWindow(popView, ll, -120);
				// mInfoWindow = new InfoWindow(BitmapDescriptorFactory
				// .fromView(popView), ll, -120, listener);
				mBaidumap.showInfoWindow(mInfoWindow);

				return true;
			}
		});

		addMarkersToMap();

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

		LatLng llB = new LatLng(23.102117, 113.327116);
		OverlayOptions ooB = new MarkerOptions().position(llB)
				.icon(bikestoreBitmap).zIndex(9).draggable(false);
		Marker mMarkerB = (Marker) (mBaidumap.addOverlay(ooB));
	}

	public void clickHandler(View v) {

		switch (v.getId()) {
		case R.id.quitButton:
			this.finish();
			overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			break;

		case R.id.addMarkerButton:
			Intent i = new Intent();
			i.setClass(RidingActivity.this, AddMarkerActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			break;

		case R.id.pauseButton:
			v.setVisibility(View.GONE);
			findViewById(R.id.resumeButton).setVisibility(View.VISIBLE);
			break;
		case R.id.resumeButton:
			v.setVisibility(View.GONE);
			findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
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
		chooseDialog.setContentView(view);
		chooseDialog.show();

	}

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

			MyLocationData locData = new MyLocationData.Builder()
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(location.getDirection())
					.latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);

			myCurrentLatLng = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory
					.newLatLng(myCurrentLatLng);
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
}
