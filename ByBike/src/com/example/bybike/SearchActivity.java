package com.example.bybike;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbToastUtil;
import com.example.bybike.marker.MarkerListActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class SearchActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	EditText searchContent;

	ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);

		mProgressDialog = new ProgressDialog(SearchActivity.this, 5);
		// 设置点击屏幕Dialog不消失
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("正在搜索，请稍后...");

		searchContent = (EditText) findViewById(R.id.searchContent);
		searchContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchContent
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEND
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							// showToast(searchContent.getText().toString());
							return true;
						}
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							searchMarkers();
							return true;
						}
						return false;
					}
				});

		int currentPage = getIntent().getIntExtra("currentPage", 0);
		switch (currentPage) {
		case 0:
			searchContent.setHint("");
			break;
		case R.id.r1:
			searchContent.setHint("友好点");
			break;
		case R.id.r2:
			searchContent.setHint("活动");
			break;
		case R.id.r4:
			searchContent.setHint("路书");
			break;
		default:
			break;
		}

	}

	/**
	 * 搜索标记点
	 */
	private void searchMarkers() {

		if (!NetUtil.isConnnected(this)) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this, 5);
			builder.setMessage("网络不可用，请设置您的网络后重试");
			builder.setTitle("温馨提示");
			builder.create();
			AlertDialog mAlertDialog = builder.create();
			mAlertDialog.setCancelable(true);
			mAlertDialog.setCanceledOnTouchOutside(true);
			mAlertDialog.show();
			return;
		}

		String name = searchContent.getText().toString().trim();
		if ("".equals(name)) {
			AbToastUtil.showToast(SearchActivity.this, "请输入搜索内容");
			return;
		}
		String urlString = Constant.serverUrl + Constant.getMarkerListUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(SearchActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "100");
		p.put("name", searchContent.getText().toString().trim());

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

				mProgressDialog.show();
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
			};

		}, jsession);

	}

	protected void processResult(String content) {
		// TODO Auto-generated method stub

		try {
			JSONObject resultObj = new JSONObject(content);
			String code = resultObj.getString("code");
			if ("0".equals(code)) {
				JSONArray dataArray = resultObj.getJSONArray("data");
				if (dataArray.length() > 0) {
					Intent i = new Intent(SearchActivity.this,
							MarkerListActivity.class);
					i.putExtra("markerList", dataArray.toString());
					startActivity(i);
					overridePendingTransition(R.anim.fragment_in,
							R.anim.fragment_out);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this,
							5);
					builder.setMessage("没有搜到相关结果，请重新输入后再试");
					builder.setTitle("温馨提示");
					builder.create();
					AlertDialog mAlertDialog = builder.create();
					mAlertDialog.setCancelable(true);
					mAlertDialog.setCanceledOnTouchOutside(true);
					mAlertDialog.show();

				}

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this, 5);
				builder.setMessage("没有搜到相关结果，请重新输入后再试");
				builder.setTitle("温馨提示");
				builder.create();
				AlertDialog mAlertDialog = builder.create();
				mAlertDialog.setCancelable(true);
				mAlertDialog.setCanceledOnTouchOutside(true);
				mAlertDialog.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void onclick(View view) {

		switch (view.getId()) {
		case R.id.spaceArea:
			SearchActivity.this.finish();
			break;
		case R.id.clear:
			searchContent.setText("");
			break;
		default:
			break;
		}
	}

}
