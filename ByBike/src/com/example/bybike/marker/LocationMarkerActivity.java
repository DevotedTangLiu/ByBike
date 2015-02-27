package com.example.bybike.marker;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.bybike.R;
import com.example.bybike.marker.AddMarkerActivity.MyLocationListener2;

public class LocationMarkerActivity extends AbActivity implements
		OnGetGeoCoderResultListener {

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	/**
	 * 当前地点击点
	 */
	private LatLng currentPt;
	private String markerType;
	private Marker marker;
	BitmapDescriptor markerIcon;
	private String address;
	private String currentPtAddress = "";
	private boolean needGetAddress = false;

	LocationClient mLocClient;
	public MyLocationListener3 mMyLocationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapcontrol);
		getTitleBar().setVisibility(View.GONE);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();

		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
		mBaiduMap.setMapStatus(msu);
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
		mBaiduMap.getUiSettings().setCompassEnabled(false);
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		initListener();

		Bundle b = getIntent().getExtras();
		markerType = b.getString("markerType");
		address = b.getString("address");
		if ("RantCar".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_rentbike);
		} else if ("Repair".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_repair);
		} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_scenery);
		} else if ("Catering".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_meals);
		} else if ("Washroom".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_washroom);
		} else if ("Parking".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_parking);
		} else if ("Other".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_others);
		} else {
			markerIcon = BitmapDescriptorFactory
					.fromResource(R.drawable.marker_icon_others);
		}
		double[] latlngs = b.getDoubleArray("latlng");
		if (!"".equals(address)) {

			needGetAddress = false;		
			// 初始化搜索模块，注册事件监听
			AbDialogUtil.showProgressDialog(LocationMarkerActivity.this, 0,
					"正在定位“" + address + "”,请稍后...");
			mSearch.geocode(new GeoCodeOption().city("").address(address));
			
		} else {

			needGetAddress = true;		
			mBaiduMap.setMyLocationEnabled(true);
			mLocClient = new LocationClient(getApplicationContext());
			mMyLocationListener = new MyLocationListener3();
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
			AbDialogUtil.showProgressDialog(LocationMarkerActivity.this, 0,
					"正在定位，请稍后...");
			mLocClient.start();// 开始定位

		}

		// }else if(latlngs != null){
		// currentPt = new LatLng(latlngs[0], latlngs[1]);
		//
		// MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentPt);
		// mBaiduMap.animateMapStatus(u);
		// updateMapState();
		// }

	}

	private void initListener() {

		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
			}

			public void onMarkerDragEnd(Marker marker) {
				currentPt = new LatLng(marker.getPosition().latitude, marker
						.getPosition().longitude);

				if(needGetAddress){
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(currentPt));
				}			
			}

			public void onMarkerDragStart(Marker marker) {
			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				currentPt = point;

				if(needGetAddress){
					mSearch.reverseGeoCode(new ReverseGeoCodeOption()
					.location(currentPt));
				}	
				
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});

		AbDialogUtil.showAlertDialog(LocationMarkerActivity.this, 0, "温馨提示",
				"在地图中点击指定地点，或拖拽图标，即可完成位置选择", null);
	}

	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {

		if (marker == null) {

			OverlayOptions ooA = new MarkerOptions().position(currentPt)
					.icon(markerIcon).zIndex(9).draggable(true);
			marker = (Marker) (mBaiduMap.addOverlay(ooA));

		} else {
			marker.setPosition(currentPt);
		}

	}

	@Override
	protected void onPause() {
		// MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		// 退出时销毁定位
		if (mLocClient != null) {
			mLocClient.stop();
		}
		mMapView.onDestroy();
		markerIcon.recycle();
		if (mSearch != null) {
			mSearch.destroy();
		}
		super.onDestroy();
	}

	/**
	 * 监听按钮事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void clickHandler(View view) {
		switch (view.getId()) {
		case R.id.goBack:

			Intent intent = getIntent();
			if (currentPt != null) {
				double[] latlng = new double[2];
				latlng[0] = currentPt.latitude;
				latlng[1] = currentPt.longitude;
				intent.putExtra("latlng", latlng);
			}
			intent.putExtra("address", currentPtAddress);

			setResult(RESULT_OK, intent);
			LocationMarkerActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/*
	 * @param arg0
	 * 
	 * @see com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#
	 * onGetGeoCodeResult(com.baidu.mapapi.search.geocode.GeoCodeResult)
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		AbDialogUtil.removeDialog(LocationMarkerActivity.this);
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			AbToastUtil.showToast(LocationMarkerActivity.this, "抱歉，未能找到地址“"
					+ address + "”的位置，请手动选择");
			return;
		}
		mBaiduMap.clear();

		currentPt = result.getLocation();
		marker = (Marker) mBaiduMap.addOverlay(new MarkerOptions().position(currentPt).icon(
				markerIcon).draggable(true));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(currentPt));
	}

	/*
	 * @param arg0
	 * 
	 * @see com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#
	 * onGetReverseGeoCodeResult
	 * (com.baidu.mapapi.search.geocode.ReverseGeoCodeResult)
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			// AbToastUtil.showToast(AddMarkerActivity.this, "未能找到经纬度对应的地址");
			currentPtAddress = "";
			return;
		}
		currentPtAddress = result.getAddress();

	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener3 implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			switch (location.getLocType()) {
			case 62:
				return;
			case 63:
				AbToastUtil.showToast(LocationMarkerActivity.this,
						"网络异常，请检查网络连接后重试");
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

			currentPt = new LatLng(location.getLatitude(),
					location.getLongitude());
			mLocClient.stop();

			OverlayOptions ooA = new MarkerOptions().position(currentPt).icon(markerIcon).zIndex(9).draggable(true);
			marker = (Marker) (mBaiduMap.addOverlay(ooA));
			MapStatusUpdate ut = MapStatusUpdateFactory.newLatLng(currentPt);
			mBaiduMap.animateMapStatus(ut);

			if(needGetAddress){
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentPt));
			}
			
			AbDialogUtil.removeDialog(LocationMarkerActivity.this);
		}
	}
}
