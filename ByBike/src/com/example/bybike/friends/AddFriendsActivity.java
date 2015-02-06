package com.example.bybike.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbToastUtil;
import com.example.bybike.R;
import com.example.bybike.SearchActivity;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.friends.HanziToPinyin.Token;
import com.example.bybike.friends.MyLetterListView.OnTouchingLetterChangedListener;
import com.example.bybike.marker.MarkerDetailActivity;
import com.example.bybike.marker.MarkerListActivity;
import com.example.bybike.user.UserPageActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class AddFriendsActivity extends AbActivity {

	private BaseAdapter adapter;
	private ListView listview;

	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private HashMap<String, Integer> alphaIndexer;

	private String[] sections;

	public List<UserBean> abOrgpersonList = new ArrayList<UserBean>();

	private EditText searchContent;

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;
	ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setContentView(R.layout.activity_add_friends);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(AddFriendsActivity.this);

		mProgressDialog = new ProgressDialog(AddFriendsActivity.this, 5);
		// 设置点击屏幕Dialog不消失
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setMessage("正在搜索，请稍后...");

		// 比较器
		pinyinComparator = new PinyinComparator();
		listview = (ListView) findViewById(R.id.list_view);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent();
				i.setClass(AddFriendsActivity.this, UserPageActivity.class);
				i.putExtra("id", abOrgpersonList.get(position).getUserId());
				startActivity(i);
				overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
			}
		});
		
		alphaIndexer = new HashMap<String, Integer>();
		searchContent = (EditText) findViewById(R.id.searchContent);

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
							searchFriends();
							return true;
						}
						return false;
					}
				});

	}
	
	/**
	 * 搜索标记点
	 */
	private void searchFriends() {

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
			AbToastUtil.showToast(AddFriendsActivity.this, "请输入搜索内容");
			return;
		}
		String urlString = Constant.serverUrl + Constant.findUserUrl;
		String jsession = SharedPreferencesUtil.getSharedPreferences_s(
				AddFriendsActivity.this, Constant.SESSION);
//		urlString += ";jsessionid=";
//		urlString += SharedPreferencesUtil.getSharedPreferences_s(
//				AddFriendsActivity.this, Constant.SESSION);
		AbRequestParams p = new AbRequestParams();
		p.put("pageNo", "1");
		p.put("pageSize", "100");
		p.put("conditions", searchContent.getText().toString().trim());

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
			if ("1".equals(code)) {
				JSONObject dataObj = resultObj.getJSONObject("data");
				
				JSONArray listArray = dataObj.getJSONArray("list");
				if(listArray.length() <= 0){
					AbToastUtil.showToast(AddFriendsActivity.this, "没有搜到相关结果，请重新输入后再试");
					return;
				}
				abOrgpersonList.clear();
				for(int i = 0 ; i < listArray.length(); i ++){
					
					UserBean ub = new UserBean();
					JSONObject jo = listArray.getJSONObject(i);
					ub.setUserNickName(jo.getString("name"));
					ub.setUserId(jo.getString("id"));
					ub.setPicUrl(jo.getString("headUrl"));
					ub.setPinyinname(getFirstPinYin(ub.getUserNickName()));
					abOrgpersonList.add(ub);
				}
				
				setAdapter(abOrgpersonList); 

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

	@Override
	protected void onResume() {
		super.onResume();
	}


	private void setAdapter(List<UserBean> list) {
		adapter = new ListAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private List<UserBean> list;
		// 图片下载器
		private AbImageLoader mAbImageLoader = null;

		public ListAdapter(Context context, List<UserBean> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;

			// 图片下载器
			mAbImageLoader = AbImageLoader.newInstance(context);
			mAbImageLoader.setLoadingImage(R.drawable.image_loading);
			mAbImageLoader.setErrorImage(R.drawable.image_error);
			mAbImageLoader.setEmptyImage(R.drawable.image_empty);

			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				String currentStr = getAlpha(list.get(i).getPinyinname());
				String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1)
						.getPinyinname()) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = getAlpha(list.get(i).getPinyinname());
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.friends_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.avater = (com.example.bybike.util.CircleImageView) convertView
						.findViewById(R.id.imageView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			UserBean cv = list.get(position);
			holder.name.setText(cv.getUserNickName());
			String currentStr = getAlpha(list.get(position).getPinyinname());
			String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
					position - 1).getPinyinname()) : " ";

			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			// 图片的下载
			if(!cv.getPicUrl().equals("")){
				mAbImageLoader.display(holder.avater, Constant.serverUrl + cv.getPicUrl());
			}		
			return convertView;
		}

		private class ViewHolder {

			TextView alpha;
			TextView name;
			com.example.bybike.util.CircleImageView avater;
		}
	}

	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}

		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	/**
	 * 获取简拼
	 * 
	 * @param source
	 * @return
	 */
	private String getFirstPinYin(String source) {

		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(source);
		if (tokens == null || tokens.size() == 0) {
			return source;
		}
		StringBuffer result = new StringBuffer();

		for (Token token : tokens) {
			if (token.type == Token.PINYIN) {
				result.append(token.target.charAt(0));
			} else {
				result.append(token.source);
			}
		}
		System.out.println(result.toString());
		return result.toString();
	}

	/**
	 * 点击事件
	 * 
	 * @param view
	 */
	public void clickHandler(View view) {
		switch (view.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		case R.id.clearSearchText:
			searchContent.setText("");
			closeKeyboard();
			break;
		default:
			break;
		}
	}

	// 关闭软键盘
	private void closeKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchContent.getWindowToken(), 0);
	}
}
