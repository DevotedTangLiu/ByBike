package com.example.bybike.exercise;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.sliding.AbSlidingPlayView;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.ecloud.pulltozoomview.PullToZoomListViewEx;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter2;

public class ExerciseDetailActivity3 extends AbActivity {

	/**
	 * 高德地图相关
	 */
	private MapView mMapView;
	private AMap aMap;
	private UiSettings mUiSettings;

	AbSlidingPlayView mAbSlidingPlayView = null;
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;

	// 评论列表
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter2 discussAdapter = null;
	PullToZoomListViewEx discussListView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pull_to_zoom_list_view);

		discussListView = (PullToZoomListViewEx) findViewById(R.id.listview);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter2(
				ExerciseDetailActivity3.this, discussValueList);
		discussListView.setAdapter(discussAdapter);
		discussListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.e("zhuwenwu", "position = " + position);
					}
				});

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
		int mScreenHeight = localDisplayMetrics.heightPixels;
		int mScreenWidth = localDisplayMetrics.widthPixels;
		AbsListView.LayoutParams localObject = new AbsListView.LayoutParams(
				mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
		discussListView.setHeaderLayoutParams(localObject);

		// ===============初始化地图========================
		// 获取地图控件引用
		mMapView = (MapView) discussListView.getZoomView().findViewById(
				R.id.bmapView);
		mMapView.onCreate(savedInstanceState);// 必须要写
		if (aMap == null) {
			aMap = mMapView.getMap();
			mUiSettings = aMap.getUiSettings();
		}
		// 设置原生缩放按钮不可用不可见
		mUiSettings.setZoomControlsEnabled(false);
		// 设置比例尺不可见
		mUiSettings.setScaleControlsEnabled(false);
		mUiSettings.setMyLocationButtonEnabled(false);
		aMap.animateCamera(CameraUpdateFactory.zoomTo(15), 100, null);
		discussListView.getZoomView().setOnTouchListener(new OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	            	discussListView.requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	            	discussListView.requestDisallowInterceptTouchEvent(false);
	                break;
	            }
	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
		// ===============================================
		mAbSlidingPlayView = (AbSlidingPlayView) discussListView.getZoomView()
				.findViewById(R.id.mAbSlidingPlayView);
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
		default:
			break;
		}
	}
	
	/**
	 * 初始化视图
	 */
	private void loadData() {

		//在地图上显示数据
		
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

		//填充详细数据
		ContentValues v0 = new ContentValues();
		discussValueList.add(v0);
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
	 * 退出页面
	 */
	private void goBack() {
		ExerciseDetailActivity3.this.finish();
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
		loadData();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}
}
