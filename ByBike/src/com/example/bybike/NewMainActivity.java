package com.example.bybike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ab.activity.AbActivity;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.example.bybike.exercise.ExerciseListFragment;
import com.example.bybike.routes.RoutesBookMainFragment;
import com.example.bybike.user.UserMainPageFragment;

public class NewMainActivity extends AbActivity {

//	private SlidingMenu menu;
	public int currentItem = 0;
	public FragmentManager fragmentManager;
	public Fragment fragment;
	
	public String tag = "";
	public String currentTag = "";//用来保存上面的tag状态
	
	private AbTaskQueue mAbTaskQueue = null;
	private boolean exit = false;
	AbTaskItem item1 = null;
	
	RelativeLayout clickRideButtonPage = null;

	RelativeLayout titleBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.sliding_menu_content);
		getTitleBar().setVisibility(View.GONE);
		
		clickRideButtonPage = (RelativeLayout)findViewById(R.id.cover);
		// 主视图的Fragment添加
		fragmentManager = getSupportFragmentManager();
		titleBar = (RelativeLayout)findViewById(R.id.titleBar);
		initSteps();
	}
	

	private void initSteps() {

	    //初始化导航栏图标
	    changeBackground(1);
	    changeMainFragment(R.id.mapButton);
	    
		mAbTaskQueue = AbTaskQueue.getInstance();
		/**
		 * 任务1： 1. 当用户在MainActivity按下返回键时触发 2.
		 * 该任务在两秒后将退出标志exit置为false,此时再次点击返回键不退出应用 3. 在任务update前，点击返回键，将退出应用
		 **/
		item1 = new AbTaskItem();
		item1.listener = new AbTaskListener() {
			@Override
			public void update() {
				exit = false;
			}

			@Override
			public void get() {
				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}
			};
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
			super.onBackPressed();
	}

	public void changeMainFragment(int toItem) {
		// TODO Auto-generated method stub
		//如果当前页面和点击的页面是同一个，则跳出
		if(toItem == currentItem) return;
		if(toItem == R.id.rideButton){
			clickRideButtonPage.setVisibility(View.VISIBLE);
			currentItem = R.id.rideButton;
			return;
		}else{
			clickRideButtonPage.setVisibility(View.GONE);
		}
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment currentFragment = fragmentManager.findFragmentByTag(currentTag);
		if(currentFragment != null) {
			// 将当前的Frament隐藏到后台去
			transaction.hide(currentFragment);
		}
		switch (toItem) {
		case R.id.myButton:
			tag = UserMainPageFragment.class.getSimpleName();
			// 显示我的页面
			if (fragmentManager.findFragmentByTag(tag) != null) {
				fragment = (UserMainPageFragment) fragmentManager
						.findFragmentByTag(tag);
			} else {
				fragment = new UserMainPageFragment();
			}
			titleBar.setVisibility(View.GONE);
			break;
			
		case R.id.mapButton:
			// 显示地图页面
			tag = MainPageFragment2.class.getSimpleName();
			// 显示我的页面
			if (fragmentManager.findFragmentByTag(tag) != null) {
				fragment = (MainPageFragment2) fragmentManager
						.findFragmentByTag(tag);
			} else {
				fragment = new MainPageFragment2();
			}
			titleBar.setVisibility(View.VISIBLE);
			break;
		case R.id.exerciseButton:
			//显示活动列表页面
			tag = ExerciseListFragment.class.getSimpleName();
			// 显示我的页面
			if (fragmentManager.findFragmentByTag(tag) != null) {
				fragment = (ExerciseListFragment) fragmentManager
						.findFragmentByTag(tag);
			} else {
				fragment = new ExerciseListFragment();
			}
			titleBar.setVisibility(View.VISIBLE);
			break;
		case R.id.routeButton:
			//显示路书页面
			tag = RoutesBookMainFragment.class.getSimpleName();
			// 显示我的页面
			if (fragmentManager.findFragmentByTag(tag) != null) {
				fragment = (RoutesBookMainFragment) fragmentManager
						.findFragmentByTag(tag);
			} else {
				fragment = new RoutesBookMainFragment();
			}
			titleBar.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		currentTag = tag;
		currentItem = toItem;
		if (fragment.isAdded()) {
			transaction.show(fragment);
		} else {
			transaction.add(R.id.content_frame, fragment, currentTag);
		}
		transaction.commit();
	}

	/**
	 * 监听按钮事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (!exit) {
				exit = true;
				showToast("再按一次返回键退出应用");
				mAbTaskQueue.execute(item1);
			} else {
				shutdown();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 关闭程序
	 */
	public void shutdown() {

		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * 接受xml页面调用点击方法 onclick(这里用一句话描述这个方法的作用)
	 * 
	 * @param view
	 */
	public void onclick(View view) {

		switch (view.getId()) {
		case R.id.mapButton:
		    changeBackground(1);
			changeMainFragment(view.getId());
			break;

		case R.id.exerciseButton:
		    changeBackground(2);
			changeMainFragment(view.getId());
			break;
			
		case R.id.rideButton:
		    changeBackground(3);
			changeMainFragment(view.getId());
			break;
			
		case R.id.routeButton:
		    changeBackground(4);
			changeMainFragment(view.getId());
			break;
		case R.id.myButton:
		    changeBackground(5);
			changeMainFragment(view.getId());
			break;
		case R.id.search:
			Intent i = new Intent();
			i.setClass(NewMainActivity.this, SearchActivity.class);
			startActivity(i);
//			overridePendingTransition(R.anim.fragment_in, 0);
			break;
		default:
			break;
		}
		
	}

	int[]ids = new int[]{R.id.r1, R.id.r2, R.id.r3, R.id.r4, R.id.r5};
	int[]buttonIds = new int[]{R.id.mapButton, R.id.exerciseButton, R.id.rideButton, R.id.routeButton, R.id.myButton};

	/**
	 * 修改导航栏图标和背景
	  * changeBackground(这里用一句话描述这个方法的作用)
	  * @param i
	 */
	private void changeBackground(int i){
	    
	    for(int j = 1; j <= 5; j ++){
	        
	        RelativeLayout rl = (RelativeLayout)findViewById(ids[j-1]);
	        Button b = (Button)findViewById(buttonIds[j-1]);
	        if(j == i){
	            rl.setSelected(true);
	            b.setSelected(true);
	        }else{
	            rl.setSelected(false);
	            b.setSelected(false);
	        }
	    }
	}
}
