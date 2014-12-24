package com.example.bybike.marker;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.exercise.ExerciseDetailActivity3;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class MarkerDetailActivity extends AbActivity {

	 // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    private String markerId = "";
    
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;
	ListView discussList;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	
	ImageView markerPic;
	ImageView markerIcon;
	TextView markerName;
	TextView phoneNumber;
	TextView markerAddress;
	TextView markerDescription;
	TextView creater;
	TextView createTime;
	TextView likeCount;
	TextView collectCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_marker_detail);
		getTitleBar().setVisibility(View.GONE);
		  // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);
        mAbImageDownloader = new AbImageDownloader(this);
        
        markerPic = (ImageView)findViewById(R.id.markerPic);
    	markerIcon = (ImageView)findViewById(R.id.markerIcon);
    	markerName = (TextView)findViewById(R.id.markerName);
    	phoneNumber = (TextView)findViewById(R.id.phoneNumber);
    	markerAddress = (TextView)findViewById(R.id.markerAddress);
    	markerDescription = (TextView)findViewById(R.id.markerDescription);
    	creater = (TextView)findViewById(R.id.creater);
    	createTime = (TextView)findViewById(R.id.createTime);
    	likeCount = (TextView)findViewById(R.id.likeCount);
    	collectCount = (TextView)findViewById(R.id.collectCount);
    	
        String detailInfoString = getIntent().getStringExtra("detailInfo");
        displayDetailInfo(detailInfoString);
		
		discussList = (ListView) findViewById(R.id.discussList);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				MarkerDetailActivity.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		// 填充评论列表
		for (int i = 0; i < 2; i++) {
			ContentValues v1 = new ContentValues();
			v1.put("userName", "ChaolotteYam");
			v1.put("discussContent", "有爱。");
			v1.put("avater",
					"http://t11.baidu.com/it/u=1610160448,1299213022&fm=56");
			v1.put("discussTime", "10.28 14:30");
			discussValueList.add(v1);
			ContentValues v2 = new ContentValues();
			v2.put("userName", "Jeronmme_1221");
			v2.put("discussContent", "今天倍儿爽！");
			v2.put("avater",
					"http://t11.baidu.com/it/u=1620038746,1252150868&fm=56");
			v2.put("discussTime", "10.28 14:48");
			discussValueList.add(v2);
		}
		discussAdapter.notifyDataSetChanged();
		
		queryDetail();

	}
	
	
	private void displayDetailInfo(String detailInfoString) {
		// TODO Auto-generated method stub
		try{
			JSONObject jo = new JSONObject(detailInfoString);
			String remarks = jo.getString("remarks");
			if(!remarks.equals("")){
				markerDescription.setText(remarks);
			}
            markerName.setText(jo.getString("name"));
            markerAddress.setText(jo.getString("address"));
            
            JSONObject markerTypeObj = jo.getJSONObject("markerTypeRelation");
			String opertingType = markerTypeObj.getString("operatingType");
			String markerType = markerTypeObj.getString("markerType");
			if("RantCar".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_rentbike_2);
			}else if("Repair".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_repair_2);
			}else if("FeatureSpot".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_scenery_2);
			}else if("Catering".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_meals_2);
			}else if("Washroom".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_washroom_2);
			}else if("Parking".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_parking_2);
			}else if("Other".equalsIgnoreCase(markerType)){
				markerIcon.setImageResource(R.drawable.marker_icon_others_2);
			}else{
				markerIcon.setImageResource(R.drawable.marker_icon_others_2);
			}
			
			String imageUrl = jo.getString("imgUrl1");
			if(!"".equals(imageUrl)){
				mAbImageDownloader.display(markerPic, Constant.serverUrl + imageUrl);
			}else{
				mAbImageDownloader.display(markerPic, "http://img0.imgtn.bdimg.com/it/u=1536848949,318514089&fm=21&gp=0.jpg");
			}
			
		}catch(Exception e){
			
		}
		
	}


	private void queryDetail() {
        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.markerDetailUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        p.put("id", markerId);
        // 绑定参数
        mAbHttpUtil.get(urlString, p, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processResult(content);
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

    private void processResult(String resultString) {
        try {
            JSONObject resultObj = new JSONObject(resultString);
            String code = resultObj.getString("code");
            if ("0".equals(code)) {
            	
            	
            	
            	
            }else {
//                showToast("查询失败，请稍后重试");
//                MarkerDetailActivity.this.finish();
            }

        }catch(JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            showToast("查询失败，请稍后重试");
//            MarkerDetailActivity.this.finish();
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

}
