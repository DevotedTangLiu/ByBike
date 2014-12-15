/*
 * @(#) PushMsgRealService.java
 * @Author:tangliu(mail) 2014-12-14
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.pushnotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.bybike.db.model.PostMessage;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

/**
 * @author tangliu(mail) 2014-12-14
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class PushMsgRealService extends Service {

	boolean flag;
	String tag = "pushservice";

	// Android设备唯一标识
	String deviceId;
	// 通知栏消息
	private int messageNotificationID = 1000;
	private Notification messageNotification = null;
	private NotificationManager messageNotificatioManager = null;

	// 获取消息线程
	private MessageThread messageThread = null;

	// 点击查看
	private Intent messageIntent = null;
	private PendingIntent messagePendingIntent = null;

	/*
	 * @param intent
	 * 
	 * @return
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(tag, "Service is Created");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// doing();
		Log.i(tag, "Service is Started");

		// 开启线程
		messageThread = new MessageThread();
		messageThread.isRunning = true;
		messageThread.start();

		return 1;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(tag, "Service is Destroyed");
	}

	/**
	 * 从服务器端获取消息
	 * 
	 */
	class MessageThread extends Thread {

		// 运行状态，下一步骤有大用
		public boolean isRunning = true;

		public void run() {
			while (isRunning) {
				try {
					// 休息10分钟 600000
					Thread.sleep(Integer.parseInt(getNoticeTime()));
					String data = getPromotMessageFromServer();
					// long id;
					try {
						JSONObject resultObj = new JSONObject(data);
						String code = resultObj.getString("code");
						if ("0".equals(code)) {
							
//							String sessionId = resultObj.getString("jsessionid");
//							SharedPreferencesUtil.saveSharedPreferences_s(PushMsgRealService.this, Constant.SESSION, sessionId);

							JSONArray messageArray = resultObj.getJSONArray("data");
							for (int i = 0; i < messageArray.length(); i++) {
								JSONObject message = messageArray.getJSONObject(i);
								String title = message.getString("title");
								String content = message.getString("content");
								String id = message.getString("id");

								PostMessage pm = new PostMessage(PushMsgRealService.this);
								pm.sendMessage(messageNotificationID, title, content);
							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
						// Log.e("JSONException1", e.toString());
					} catch (Exception e) {
						e.printStackTrace();
						// Log.e("JSONException2", e.toString());
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
					// Log.e("JSONException3", e.toString());
				}
			}
		}
	}

	private String noticeTime = "";

	private String getNoticeTime() {
		noticeTime = String.valueOf(Constant.NOTIFICATION_PUSH_INTERVAL);
		return noticeTime;
	}

	/**
	 * 获取优惠信息
	 */
	String requestURL = Constant.serverUrl + Constant.pushUrl;

	public String getPromotMessageFromServer() {
		String url = requestURL + "?checkTime=" + System.currentTimeMillis();
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
