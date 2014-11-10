package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.example.bybike.R;
import com.example.bybike.adapter.ImageListAdapter.OnCollectButtonClick;
import com.example.bybike.adapter.ImageListAdapter.OnLikeButtonClick;
import com.example.bybike.adapter.ImageListAdapter.OnTalkButtonClick;

/**
 * 路数列表的Adapter
 * 
 * @author tangliu
 * 
 */
public class RoutesBookListAdapter2 extends BaseAdapter {

	private static String TAG = "RoutresBookListAdapter";
	private static final boolean D = true;

	private Context mContext;
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List mData;
	// 图片下载器
	private AbImageDownloader routeImageDownloader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public RoutesBookListAdapter2(Context context, List list) {

		this.mContext = context;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		routeImageDownloader = new AbImageDownloader(mContext);
		routeImageDownloader.setLoadingImage(R.drawable.image_loading);
		routeImageDownloader.setErrorImage(R.drawable.image_error);
		routeImageDownloader.setNoImage(R.drawable.image_no);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		OnLikeButtonClick likeListener = null;
		OnCollectButtonClick collectListener = null;
		OnTalkButtonClick talkListener = null;
		final ViewHolder holder;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = mInflater.inflate(R.layout.routes_list_item2, null);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.routePic = ((ImageView) convertView
					.findViewById(R.id.routePic));
			holder.likeButton = (RelativeLayout) convertView
					.findViewById(R.id.likeButton);
			holder.collectButton = (RelativeLayout) convertView
					.findViewById(R.id.collectButton);
			holder.talkButton = (RelativeLayout) convertView
					.findViewById(R.id.talkButton);

			likeListener = new OnLikeButtonClick();
			holder.likeButton.setOnClickListener(likeListener);
			collectListener = new OnCollectButtonClick();
			holder.collectButton.setOnClickListener(collectListener);
			talkListener = new OnTalkButtonClick();
			holder.talkButton.setOnClickListener(talkListener);

			convertView.setTag(holder);
			convertView.setTag(holder.likeButton.getId(), likeListener);// 对监听对象保存
			convertView.setTag(holder.collectButton.getId(), collectListener);
			convertView.setTag(holder.talkButton.getId(), talkListener);
		} else {
			holder = (ViewHolder) convertView.getTag();
			likeListener = (OnLikeButtonClick) convertView.getTag(holder.likeButton.getId());// 重新获得监听对象
			collectListener = (OnCollectButtonClick) convertView.getTag(holder.collectButton.getId());// 重新获得监听对象
			talkListener = (OnTalkButtonClick) convertView.getTag(holder.talkButton.getId());// 重新获得监听对象
		}
		likeListener.setPosition(position);
		collectListener.setPosition(position);
		talkListener.setPosition(position);
		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String routeUrl = (String) obj.get("routePic");
		// 图片的下载
		routeImageDownloader.display(holder.routePic, "http://b.hiphotos.baidu.com/image/w%3D400/sign=42dd371b7bf0f736d8fe4d013a54b382/a8014c086e061d95e3c2365c78f40ad163d9caca.jpg");
		return convertView;
	}

	class OnLikeButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Log.d(TAG, String.valueOf(position));
			v.setSelected(true);
		}
	}

	class OnCollectButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Log.d(TAG, String.valueOf(position));
			v.setSelected(true);
		}
	}

	class OnTalkButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Log.d(TAG, String.valueOf(position));
		}
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		ImageView routePic;
		RelativeLayout likeButton;
		RelativeLayout collectButton;
		RelativeLayout talkButton;
	}

}
