/*
 * @(#) PushMsgService.java
 * @Author:tangliu(mail) 2014-12-14
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.pushnotification;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.db.model.PostMessage;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

/**
 * @author tangliu(mail) 2014-12-14
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class PushMsgService {

	private Context pushMsgRealService;
	boolean flag = true;

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	String requestURL;

	/**
	 * 创建一个新的实例
	 * 
	 * @param pushMsgRealService
	 */
	public PushMsgService(Context pushMsgRealService) {
		this.pushMsgRealService = pushMsgRealService;
		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this.pushMsgRealService);
		mAbHttpUtil.setDebug(false);
		requestURL = Constant.serverUrl + Constant.pushUrl;
	}

	/**
	 * contact(这里用一句话描述这个方法的作用)
	 */
	public void contact() {

		Log.v("3", Thread.currentThread().getName());
		AbRequestParams p = new AbRequestParams();
		if (flag) {
			p.put("checkTime", String.valueOf(System.currentTimeMillis()));
			mAbHttpUtil.post(requestURL, p, new AbStringHttpResponseListener() {

				// 获取数据成功会调用这里
				@Override
				public void onSuccess(int statusCode, String content) {

					processResult(content);
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

			});
		}
	}

	/**
	 * 处理返回结果 processResult(这里用一句话描述这个方法的作用)
	 * 
	 * @param resultString
	 */
	private void processResult(String resultString) {
		try {
			JSONObject resultObj = new JSONObject(resultString);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				String sessionId = resultObj.getString("jsessionid");
				SharedPreferencesUtil.saveSharedPreferences_s(
						this.pushMsgRealService, Constant.SESSION, sessionId);

				JSONArray messageArray = resultObj.getJSONArray("data");
				for (int i = 0; i < messageArray.length(); i++) {
					JSONObject message = messageArray.getJSONObject(i);
					String title = message.getString("title");
					String content = message.getString("content");
					String id = message.getString("id");

					PostMessage pm = new PostMessage(pushMsgRealService);
					pm.sendMessage(0, title, content);
				}
			}
		} catch (Exception e) {

		} finally {
			// SystemClock.sleep(Constant.NOTIFICATION_PUSH_INTERVAL);
			// flag = true;
		}
	}

	AbStringHttpResponseListener listener = new AbStringHttpResponseListener() {

		// 获取数据成功会调用这里
		@Override
		public void onSuccess(int statusCode, String content) {

			processResult(content);
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

	};

	/**
	 * turnOn(这里用一句话描述这个方法的作用)
	 */
	public void turnOn() {
		flag = true;

	}

	/**
	 * turnOff(这里用一句话描述这个方法的作用)
	 */
	public void turnOff() {
		flag = false;

	}

}
