package com.example.bybike.setting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbAlertDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.example.bybike.R;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;

public class UpdateActivity extends AbActivity {

    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    public ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_update);
        getTitleBar().setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(UpdateActivity.this, 5);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("正在查询，请稍后...");

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        getUpdateInfo();
    }

    /**
     * 请求服务器，获取更新信息
     */
    private void getUpdateInfo() {

        String urlString = Constant.serverUrl + Constant.getVersionUrl;
        // 绑定参数
        mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {

            // 获取数据成功会调用这里
            @Override
            public void onSuccess(int statusCode, String content) {
                processResult(content);
            };

            // 开始执行前
            @Override
            public void onStart() {
                mProgressDialog.show();
            }

            // 失败，调用
            @Override
            public void onFailure(int statusCode, String content, Throwable error) {
                mProgressDialog.dismiss();
                AbToastUtil.showToast(UpdateActivity.this, "查询失败，请稍后再试...");
            }

            // 完成后调用，失败，成功
            @Override
            public void onFinish() {
                mProgressDialog.dismiss();
            };

        });
    }

    /**
     * 处理服务器返回信息
     * 
     * @param content
     */
    protected void processResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject resultObj = new JSONObject(content);
            String code = resultObj.getString("code");
            if("0".equals(code)){
                
                JSONObject dataObj = resultObj.getJSONObject("data");
                String versionCode = dataObj.getString("androidVersion");
                int verCode = getVerCode(UpdateActivity.this);
                if(verCode < Integer.valueOf(versionCode)){
                    
                    final String downLoadUrl = dataObj.getString("androidUrl");
                    
                    AbDialogUtil.showAlertDialog(UpdateActivity.this, 0, "温馨提示", "您当前使用的不是最新版本,点击确认下载最新版本",
                            new AbAlertDialogFragment.AbDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            // TODO Auto-generated method stub
                             downFile(downLoadUrl);                      
                             AbDialogUtil.removeDialog(UpdateActivity.this);
                             AbDialogUtil.showProgressDialog(UpdateActivity.this, 0, "正在下载更新，请稍后..");
                        }
                        @Override
                        public void onNegativeClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(UpdateActivity.this);
                        }
                    });
                }else{
                    
                    AbDialogUtil.showAlertDialog(UpdateActivity.this, 0, "温馨提示", "您当前使用的已经是最新版本",
                            new AbAlertDialogFragment.AbDialogOnClickListener() {
                        @Override
                        public void onPositiveClick() {
                            // TODO Auto-generated method stub
                             AbDialogUtil.removeDialog(UpdateActivity.this);
                             UpdateActivity.this.finish();
                        }
                        @Override
                        public void onNegativeClick() {
                            // TODO Auto-generated method stub
                            AbDialogUtil.removeDialog(UpdateActivity.this);
                        }
                    });
                    
                }
                
            }else{
                AbToastUtil.showToast(UpdateActivity.this, "查询失败，请稍后重试...");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }

    private static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo("com.example.bybike", 0).versionCode;
        } catch (NameNotFoundException e) {
            Log.e("updating", e.getMessage());
        }
        return verCode;
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo("com.example.bybike", 0).versionName;
        } catch (NameNotFoundException e) {
            Log.e("updating", e.getMessage());
        }
        return verName;
    }

    private void downFile(final String url) {

        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    long length = entity.getContentLength();
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(Environment.getExternalStorageDirectory(), "ByBike.apk");
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int count = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            count += ch;
                            if (length > 0) {
                            }
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    update();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void update() {
        AbDialogUtil.removeDialog(UpdateActivity.this);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "ByBike.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    public void update2(String downloadUrl) {
        Uri uri = Uri.parse(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        switch (v.getId()) {
        case R.id.goBack:
            this.finish();
            break;
//        case R.id.updateButton:
//            String downloadUrl = "http://gdown.baidu.com/data/wisegame/4b9f5ddd9d7b1cb5/piaoliupingzi_15.apk";
//            downFile(downloadUrl);
//            AbDialogUtil.showProgressDialog(UpdateActivity.this, 0, "正在下载更新，请稍后..");
//            break;
        default:
            break;
        }

    }
}
