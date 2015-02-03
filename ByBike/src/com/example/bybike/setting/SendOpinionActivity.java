/*
 * @(#) SendOpinionActivity.java
 * @Author:tangliu(mail) 2014-11-26
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.setting;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.example.bybike.R;
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

/**
 * @author tangliu(mail) 2014-11-26
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class SendOpinionActivity extends AbActivity {

	TextView opinion;
	TextView contactView;
	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_opinion);
		getTitleBar().setVisibility(View.GONE);

		opinion = (TextView) findViewById(R.id.opinionDetail);
		contactView = (TextView) findViewById(R.id.contact);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		case R.id.sendButton:
			sendOpinion();
			break;
		default:
			break;
		}
	}

	private void sendOpinion() {
		String opinionContent = opinion.getText().toString().trim();
		String contact = contactView.getText().toString().trim();

		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(SendOpinionActivity.this, 0, "温馨提示",
					"网络不可用，请设置您的网络后重试", null);
			return;
		}
		if ("".equalsIgnoreCase(opinionContent)) {
			AbDialogUtil.showAlertDialog(SendOpinionActivity.this, 0, "温馨提示",
					"意见内容不能为空", null);
			return;
		}

		String urlString = Constant.serverUrl + Constant.suggestionUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(
				SendOpinionActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("content", opinionContent);
		p.put("contact", contact);
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

				AbDialogUtil.showProgressDialog(SendOpinionActivity.this, 0,
						"正在提交，请稍后...");
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				AbDialogUtil.removeDialog(SendOpinionActivity.this);
			};

		});

	}

	/**
	 * 发送成功对话框
	 */
	private Dialog dialog;

	private void showResultDialog() {

		dialog = new Dialog(this, R.style.Theme_dialog);
		LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = l.inflate(R.layout.dialog_sendopinion_success, null);
		dialog.setContentView(view);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				SendOpinionActivity.this.finish();
				overridePendingTransition(R.anim.fragment_in,
						R.anim.fragment_out);
			}
		});
		dialog.show();
	}

	protected void processResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if ("0".equals(code)) {

				// String sessionId = responseObj.getString("jsessionid");
				// SharedPreferencesUtil.saveSharedPreferences_s(SendOpinionActivity.this,
				// Constant.SESSION, sessionId);
				showResultDialog();

			} else {

			    AbDialogUtil.showAlertDialog(SendOpinionActivity.this, 0,
	                    "温馨提示", responseObj.getString("message"), null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 AbDialogUtil.showAlertDialog(SendOpinionActivity.this, 0,
                     "温馨提示", "提交失败，请稍后重试", null);
		}

	}
}
