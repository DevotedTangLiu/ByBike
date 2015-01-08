package com.example.bybike.marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class AddCommentActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	private String markerId = "";
	private String commentsString = "";

	ListView discussList;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;
	
	EditText comment;
	JSONArray dataArray;
	TextView discussCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_add_comment);
		getTitleBar().setVisibility(View.GONE);
		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(false);

		TextView title = (TextView)findViewById(R.id.title);
		title.setText(getIntent().getStringExtra("name"));
		comment = (EditText)findViewById(R.id.comment);
		discussCount = (TextView)findViewById(R.id.discussCount);
		
		markerId = getIntent().getStringExtra("id");
		commentsString = getIntent().getStringExtra("comments");

		discussList = (ListView) findViewById(R.id.discussList);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				AddCommentActivity.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		try {
			dataArray = new JSONArray(commentsString);
			discussValueList.clear();
			for (int i = 0; i < dataArray.length(); i++) {

				JSONObject jo = dataArray.getJSONObject(i);
				ContentValues v1 = new ContentValues();
				v1.put("senderId", jo.getString("senderId"));
				v1.put("userName", jo.getString("senderName"));
				v1.put("discussContent", jo.getString("content"));
				if(!"null".equals(jo.getString("senderHeadUrl"))){
					v1.put("avater",
							Constant.serverUrl + jo.getString("senderHeadUrl"));
				}else{
					v1.put("avater", "");
				}
				v1.put("discussTime", jo.getString("discussTime"));
				discussValueList.add(v1);
			}
			discussAdapter.notifyDataSetChanged();
			discussCount.setText("评论("+String.valueOf(discussValueList.size())+")");
			discussList.setSelection(discussAdapter.getCount()-1);  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dataArray = new JSONArray();
		}

	}
	
	public void clickHandler(View view){
		switch (view.getId()) {
		case R.id.goBack:
			Intent intent = getIntent();
			intent.putExtra("discussList", dataArray.toString());
			setResult(RESULT_OK, intent);
			AddCommentActivity.this.finish();
			break;

		case R.id.sendButton:
			sendComment();
			break;
		default:
			break;
		}
	}
	
	private void sendComment(){
		
		if (!NetUtil.isConnnected(this)) {
			showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
			return;
		}
		String content = comment.getText().toString().trim();
		if("".equals(content)){
			showToast("请输入评论内容");
			return;
		}
		if(!SharedPreferencesUtil.getSharedPreferences_b(this, Constant.ISLOGINED)){
			showDialog("温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Intent i = new Intent(AddCommentActivity.this, LoginActivity.class);
					startActivity(i);
					overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
					dialog.dismiss();
				}
			});
			return;
		}
		String urlString = Constant.serverUrl + Constant.commentMarkerUrl;
		urlString += ";jsessionid=";
		urlString += SharedPreferencesUtil.getSharedPreferences_s(AddCommentActivity.this,
				Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("markerId", markerId);
		p.put("content", content);
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
				showProgressDialog("正在评论...");
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				showToast("评论失败，请稍后重试...");
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
				
				//隐藏输入法
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(AddCommentActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
				
				ContentValues v1 = new ContentValues();
				v1.put("senderId", SharedPreferencesUtil.getSharedPreferences_s(AddCommentActivity.this, Constant.USERID));
				v1.put("userName", SharedPreferencesUtil.getSharedPreferences_s(AddCommentActivity.this, Constant.USERNICKNAME));
				v1.put("discussContent", comment.getText().toString().trim());
				v1.put("avater",SharedPreferencesUtil.getSharedPreferences_s(AddCommentActivity.this, Constant.USERAVATARURL));
				v1.put("discussTime", (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()).toString());
				discussValueList.add(v1);
				
				discussAdapter.notifyDataSetChanged();
				discussCount.setText("评论("+String.valueOf(discussValueList.size())+")");
				
				JSONObject jo = new JSONObject();
				jo.put("senderId", v1.get("senderId"));
				jo.put("senderName", v1.get("userName"));
				jo.put("content", v1.get("discussContent"));
				jo.put("senderHeadUrl", v1.get("avater"));
				jo.put("discussTime", v1.get("discussTime"));
				dataArray.put(jo);
				
				comment.setText("");
				
			}else if("3".equals(code)){
				
				SharedPreferencesUtil.saveSharedPreferences_b(AddCommentActivity.this, Constant.ISLOGINED, false);
				showDialog("温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i = new Intent(AddCommentActivity.this, LoginActivity.class);
						startActivity(i);
						overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
					}
				});
				
			}else{
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
