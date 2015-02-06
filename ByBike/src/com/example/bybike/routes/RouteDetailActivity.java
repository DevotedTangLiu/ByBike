package com.example.bybike.routes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.fragment.AbDialogFragment;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.fragment.AbDialogFragment.AbDialogOnLoadListener;
import com.ab.fragment.AbProgressDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.sliding.AbSlidingPlayView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
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
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.exercise.AddActivityCommentActivity;
import com.example.bybike.exercise.ExerciseDetailActivity3;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.riding.RidingActivity;
import com.example.bybike.routes.ObservableScrollView.ScrollViewListener;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.PublicMethods;
import com.example.bybike.util.SharedPreferencesUtil;

public class RouteDetailActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	private AbTaskQueue mAbTaskQueue = null;
	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 图片区域
	RelativeLayout exercisePicArea = null;
	AbSlidingPlayView mAbSlidingPlayView = null;
	String[] imageUrls = new String[8];
	// 滚动区域
	ObservableScrollView scrollView = null;
	private LinearLayout spaceArea;
	// 评论列表
	ListView discussList = null;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	// 图片下载类
	private AbImageLoader mAbImageDownloader = null;

	private String routeId = "";
	private String routeNameString = "";
	private String commentsString = "";

	TextView distanceText;
	TextView speedText;
	TextView carbonReduceText;
	TextView hour;
	TextView minute;
	TextView second;

	CircleImageView userHeadPic;
	TextView userName;
	TextView routeDetail;
	TextView exerciseRouteAddress;
	TextView exerciseTime;
	// 点赞、评论、分享区域
	RelativeLayout likeButton;
	TextView likeCount;
	RelativeLayout collectButton;
	TextView collectCount;
	TextView discussCount;
	Button discussButton;

	List<BitmapDescriptor> bitMapDescriptorList = new ArrayList<BitmapDescriptor>();
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

	private AbProgressDialogFragment mDialogFragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setAbContentView(R.layout.activity_route_detail);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		routeId = getIntent().getStringExtra("id");

		bitMapDescriptorList.clear();
		bitMapDescriptorList.add(marker_icon_bikestore);
		bitMapDescriptorList.add(marker_icon_meals);
		bitMapDescriptorList.add(marker_icon_others);
		bitMapDescriptorList.add(marker_icon_parking);
		bitMapDescriptorList.add(marker_icon_rentbike);
		bitMapDescriptorList.add(marker_icon_repair);
		bitMapDescriptorList.add(marker_icon_scenery);
		bitMapDescriptorList.add(marker_icon_washroom);

		mAbSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);
		// 图片的下载
		mAbImageDownloader = AbImageLoader
				.newInstance(RouteDetailActivity.this);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);
		mAbImageDownloader.setEmptyImage(R.drawable.image_empty);

		scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
		scrollView.setScrollViewListener(scrollChangedListener);
		spaceArea = (LinearLayout) findViewById(R.id.spaceArea);
		exercisePicArea = (RelativeLayout) findViewById(R.id.exercisePicArea);

		distanceText = (TextView) findViewById(R.id.distanceText);
		speedText = (TextView) findViewById(R.id.speedText);
		carbonReduceText = (TextView) findViewById(R.id.carbonReduceText);
		hour = (TextView) findViewById(R.id.hour);
		minute = (TextView) findViewById(R.id.minute);
		second = (TextView) findViewById(R.id.second);

		userHeadPic = (CircleImageView) findViewById(R.id.userHeadPic);
		userName = (TextView) findViewById(R.id.userName);
		routeDetail = (TextView) findViewById(R.id.routeDetail);
		exerciseRouteAddress = (TextView) findViewById(R.id.exerciseRouteAddress);
		exerciseTime = (TextView) findViewById(R.id.exerciseTime);

		// 按钮点击事件
		likeButton = (RelativeLayout) findViewById(R.id.likeButton);
		likeCount = (TextView) findViewById(R.id.likeCount);
		collectButton = (RelativeLayout) findViewById(R.id.collectButton);
		collectCount = (TextView) findViewById(R.id.collectCount);
		discussCount = (TextView) findViewById(R.id.discussCount);

		// 评论列表
		discussList = (ListView) findViewById(R.id.discussList);
		discussList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ContentValues cv = discussValueList.get(position);
				
				Intent i = new Intent(RouteDetailActivity.this,
						AddRouteCommentActivity.class);
				i.putExtra("id", routeId);			
				i.putExtra("comments", commentsString);
				i.putExtra("name", routeNameString);
				i.putExtra("receiverId", cv.getAsString("senderId"));
				i.putExtra("receiverName", cv.getAsString("userName"));
				i.putExtra("commentId", cv.getAsString("id"));
				startActivityForResult(i, 0);
				overridePendingTransition(R.anim.fragment_up, R.anim.fragment_out);
				
			}
		});
		
		
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				RouteDetailActivity.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
		initActivity();
	}

	private View popView;
	private TextView name;
	private ImageView badge;
	private BitmapDescriptor bdsp;

	/**
	 * 初始化点击事件等
	 */
	private void initActivity() {

		/**
		 * 点击喜欢按钮触发事件
		 */
		likeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!SharedPreferencesUtil.getSharedPreferences_b(
						RouteDetailActivity.this, Constant.ISLOGINED)) {

					AbDialogUtil
							.showAlertDialog(
									RouteDetailActivity.this,
									0,
									"温馨提示",
									"您还未登陆，或登陆状态过期，请重新登录再试",
									new AbAlertDialogFragment.AbDialogOnClickListener() {

										@Override
										public void onPositiveClick() {
											// TODO Auto-generated method stub
											Intent i = new Intent(
													RouteDetailActivity.this,
													LoginActivity.class);
											RouteDetailActivity.this
													.startActivity(i);
											RouteDetailActivity.this
													.overridePendingTransition(
															R.anim.fragment_in,
															R.anim.fragment_out);
											AbDialogUtil
													.removeDialog(RouteDetailActivity.this);
										}

										@Override
										public void onNegativeClick() {
											// TODO Auto-generated method stub
											AbDialogUtil
													.removeDialog(RouteDetailActivity.this);
										}
									});
					return;
				}
				if (v.isSelected()) {
					v.setSelected(false);
					int count = Integer.valueOf(likeCount.getText().toString());
					count--;
					likeCount.setText(String.valueOf(count));
				} else {
					v.setSelected(true);
					int count = Integer.valueOf(likeCount.getText().toString());
					count++;
					likeCount.setText(String.valueOf(count));
				}

				// likeButtonClicked();
			}
		});
		/**
		 * 点击收藏按钮触发事件
		 */
		collectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!SharedPreferencesUtil.getSharedPreferences_b(
						RouteDetailActivity.this, Constant.ISLOGINED)) {

					AbDialogUtil
							.showAlertDialog(
									RouteDetailActivity.this,
									0,
									"温馨提示",
									"您还未登陆，或登陆状态过期，请重新登录再试",
									new AbAlertDialogFragment.AbDialogOnClickListener() {

										@Override
										public void onPositiveClick() {
											// TODO Auto-generated method stub
											Intent i = new Intent(
													RouteDetailActivity.this,
													LoginActivity.class);
											RouteDetailActivity.this
													.startActivity(i);
											RouteDetailActivity.this
													.overridePendingTransition(
															R.anim.fragment_in,
															R.anim.fragment_out);
											AbDialogUtil
													.removeDialog(RouteDetailActivity.this);
										}

										@Override
										public void onNegativeClick() {
											// TODO Auto-generated method stub
											AbDialogUtil
													.removeDialog(RouteDetailActivity.this);
										}
									});
					return;
				}

				if (v.isSelected()) {
					v.setSelected(false);
					int count = Integer.valueOf(collectCount.getText()
							.toString());
					count--;
					collectCount.setText(String.valueOf(count));
				} else {
					v.setSelected(true);
					int count = Integer.valueOf(collectCount.getText()
							.toString());
					count++;
					collectCount.setText(String.valueOf(count));
				}
				// collectButtonClicked();
			}
		});

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
		// 点击地图进入骑行页面
		mBaidumap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {

				Intent i = new Intent();
				i.setClass(RouteDetailActivity.this, RidingActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});

		// Marker点击事件
		popView = LayoutInflater.from(this).inflate(
				R.layout.infowindow_interest_points, null);
		name = (TextView) popView.findViewById(R.id.name);
		badge = (ImageView) popView.findViewById(R.id.badge);
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
					i.setClass(RouteDetailActivity.this,
							MarkerDetailActivity.class);
					// i.putExtra("detailInfo",
					// marker.getExtraInfo().getString("detailInfo"));
					i.putExtra("id", marker.getExtraInfo().getString("id"));
					startActivity(i);
					overridePendingTransition(R.anim.fragment_in, 0);
				}
				return true;
			}
		});
		// ===============================================

		loadData();

	}

	private void loadData() {
		
		queryDetail();
		/**
		 * 查询评论列表
		 */
		queryComments();
	}

	private void queryDetail() {
		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(RouteDetailActivity.this, 0, "温馨提示",
					"网络不可用，请设置您的网络后重试", null);
			return;
		}
		String urlString = Constant.serverUrl + Constant.routeDetailUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", routeId);
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processResult(content);
			};

			// 开始执行前
			@Override
			public void onStart() {
				 AbDialogUtil.showProgressDialog(RouteDetailActivity.this, 0,"正在查询，请稍后...");
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {

				 AbDialogUtil.removeDialog(RouteDetailActivity.this);
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				
				AbDialogUtil.removeDialog(RouteDetailActivity.this);

			};

		});
	}

	private void processResult(String resultString) {


		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {

				JSONObject dataObj = resultObj.getJSONObject("data");

				JSONObject routeObj = dataObj.getJSONObject("road");
				TextView titleView = (TextView) findViewById(R.id.title);
				titleView.setText(routeObj.getString("roadTitle"));
				routeNameString = routeObj.getString("roadTitle");
				try {
					distanceText.setText(routeObj.getString("totalDistance"));
				} catch (Exception e) {
					distanceText.setText("10.50");
				}

				speedText.setText(routeObj.getString("averageSpeed"));
				try {
					carbonReduceText.setText(routeObj
							.getString("carbonReduction"));
				} catch (Exception e) {
					carbonReduceText.setText("0.2");
				}

				long totalSecs = routeObj.getLong("totalTime");
				int hours = (int) (totalSecs / 3600);
				int mins = (int) (totalSecs - hours * 3600) / 60;
				int secs = (int) (totalSecs - hours * 3600) % 60;
				hour.setText(String.valueOf(hours));
				minute.setText(String.valueOf(mins));
				second.setText(String.valueOf(secs));

				likeCount.setText(routeObj.getString("likeCount"));
				collectCount.setText(routeObj.getString("collectCount"));
				discussCount.setText("评论(" + routeObj.getString("commentCount")
						+ ")");

				String userPicUrl = routeObj.getString("roadCreatorImg");
				if (!"".equals(userPicUrl)) {
					mAbImageDownloader.display(userHeadPic, Constant.serverUrl
							+ userPicUrl);
				}
				userName.setText(routeObj.getString("roadCreatorName"));

				routeDetail.setText(routeObj.getString("roadContent"));
				exerciseRouteAddress.setText(routeObj.getString("roadPlace"));
				exerciseTime.setText(routeObj.getString("roadStartTime"));

				// 下载和显示图片
				for (int i = 1; i <= 8; i++) {
					String index = "roadContentImg" + String.valueOf(i);
					imageUrls[i - 1] = routeObj.getString(index);
				}
				for (int i = 0; i < 8; i++) {

					if (imageUrls[i] != null && !"".equals(imageUrls[i])) {
						final View mPlayView = mInflater.inflate(
								R.layout.play_view_item, null);
						ImageView mPlayImage = (ImageView) mPlayView
								.findViewById(R.id.mPlayImage);
						mAbSlidingPlayView.addView(mPlayView);
						mAbImageDownloader.display(mPlayImage,
								Constant.serverUrl + imageUrls[i]);
					}
				}

				// 在地图上显示路线图
				try {
					JSONArray pointsArray = dataObj.getJSONArray("points");
					List<LatLng> points = new ArrayList<LatLng>();
					for (int i = 0; i < pointsArray.length(); i++) {
						JSONObject point = pointsArray.getJSONObject(i);
						LatLng ll = new LatLng(point.getDouble("lat"),
								point.getDouble("lng"));
						points.add(ll);
					}
					if (points.size() >= 2) {
						OverlayOptions ooPolyline = new PolylineOptions()
								.width(10).color(0xAAFF0000).points(points);
						mBaidumap.addOverlay(ooPolyline);

						LatLngBounds bounds = new LatLngBounds.Builder()
								.include(points.get(0))
								.include(points.get(points.size() - 1)).build();
						MapStatusUpdate u = MapStatusUpdateFactory
								.newLatLngBounds(bounds);
						mBaidumap.animateMapStatus(u, 500);
					}

				} catch (Exception e) {

				}

				// 将用户添加的友好点表示在地图上
				JSONArray markerBeanArray;
				try {
					markerBeanArray = dataObj.getJSONArray("markers");
					for (int index = 0; index < markerBeanArray.length(); index++) {

						MarkerBean mb = new MarkerBean();
						JSONObject jo = markerBeanArray.getJSONObject(index);
						mb.setMarkerId(jo.getString("id"));
						mb.setMarkerName(jo.getString("name"));
						mb.setLatitude(jo.getDouble("lat"));
						mb.setLongitude(jo.getDouble("lng"));
						JSONObject markerTypeObj = jo
								.getJSONObject("markerTypeRelation");
						mb.setMarkerType(markerTypeObj.getString("markerType"));

						double lat = mb.getLatitude();
						double lng = mb.getLongitude();
						LatLng llA = new LatLng(lat, lng);
						String markerType = mb.getMarkerType();
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
						} else {
							ooA = new MarkerOptions().position(llA)
									.icon(marker_icon_others).zIndex(9)
									.draggable(false);
						}
						Marker mMarkerA = (Marker) (mBaidumap.addOverlay(ooA));
						Bundle b = new Bundle();
						b.putString("name", mb.getMarkerName());
						b.putString("id", mb.getMarkerId());
						b.putString("markerType", markerType);
						mMarkerA.setExtraInfo(b);
						mMarkerA.setTitle("true");
						markerList.add(mMarkerA);
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else {
				AbToastUtil.showToast(RouteDetailActivity.this, "查询失败，请稍后重试");
				RouteDetailActivity.this.finish();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbToastUtil.showToast(RouteDetailActivity.this, "查询失败，请稍后重试");
			RouteDetailActivity.this.finish();
		}

	}

	private void queryComments() {

		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(RouteDetailActivity.this, 0, "温馨提示",
					"网络不可用，请设置您的网络后重试", null);
			return;
		}
		String urlString = Constant.serverUrl + Constant.routeCommentUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", routeId);
		p.put("pageNo", "1");
		p.put("pageSize", "50");
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processCommentResult(content);
			};

			// 开始执行前
			@Override
			public void onStart() {

			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {

				AbDialogUtil.removeDialog(RouteDetailActivity.this);
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				AbDialogUtil.removeDialog(RouteDetailActivity.this);
				
				mAbTaskQueue = AbTaskQueue.getInstance();
				final AbTaskItem item1 = new AbTaskItem();
		        item1.setListener(new AbTaskListener() {

		            @Override
		            public void update() {
		            	AbDialogUtil.removeDialog(RouteDetailActivity.this);           
		            }

		            @Override
		            public void get() {
		                try {
		                    Thread.sleep(1000);
		                } catch (Exception e) {
		                }
		            };
		        });
		       
		        mAbTaskQueue.execute(item1);

			};

		});

	}

	protected void processCommentResult(String resultString) {
		// TODO Auto-generated method stub
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {

				JSONArray dataArray = resultObj.getJSONArray("data");
				commentsString = dataArray.toString();
				discussValueList.clear();
				for (int i = 0; i < dataArray.length(); i++) {

					JSONObject jo = dataArray.getJSONObject(i);
					ContentValues v1 = new ContentValues();
					v1.put("id", jo.getString("id"));
					v1.put("senderId", jo.getString("senderId"));
					v1.put("userName", jo.getString("senderName"));
					v1.put("discussContent", jo.getString("content"));
					if (!"null".equals(jo.getString("senderHeadUrl"))) {
						v1.put("avater",
								Constant.serverUrl
										+ jo.getString("senderHeadUrl"));
					} else {
						v1.put("avater", "");
					}
					v1.put("discussTime", jo.getString("discussTime"));
					v1.put("receiverId", jo.getString("receiverId"));
					v1.put("receiverName", jo.getString("receiverName"));
					discussValueList.add(v1);
				}
				discussAdapter.notifyDataSetChanged();

			} else {
				AbToastUtil.showToast(RouteDetailActivity.this,
						resultObj.getString("message"));
			}

			PublicMethods.setListViewHeightBasedOnChildren(discussList);
			Handler mHandler = new Handler();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					scrollView.fullScroll(ScrollView.FOCUS_UP);
				}
			});

			AbDialogUtil
					.removeDialog(RouteDetailActivity.this, "queryComments");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// showToast("评论列表加载失败，请稍后重试");
		}

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

		case R.id.mapOrPic:
			if (mMapView.getVisibility() == View.VISIBLE) {
				mAbSlidingPlayView.setVisibility(View.VISIBLE);
				mMapView.setVisibility(View.GONE);
			} else {
				mMapView.setVisibility(View.VISIBLE);
				mAbSlidingPlayView.setVisibility(View.GONE);
			}
			break;
		case R.id.discussButton:
			Intent i = new Intent(RouteDetailActivity.this,
					AddRouteCommentActivity.class);
			i.putExtra("id", routeId);
			i.putExtra("comments", commentsString);
			i.putExtra("name", routeNameString);
			i.putExtra("receiverId", "");
			i.putExtra("receiverName", "");
			i.putExtra("commentId", "");
			startActivityForResult(i, 0);
			overridePendingTransition(R.anim.fragment_up, R.anim.fragment_out);
			break;
		default:
			break;
		}
	}

	/**
	 * 退出页面
	 */
	private void goBack() {
		RouteDetailActivity.this.finish();
		overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	}

	private ScrollViewListener scrollChangedListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x,
				int y, int oldx, int oldy) {
			// TODO Auto-generated method stub
			if (y > 0) {
				scrollView.bringToFront();
				spaceArea.setVisibility(View.VISIBLE);
			} else {
				spaceArea.setVisibility(View.INVISIBLE);
				exercisePicArea.bringToFront();
			}
		}

	};

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case 0:
				String discussListString = data.getStringExtra("discussList");
				try {
					JSONArray dataArray = new JSONArray(discussListString);
					commentsString = dataArray.toString();
					if (dataArray.length() > discussValueList.size()) {
						for (int i = discussValueList.size(); i < dataArray
								.length(); i++) {

							JSONObject jo = dataArray.getJSONObject(i);
							ContentValues v1 = new ContentValues();
							v1.put("senderId", jo.getString("senderId"));
							v1.put("userName", jo.getString("senderName"));
							v1.put("discussContent", jo.getString("content"));
							v1.put("avater", jo.getString("senderHeadUrl"));
							v1.put("discussTime", jo.getString("discussTime")
									.substring(0, 19));
							v1.put("receiverId", jo.getString("receiverId"));
							v1.put("receiverName", jo.getString("receiverName"));
							
							discussValueList.add(v1);
						}
						discussAdapter.notifyDataSetChanged();
					}
					PublicMethods
							.setListViewHeightBasedOnChildren(this.discussList);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			default:
				break;
			}

		}

	}
}
