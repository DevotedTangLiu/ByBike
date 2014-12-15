package com.example.bybike.message;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.view.pullview.AbPullListView;
import com.example.bybike.R;

public class MessageListActivity extends AbActivity {

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	// 评论列表
	AbPullListView messageList = null;
	List<ContentValues> messageValueList = null;
	MessageListAdapter messageAdapter = null;
	private View headView = null;
	private int currentType = 0;
	private Button applyButton;
	private Button discussButton;
	private Button notifyButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_message_list);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(false);

		messageList = (AbPullListView) findViewById(R.id.mListView);
		// 添加header
		headView = mInflater.inflate(R.layout.header_message_list, null);
		applyButton = (Button) headView.findViewById(R.id.applyButton);
		discussButton = (Button) headView.findViewById(R.id.discussButton);
		notifyButton = (Button) headView.findViewById(R.id.notifyButton);
		applyButton.setSelected(true);

		messageList.addHeaderView(headView);
		messageValueList = new ArrayList<ContentValues>();
		messageAdapter = new MessageListAdapter(MessageListActivity.this,
				messageValueList);
		messageList.setAdapter(messageAdapter);

		loadData();

	}

	/**
	 * 载入数据
	 */
	private void loadData() {
		// TODO Auto-generated method stub

		// 填充消息列表
		ContentValues v1 = new ContentValues();
		v1.put("id", "1");
		v1.put("messageType", 0);
		v1.put("message", "ChaolotteYam邀请您加为好友");
		v1.put("avater",
				"http://t11.baidu.com/it/u=1610160448,1299213022&fm=56");
		v1.put("messageTime", "10.28 14:30");
		messageValueList.add(v1);
		
		ContentValues v2 = new ContentValues();
		v2.put("id", "1");
		v2.put("messageType", 0);
		v2.put("message", "tangliu邀请您加为好友");
		v2.put("avater",
				"http://img0.bdstatic.com/img/image/shouye/mxpyy1211.jpg");
		v2.put("messageTime", "10.28 14:35");
		messageValueList.add(v2);
		
		ContentValues v3 = new ContentValues();
		v3.put("id", "1");
		v3.put("messageType", 2);
		v3.put("message", "参加返老还童活动吧");
		v3.put("messageTime", "10.28 14:35");
		messageValueList.add(v3);
		
		messageAdapter.notifyDataSetChanged();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.goBack:
			this.finish();
			break;
		default:
			break;
		}

	}

}
