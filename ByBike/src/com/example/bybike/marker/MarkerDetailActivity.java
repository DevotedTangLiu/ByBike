package com.example.bybike.marker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
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
    private JSONObject markerData;

    // 图片下载类
    private AbImageLoader mAbImageLoader = null;
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
    
    RelativeLayout likeButton;
    RelativeLayout collectButton;

    WordWrapView view_wordwrap;

    ProgressDialog mProgressDialog;

    // private String[] markerPics = new String[5];
    private ArrayList<String> markerPics = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_marker_detail);
        getTitleBar().setVisibility(View.GONE);
        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);

        mProgressDialog = new ProgressDialog(MarkerDetailActivity.this, 5);
        // 设置点击屏幕Dialog不消失
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("正在搜索，请稍后...");

        mAbImageLoader = AbImageLoader.newInstance(MarkerDetailActivity.this);
        mAbImageLoader.setLoadingImage(R.drawable.image_loading);
        mAbImageLoader.setErrorImage(R.drawable.image_error);
        mAbImageLoader.setEmptyImage(R.drawable.image_empty);

        markerPic = (ImageView) findViewById(R.id.markerPic);
        markerIcon = (ImageView) findViewById(R.id.markerIcon);
        publicIcon = (ImageView) findViewById(R.id.publicIcon);
        markerName = (TextView) findViewById(R.id.markerName);
        phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        markerAddress = (TextView) findViewById(R.id.markerAddress);
        markerDescription = (TextView) findViewById(R.id.markerDescription);
        creater = (TextView) findViewById(R.id.creater);
        createTime = (TextView) findViewById(R.id.createTime);
        likeButton = (RelativeLayout)findViewById(R.id.likeButton);
        likeCount = (TextView) findViewById(R.id.likeCount);
        collectButton = (RelativeLayout)findViewById(R.id.collectButton);
        collectCount = (TextView) findViewById(R.id.collectCount);
        discussCount = (TextView) findViewById(R.id.discussCount);
        markerPicCount = (TextView) findViewById(R.id.markerPicCount);
        view_wordwrap = (WordWrapView) findViewById(R.id.view_wordwrap);

        markerId = getIntent().getStringExtra("id");
        markerData = new JSONObject();
        try {
            markerData.put("id", markerId);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        discussList = (ListView) findViewById(R.id.discussList);
        discussList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                ContentValues cv = discussValueList.get(position);

                Intent i = new Intent(MarkerDetailActivity.this, AddCommentActivity.class);
                i.putExtra("id", markerId);
                i.putExtra("comments", commentsString);
                i.putExtra("name", markerNameString);
                i.putExtra("receiverId", cv.getAsString("senderId"));
                i.putExtra("receiverName", cv.getAsString("userName"));
                i.putExtra("commentId", cv.getAsString("id"));
                startActivityForResult(i, 0);
                overridePendingTransition(R.anim.fragment_up, R.anim.fragment_out);

            }
        });
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
     * 查询友好点详情 queryDetail(这里用一句话描述这个方法的作用)
     */
    private void queryDetail() {
        if (!NetUtil.isConnnected(this)) {
            AbDialogUtil.showAlertDialog(MarkerDetailActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
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

                if (mProgressDialog != null) {
                    mProgressDialog.show();
                }
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            };

        });
    }

    /**
     * 点赞/取消点赞 queryDetail(这里用一句话描述这个方法的作用)
     */
    private void likeButtonClicked() {

        String urlString = Constant.serverUrl + Constant.markerLikeClicked;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
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

        }, jsession);
    }

    /**
     * 收藏/取消收藏 queryDetail(这里用一句话描述这个方法的作用)
     */
    private void collectButtonClicked() {

        String urlString = Constant.serverUrl + Constant.markerCollectClicked;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
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

        }, jsession);
    }

    /**
     * 查询友好点评论 queryComments(这里用一句话描述这个方法的作用)
     */
    private void queryComments() {

        if (!NetUtil.isConnnected(this)) {
            AbDialogUtil.showAlertDialog(MarkerDetailActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        String urlString = Constant.serverUrl + Constant.getMarkerCommentList;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
        p.put("pageNo", "1");
        p.put("pageSize", "100");
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
                if (mProgressDialog != null) {
                    mProgressDialog.show();
                }
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            };

        }, jsession);

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
                    v1.put("id", jo.getString("id"));
                    v1.put("senderId", jo.getString("senderId"));
                    v1.put("userName", jo.getString("senderName"));
                    v1.put("discussContent", jo.getString("content"));
                    if (!"null".equals(jo.getString("senderHeadUrl"))) {
                        v1.put("avater", Constant.serverUrl + jo.getString("senderHeadUrl"));
                    } else {
                        v1.put("avater", "");
                    }
                    v1.put("discussTime", jo.getString("discussTime").substring(0, 19));
                    v1.put("receiverId", jo.getString("receiverId"));
                    v1.put("receiverName", jo.getString("receiverName"));
                    discussValueList.add(v1);
                }
                discussAdapter.notifyDataSetChanged();

            } else {
                AbToastUtil.showToast(MarkerDetailActivity.this, resultObj.getString("message"));
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
                if("true".equalsIgnoreCase(markerObj.getString("likeStatus"))){
                	likeButton.setSelected(true);
                }else{
                	likeButton.setSelected(false);
                }
                likeCount.setText(markerObj.getString("likeCount"));
                if("true".equalsIgnoreCase(markerObj.getString("collectStatus"))){
                	collectButton.setSelected(true);
                }else{
                	collectButton.setSelected(false);
                }
                collectCount.setText(markerObj.getString("collectCount"));
                discussCount.setText("评论 (" + markerObj.getString("commentCount") + ")");

                JSONObject markerTypeObj = dataObj.getJSONObject("markerType");
                phoneNumber.setText(markerTypeObj.getString("phone"));
                String markerType = markerTypeObj.getString("markerType");
                if ("RantCar".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_rentbike_2);

                    String operatingType = "类型： ";
                    if("Public".equalsIgnoreCase(markerTypeObj.getString("operatingType"))){
                        operatingType += "公共单车";
                    }else if("Private".equalsIgnoreCase(markerTypeObj.getString("operatingType"))){
                        operatingType += "私人";
                    }               
                    String bikeType = "车型： " + markerTypeObj.getString("motorcycleType");
                    bikeType = bikeType.replace("Race", "公路车").replace("Mtb", "山地车").replace("Xz", "小折").replace("Df", "死飞").replace("Db", "双人单车");
                    String price = "价格： " + markerTypeObj.getString("price");
                    String phoneNumber = "电话： " + markerTypeObj.getString("phone");
                    String[] strs = new String[] { operatingType, bikeType, price, phoneNumber };
                    for (int i = 0; i < strs.length; i++) {
                        TextView textview = new TextView(this);
                        textview.setText(strs[i]);
                        view_wordwrap.addView(textview);
                    }
                } else if ("Repair".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_repair_2);
                    String operatingType = "类型： " + markerTypeObj.getString("operatingType");
                    operatingType = operatingType.replace("FixedPoint", "固定店铺").replace("TemporaryPepairing", "临时维修点");
                    String phoneNumber = "电话： " + markerTypeObj.getString("phone");
                    String[] strs = new String[] { operatingType, phoneNumber };
                    for (int i = 0; i < strs.length; i++) {
                        TextView textview = new TextView(this);
                        textview.setText(strs[i]);
                        view_wordwrap.addView(textview);
                    }

                } else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_scenery_2);
                    String canEnter = "可否进入： " + markerTypeObj.getString("allowEnter");
                    canEnter = canEnter.replace("true", "是").replace("false", "否");
                    String canPark = "是否方便停车： " + markerTypeObj.getString("allowPark");
                    canPark = canPark.replace("true", "是").replace("false", "否");
                    String phoneNumber = "电话： " + markerTypeObj.getString("phone");
                    String[] strs = new String[] { canEnter, canPark, phoneNumber };
                    for (int i = 0; i < strs.length; i++) {
                        TextView textview = new TextView(this);
                        textview.setText(strs[i]);
                        view_wordwrap.addView(textview);
                    }

                } else if ("Catering".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_meals_2);
                    String canPark = "是否方便停车： " + markerTypeObj.getString("allowPark");
                    canPark = canPark.replace("true", "是").replace("false", "否");
                    String phoneNumber = "电话： " + markerTypeObj.getString("phone");
                    String[] strs = new String[] { canPark, phoneNumber };
                    for (int i = 0; i < strs.length; i++) {
                        TextView textview = new TextView(this);
                        textview.setText(strs[i]);
                        view_wordwrap.addView(textview);
                    }

                } else if ("Washroom".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_washroom_2);
                    String canPark = "是否方便停车： " + markerTypeObj.getString("allowPark");
                    canPark = canPark.replace("true", "是").replace("false", "否");
                    TextView textview = new TextView(this);
                    textview.setText(canPark);
                    view_wordwrap.addView(textview);
                } else if ("Parking".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_parking_2);
                    String takeCare = "是否有人保管： " + markerTypeObj.getString("takeCare");
                    takeCare = takeCare.replace("true", "是").replace("false", "否");
                    String inOutDoor = "室内/室外： " + markerTypeObj.getString("operatingType");
                    inOutDoor = inOutDoor.replace("Indoor", "室内").replace("Outdoor", "室外");
                    String[] strs = new String[] { takeCare, inOutDoor };
                    for (int i = 0; i < strs.length; i++) {
                        TextView textview = new TextView(this);
                        textview.setText(strs[i]);
                        view_wordwrap.addView(textview);
                    }
                } else if ("Other".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_others_2);
                } else if ("CarShop".equalsIgnoreCase(markerType)) {
                    markerIcon.setImageResource(R.drawable.marker_icon_bikestore_2);
                    String phone = "电话： " + markerTypeObj.getString("phone");
                    TextView textview = new TextView(this);
                    textview.setText(phone);
                    view_wordwrap.addView(textview);
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
                    mAbImageLoader.display(markerPic, Constant.serverUrl + markerPics.get(0));
                }
                markerPicCount.setText(String.valueOf(markerPics.size()));

                String operatingType = markerObj.getString("isPublic");
                if ("true".equalsIgnoreCase(operatingType)) {
                    publicIcon.setVisibility(View.VISIBLE);
                } else {
                    publicIcon.setVisibility(View.INVISIBLE);
                }

                markerData.put("name", markerObj.getString("name"));
                markerData.put("isPublic", operatingType);
                markerData.put("markerType", markerType);
                markerData.put("lat", markerObj.getString("lat"));
                markerData.put("lng", markerObj.getString("lng"));
                
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
            Intent intent = getIntent();
            intent.putExtra("markerData", markerData.toString());
            setResult(RESULT_OK, intent);
            MarkerDetailActivity.this.finish();
            break;
        case R.id.likeButton:
            if (!SharedPreferencesUtil.getSharedPreferences_b(MarkerDetailActivity.this, Constant.ISLOGINED)) {

                AbDialogUtil.showAlertDialog(MarkerDetailActivity.this, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                            @Override
                            public void onPositiveClick() {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(MarkerDetailActivity.this, LoginActivity.class);
                                MarkerDetailActivity.this.startActivity(i);
                                MarkerDetailActivity.this.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                                AbDialogUtil.removeDialog(MarkerDetailActivity.this);
                            }

                            @Override
                            public void onNegativeClick() {
                                // TODO Auto-generated method stub
                                AbDialogUtil.removeDialog(MarkerDetailActivity.this);
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

                AbDialogUtil.showAlertDialog(MarkerDetailActivity.this, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                            @Override
                            public void onPositiveClick() {
                                // TODO Auto-generated method stub
                                Intent i = new Intent(MarkerDetailActivity.this, LoginActivity.class);
                                MarkerDetailActivity.this.startActivity(i);
                                MarkerDetailActivity.this.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                                AbDialogUtil.removeDialog(MarkerDetailActivity.this);
                            }

                            @Override
                            public void onNegativeClick() {
                                // TODO Auto-generated method stub
                                AbDialogUtil.removeDialog(MarkerDetailActivity.this);
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
            i.putExtra("receiverId", "");
            i.putExtra("receiverName", "");
            i.putExtra("commentId", "");
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
                            v1.put("receiverId", jo.getString("receiverId"));
                            v1.put("receiverName", jo.getString("receiverName"));

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
