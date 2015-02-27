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

import com.ab.util.AbToastUtil;
import com.example.bybike.db.dao.MessageBeanDao;
import com.example.bybike.db.model.MessageBean;
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
	private int applyNotificationID = 1001;
	private int commentNotificationID = 1002;
	private int notifyNotificationID = 1001;

	// 获取消息线程
	private MessageThread messageThread = null;

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

	MessageBeanDao MessageBeanDao = null;

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

					// 私人消息
					if (SharedPreferencesUtil.getSharedPreferences_b(
							PushMsgRealService.this, Constant.ISLOGINED)) {
						String data = getPromotMessageFromServer();
						try {
							JSONObject resultObj = new JSONObject(data);
							String code = resultObj.getString("code");
							if ("0".equals(code)) {

								PostMessage pm = new PostMessage(
										PushMsgRealService.this);

								JSONObject dataObj = resultObj
										.getJSONObject("data");
								JSONArray activityNewsArray = dataObj
										.getJSONArray("activityNewsList");
								JSONArray discussListArray = dataObj
										.getJSONArray("discussList");
								JSONArray friendApplyList = dataObj
										.getJSONArray("friendApplyList");

								MessageBeanDao = new MessageBeanDao(
										PushMsgRealService.this);
								MessageBeanDao.startWritableDatabase(false);
								// 测试服务器时每次先删除数据，免得越来越多
								// MessageBeanDao.deleteAll();
								for (int i = 0; i < discussListArray.length(); i++) {

									JSONObject message = discussListArray
											.getJSONObject(i);

									MessageBean mb = new MessageBean();
									mb.setMessageType("1");
									mb.setMessageId(message.getString("id"));
									mb.setMessageContent(message
											.getString("content"));
									mb.setSendTime(message
											.getString("discussTime"));
									mb.setSenderId(message
											.getString("senderId"));
									mb.setSenderName(message
											.getString("senderName"));
									mb.setSenderHeadUrl(message
											.getString("senderHeadUrl"));
									mb.setCommentType(message
											.getString("function"));
									mb.setFunctionId(message
											.getString("functionId"));
									MessageBeanDao.insert(mb);
								}
								/**
								 * 好友请求
								 */
								for (int i = 0; i < friendApplyList.length(); i++) {
									JSONObject message = friendApplyList
											.getJSONObject(i);

									MessageBean mb = new MessageBean();
									mb.setMessageType("0");
									mb.setMessageId(message.getString("id"));
									mb.setSenderId(message
											.getString("senderId"));
									mb.setSenderName(message
											.getString("senderName"));
									mb.setSenderHeadUrl(message
											.getString("senderHeadUrl"));
									mb.setSendTime(message
											.getString("sendTime"));
									mb.setRemarks(message.getString("remarks"));

									MessageBeanDao.insert(mb);
								}

								for (int i = 0; i < activityNewsArray.length(); i++) {
									JSONObject message = activityNewsArray
											.getJSONObject(i);

									MessageBean mb = new MessageBean();
									mb.setMessageType("2");
									mb.setMessageId(message.getString("id"));
									mb.setActivityTitle(message
											.getString("title"));
									mb.setActivityId(message
											.getString("activityId"));
									mb.setMessageContent(message
											.getString("content"));
									mb.setSendTime(message
											.getString("sendTime"));

									MessageBeanDao.insert(mb);
								}
								MessageBeanDao.closeDatabase();
								/**
								 * 评论列表
								 */
								if (discussListArray.length() > 0) {
									JSONObject pushMessage = discussListArray
											.getJSONObject(0);
									String title = pushMessage
											.getString("senderName") + "评论了你";
									String content = pushMessage
											.getString("content");
									pm.sendMessage(commentNotificationID,
											title, content, 1);
								}
								/**
								 * 活动通知
								 */
								if (activityNewsArray.length() > 0) {
									JSONObject pushMessage = activityNewsArray
											.getJSONObject(0);
									String title = pushMessage
											.getString("title");
									String content = pushMessage
											.getString("content");
									pm.sendMessage(notifyNotificationID, title,
											content, 2);
								}
								/**
								 * 好友请求
								 */
								if (friendApplyList.length() > 0) {
									JSONObject pushMessage = friendApplyList
											.getJSONObject(0);
									String title = "好友请求";
									String content = pushMessage
											.getString("senderName")
											+ "邀请您加为好友";
									pm.sendMessage(applyNotificationID, title,
											content, 0);
								}

							} else if ("3".equals(code)) {

								// AbToastUtil.showToast(PushMsgRealService.this,
								// "登陆已过期...");
								SharedPreferencesUtil.saveSharedPreferences_s(
										PushMsgRealService.this,
										Constant.SESSION, "");
								SharedPreferencesUtil.saveSharedPreferences_s(
										PushMsgRealService.this,
										Constant.USERID, "");
								SharedPreferencesUtil.saveSharedPreferences_b(
										PushMsgRealService.this,
										Constant.ISLOGINED, false);

							}

						} catch (JSONException e) {
							e.printStackTrace();
							// Log.e("JSONException1", e.toString());
						} catch (Exception e) {
							e.printStackTrace();
							// Log.e("JSONException2", e.toString());
						}

					}

					// ==============================公共消息处理==============================================
					String publicMessageStr = getPublicMessageFromServer();

					try {
						JSONObject resultObj = new JSONObject(publicMessageStr);
						String code = resultObj.getString("code");
						if ("0".equals(code)) {

							String checkTime = resultObj.getString("message");
							SharedPreferencesUtil.saveSharedPreferences_s(
									PushMsgRealService.this,
									Constant.CHECKTIME, checkTime);

							PostMessage pm = new PostMessage(
									PushMsgRealService.this);

							JSONArray activityNewsArray = resultObj
									.getJSONArray("data");

							if (activityNewsArray.length() > 0) {

								MessageBeanDao = new MessageBeanDao(
										PushMsgRealService.this);
								MessageBeanDao.startWritableDatabase(false);
								/**
								 * 通知列表
								 */
								for (int i = 0; i < activityNewsArray.length(); i++) {

									JSONObject message = activityNewsArray
											.getJSONObject(i);

									MessageBean mb = new MessageBean();
									mb.setMessageType("3");
									mb.setMessageId(message.getString("id"));
									mb.setMessageContent(message
											.getString("content"));
									mb.setSendTime(message
											.getString("sendTime"));

									// 复用
									mb.setCommentType(message
											.getString("function"));
									mb.setFunctionId(message
											.getString("functionId"));
									MessageBeanDao.insert(mb);
								}

								MessageBeanDao.closeDatabase();
								if (activityNewsArray.length() > 0) {
									JSONObject pushMessage = activityNewsArray
											.getJSONObject(0);
									String title = pushMessage
											.getString("title");
									String content = pushMessage
											.getString("content");
									pm.sendMessage(notifyNotificationID, title,
											content, 2);
								}

							}

						}

					} catch (JSONException e) {
						e.printStackTrace();
						// Log.e("JSONException1", e.toString());
					} catch (Exception e) {
						e.printStackTrace();
						// Log.e("JSONException2", e.toString());
					}

					// ====================================================================================
					// 休息10分钟 600000
					Thread.sleep(Integer.parseInt(getNoticeTime()));

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
	 * 获取私人消息
	 */
	String requestURL = Constant.serverUrl + Constant.getPrivateMessage;

	public String getPromotMessageFromServer() {
		// String url = requestURL + "?checkTime=" + System.currentTimeMillis();
		String url = requestURL;
		String jsessionId = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.SESSION);
		return javaHttpGet(url, jsessionId);
	}

	/**
	 * 获取公众消息
	 * 
	 * @return
	 */
	public String getPublicMessageFromServer() {
		// String url = requestURL + "?checkTime=" + System.currentTimeMillis();
		String url = Constant.serverUrl + Constant.getPublicMessage;
		String checkTime = SharedPreferencesUtil.getSharedPreferences_s(
				PushMsgRealService.this, Constant.CHECKTIME);
		return javaHttpPost(url, checkTime);
	}

	private String javaHttpGet(String url, String jsessionId) {
		try {
			URL pathUrl = new URL(url); // 创建一个URL对象
			HttpURLConnection urlConnect = (HttpURLConnection) pathUrl
					.openConnection(); // 打开一个HttpURLConnection连接
			urlConnect.setRequestProperty("Accept-Encoding", "gzip");
			urlConnect.setRequestProperty("User-Agent",
					"%E5%8E%A6%E9%97%A8%E8%88%AA%E7%A9%BAandroid");
			urlConnect.setRequestProperty("COOKIE", "JSESSIONID=" + jsessionId);
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

	private String javaHttpPost(String url, String checkTime) {
		try {
			URL pathUrl = new URL(url); // 创建一个URL对象
			HttpURLConnection urlConnect = (HttpURLConnection) pathUrl
					.openConnection(); // 打开一个HttpURLConnection连接
			urlConnect.setRequestProperty("Accept-Encoding", "gzip");
			urlConnect.setRequestProperty("User-Agent",
					"%E5%8E%A6%E9%97%A8%E8%88%AA%E7%A9%BAandroid");
			// urlConnect.setRequestProperty("COOKIE", "JSESSIONID=" +
			// SharedPreferencesUtil.getSharedPreferences_s(PushMsgRealService.this,
			// Constant.SESSION));
			urlConnect.setConnectTimeout(30000); // 设置连接超时时间

			urlConnect.setDoOutput(true);
			urlConnect.setRequestMethod("POST");
			String pro = "checkTime=" + checkTime;
			urlConnect.getOutputStream().write(pro.getBytes());
			urlConnect.getOutputStream().flush();
			urlConnect.getOutputStream().close();

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
