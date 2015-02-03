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

import android.content.Intent;
import android.graphics.Paint;
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
import com.ab.http.AbHttpUtil;
import com.ab.image.AbImageLoader;
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
 // 图片下载类
    private AbImageLoader mAbImageLoader = null;
    //
    private Button chooseExercise;
    private Button chooseRouteBook;
    private Button chooseMarker;

    private List<Map<String, Object>> myExerciseListData = null;
    private ExerciseListAdapter2 exerciseListAdapter = null;
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

    /**
     * 用户信息
     */
    TextView userName;
    CircleImageView userHeadImageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_uer_mian_page);
        getTitleBar().setVisibility(View.GONE);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);

        chooseExercise = (Button) findViewById(R.id.chooseExercise);
        chooseExercise.setSelected(true);
        chooseRouteBook = (Button) findViewById(R.id.chooseRouteBook);
        chooseMarker = (Button) findViewById(R.id.chooseMarker);

        // 骑友设置下划线
        TextView friendsCount = (TextView) findViewById(R.id.friendsCount);
        friendsCount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
        // 设置用户名字
        userName = (TextView) findViewById(R.id.userName);
        // 设置用户头像
        mAbImageLoader = AbImageLoader.newInstance(UserPageActivity.this);
        userHeadImageView = (CircleImageView)findViewById(R.id.fragment_my_image_user);
        
        userName.setText(SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERNICKNAME));
        String userHeadPicUrl = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERAVATARURL);
        if(userHeadPicUrl.length() > 0){
            mAbImageLoader.display(userHeadImageView, Constant.serverUrl + userHeadPicUrl);
        }

        // 填充用户活动、路书、友好点数据
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        initViewPager();

    }
    
    /**
     * 初始化viewPager数据 initViewPager(这里用一句话描述这个方法的作用)
     */
    private void initViewPager() {

        LayoutInflater mInflater = getLayoutInflater();
        /**
         * 初始化我的活动列表
         */
        View myExerciseListLayout = mInflater.inflate(R.layout.pull_list, null);
        myExerciseListView = (AbPullToRefreshView) myExerciseListLayout.findViewById(R.id.mPullRefreshView);
        myExerciseList = (ListView) myExerciseListLayout.findViewById(R.id.mListView);
        View header = mInflater.inflate(R.layout.listview_header, null);
        myExerciseList.addHeaderView(header);

        // ListView数据
        myExerciseListData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("exercisePic",
                    "http://img5.imgtn.bdimg.com/it/u=1530701415,1979691644&fm=21&gp=0.jpg");
            myExerciseListData.add(map);
        }
        // 使用自定义的Adapter
        exerciseListAdapter = new ExerciseListAdapter2(UserPageActivity.this, UserPageActivity.this,
                myExerciseListData, R.layout.exercise_list_item2, new String[] {
                        "exercisePic", "exerciseTitle", "exerciseAddress",
                        "exerciseTime", "exerciseUserCount", "lickCount",
                        "talkCount", "collectCount" }, new int[] {
                        R.id.exercisePic, R.id.exerciseTitle,
                        R.id.exerciseRouteAddress, R.id.exerciseTime,
                        R.id.userCount, R.id.likeCount, R.id.talkCount,
                        R.id.collectCount });
        myExerciseList.setAdapter(exerciseListAdapter);
        // item被点击事件
        // item被点击事件
        myExerciseList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Intent i = new Intent();
                i.setClass(UserPageActivity.this, ExerciseDetailActivity3.class);
                startActivity(i);
                overridePendingTransition(R.anim.fragment_in,
                        R.anim.fragment_out);
            }
        });
        listViews.add(myExerciseListLayout);
        /**
         * 初始化路书列表
         */
        View myRouteBookListLayout = mInflater
                .inflate(R.layout.pull_list, null);
        myRouteBookListView = (AbPullToRefreshView) myRouteBookListLayout.findViewById(R.id.mPullRefreshView);
        myRouteBookList = (ListView) myRouteBookListLayout.findViewById(R.id.mListView);
        View routeBookHeader = mInflater.inflate(R.layout.listview_header_routebooks, null);
        myRouteBookList.addHeaderView(routeBookHeader);
        myRouteBookListData = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            myRouteBookListData.add(map);
        }
        myRouteBookListAdapter = new RoutesBookListAdapter2(UserPageActivity.this,
                myRouteBookListData);
        myRouteBookList.setAdapter(myRouteBookListAdapter);
        myRouteBookList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Intent i = new Intent();
                i.setClass(UserPageActivity.this, RouteDetailActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fragment_in,
                        R.anim.fragment_out);
            }
        });
        listViews.add(myRouteBookListLayout);

        /**
         * 初始化标记列表
         */
        View myMarkerListLayout = mInflater.inflate(R.layout.pull_list, null);
        myMarkerListView = (AbPullToRefreshView) myRouteBookListLayout.findViewById(R.id.mPullRefreshView);
        myMarkerList = (ListView) myRouteBookListLayout.findViewById(R.id.mListView);
        View markerHeader = mInflater.inflate(R.layout.listview_header_markers, null);
        myMarkerList.addHeaderView(markerHeader);
        myMarkerListData = new ArrayList<MarkerBean>();
        for (int i = 0; i < 10; i++) {
            MarkerBean mb = new MarkerBean();
            myMarkerListData.add(mb);
        }
        myMarkerListAdapter = new MarkerListAdapter2(UserPageActivity.this, myMarkerListData);
        myMarkerList.setAdapter(myMarkerListAdapter);
        myMarkerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Intent i = new Intent();
                i.setClass(UserPageActivity.this, MarkerDetailActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fragment_in,
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
        case R.id.friendsCountArea:
            Intent friendsIntent = new Intent();
            friendsIntent.setClass(UserPageActivity.this, FriendsActivity.class);
            startActivity(friendsIntent);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
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
        default:
            break;
        }
    }

}
