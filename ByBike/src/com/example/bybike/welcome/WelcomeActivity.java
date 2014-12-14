package com.example.bybike.welcome;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.pushnotification.PushMsgRealService;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

public class WelcomeActivity extends AbActivity {

    private AbTaskQueue mAbTaskQueue = null;
    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;

    ImageView welcome_background;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mAbTaskQueue = AbTaskQueue.getInstance();

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);

        /**
         * 启动通知服务
         */
        Intent intent = new Intent(WelcomeActivity.this, PushMsgRealService.class);
        startService(intent);

//        checkOnLine();

    }

    private void checkOnLine() {

        String urlString = Constant.serverUrl + Constant.checkIfOnLineUrl;
        urlString += ";jsessionid=";
        urlString += SharedPreferencesUtil.getSharedPreferences_s(WelcomeActivity.this, Constant.SESSION);
        mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {

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
            public void onFailure(int statusCode, String content, Throwable error) {
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
            };

        });

    }

    private boolean alreadyCheck = true;

    protected void processResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject responseObj = new JSONObject(content);
            String code = responseObj.getString("code");
            if ("0".equals(code)) {
                String sessionId = responseObj.getString("jsessionid");
                SharedPreferencesUtil.saveSharedPreferences_s(WelcomeActivity.this, Constant.SESSION, sessionId);
                SharedPreferencesUtil.saveSharedPreferences_b(WelcomeActivity.this, Constant.ISLOGINED, true);

            } else {
                SharedPreferencesUtil.saveSharedPreferences_s(WelcomeActivity.this, Constant.SESSION, "");
                SharedPreferencesUtil.saveSharedPreferences_b(WelcomeActivity.this, Constant.ISLOGINED, false);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            SharedPreferencesUtil.saveSharedPreferences_s(WelcomeActivity.this, Constant.SESSION, "");
            SharedPreferencesUtil.saveSharedPreferences_b(WelcomeActivity.this, Constant.ISLOGINED, false);
        }

        alreadyCheck = true;

    }

    @Override
    public void onStart() {

        super.onStart();
        final AbTaskItem item1 = new AbTaskItem();
        item1.listener = new AbTaskListener() {

            @Override
            public void update() {

                if (alreadyCheck) {
                    goToMainActivity();
                } else {
                    mAbTaskQueue.execute(item1);
                }
            }

            @Override
            public void get() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
            };
        };

        mAbTaskQueue.execute(item1);

    }

    private void goToMainActivity() {

        Intent intent = new Intent();
        intent.setClass(this, NewMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (welcome_background != null) {
            welcome_background.setImageBitmap(null);
        }
        System.gc();
    }
}
