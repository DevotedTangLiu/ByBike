package com.example.bybike.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.R;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.Constant;

public class RegisterActivity extends AbActivity{
	
	ImageView register_background;
	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	
	EditText account;
	EditText nickname;
	EditText password;
	EditText repeatPsd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_register);
		getTitleBar().setVisibility(View.GONE);

		register_background = (ImageView) findViewById(R.id.register_background);
		register_background.setImageBitmap(BitmapUtil.decodeSampledBitmapFromResource(
				getResources(), R.drawable.register_background, 540, 960));
		
		TextView haveAccount = (TextView) findViewById(R.id.haveAccount);
		haveAccount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				RegisterActivity.this.finish();
			}
		});
		
		mAbHttpUtil = AbHttpUtil.getInstance(this);

		account = (EditText)findViewById(R.id.account);
		nickname = (EditText)findViewById(R.id.nickname);
		password = (EditText)findViewById(R.id.password);
		repeatPsd = (EditText)findViewById(R.id.repartPassword);
		
		Button registerButton = (Button)findViewById(R.id.registerButton);
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				register();
			}
		});
		
		Button exitButton = (Button)findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                RegisterActivity.this.finish();
            }
        });

	}

	/**
	 * 点击注册按钮，注册
	 */
	private void register(){
		
		String accountString = account.getText().toString().trim();
		String nicknameString = nickname.getText().toString().trim();
		String passwordString = password.getText().toString().trim();
		String repeatPwdString = repeatPsd.getText().toString().trim();
		
		if(!passwordString.equals(repeatPwdString)){
			showDialog("温馨提示", "密码不一致，请重新输入");
			return;
		}
		
		String urlString =  Constant.serverUrl + Constant.registerUrl;
		AbRequestParams params = new AbRequestParams();
	    params.put("username", nicknameString);
	    params.put("email", accountString);
	    params.put("password", passwordString);
	    params.put("channel", "1");
	    
	    mAbHttpUtil.post(urlString, params, new AbStringHttpResponseListener() {

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
				showDialog("温馨提示", "注册失败，请稍后重试");
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {

			};

		});
		
	}
	
	protected void processResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if("10000".equals(code)){
				String userId = responseObj.getString("userId");
				String username = responseObj.getString("username");
				String sessionId = responseObj.getString("sessionId");
			}else{
				showDialog("温馨提示", "注册失败，请稍后重试");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDialog("温馨提示", "注册失败，请稍后重试");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(register_background != null){
			register_background.setImageBitmap(null);
		}
		System.gc();
	}

}
