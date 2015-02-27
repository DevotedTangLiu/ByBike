package com.example.bybike.riding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.bybike.R;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.setting.AccountSettingActivity;
import com.example.bybike.setting.SettingMainActivity;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class AddRouteBookActivity extends AbActivity {

	// 图片下载器
	private AbImageLoader mAbImageLoader = null;
    // 基础地图相关
    MapView mMapView = null;
    BaiduMap mBaidumap = null;

    private View mAvatarView = null;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private String mFileName;

    RelativeLayout picturesArea;
    RelativeLayout mapArea;
    Button showPictureArea;
    private int currentPhotoId;
    private File[] photoFiles = new File[6];
    private int[] photosIds = new int[] { R.id.photo1, R.id.photo2, R.id.photo3, R.id.photo4, R.id.photo5, R.id.photo6 };
    ImageView photo1;
    ImageView photo2;
    ImageView photo3;
    ImageView photo4;
    ImageView photo5;
    ImageView photo6;

    EditText routeTitle;
    EditText routeDetail;
    EditText routeAddress;
    TextView distance;
    TextView speed;
    TextView reduceCarbon;
    TextView timeUsed;

    String routePoints;
    String roadMarkers = "";
    long timeUsedInt;

    List<BitmapDescriptor> bitMapDescriptorList = new ArrayList<BitmapDescriptor>();
    BitmapDescriptor marker_icon_bikestore = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_bikestore);
    BitmapDescriptor marker_icon_meals = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_meals);
    BitmapDescriptor marker_icon_others = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_others);
    BitmapDescriptor marker_icon_parking = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_parking);
    BitmapDescriptor marker_icon_rentbike = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_rentbike);
    BitmapDescriptor marker_icon_repair = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_repair);
    BitmapDescriptor marker_icon_scenery = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_scenery);
    BitmapDescriptor marker_icon_washroom = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon_washroom);
    List<Marker> markerList = new ArrayList<Marker>();
    
    LinearLayout markerNamesArea;
    TextView markerNames;
    
 // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_route_add);
        getTitleBar().setVisibility(View.GONE);
        
        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbImageLoader = AbImageLoader.newInstance(this);
        
        CircleImageView headPic = (CircleImageView)findViewById(R.id.headPic);
        mAbImageLoader.display(headPic, Constant.serverUrl + SharedPreferencesUtil.getSharedPreferences_s(AddRouteBookActivity.this, Constant.USERAVATARURL));

        bitMapDescriptorList.clear();
        bitMapDescriptorList.add(marker_icon_bikestore);
        bitMapDescriptorList.add(marker_icon_meals);
        bitMapDescriptorList.add(marker_icon_others);
        bitMapDescriptorList.add(marker_icon_parking);
        bitMapDescriptorList.add(marker_icon_rentbike);
        bitMapDescriptorList.add(marker_icon_repair);
        bitMapDescriptorList.add(marker_icon_scenery);
        bitMapDescriptorList.add(marker_icon_washroom);

        markerNamesArea = (LinearLayout)findViewById(R.id.markerNamesArea);
        markerNames = (TextView)findViewById(R.id.markerNames);
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();

        picturesArea = (RelativeLayout) findViewById(R.id.picturesArea);
        mapArea = (RelativeLayout) findViewById(R.id.mapArea);
        showPictureArea = (Button) findViewById(R.id.showPictureArea);

        photo1 = (ImageView) findViewById(R.id.photo1);
        photo2 = (ImageView) findViewById(R.id.photo2);
        photo3 = (ImageView) findViewById(R.id.photo3);
        photo4 = (ImageView) findViewById(R.id.photo4);
        photo5 = (ImageView) findViewById(R.id.photo5);
        photo6 = (ImageView) findViewById(R.id.photo6);
        // 初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(AddRouteBookActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }

        routeTitle = (EditText) findViewById(R.id.routeTitle);
        routeDetail = (EditText) findViewById(R.id.routeDetail);
        routeAddress = (EditText) findViewById(R.id.routeAddress);
        distance = (TextView) findViewById(R.id.distance);
        distance.setText(getIntent().getStringExtra("distance"));
        speed = (TextView) findViewById(R.id.speed);
        speed.setText(getIntent().getStringExtra("speed"));
        reduceCarbon = (TextView) findViewById(R.id.reduceCarbon);
        reduceCarbon.setText(getIntent().getStringExtra("carbonReduce"));
        timeUsed = (TextView) findViewById(R.id.timeUsed);

        timeUsedInt = getIntent().getLongExtra("timeUsed", 0);
        int hours = (int) (timeUsedInt / 3600);
        int mins = (int) (timeUsedInt - hours * 3600) / 60;
        int secs = (int) (timeUsedInt - hours * 3600) % 60;
        timeUsed.setText(String.format("%1$02d:%2$02d:%3$02d", hours, mins, secs));

        routePoints = getIntent().getStringExtra("roadPoints");

        init();
        if (!SharedPreferencesUtil.getSharedPreferences_b(AddRouteBookActivity.this, Constant.ISLOGINED)) {

            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                    new AbAlertDialogFragment.AbDialogOnClickListener() {

                        @Override
                        public void onPositiveClick() {
                            // TODO Auto-generated method stub
                            Intent i = new Intent(AddRouteBookActivity.this, LoginActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                            AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                        }

                        @Override
                        public void onNegativeClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                        }
                    });
            return;
        }

    }

    private void init() {
        // ===============初始化地图========================
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

        // 在地铁上画出路线
        List<LatLng> points = new ArrayList<LatLng>();
        try {
            JSONArray routePointsArray = new JSONArray(routePoints);
            for (int i = 0; i < routePointsArray.length(); i++) {
                JSONObject jo = routePointsArray.getJSONObject(i);
                LatLng lt = new LatLng(jo.getDouble("lat"), jo.getDouble("lng"));
                points.add(lt);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (points.size() >= 2) {
            OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
            mBaidumap.addOverlay(ooPolyline);

            LatLngBounds bounds = new LatLngBounds.Builder().include(points.get(0)).include(points.get(points.size() - 1)).build();
            MapStatusUpdate utmp = MapStatusUpdateFactory.newLatLngBounds(bounds);
            mBaidumap.animateMapStatus(utmp, 500);
        }

        // 将用户添加的友好点表示在地图上
        String markerListString = getIntent().getStringExtra("addMarkerList");
        JSONArray markerBeanArray;
        
        String markerNamesString = "";
        try {
            markerBeanArray = new JSONArray(markerListString);
            for (int index = 0; index < markerBeanArray.length(); index++) {

                MarkerBean mb = new MarkerBean();
                JSONObject jo = markerBeanArray.getJSONObject(index);
                mb.setMarkerId(jo.getString("markerId"));
                mb.setMarkerName(jo.getString("markerName"));
                mb.setMarkerType(jo.getString("markerType"));
                mb.setLatitude(jo.getDouble("lat"));
                mb.setLongitude(jo.getDouble("lng"));

                markerNamesString += mb.getMarkerName();
                if(index < markerBeanArray.length() - 1){
                	markerNamesString += ",";
                }
                double lat = mb.getLatitude();
                double lng = mb.getLongitude();
                LatLng llA = new LatLng(lat, lng);

                String markerType = mb.getMarkerType();
                OverlayOptions ooA = null;
                if ("RantCar".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_rentbike).zIndex(9).draggable(false);
                } else if ("Repair".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_repair).zIndex(9).draggable(false);
                } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_scenery).zIndex(9).draggable(false);
                } else if ("Catering".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_meals).zIndex(9).draggable(false);
                } else if ("Washroom".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_washroom).zIndex(9).draggable(false);
                } else if ("Parking".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_parking).zIndex(9).draggable(false);
                } else if ("Other".equalsIgnoreCase(markerType)) {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_others).zIndex(9).draggable(false);
                } else {
                    ooA = new MarkerOptions().position(llA).icon(marker_icon_others).zIndex(9).draggable(false);
                }
                Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
                Bundle b = new Bundle();
                b.putString("name", mb.getMarkerName());
                b.putString("id", mb.getMarkerId());
                b.putString("markerType", markerType);
                mMarkerA.setExtraInfo(b);
                mMarkerA.setTitle("true");
                markerList.add(mMarkerA);

                roadMarkers += mb.getMarkerId();
                roadMarkers += ";";
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        if(!markerNamesString.equals("")){
        	markerNamesArea.setVisibility(View.VISIBLE);
            markerNames.setText(markerNamesString);
        }

    }

    public void clickHandler(View v) {
        switch (v.getId()) {

        case R.id.goBack:
            goBack();
            break;

        case R.id.showPictureArea:
            v.setVisibility(View.GONE);
            picturesArea.setVisibility(View.VISIBLE);
            break;
        case R.id.showMapArea:
            showPictureArea.setVisibility(View.VISIBLE);
            picturesArea.setVisibility(View.GONE);
            break;
        case R.id.photo1:
            getView();
            photo2.setVisibility(View.VISIBLE);
            currentPhotoId = 1;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo2:
            getView();
            photo3.setVisibility(View.VISIBLE);
            currentPhotoId = 2;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo3:
            getView();
            photo4.setVisibility(View.VISIBLE);
            currentPhotoId = 3;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo4:
            getView();
            photo5.setVisibility(View.VISIBLE);
            currentPhotoId = 4;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo5:
            getView();
            photo6.setVisibility(View.VISIBLE);
            currentPhotoId = 5;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.photo6:
            getView();
            currentPhotoId = 6;
            AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
            break;
        case R.id.cancel:
            goBack();
            break;
        case R.id.submit:
            addRouteBookTest();
            break;
        default:
            break;
        }

    }

    private void getView() {

        mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
        Button albumButton = (Button) mAvatarView.findViewById(R.id.choose_album);
        Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
        Button cancelButton = (Button) mAvatarView.findViewById(R.id.choose_cancel);
        albumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                // 从相册中去获取
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    AbToastUtil.showToast(AddRouteBookActivity.this, "没有找到照片");
                }
            }

        });

        camButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                doPickPhotoAction();
            }

        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
            }

        });
    }

    /**
     * addRouteBook(这里用一句话描述这个方法的作用)
     */
    private void addRouteBookTest() {
        // TODO Auto-generated method stub
        if (!NetUtil.isConnnected(this)) {
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        String routeTitleString = routeTitle.getText().toString().trim();
        if ("".equals(routeTitleString)) {
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "路书主题不能为空，请重新输入", null);
            return;
        }
        String routeDetailString = routeDetail.getText().toString().trim();
        if ("".equals(routeDetailString)) {
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "路书详情不能为空，请重新输入", null);
            return;
        }
        String routeAddressString = routeAddress.getText().toString().trim();
        if ("".equals(routeAddressString)) {
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "途径路线不能为空，请重新输入", null);
            return;
        }

        String urlString = Constant.serverUrl + Constant.addRouteBookUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);

        RequestParams params = new RequestParams();
        params.setTANGLIU(true);
        params.addBodyParameter("roadPlace", routeAddressString);
        params.addBodyParameter("roadTitle", routeTitleString);
        params.addBodyParameter("roadContent", routeDetailString);
        params.addBodyParameter("averageSpeed", speed.getText().toString());
        params.addBodyParameter("totalDistance", distance.getText().toString());
        params.addBodyParameter("totalTime", String.valueOf(timeUsedInt));
        params.addBodyParameter("roadPoints", routePoints);
        if (roadMarkers.length() > 0) {
            params.addBodyParameter("roadMarkers", roadMarkers.substring(0, roadMarkers.length() - 1));
        }
        for (int i = 0; i < 6; i++) {
            if (photoFiles[i] != null) {
                String tmp = "img" + String.valueOf(i + 1);
                params.addBodyParameter(tmp, photoFiles[i]);

            }
        }
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, urlString, params, new RequestCallBack<String>() {

            @Override
            public void onStart() {
                AbDialogUtil.showProgressDialog(AddRouteBookActivity.this, 0, "正在上传...");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                if (isUploading) {
                } else {
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
//                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                processResult(responseInfo.result);
            }

            @Override
            public void onFailure(com.lidroid.xutils.exception.HttpException error, String msg) {
                // TODO Auto-generated method stub
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                AbToastUtil.showToast(AddRouteBookActivity.this, "保存失败，请稍后重试...");
            }
        });

    }

    /**
     * 处理保存非路线点的结果
     */
    public void processResult(String content){

        try {
            JSONObject resultObj = new JSONObject(content);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {
              
                JSONObject dataObj = resultObj.getJSONObject("data");
                String id = dataObj.getString("id");
                saveRouteLineRequest(id);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AbDialogUtil.removeDialog(AddRouteBookActivity.this);
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "保存失败,请稍后重试", null);
        }

    }
    
    private void saveRouteLineRequest(String id){
        
        String urlString = Constant.serverUrl + Constant.saveRouteLineUrl;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(AddRouteBookActivity.this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("roadId", id);
        p.put("updateStatus", "false");
        p.put("points", routePoints);
        mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processRouteLineResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                AbToastUtil.showToast(AddRouteBookActivity.this, "保存失败，请稍后重试...");
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
            };

        }, jsession);
    }
    /**
     * processResult(这里用一句话描述这个方法的作用)
     * 
     * @param content
     */
    protected void processRouteLineResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject resultObj = new JSONObject(content);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {
                AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "保存成功，点击确认返回",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                            @Override
                            public void onPositiveClick() {
                                // TODO Auto-generated method stub

                                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                                Intent intent = getIntent();
                                setResult(RESULT_OK, intent);
                                AddRouteBookActivity.this.finish();
                            }

                            @Override
                            public void onNegativeClick() {
                                // TODO Auto-generated method stub
                                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                            }
                        });

            } else if ("3".equals(code)) {
                AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                            @Override
                            public void onPositiveClick() {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(AddRouteBookActivity.this, LoginActivity.class);
                                startActivity(i);
                                overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                            }

                            @Override
                            public void onNegativeClick() {
                                // TODO Auto-generated method stub
                                AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                            }
                        });
            } else {
                AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "保存失败\n" + resultObj.getString("message"), null);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "保存失败,请稍后重试", null);
        }

    }

    /**
     * goBack(这里用一句话描述这个方法的作用)
     */
    private void goBack() {
        // TODO Auto-generated method stub
        AbDialogUtil.showAlertDialog(AddRouteBookActivity.this, 0, "温馨提示", "确认取消并退出本页面吗？退出后本路书信息将不再保存。",
                new AbAlertDialogFragment.AbDialogOnClickListener() {

                    @Override
                    public void onPositiveClick() {
                        // TODO Auto-generated method stub

                        AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                        AddRouteBookActivity.this.finish();
                    }

                    @Override
                    public void onNegativeClick() {
                        // TODO Auto-generated method stub
                        AbDialogUtil.removeDialog(AddRouteBookActivity.this);
                    }
                });
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
            AbToastUtil.showToast(AddRouteBookActivity.this, "未找到系统相机程序");
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
            AbToastUtil.showToast(AddRouteBookActivity.this, "没有可用的存储卡");
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
                AbToastUtil.showToast(AddRouteBookActivity.this, "未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
            // if(D)Log.d(TAG, "将要进行裁剪的图片的路径是 = " +
            // mCurrentPhotoFile.getPath());
            String currentFilePath2 = mCurrentPhotoFile.getPath();          
            /**
             * 部分手机拍照后会旋转显示
             */
            int degree = BitmapUtil.readPictureDegree(currentFilePath2);
            Bitmap currentBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath2, 640, 480);
            currentBitMap = BitmapUtil.rotaingImageView(degree, currentBitMap);
            ImageView image = (ImageView) findViewById(photosIds[currentPhotoId - 1]);
            image.setImageBitmap(currentBitMap);

            String picFileName = System.currentTimeMillis() + ".jpg";
            File picFile = new File(PHOTO_DIR, picFileName);
            // 压缩图片
            BitmapUtil.compressBmpToFile(currentBitMap, picFile);
            photoFiles[currentPhotoId - 1] = picFile;
            break;
        default:
            break;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        mMapView.onDestroy();
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
