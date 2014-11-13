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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Config;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout.LayoutParams;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.R;

public class UpdateActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_update);

		AbTitleBar mAbTitleBar = getTitleBar();
		mAbTitleBar.getTitleTextButton().setTextColor(Color.rgb(110, 110, 110));
		mAbTitleBar.setTitleText("检查更新");
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 150);
		mAbTitleBar.setLayoutParams(params);
		mAbTitleBar.setTitleTextBold(true);
		mAbTitleBar.setLogo(R.drawable.back_button);
		mAbTitleBar.setTitleBarBackground(R.drawable.title_bar);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(false);
		
		String downloadUrl = "http://gdown.baidu.com/data/wisegame/4b9f5ddd9d7b1cb5/piaoliupingzi_15.apk";
		downFile(downloadUrl);
	}

	/**
	 * 请求服务器，获取更新信息
	 */
	private void getUpdateInfo() {

		String versionCode = String.valueOf(getVerCode(this));
		AbRequestParams p = new AbRequestParams();
		p.put("versionCode", versionCode);
		String urlString = "";
		// 绑定参数
		mAbHttpUtil.get(urlString, p, new AbStringHttpResponseListener() {

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
	 * 处理服务器返回信息
	 * 
	 * @param content
	 */
	protected void processResult(String content) {
		// TODO Auto-generated method stub

	}

	private static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.example.bybike", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("updating", e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.example.bybike", 0).versionName;
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
                        File file = new File(  
                                Environment.getExternalStorageDirectory(),  
                                "ByBike.apk");  
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
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.setDataAndType(Uri.fromFile(new File(Environment  
                .getExternalStorageDirectory(), "ByBike.apk")),  
                "application/vnd.android.package-archive");  
        startActivity(intent);  
    }
	
	public void update2(String downloadUrl){
		Uri uri = Uri.parse(downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);	
	}
}
