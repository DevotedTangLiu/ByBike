package com.example.bybike.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.R;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class AccountSettingActivity extends AbActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_setting_accountsetting);
		getTitleBar().setVisibility(View.GONE);

		
		Boolean islogined = SharedPreferencesUtil.getSharedPreferences_b(this,
				Constant.ISLOGINED);
//		if (!islogined) {
//			Intent intent = new Intent();
//			intent.setClass(AccountSettingActivity.this, LoginActivity.class);
//			startActivityForResult(intent, goToLoginActivityFlag);
//		}
		String account = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERACCOUNT);
		String nickName = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERNICKNAME);
//		TextView userAccountText = (TextView)findViewById(R.id.userAccount);
//		userAccountText.setText(account);
//		EditText newUserAccountText = (EditText)findViewById(R.id.newUserAccount);
//		newUserAccountText.setText(nickName);

	}

	private int goToLoginActivityFlag = 1;
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart(){
		super.onStart();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if (goToLoginActivityFlag == requestCode) {
			
			AccountSettingActivity.this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	boolean saved = false;
	public void onClick(View v){
	    switch (v.getId()) {
        case R.id.saveButton:
            if(!saved){
                saved = true;
                v.setBackgroundResource(R.drawable.accountsetting_save_button_success);
            }
            break;

        case R.id.goBack:
            AccountSettingActivity.this.finish();
            break;
        default:
            break;
        }
	}
	
}
