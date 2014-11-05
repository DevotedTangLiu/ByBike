package com.example.bybike.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.RoutesBookListAdapter;

public class RoutesBookMainFragment extends Fragment {

	private NewMainActivity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private RoutesBookListAdapter myListViewAdapter = null;
	
	Button orderByTime;
	Button orderByDistance;
	Button orderByArea;
	Button orderByHot;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_routes_list, null);

		AbTitleBar mAbTitleBar = mActivity.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);

		// 获取ListView对象
		mAbPullListView = (AbPullListView) view.findViewById(R.id.mListView);

		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(true);

		// 设置进度条的样式
		mAbPullListView.getHeaderView().setHeaderProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));
		mAbPullListView.getFooterView().setFooterProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));

		// ListView数据
		list = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("avatorPic",
					"http://t12.baidu.com/it/u=3697398959,3070188371&fm=56");
			map.put("routePic",
					"http://img2.imgtn.bdimg.com/it/u=3104192451,2031802851&fm=21&gp=0.jpg");
			list.add(map);
		}
		// 使用自定义的Adapter
		myListViewAdapter = new RoutesBookListAdapter(mActivity, list);
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, RouteDetailActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			}
		});

		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
			}

			@Override
			public void onLoadMore() {
			}

		});

		orderByTime = (Button)view.findViewById(R.id.orderByTime);
		orderByDistance = (Button)view.findViewById(R.id.orderByDistance);
		orderByArea = (Button)view.findViewById(R.id.orderByArea);
		orderByHot = (Button)view.findViewById(R.id.orderByHot);
		orderByTime.setSelected(true);
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
	}

	public void showTitleBar() {
		// TODO Auto-generated method stub
		if (mActivity != null) {
			// mActivity.getTitleBar().setVisibility(View.GONE);
		}
	}

}
