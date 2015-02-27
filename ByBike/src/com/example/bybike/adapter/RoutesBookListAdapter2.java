package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.adapter.RoutesBookListAdapter.OnImageClickListener;
import com.example.bybike.user.UserPageActivity;

/**
 * 路数列表的Adapter 这个adapter专用于“我的”页面列表展示
 * 
 * @author tangliu
 * 
 */
public class RoutesBookListAdapter2 extends BaseAdapter {

	private static String TAG = "RoutresBookListAdapter";
	private static final boolean D = true;

	private Context mContext;
	private NewMainActivity mActivity;
	private UserPageActivity uActivity;
	private String ownerType = "";
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List mData;
	// 图片下载器
	private AbImageLoader mAbImageLoader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public RoutesBookListAdapter2(Context context, NewMainActivity activity,
			List list) {

		this.ownerType = "Fragment";
		this.mContext = context;
		this.mActivity = activity;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAbImageLoader = AbImageLoader.newInstance(context);
		mAbImageLoader.setLoadingImage(R.drawable.image_loading);
		mAbImageLoader.setErrorImage(R.drawable.image_error);
		mAbImageLoader.setEmptyImage(R.drawable.image_empty);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public RoutesBookListAdapter2(Context context, UserPageActivity activity,
			List list) {

		this.ownerType = "Activity";
		this.uActivity = activity;
		this.mContext = context;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAbImageLoader = AbImageLoader.newInstance(context);
		mAbImageLoader.setLoadingImage(R.drawable.image_loading);
		mAbImageLoader.setErrorImage(R.drawable.image_error);
		mAbImageLoader.setEmptyImage(R.drawable.image_empty);
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
		OnImageClickListener imageClick = null;
		final ViewHolder holder;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = mInflater.inflate(R.layout.routes_list_item2, null);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.routePic = ((ImageView) convertView
					.findViewById(R.id.routePic));
			holder.routeTitle = (TextView) convertView
					.findViewById(R.id.routeTitle);
			holder.routeAddress = (TextView) convertView
					.findViewById(R.id.routeAddress);
			holder.routeDescription = (TextView) convertView
					.findViewById(R.id.routeDescription);
			holder.routeTime = (TextView) convertView
					.findViewById(R.id.routeTime);
			holder.collectCount = (TextView) convertView
					.findViewById(R.id.collectCount);
			holder.talkCount = (TextView) convertView
					.findViewById(R.id.talkCount);
			holder.likeCount = (TextView) convertView
					.findViewById(R.id.likeCount);
			holder.likeButton = (RelativeLayout) convertView
					.findViewById(R.id.likeButton);
			holder.collectButton = (RelativeLayout) convertView
					.findViewById(R.id.collectButton);

			likeListener = new OnLikeButtonClick();
			holder.likeButton.setOnClickListener(likeListener);
			collectListener = new OnCollectButtonClick();
			holder.collectButton.setOnClickListener(collectListener);

			convertView.setTag(holder);
			convertView.setTag(holder.likeButton.getId(), likeListener);// 对监听对象保存
			convertView.setTag(holder.collectButton.getId(), collectListener);

		} else {
			holder = (ViewHolder) convertView.getTag();
			likeListener = (OnLikeButtonClick) convertView
					.getTag(holder.likeButton.getId());// 重新获得监听对象
			collectListener = (OnCollectButtonClick) convertView
					.getTag(holder.collectButton.getId());// 重新获得监听对象
		}
		likeListener.setPosition(position);
		collectListener.setPosition(position);
		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String avatorUrl = (String) obj.get("userHeadPicUrl");
		String routeUrl = (String) obj.get("routePic");
		// 图片的下载

		mAbImageLoader.display(holder.routePic, routeUrl);
		holder.routeTitle.setText((String) obj.get("title"));
		holder.routeAddress.setText((String) obj.get("kilometers") + "km "
				+ (String) obj.get("routeAddress"));
		holder.routeDescription.setText((String) obj.get("roadContent"));

		// 点赞和收藏按钮状态
		if (((String) obj.get("likeStatus")).equals("true")) {
			holder.likeButton.setSelected(true);
		} else {
			holder.likeButton.setSelected(false);
		}
		if (((String) obj.get("collectStatus")).equals("true")) {
			holder.collectButton.setSelected(true);
		} else {
			holder.collectButton.setSelected(false);
		}
		// 点赞和收藏数
		holder.likeCount.setText((String) obj.get("likeCount"));
		holder.talkCount.setText((String) obj.get("commentCount"));
		holder.collectCount.setText((String) obj.get("collectCount"));

		return convertView;
	}

	class OnLikeButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			final Map<String, Object> obj = (Map<String, Object>) mData
					.get(position);
			int likeCount = Integer.valueOf((String) obj.get("likeCount"));
			if (v.isSelected()) {
				v.setSelected(false);
				likeCount--;
				if (likeCount < 0)
					likeCount = 0;

				obj.put("likeStatus", "false");

			} else {
				likeCount++;
				v.setSelected(true);

				obj.put("likeStatus", "true");
			}
			obj.put("likeCount", String.valueOf(likeCount));
			TextView likeCountText = (TextView) v.findViewById(R.id.likeCount);
			likeCountText.setText(String.valueOf(likeCount));

			if ("Fragment".equalsIgnoreCase(ownerType)) {
				mActivity.onListViewButtonClicked(2, 0, (String) obj.get("id"));
			} else {
				uActivity.onListViewButtonClicked(2, 0, (String) obj.get("id"));
			}
		}
	}

	class OnCollectButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			final Map<String, Object> obj = (Map<String, Object>) mData
					.get(position);
			int collectCount = Integer.valueOf((String) obj.get("collectCount"));
			if (v.isSelected()) {
				v.setSelected(false);
				collectCount--;
				if (collectCount < 0)
					collectCount = 0;
				obj.put("collectStatus", "false");

			} else {
				collectCount++;
				v.setSelected(true);
				obj.put("collectStatus", "true");
			}
			obj.put("collectCount", String.valueOf(collectCount));
			TextView collectCountText = (TextView) v.findViewById(R.id.collectCount);
			collectCountText.setText(String.valueOf(collectCount));

			if ("Fragment".equalsIgnoreCase(ownerType)) {
				mActivity.onListViewButtonClicked(2, 1, (String) obj.get("id"));
			} else {
				uActivity.onListViewButtonClicked(2, 1, (String) obj.get("id"));
			}
		}
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		ImageView routePic;
		TextView routeTitle;
		TextView routeAddress;
		TextView routeDescription;
		TextView routeTime;
		TextView likeCount;
		RelativeLayout likeButton;
		TextView collectCount;
		RelativeLayout collectButton;
		TextView talkCount;
	}

}
