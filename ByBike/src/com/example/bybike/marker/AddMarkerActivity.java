package com.example.bybike.marker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.bybike.R;
import com.example.bybike.riding.RidingActivity;
import com.example.bybike.riding.RidingActivity.MyLocationListener;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class AddMarkerActivity extends AbActivity implements OnGetGeoCoderResultListener {

    // 基础地图相关
    MapView mMapView = null;
    BaiduMap mBaidumap = null;
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    private View mAvatarView = null;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;

    private static final int GO_TO_LOCATION_MARKER = 1001;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private String mFileName;

    private int currentPhotoId;
    private File[] photoFiles = new File[4];
    private int[] photosIds = new int[] { R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4 };
    private int[] markerTypeIds = new int[] { R.id.type1, R.id.type2, R.id.type3, R.id.type4, R.id.type5, R.id.type6, R.id.type7, R.id.type8 };
    private int[] markerTypeTextIds = new int[] { R.id.type1text, R.id.type2text, R.id.type3text, R.id.type4text, R.id.type5text, R.id.type6text,
            R.id.type7text, R.id.type8text };

    /**
     * 友好点
     */
    private LatLng currentPt;
    private String markerId;
    private boolean saveSucceed = false;
    private Marker marker;

    private String markerType = "RantCar";
    BitmapDescriptor marker_icon_bikestore = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bikestore);
    BitmapDescriptor marker_icon_meals = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_meals);
    BitmapDescriptor marker_icon_others = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_others);
    BitmapDescriptor marker_icon_parking = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_parking);
    BitmapDescriptor marker_icon_rentbike = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_rentbike);
    BitmapDescriptor marker_icon_repair = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_repair);
    BitmapDescriptor marker_icon_scenery = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_scenery);
    BitmapDescriptor marker_icon_washroom = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_washroom);
    List<BitmapDescriptor> bitMapDescriptorList = new ArrayList<BitmapDescriptor>();

    /**
     * 地址字符串
     */
    private String address;
    private EditText addressText;
    private EditText markerName;
    private EditText description;

    LinearLayout rentBikeArea, bikeStroeArea, parkArea, sceneryArea, repairArea, mealArea, washroomArea;

    RadioGroup bikeType;
    RadioGroup operatingTypeGoup;
    // 租车：经营点类型、车型、价格、电话
    private String operatingType = "Public";
    private String motorcycleTypeString;
    private int[] motorcycleType = new int[] { R.id.gongluche, R.id.shandiche, R.id.xiaozhe, R.id.shuangren, R.id.sifei };
    // 维修： 经营点类型（固定、临时） 电话
    // 固定：FixedPoint, 临时：TemporaryPepairing
    // 景点：可否进入、是否方便停车、电话
    private String allowEnter = "true";
    private String allowPark = "true";
    // 餐饮：是否方便停车、电话

    // 洗手间：是否方便停车

    // 停车：是否有人保管、室内/室外
    // 室内：Indoor 室外：Outdoor
    private String takeCare = "true";

    // 车店：电话

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListener2 mMyLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_create_marker);
        getTitleBar().setVisibility(View.GONE);

        bitMapDescriptorList.clear();
        bitMapDescriptorList.add(marker_icon_bikestore);
        bitMapDescriptorList.add(marker_icon_meals);
        bitMapDescriptorList.add(marker_icon_others);
        bitMapDescriptorList.add(marker_icon_parking);
        bitMapDescriptorList.add(marker_icon_rentbike);
        bitMapDescriptorList.add(marker_icon_repair);
        bitMapDescriptorList.add(marker_icon_scenery);
        bitMapDescriptorList.add(marker_icon_washroom);

        rentBikeArea = (LinearLayout) findViewById(R.id.rentBikeArea);
        bikeStroeArea = (LinearLayout) findViewById(R.id.bikeStroeArea);
        parkArea = (LinearLayout) findViewById(R.id.parkArea);
        sceneryArea = (LinearLayout) findViewById(R.id.sceneryArea);
        repairArea = (LinearLayout) findViewById(R.id.repairArea);
        mealArea = (LinearLayout) findViewById(R.id.mealArea);
        washroomArea = (LinearLayout) findViewById(R.id.washroomArea);

        // ===============初始化地图========================
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();

        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 默认初始地图放大级别为17级
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(17f);
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
        mBaidumap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                Intent i = new Intent(AddMarkerActivity.this, LocationMarkerActivity.class);
                i.putExtra("markerType", markerType);
                i.putExtra("address", addressText.getText().toString());
                if (currentPt != null) {
                    double[] latlng = new double[2];
                    latlng[0] = currentPt.latitude;
                    latlng[1] = currentPt.longitude;
                    i.putExtra("latlng", latlng);
                }
                startActivityForResult(i, GO_TO_LOCATION_MARKER);
                overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            }
        });
        // ===============================================

        // 初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(AddMarkerActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }

        /**
         * 初始化友好点显示
         */
        changeAlpher(1);

        Bundle b = getIntent().getExtras();
        if (null != b) {
            double latitude = b.getDouble("latitude");
            double longitude = b.getDouble("longitude");
            currentPt = new LatLng(latitude, longitude);
            OverlayOptions ooA = new MarkerOptions().position(currentPt).icon(marker_icon_rentbike).zIndex(9).draggable(true);
            marker = (Marker) (mBaidumap.addOverlay(ooA));
            MapStatusUpdate ut = MapStatusUpdateFactory.newLatLng(currentPt);
            mBaidumap.animateMapStatus(ut);

            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentPt));

        } else {
            // 定位并显示地址转码在地址栏

            // 开启定位图层
            mBaidumap.setMyLocationEnabled(true);
            mLocClient = new LocationClient(getApplicationContext());
            mMyLocationListener = new MyLocationListener2();
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
            AbDialogUtil.showProgressDialog(AddMarkerActivity.this, 0, "正在定位，请稍后...");
            mLocClient.start();// 开始定位
        }

        addressText = (EditText) findViewById(R.id.markerAddress);
        markerName = (EditText) findViewById(R.id.markerName);
        description = (EditText) findViewById(R.id.description);

    }

    /**
     * 接受调用
     * 
     * @param source
     */
    public void clickHandler(View source) {

        switch (source.getId()) {
        case R.id.goBack:
            goBack();
            break;
        case R.id.photo1:
            currentPhotoId = 1;
            test();
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo2:
            currentPhotoId = 2;
            test();
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo3:
            currentPhotoId = 3;
            test();
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo4:
            currentPhotoId = 4;
            test();
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.type1:
            changeAlpher(1);
            break;
        case R.id.type2:
            changeAlpher(2);
            break;
        case R.id.type3:
            changeAlpher(3);
            break;
        case R.id.type4:
            changeAlpher(4);
            break;
        case R.id.type5:
            changeAlpher(5);
            break;
        case R.id.type6:
            changeAlpher(6);
            break;
        case R.id.type7:
            changeAlpher(7);
            break;
        case R.id.type8:
            changeAlpher(8);
            break;
        case R.id.cancel:
            goBack();
            break;
        case R.id.submit:
            addMarkerTest();
            break;
        default:
            break;
        }
    }

    private void test() {
        mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
        Button albumButton = (Button) mAvatarView.findViewById(R.id.choose_album);
        Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
        Button cancelButton = (Button) mAvatarView.findViewById(R.id.choose_cancel);
        albumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                // 从相册中去获取
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    AbToastUtil.showToast(AddMarkerActivity.this, "没有找到照片");
                }
            }

        });

        camButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                doPickPhotoAction();
            }

        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddMarkerActivity.this);
            }

        });
    }

    private void addMarkerTest() {

        if (!NetUtil.isConnnected(this)) {

            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        String markerNameString = markerName.getText().toString().trim();
        if ("".equals(markerNameString)) {
            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "友好点名称不能为空，请重新输入", null);
            return;
        }
        address = addressText.getText().toString().trim();
        if ("".equals(address)) {
            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "地址不能为空，请重新输入", null);
            return;
        }
        if (marker == null) {
            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "请在地图上选择友好点位置", null);
            return;
        }
        String remarks = description.getText().toString().trim();
        if ("".equals(remarks)) {
            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "请简单描述一下该友好点再重新保存", null);
            return;
        }
        if (!SharedPreferencesUtil.getSharedPreferences_b(AddMarkerActivity.this, Constant.ISLOGINED)) {

            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                    new AbAlertDialogFragment.AbDialogOnClickListener() {

                        @Override
                        public void onPositiveClick() {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(AddMarkerActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                            AbDialogUtil.removeDialog(AddMarkerActivity.this);
                        }

                        @Override
                        public void onNegativeClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(AddMarkerActivity.this);
                        }
                    });
            return;
        }
        String urlString = Constant.serverUrl + Constant.addMarkerUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);

        RequestParams params = new RequestParams();
        params.setTANGLIU(true);
        params.addBodyParameter("markerType", markerType);
        params.addBodyParameter("lat", String.valueOf(marker.getPosition().latitude));
        params.addBodyParameter("lng", String.valueOf(marker.getPosition().longitude));
        params.addBodyParameter("remarks", remarks);
        params.addBodyParameter("address", address);
        params.addBodyParameter("name", markerNameString);

        if ("RantCar".equalsIgnoreCase(markerType)) {

            params.addBodyParameter("operatingType", operatingType);
            EditText phoneNumberRentBike = (EditText) findViewById(R.id.phoneNumberRentBike);
            params.addBodyParameter("phone", phoneNumberRentBike.getText().toString().trim());
            EditText priceRentBike = (EditText) findViewById(R.id.priceRentBike);
            params.addBodyParameter("price", priceRentBike.getText().toString().trim());

            motorcycleTypeString = "";
            for (int i = 0; i < 5; i++) {
                CheckBox cb = (CheckBox) findViewById(motorcycleType[i]);
                if (cb.isChecked()) {
                    switch (i) {
                    case 0:
                        motorcycleTypeString += "Race,";
                        break;
                    case 1:
                        motorcycleTypeString += "Mtb,";
                        break;
                    case 2:
                        motorcycleTypeString += "Xz,";
                        break;
                    case 3:
                        motorcycleTypeString += "Df,";
                        break;
                    case 4:
                        motorcycleTypeString += "Db,";
                        break;
                    default:
                        break;
                    }
                }
            }
            if (motorcycleTypeString.endsWith(",")) {
                motorcycleTypeString = motorcycleTypeString.substring(0, motorcycleTypeString.length() - 1);
            }
            params.addBodyParameter("motorcycleType", motorcycleTypeString);

        } else if ("CarShop".equalsIgnoreCase(markerType)) {

            EditText phoneNumberBikeStore = (EditText) findViewById(R.id.phoneNumberBikeStore);
            params.addBodyParameter("phone", phoneNumberBikeStore.getText().toString().trim());

        } else if ("Parking".equalsIgnoreCase(markerType)) {

            params.addBodyParameter("operatingType", operatingType);
            params.addBodyParameter("takeCare", takeCare);

        } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {

            EditText phoneNumberScenery = (EditText) findViewById(R.id.phoneNumberScenery);
            params.addBodyParameter("phone", phoneNumberScenery.getText().toString().trim());
            params.addBodyParameter("allowEnter", allowEnter);
            params.addBodyParameter("allowPark", allowPark);

        } else if ("Repair".equalsIgnoreCase(markerType)) {

            params.addBodyParameter("operatingType", operatingType);
            EditText phoneNumberRepair = (EditText) findViewById(R.id.phoneNumberRepair);
            params.addBodyParameter("phone", phoneNumberRepair.getText().toString().trim());

        } else if ("Catering".equalsIgnoreCase(markerType)) {

            EditText phoneNumberMeal = (EditText) findViewById(R.id.phoneNumberMeal);
            params.addBodyParameter("phone", phoneNumberMeal.getText().toString().trim());
            params.addBodyParameter("allowPark", allowPark);

        } else if ("Washroom".equalsIgnoreCase(markerType)) {

            params.addBodyParameter("allowPark", allowPark);

        }
        // params.addBodyParameter("motorcycleType", motorcycleType);
        for (int i = 0; i < 4; i++) {
            if (photoFiles[i] != null) {
                String tmp = "img" + String.valueOf(i + 1);
                params.addBodyParameter(tmp, photoFiles[i]);
            }
        }

        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, urlString, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                AbDialogUtil.showProgressDialog(AddMarkerActivity.this, 0, "正在上传...");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (isUploading) {
                } else {
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                processResult(responseInfo.result);
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
                // TODO Auto-generated method stub
                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                AbToastUtil.showToast(AddMarkerActivity.this, "保存失败，请稍后重试...");
            }
        });

    }

    /**
     * processResult(这里用一句话描述这个方法的作用)
     * 
     * @param content
     */
    protected void processResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject resultObject = new JSONObject(content);
            String code = resultObject.getString("code");
            if ("0".equals(code)) {

                saveSucceed = true;
                JSONObject dataObj = resultObject.getJSONObject("data");
                markerId = dataObj.getString("id");

                AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "保存成功", new AbAlertDialogFragment.AbDialogOnClickListener() {

                    @Override
                    public void onPositiveClick() {
                        // TODO Auto-generated method stub
                        Intent intent = getIntent();
                        intent.putExtra("lat", currentPt.latitude);
                        intent.putExtra("lng", currentPt.longitude);
                        intent.putExtra("markerId", markerId);
                        intent.putExtra("markerType", markerType);
                        intent.putExtra("markerName", markerName.getText().toString().trim());

                        setResult(RESULT_OK, intent);
                        AddMarkerActivity.this.finish();
                    }

                    @Override
                    public void onNegativeClick() {
                        // TODO Auto-generated method stub
                        AbDialogUtil.removeDialog(AddMarkerActivity.this);
                    }
                });

            } else if ("3".equals(code)) {

                AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "保存失败：\n" + resultObject.getString("message"),
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                            @Override
                            public void onPositiveClick() {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(AddMarkerActivity.this, LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);

                                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                            }

                            @Override
                            public void onNegativeClick() {
                                // TODO Auto-generated method stub
                                AbDialogUtil.removeDialog(AddMarkerActivity.this);
                            }
                        });

            } else {
                AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "保存失败：\n" + resultObject.getString("message"), null);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 修改button背景透明度
     */
    private void changeAlpher(int target) {

        switch (target) {
        case 1:
            markerType = "RantCar";
            break;
        case 2:
            markerType = "CarShop";
            break;
        case 3:
            markerType = "Parking";
            break;
        case 4:
            markerType = "FeatureSpot";
            break;
        case 5:
            markerType = "Repair";
            break;
        case 6:
            markerType = "Catering";
            break;
        case 7:
            markerType = "Washroom";
            break;
        case 8:
            markerType = "Other";
            break;
        default:
            break;
        }

        for (int i = 1; i <= markerTypeIds.length; i++) {
            Button b = (Button) findViewById(markerTypeIds[i - 1]);
            TextView t = (TextView) findViewById(markerTypeTextIds[i - 1]);
            if (i != target) {
                b.setAlpha(0.5f);
                t.setAlpha(0.5f);
            } else {
                b.setAlpha(1f);
                t.setAlpha(1f);
            }
        }

        updateMarkerIcon();
        updateInputArea(target);
    }

    private void updateInputArea(int target) {
        // TODO Auto-generated method stub
        rentBikeArea.setVisibility(View.GONE);
        bikeStroeArea.setVisibility(View.GONE);
        parkArea.setVisibility(View.GONE);
        sceneryArea.setVisibility(View.GONE);
        repairArea.setVisibility(View.GONE);
        mealArea.setVisibility(View.GONE);
        washroomArea.setVisibility(View.GONE);

        switch (target) {
        case 1:
            rentBikeArea.setVisibility(View.VISIBLE);
            operatingType = "Public";
            operatingTypeGoup = (RadioGroup) findViewById(R.id.operatingType);
            operatingTypeGoup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.publicType:
                        operatingType = "Public";
                        break;
                    case R.id.bikeStoreType:
                        operatingType = "BicycleShop";
                        break;
                    case R.id.privateType:
                        operatingType = "Private";
                        break;
                    default:
                        break;
                    }

                }
            });
            break;
        case 2:
            bikeStroeArea.setVisibility(View.VISIBLE);
            break;
        case 3:
            parkArea.setVisibility(View.VISIBLE);
            operatingType = "Indoor";
            operatingTypeGoup = (RadioGroup) findViewById(R.id.indorOutDoorChooose);
            operatingTypeGoup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.inDoor:
                        operatingType = "Indoor";
                        break;
                    case R.id.outDoor:
                        operatingType = "Outdoor";
                        break;
                    default:
                        break;
                    }

                }
            });

            RadioGroup takeCaredArea = (RadioGroup) findViewById(R.id.takeCaredArea);
            takeCaredArea.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.haveTakeCared:
                        takeCare = "true";
                        break;
                    case R.id.noTakeCared:
                        takeCare = "false";
                        break;
                    default:
                        break;
                    }

                }
            });

            break;
        case 4:
            sceneryArea.setVisibility(View.VISIBLE);
            RadioGroup enterChoose = (RadioGroup) findViewById(R.id.enterChoose);
            enterChoose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.canEnter:
                        allowEnter = "true";
                        break;
                    case R.id.noEnter:
                        allowEnter = "false";
                        break;
                    default:
                        break;
                    }

                }
            });

            RadioGroup canParkChoose = (RadioGroup) findViewById(R.id.canParkChoose);
            canParkChoose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.canParkScenery:
                        allowPark = "true";
                        break;
                    case R.id.canNotParkScenery:
                        allowPark = "false";
                        break;
                    default:
                        break;
                    }

                }
            });

            break;
        case 5:
            repairArea.setVisibility(View.VISIBLE);
            operatingType = "FixedPoint";
            operatingTypeGoup = (RadioGroup) findViewById(R.id.repairTypeChoose);
            operatingTypeGoup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.fixedStore:
                        operatingType = "FixedPoint";
                        break;
                    case R.id.tmpStore:
                        operatingType = "TemporaryPepairing";
                        break;
                    default:
                        break;
                    }

                }
            });
            break;
        case 6:
            mealArea.setVisibility(View.VISIBLE);

            RadioGroup ifCanParkedChooseMeal = (RadioGroup) findViewById(R.id.ifCanParkedChooseMeal);
            ifCanParkedChooseMeal.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.canParkMeal:
                        allowPark = "true";
                        break;
                    case R.id.canNotParkMeal:
                        allowPark = "false";
                        break;
                    default:
                        break;
                    }

                }
            });
            break;
        case 7:
            washroomArea.setVisibility(View.VISIBLE);

            RadioGroup ifCanParkedChooseWashroom = (RadioGroup) findViewById(R.id.ifCanParkedChooseWashroom);
            ifCanParkedChooseWashroom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // TODO Auto-generated method stub
                    switch (checkedId) {
                    case R.id.canParkWashroom:
                        allowPark = "true";
                        break;
                    case R.id.canNotParkWashroom:
                        allowPark = "false";
                        break;
                    default:
                        break;
                    }

                }
            });
            break;
        default:
            break;
        }

    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {

            AbToastUtil.showToast(AddMarkerActivity.this, "未找到系统相机程序");
        }
    }

    /**
     * 描述：从照相机获取
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            AbToastUtil.showToast(AddMarkerActivity.this, "没有可用的存储卡");
        }
    }

    /**
     * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
        case PHOTO_PICKED_WITH_DATA:
            Uri uri = mIntent.getData();
            String currentFilePath = getPath(uri);
            if (!AbStrUtil.isEmpty(currentFilePath)) {
                Bitmap currentBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath, 640, 480);
                ImageView image = (ImageView) findViewById(photosIds[currentPhotoId - 1]);
                image.setImageBitmap(currentBitMap);

                String picFileName = System.currentTimeMillis() + ".jpg";
                File picFile = new File(PHOTO_DIR, picFileName);
                // 压缩图片
                BitmapUtil.compressBmpToFile(currentBitMap, picFile);
                photoFiles[currentPhotoId - 1] = picFile;
            } else {

                AbToastUtil.showToast(AddMarkerActivity.this, "未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
            // if(D)Log.d(TAG, "将要进行裁剪的图片的路径是 = " +
            // mCurrentPhotoFile.getPath());
            String currentFilePath2 = mCurrentPhotoFile.getPath();
            Bitmap currentBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath2, 640, 480);
            ImageView image = (ImageView) findViewById(photosIds[currentPhotoId - 1]);
            image.setImageBitmap(currentBitMap);

            String picFileName = System.currentTimeMillis() + ".jpg";
            File picFile = new File(PHOTO_DIR, picFileName);
            // 压缩图片
            BitmapUtil.compressBmpToFile(currentBitMap, picFile);
            photoFiles[currentPhotoId - 1] = picFile;
            break;
        // case CAMERA_CROP_DATA:
        // String path = mIntent.getStringExtra("PATH");
        // // if(D)Log.d(TAG, "裁剪后得到的图片的路径是 = " + path);
        // break;
        case GO_TO_LOCATION_MARKER:
            Bundle b = mIntent.getExtras();
            if (b != null) {
                double[] latlngs = b.getDoubleArray("latlng");
                if (latlngs != null) {
                    currentPt = new LatLng(latlngs[0], latlngs[1]);
                    if (marker == null) {

                        OverlayOptions ooA = new MarkerOptions().position(currentPt).icon(marker_icon_rentbike).zIndex(9).draggable(true);
                        marker = (Marker) (mBaidumap.addOverlay(ooA));

                    } else {
                        marker.setPosition(currentPt);
                    }
                    updateMarkerIcon();
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentPt);
                    mBaidumap.animateMapStatus(u);
                }
            }
            break;
        default:
            break;
        }
    }

    private void updateMarkerIcon() {
        if (marker == null)
            return;
        if ("RantCar".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_rentbike);
        } else if ("Repair".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_repair);
        } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_scenery);
        } else if ("Catering".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_meals);
        } else if ("Washroom".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_washroom);
        } else if ("Parking".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_parking);
        } else if ("Other".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_others);
        } else if ("CarShop".equalsIgnoreCase(markerType)) {
            marker.setIcon(marker_icon_bikestore);
        } else {
            marker.setIcon(marker_icon_others);
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri) {
        if (AbStrUtil.isEmpty(uri.getAuthority())) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    /**
     * 退出页面
     */
    private void goBack() {

        if (!saveSucceed) {

            AbDialogUtil.showAlertDialog(AddMarkerActivity.this, 0, "温馨提示", "确认取消并退出本页面吗？退出后本友好点信息将不再保存。",
                    new AbAlertDialogFragment.AbDialogOnClickListener() {

                        @Override
                        public void onPositiveClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(AddMarkerActivity.this);
                            AddMarkerActivity.this.finish();
                        }

                        @Override
                        public void onNegativeClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(AddMarkerActivity.this);
                        }
                    });

        } else {

            Intent intent = getIntent();
            intent.putExtra("lat", currentPt.latitude);
            intent.putExtra("lng", currentPt.longitude);
            intent.putExtra("markerId", markerId);
            intent.putExtra("markerType", markerType);
            intent.putExtra("markerName", markerName.getText().toString().trim());

            setResult(RESULT_OK, intent);
            AddMarkerActivity.this.finish();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaidumap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        for (BitmapDescriptor b : bitMapDescriptorList) {
            b.recycle();
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

    /*
     * @param arg0
     * 
     * @see com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#onGetGeoCodeResult(com.baidu.mapapi.search.geocode.GeoCodeResult)
     */
    @Override
    public void onGetGeoCodeResult(GeoCodeResult arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * @param arg0
     * 
     * @see
     * com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener#onGetReverseGeoCodeResult(com.baidu.mapapi.search.geocode.ReverseGeoCodeResult)
     */
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        // TODO Auto-generated method stub
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            AbToastUtil.showToast(AddMarkerActivity.this, "未能找到经纬度对应的地址");
            return;
        }
        addressText.setText(result.getAddress());
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener2 implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            switch (location.getLocType()) {
            case 62:
                return;
            case 63:
                AbToastUtil.showToast(AddMarkerActivity.this, "网络异常，请检查网络连接后重试");
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
            
            currentPt = new LatLng(location.getLatitude(), location.getLongitude());
            mLocClient.stop();
            
            OverlayOptions ooA = new MarkerOptions().position(currentPt).icon(marker_icon_rentbike).zIndex(9).draggable(true);
            marker = (Marker) (mBaidumap.addOverlay(ooA));
            MapStatusUpdate ut = MapStatusUpdateFactory.newLatLng(currentPt);
            mBaidumap.animateMapStatus(ut);
            
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(currentPt));

            AbDialogUtil.removeDialog(AddMarkerActivity.this);
        }
    }
}
