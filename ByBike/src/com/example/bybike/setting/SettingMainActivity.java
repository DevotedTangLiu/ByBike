package com.example.bybike.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

public class SettingMainActivity extends AbActivity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.fragment_setting_main);
		
		getTitleBar().setVisibility(View.GONE);
		LinearLayout accountSetting = (LinearLayout)findViewById(R.id.accountSetting);
		LinearLayout update = (LinearLayout)findViewById(R.id.updateArea);
		accountSetting.setOnClickListener(this);
		update.setOnClickListener(this);
		
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
		case R.id.updateArea:
			i.setClass(this, UpdateActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}

	}
}
