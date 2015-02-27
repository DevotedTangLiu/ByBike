/*
 * @(#) UserPageActivity.java
 * @Author:tangliu(mail) 2014-12-17
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

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseListAdapter2;
import com.example.bybike.adapter.MarkerListAdapter2;
import com.example.bybike.adapter.RoutesBookListAdapter2;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.exercise.ExerciseDetailActivity3;
import com.example.bybike.friends.FriendsActivity;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.routes.RouteDetailActivity;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

/**
  * @author tangliu(mail) 2014-12-17
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class UserPageActivity extends AbActivity {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    
    private String userId = "";
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
	
	public ProgressDialog mProgressDialog;
	
	private boolean beApplyed = false;

    /**
     * 用户信息
     */
    TextView userName;
    CircleImageView userHeadImageView;
    Button addFriend;
    Button deleteFriend;   
    Button agreeAddFriend;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_uer_mian_page);
        getTitleBar().setVisibility(View.GONE);
        
        mProgressDialog = new ProgressDialog(UserPageActivity.this, 5);
        // 设置点击屏幕Dialog不消失
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("正在查询，请稍后...");
        
        userId = getIntent().getStringExtra("id");
        if(userId == null || "".equals(userId)){
        	AbToastUtil.showToast(UserPageActivity.this, "用户id未传入...");
        }
        
        String tmp = getIntent().getStringExtra("beApplyed");
        if(tmp == null){
        	beApplyed = false;
        }else{
        	beApplyed = true;
        }

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);

        chooseExercise = (Button) findViewById(R.id.chooseExercise);
        chooseExercise.setSelected(true);
        chooseRouteBook = (Button) findViewById(R.id.chooseRouteBook);
        chooseMarker = (Button) findViewById(R.id.chooseMarker);

        // 骑友设置下划线
        friendsCount = (TextView) findViewById(R.id.friendsCount);
//        friendsCount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        // 设置用户名字
        userName = (TextView) findViewById(R.id.userName);
		totalDistance = (TextView) findViewById(R.id.totalDistance);
		totalCarbon = (TextView) findViewById(R.id.totalCarbon);
        // 设置用户头像
        mAbImageLoader = AbImageLoader.newInstance(UserPageActivity.this);
        userHeadImageView = (CircleImageView)findViewById(R.id.fragment_my_image_user);
        
        addFriend = (Button)findViewById(R.id.addFriend);
        deleteFriend = (Button)findViewById(R.id.deleteFriend);
        agreeAddFriend = (Button)findViewById(R.id.agreeAddFriend);

        initUserInfo();
        // 填充用户活动、路书、友好点数据
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        initViewPager();
    }
    
    /**
	 * 从服务器获取用户信息，并更新到页面
	 */
	private void initUserInfo(){
		
		String urlString = Constant.serverUrl + Constant.getUserInfoUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("id", userId);
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
	
	
	private void processUserInfoResult(String resultString) {
		
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				JSONObject dataObject = resultObj.getJSONObject("data");
				String nickname = dataObject.getString("name");
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
				
				String isFriend = dataObject.getString("isFriend");
				if("0".equals(isFriend)){
					if(beApplyed){
						agreeAddFriend.setVisibility(View.VISIBLE);
						addFriend.setVisibility(View.GONE);			
					}else{
						agreeAddFriend.setVisibility(View.GONE);
						addFriend.setVisibility(View.VISIBLE);		
					}			
					deleteFriend.setVisibility(View.GONE);
				}else{
					addFriend.setVisibility(View.GONE);
					deleteFriend.setVisibility(View.VISIBLE);
				}

			} else {
				AbToastUtil.showToast(UserPageActivity.this, "查询失败，请稍后重试");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbToastUtil.showToast(UserPageActivity.this, "查询失败，请稍后重试");
		}
	}
    
    /**
     * 初始化viewPager数据 initViewPager(这里用一句话描述这个方法的作用)
     */
    private void initViewPager() {

    	LayoutInflater mInflater = UserPageActivity.this.getLayoutInflater();
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
				i.setClass(UserPageActivity.this, ExerciseDetailActivity3.class);
				i.putExtra("id", (String)myExerciseListData.get(position).get("id"));
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
//		View header = mInflater.inflate(R.layout.listview_header, null);
//		myExerciseList.addHeaderView(header);
//		// ListView数据
		myExerciseListData = new ArrayList<Map<String, Object>>();
//		// 使用自定义的Adapter
		myExerciseListAdapter = new ExerciseListAdapter2(this, UserPageActivity.this,myExerciseListData, R.layout.exercise_list_item2, new String[] {"exercisePic"}, new int[] {R.id.exercisePic});
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
	                intent.setClass(UserPageActivity.this, RouteDetailActivity.class);
	                intent.putExtra("id", (String) myRouteBookListData.get(position).get("id"));
	                startActivity(intent);
	                overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	            }

	        });
//		View routeBookHeader = mInflater.inflate(R.layout.listview_header_routebooks, null);
//		myRouteBookList.addHeaderView(routeBookHeader);
		myRouteBookListData = new ArrayList<Map<String, Object>>();
		myRouteBookListAdapter = new RoutesBookListAdapter2(UserPageActivity.this, UserPageActivity.this, myRouteBookListData);
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
		myMarkerListAdapter = new MarkerListAdapter2(UserPageActivity.this, UserPageActivity.this, myMarkerListData);
		myMarkerList.setAdapter(myMarkerListAdapter);
		myMarkerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(UserPageActivity.this, MarkerDetailActivity.class);
				i.putExtra("id", myMarkerListData.get(position).getMarkerId());
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in,R.anim.fragment_out);
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

    public void onClick(View v){
        switch (v.getId()) {
        case R.id.goBack:
            UserPageActivity.this.finish();
            break;
//        case R.id.friendsCountArea:
//            Intent friendsIntent = new Intent();
//            friendsIntent.setClass(UserPageActivity.this, FriendsActivity.class);
//            startActivity(friendsIntent);
//            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
//            break;
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
        case R.id.addFriend:
        	AbDialogUtil.showAlertDialog(UserPageActivity.this, 0, "温馨提示", "确认要邀请此用户添加好友吗？",
	    			new AbAlertDialogFragment.AbDialogOnClickListener() {

				@Override
				public void onPositiveClick() {
					// TODO Auto-generated method stub
					
					addFriendRequest();
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
				@Override
				public void onNegativeClick() {
					// TODO Auto-generated method stub
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
            });
        	break;
        case R.id.agreeAddFriend:
        	AbDialogUtil.showAlertDialog(UserPageActivity.this, 0, "温馨提示", "确认要添加此用户为好友吗？",
	    			new AbAlertDialogFragment.AbDialogOnClickListener() {

				@Override
				public void onPositiveClick() {
					// TODO Auto-generated method stub
					
					addFriendRequest();
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
				@Override
				public void onNegativeClick() {
					// TODO Auto-generated method stub
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
            });
        	break;
        	
        case R.id.deleteFriend:
        	AbDialogUtil.showAlertDialog(UserPageActivity.this, 0, "温馨提示", "确认要删除此好友吗？",
	    			new AbAlertDialogFragment.AbDialogOnClickListener() {

				@Override
				public void onPositiveClick() {
					// TODO Auto-generated method stub
					
					deleteFriendRequest();
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
				@Override
				public void onNegativeClick() {
					// TODO Auto-generated method stub
					AbDialogUtil.removeDialog(UserPageActivity.this);
				}
            });
        	break;
        default:
            break;
        }
    }
    
    /**
     * 添加好友请求======================================================================================================
     */
    private void addFriendRequest(){
    	
    	String urlString = Constant.serverUrl + Constant.addFriendUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("friendId", userId);
		p.put("remarks", "");

		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processAddFriendResult(content);
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
    
    private void processAddFriendResult(String resultString) {
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				if(beApplyed){
					AbToastUtil.showToast(UserPageActivity.this, "好友添加成功...");
					addFriend.setVisibility(View.GONE);
					agreeAddFriend.setVisibility(View.GONE);
					deleteFriend.setVisibility(View.VISIBLE);
				}else{
					AbToastUtil.showToast(UserPageActivity.this, "请求已发送，等待对方确认...");
				}
				
			} else {
				AbToastUtil.showToast(UserPageActivity.this, resultObj.getString("message"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /**
     * 删除好友请求======================================================================================================
     */
    private void deleteFriendRequest(){
    	
    	String urlString = Constant.serverUrl + Constant.deleteFriendUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("friendId", userId);
		p.put("remarks", "");

		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processDeleteFriendResult(content);
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
    
    private void processDeleteFriendResult(String resultString) {
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				AbToastUtil.showToast(UserPageActivity.this, "删除成功...");
				addFriend.setVisibility(View.VISIBLE);
				deleteFriend.setVisibility(View.GONE);
				agreeAddFriend.setVisibility(View.GONE);
			} else {
				AbToastUtil.showToast(UserPageActivity.this, "删除好友请求失败，请稍后再试...");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    
    /**
	 * 查询路书列表======================================================================================================
	 */
	private void queryRouteBookList() {

		String urlString = Constant.serverUrl + Constant.routeListUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
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
		p.put("creatorId", userId);
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
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "100");
		p.put("creatorId", userId);

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
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(UserPageActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "10");
		p.put("creatorId", userId);

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
	
	
	 /**
     * 
      * onListViewButtonClicked(这里用一句话描述这个方法的作用)
      * @param fucntionType  0：markerList 1:exerciseList 2:routeBookList
      * @param buttonType    0: likeButton                1:collectButton
      * @param id
     */
    public void onListViewButtonClicked(int fucntionType, int buttonType, String id) {

        String urlString = "";
        switch (fucntionType) {
        case 0:
            if(0 == buttonType){
                urlString = Constant.serverUrl + Constant.markerLikeClicked;
            }else{
                urlString = Constant.serverUrl + Constant.markerCollectClicked;
            }        
            break;

        case 1:
            if(0 == buttonType){
                urlString = Constant.serverUrl + Constant.exerciseLikeClicked;
            }else{
                urlString = Constant.serverUrl + Constant.exerciseCollectClicked;
            }
            break;
        case 2:
        	if(0 == buttonType){
        		urlString = Constant.serverUrl + Constant.routeLikeClicked;
        	}else{
        		urlString = Constant.serverUrl + Constant.routeCollectClicked;
        	}
            break;
        default:
            break;
        }

        String jsession = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", id);
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
            public void onFailure(int statusCode, String content, Throwable error) {
            }
            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
            };

        }, jsession);

    }

}
