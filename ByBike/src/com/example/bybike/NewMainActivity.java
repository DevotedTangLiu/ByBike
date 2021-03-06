package com.example.bybike;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.ab.util.AbToastUtil;
import com.baidu.mapapi.model.LatLng;
import com.example.bybike.exercise.ExerciseDetailActivity3;
import com.example.bybike.exercise.ExerciseListFragment;
import com.example.bybike.marker.AddMarkerActivity;
import com.example.bybike.message.MessageListActivity;
import com.example.bybike.riding.RidingActivity;
import com.example.bybike.routes.RoutesBookMainFragment;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.user.UserMainPageFragment;
import com.example.bybike.util.AnimationController;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class NewMainActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
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
	private TextView titleText;
	private ImageView titleIcon;
	private ImageView b;

	private final int GO_TO_LOGIN_ACTIVITY = 10001;
	private final int GO_TO_SEARCHING_ACTIVITY = 10002;
	public final int GO_TO_MARKERDETAIL_ACTIVITY = 10003;
	private final int GO_TO_RIDING_ACTIVITY = 10004;
	private final int GO_TO_SETTING_MAIN_ACTIVITY = 10005;

	Button ifHasNewMessage;
	public ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.sliding_menu_content);
		getTitleBar().setVisibility(View.GONE);

		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mProgressDialog = new ProgressDialog(NewMainActivity.this, 5);
		// 设置点击屏幕Dialog不消失
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("正在查询，请稍后...");

		animationController = new AnimationController();
		clickRideButtonPage = (RelativeLayout) findViewById(R.id.cover);
		titleText = (TextView) findViewById(R.id.titleText);
		titleIcon = (ImageView) findViewById(R.id.titleIcon);
		titleBar = (RelativeLayout) findViewById(R.id.titleBar);
		b = (ImageView) findViewById(R.id.rideButton);
		ifHasNewMessage = (Button) findViewById(R.id.ifHasNewMessage);

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
		item1.setListener(new AbTaskListener() {
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
		});
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
		// 先关闭骑行页面
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
				AbToastUtil.showToast(NewMainActivity.this, "再按一次返回键退出应用");
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

			if (SharedPreferencesUtil.getSharedPreferences_b(this,
					Constant.ISLOGINED)) {
				changeBackground(5);
				changeMainFragment(view.getId());
			} else {
				Intent i = new Intent();
				i.setClass(NewMainActivity.this, LoginActivity.class);
				startActivityForResult(i, GO_TO_LOGIN_ACTIVITY);
			}
			break;
		case R.id.search:
			Intent i = new Intent();
			i.setClass(NewMainActivity.this, SearchActivity.class);
			i.putExtra("currentPage", currentItem);
			startActivityForResult(i, GO_TO_SEARCHING_ACTIVITY);
			// overridePendingTransition(R.anim.fragment_in, 0);
			break;
		case R.id.cover:
			showRiding();
			break;
		case R.id.goToAddMarker:
			Intent goToAddMarkerIntent = new Intent();
			goToAddMarkerIntent.setClass(NewMainActivity.this,
					AddMarkerActivity.class);
			// if (currentLatLng != null) {
			// goToAddMarkerIntent.putExtra("latitude", currentLatLng.latitude);
			// goToAddMarkerIntent.putExtra("longitude",
			// currentLatLng.longitude);
			// }
			startActivity(goToAddMarkerIntent);
			break;
		case R.id.goToRide:
			Intent goToRidingIntent = new Intent();
			goToRidingIntent.setClass(NewMainActivity.this,
					RidingActivity.class);
			if (currentLatLng != null) {
				goToRidingIntent.putExtra("latitude", currentLatLng.latitude);
				goToRidingIntent
						.putExtra("longtitude", currentLatLng.longitude);
			}
			startActivityForResult(goToRidingIntent, GO_TO_RIDING_ACTIVITY);
			overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			break;
		case R.id.goToMessage:
			Intent goToMessageIntent = new Intent();
			goToMessageIntent.setClass(NewMainActivity.this,
					MessageListActivity.class);
			startActivity(goToMessageIntent);
			overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
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
	 * 点击骑行按钮，特殊处理 showRiding(这里用一句话描述这个方法的作用)
	 */
	private AnimationController animationController;

	private void showRiding() {

		if (b.isSelected()) {
			b.setSelected(false);
			animationController.fadeOut(clickRideButtonPage, 300, 0);
			// clickRideButtonPage.setVisibility(View.GONE);

		} else {
			b.setSelected(true);
			// clickRideButtonPage.setVisibility(View.VISIBLE);
			animationController.fadeIn(clickRideButtonPage, 300, 0);
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

		if (i == 1) {
			titleText.setVisibility(View.GONE);
			titleIcon.setVisibility(View.VISIBLE);
		} else if (i == 2) {
			titleText.setVisibility(View.VISIBLE);
			titleText.setText("活动");
			titleIcon.setVisibility(View.INVISIBLE);
		} else if (i == 3) {
			titleText.setVisibility(View.VISIBLE);
			titleText.setText("骑行");
			titleIcon.setVisibility(View.INVISIBLE);
		} else if (i == 4) {
			titleText.setVisibility(View.VISIBLE);
			titleText.setText("路书");
			titleIcon.setVisibility(View.INVISIBLE);
		} else {
			return;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {

			switch (requestCode) {
			case GO_TO_SETTING_MAIN_ACTIVITY:
				changeBackground(1);
				changeMainFragment(R.id.r1);
				break;

			case GO_TO_LOGIN_ACTIVITY:
				if (SharedPreferencesUtil.getSharedPreferences_b(
						NewMainActivity.this, Constant.ISLOGINED)) {
					changeBackground(5);
					changeMainFragment(R.id.r5);
				}
				break;
			case GO_TO_SEARCHING_ACTIVITY:
				MainPageFragment mainFragment = (MainPageFragment) fragmentManager
						.findFragmentByTag(MainPageFragment.class
								.getSimpleName());
				if (mainFragment != null) {
					String markerData = data.getStringExtra("markerData");
					mainFragment.showMarker(markerData);
				}
				break;
			case GO_TO_MARKERDETAIL_ACTIVITY:
				MainPageFragment mainFragment2 = (MainPageFragment) fragmentManager
						.findFragmentByTag(MainPageFragment.class
								.getSimpleName());
				if (mainFragment2 != null) {
					String markerData = data.getStringExtra("markerData");
					mainFragment2.showMarker(markerData);
				}
				changeBackground(1);
				changeMainFragment(R.id.r1);
				break;
			case GO_TO_RIDING_ACTIVITY:
				changeBackground(4);
				changeMainFragment(R.id.r4);
				RoutesBookMainFragment routeFragment = (RoutesBookMainFragment)fragmentManager.findFragmentByTag(RoutesBookMainFragment.class.getSimpleName());
				if (routeFragment != null) {
					routeFragment.refresh();
				}

				break;
			default:
				break;
			}

		}

	}

	/**
	 * 
	 * onListViewButtonClicked(这里用一句话描述这个方法的作用)
	 * 
	 * @param fucntionType
	 *            0：markerList 1:exerciseList 2:routeBookList
	 * @param buttonType
	 *            0: likeButton 1:collectButton
	 * @param id
	 */
	public void onListViewButtonClicked(int fucntionType, int buttonType,
			String id) {

		String urlString = "";
		switch (fucntionType) {
		case 0:
			if (0 == buttonType) {
				urlString = Constant.serverUrl + Constant.markerLikeClicked;
			} else {
				urlString = Constant.serverUrl + Constant.markerCollectClicked;
			}
			break;

		case 1:
			if (0 == buttonType) {
				urlString = Constant.serverUrl + Constant.exerciseLikeClicked;
			} else {
				urlString = Constant.serverUrl
						+ Constant.exerciseCollectClicked;
			}
			break;
		case 2:
			if (0 == buttonType) {
				urlString = Constant.serverUrl + Constant.routeLikeClicked;
			} else {
				urlString = Constant.serverUrl + Constant.routeCollectClicked;
			}
			break;
		default:
			break;
		}

		String jsession = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
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
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
			};

		}, jsession);

	}
}
