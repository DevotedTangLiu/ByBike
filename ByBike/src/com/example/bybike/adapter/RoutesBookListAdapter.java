package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import com.example.bybike.adapter.ExerciseDiscussListAdapter.OnImageClickListener;
import com.example.bybike.adapter.ImageListAdapter.OnCollectButtonClick;
import com.example.bybike.adapter.ImageListAdapter.OnLikeButtonClick;
import com.example.bybike.adapter.ImageListAdapter.OnTalkButtonClick;
import com.example.bybike.user.UserPageActivity;
import com.example.bybike.util.Constant;

/**
 * 路数列表的Adapter
 * 
 * @author tangliu
 * 
 */
public class RoutesBookListAdapter extends BaseAdapter {

	private static String TAG = "RoutresBookListAdapter";
	private static final boolean D = true;

	private Context mContext;
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List mData;
	// 图片下载器
	private AbImageDownloader avatorImageDownloader = null;
	private AbImageDownloader routeImageDownloader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public RoutesBookListAdapter(Context context, List list) {

		this.mContext = context;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		routeImageDownloader = new AbImageDownloader(mContext);
		routeImageDownloader.setLoadingImage(R.drawable.image_loading);
		routeImageDownloader.setErrorImage(R.drawable.image_error);
		routeImageDownloader.setNoImage(R.drawable.image_no);

		avatorImageDownloader = new AbImageDownloader(mContext);
		avatorImageDownloader.setLoadingImage(R.drawable.image_loading);
		avatorImageDownloader.setErrorImage(R.drawable.image_error);
		avatorImageDownloader.setNoImage(R.drawable.image_no);
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
		 OnImageClickListener imageClick = null;
		final ViewHolder holder;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = mInflater.inflate(R.layout.routes_list_item, null);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.avatorPic = ((ImageView) convertView
					.findViewById(R.id.avatorPic));
			holder.routePic = ((ImageView) convertView
					.findViewById(R.id.routePic));
			holder.userNickName = (TextView) convertView
					.findViewById(R.id.userNickName);
			holder.routeTitle = (TextView)convertView.findViewById(R.id.routeTitle);
			holder.routeAddress = (TextView)convertView.findViewById(R.id.routeAddress);
			holder.routeDescription = (TextView)convertView.findViewById(R.id.routeDescription);
			holder.routeTime = (TextView)convertView.findViewById(R.id.routeTime);
			holder.collectCount = (TextView)convertView.findViewById(R.id.collectCount);
			holder.talkCount = (TextView)convertView.findViewById(R.id.talkCount);
			holder.likeCount = (TextView)convertView.findViewById(R.id.likeCount);
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
			
			imageClick = new OnImageClickListener();
            holder.avatorPic.setOnClickListener(imageClick);

			convertView.setTag(holder);
			convertView.setTag(holder.likeButton.getId(), likeListener);// 对监听对象保存
			convertView.setTag(holder.collectButton.getId(), collectListener);
			convertView.setTag(holder.talkButton.getId(), talkListener);
			convertView.setTag(holder.avatorPic.getId(), imageClick);
		} else {
			holder = (ViewHolder) convertView.getTag();
			likeListener = (OnLikeButtonClick) convertView.getTag(holder.likeButton.getId());// 重新获得监听对象
			collectListener = (OnCollectButtonClick) convertView.getTag(holder.collectButton.getId());// 重新获得监听对象
			talkListener = (OnTalkButtonClick) convertView.getTag(holder.talkButton.getId());// 重新获得监听对象
		    imageClick = (OnImageClickListener)convertView.getTag(holder.avatorPic.getId());
		
		}
		likeListener.setPosition(position);
		collectListener.setPosition(position);
		talkListener.setPosition(position);
		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String avatorUrl = (String) obj.get("userHeadPicUrl");
		String routeUrl = (String) obj.get("routePic");
		// 图片的下载
		avatorImageDownloader.display(holder.avatorPic, avatorUrl);
		routeImageDownloader.display(holder.routePic, routeUrl);
		holder.routeTitle.setText((String)obj.get("title"));
		holder.likeCount.setText((String)obj.get("likeCount"));
		holder.talkCount.setText((String)obj.get("commentCount"));
		holder.collectCount.setText((String)obj.get("collectCount"));
		holder.userNickName.setText((String)obj.get("userName"));
		holder.routeAddress.setText((String)obj.get("kilometers") + "km "+ (String)obj.get("routeAddress"));
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
		ImageView avatorPic;
		TextView userNickName;
		TextView routeTitle;
		TextView routeAddress;
		TextView routeDescription;
		TextView routeTime;
		TextView likeCount;
		RelativeLayout likeButton;
		TextView collectCount;
		RelativeLayout collectButton;
		TextView talkCount;
		RelativeLayout talkButton;
	}
	
	class OnImageClickListener implements OnClickListener {

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(mContext, UserPageActivity.class);
            mContext.startActivity(i);
        }
    }

}
