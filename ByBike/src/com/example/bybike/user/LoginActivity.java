package com.example.bybike.user;

import java.util.List;

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
import com.ab.util.AbDialogUtil;
import com.example.bybike.R;
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;
import com.example.bybike.util.Utils;

public class LoginActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	EditText accountText;
	private String account;
	EditText passwordText;
	private String password;
	private String nickname;

	ImageView login_background;
	UserBeanDao userDao = null;

	private final int CODE_TO_REGISTER = 1;
	private final int CODE_TO_FORGETPASSWORD = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_login);
		getTitleBar().setVisibility(View.GONE);

		login_background = (ImageView) findViewById(R.id.login_background);
		login_background.setImageBitmap(BitmapUtil
				.decodeSampledBitmapFromResource(getResources(),
						R.drawable.register_background, 540, 960));

		TextView havntRegister = (TextView) findViewById(R.id.havntRegister);
		havntRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivityForResult(intent, CODE_TO_REGISTER);
			}
		});

		TextView forgetPassword = (TextView) findViewById(R.id.forgetPassword);
		forgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this,
						ForgetPasswordActivity.class);
				startActivityForResult(intent, CODE_TO_FORGETPASSWORD);
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

		Button exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LoginActivity.this.finish();
			}
		});

		accountText = (EditText) findViewById(R.id.loginAccount);
		passwordText = (EditText) findViewById(R.id.loginPassword);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);

	}

	/**
	 * 登陆方法，先获取数据，再登陆
	 */
	private void login() {

		if (!NetUtil.isConnnected(this)) {
		    AbDialogUtil.showAlertDialog(LoginActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
			return;
		}

		account = accountText.getText().toString().trim();
		password = passwordText.getText().toString().trim();

		String urlString = Constant.serverUrl + Constant.loginUrl;
		AbRequestParams p = new AbRequestParams();
		p.put("username", account);
		p.put("password", password);
		p.put("equipmentType", "0");
		p.put("equipment", Utils.getDeviceId(LoginActivity.this));
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
			    AbDialogUtil.showProgressDialog(LoginActivity.this, 0, "正在登录，请稍后...");
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
			    AbDialogUtil.removeDialog(LoginActivity.this);
			};

		});
	}

	/**
	 * 处理登陆返回结果
	 * 
	 * @param content
	 */
	protected void processResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if ("0".equals(code)) {
				String sessionId = responseObj.getString("jsessionid");
				JSONObject dataObject = responseObj.getJSONObject("data");
				nickname = dataObject.getString("name");
				account = dataObject.getString("loginName");
				String carbonCount = dataObject.getString("carbonCount");
				String totalDistance = dataObject.getString("totalDistance");
				String headPic;
				try {
					headPic = dataObject.getString("headUrl");
				} catch (JSONException e) {
					headPic = "";
				}
				String userId = dataObject.getString("id");

				userDao = new UserBeanDao(LoginActivity.this);
				// (1)获取数据库
				userDao.startWritableDatabase(false);
				// (2)执行查询
				String[] params = new String[1];
				params[0] = account;
				List<UserBean> users = userDao.queryList("user_email = ?",
						params);
				if (users != null && users.size() > 0) {
					UserBean user = users.get(0);
					user.setUserEmail(account);
					user.setPassword(password);
					user.setSession(sessionId);
					user.setUserNickName(nickname);
					user.setPicUrl(headPic);
					user.setUserId(userId);
					userDao.update(user);

				} else {
					UserBean user = new UserBean();
					user.setSession(sessionId);
					user.setUserEmail(account);
					user.setPassword(password);
					user.setUserNickName(nickname);
					user.setPicUrl(headPic);
					user.setUserId(userId);
					userDao.insert(user);
				}

				// (3)关闭数据库
				userDao.closeDatabase();

				SharedPreferencesUtil.saveSharedPreferences_s(
						LoginActivity.this, Constant.SESSION, sessionId);
				SharedPreferencesUtil.saveSharedPreferences_s(this,
						Constant.USERACCOUNT, this.account);
				SharedPreferencesUtil.saveSharedPreferences_s(this,
						Constant.USERPASSWORD, this.password);
				SharedPreferencesUtil.saveSharedPreferences_s(this,
						Constant.USERNICKNAME, nickname);
				SharedPreferencesUtil.saveSharedPreferences_s(this,
						Constant.USERAVATARURL, headPic);
				SharedPreferencesUtil.saveSharedPreferences_b(this,
						Constant.ISLOGINED, true);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.USERID, userId);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.carbonCount, carbonCount);
				SharedPreferencesUtil.saveSharedPreferences_s(this, Constant.totalDistance, totalDistance);
				SharedPreferencesUtil.saveSharedPreferences_b(this, Constant.hasLogined, true);

				Intent intent = getIntent();
				setResult(RESULT_OK, intent);
				LoginActivity.this.finish();

			} else {
			    AbDialogUtil.showAlertDialog(LoginActivity.this, 0, "温馨提示", responseObj.getString("message"), null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
            AbDialogUtil.showAlertDialog(LoginActivity.this, 0, "温馨提示", "登陆失败，请稍后重试", null);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (login_background != null) {
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			if (requestCode == CODE_TO_REGISTER) {

				nickname = data.getStringExtra("nickname");
				String email = data.getStringExtra("email");
				String password = data.getStringExtra("password");

				accountText.setText(email);
				passwordText.setText(password);
				login();
			}
			break;
		default:
			break;
		}
	}

}
