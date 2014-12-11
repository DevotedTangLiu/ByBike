package com.example.bybike.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.R;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class SettingMainActivity extends AbActivity implements OnClickListener {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.fragment_setting_main);
        getTitleBar().setVisibility(View.GONE);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        switch (v.getId()) {
        case R.id.accountSetting:
            i.setClass(this, AccountSettingActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            break;
        case R.id.update:
            i.setClass(this, UpdateActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            break;
        case R.id.aboutUs:
            i.setClass(this, LoginActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            break;
        case R.id.logout:
            showLogoutDialog();
            break;
        case R.id.map:
            break;
        case R.id.opinion:
            i.setClass(this, SendOpinionActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
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
            logout();
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

    private void logout() {
        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }
        String urlString = Constant.serverUrl + Constant.logoutUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(SettingMainActivity.this, Constant.SESSION);
        mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                showProgressDialog("正在登出，请稍后...");
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
            JSONObject responseObj = new JSONObject(content);
            String code = responseObj.getString("code");
            if ("0".equals(code)) {
                String sessionId = responseObj.getString("jsessionid");
                SharedPreferencesUtil.saveSharedPreferences_s(SettingMainActivity.this, Constant.SESSION, sessionId);
                SharedPreferencesUtil.saveSharedPreferences_b(SettingMainActivity.this, Constant.ISLOGINED, false);
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                this.finish();
                
            } else {
                
                showDialog("温馨提示", responseObj.getString("message"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            showDialog("温馨提示", "提交失败，请稍后重试");
        }

    }

}
