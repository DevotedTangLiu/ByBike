package com.example.bybike.marker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class MarkerDetailActivity extends AbActivity {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    private String markerId = "";
    private String markerNameString = "";
    private String commentsString = "";

    // 图片下载类
    private AbImageDownloader mAbImageDownloader = null;
    ListView discussList;
    List<ContentValues> discussValueList = null;
    ExerciseDiscussListAdapter discussAdapter = null;

    ImageView markerPic;
    ImageView markerIcon;
    ImageView publicIcon;
    TextView markerName;
    TextView phoneNumber;
    TextView markerAddress;
    TextView markerDescription;
    TextView creater;
    TextView createTime;
    TextView likeCount;
    TextView collectCount;
    TextView discussCount;
    TextView markerPicCount;

    // private String[] markerPics = new String[5];
    private ArrayList<String> markerPics = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_marker_detail);
        getTitleBar().setVisibility(View.GONE);
        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);
        mAbImageDownloader = new AbImageDownloader(this);

        markerPic = (ImageView) findViewById(R.id.markerPic);
        markerIcon = (ImageView) findViewById(R.id.markerIcon);
        publicIcon = (ImageView) findViewById(R.id.publicIcon);
        markerName = (TextView) findViewById(R.id.markerName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        markerAddress = (TextView) findViewById(R.id.markerAddress);
        markerDescription = (TextView) findViewById(R.id.markerDescription);
        creater = (TextView) findViewById(R.id.creater);
        createTime = (TextView) findViewById(R.id.createTime);
        likeCount = (TextView) findViewById(R.id.likeCount);
        collectCount = (TextView) findViewById(R.id.collectCount);
        discussCount = (TextView) findViewById(R.id.discussCount);
        markerPicCount = (TextView) findViewById(R.id.markerPicCount);

        markerId = getIntent().getStringExtra("id");

        discussList = (ListView) findViewById(R.id.discussList);
        discussValueList = new ArrayList<ContentValues>();
        discussAdapter = new ExerciseDiscussListAdapter(MarkerDetailActivity.this, discussValueList);
        discussList.setAdapter(discussAdapter);

        markerPic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(MarkerDetailActivity.this, ViewPicturesActivity.class);
                i.putStringArrayListExtra("pictureUrls", markerPics);
                startActivity(i);
                overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            }
        });

        queryDetail();
        queryComments();

    }

    /**
     * 查询友好点详情
      * queryDetail(这里用一句话描述这个方法的作用)
     */
    private void queryDetail() {
        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.markerDetailUrl;
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
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
                showProgressDialog("正在查询,请稍后...");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                removeProgressDialog();
            };

        });
    }

    /**
     * 点赞/取消点赞
      * queryDetail(这里用一句话描述这个方法的作用)
     */
    private void likeButtonClicked() {

        String urlString = Constant.serverUrl + Constant.markerLikeClicked;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
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
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
            };

        });
    }
    
    /**
     * 收藏/取消收藏
      * queryDetail(这里用一句话描述这个方法的作用)
     */
    private void collectButtonClicked() {

        String urlString = Constant.serverUrl + Constant.markerCollectClicked;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
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
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
            };

        });
    }

    /**
     * 查询友好点评论    
      * queryComments(这里用一句话描述这个方法的作用)
     */
    private void queryComments() {

        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.getMarkerCommentList;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
        p.put("pageNo", "1");
        p.put("pageSize", "20");
        // 绑定参数
        mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processCommentResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                showProgressDialog("请稍后...");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                removeProgressDialog();
            };

        });

    }

    protected void processCommentResult(String resultString) {
        // TODO Auto-generated method stub
        try {
            JSONObject resultObj = new JSONObject(resultString);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {

                JSONArray dataArray = resultObj.getJSONArray("data");
                commentsString = dataArray.toString();
                discussValueList.clear();
                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject jo = dataArray.getJSONObject(i);
                    ContentValues v1 = new ContentValues();
                    v1.put("senderId", jo.getString("senderId"));
                    v1.put("userName", jo.getString("senderName"));
                    v1.put("discussContent", jo.getString("content"));
                    if (!"null".equals(jo.getString("senderHeadUrl"))) {
                        v1.put("avater", Constant.serverUrl + jo.getString("senderHeadUrl"));
                    } else {
                        v1.put("avater", "");
                    }
                    v1.put("discussTime", jo.getString("discussTime").substring(0, 19));
                    discussValueList.add(v1);
                }
                discussAdapter.notifyDataSetChanged();

            } else {
                showToast(resultObj.getString("message"));
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // showToast("评论列表加载失败，请稍后重试");
        }

    }

    private void processResult(String resultString) {
        try {
            JSONObject resultObj = new JSONObject(resultString);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {

                JSONObject dataObj = resultObj.getJSONObject("data");
                JSONObject markerObj = dataObj.getJSONObject("marker");
                String remarks = markerObj.getString("remarks");
                markerDescription.setText(remarks);
                markerName.setText(markerObj.getString("name"));
                markerNameString = markerObj.getString("name");
                markerAddress.setText(markerObj.getString("address"));
                creater.setText(markerObj.getString("creatorName"));
                createTime.setText(markerObj.getString("createDate").substring(0, 10));
                likeCount.setText(markerObj.getString("likeCount"));
                collectCount.setText(markerObj.getString("collectCount"));
                discussCount.setText("评论 (" + markerObj.getString("commentCount") + ")");

                JSONObject markerTypeObj = dataObj.getJSONObject("markerType");
                phoneNumber.setText(markerTypeObj.getString("phone"));
                String markerType = markerTypeObj.getString("markerType");
                if ("RantCar".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_rentbike_2);
                } else if ("Repair".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_repair_2);
                } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_scenery_2);
                } else if ("Catering".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_meals_2);
                } else if ("Washroom".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_washroom_2);
                } else if ("Parking".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_parking_2);
                } else if ("Other".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_others_2);
                } else if ("CarShop".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_bikestore_2);
                } else {
                    markerIcon.setImageResource(R.drawable.marker_icon_others_2);
                }

                markerPics.clear();
                for (int i = 1; i <= 5; i++) {
                    String index = "markerContentImg" + String.valueOf(i) + "Url";
                    String url = markerObj.getString(index);
                    if (null != url && !"".equals(url)) {
                        markerPics.add(url);
                    }
                }
                if (markerPics.size() > 0) {
                    mAbImageDownloader.display(markerPic, Constant.serverUrl + markerPics.get(0));
                }
                markerPicCount.setText(String.valueOf(markerPics.size()));

                String operatingType = markerTypeObj.getString("operatingType");
                if ("Public".equalsIgnoreCase(operatingType)) {
                    publicIcon.setVisibility(View.VISIBLE);
                } else {
                    publicIcon.setVisibility(View.INVISIBLE);
                }

            } else {
                // showToast("查询失败，请稍后重试");
                // MarkerDetailActivity.this.finish();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // showToast("查询失败，请稍后重试");
            // MarkerDetailActivity.this.finish();
        }
    }

    /**
     * 接受调用
     * 
     * @param source
     */
    public void clickHandler(View source) {

        switch (source.getId()) {
        case R.id.goBack:
            goBack();
            break;

        case R.id.goToMap:
            goBack();
            break;
        case R.id.likeButton:
            if (!SharedPreferencesUtil.getSharedPreferences_b(MarkerDetailActivity.this, Constant.ISLOGINED)) {

                showDialog("温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(MarkerDetailActivity.this, LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                        dialog.dismiss();
                    }
                });
                return;
            }
            if (source.isSelected()) {
                source.setSelected(false);
                int count = Integer.valueOf(likeCount.getText().toString());
                count--;
                if (count < 0)
                    count = 0;
                likeCount.setText(String.valueOf(count));
            } else {
                source.setSelected(true);
                int count = Integer.valueOf(likeCount.getText().toString());
                count++;
                likeCount.setText(String.valueOf(count));
            }
            likeButtonClicked();
            break;
        case R.id.collectButton:
            if (!SharedPreferencesUtil.getSharedPreferences_b(MarkerDetailActivity.this, Constant.ISLOGINED)) {

                showDialog("温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent i = new Intent(MarkerDetailActivity.this, LoginActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                        dialog.dismiss();
                    }
                });
                return;
            }
            if (source.isSelected()) {
                source.setSelected(false);
                int count = Integer.valueOf(collectCount.getText().toString());
                count--;
                if (count < 0)
                    count = 0;
                collectCount.setText(String.valueOf(count));
            } else {
                source.setSelected(true);
                int count = Integer.valueOf(collectCount.getText().toString());
                count++;
                collectCount.setText(String.valueOf(count));
            }
            collectButtonClicked();
            break;
        case R.id.discussButton:
            Intent i = new Intent(MarkerDetailActivity.this, AddCommentActivity.class);
            i.putExtra("id", markerId);
            i.putExtra("comments", commentsString);
            i.putExtra("name", markerNameString);
            startActivityForResult(i, 0);
            overridePendingTransition(R.anim.fragment_up, R.anim.fragment_out);
            break;
        default:
            break;
        }
    }

    /**
     * 退出页面
     */
    private void goBack() {
        MarkerDetailActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
            case 0:
                String discussList = data.getStringExtra("discussList");
                try {
                    JSONArray dataArray = new JSONArray(discussList);
                    commentsString = dataArray.toString();
                    if (dataArray.length() > discussValueList.size()) {
                        for (int i = discussValueList.size(); i < dataArray.length(); i++) {

                            JSONObject jo = dataArray.getJSONObject(i);
                            ContentValues v1 = new ContentValues();
                            v1.put("senderId", jo.getString("senderId"));
                            v1.put("userName", jo.getString("senderName"));
                            v1.put("discussContent", jo.getString("content"));
                            v1.put("avater", jo.getString("senderHeadUrl"));
                            v1.put("discussTime", jo.getString("discussTime").substring(0, 19));
                            discussValueList.add(v1);
                        }
                        discussAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;

            default:
                break;
            }

        }

    }

}
