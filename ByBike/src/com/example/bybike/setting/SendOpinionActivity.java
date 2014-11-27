/*
 * @(#) SendOpinionActivity.java
 * @Author:tangliu(mail) 2014-11-26
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

/**
 * @author tangliu(mail) 2014-11-26
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class SendOpinionActivity extends AbActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_opinion);
		getTitleBar().setVisibility(View.GONE);

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		case R.id.sendButton:
			showResultDialog();
			break;
		default:
			break;
		}
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

}
