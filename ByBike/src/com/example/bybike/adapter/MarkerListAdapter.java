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

import com.example.bybike.R;
import com.example.bybike.db.model.MarkerBean;

/**
 * 路数列表的Adapter
 * 
 * @author tangliu
 * 
 */
public class MarkerListAdapter extends BaseAdapter {

	private static String TAG = "MarkerListAdapter";
	private static final boolean D = true;

	private Context mContext;
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List<MarkerBean> mData;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public MarkerListAdapter(Context context, List<MarkerBean> list) {

		this.mContext = context;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.markers_list_item, null);
			// 减少findView的次数
			holder = new ViewHolder();
			// 初始化布局中的元素
			holder.markerIconPic = ((ImageView) convertView
					.findViewById(R.id.markerIconPic));
			holder.markerType = ((TextView) convertView
					.findViewById(R.id.markerType));
			holder.markerName = ((TextView) convertView
                    .findViewById(R.id.markerName));
			holder.markerAddress = ((TextView) convertView
                    .findViewById(R.id.markerAddress));
			holder.likeButton = (RelativeLayout) convertView
					.findViewById(R.id.likeButton);
			holder.collectButton = (RelativeLayout) convertView
					.findViewById(R.id.collectButton);
			holder.talkButton = (RelativeLayout) convertView
					.findViewById(R.id.talkButton);
			holder.likeCount = (TextView) convertView
					.findViewById(R.id.likeCount);
			holder.talkCount = (TextView) convertView
					.findViewById(R.id.talkCount);
			holder.collectCount = (TextView) convertView
					.findViewById(R.id.collectCount);

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
		MarkerBean mb = mData.get(position);
		holder.collectCount.setText(mb.getCollectCount());
		holder.likeCount.setText(mb.getLikeCount());
		holder.talkCount.setText(mb.getCommentCount());
		holder.markerName.setText(mb.getMarkerName());
		holder.markerAddress.setText(mb.getAddress());
		String markerType = mb.getMarkerType();
		if ("RantCar".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_rentbike);
			holder.markerType.setText("租车");
		} else if ("Repair".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_repair);
			holder.markerType.setText("维修");
		} else if ("FeatureSpot".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_scenery);
			holder.markerType.setText("景点");
		} else if ("Catering".equalsIgnoreCase(markerType)) {
			holder.markerIconPic.setImageResource(R.drawable.marker_icon_meals);
			holder.markerType.setText("餐饮");
		} else if ("Washroom".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_washroom);
			holder.markerType.setText("卫生间");
		} else if ("Parking".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_parking);
			holder.markerType.setText("停车");
		} else if ("Other".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_others);
			holder.markerType.setText("其他");
		} else if ("CarShop".equalsIgnoreCase(markerType)) {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_bikestore);
			holder.markerType.setText("车店");
		} else {
			holder.markerIconPic
					.setImageResource(R.drawable.marker_icon_others);
			holder.markerType.setText("其他");
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
		ImageView markerIconPic;
		TextView markerType;
		TextView markerName;
		TextView markerAddress;
		RelativeLayout likeButton;
		RelativeLayout collectButton;
		RelativeLayout talkButton;
		TextView likeCount;
		TextView talkCount;
		TextView collectCount;
	}

}
