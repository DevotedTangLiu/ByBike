package com.example.bybike.message;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.example.bybike.R;
import com.example.bybike.util.CircleImageView;

/**
 * 消息列表的Adapter
 * 
 * @author tangliu
 * 
 */
public class MessageListAdapter extends BaseAdapter {

	private static String TAG = "MessageListAdapter";
	private static final boolean D = true;

	private Context mContext;
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List<ContentValues> list;
	// 图片下载器
	private AbImageLoader mAbImageDownloader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public MessageListAdapter(Context context, List<ContentValues> list) {

		this.mContext = context;
		this.list = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mAbImageDownloader = AbImageLoader.newInstance(context);
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			// 使用自定义的list_items作为Layout
			convertView = mInflater.inflate(R.layout.message_apply_list_item, null);
			// 减少findView的次数
			holder = new ViewHolder();
			holder.imageView = (CircleImageView)convertView.findViewById(R.id.imageView);
			holder.messageContent = (TextView)convertView.findViewById(R.id.messageContent);
			holder.messageTime = (TextView)convertView.findViewById(R.id.messageTime);
			holder.notifyIcon = (ImageView)convertView.findViewById(R.id.notifyIcon);
			// 初始化布局中的元素
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 获取该行的数据
		ContentValues v = this.list.get(position);
		int type = v.getAsInteger("messageType");
		if(type == 0 || type == 1){
			mAbImageDownloader.display(holder.imageView, v.getAsString("avater"));
			holder.notifyIcon.setVisibility(View.INVISIBLE);
		}else if(type == 2){
			holder.imageView.setVisibility(View.INVISIBLE);
			holder.notifyIcon.setVisibility(View.VISIBLE);
		}
		
		holder.messageContent.setText(v.getAsString("message"));
		holder.messageTime.setText(v.getAsString("messageTime"));
		
		// 图片的下载
		return convertView;
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		CircleImageView imageView;
		ImageView notifyIcon;
		TextView messageContent;
		TextView messageTime;
	}

}
