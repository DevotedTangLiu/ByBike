package com.example.bybike;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.baidu.mapapi.model.LatLng;
import com.example.bybike.exercise.ExerciseListFragment;
import com.example.bybike.marker.AddMarkerActivity;
import com.example.bybike.riding.RidingActivity;
import com.example.bybike.routes.RoutesBookMainFragment;
import com.example.bybike.setting.SettingMainActivity;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.user.UserMainPageFragment;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class NewMainActivity extends AbActivity {

	// private SlidingMenu menu;
	public int currentItem = 0;
	public FragmentManager fragmentManager;
	public Fragment fragment;

	public String tag = "";
	public String currentTag = "";// 用来保存上面的tag状态

	private AbTaskQueue mAbTaskQueue = null;
	private boolean exit = false;
	AbTaskItem item1 = null;

	RelativeLayout clickRideButtonPage = null;

	RelativeLayout titleBar;
	
	public LatLng currentLatLng;
	/**
	 * 标题
	 */
	private TextView titleText ;
	private ImageView titleIcon;
	private ImageView b;
	
	private final int GO_TO_LOGIN_ACTIVITY = 10001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.sliding_menu_content);
		getTitleBar().setVisibility(View.GONE);

		clickRideButtonPage = (RelativeLayout) findViewById(R.id.cover);
		titleText = (TextView)findViewById(R.id.titleText);
		titleIcon = (ImageView)findViewById(R.id.titleIcon);
		titleBar = (RelativeLayout) findViewById(R.id.titleBar);
		b = (ImageView)findViewById(R.id.rideButton);
		// 主视图的Fragment添加
		fragmentManager = getSupportFragmentManager();
		initSteps();
	}

	private void initSteps() {

		// 初始化导航栏图标
		changeBackground(1);
		changeMainFragment(R.id.r1);

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
		// getMenuInflater().inflate(R.menu.main, menu);
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
	    //先关闭骑行页面
	    b.setSelected(false);
        clickRideButtonPage.setVisibility(View.GONE);
		// 如果当前页面和点击的页面是同一个，则跳出
		if (toItem == currentItem)
			return;

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		Fragment currentFragment = fragmentManager
				.findFragmentByTag(currentTag);
		if (currentFragment != null) {
			// 将当前的Frament隐藏到后台去
			transaction.hide(currentFragment);
		}
		switch (toItem) {
		case R.id.r5:
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

		case R.id.r1:
			// 显示地图页面
			tag = MainPageFragment.class.getSimpleName();
			// 显示我的页面
			if (fragmentManager.findFragmentByTag(tag) != null) {
				fragment = (MainPageFragment) fragmentManager
						.findFragmentByTag(tag);
			} else {
				fragment = new MainPageFragment();
			}
			titleBar.setVisibility(View.VISIBLE);
			break;
		case R.id.r2:
			// 显示活动列表页面
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
		case R.id.r4:
			// 显示路书页面
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
		transaction.commitAllowingStateLoss();
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
		case R.id.r1:
			changeBackground(1);
			changeMainFragment(view.getId());
			break;

		case R.id.r2:
			changeBackground(2);
			changeMainFragment(view.getId());
			break;

		case R.id.r3:
		    showRiding();
			break;

		case R.id.r4:
			changeBackground(4);
			changeMainFragment(view.getId());
			break;
		case R.id.r5:
			/**
			 * 如果点击“我的”按钮，想进入“我的页面”,则需要先判断用户是否已经登录
			 * 如果已登录，则直接进入，否则先进入登陆页面登陆后再进入我的页面
			 */

			if(SharedPreferencesUtil.getSharedPreferences_b(this, Constant.ISLOGINED)){
				changeBackground(5);
				changeMainFragment(view.getId());
			}else{
				Intent i = new Intent();
				i.setClass(NewMainActivity.this, LoginActivity.class);
				startActivityForResult(i, GO_TO_LOGIN_ACTIVITY);
			}
			break;
		case R.id.search:
			Intent i = new Intent();
			i.setClass(NewMainActivity.this, SearchActivity.class);
			startActivity(i);
			// overridePendingTransition(R.anim.fragment_in, 0);
			break;
		case R.id.cover:
		    showRiding();
            break;
		case R.id.goToAddMarker:
			Intent goToAddMarkerIntent = new Intent();
			goToAddMarkerIntent.setClass(NewMainActivity.this,
					AddMarkerActivity.class);
			startActivity(goToAddMarkerIntent);
			break;
		case R.id.goToRide:
			Intent goToRidingIntent = new Intent();
			goToRidingIntent.setClass(NewMainActivity.this,
					RidingActivity.class);
			goToRidingIntent.putExtra("latitude", currentLatLng.latitude);
			goToRidingIntent.putExtra("longitude", currentLatLng.longitude);
			startActivity(goToRidingIntent);
			overridePendingTransition(R.anim.fragment_in,
					R.anim.fragment_out);
			break;
		case R.id.goToMessage:
			createSystemNoticeMessage();
			break;
		default:
			break;
		}

	}

	int[] ids = new int[] { R.id.r1, R.id.r2, R.id.r3, R.id.r4, R.id.r5 };
	int[] buttonIds = new int[] { R.id.mapButton, R.id.exerciseButton,
			R.id.rideButton, R.id.routeButton, R.id.myButton };
	int[] texts = new int[] { R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5 };

	/**
	 * 点击骑行按钮，特殊处理
	  * showRiding(这里用一句话描述这个方法的作用)
	 */
	private void showRiding(){
	    
	    if(b.isSelected()){
	        b.setSelected(false);
	        clickRideButtonPage.setVisibility(View.GONE);
	    }else{
	        b.setSelected(true);
	        clickRideButtonPage.setVisibility(View.VISIBLE);
	    }
	    
	}
	/**
	 * 修改导航栏图标和背景 changeBackground(这里用一句话描述这个方法的作用)
	 * 
	 * @param i
	 */
	private void changeBackground(int i) {

		for (int j = 1; j <= 5; j++) {

			RelativeLayout rl = (RelativeLayout) findViewById(ids[j - 1]);
			ImageView b = (ImageView) findViewById(buttonIds[j - 1]);
			TextView t = (TextView) findViewById(texts[j - 1]);
			if (j == i) {
				rl.setBackgroundResource(R.drawable.bottom_block_sec);
				b.setSelected(true);
				t.setTextColor(Color.rgb(100, 100, 100));
			} else {
				rl.setBackgroundDrawable(null);
				b.setSelected(false);
				t.setTextColor(Color.rgb(180, 180, 180));
			}
		}
		
		if(i == 1){
		    titleText.setVisibility(View.GONE);
		    titleIcon.setVisibility(View.VISIBLE);
		}else if(i == 2){
		    titleText.setVisibility(View.VISIBLE);
		    titleText.setText("活动");
            titleIcon.setVisibility(View.INVISIBLE);
		}else if(i == 3){
		    titleText.setVisibility(View.VISIBLE);
            titleText.setText("骑行");
            titleIcon.setVisibility(View.INVISIBLE);
        }else if(i == 4){
            titleText.setVisibility(View.VISIBLE);
            titleText.setText("路书");
            titleIcon.setVisibility(View.INVISIBLE);
        }else{
            return;
        }
	}

	/**
	 * 生成系统通知栏消息的例子
	 */
	private void createSystemNoticeMessage() {
		// 获取系统通知服务引用
		String ns = this.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		// 自定义下拉视图
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "系统通知demo...";

		RemoteViews contentView = new RemoteViews(getPackageName(),
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
		Intent intent = new Intent(this, SettingMainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.contentIntent = contentIntent;

		mNotificationManager.notify(3, notification);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			
			switch (requestCode) {
			case 5:
				changeBackground(1);
				changeMainFragment(R.id.r1);
				break;

			case GO_TO_LOGIN_ACTIVITY:
				if(SharedPreferencesUtil.getSharedPreferences_b(NewMainActivity.this, Constant.ISLOGINED)){
					changeBackground(5);
					changeMainFragment(R.id.r5);
				}
				break;
			default:
				break;
			}
			
		}

	}
}
