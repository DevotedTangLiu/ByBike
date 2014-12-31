package com.example.bybike.marker;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
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
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.bybike.R;

public class LocationMarkerActivity extends AbActivity implements OnGetGeoCoderResultListener {

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
		
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
			}

			public void onMarkerDragEnd(Marker marker) {
				currentPt = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
			}

			public void onMarkerDragStart(Marker marker) {
			}
		});
		
		initListener();
		
		Bundle b = getIntent().getExtras();
		markerType = b.getString("markerType");
		address = b.getString("address");
		if ("RantCar".equalsIgnoreCase(markerType)) {
			markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_rentbike);
        } else if ("Repair".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_repair);
        } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_scenery);
        } else if ("Catering".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_meals);
        } else if ("Washroom".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_washroom);
        } else if ("Parking".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_parking);
        } else if ("Other".equalsIgnoreCase(markerType)) {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_others);
        } else {
        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_others);
        }
		double[] latlngs = b.getDoubleArray("latlng");
		if(latlngs != null){
			currentPt = new LatLng(latlngs[0], latlngs[1]);
			
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentPt);
	        mBaiduMap.animateMapStatus(u);
			updateMapState();
		}else if(!"".equals(address)){
		 // 初始化搜索模块，注册事件监听
	        mSearch = GeoCoder.newInstance();
	        mSearch.setOnGetGeoCodeResultListener(this);
	        showProgressDialog("正在定位“" + address +"”,请稍后...");
	        mSearch.geocode(new GeoCodeOption().city("").address(address));
		}
		
	}

	private void initListener() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		
		showDialog("温馨提示", "在地图中点击指定地点，或拖拽图标，即可完成位置选择");
	}


	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {
		
		if(marker == null){
			
			OverlayOptions ooA = new MarkerOptions().position(currentPt).icon(markerIcon)
					.zIndex(9).draggable(true);
			marker = (Marker) (mBaiduMap.addOverlay(ooA));
			
		}else{
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
		mMapView.onDestroy();
		markerIcon.recycle();
		if(mSearch != null){
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
	
	public void clickHandler(View view){
		switch (view.getId()) {
		case R.id.goBack:
			
			Intent intent = getIntent();
			if(currentPt != null){
				double [] latlng = new double[2];
				latlng[0] = currentPt.latitude;
				latlng[1] = currentPt.longitude;
				intent.putExtra("latlng", latlng);
			}
			
            setResult(RESULT_OK, intent);
            LocationMarkerActivity.this.finish();
			break;

		default:
			break;
		}
	}

    
    /*
      * @param arg0
      * @see com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#onGetGeoCodeResult(com.baidu.mapapi.search.geocode.GeoCodeResult)
      */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        // TODO Auto-generated method stub
        removeProgressDialog();
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            showToast("抱歉，未能找到地址“"+address+"”的位置，请手动选择");
            return;
        }
        mBaiduMap.clear();
        
        currentPt = result.getLocation();
        mBaiduMap.addOverlay(new MarkerOptions().position(currentPt)
                .icon(markerIcon));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(currentPt));   
    }

    
    /*
      * @param arg0
      * @see com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#onGetReverseGeoCodeResult(com.baidu.mapapi.search.geocode.ReverseGeoCodeResult)
      */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
        // TODO Auto-generated method stub
        
    }
}
