package com.example.bybike.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

public class SettingMainActivity extends AbActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.fragment_setting_main);
		getTitleBar().setVisibility(View.GONE);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		switch (v.getId()) {
		case R.id.accountSetting:
			i.setClass(this, AccountSettingActivity.class);
			startActivity(i);
			break;
		case R.id.update:
			i.setClass(this, UpdateActivity.class);
			startActivity(i);
			break;
		case R.id.aboutUs:
			break;
		case R.id.logout:
			showLogoutDialog();
			break;
		case R.id.map:
			break;
		case R.id.opinion:
			break;
		case R.id.goBack:
			this.finish();
			break;
		case R.id.cancelButton:
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			break;
		case R.id.confirmButton:
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			Intent intent = getIntent();
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		default:
			break;
		}

	}

	private Dialog dialog;

	/**
	 * 显示退出对话框
	 */
	private void showLogoutDialog() {
		dialog = new Dialog(this, R.style.Theme_dialog);
		LayoutInflater l = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = l.inflate(R.layout.dialog_logout, null);
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
