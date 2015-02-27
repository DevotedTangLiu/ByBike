package com.example.bybike.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.pullview.AbPullToRefreshView;
import com.example.bybike.R;
import com.example.bybike.adapter.RoutesListActivityAdapter;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class RouteBookListActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	private List<Map<String, Object>> myRouteListData = null;
	private RoutesListActivityAdapter myRouteListAdapter = null;

	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_marker_list);
		getTitleBar().setVisibility(View.GONE);

		mAbHttpUtil = AbHttpUtil.getInstance(this);

		mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		mListView = (ListView) findViewById(R.id.mListView);
		// 打开关闭下拉刷新加载更多功能
		mAbPullToRefreshView.setPullRefreshEnable(false);
		mAbPullToRefreshView.setLoadMoreEnable(false);
		// ListView数据
		myRouteListData = new ArrayList<Map<String, Object>>();
		// 使用自定义的Adapter
		myRouteListAdapter = new RoutesListActivityAdapter(this,
				myRouteListData);
		mListView.setAdapter(myRouteListAdapter);
		// item被点击事件
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(RouteBookListActivity.this,
						RouteDetailActivity.class);
				i.putExtra("id",
						(String) myRouteListData.get(position).get("id"));
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});

		String dataString = getIntent().getExtras().getString("routeBooksList");
		try {
			JSONArray dataArray = new JSONArray(dataString);
			for (int i = 0; i < dataArray.length(); i++) {
				JSONObject jo = dataArray.getJSONObject(i);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", jo.getString("roadId"));
				map.put("routePic",
						"http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
				for (int index = 1; index <= 8; index++) {
					String tmpIndex = "roadContentImg" + String.valueOf(index);
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

				myRouteListData.add(map);
			}

			myRouteListAdapter.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goBack:
			RouteBookListActivity.this.finish();
			break;

		default:
			break;
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
