package com.example.bybike.welcome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.db.dao.MarkerBeanDao;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.pushnotification.PushMsgRealService;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class WelcomeActivity extends AbActivity {

	private AbTaskQueue mAbTaskQueue = null;

	ImageView welcome_background;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mAbTaskQueue = AbTaskQueue.getInstance();

		MarkerListThread thread = new MarkerListThread();
		thread.start();
		/**
		 * 启动通知服务
		 */
		Intent intent = new Intent(WelcomeActivity.this,
				PushMsgRealService.class);
		startService(intent);
		
		SharedPreferencesUtil.saveSharedPreferences_b(this, Constant.hasLogined, false);
	}

	private boolean alreadyCheck = true;

	@Override
	public void onStart() {

		super.onStart();
		final AbTaskItem item1 = new AbTaskItem();
		item1.setListener(new AbTaskListener() {

			@Override
			public void update() {

				if (alreadyCheck) {
					goToMainActivity();
				} else {
					mAbTaskQueue.execute(item1);
				}
			}

			@Override
			public void get() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			};
		});

		mAbTaskQueue.execute(item1);

	}

	private void goToMainActivity() {

		Intent intent = new Intent();
		intent.setClass(this, NewMainActivity.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (welcome_background != null) {
			welcome_background.setImageBitmap(null);
		}
		System.gc();
	}

	MarkerBeanDao markerBeanDao = null;

	/**
	 * 从服务器端获取消息
	 * 
	 */
	class MarkerListThread extends Thread {

		public void run() {

			try {

				String resultString = getPromotMessageFromServer();
				JSONObject resultObject = new JSONObject(resultString);
				String code = resultObject.getString("code");
				if ("0".equals(code)) {
					JSONArray dataArray = resultObject.getJSONArray("data");
					// (1)获取数据库
					markerBeanDao = new MarkerBeanDao(WelcomeActivity.this);
					markerBeanDao.startWritableDatabase(false);
					SharedPreferencesUtil.saveSharedPreferences_b(WelcomeActivity.this, Constant.markerDataLoaded, false);
					markerBeanDao.deleteAll();
					for (int i = 0; i < dataArray.length(); i++) {

						JSONObject jo = dataArray.getJSONObject(i);

						MarkerBean mb = new MarkerBean();
						mb.setMarkerId(jo.getString("Id"));
						try{
							mb.setLatitude(jo.getDouble("lat"));
							mb.setLongitude(jo.getDouble("lng"));
						}catch(Exception e){
							e.printStackTrace();
							System.out.println(mb.getMarkerId());
							System.out.println(jo.getString("name"));
						}					
						mb.setMarkerName(jo.getString("name"));
						mb.setMarkerType(jo.getString("markerType"));
						mb.setOperatingType(jo.getString("operatingType"));
						mb.setCreaterId(jo.getString("creatorId"));
						mb.setIsPublic(jo.getString("isPublic"));
						
						//复用，将是否特殊标记点存入description，将特殊标记图片存入imgurl1
						mb.setDescription(jo.getString("particular"));
						mb.setImgurl1(jo.getString("particularImgUrl"));

						markerBeanDao.insert(mb);

					}
					markerBeanDao.closeDatabase();
					SharedPreferencesUtil.saveSharedPreferences_b(WelcomeActivity.this, Constant.markerDataLoaded, true);
				}

			} catch (Exception e) {
				e.printStackTrace();
				// Log.e("JSONException3", e.toString());
			}
		}

	}

	/**
	 * 获取优惠信息
	 */
	String requestURL = Constant.serverUrl + Constant.getMarkerListUrl;

	public String getPromotMessageFromServer() {
		
		String url = requestURL;
		url += "?pageNo=1&pageSize=1000";
		return javaHttpGet(url);
	}

	private String javaHttpGet(String url) {
		try {
			URL pathUrl = new URL(url); // 创建一个URL对象
			HttpURLConnection urlConnect = (HttpURLConnection) pathUrl
					.openConnection(); // 打开一个HttpURLConnection连接
			urlConnect.setRequestProperty("Accept-Encoding", "gzip");
			urlConnect.setRequestProperty("User-Agent",
					"%E5%8E%A6%E9%97%A8%E8%88%AA%E7%A9%BAandroid");
			urlConnect.setConnectTimeout(30000); // 设置连接超时时间
			urlConnect.connect();
			InputStreamReader in = new InputStreamReader(
					urlConnect.getInputStream()); // 得到读取的内容
			BufferedReader buffer = new BufferedReader(in); // 为输出创建BufferedReader
			String inputLine = "";
			String resultData = "";
			while (((inputLine = buffer.readLine()) != null)) {
				resultData += inputLine;
			}
			return resultData;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}
