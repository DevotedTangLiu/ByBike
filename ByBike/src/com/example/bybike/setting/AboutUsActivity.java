package com.example.bybike.setting;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

public class AboutUsActivity extends AbActivity {

	// http请求帮助类
	// private AbHttpUtil mAbHttpUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_aboutus);
		getTitleBar().setVisibility(View.GONE);
		
		WebView aboutUs = (WebView)findViewById(R.id.aboutUs);
		aboutUs.getSettings().setUseWideViewPort(true);
		aboutUs.getSettings().setLoadWithOverviewMode(true);
		aboutUs.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		String url = "http://bybike.itcreating.com";
		aboutUs.loadUrl(url);
		// 获取Http工具类
		// mAbHttpUtil = AbHttpUtil.getInstance(this);

	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.goBack:
			AboutUsActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
