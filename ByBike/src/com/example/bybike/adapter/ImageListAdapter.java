package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.fragment.AbAlertDialogFragment;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.user.LoginActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

/**
 * Copyright (c) 2011 All rights reserved 名称：MyListViewAdapter
 * 描述：在Adapter中释放Bitmap
 * 
 * @author zhaoqp
 * @date 2011-12-10
 * @version
 */
public class ImageListAdapter extends BaseAdapter {

	private static String TAG = "ImageListAdapter";
	private static final boolean D = true;

	private Context mContext;
	private NewMainActivity mActivity;
	// xml转View对象
	private LayoutInflater mInflater;
	// 单行的布局
	private int mResource;
	// 列表展现的数据
	private List mData;
	// Map中的key
	private String[] mFrom;
	// view的id
	private int[] mTo;
	// 图片下载器
	private AbImageLoader mAbImageLoader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param data
	 *            列表展现的数据
	 * @param resource
	 *            单行的布局
	 * @param from
	 *            Map中的key
	 * @param to
	 *            view的id
	 */
	public ImageListAdapter(Context context, NewMainActivity activity,
			List data, int resource, String[] from, int[] to) {
		this.mContext = context;
		this.mActivity = activity;
		this.mData = data;
		this.mResource = resource;
		this.mFrom = from;
		this.mTo = to;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 图片下载器
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
		final ViewHolder holder;
		OnLikeButtonClick likeListener = null;
		OnCollectButtonClick collectListener = null;
		// OnTalkButtonClick talkListener = null;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = mInflater.inflate(mResource, parent, false);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.exercisePic = ((ImageView) convertView
					.findViewById(R.id.exercisePic));
			holder.likeButton = (RelativeLayout) convertView
					.findViewById(R.id.likeButton);
			holder.collectButton = (RelativeLayout) convertView
					.findViewById(R.id.collectButton);
			// holder.talkButton = (RelativeLayout) convertView
			// .findViewById(R.id.talkButton);
			holder.exerciseTitle = (TextView) convertView
					.findViewById(R.id.exerciseTitle);
			holder.exerciseAddress = (TextView) convertView
					.findViewById(R.id.exerciseRouteAddress);
			holder.exerciseTime = (TextView) convertView
					.findViewById(R.id.exerciseTime);
			holder.userCount = (TextView) convertView
					.findViewById(R.id.userCount);
			holder.likeCount = (TextView) convertView
					.findViewById(R.id.likeCount);
			holder.talkCount = (TextView) convertView
					.findViewById(R.id.talkCount);
			holder.collectCount = (TextView) convertView
					.findViewById(R.id.collectCount);
			holder.joinStatus = (ImageView) convertView
					.findViewById(R.id.joinStatus);

			likeListener = new OnLikeButtonClick();
			holder.likeButton.setOnClickListener(likeListener);
			collectListener = new OnCollectButtonClick();
			holder.collectButton.setOnClickListener(collectListener);
			// talkListener = new OnTalkButtonClick();
			// holder.talkButton.setOnClickListener(talkListener);

			convertView.setTag(holder);
			convertView.setTag(holder.likeButton.getId(), likeListener);// 对监听对象保存
			convertView.setTag(holder.collectButton.getId(), collectListener);
			// convertView.setTag(holder.talkButton.getId(), talkListener);
		} else {
			holder = (ViewHolder) convertView.getTag();
			likeListener = (OnLikeButtonClick) convertView
					.getTag(holder.likeButton.getId());// 重新获得监听对象
			collectListener = (OnCollectButtonClick) convertView
					.getTag(holder.collectButton.getId());// 重新获得监听对象
			// talkListener = (OnTalkButtonClick)
			// convertView.getTag(holder.talkButton.getId());// 重新获得监听对象
		}
		likeListener.setPosition(position);
		collectListener.setPosition(position);
		// talkListener.setPosition(position);

		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String imageUrl = (String) obj.get("exercisePic");
		// 图片的下载
		mAbImageLoader.display(holder.exercisePic, imageUrl);
		holder.exerciseAddress.setText((String) obj.get("exerciseAddress"));
		holder.exerciseTitle.setText((String) obj.get("exerciseTitle"));
		holder.exerciseTime.setText((String) obj.get("exerciseTime"));

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
		// 点赞、收藏数
		holder.collectCount.setText((String) obj.get("collectCount"));
		holder.likeCount.setText((String) obj.get("likeCount"));
		holder.talkCount.setText((String) obj.get("talkCount"));

		holder.userCount.setText("已报名人数："
				+ (String) obj.get("relityActivityNumber"));
		if ("71".equals((String) obj.get("joinStatus"))) {
			holder.joinStatus.setVisibility(View.VISIBLE);
		} else {
			holder.joinStatus.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	class OnLikeButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			if (!SharedPreferencesUtil.getSharedPreferences_b(mActivity,
					Constant.ISLOGINED)) {

				AbDialogUtil.showAlertDialog(mActivity, 0, "温馨提示",
						"您还未登陆，或登陆状态过期，请重新登录再试",
						new AbAlertDialogFragment.AbDialogOnClickListener() {

							@Override
							public void onPositiveClick() {
								// TODO Auto-generated method stub
								Intent i = new Intent(mActivity,
										LoginActivity.class);
								mActivity.startActivity(i);
								mActivity
										.overridePendingTransition(
												R.anim.fragment_in,
												R.anim.fragment_out);
								AbDialogUtil.removeDialog(mActivity);
							}

							@Override
							public void onNegativeClick() {
								// TODO Auto-generated method stub
								AbDialogUtil.removeDialog(mActivity);
							}
						});
				return;
			}

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

			mActivity.onListViewButtonClicked(1, 0, (String) obj.get("id"));
		}
	}

	class OnCollectButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			
			if (!SharedPreferencesUtil.getSharedPreferences_b(mActivity,
					Constant.ISLOGINED)) {

				AbDialogUtil.showAlertDialog(mActivity, 0, "温馨提示",
						"您还未登陆，或登陆状态过期，请重新登录再试",
						new AbAlertDialogFragment.AbDialogOnClickListener() {

							@Override
							public void onPositiveClick() {
								// TODO Auto-generated method stub
								Intent i = new Intent(mActivity,
										LoginActivity.class);
								mActivity.startActivity(i);
								mActivity
										.overridePendingTransition(
												R.anim.fragment_in,
												R.anim.fragment_out);
								AbDialogUtil.removeDialog(mActivity);
							}

							@Override
							public void onNegativeClick() {
								// TODO Auto-generated method stub
								AbDialogUtil.removeDialog(mActivity);
							}
						});
				return;
			}
			
			final Map<String, Object> obj = (Map<String, Object>) mData
					.get(position);
			int collectCount = Integer
					.valueOf((String) obj.get("collectCount"));
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
			TextView collectCountText = (TextView) v
					.findViewById(R.id.collectCount);
			collectCountText.setText(String.valueOf(collectCount));
			
			mActivity.onListViewButtonClicked(1, 1, (String) obj.get("id"));
			
		}
	}

	// class OnTalkButtonClick implements OnClickListener {
	//
	// int position;
	//
	// public void setPosition(int position) {
	// this.position = position;
	// }
	//
	// @Override
	// public void onClick(View v) {
	// Log.d(TAG, String.valueOf(position));
	// }
	// }

	/**
	 * View元素
	 */
	static class ViewHolder {
		ImageView exercisePic;
		RelativeLayout likeButton;
		RelativeLayout collectButton;
		// RelativeLayout talkButton;
		TextView exerciseTitle;
		TextView exerciseAddress;
		TextView exerciseTime;
		TextView userCount;
		TextView likeCount;
		TextView talkCount;
		TextView collectCount;
		ImageView joinStatus;

	}

}
