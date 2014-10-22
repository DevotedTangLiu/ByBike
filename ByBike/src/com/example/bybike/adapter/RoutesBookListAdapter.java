package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ab.bitmap.AbImageDownloader;
import com.example.bybike.R;

/**
 * 路数列表的Adapter
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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String avatorUrl = (String) obj.get("avatorPic");
		String routeUrl = (String) obj.get("routePic");
		// 图片的下载
		avatorImageDownloader.display(holder.avatorPic, avatorUrl);
		routeImageDownloader.display(holder.routePic, routeUrl);
		return convertView;
	}

	/**
	 * View元素
	 */
	static class ViewHolder {
		ImageView routePic;
		ImageView avatorPic;
	}

}
