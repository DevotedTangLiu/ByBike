package com.example.bybike.exercise;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ImageListAdapter;

public class ExerciseListFragment extends Fragment {

	private NewMainActivity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullListView mAbPullListView = null;
	private ImageListAdapter myListViewAdapter = null;

	Button orderByTime;
	Button orderByLikeCounts;
	Button orderByCollectCounts;
	Button orderByDistance;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_exercise_list, null);
		mActivity.getTitleBar().setVisibility(View.GONE);

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
				R.layout.exercise_list_item, new String[] { "exercisePic",
						"exerciseTitle", "exerciseAddress", "exerciseTime",
						"exerciseUserCount", "lickCount", "talkCount",
						"collectCount" }, new int[] { R.id.exercisePic,
						R.id.exerciseTitle, R.id.exerciseRouteAddress,
						R.id.exerciseTime, R.id.userCount, R.id.likeCount,
						R.id.talkCount, R.id.collectCount });
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, ExerciseDetailActivity3.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
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

		orderByTime = (Button) view.findViewById(R.id.orderByTime);
		orderByLikeCounts = (Button) view.findViewById(R.id.orderByLikeCounts);
		orderByCollectCounts = (Button) view
				.findViewById(R.id.orderByCollectCounts);
		orderByDistance = (Button) view.findViewById(R.id.orderByDistance);
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
		}
	}

	private OnTypeButtonClickListener click = new OnTypeButtonClickListener();

	private class OnTypeButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

}
