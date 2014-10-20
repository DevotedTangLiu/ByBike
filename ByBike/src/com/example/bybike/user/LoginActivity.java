package com.example.bybike.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class LoginActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	EditText accountText;
	private String account;
	EditText passwordText;
	private String password;

	ImageView login_background;
	UserBeanDao userDao = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_login);
		getTitleBar().setVisibility(View.GONE);

		login_background = (ImageView) findViewById(R.id.login_background);
		login_background.setImageBitmap(BitmapUtil.decodeSampledBitmapFromResource(
				getResources(), R.drawable.login_background, 540, 960));

		TextView havntRegister = (TextView) findViewById(R.id.havntRegister);
		havntRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
			}
		});

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login();
			}
		});

		accountText = (EditText) findViewById(R.id.loginAccount);
		passwordText = (EditText) findViewById(R.id.loginPassword);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(false);

	}

	/**
	 * 登陆方法，先获取数据，再登陆
	 */
	private void login() {

		account = accountText.getText().toString().trim();
		password = passwordText.getText().toString().trim();

		String urlString = Constant.serverUrl + Constant.loginUrl;
		AbRequestParams p = new AbRequestParams();
		p.put("email", account);
		p.put("password", password);
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
	
	/**
	 * 处理登陆返回结果
	 * @param content
	 */
	protected void processResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if("10000".equals(code)){
				String userId = responseObj.getString("userId");
				String username = responseObj.getString("username");
				String sessionId = responseObj.getString("sessionId");
				String avatarUrl = responseObj.getString("avatarUrl");
				
				userDao = new UserBeanDao(LoginActivity.this);
				//(1)获取数据库
				userDao.startWritableDatabase(false);
				//(2)执行查询
				UserBean user = (UserBean)userDao.queryOne(Integer.valueOf(userId));
				if(null == user){
					user = new UserBean();user.setUserId(userId);
					user.setUserEmail(this.account);
					user.setPassword(this.password);
					user.setUserNickName(username);
					user.setSession(sessionId);
					user.setPicUrl(avatarUrl);
					
					userDao.insert(user);
				}else{
					user.setUserId(userId);
					user.setUserEmail(this.account);
					user.setPassword(this.password);
					user.setUserNickName(username);
					user.setSession(sessionId);
					user.setPicUrl(avatarUrl);

	                userDao.update(user);
				}
				//(3)关闭数据库
				userDao.closeDatabase(false);
				
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERID, userId);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERACCOUNT, this.account);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERPASSWORD, this.password);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERNICKNAME, username);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERAVATARURL, avatarUrl);
			}else{
				showDialog("温馨提示", "注册失败，请稍后重试");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showDialog("温馨提示", "登陆失败，请稍后重试");
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(login_background!=null){
			login_background.setImageBitmap(null);
		}
		System.gc();
	}
	
	/**
	 * 监听按钮事件
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			setResult(0);
			LoginActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
