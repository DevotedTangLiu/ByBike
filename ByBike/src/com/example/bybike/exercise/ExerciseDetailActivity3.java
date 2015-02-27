package com.example.bybike.exercise;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.sliding.AbSlidingPlayView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.friends.AddFriendsActivity;
import com.example.bybike.marker.AddCommentActivity;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.riding.RidingActivity;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class ExerciseDetailActivity3 extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	private String exerciseId = "";
	private String activityNameString = "";
	private String commentsString = "";

	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 图片区域
	RelativeLayout exercisePicArea = null;
	AbSlidingPlayView mAbSlidingPlayView = null;
	LinearLayout coverView;
	String[] imageUrls = new String[8];
	// 评论列表
	ListView discussList = null;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	// 图片下载类
	private AbImageLoader mAbImageDownloader = null;

	View detailheader;
	// 点赞、评论、分享区域
	RelativeLayout likeButton;
	TextView likeCount;
	RelativeLayout collectButton;
	TextView collectCount;
	TextView distance;
	TextView timeLong;
	TextView publishTime;
	TextView exerciseTitle;
	TextView exerciseRouteAddress;
	TextView exerciseTime;
	TextView exerciseDetail;
	TextView exercisePrice;
	TextView deadline;
	TextView applyUserCount;
	TextView discussCount;
	Button discussButton;

	Button shareButton;
	Button mapOrPic;
	Button mapOrPicMap;

	private String activityData;
	TextView applyText;
	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_detaill3);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mProgressDialog = new ProgressDialog(ExerciseDetailActivity3.this, 5);
		// 设置点击屏幕Dialog不消失
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("正在查询，请稍后...");

		exerciseId = getIntent().getStringExtra("id");

		// 评论列表
		discussList = (ListView) findViewById(R.id.discussList);
		discussList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ContentValues cv = discussValueList.get(position - 1);

				Intent i = new Intent(ExerciseDetailActivity3.this,
						AddActivityCommentActivity.class);
				i.putExtra("id", exerciseId);
				i.putExtra("comments", commentsString);
				i.putExtra("name", activityNameString);
				i.putExtra("receiverId", cv.getAsString("senderId"));
				i.putExtra("receiverName", cv.getAsString("userName"));
				i.putExtra("commentId", cv.getAsString("id"));
				startActivityForResult(i, 0);
				overridePendingTransition(R.anim.fragment_up,
						R.anim.fragment_out);

			}
		});
		// 添加header
		detailheader = mInflater.inflate(R.layout.header_exercise_detail, null);

		mapOrPic = (Button) detailheader.findViewById(R.id.mapOrPic);
		mapOrPicMap = (Button) detailheader.findViewById(R.id.mapOrPicMap);
		exercisePicArea = (RelativeLayout) detailheader
				.findViewById(R.id.exercisePicArea);
		mAbSlidingPlayView = (AbSlidingPlayView) detailheader
				.findViewById(R.id.mAbSlidingPlayView);
		coverView = (LinearLayout) detailheader.findViewById(R.id.coverView);

		likeButton = (RelativeLayout) detailheader
				.findViewById(R.id.likeButton);
		likeCount = (TextView) detailheader.findViewById(R.id.likeCount);
		collectButton = (RelativeLayout) detailheader
				.findViewById(R.id.collectButton);
		collectCount = (TextView) detailheader.findViewById(R.id.collectCount);
		discussCount = (TextView) detailheader.findViewById(R.id.discussCount);
		discussButton = (Button) detailheader.findViewById(R.id.discussButton);

		distance = (TextView) detailheader.findViewById(R.id.distance);
		timeLong = (TextView) detailheader.findViewById(R.id.timeLong);
		publishTime = (TextView) detailheader.findViewById(R.id.publishTime);
		exerciseTitle = (TextView) detailheader
				.findViewById(R.id.exerciseTitle);
		exerciseRouteAddress = (TextView) detailheader
				.findViewById(R.id.exerciseRouteAddress);
		exerciseTime = (TextView) detailheader.findViewById(R.id.exerciseTime);
		exerciseDetail = (TextView) detailheader
				.findViewById(R.id.exerciseDetail);
		exercisePrice = (TextView) detailheader
				.findViewById(R.id.exercisePrice);
		deadline = (TextView) detailheader.findViewById(R.id.deadline);
		applyUserCount = (TextView) detailheader
				.findViewById(R.id.applyUserCount);

		discussList.addHeaderView(detailheader);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				ExerciseDetailActivity3.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
		applyText = (TextView) findViewById(R.id.applyText);
		initActivity();
	}

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
						ExerciseDetailActivity3.this, Constant.ISLOGINED)) {

					AbDialogUtil
							.showAlertDialog(
									ExerciseDetailActivity3.this,
									0,
									"温馨提示",
									"您还未登陆，或登陆状态过期，请重新登录再试",
									new AbAlertDialogFragment.AbDialogOnClickListener() {

										@Override
										public void onPositiveClick() {
											// TODO Auto-generated method stub
											Intent i = new Intent(
													ExerciseDetailActivity3.this,
													LoginActivity.class);
											ExerciseDetailActivity3.this
													.startActivity(i);
											ExerciseDetailActivity3.this
													.overridePendingTransition(
															R.anim.fragment_in,
															R.anim.fragment_out);
											AbDialogUtil
													.removeDialog(ExerciseDetailActivity3.this);
										}

										@Override
										public void onNegativeClick() {
											// TODO Auto-generated method stub
											AbDialogUtil
													.removeDialog(ExerciseDetailActivity3.this);
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

				likeButtonClicked();
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
						ExerciseDetailActivity3.this, Constant.ISLOGINED)) {

					AbDialogUtil
							.showAlertDialog(
									ExerciseDetailActivity3.this,
									0,
									"温馨提示",
									"您还未登陆，或登陆状态过期，请重新登录再试",
									new AbAlertDialogFragment.AbDialogOnClickListener() {

										@Override
										public void onPositiveClick() {
											// TODO Auto-generated method stub
											Intent i = new Intent(
													ExerciseDetailActivity3.this,
													LoginActivity.class);
											ExerciseDetailActivity3.this
													.startActivity(i);
											ExerciseDetailActivity3.this
													.overridePendingTransition(
															R.anim.fragment_in,
															R.anim.fragment_out);
											AbDialogUtil
													.removeDialog(ExerciseDetailActivity3.this);
										}

										@Override
										public void onNegativeClick() {
											// TODO Auto-generated method stub
											AbDialogUtil
													.removeDialog(ExerciseDetailActivity3.this);
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
				collectButtonClicked();
			}
		});

		/**
		 * 定义discussList的触摸事件和滚动事件
		 */
		discussList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (detailheader.getTop() >= 0) {
						coverView.setVisibility(View.GONE);
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				// TODO Auto-generated method stub
				if (detailheader.getTop() < 0) {
					int tmp = (int) (Math.abs(detailheader.getTop()) * 255 / 500);
					if (tmp > 255)
						tmp = 255;
					int color = Color.argb(tmp, 0, 0, 0);
					coverView.setVisibility(View.VISIBLE);
					coverView.setBackgroundColor(color);
				} else {
					coverView.setVisibility(View.GONE);
				}

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
				i.setClass(ExerciseDetailActivity3.this, RidingActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		// ===============================================

		loadData();

	}

	/**
	 * 初始化视图
	 */
	private void loadData() {
		/**
		 * 查询活动详情
		 */
		queryDetail();
		/**
		 * 查询评论列表
		 */
		queryComments();
	}

	private void queryComments() {

		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(ExerciseDetailActivity3.this, 0,
					"温馨提示", "网络不可用，请设置您的网络后重试", null);
			return;
		}
		String urlString = Constant.serverUrl + Constant.getCommentList;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("activityId", exerciseId);
		p.put("pageNo", "1");
		p.put("pageSize", "100");
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
				mProgressDialog.show();
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				mProgressDialog.dismiss();
			};

		}, jsession);

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
				AbToastUtil.showToast(ExerciseDetailActivity3.this,
						resultObj.getString("message"));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// showToast("评论列表加载失败，请稍后重试");
		}

	}

	private void queryDetail() {
		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(ExerciseDetailActivity3.this, 0,
					"温馨提示", "网络不可用，请设置您的网络后重试", null);
			return;
		}
		String urlString = Constant.serverUrl + Constant.exerciseDetailUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", exerciseId);
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
				mProgressDialog.show();
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				mProgressDialog.dismiss();
			};

		}, jsession);
	}

	private void processResult(String resultString) {
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {

				JSONObject dataObj = resultObj.getJSONObject("data");
				JSONObject activityObj = dataObj.getJSONObject("activity");
				activityData = activityObj.toString();
				TextView titleView = (TextView) findViewById(R.id.title);
				titleView.setText(activityObj.getString("title"));
				exerciseTitle.setText(activityObj.getString("title"));
				activityNameString = activityObj.getString("title");
				distance.setText(activityObj.getString("wayLength"));
				timeLong.setText("约" + activityObj.getString("wasteTime")
						+ "小时");
				// 发布时间
				publishTime.setText(activityObj.getString("publishDate")
						.substring(0, 11));
				exerciseRouteAddress.setText("约"
						+ activityObj.getString("wayLength") + "km  "
						+ activityObj.getString("wayLine"));
				exerciseTime.setText(activityObj.getString("activityStartDate")
						+ "-" + activityObj.getString("activityEndDate"));
				exerciseDetail.setText(activityObj.getString("introduction"));
				exercisePrice.setText(activityObj.getString("entryFee") + " 元");
				deadline.setText(activityObj.getString("endBookDate"));
				applyUserCount.setText(activityObj
						.getString("relityActivityNumber"));
				collectCount.setText(activityObj.getString("collectCount"));
				likeCount.setText(activityObj.getString("likeCount"));
				discussCount.setText("评论 ("
						+ activityObj.getString("commentCount") + ")");

				if ("71".equals(activityObj.getString("joinStatus"))) {
					applyText.setText("已报名");
				} else {
					applyText.setText("报名");
				}

				if ("true".equals(activityObj.getString("likeStatus"))) {
					likeButton.setSelected(true);
				} else {
					likeButton.setSelected(false);
				}

				if ("true".equals(activityObj.getString("collectStatus"))) {
					collectButton.setSelected(true);
				} else {
					collectButton.setSelected(false);
				}

				// 下载和显示图片
				for (int i = 1; i <= 8; i++) {
					if (i == 1) {
						imageUrls[i - 1] = activityObj
								.getString("activityImgUrl");
					} else {
						String index = "img" + String.valueOf(i) + "Url";
						imageUrls[i - 1] = activityObj.getString(index);
					}

				}
				mAbImageDownloader = AbImageLoader
						.newInstance(ExerciseDetailActivity3.this);
				mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
				mAbImageDownloader.setErrorImage(R.drawable.image_error);
				mAbImageDownloader.setEmptyImage(R.drawable.image_empty);
				for (int i = 0; i < 8; i++) {

					if (imageUrls[i] != null && !"".equals(imageUrls[i])) {
						final View mPlayView = mInflater.inflate(
								R.layout.play_view_item, null);
						ImageView mPlayImage = (ImageView) mPlayView
								.findViewById(R.id.mPlayImage);
						// mAbSlidingPlayView.setPageLineHorizontalGravity(Gravity.CENTER);
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
					OverlayOptions ooPolyline = new PolylineOptions().width(10)
							.color(0xAAFF0000).points(points);
					mBaidumap.addOverlay(ooPolyline);

					LatLngBounds bounds = new LatLngBounds.Builder()
							.include(points.get(0))
							.include(points.get(points.size() - 1)).build();
					MapStatusUpdate u = MapStatusUpdateFactory
							.newLatLngBounds(bounds);
					mBaidumap.animateMapStatus(u, 500);

				} catch (Exception e) {

				}

			} else {
				AbToastUtil.showToast(ExerciseDetailActivity3.this,
						"查询失败，请稍后重试");
				ExerciseDetailActivity3.this.finish();
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbToastUtil.showToast(ExerciseDetailActivity3.this, "查询失败，请稍后重试");
			ExerciseDetailActivity3.this.finish();

		}

	}

	/**
	 * 接受页面button调用
	 * 
	 * @param source
	 */
	public void clickHandler(View source) {

		switch (source.getId()) {
		case R.id.goBack:
			goBack();
			break;

		case R.id.mapOrPicMap:
			mapOrPicMap.setVisibility(View.GONE);
			mapOrPic.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.VISIBLE);
			mAbSlidingPlayView.setVisibility(View.GONE);
			break;
		case R.id.mapOrPic:
			mapOrPicMap.setVisibility(View.VISIBLE);
			mapOrPic.setVisibility(View.GONE);
			mMapView.setVisibility(View.GONE);
			mAbSlidingPlayView.setVisibility(View.VISIBLE);
			break;
		case R.id.shareButton: // 点击分享按钮
			showShare();
			break;
		case R.id.applyArea: // 点击报名事件
			if (applyText.getText().toString().equals("报名")) {
				applyClick();
			} else {
				AbToastUtil.showToast(ExerciseDetailActivity3.this,
						"你已经报过名了...");
			}
			break;
		case R.id.discussButton:
			Intent i = new Intent(ExerciseDetailActivity3.this,
					AddActivityCommentActivity.class);
			i.putExtra("id", exerciseId);
			i.putExtra("comments", commentsString);
			i.putExtra("name", activityNameString);
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
		ExerciseDetailActivity3.this.finish();
		overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	}

	/**
	 * 显示报名的对话框
	 */
	private void applyClick() {

		Intent i = new Intent(ExerciseDetailActivity3.this,
				ApplyAcitivity.class);
		i.putExtra("activityData", activityData);
		startActivity(i);
		overridePendingTransition(R.anim.fragment_up, R.anim.fragment_out);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mMapView.onDestroy();
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		AbDialogUtil.removeDialog(ExerciseDetailActivity3.this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	private void showShare() {

		AbToastUtil.showToast(ExerciseDetailActivity3.this, "正在努力建设中，敬请期待...");
//		ShareSDK.initSDK(this);
//		OnekeyShare oks = new OnekeyShare();
//		oks.setSilent(true); // 隐藏编辑页面
//		// 关闭sso授权
//		oks.disableSSOWhenAuthorize();
//		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.ic_launcher,
//				getString(R.string.app_name));
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(getString(R.string.share));
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText(exerciseDetail.getText().toString());
//		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath("");// 确保SDcard下面存在此张图片
//		// url仅在微信（包括好友和朋友圈）中使用
//		oks.setUrl("http://sharesdk.cn");
//		// 启动分享GUI
//		oks.show(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case 0:
				String discussList = data.getStringExtra("discussList");
				try {
					JSONArray dataArray = new JSONArray(discussList);
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

	/**
	 * 点赞/取消点赞 queryDetail(这里用一句话描述这个方法的作用)
	 */
	private void likeButtonClicked() {

		String urlString = Constant.serverUrl + Constant.exerciseLikeClicked;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", exerciseId);
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

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

		}, jsession);
	}

	/**
	 * 收藏/取消收藏 queryDetail(这里用一句话描述这个方法的作用)
	 */
	private void collectButtonClicked() {

		String urlString = Constant.serverUrl + Constant.exerciseCollectClicked;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", exerciseId);
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

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

		}, jsession);
	}
}
