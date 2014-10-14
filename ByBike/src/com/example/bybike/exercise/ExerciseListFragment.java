package com.example.bybike.exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ImageListAdapter;

public class ExerciseListFragment extends Fragment {

	private NewMainActivity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private ImageListAdapter myListViewAdapter = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_exercise_list, null);

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
			map.put("exercisePic",
					"http://img5.imgtn.bdimg.com/it/u=1530701415,1979691644&fm=21&gp=0.jpg");
			list.add(map);
		}
		// 使用自定义的Adapter
		myListViewAdapter = new ImageListAdapter(mActivity, mActivity, list,
				R.layout.exercise_list_item, new String[] { "exercisePic", },
				new int[] { R.id.exercisePic });
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				// refreshOrLoadMore = true;
				// currentPage = 1;
				// queryShopList(ShopListActivity.this.getString(shopType),
				// currentPage, pageSize);
			}

			@Override
			public void onLoadMore() {
				// refreshOrLoadMore = false;
				// currentPage++;
				// queryShopList(ShopListActivity.this.getString(shopType),
				// currentPage, pageSize);
			}

		});

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
	}

	public void showTitleBar() {
		// TODO Auto-generated method stub
		if(mActivity != null){
//			mActivity.getTitleBar().setVisibility(View.GONE);
		}
	}

}
