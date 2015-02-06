package com.example.bybike.message;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.view.pullview.AbPullToRefreshView;
import com.example.bybike.R;
import com.example.bybike.db.dao.MessageBeanDao;
import com.example.bybike.db.model.MessageBean;

public class MessageListActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	private List<MessageBean> myApplyListData = null;
	private MessageListAdapter myApplyListAdapter = null;
	private AbPullToRefreshView myApplyListView = null;
	private ListView myApplyList = null;

	private List<MessageBean> myCommentListData = null;
	private MessageListAdapter myCommentListAdapter = null;
	private AbPullToRefreshView myCommentListView = null;
	private ListView myCommentList = null;

	private List<MessageBean> myNotifyListData = null;
	private MessageListAdapter myNotifyListAdapter = null;
	private AbPullToRefreshView myNotifyListView = null;
	private ListView myNotifyList = null;

	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表

	private Button applyButton;
	private Button commentButton;
	private Button notifyButton;
	
	MessageBeanDao messageBeanDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_message_list);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		applyButton = (Button) findViewById(R.id.applyButton);
		commentButton = (Button) findViewById(R.id.commentButton);
		notifyButton = (Button) findViewById(R.id.notifyButton);
		applyButton.setSelected(true);
		
		// 填充用户活动、路书、友好点数据
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();
		initViewPager();
	}
	
	@Override
	protected void onResume(){		
		super.onResume();
		
	}

	/**
	 * 初始化viewPager数据 initViewPager(这里用一句话描述这个方法的作用)
	 */
	private void initViewPager() {

		LayoutInflater mInflater = MessageListActivity.this.getLayoutInflater();
		/**
		 * 初始化我的请求列表
		 */
		View myApplyListLayout = mInflater.inflate(R.layout.pull_list, null);
		myApplyListView = (AbPullToRefreshView) myApplyListLayout
				.findViewById(R.id.mPullRefreshView);
		
		myApplyListView.setPullRefreshEnable(false);
		myApplyListView.setLoadMoreEnable(false);
		
		myApplyList = (ListView) myApplyListLayout.findViewById(R.id.mListView);
		myApplyList.setDivider(new ColorDrawable(Color.GRAY));  
		myApplyList.setDividerHeight(1); 

		// ListView数据
		myApplyListData = new ArrayList<MessageBean>();
		// 使用自定义的Adapter
		myApplyListAdapter = new MessageListAdapter(MessageListActivity.this,
				myApplyListData);
		myApplyList.setAdapter(myApplyListAdapter);
		// item被点击事件
		myApplyList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		listViews.add(myApplyListLayout);

		/**
		 * 初始评论通知列表
		 */
		View myCommentListLayout = mInflater.inflate(R.layout.pull_list, null);
		myCommentListView = (AbPullToRefreshView) myCommentListLayout
				.findViewById(R.id.mPullRefreshView);
		
		myCommentListView.setPullRefreshEnable(false);
		myCommentListView.setLoadMoreEnable(false);
		
		myCommentList = (ListView) myCommentListLayout
				.findViewById(R.id.mListView);
		myCommentList.setDivider(new ColorDrawable(Color.GRAY));  
		myCommentList.setDividerHeight(1); 
		// ListView数据
		myCommentListData = new ArrayList<MessageBean>();
		// 使用自定义的Adapter
		myCommentListAdapter = new MessageListAdapter(MessageListActivity.this,
				myCommentListData);
		myCommentList.setAdapter(myCommentListAdapter);
		// item被点击事件
		myCommentList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		listViews.add(myCommentListLayout);

		/**
		 * 初始化我的通知列表
		 */
		View myNotifyListLayout = mInflater.inflate(R.layout.pull_list, null);
		myNotifyListView = (AbPullToRefreshView) myNotifyListLayout
				.findViewById(R.id.mPullRefreshView);
		
		myNotifyListView.setPullRefreshEnable(false);
		myNotifyListView.setLoadMoreEnable(false);
		
		myNotifyList = (ListView) myNotifyListLayout
				.findViewById(R.id.mListView);
		myNotifyList.setDivider(new ColorDrawable(Color.GRAY));  
		myNotifyList.setDividerHeight(1); 
		// ListView数据
		myNotifyListData = new ArrayList<MessageBean>();
		// 使用自定义的Adapter
		myNotifyListAdapter = new MessageListAdapter(MessageListActivity.this,
				myNotifyListData);
		myNotifyList.setAdapter(myNotifyListAdapter);
		// item被点击事件
		myNotifyList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		listViews.add(myNotifyListLayout);

		/**
		 * 显示viewPager
		 */
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		showMessages();
		
	}
	
	
	private void showMessages(){
		
		messageBeanDao = new MessageBeanDao(MessageListActivity.this);
		messageBeanDao.startReadableDatabase();
		
		List<MessageBean>messages = new ArrayList<MessageBean>();
		messages = messageBeanDao.queryList();
		if(messages.size() > 0){		
			myApplyListData.clear();
			myCommentListData.clear();
			myNotifyListData.clear();
		}
		for(MessageBean m : messages){
			
			if(m.getMessageType().equals("0")){
				
				myApplyListData.add(m);
				
			}else if("1".equals(m.getMessageType())){
				
				myCommentListData.add(m);
				
			}else if("2".equals(m.getMessageType())){
				
				myNotifyListData.add(m);
			}		
		}
		
		messageBeanDao.closeDatabase();
		
		myApplyListAdapter.notifyDataSetChanged();
		myCommentListAdapter.notifyDataSetChanged();
		myNotifyListAdapter.notifyDataSetChanged();	
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		case R.id.applyButton:
			mPager.setCurrentItem(0);
			break;
		case R.id.commentButton:
			mPager.setCurrentItem(1);
			break;
		case R.id.notifyButton:
			mPager.setCurrentItem(2);
			break;
		default:
			break;
		}

	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				applyButton.setSelected(true);
				commentButton.setSelected(false);
				notifyButton.setSelected(false);
				break;
			case 1:
				applyButton.setSelected(false);
				commentButton.setSelected(true);
				notifyButton.setSelected(false);
				break;
			case 2:
				applyButton.setSelected(false);
				commentButton.setSelected(false);
				notifyButton.setSelected(true);
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

}
