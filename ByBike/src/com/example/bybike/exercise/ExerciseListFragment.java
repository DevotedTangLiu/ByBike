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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.listener.AbOnListViewListener;
import com.ab.view.pullview.AbPullListView;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ImageListAdapter;
import com.example.bybike.db.model.ExerciseBean;
import com.example.bybike.setting.AccountSettingActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

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
	
	 // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_exercise_list, null);
		mActivity.getTitleBar().setVisibility(View.GONE);
		
		 // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
        mAbHttpUtil.setDebug(false);

		// 获取ListView对象
		mAbPullListView = (AbPullListView) view.findViewById(R.id.mListView);
		
		// 添加header
		View header = mActivity.mInflater.inflate(R.layout.exercise_list_header, null);
		mAbPullListView.addHeaderView(header);

		// 打开关闭下拉刷新加载更多功能
		mAbPullListView.setPullRefreshEnable(true);
		mAbPullListView.setPullLoadEnable(false);

		// 设置进度条的样式
		mAbPullListView.getHeaderView().setHeaderProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));
		mAbPullListView.getFooterView().setFooterProgressBarDrawable(
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
		mAbPullListView.setAdapter(myListViewAdapter);
		// item被点击事件
		mAbPullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(mActivity, ExerciseDetailActivity3.class);
				i.putExtra("id", (String)list.get(position - 2).get("id"));
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			}
		});

		mAbPullListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
			    queryExerciseList();
			    refreshOrLoadMore = true;
			}

			@Override
			public void onLoadMore() {
			    queryExerciseList();
			    refreshOrLoadMore = false;
			}

		});

		orderByTime = (Button) view.findViewById(R.id.orderByTime);
		orderByLikeCounts = (Button) view.findViewById(R.id.orderByLikeCounts);
		orderByCollectCounts = (Button) view
				.findViewById(R.id.orderByCollectCounts);
		orderByDistance = (Button) view.findViewById(R.id.orderByDistance);
		orderByTime.setSelected(true);
		
		queryExerciseList();
		return view;
	}

	@Override
	public void onStart(){
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
	private void queryExerciseList(){
	    if (!NetUtil.isConnnected(mActivity)) {
	        mActivity.showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.exerciseListUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
        // 绑定参数
        mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {
                
                processResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                mActivity.showProgressDialog("正在查询，请稍后...");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                mActivity.removeProgressDialog();
            };

        });
	}
	
	private void processResult(String resultString){
	    try {
            JSONObject resultObj = new JSONObject(resultString);
            String code = resultObj.getString("code");
            if("0".equals(code)){
                
                list.clear();
                JSONArray dataArray = resultObj.getJSONArray("data");
                for(int i = 0; i < dataArray.length(); i ++){
                    JSONObject jo = dataArray.getJSONObject(i);
                    JSONObject ridingBookObj = jo.getJSONObject("ridingBook");
                            
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", jo.getString("id"));
                    map.put("exercisePic", Constant.serverUrl + jo.getString("img1Url"));
                    map.put("exerciseTitle", jo.getString("title"));
                    map.put("exerciseAddress", jo.getString("wayLine"));
//                    map.put("exerciseTime", jo.getString("activityDate"));
//                    map.put("lickCount", ridingBookObj.getString("likeCount"));
                    map.put("talkCount", jo.getString("commentCount"));
                    map.put("collectCount", jo.getString("collectCount"));
                    
                    list.add(map);
                }
                
                myListViewAdapter.notifyDataSetChanged();
                if(refreshOrLoadMore){
                    mAbPullListView.stopRefresh();
                }else{
                    mAbPullListView.stopLoadMore();
                }
                
            }else{
                mActivity.showToast("查询失败，请稍后重试");
            }
            
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mActivity.showToast("查询失败，请稍后重试");
        }
	}

}
