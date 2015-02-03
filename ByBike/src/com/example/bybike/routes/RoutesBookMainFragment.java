package com.example.bybike.routes;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ab.fragment.AbDialogFragment.AbDialogOnLoadListener;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.RoutesBookListAdapter;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class RoutesBookMainFragment extends Fragment implements OnHeaderRefreshListener, OnFooterLoadListener {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

    private NewMainActivity mActivity = null;
    private List<Map<String, Object>> list = null;
    private List<Map<String, Object>> newList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private RoutesBookListAdapter myListViewAdapter = null;

    Button orderByTime;
    Button orderByDistance;
    Button orderByArea;
    Button orderByHot;

    // createDateDesc,PopularDesc,orderByDistance
    private String orderType = "createDateDesc";
    private String kilometersStart = "0";
    private String kilometersEnd = "10";

    LinearLayout distanceArea;
    RelativeLayout a010km;
    RelativeLayout a1020km;
    RelativeLayout a2030km;
    RelativeLayout a30km;

    private int pageNo = 1;
    private int pageSize = 10;
    private boolean isRefreshing = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routes_list, null);

        AbTitleBar mAbTitleBar = mActivity.getTitleBar();
        mAbTitleBar.setVisibility(View.GONE);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);

        // 获取ListView对象
        mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);
        mListView = (ListView) view.findViewById(R.id.mListView);

        // 设置监听器
        mAbPullToRefreshView.setOnHeaderRefreshListener(this);
        mAbPullToRefreshView.setOnFooterLoadListener(this);

        // 添加header
        View header = mActivity.mInflater.inflate(R.layout.route_list_header, null);
        /**
         * header处理
         */
        distanceArea = (LinearLayout) header.findViewById(R.id.distanceArea);
        a010km = (RelativeLayout) header.findViewById(R.id.a010km);
        a1020km = (RelativeLayout) header.findViewById(R.id.a1020km);
        a2030km = (RelativeLayout) header.findViewById(R.id.a2030km);
        a30km = (RelativeLayout) header.findViewById(R.id.a30km);
        // a010km.setSelected(true);
        a010km.setOnClickListener(clickListener);
        a1020km.setOnClickListener(clickListener);
        a2030km.setOnClickListener(clickListener);
        a30km.setOnClickListener(clickListener);

        orderByTime = (Button) header.findViewById(R.id.orderByTime);
        orderByDistance = (Button) header.findViewById(R.id.orderByDistance);
        orderByArea = (Button) header.findViewById(R.id.orderByArea);
        orderByHot = (Button) header.findViewById(R.id.orderByHot);
        orderByTime.setSelected(true);

        orderByTime.setOnClickListener(clickListener);
        orderByDistance.setOnClickListener(clickListener);
        orderByArea.setOnClickListener(clickListener);
        orderByHot.setOnClickListener(clickListener);

        mListView.addHeaderView(header);

        // 设置进度条的样式
        mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));

        // ListView数据
        list = new ArrayList<Map<String, Object>>();
        // 使用自定义的Adapter
        myListViewAdapter = new RoutesBookListAdapter(mActivity, list);
        mListView.setAdapter(myListViewAdapter);
        // item被点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(mActivity, RouteDetailActivity.class);
                intent.putExtra("id", (String) list.get(position - 1).get("id"));
                startActivity(intent);
                mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            }

        });

        queryRouteBookList();
        return view;
    }

    boolean refreshOrLoadMore = true;

    private void queryRouteBookList() {
    	
        if (!NetUtil.isConnnected(mActivity)) {
            AbDialogUtil.showAlertDialog(mActivity, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        
        String urlString = Constant.serverUrl + Constant.routeListUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("pageNo", String.valueOf(pageNo));
        p.put("pageSize", String.valueOf(pageSize));
        if (!"orderByDistance".equalsIgnoreCase(orderType)) {
            p.put("orderBy", orderType);
        } else {
            p.put("kilometersStart", kilometersStart);
            p.put("kilometersEnd", kilometersEnd);
        }
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
                if (!isRefreshing) {
                	mActivity.showProgressDialog("正在查询，请稍后...");
                }
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
//            	mActivity.removeProgressDialog();
            };

        });
    }

    private void processResult(String resultString) {
        try {
            JSONObject resultObj = new JSONObject(resultString);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {

                newList = new ArrayList<Map<String, Object>>();
                JSONArray dataArray = resultObj.getJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    JSONObject jo = dataArray.getJSONObject(i);

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("id", jo.getString("roadId"));
                    map.put("routePic", "http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
                    for (int index = 1; index <= 8; index++) {
                        String tmpIndex = "roadContentImg" + String.valueOf(index);
                        String imgUrl = jo.getString(tmpIndex);
                        if (null != imgUrl && !"".equals(imgUrl)) {
                            map.put("routePic", Constant.serverUrl + jo.getString("roadContentImg1"));
                            break;
                        }
                    }
                    map.put("title", jo.getString("roadTitle"));
                    map.put("routeAddress", jo.getString("roadPlace"));
                    map.put("exerciseTime", jo.getString("roadStartTime"));
                    map.put("likeCount", jo.getString("likeCount"));
                    map.put("commentCount", jo.getString("commentCount"));
                    map.put("collectCount", jo.getString("collectCount"));
                    map.put("kilometers", jo.getString("totalDistance"));
                    map.put("roadContent", jo.getString("roadContent"));

                    // map.put("userId", userObj.getString("id"));
                    map.put("userHeadPicUrl", jo.getString("roadCreatorImg"));
                    map.put("userName", jo.getString("roadCreatorName"));

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

    /**
     * 按钮点击事件
     * 
     * @author tangliu
     * 
     */
    private OnButtonItemClickListener clickListener = new OnButtonItemClickListener();

    public class OnButtonItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            switch (v.getId()) {
            case R.id.orderByDistance:
                if (!"orderByDistance".equalsIgnoreCase(orderType)) {
                    orderType = "orderByDistance";
                    changeBackground();
                    v.setSelected(true);
                    isRefreshing = false;
                    distanceArea.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.orderByTime:
                if (!"createDateDesc".equalsIgnoreCase(orderType)) {
                    changeBackground();
                    v.setSelected(true);
                    distanceArea.setVisibility(View.GONE);
                    orderType = "createDateDesc";
                    pageNo = 1;
                    isRefreshing = false;
                    queryRouteBookList();
                }
                break;
            case R.id.orderByArea:
                break;
            case R.id.orderByHot:
                if (!"PopularDesc".equalsIgnoreCase(orderType)) {
                    changeBackground();
                    v.setSelected(true);
                    distanceArea.setVisibility(View.GONE);
                    orderType = "PopularDesc";
                    pageNo = 1;
                    isRefreshing = false;
                    queryRouteBookList();
                }

                break;
            case R.id.a010km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "0";
                    kilometersEnd = "10";
                    queryRouteBookList();
                }
                break;
            case R.id.a1020km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "10";
                    kilometersEnd = "20";
                    queryRouteBookList();
                }
                break;
            case R.id.a2030km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "20";
                    kilometersEnd = "30";
                    queryRouteBookList();
                }
                break;
            case R.id.a30km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "30";
                    kilometersEnd = "";
                    queryRouteBookList();
                }
                break;
            default:
                break;
            }

        }

    }

    private void changeBackground() {

        orderByTime.setSelected(false);
        orderByDistance.setSelected(false);
        orderByArea.setSelected(false);
        orderByHot.setSelected(false);
    }

    private void changeDisBackground() {
        a010km.setSelected(false);
        a1020km.setSelected(false);
        a2030km.setSelected(false);
        a30km.setSelected(false);
    }

    @Override
    public void onFooterLoad(AbPullToRefreshView view) {
        // TODO Auto-generated method stub
        isRefreshing = true;
        pageNo++;
        queryRouteBookList();
        refreshOrLoadMore = false;
    }

    @Override
    public void onHeaderRefresh(AbPullToRefreshView view) {
        // TODO Auto-generated method stub
        isRefreshing = true;
        pageNo = 1;
        queryRouteBookList();
        refreshOrLoadMore = true;
    }

}
