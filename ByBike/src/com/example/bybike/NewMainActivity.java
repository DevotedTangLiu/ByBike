package com.example.bybike;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.ab.activity.AbActivity;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.ab.view.slidingmenu.SlidingMenu;
import com.example.bybike.exercise.ExerciseListFragment;
import com.example.bybike.riding.RidingFragment;
import com.example.bybike.routes.RoutesBookMainFragment;
import com.example.bybike.setting.SettingMainActivity;
import com.example.bybike.user.UserMainPageFragment;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class NewMainActivity extends AbActivity {

	private SlidingMenu menu;
	private Fragment currentFragment;

	private AbTaskQueue mAbTaskQueue = null;
	private boolean exit = false;
	AbTaskItem item1 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.sliding_menu_content);
		getTitleBar().setVisibility(View.GONE);
		currentFragment = new MainPageFragment2();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, currentFragment).commit();
		// 主视图的Fragment添加
		/**
		 * 如果是第一次启动应用，则显示登录页面，提示用户注册登陆，否则直接显示骑行页面
		 */
		if (!SharedPreferencesUtil.getSharedPreferences_b(this,
				Constant.FIRSTUSED)) {
		} 

		initSteps();

	}

	private void initSteps() {

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
		getMenuInflater().inflate(R.menu.main, menu);
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
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}

	public void changeMainFragment(int i) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		switch (i) {
		case 0:
			// 显示我的页面
			if (!(currentFragment instanceof UserMainPageFragment)) {
				
			    UserMainPageFragment toFragment = null;
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment f : fragments) {
					if (f instanceof UserMainPageFragment) {
						toFragment = (UserMainPageFragment) f;
						transaction.show(toFragment);
					}else if(f instanceof RidingFragment){
						transaction.remove(f);
					}else if(f == null){
						continue;
					}else{
						transaction.hide(f);
					}
				}			
				if (null == toFragment) {
					toFragment = new UserMainPageFragment();
					transaction.add(R.id.content_frame, toFragment);
					transaction.show(toFragment);
				}
				
				transaction.commit();
				currentFragment = toFragment;
			}
//			Intent intent = new Intent();
//			intent.setClass(this, SettingMainActivity.class);
//			startActivity(intent);
			break;
		case 1:
			// 显示地图页面
			if (!(currentFragment instanceof MainPageFragment2)) {

				MainPageFragment2 toMainPageFragment = null;
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment f : fragments) {

					if (f instanceof MainPageFragment2) {
						toMainPageFragment = (MainPageFragment2) f;
						transaction.show(toMainPageFragment);
					}else if(f instanceof RidingFragment){
						transaction.remove(f);
					}else if(f == null){
						continue;
					}else{
						transaction.hide(f);
					}
				}
				if (null == toMainPageFragment) {
					toMainPageFragment = new MainPageFragment2();
					transaction.add(R.id.content_frame, toMainPageFragment);
					transaction.show(toMainPageFragment);
				}

				transaction.commit();
				toMainPageFragment.showTitleBar();
				// 如果前面的Fragment是LoginFragment或者RegisterFragment，则删除
//				for (Fragment f : fragments) {
//					if (f instanceof LoginFragment
//							|| f instanceof RegisterFragment) {
//						getSupportFragmentManager().beginTransaction()
//								.remove(f).commit();
//					}
//				}
				currentFragment = toMainPageFragment;
			}
			break;
		case 2:
			//显示活动列表页面
			if (!(currentFragment instanceof ExerciseListFragment)) {

				ExerciseListFragment toExerciseListFragment = null;
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment f : fragments) {
					if (f instanceof ExerciseListFragment) {
						toExerciseListFragment = (ExerciseListFragment) f;
						transaction.show(toExerciseListFragment);
					}else if(f instanceof RidingFragment){
						transaction.remove(f);
					}else if(f == null){
						continue;
					}else{
						transaction.hide(f);
					}
				}
				if (null == toExerciseListFragment) {
					toExerciseListFragment = new ExerciseListFragment();
					transaction.add(R.id.content_frame, toExerciseListFragment);
					transaction.show(toExerciseListFragment);
				}
				transaction.commit();

				toExerciseListFragment.showTitleBar();
				currentFragment = toExerciseListFragment;
			}

			break;
			
		case 3:
			//显示骑行页面
			if (!(currentFragment instanceof RidingFragment)) {

				RidingFragment toFragment = null;
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment f : fragments) {
					if (f instanceof RidingFragment) {
						toFragment = (RidingFragment) f;
					}else if(f == null){
						continue;
					}else{
						transaction.hide(f);
					}
				}
				transaction.show(currentFragment);
				if(toFragment != null){
					transaction.show(toFragment);
				}else{
					toFragment = new RidingFragment();
					transaction.add(R.id.content_frame, toFragment);
					transaction.show(toFragment);
				}
				transaction.commit();
				toFragment.showTitleBar();
				currentFragment = toFragment;
			}

			break;
		case 4:
			//显示路数页面
			if (!(currentFragment instanceof RoutesBookMainFragment)) {

				RoutesBookMainFragment toFragment = null;
				List<Fragment> fragments = getSupportFragmentManager()
						.getFragments();
				for (Fragment f : fragments) {
					if (f instanceof RoutesBookMainFragment) {
						toFragment = (RoutesBookMainFragment) f;
					}else if(f == null){
						continue;
					}else{
						transaction.hide(f);
					}
				}
				if(toFragment != null){
					transaction.show(toFragment);
				}else{
					toFragment = new RoutesBookMainFragment();
					transaction.add(R.id.content_frame, toFragment);
					transaction.show(toFragment);
				}
				transaction.commit();
				toFragment.showTitleBar();
				currentFragment = toFragment;
			}

			break;
		default:
			break;
		}

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
			changeMainFragment(1);
			break;

		case R.id.exerciseButton:
			changeMainFragment(2);
			break;
			
		case R.id.rideButton:
			changeMainFragment(3);
			break;
			
		case R.id.routeButton:
			changeMainFragment(4);
			break;
		case R.id.myButton:
			changeMainFragment(0);
			break;
			
		default:
			break;
		}
		
	}

}
