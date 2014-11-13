/*
 * @(#) UserMainPageFragment.java
 * @Author:tangliu(mail) 2014-10-15
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseListAdapter2;
import com.example.bybike.adapter.MarkerListAdapter;
import com.example.bybike.adapter.RoutesBookListAdapter2;
import com.example.bybike.exercise.ExerciseDetailActivity;
import com.example.bybike.friends.FriendsActivity;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.routes.RouteDetailActivity;
import com.example.bybike.setting.SettingMainActivity;

/**
 * @author tangliu(mail) 2014-10-15
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class UserMainPageFragment extends Fragment {

	private NewMainActivity mActivity = null;
	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;
	//
	private Button chooseExercise;
	private Button chooseRouteBook;
	private Button chooseMarker;

	private List<Map<String, Object>> myExerciseListData = null;
	private ExerciseListAdapter2 exerciseListAdapter = null;
	private AbPullListView myExerciseListView = null;

	private List<Map<String, Object>> myRouteBookListData = null;
	private RoutesBookListAdapter2 myRouteBookListAdapter = null;
	private AbPullListView myRouteBookListView = null;

	private List<Map<String, Object>> myMarkerListData = null;
	private MarkerListAdapter myMarkerListAdapter = null;
	private AbPullListView myMarkerListView = null;

	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_uer_mian_page, null);
		AbTitleBar mAbTitleBar = mActivity.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
		mAbHttpUtil.setDebug(false);

		Button gotoSetting = (Button) view.findViewById(R.id.goToSetting);
		gotoSetting.setOnClickListener(click);
		RelativeLayout goToMessage = (RelativeLayout)view.findViewById(R.id.goToMessage);
		goToMessage.setOnClickListener(click);

		chooseExercise = (Button) view.findViewById(R.id.chooseExercise);
		chooseExercise.setSelected(true);
		chooseRouteBook = (Button) view.findViewById(R.id.chooseRouteBook);
		chooseMarker = (Button) view.findViewById(R.id.chooseMarker);
		chooseExercise.setOnClickListener(click);
		chooseRouteBook.setOnClickListener(click);
		chooseMarker.setOnClickListener(click);

		mPager = (ViewPager) view.findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		
		TextView friendsCount = (TextView)view.findViewById(R.id.friendsCount);
        friendsCount.setOnClickListener(click);
		
		initViewPager();

		return view;
	}

	/**
	 * 初始化viewPager数据 initViewPager(这里用一句话描述这个方法的作用)
	 */
	private void initViewPager() {

		LayoutInflater mInflater = mActivity.getLayoutInflater();
		/**
		 * 初始化我的活动列表
		 */
		View myExerciseListLayout = mInflater.inflate(R.layout.pull_list, null);
		myExerciseListView = (AbPullListView) myExerciseListLayout
				.findViewById(R.id.mListView);
		View header = mInflater.inflate(R.layout.listview_header, null);
		myExerciseListView.addHeaderView(header);

		// ListView数据
		myExerciseListData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("exercisePic",
					"http://img5.imgtn.bdimg.com/it/u=1530701415,1979691644&fm=21&gp=0.jpg");
			myExerciseListData.add(map);
		}
		// 使用自定义的Adapter
		exerciseListAdapter = new ExerciseListAdapter2(mActivity, mActivity,
				myExerciseListData, R.layout.exercise_list_item2, new String[] {
						"exercisePic", "exerciseTitle", "exerciseAddress",
						"exerciseTime", "exerciseUserCount", "lickCount",
						"talkCount", "collectCount" }, new int[] {
						R.id.exercisePic, R.id.exerciseTitle,
						R.id.exerciseRouteAddress, R.id.exerciseTime,
						R.id.userCount, R.id.likeCount, R.id.talkCount,
						R.id.collectCount });
		myExerciseListView.setAdapter(exerciseListAdapter);
		// item被点击事件
		// item被点击事件
		myExerciseListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, ExerciseDetailActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
		listViews.add(myExerciseListLayout);
		/**
		 * 初始化路书列表
		 */
		View myRouteBookListLayout = mInflater
				.inflate(R.layout.pull_list, null);
		myRouteBookListView = (AbPullListView) myRouteBookListLayout
				.findViewById(R.id.mListView);
		View routeBookHeader = mInflater.inflate(
				R.layout.listview_header_routebooks, null);
		myRouteBookListView.addHeaderView(routeBookHeader);
		myRouteBookListData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			myRouteBookListData.add(map);
		}
		myRouteBookListAdapter = new RoutesBookListAdapter2(mActivity,
				myRouteBookListData);
		myRouteBookListView.setAdapter(myRouteBookListAdapter);
		myRouteBookListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, RouteDetailActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
		listViews.add(myRouteBookListLayout);

		/**
		 * 初始化标记列表
		 */
		View myMarkerListLayout = mInflater.inflate(R.layout.pull_list, null);
		myMarkerListView = (AbPullListView) myMarkerListLayout
				.findViewById(R.id.mListView);
		View markerHeader = mInflater.inflate(R.layout.listview_header_markers,
				null);
		myMarkerListView.addHeaderView(markerHeader);
		myMarkerListData = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			myMarkerListData.add(map);
		}
		myMarkerListAdapter = new MarkerListAdapter(mActivity, myMarkerListData);
		myMarkerListView.setAdapter(myMarkerListAdapter);
		myMarkerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, MarkerDetailActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
		listViews.add(myMarkerListLayout);

		/**
		 * 显示viewPager
		 */
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());

	}

	/**
	 * 点击事件
	 */
	private OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.goToMessage:
				createSystemNoticeMessage();
				break;
			case R.id.goToSetting:
				Intent i = new Intent();
				i.setClass(mActivity, SettingMainActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, 0);
				break;
			case R.id.chooseExercise:
				mPager.setCurrentItem(0);
				chooseExercise.setSelected(true);
				chooseRouteBook.setSelected(false);
				chooseMarker.setSelected(false);
				break;

			case R.id.chooseRouteBook:
				mPager.setCurrentItem(1);
				chooseExercise.setSelected(false);
				chooseRouteBook.setSelected(true);
				chooseMarker.setSelected(false);
				break;
				
			case R.id.chooseMarker:
				mPager.setCurrentItem(2);
				chooseExercise.setSelected(false);
				chooseRouteBook.setSelected(false);
				chooseMarker.setSelected(true);
				break;
			case R.id.friendsCount:
				Intent friendsIntent = new Intent();
				friendsIntent.setClass(mActivity, FriendsActivity.class);
				startActivity(friendsIntent);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
				break;
			default:
				break;
			}

		}
	};

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				chooseExercise.setSelected(true);
				chooseRouteBook.setSelected(false);
				chooseMarker.setSelected(false);
				break;
			case 1:
				chooseExercise.setSelected(false);
				chooseRouteBook.setSelected(true);
				chooseMarker.setSelected(false);
				break;
			case 2:
				chooseExercise.setSelected(false);
				chooseRouteBook.setSelected(false);
				chooseMarker.setSelected(true);
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
	}

	/**
	 * 生成系统通知栏消息的例子
	 */
	private void createSystemNoticeMessage() {
		// 获取系统通知服务引用
		String ns = mActivity.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) mActivity
				.getSystemService(ns);

		// 自定义下拉视图
		Notification notification = new Notification();
		notification.icon = R.drawable.me3;
		notification.tickerText = "系统通知demo...";

		RemoteViews contentView = new RemoteViews(mActivity.getPackageName(),
				R.layout.system_notice_layout);
		contentView.setTextViewText(R.id.title,
				"Hello, this message is in a custom expanded view");
		contentView.setTextViewText(R.id.content,
				"从前有座山，山上有座庙，庙里有个老和尚，老和尚经常和小和尚说...");
		notification.contentView = contentView;

		// 通知被点击后，自动消失
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法
		// 但是必须定义 contentIntent
		Intent intent = new Intent(mActivity, SettingMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(mActivity, 0,
				intent, 0);
		notification.contentIntent = contentIntent;

		mNotificationManager.notify(3, notification);
	}
}
