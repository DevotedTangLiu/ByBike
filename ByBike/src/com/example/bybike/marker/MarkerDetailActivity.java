package com.example.bybike.marker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.example.bybike.R;
import com.example.bybike.adapter.ExerciseDiscussListAdapter;

public class MarkerDetailActivity extends AbActivity {

	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;
	ListView discussList;
	List<ContentValues> discussValueList = null;
	ExerciseDiscussListAdapter discussAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_marker_detail);
		getTitleBar().setVisibility(View.GONE);

		mAbImageDownloader = new AbImageDownloader(this);
		ImageView markerPic = (ImageView) findViewById(R.id.markerPic);
		mAbImageDownloader
				.display(markerPic,
						"http://img0.imgtn.bdimg.com/it/u=1536848949,318514089&fm=21&gp=0.jpg");

		discussList = (ListView) findViewById(R.id.discussList);
		discussValueList = new ArrayList<ContentValues>();
		discussAdapter = new ExerciseDiscussListAdapter(
				MarkerDetailActivity.this, discussValueList);
		discussList.setAdapter(discussAdapter);

		// 填充评论列表
		for (int i = 0; i < 2; i++) {
			ContentValues v1 = new ContentValues();
			v1.put("userName", "ChaolotteYam");
			v1.put("discussContent", "有爱。");
			v1.put("avater",
					"http://t11.baidu.com/it/u=1610160448,1299213022&fm=56");
			v1.put("discussTime", "10.28 14:30");
			discussValueList.add(v1);
			ContentValues v2 = new ContentValues();
			v2.put("userName", "Jeronmme_1221");
			v2.put("discussContent", "今天倍儿爽！");
			v2.put("avater",
					"http://t11.baidu.com/it/u=1620038746,1252150868&fm=56");
			v2.put("discussTime", "10.28 14:48");
			discussValueList.add(v2);
		}
		discussAdapter.notifyDataSetChanged();

	}
	
	/**
	 * 接受调用
	 * 
	 * @param source
	 */
	public void clickHandler(View source) {

		switch (source.getId()) {
		case R.id.goBack:
			goBack();
			break;

		case R.id.goToMap:
			goBack();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 退出页面
	 */
	private void goBack() {
		MarkerDetailActivity.this.finish();
	}

}
