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
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.example.bybike.R;
import com.example.bybike.db.dao.MessageBeanDao;
import com.example.bybike.message.MessageListActivity;
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
            i.setClass(this, AboutUsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
            break;
        case R.id.logout:
            showLogoutDialog();
            break;
        case R.id.map:
        	AbToastUtil.showToast(SettingMainActivity.this, "正在建设中，敬请期待...");
//            i.setClass(this, OfflineMapActivity.class);
//            startActivity(i);
//            overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
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
            AbDialogUtil.showAlertDialog(SettingMainActivity.this, 0, "温馨提示", "网络不可用，请设置您的网络后重试", null);
            return;
        }
        String urlString = Constant.serverUrl + Constant.logoutUrl;
        String jsession = SharedPreferencesUtil.getSharedPreferences_s(SettingMainActivity.this, Constant.SESSION);
        AbRequestParams p = new AbRequestParams();
        mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {

                processResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                AbDialogUtil.showProgressDialog(SettingMainActivity.this, 0, "正在登出，请稍后...");
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                AbDialogUtil.removeDialog(SettingMainActivity.this);
            };

        }, jsession);
    }

    protected void processResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject responseObj = new JSONObject(content);
            String code = responseObj.getString("code");
            if ("0".equals(code)) {
                
                MessageBeanDao messageBeanDao = new MessageBeanDao(SettingMainActivity.this);
                messageBeanDao.startReadableDatabase();
                messageBeanDao.delete("message_type=? or message_type=? or message_type=?", new String[]{"0", "1", "2"});
                messageBeanDao.closeDatabase();
                SharedPreferencesUtil.saveSharedPreferences_s(SettingMainActivity.this, Constant.SESSION, "");
                SharedPreferencesUtil.saveSharedPreferences_b(SettingMainActivity.this, Constant.ISLOGINED, false);
                SharedPreferencesUtil.saveSharedPreferences_s(SettingMainActivity.this, Constant.USERID, "");
                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                this.finish();

            } else {

                AbDialogUtil.showAlertDialog(SettingMainActivity.this, 0, "温馨提示", responseObj.getString("message"), null);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            AbDialogUtil.showAlertDialog(SettingMainActivity.this, 0, "温馨提示", "提交失败，请稍后重试", null);
        }

    }

}
