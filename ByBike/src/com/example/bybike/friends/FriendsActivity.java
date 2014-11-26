package com.example.bybike.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.example.bybike.R;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.friends.HanziToPinyin.Token;
import com.example.bybike.friends.MyLetterListView.OnTouchingLetterChangedListener;

public class FriendsActivity extends AbActivity {

	private BaseAdapter adapter;

	private ListView listview;

	private TextView overlay;

	private MyLetterListView letterListView;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;

	private HashMap<String, Integer> alphaIndexer;

	private String[] sections;

	public List<UserBean> abOrgpersonList = new ArrayList<UserBean>();

	private WindowManager windowManager;
	
	private EditText searchText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setContentView(R.layout.friends);

		// 比较器
		pinyinComparator = new PinyinComparator();

		windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		// asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		listview = (ListView) findViewById(R.id.list_view);
		letterListView = (MyLetterListView) findViewById(R.id.my_list_view);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		alphaIndexer = new HashMap<String, Integer>();
		new Handler();
		new OverlayThread();
		initOverlay();

		searchText = (EditText) findViewById(R.id.searchContent);
		searchText.addTextChangedListener(new MyTextWatcher());
	}

	@Override
	protected void onResume() {
		super.onResume();
		showFriends();
	}

	private void showFriends() {

		List<UserBean> list = new ArrayList<UserBean>();
		
		list.clear();
		UserBean a = new UserBean();
		a.setUserNickName("Don Dong");
		a.setPicUrl("http://a.hiphotos.baidu.com/image/h%3D360/sign=6a73a026ba12c8fcabf3f0cbcc0292b4/8326cffc1e178a82a1f9ada6f503738da877e8eb.jpg");
		a.setPinyinname(getFirstPinYin("Don Dong"));
		list.add(a);

		UserBean b = new UserBean();
		b.setUserNickName("FrancoLam");
		b.setPicUrl("http://e.hiphotos.baidu.com/image/h%3D360/sign=8ae1030fb5003af352bada66052ac619/b58f8c5494eef01f861bfbc3e3fe9925bc317dad.jpg");
		b.setPinyinname(getFirstPinYin("FrancoLam"));
		list.add(b);

		UserBean c = new UserBean();
		c.setUserNickName("Franco Lam");
		c.setPicUrl("http://e.hiphotos.baidu.com/image/h%3D360/sign=42df56e2b01c8701c9b6b4e0177e9e6e/0d338744ebf81a4c3da304cfd42a6059252da627.jpg");
		c.setPinyinname(getFirstPinYin("Franco Lam"));
		list.add(c);

		UserBean d = new UserBean();
		d.setUserNickName("Gergary_2034");
		d.setPicUrl("http://c.hiphotos.baidu.com/image/h%3D360/sign=c48c21098494a4c21523e12d3ef41bac/a8773912b31bb051ea1d732e357adab44aede0ff.jpg");
		d.setPinyinname(getFirstPinYin("Gergary_2034"));
		list.add(d);

		UserBean e = new UserBean();
		e.setUserNickName("唐流");
		e.setPicUrl("http://c.hiphotos.baidu.com/image/h%3D360/sign=c48c21098494a4c21523e12d3ef41bac/a8773912b31bb051ea1d732e357adab44aede0ff.jpg");
		e.setPinyinname(getFirstPinYin("唐流"));
		list.add(e);

		UserBean f = new UserBean();
		f.setUserNickName("一声百年何足道");
		f.setPicUrl("http://c.hiphotos.baidu.com/image/h%3D360/sign=c48c21098494a4c21523e12d3ef41bac/a8773912b31bb051ea1d732e357adab44aede0ff.jpg");
		f.setPinyinname(getFirstPinYin("一生百年何足道"));
		list.add(f);

		UserBean g = new UserBean();
		g.setUserNickName("~people changed");
		g.setPicUrl("http://c.hiphotos.baidu.com/image/h%3D360/sign=c48c21098494a4c21523e12d3ef41bac/a8773912b31bb051ea1d732e357adab44aede0ff.jpg");
		g.setPinyinname(getFirstPinYin("~people changed"));

		list.add(g);

		UserBean h = new UserBean();

		h.setPicUrl("http://c.hiphotos.baidu.com/image/h%3D360/sign=c48c21098494a4c21523e12d3ef41bac/a8773912b31bb051ea1d732e357adab44aede0ff.jpg");
		h.setUserNickName("测试");
		h.setPinyinname(getFirstPinYin("测试"));
		list.add(h);

		if (list.size() > 0) {
			abOrgpersonList = list;
			Collections.sort(list, pinyinComparator);
			setAdapter(list);
		}

	}

	private void setAdapter(List<UserBean> list) {
		adapter = new ListAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private List<UserBean> list;
		// 图片下载器
		private AbImageDownloader mAbImageDownloader = null;
		// 用户列表
		private List<UserBean> userList;

		public ListAdapter(Context context, List<UserBean> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;

			// 图片下载器
			mAbImageDownloader = new AbImageDownloader(context);
			mAbImageDownloader.setType(AbConstant.ORIGINALIMG);
			mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
			mAbImageDownloader.setErrorImage(R.drawable.image_error);
			mAbImageDownloader.setNoImage(R.drawable.image_no);

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
			mAbImageDownloader.display(holder.avater, cv.getPicUrl());

			return convertView;
		}

		private class ViewHolder {

			TextView alpha;
			TextView name;
			com.example.bybike.util.CircleImageView avater;
		}
	}

	/**
	 * 通过TextWatcher去观察输入框中输入的内容
	 */
	class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			try {
				filterData(s.toString());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	}
	
	  /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr){
        List<UserBean> tempList = new ArrayList<UserBean>();
        
        if(TextUtils.isEmpty(filterStr)){
            tempList = abOrgpersonList;
        }else{
            tempList.clear();
            
            for (int i = 0; i < abOrgpersonList.size(); i++) {
                UserBean item = abOrgpersonList.get(i);
                String name = item.getUserNickName();
                String pyname = item.getPinyinname();
                if(name.indexOf(filterStr.toString()) != -1 || pyname.contains((filterStr.toString()))){
                    tempList.add(item);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(tempList, pinyinComparator);
        setAdapter(tempList);
    }

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(120,
				120, 100, 0, WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		// WindowManager windowManager = (WindowManager)
		// this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s, float y, float x) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);

				listview.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);

			}
		}

		@Override
		public void onTouchingLetterEnd() {
			overlay.setVisibility(View.GONE);
		}
	}

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
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
		if (windowManager != null)// 防止内存泄露
		{
			windowManager.removeView(overlay);
		}
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
	 * @param view
	 */
	public void clickHandler(View view) {
		switch (view.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		case R.id.clearSearchText:
			searchText.setText("");
			closeKeyboard();
			break;
		default:
			break;
		}
	}
	
	 // 关闭软键盘
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }
}
