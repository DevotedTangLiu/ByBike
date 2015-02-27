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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.example.bybike.SearchActivity;
import com.example.bybike.adapter.ImageListAdapter;
import com.example.bybike.routes.RoutesBookMainFragment.OnButtonItemClickListener;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class ExerciseListFragment extends Fragment implements OnHeaderRefreshListener, OnFooterLoadListener {

    private NewMainActivity mActivity = null;
    private List<Map<String, Object>> list = null;
    private List<Map<String, Object>> newList = null;
    private AbPullToRefreshView mAbPullToRefreshView = null;
    private ListView mListView = null;
    private ImageListAdapter myListViewAdapter = null;

    Button orderByTime;
    Button orderByDistance;
    Button orderByArea;
    Button orderByHot;

    // PublishDateDesc 发布时间倒序
    // PopularDesc 热门度倒序
    // ActivityStartDateDesc 开始时间倒序
    // orderByDistance 距离筛选
    private String orderType = "PublishDateDesc";
    private String kilometersStart = "0";
    private String kilometersEnd = "10";
    private String areaId = "";

    LinearLayout distanceArea;
    RelativeLayout a010km;
    RelativeLayout a1020km;
    RelativeLayout a2030km;
    RelativeLayout a30km;
    
    LinearLayout areaOfGZ;
    RelativeLayout panyuqu;
    RelativeLayout tianhequ;
    RelativeLayout huangpuqu;
    RelativeLayout yuexiuqu;
    RelativeLayout haizhuqu;
    RelativeLayout liwanqu;
    RelativeLayout baiyunqu;
    RelativeLayout qitaqu;

    private int pageNo = 1;
    private int pageSize = 10;
    private boolean isRefreshing = false;

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_exercise_list, null);
        mActivity.getTitleBar().setVisibility(View.GONE);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);

        // 获取ListView对象
        mAbPullToRefreshView = (AbPullToRefreshView) view.findViewById(R.id.mPullRefreshView);
        mListView = (ListView) view.findViewById(R.id.mListView);

        // 添加header
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
        
        areaOfGZ = (LinearLayout) header.findViewById(R.id.areaOfGZ);
        panyuqu = (RelativeLayout) header.findViewById(R.id.panyuqu);
        tianhequ = (RelativeLayout) header.findViewById(R.id.tianhequ);
        huangpuqu = (RelativeLayout) header.findViewById(R.id.huangpuqu);
        yuexiuqu = (RelativeLayout) header.findViewById(R.id.yuexiuqu);
        haizhuqu = (RelativeLayout) header.findViewById(R.id.haizhuqu);
        liwanqu = (RelativeLayout) header.findViewById(R.id.liwanqu);
        baiyunqu = (RelativeLayout) header.findViewById(R.id.baiyunqu);
        qitaqu = (RelativeLayout) header.findViewById(R.id.qitaqu);
        panyuqu.setOnClickListener(clickListener);
        tianhequ.setOnClickListener(clickListener);
        huangpuqu.setOnClickListener(clickListener);
        yuexiuqu.setOnClickListener(clickListener);
        haizhuqu.setOnClickListener(clickListener);
        liwanqu.setOnClickListener(clickListener);
        baiyunqu.setOnClickListener(clickListener);
        qitaqu.setOnClickListener(clickListener);

        mListView.addHeaderView(header);

        // 设置监听器
        mAbPullToRefreshView.setOnHeaderRefreshListener(this);
        mAbPullToRefreshView.setOnFooterLoadListener(this);

        // 设置进度条的样式
        mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));
        mAbPullToRefreshView.getFooterView().setFooterProgressBarDrawable(this.getResources().getDrawable(R.drawable.progress_circular));

        // ListView数据
        list = new ArrayList<Map<String, Object>>();
        // 使用自定义的Adapter
        myListViewAdapter = new ImageListAdapter(mActivity, mActivity, list, R.layout.exercise_list_item, new String[] { "exercisePic" },
                new int[] { R.id.exercisePic });
        mListView.setAdapter(myListViewAdapter);
        // item被点击事件
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent();
                i.setClass(mActivity, ExerciseDetailActivity3.class);
                i.putExtra("id", (String) list.get(position - 1).get("id"));
                startActivity(i);
                mActivity.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            }
        });
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


    boolean refreshOrLoadMore = true;

    private void queryExerciseList() {
        if (!NetUtil.isConnnected(mActivity)) {
            AbDialogUtil.showAlertDialog(mActivity, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        String urlString = Constant.serverUrl + Constant.exerciseListUrl;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);
//        urlString += ";jsessionid=";
//        urlString += SharedPreferencesUtil.getSharedPreferences_s(mActivity, Constant.SESSION);

        AbRequestParams p = new AbRequestParams();
        p.put("pageNo", String.valueOf(pageNo));
        p.put("pageSize", String.valueOf(pageSize));
        if ("PublishDateDesc".equalsIgnoreCase(orderType) || "PopularDesc".equalsIgnoreCase(orderType)) {
            p.put("orderBy", orderType);
        }else if("orderByDistance".equalsIgnoreCase(orderType)){
        	 p.put("wayLengthStart", kilometersStart);
             p.put("wayLengthEnd", kilometersEnd);
        }else if("orderByArea".equalsIgnoreCase(orderType)){
            p.put("gatheringLocationDistrictId", areaId);
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

                if(!isRefreshing){
                    mActivity.mProgressDialog.show();
                }
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {

                mActivity.mProgressDialog.dismiss();
            };

        }, jsession);
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
                    map.put("exercisePic", "http://img3.imgtn.bdimg.com/it/u=3823186829,2727347960&fm=21&gp=0.jpg");
                    String imgUrl = jo.getString("activityImgUrl");
                    if (null != imgUrl && !"".equals(imgUrl.trim())) {
                        map.put("exercisePic", Constant.serverUrl + imgUrl);
                    }
                    map.put("exerciseTitle", jo.getString("title"));
                    map.put("exerciseAddress", jo.getString("wayLine"));
                    map.put("exerciseTime", jo.getString("activityStartDate") + "-" + jo.getString("activityEndDate"));
                    map.put("likeCount", jo.getString("likeCount"));
                    map.put("talkCount", jo.getString("commentCount"));
                    map.put("collectCount", jo.getString("collectCount"));
                    map.put("relityActivityNumber", jo.getString("relityActivityNumber"));
                    map.put("likeStatus", jo.getString("likeStatus"));
                    map.put("collectStatus", jo.getString("collectStatus"));
                    if ("".equals(jo.getString("relityActivityNumber"))) {
                        map.put("relityActivityNumber", "0");
                    }
                    map.put("joinStatus", jo.getString("joinStatus"));
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
                    areaOfGZ.setVisibility(View.GONE);
                }
                break;
            case R.id.orderByTime:
                if (!"PublishDateDesc".equalsIgnoreCase(orderType)) {
                    changeBackground();
                    v.setSelected(true);
                    distanceArea.setVisibility(View.GONE);
                    areaOfGZ.setVisibility(View.GONE);
                    orderType = "PublishDateDesc";
                    pageNo = 1;
                    isRefreshing = false;
                    queryExerciseList();
                }
                break;
            case R.id.orderByArea:
                if(!"orderByArea".equalsIgnoreCase(orderType)){
                    orderType = "orderByArea";
                    isRefreshing = false;
                    changeBackground();
                    v.setSelected(true);
                    areaOfGZ.setVisibility(View.VISIBLE);
                    distanceArea.setVisibility(View.GONE);
                }
                break;
            case R.id.orderByHot:
                if (!"PopularDesc".equalsIgnoreCase(orderType)) {
                    changeBackground();
                    v.setSelected(true);
                    distanceArea.setVisibility(View.GONE);
                    areaOfGZ.setVisibility(View.GONE);
                    orderType = "PopularDesc";
                    pageNo = 1;
                    isRefreshing = false;
                    queryExerciseList();
                }
                break;
            case R.id.a010km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "0";
                    kilometersEnd = "10";
                    queryExerciseList();
                }
                break;
            case R.id.a1020km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "10";
                    kilometersEnd = "20";
                    queryExerciseList();
                }
                break;
            case R.id.a2030km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "20";
                    kilometersEnd = "30";
                    queryExerciseList();
                }
                break;
            case R.id.a30km:
                if (!v.isSelected()) {
                    changeDisBackground();
                    v.setSelected(true);
                    kilometersStart = "30";
                    kilometersEnd = "";
                    queryExerciseList();
                }
                break;
            case R.id.panyuqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "a8bb1e1dfbaf4ba38a94b9807ac0196e";
                    queryExerciseList();
                }
                break;
            case R.id.tianhequ:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "92844c0656b84d9f9be9528563fc9566";
                    queryExerciseList();
                }
                break;
            case R.id.huangpuqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "21a11560ab6d419dac6bbe44fbb670f4";
                    queryExerciseList();
                }
                break;
            case R.id.yuexiuqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "bff8f47a73e8497caaa61504e24e2e21";
                    queryExerciseList();
                }
                break;
            case R.id.haizhuqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "f9806ade6b824aee8149eb014928a0ca";
                    queryExerciseList();
                }
                break;
            case R.id.liwanqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "31924e4541ea49fcb8c1aec91a1e487e";
                    queryExerciseList();
                }
                break;
            case R.id.baiyunqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "8f15487d80694bad8284b6a61c315417";
                    queryExerciseList();
                }
                break;
            case R.id.qitaqu:
                if (!v.isSelected()) {
                    changeAreaBackground();
                    v.setSelected(true);
                    areaId = "7ad3be69b96e420b8d33c6b1ae660476";
                    queryExerciseList();
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
    
    private void changeAreaBackground(){
        panyuqu.setSelected(false);
        tianhequ.setSelected(false);
        huangpuqu.setSelected(false);
        yuexiuqu.setSelected(false);
        haizhuqu.setSelected(false);
        liwanqu.setSelected(false);
        baiyunqu.setSelected(false);
        qitaqu.setSelected(false);
    }

}
