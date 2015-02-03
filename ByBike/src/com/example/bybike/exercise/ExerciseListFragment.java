package com.example.bybike.exercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ImageListAdapter;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class ExerciseListFragment extends Fragment implements
		OnHeaderRefreshListener, OnFooterLoadListener {

	private NewMainActivity mActivity = null;
	private List<Map<String, Object>> list = null;
	private List<Map<String, Object>> newList = null;
	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;
	private ImageListAdapter myListViewAdapter = null;

	Button orderByTime;
	Button orderByLikeCounts;
	Button orderByCollectCounts;
	Button orderByDistance;

	private int pageNo = 1;
	private int pageSize = 10;

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_exercise_list, null);
		mActivity.getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(mActivity);

		// 获取ListView对象
		mAbPullToRefreshView = (AbPullToRefreshView) view
				.findViewById(R.id.mPullRefreshView);
		mListView = (ListView) view.findViewById(R.id.mListView);

		// 添加header
		View header = mActivity.mInflater.inflate(
				R.layout.exercise_list_header, null);
		mListView.addHeaderView(header);

		// 设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);

		// 设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));

		// ListView数据
		list = new ArrayList<Map<String, Object>>();
		// 使用自定义的Adapter
		myListViewAdapter = new ImageListAdapter(mActivity, mActivity, list,
				R.layout.exercise_list_item, new String[] { "exercisePic",
						"exerciseTitle", "exerciseAddress", "exerciseTime",
						"exerciseUserCount", "lickCount", "talkCount",
						"collectCount" }, new int[] { R.id.exercisePic,
						R.id.exerciseTitle, R.id.exerciseRouteAddress,
						R.id.exerciseTime, R.id.userCount, R.id.likeCount,
						R.id.talkCount, R.id.collectCount });
		mListView.setAdapter(myListViewAdapter);
		// item被点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, ExerciseDetailActivity3.class);
				i.putExtra("id", (String) list.get(position - 1).get("id"));
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});

		orderByTime = (Button) view.findViewById(R.id.orderByTime);
		orderByLikeCounts = (Button) view.findViewById(R.id.orderByLikeCounts);
		orderByCollectCounts = (Button) view
				.findViewById(R.id.orderByCollectCounts);
		orderByDistance = (Button) view.findViewById(R.id.orderByDistance);
		orderByTime.setSelected(true);

		AbDialogUtil.showProgressDialog(mActivity, 0, "正在查询，请稍后...");
		queryExerciseList();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
	}

	private OnTypeButtonClickListener click = new OnTypeButtonClickListener();

	private class OnTypeButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}

	}

	boolean refreshOrLoadMore = true;

	private void queryExerciseList() {
		if (!NetUtil.isConnnected(mActivity)) {
			AbDialogUtil.showAlertDialog(mActivity, 0, "温馨提示",
					"网络不可用，请设置您的网络后重试", null);
			return;
		}
		String urlString = Constant.serverUrl + Constant.exerciseListUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(mActivity,
				Constant.SESSION);
		
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", String.valueOf(pageNo));
		p.put("pageSize", String.valueOf(pageSize));
		// 绑定参数
		mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {

				processResult(content);
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

		});
	}

	private void processResult(String resultString) {
	    
	    AbDialogUtil.removeDialog(mActivity);
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {

				newList = new ArrayList<Map<String, Object>>();
				JSONArray dataArray = resultObj.getJSONArray("data");
				for (int i = 0; i < dataArray.length(); i++) {
					JSONObject jo = dataArray.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();

					map.put("id", jo.getString("id"));
					map.put("exercisePic",
							"http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
					String imgUrl = jo.getString("activityImgUrl");
					if (null != imgUrl && !"".equals(imgUrl.trim())) {
						map.put("exercisePic", Constant.serverUrl + imgUrl);
					}
					map.put("exerciseTitle", jo.getString("title"));
					map.put("exerciseAddress", jo.getString("wayLine"));
					map.put("exerciseTime", jo.getString("activityStartDate")
							+ "-" + jo.getString("activityEndDate"));
					map.put("likeCount", jo.getString("likeCount"));
					map.put("talkCount", jo.getString("commentCount"));
					map.put("collectCount", jo.getString("collectCount"));

					newList.add(map);
				}

				if (refreshOrLoadMore) {
					list.clear();
				}
				if (newList != null && newList.size() > 0) {
					list.addAll(newList);
				}
				if (refreshOrLoadMore) {
					mAbPullToRefreshView.onHeaderRefreshFinish();
				} else {
					mAbPullToRefreshView.onFooterLoadFinish();
				}
				newList.clear();
				myListViewAdapter.notifyDataSetChanged();

			} else {
				AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbToastUtil.showToast(mActivity, "查询失败，请稍后重试");
		}
	}

	@Override
	public void onFooterLoad(AbPullToRefreshView view) {
		// TODO Auto-generated method stub
		 pageNo++;
		 queryExerciseList();
		 refreshOrLoadMore = false;
	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		// TODO Auto-generated method stub
		 pageNo = 1;
		 queryExerciseList();
		 refreshOrLoadMore = true;
	}

}
