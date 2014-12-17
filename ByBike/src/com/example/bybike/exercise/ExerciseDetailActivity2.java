package com.example.bybike.exercise;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.sliding.AbSlidingPlayView;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.util.DensityUtil;

public class ExerciseDetailActivity2 extends AbActivity {

	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 图片区域
	RelativeLayout exercisePicArea = null;
	AbSlidingPlayView mAbSlidingPlayView = null;
	LinearLayout coverView;
	// 滚动区域
	private LinearLayout spaceArea;
	// 评论列表
	ListView discussList = null;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;

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

	private ScalingRunnable mScalingRunnable;
	private int mHeaderHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise_detaill2);
		getTitleBar().setVisibility(View.GONE);
		exercisePicArea = (RelativeLayout) findViewById(R.id.exercisePicArea);
		mAbSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);
		coverView = (LinearLayout) findViewById(R.id.coverView);
		discussList = (ListView) findViewById(R.id.discussList);
		/**
		 * 获取屏幕分辨率、设置地图（图片列表）高度
		 */
		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int mScreenWidth = localDisplayMetrics.widthPixels;
		mHeaderHeight = (int) (9.0F * (mScreenWidth / 16.0F));
		RelativeLayout.LayoutParams localObject = new RelativeLayout.LayoutParams(
				mScreenWidth, mHeaderHeight);
		localObject.topMargin = DensityUtil.dip2px(this, 60);
		exercisePicArea.setLayoutParams(localObject);

		// 添加header
		detailheader = mInflater.inflate(R.layout.detail_header, null);
		spaceArea = (LinearLayout) detailheader.findViewById(R.id.spaceArea);
		/**
		 * 设置空白区域的高度和marginTop
		 */
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
				mScreenWidth, mHeaderHeight);
		exercisePicArea.setLayoutParams(localObject);

		likeButton = (RelativeLayout) detailheader
				.findViewById(R.id.likeButton);
		likeCount = (TextView) detailheader.findViewById(R.id.likeCount);
		collectButton = (RelativeLayout) detailheader
				.findViewById(R.id.collectButton);
		collectCount = (TextView) detailheader.findViewById(R.id.collectCount);
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
		discussCount = (TextView) detailheader.findViewById(R.id.discussCount);
		discussButton = (Button) detailheader.findViewById(R.id.discussButton);
		likeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
			}
		});
		collectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
			}
		});

		discussList.addHeaderView(detailheader);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				ExerciseDetailActivity2.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		/**
		 * 定义discussList的触摸事件和滚动事件
		 */
		discussList.setOnTouchListener(onListViewTouch);
		discussList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					if (detailheader.getTop() < 0) {
						// discussList.bringToFront();
						// spaceArea.setVisibility(View.VISIBLE);
					} else {
						exercisePicArea.bringToFront();
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

					discussList.bringToFront();
					int tmp = (int) (Math.abs(detailheader.getTop()) * 255 / mHeaderHeight);
					if (tmp > 255)
						tmp = 255;
					int color = Color.argb(tmp, 0, 0, 0);
					coverView.setVisibility(View.VISIBLE);
					coverView.setBackgroundColor(color);
				}

			}
		});
		mScalingRunnable = new ScalingRunnable();

		// ===============初始化地图========================
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaidumap = mMapView.getMap();
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
		// ===============================================

		// 点击报名事件
		TextView applyArea = (TextView) findViewById(R.id.applyArea);
		applyArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				applyClick();
			}
		});

	}

	/**
	 * 初始化视图
	 */
	private void loadData() {

		// 在地图上显示数据

		// 下载和显示图片
		mAbImageDownloader = new AbImageDownloader(this);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setNoImage(R.drawable.image_no);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);

		for (int i = 0; i < 3; i++) {

			final View mPlayView = mInflater.inflate(R.layout.play_view_item,
					null);
			ImageView mPlayImage = (ImageView) mPlayView
					.findViewById(R.id.mPlayImage);
			mAbSlidingPlayView.setPageLineHorizontalGravity(Gravity.CENTER);
			mAbSlidingPlayView.addView(mPlayView);

			mAbImageDownloader
					.display(mPlayImage,
							"http://img0.imgtn.bdimg.com/it/u=1196327338,3394668792&fm=21&gp=0.jpg");
		}

		// 填充详细数据

		// 填充评论列表
		for (int i = 0; i < 2; i++) {
			ContentValues v1 = new ContentValues();
			v1.put("userName", "ChaolotteYam");
			v1.put("discussContent", "有爱。");
			v1.put("avater",
					"http://t11.baidu.com/it/u=1610160448,1299213022&fm=56");
			v1.put("discussTime", "10.28 14:30");
			discussValueList.add(v1);
			ContentValues v2 = new ContentValues();
			v2.put("userName", "Jeronmme_1221");
			v2.put("discussContent", "今天倍儿爽！");
			v2.put("avater",
					"http://t11.baidu.com/it/u=1620038746,1252150868&fm=56");
			v2.put("discussTime", "10.28 14:48");
			discussValueList.add(v2);
		}
		discussAdapter.notifyDataSetChanged();
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

		case R.id.mapOrPic:
			if (mMapView.getVisibility() == View.VISIBLE) {
				mAbSlidingPlayView.setVisibility(View.VISIBLE);
				mMapView.setVisibility(View.GONE);
			} else {
				mMapView.setVisibility(View.VISIBLE);
				mAbSlidingPlayView.setVisibility(View.GONE);
			}
			break;
		case R.id.hasBike:
			if (hasBike.isSelected()) {
				hasBike.setSelected(false);
			} else {
				hasBike.setSelected(true);
			}
			break;
		case R.id.hasHelmet:
			if (hasHelmet.isSelected()) {
				hasHelmet.setSelected(false);
			} else {
				hasHelmet.setSelected(true);
			}
			break;
			
		case R.id.shareButton:
			showShare();
			break;
		default:
			break;
		}
	}

	/**
	 * 退出页面
	 */
	private void goBack() {
		ExerciseDetailActivity2.this.finish();
		overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	}

	/**
	 * 显示报名页面
	 */
	Dialog dialog;
	Button hasBike;
	Button hasHelmet;

	/**
	 * 显示报名的对话框
	 */
	private void applyClick() {
		dialog = new Dialog(this, R.style.Theme_dialog);
		LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = l.inflate(R.layout.apply_for_exercise_layout, null);

		final EditText realNameText = (EditText) view
				.findViewById(R.id.realName);
		final EditText phoneNumberText = (EditText) view
				.findViewById(R.id.phoneNumber);
		final EditText cardNumberText = (EditText) view
				.findViewById(R.id.cardNumber);
		TextView commentText = (TextView) view.findViewById(R.id.comment);
		hasBike = (Button) view.findViewById(R.id.hasBike);
		hasHelmet = (Button) view.findViewById(R.id.hasHelmet);
		Button cancelButton = (Button) view.findViewById(R.id.cancelButton);
		Button submitButton = (Button) view.findViewById(R.id.submitButton);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 获取数据
				String realName = realNameText.getText().toString().trim();
				String phoneNumber = phoneNumberText.getText().toString()
						.trim();
				String cardNumber = cardNumberText.getText().toString().trim();
				if (realName.equals("")) {
					showDialog("温馨提示", "请输入真实姓名");
				} else if (phoneNumber.equals("")) {
					showDialog("温馨提示", "请输入电话号码");
				} else if (cardNumber.equals("")) {
					showDialog("温馨提示", "请输入身份证号");
				} else {
					dialog.dismiss();
				}
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mMapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		removeProgressDialog();
		loadData();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 定义listView的触摸事件
	 */
	private OnListViewTouch onListViewTouch = new OnListViewTouch();
	private float mLastMotionY;
	private float mInitialMotionY;
	private boolean isZooming = false;

	private class OnListViewTouch implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = mInitialMotionY = event.getY();
				discussList.bringToFront();
				discussList.requestLayout();
				discussList.invalidate();
				return true;

			case MotionEvent.ACTION_MOVE:

				mLastMotionY = event.getY();
				if ((event.getY() - mInitialMotionY) > 1f
						&& detailheader.getTop() >= 0) {
					isZooming = true;
					pullEvent();
					return true;
				}

			case MotionEvent.ACTION_UP:
				if (isZooming) {
					smoothScrollToTop();
					isZooming = false;
					return true;
				}
				break;
			default:
				break;
			}
			return false;
		}

	}

	/**
	 * 重置动画，自动滑动到顶部
	 */
	protected void smoothScrollToTop() {
		Log.d("ExerciseDetailActivity2", "smoothScrollToTop --> ");
		exercisePicArea.bringToFront();
		coverView.setVisibility(View.GONE);
		mScalingRunnable.startAnimation(200L);
	}

	private void pullEvent() {
		final int newScrollValue;
		final float initialMotionValue, lastMotionValue;

		initialMotionValue = mInitialMotionY;
		lastMotionValue = mLastMotionY;

		newScrollValue = Math.round(Math.min(initialMotionValue
				- lastMotionValue, 0) / 2.0f);

		pullHeaderToZoom(newScrollValue);
	}

	/**
	 * 通过设置"headerView"的高度，实现放大的效果
	 * 
	 * @param newScrollValue
	 */
	protected void pullHeaderToZoom(int newScrollValue) {
		// Log.d("ExerciseDetailActivity2",
		// "pullHeaderToZoom --> newScrollValue = " + newScrollValue);
		// Log.d("ExerciseDetailActivity2",
		// "pullHeaderToZoom --> mHeaderHeight = " + mHeaderHeight);
		if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
			mScalingRunnable.abortAnimation();
		}

		ViewGroup.LayoutParams localLayoutParams = exercisePicArea
				.getLayoutParams();
		localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;
		exercisePicArea.setLayoutParams(localLayoutParams);

		LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) spaceArea
				.getLayoutParams();
		llp.height = localLayoutParams.height;
		spaceArea.setLayoutParams(llp);
		
		mMapView.requestLayout();
		mMapView.invalidate();
		

	}

	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float paramAnonymousFloat) {
			float f = paramAnonymousFloat - 1.0F;
			return 1.0F + f * (f * (f * (f * f)));
		}
	};

	/**
	 * 这个runable用于下拉放大列表listview的“header”后，将header和listview弹回原位置
	 * 
	 * @author tangliu
	 * 
	 */
	class ScalingRunnable implements Runnable {
		protected long mDuration;
		protected boolean mIsFinished = true;
		protected float mScale;
		protected long mStartTime;

		ScalingRunnable() {
		}

		public void abortAnimation() {
			mIsFinished = true;
		}

		public boolean isFinished() {
			return mIsFinished;
		}

		public void run() {
			if (exercisePicArea != null) {
				float f2;
				ViewGroup.LayoutParams localLayoutParams;
				LinearLayout.LayoutParams llp;
				if ((!mIsFinished) && (mScale > 1.0D)) {
					float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime)
							/ (float) mDuration;
					f2 = mScale - (mScale - 1.0F)
							* sInterpolator.getInterpolation(f1);
					localLayoutParams = exercisePicArea.getLayoutParams();
					llp = (LinearLayout.LayoutParams) spaceArea
							.getLayoutParams();
					Log.d("ExerciseDetailActivity2",
							"ScalingRunnable --> f2 = " + f2);
					if (f2 > 1.0F) {
						int tmpHeight = (int) f2 * mHeaderHeight;
						localLayoutParams.height = tmpHeight;
						exercisePicArea.setLayoutParams(localLayoutParams);

						llp.height = localLayoutParams.height;
						spaceArea.setLayoutParams(llp);

						exercisePicArea.post(this);
						return;
					}
					mIsFinished = true;
				}
			}
		}

		public void startAnimation(long paramLong) {
			if (exercisePicArea != null) {
				mStartTime = SystemClock.currentThreadTimeMillis();
				mDuration = paramLong;
				mScale = ((float) (exercisePicArea.getBottom()) / mHeaderHeight);
				mIsFinished = false;
				exercisePicArea.post(this);
			}
		}
	}

	private void showShare() {
//		ShareSDK.initSDK(this);
//		OnekeyShare oks = new OnekeyShare();
//		// 关闭sso授权
//		oks.disableSSOWhenAuthorize();
//
//		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.ic_launcher,
//				getString(R.string.app_name));
//		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(getString(R.string.share));
//		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//		oks.setTitleUrl("http://sharesdk.cn");
//		// text是分享文本，所有平台都需要这个字段
//		oks.setText("我是分享文本");
//		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
//		// url仅在微信（包括好友和朋友圈）中使用
//		oks.setUrl("http://sharesdk.cn");
//		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//		oks.setComment("我是测试评论文本");
//		// site是分享此内容的网站名称，仅在QQ空间使用
//		oks.setSite(getString(R.string.app_name));
//		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
//		oks.setSiteUrl("http://sharesdk.cn");
//
//		// 启动分享GUI
//		oks.show(this);
	}

}
