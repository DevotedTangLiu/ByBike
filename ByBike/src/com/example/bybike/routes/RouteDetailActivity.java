package com.example.bybike.routes;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.example.bybike.routes.ObservableScrollView.ScrollViewListener;
import com.example.bybike.util.PublicMethods;

public class RouteDetailActivity extends AbActivity {

	// 基础地图相关
	MapView mMapView = null;
	BaiduMap mBaidumap = null;
	// 图片区域
	RelativeLayout exercisePicArea = null;
	AbSlidingPlayView mAbSlidingPlayView = null;
	// 滚动区域
	ObservableScrollView scrollView = null;
	private LinearLayout spaceArea;
	// 评论列表
	ListView discussList = null;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;
	// 点赞、评论、分享区域
	RelativeLayout likeButton;
	TextView likeCount;
	RelativeLayout collectButton;
	TextView collectCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setAbContentView(R.layout.activity_route_detail);

		mAbSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);

		// 图片的下载
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

		scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
		scrollView.setScrollViewListener(scrollChangedListener);
		spaceArea = (LinearLayout) findViewById(R.id.spaceArea);
		exercisePicArea = (RelativeLayout) findViewById(R.id.exercisePicArea);

		// 按钮点击事件
		likeButton = (RelativeLayout) findViewById(R.id.likeButton);
		likeCount = (TextView) findViewById(R.id.likeCount);
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
		collectButton = (RelativeLayout) findViewById(R.id.collectButton);
		collectCount = (TextView) findViewById(R.id.collectCount);
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

		// 加载评论列表
		discussList = (ListView) findViewById(R.id.discussList);
		discussValueList = new ArrayList<ContentValues>();
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
		discussAdapter = new ExerciseDiscussListAdapter(this, discussValueList);
		discussList.setAdapter(discussAdapter);
		PublicMethods.setListViewHeightBasedOnChildren(discussList);
		Handler mHandler = new Handler();
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_UP);
			}
		});
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
