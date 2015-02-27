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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.SearchActivity;
import com.example.bybike.adapter.ExerciseListAdapter2;
import com.example.bybike.adapter.MarkerListAdapter2;
import com.example.bybike.adapter.RoutesBookListAdapter2;
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.exercise.ExerciseDetailActivity3;
import com.example.bybike.friends.FriendsActivity;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.marker.MarkerListActivity;
import com.example.bybike.message.MessageListActivity;
import com.example.bybike.routes.RouteDetailActivity;
import com.example.bybike.setting.SettingMainActivity;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

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
	private AbImageLoader mAbImageLoader = null;
	//
	private Button chooseExercise;
	private Button chooseRouteBook;
	private Button chooseMarker;

	private List<Map<String, Object>> myExerciseListData = null;
	private ExerciseListAdapter2 myExerciseListAdapter = null;
	private AbPullToRefreshView myExerciseListView = null;
	private ListView myExerciseList = null;

	private List<Map<String, Object>> myRouteBookListData = null;
	private RoutesBookListAdapter2 myRouteBookListAdapter = null;
	private AbPullToRefreshView myRouteBookListView = null;
	private ListView myRouteBookList = null;

	private List<MarkerBean> myMarkerListData = null;
	private MarkerListAdapter2 myMarkerListAdapter = null;
	private AbPullToRefreshView myMarkerListView = null;
	private ListView myMarkerList = null;

	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	
	TextView friendsCount;
	TextView totalDistance;
	TextView totalCarbon;

	/**
	 * 用户信息
	 */
	TextView userName;
	CircleImageView userHeadImageView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_uer_mian_page, null);
		AbTitleBar mAbTitleBar = mActivity.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);

		Button gotoSetting = (Button) view.findViewById(R.id.goToSetting);
		gotoSetting.setOnClickListener(click);
		RelativeLayout goToMessage = (RelativeLayout) view
				.findViewById(R.id.goToMessage);
		goToMessage.setOnClickListener(click);

		chooseExercise = (Button) view.findViewById(R.id.chooseExercise);
		chooseExercise.setSelected(true);
		chooseRouteBook = (Button) view.findViewById(R.id.chooseRouteBook);
		chooseMarker = (Button) view.findViewById(R.id.chooseMarker);
		chooseExercise.setOnClickListener(click);
		chooseRouteBook.setOnClickListener(click);
		chooseMarker.setOnClickListener(click);

		// 骑友设置下划线
		friendsCount = (TextView) view.findViewById(R.id.friendsCount);
		friendsCount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		friendsCount.setOnClickListener(click);
		
		TextView friends = (TextView)view.findViewById(R.id.friends);
		friends.setOnClickListener(click);
		
		// 设置用户名字
		userName = (TextView) view.findViewById(R.id.userName);
		totalDistance = (TextView) view.findViewById(R.id.totalDistance);
		totalCarbon = (TextView) view.findViewById(R.id.totalCarbon);
		// 设置用户头像
		mAbImageLoader = AbImageLoader.newInstance(mActivity);
		mAbImageLoader.setLoadingImage(R.drawable.image_loading);
		mAbImageLoader.setErrorImage(R.drawable.image_error);
		mAbImageLoader.setEmptyImage(R.drawable.image_empty);
		userHeadImageView = (CircleImageView) view.findViewById(R.id.fragment_my_image_user);
		
		if(!SharedPreferencesUtil.getSharedPreferences_b(mActivity, Constant.hasLogined)){
			initUserInfo();
		}	

		// 填充用户活动、路书、友好点数据
		mPager = (ViewPager) view.findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
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
		myExerciseListView = (AbPullToRefreshView) myExerciseListLayout.findViewById(R.id.mPullRefreshView);
		myExerciseList = (ListView) myExerciseListLayout.findViewById(R.id.mListView);
		myExerciseList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, ExerciseDetailActivity3.class);
				i.putExtra("id", (String)myExerciseListData.get(position).get("id"));
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
//		View header = mInflater.inflate(R.layout.listview_header, null);
//		myExerciseList.addHeaderView(header);
//		// ListView数据
		myExerciseListData = new ArrayList<Map<String, Object>>();
//		// 使用自定义的Adapter
		myExerciseListAdapter = new ExerciseListAdapter2(mActivity, mActivity, 
				myExerciseListData, R.layout.exercise_list_item2, new String[] {"exercisePic"}, new int[] {R.id.exercisePic});
		myExerciseList.setAdapter(myExerciseListAdapter);
		listViews.add(myExerciseListLayout);
		
		/**
		 * 初始化路书列表
		 */
		View myRouteBookListLayout = mInflater.inflate(R.layout.pull_list, null);
		myRouteBookListView = (AbPullToRefreshView) myRouteBookListLayout.findViewById(R.id.mPullRefreshView);
		myRouteBookList = (ListView) myRouteBookListLayout.findViewById(R.id.mListView);
		myRouteBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	                // TODO Auto-generated method stub
	                Intent intent = new Intent();
	                intent.setClass(mActivity, RouteDetailActivity.class);
	                intent.putExtra("id", (String) myRouteBookListData.get(position).get("id"));
	                mActivity.startActivity(intent);
	                mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	            }

	        });
//		View routeBookHeader = mInflater.inflate(R.layout.listview_header_routebooks, null);
//		myRouteBookList.addHeaderView(routeBookHeader);
		myRouteBookListData = new ArrayList<Map<String, Object>>();
		myRouteBookListAdapter = new RoutesBookListAdapter2(mActivity, mActivity,  myRouteBookListData);
		myRouteBookList.setAdapter(myRouteBookListAdapter);
		listViews.add(myRouteBookListLayout);

		/**
		 * 初始化标记列表
		 */
		View myMarkerListLayout = mInflater.inflate(R.layout.pull_list, null);
		myMarkerListView = (AbPullToRefreshView) myMarkerListLayout.findViewById(R.id.mPullRefreshView);
		myMarkerList = (ListView) myMarkerListLayout.findViewById(R.id.mListView);
//		View markerHeader = mInflater.inflate(R.layout.listview_header_markers, null);
//		myMarkerList.addHeaderView(markerHeader);
		myMarkerListData = new ArrayList<MarkerBean>();
		myMarkerListAdapter = new MarkerListAdapter2(mActivity, mActivity, myMarkerListData);
		myMarkerList.setAdapter(myMarkerListAdapter);
		myMarkerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, MarkerDetailActivity.class);
				i.putExtra("id", myMarkerListData.get(position).getMarkerId());
				mActivity.startActivityForResult(i, mActivity.GO_TO_MARKERDETAIL_ACTIVITY);
				mActivity.overridePendingTransition(R.anim.fragment_in,R.anim.fragment_out);
			}
		});
		listViews.add(myMarkerListLayout);

		/**
		 * 显示viewPager
		 */
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		searchExerciseList();
		queryRouteBookList();
		searchMarkerList();

	}
	
	/**
	 * 从服务器获取用户信息，并更新到页面
	 */
	private void initUserInfo(){
		
		String urlString = Constant.serverUrl + Constant.getUserInfoUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.USERID));
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processUserInfoResult(content);
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
//				mActivity.mProgressDialog.dismiss();
			};

		}, jsession);
		
		
	}
	
	
	private void processUserInfoResult(String resultString) {
		
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				JSONObject dataObject = resultObj.getJSONObject("data");
				String nickname = dataObject.getString("name");
//				String account = dataObject.getString("loginName");
				String carbonCount = dataObject.getString("carbonCount");
				String totalDistance = dataObject.getString("totalDistance");
				String totalFriend = dataObject.getString("friendCount");
				
				String headPic;
				try {
					headPic = dataObject.getString("headUrl");
				} catch (JSONException e) {
					headPic = "";
				}
				
				userName.setText(nickname);
				if (headPic.length() > 0) {
					mAbImageLoader.display(userHeadImageView, Constant.serverUrl
							+ headPic);
				}
				this.totalDistance.setText(totalDistance);
				totalCarbon.setText(carbonCount);
				friendsCount.setText(totalFriend);
				
//				String userId = dataObject.getString("id");
				SharedPreferencesUtil.saveSharedPreferences_s(mActivity,Constant.USERNICKNAME, nickname);
				SharedPreferencesUtil.saveSharedPreferences_s(mActivity,Constant.USERAVATARURL, headPic);
				SharedPreferencesUtil.saveSharedPreferences_s(mActivity, Constant.carbonCount, carbonCount);
				SharedPreferencesUtil.saveSharedPreferences_s(mActivity, Constant.totalDistance, totalDistance);
				SharedPreferencesUtil.saveSharedPreferences_s(mActivity, Constant.FRIENDSCOUNT, totalFriend);

			} else {
//				AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//			AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
		}
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
				Intent goToMessageIntent = new Intent();
				goToMessageIntent
						.setClass(mActivity, MessageListActivity.class);
				mActivity.startActivity(goToMessageIntent);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
				break;
			case R.id.goToSetting:
				Intent i = new Intent();
				i.setClass(mActivity, SettingMainActivity.class);
				mActivity.startActivityForResult(i, 10005);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
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
				mActivity.startActivity(friendsIntent);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
				break;
			case R.id.friends:
				Intent goFriendsIntent = new Intent();
				goFriendsIntent.setClass(mActivity, FriendsActivity.class);
				mActivity.startActivity(goFriendsIntent);
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

	@Override
	public void onResume() {
		super.onResume();
		userName.setText(SharedPreferencesUtil.getSharedPreferences_s(
				mActivity, Constant.USERNICKNAME));
		totalDistance.setText(SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.totalDistance));
		totalCarbon.setText(SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.carbonCount));
		friendsCount.setText(SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.FRIENDSCOUNT));
		String userHeadPicUrl = SharedPreferencesUtil.getSharedPreferences_s(
				mActivity, Constant.USERAVATARURL);
		if (userHeadPicUrl.length() > 0) {
			mAbImageLoader.display(userHeadImageView, Constant.serverUrl
					+ userHeadPicUrl);
		}

	}

	/**
	 * 查询路书列表======================================================================================================
	 */
	private void queryRouteBookList() {

		String urlString = Constant.serverUrl + Constant.routeListUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
//		p.put("pageNo", String.valueOf(pageNo));
//		p.put("pageSize", String.valueOf(pageSize));
//		if ("createDateDesc".equalsIgnoreCase(orderType)
//				|| "PopularDesc".equalsIgnoreCase(orderType)) {
//			p.put("orderBy", orderType);
//		} else if ("orderByDistance".equalsIgnoreCase(orderType)) {
//			p.put("kilometersStart", kilometersStart);
//			p.put("kilometersEnd", kilometersEnd);
//		} else if ("orderByArea".equalsIgnoreCase(orderType)) {
//			p.put("districtId", areaId);
//		}
		p.put("pageNo", "1");
		p.put("pageSize", "30");
		p.put("creatorId", SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.USERID));
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processRouteListResult(content);
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
//				mActivity.mProgressDialog.dismiss();
			};

		}, jsession);
	}

	private void processRouteListResult(String resultString) {
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {

				if(myRouteBookListData != null){
					myRouteBookListData.clear();
				}else{
					myRouteBookListData = new ArrayList<Map<String, Object>>();
				}
				
				JSONArray dataArray = resultObj.getJSONArray("data");
//				if (dataArray.length() <= 0) {
//					AbToastUtil.showToast(mActivity, "没有符合条件的记录...");
//				}
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject jo = dataArray.getJSONObject(i);

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", jo.getString("roadId"));
					map.put("routePic",
							"http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
					for (int index = 1; index <= 8; index++) {
						String tmpIndex = "roadContentImg"
								+ String.valueOf(index);
						String imgUrl = jo.getString(tmpIndex);
						if (null != imgUrl && !"".equals(imgUrl)) {
							map.put("routePic",
									Constant.serverUrl
											+ jo.getString("roadContentImg1"));
							break;
						}
					}
					map.put("title", jo.getString("roadTitle"));
					map.put("routeAddress", jo.getString("roadPlace"));
					map.put("exerciseTime", jo.getString("roadStartTime"));
					map.put("likeCount", jo.getString("likeCount"));
					map.put("likeStatus", jo.getString("likeStatus"));
					map.put("commentCount", jo.getString("commentCount"));
					map.put("collectCount", jo.getString("collectCount"));
					map.put("collectStatus", jo.getString("collectStatus"));
					map.put("kilometers", jo.getString("totalDistance"));
					map.put("roadContent", jo.getString("roadContent"));
					map.put("creatorId", jo.getString("roadCreatorId"));
					map.put("userHeadPicUrl", jo.getString("roadCreatorImg"));
					map.put("userName", jo.getString("roadCreatorName"));

					myRouteBookListData.add(map);
				}

//				if (refreshOrLoadMore) {
//					list.clear();
//				}
//				if (newList != null && newList.size() > 0) {
//					list.addAll(newList);
//				}
//				if (refreshOrLoadMore) {
//					mAbPullToRefreshView.onHeaderRefreshFinish();
//				} else {
//					mAbPullToRefreshView.onFooterLoadFinish();
//				}
//				newList.clear();
				myRouteBookListAdapter.notifyDataSetChanged();

			} else {
				AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
		}
	}

	
	
	/**
	 * 查询标记点列表====================================================================================================
	 */
	/**
	 * 搜索标记点
	 */
	private void searchMarkerList() {

		String urlString = Constant.serverUrl + Constant.getMarkerListUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "100");
		p.put("creatorId", SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.USERID));

		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processMarkerListResult(content);
			};

			// 开始执行前
			@Override
			public void onStart() {

//				mProgressDialog.show();
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

	protected void processMarkerListResult(String content) {
		// TODO Auto-generated method stub

		try {
			JSONObject resultObj = new JSONObject(content);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				JSONArray dataArray = resultObj.getJSONArray("data");
				if(myMarkerListData != null){
					myMarkerListData.clear();
				}else{
					myMarkerListData = new ArrayList<MarkerBean>();
				}
				
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject jo = dataArray.getJSONObject(i);
					MarkerBean mb = new MarkerBean();
					mb.setMarkerId(jo.getString("Id"));
					mb.setAddress(jo.getString("address"));
					mb.setCollectCount(jo.getString("collectCount"));
					mb.setLikeCount(jo.getString("likeCount"));
					mb.setCommentCount(jo.getString("commentCount"));
					mb.setMarkerName(jo.getString("name"));
					mb.setMarkerType(jo.getString("markerType"));
					
					//复用表，保存likeStatus和collectStatus
					mb.setImgurl1(jo.getString("likeStatus"));
					mb.setImgurl2(jo.getString("collectStatus"));

					myMarkerListData.add(mb);
				}

				myMarkerListAdapter.notifyDataSetChanged();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 查询活动列表====================================================================================================
	 */
	private void searchExerciseList() {

		String urlString = Constant.serverUrl + Constant.exerciseListUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "10");
		p.put("creatorId", SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.USERID));

		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processExerciseListResult(content);
			};

			// 开始执行前
			@Override
			public void onStart() {

//				mProgressDialog.show();
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

	protected void processExerciseListResult(String content) {
		// TODO Auto-generated method stub

		try {
			JSONObject resultObj = new JSONObject(content);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				JSONArray dataArray = resultObj.getJSONArray("data");
				if(myExerciseListData != null){
					myExerciseListData.clear();
				}else{
					myExerciseListData = new ArrayList<Map<String, Object>>();
				}
			
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject jo = dataArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String, Object>();

                    map.put("id", jo.getString("id"));
                    map.put("exercisePic", "http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
                    String imgUrl = jo.getString("activityImgUrl");
                    if (null != imgUrl && !"".equals(imgUrl.trim())) {
                        map.put("exercisePic", Constant.serverUrl + imgUrl);
                    }
                    map.put("exerciseTitle", jo.getString("title"));
                    map.put("exerciseAddress", jo.getString("wayLine"));
                    map.put("exerciseTime", jo.getString("activityStartDate") + "-" + jo.getString("activityEndDate"));
                    map.put("likeCount", jo.getString("likeCount"));
                    map.put("talkCount", jo.getString("commentCount"));
                    map.put("collectCount", jo.getString("collectCount"));
                    map.put("relityActivityNumber", jo.getString("relityActivityNumber"));
                    map.put("likeStatus", jo.getString("likeStatus"));
                    map.put("collectStatus", jo.getString("collectStatus"));
                    if ("".equals(jo.getString("relityActivityNumber"))) {
                        map.put("relityActivityNumber", "0");
                    }
                    map.put("joinStatus", jo.getString("joinStatus"));
                    
                    myExerciseListData.add(map);
                }

                myExerciseListAdapter.notifyDataSetChanged();
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
