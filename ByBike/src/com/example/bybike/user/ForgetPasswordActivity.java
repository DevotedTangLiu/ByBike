package com.example.bybike.user;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.R;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;

public class ForgetPasswordActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	EditText email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_forgetpassword);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(false);
		
		email = (EditText)findViewById(R.id.email);

	}

	public void clickHandler(View v) {
		switch (v.getId()) {
		case R.id.exitButton:
			ForgetPasswordActivity.this.finish();
			break;

		case R.id.submit:
			resetPassword();
			break;
		default:
			break;
		}
	}

	private void resetPassword() {

		String emailString = email.getText().toString().trim();
		if("".equals(emailString)){
			showDialog("温馨提示", "请输入正确的邮箱地址");
			return;
		}
		if(!emailString.contains("@")){
			showDialog("温馨提示", "请输入正确的邮箱地址");
			return;
		}
		if (!NetUtil.isConnnected(this)) {
			showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
			return;
		}

		String urlString = Constant.serverUrl + Constant.forgetPasswrodUrl;
		AbRequestParams p = new AbRequestParams();
		p.put("email", emailString);
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
				showProgressDialog("请稍后...");
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				removeProgressDialog();
			};

		});

	}

	protected void processResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject resultObj = new JSONObject(content);
			String code = resultObj.getString("code");
			if("0".equals(code)){
			    
			    showDialog("温馨提示", resultObj.getString("message"), new OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        ForgetPasswordActivity.this.finish();
                    }
                });
			}else{
			    showDialog("温馨提示", resultObj.getString("message"));
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
