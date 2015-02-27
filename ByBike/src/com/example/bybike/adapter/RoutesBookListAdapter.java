package com.example.bybike.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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
import com.example.bybike.user.UserPageActivity;
import com.example.bybike.util.Constant;
import com.example.bybike.util.SharedPreferencesUtil;

/**
 * 路数列表的Adapter
 * 
 * @author tangliu
 * 
 */
public class RoutesBookListAdapter extends BaseAdapter {

	private static String TAG = "RoutresBookListAdapter";
	private static final boolean D = true;

	private NewMainActivity mContext;
	// xml转View对象
	private LayoutInflater mInflater;
	// 列表展现的数据
	private List mData;
	// 图片下载器
	private AbImageLoader avatorImageDownloader = null;
	private AbImageLoader routeImageDownloader = null;

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param list
	 */
	public RoutesBookListAdapter(NewMainActivity context, List list) {

		this.mContext = context;
		this.mData = list;
		// 用于将xml转为View
		this.mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		avatorImageDownloader = AbImageLoader.newInstance(context);
		avatorImageDownloader.setLoadingImage(R.drawable.image_loading);
		avatorImageDownloader.setErrorImage(R.drawable.image_error);
		avatorImageDownloader.setEmptyImage(R.drawable.image_empty);

		routeImageDownloader = AbImageLoader.newInstance(context);
		routeImageDownloader.setLoadingImage(R.drawable.image_loading);
		routeImageDownloader.setErrorImage(R.drawable.image_error);
		routeImageDownloader.setEmptyImage(R.drawable.image_empty);
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
//		OnTalkButtonClick talkListener = null;
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
//			holder.talkButton = (RelativeLayout) convertView
//					.findViewById(R.id.talkButton);

			likeListener = new OnLikeButtonClick();
			holder.likeButton.setOnClickListener(likeListener);
			collectListener = new OnCollectButtonClick();
			holder.collectButton.setOnClickListener(collectListener);
//			talkListener = new OnTalkButtonClick();
//			holder.talkButton.setOnClickListener(talkListener);
			
			imageClick = new OnImageClickListener();
            holder.avatorPic.setOnClickListener(imageClick);

			convertView.setTag(holder);
			convertView.setTag(holder.likeButton.getId(), likeListener);// 对监听对象保存
			convertView.setTag(holder.collectButton.getId(), collectListener);
//			convertView.setTag(holder.talkButton.getId(), talkListener);
			convertView.setTag(holder.avatorPic.getId(), imageClick);
		} else {
			holder = (ViewHolder) convertView.getTag();
			likeListener = (OnLikeButtonClick) convertView.getTag(holder.likeButton.getId());// 重新获得监听对象
			collectListener = (OnCollectButtonClick) convertView.getTag(holder.collectButton.getId());// 重新获得监听对象
//			talkListener = (OnTalkButtonClick) convertView.getTag(holder.talkButton.getId());// 重新获得监听对象
		    imageClick = (OnImageClickListener)convertView.getTag(holder.avatorPic.getId());
		
		}
		likeListener.setPosition(position);
		collectListener.setPosition(position);
		imageClick.setPosition(position);
//		talkListener.setPosition(position);
		// 获取该行的数据
		final Map<String, Object> obj = (Map<String, Object>) mData
				.get(position);
		String avatorUrl = (String) obj.get("userHeadPicUrl");
		String routeUrl = (String) obj.get("routePic");
		// 图片的下载
		if(!"".equals(avatorUrl)){
			avatorImageDownloader.display(holder.avatorPic, Constant.serverUrl + avatorUrl);
		}else{
			holder.avatorPic.setImageResource(R.drawable.user_icon_pic);
		}
		routeImageDownloader.display(holder.routePic, routeUrl);
		holder.routeTitle.setText((String)obj.get("title"));
		holder.userNickName.setText((String)obj.get("userName"));
		holder.routeAddress.setText((String)obj.get("kilometers") + "km "+ (String)obj.get("routeAddress"));
		holder.routeDescription.setText((String)obj.get("roadContent"));
		//点赞和收藏按钮状态
		if(((String)obj.get("likeStatus")).equals("true")){
			holder.likeButton.setSelected(true);
		}else{
			holder.likeButton.setSelected(false);
		}
		if(((String)obj.get("collectStatus")).equals("true")){
			holder.collectButton.setSelected(true);
		}else{
			holder.collectButton.setSelected(false);
		}
		//点赞和收藏数
		holder.likeCount.setText((String)obj.get("likeCount"));
		holder.talkCount.setText((String)obj.get("commentCount"));
		holder.collectCount.setText((String)obj.get("collectCount"));
		return convertView;
	}

	class OnLikeButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
		    if (!SharedPreferencesUtil.getSharedPreferences_b(mContext, Constant.ISLOGINED)) {

                AbDialogUtil.showAlertDialog(mContext, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                    @Override
                    public void onPositiveClick() {
                        // TODO Auto-generated method stub
                          Intent i = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(i);
                            mContext.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                            AbDialogUtil.removeDialog(mContext);
                    }
                    @Override
                    public void onNegativeClick() {
                        // TODO Auto-generated method stub
                        AbDialogUtil.removeDialog(mContext);
                    }
                });
                return;
            }
		    final Map<String, Object> obj = (Map<String, Object>) mData
                    .get(position);
            int likeCount = Integer.valueOf((String)obj.get("likeCount"));
            if(v.isSelected()){
                v.setSelected(false);
                likeCount --;
                if(likeCount < 0)
                    likeCount = 0;
                obj.put("likeStatus", "false");
                
            }else{
                likeCount ++;
                v.setSelected(true);
                obj.put("likeStatus", "true");
            }
            obj.put("likeCount", String.valueOf(likeCount));
            TextView likeCountText = (TextView)v.findViewById(R.id.likeCount);
            likeCountText.setText(String.valueOf(likeCount));
            
            mContext.onListViewButtonClicked(2, 0, (String)obj.get("id"));
		}
	}

	class OnCollectButtonClick implements OnClickListener {

		int position;

		public void setPosition(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
		    if (!SharedPreferencesUtil.getSharedPreferences_b(mContext, Constant.ISLOGINED)) {

                AbDialogUtil.showAlertDialog(mContext, 0, "温馨提示", "您还未登陆，或登陆状态过期，请重新登录再试",
                        new AbAlertDialogFragment.AbDialogOnClickListener() {

                    @Override
                    public void onPositiveClick() {
                        // TODO Auto-generated method stub
                          Intent i = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(i);
                            mContext.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
                            AbDialogUtil.removeDialog(mContext);
                    }
                    @Override
                    public void onNegativeClick() {
                        // TODO Auto-generated method stub
                        AbDialogUtil.removeDialog(mContext);
                    }
                });
                return;
            }
		    final Map<String, Object> obj = (Map<String, Object>) mData
                    .get(position);
            int collectCount = Integer.valueOf((String)obj.get("collectCount"));
            if(v.isSelected()){
                v.setSelected(false);
                collectCount --;
                if(collectCount < 0)
                    collectCount = 0;
                
                obj.put("collectStatus", "false");
                
            }else{
                collectCount ++;
                v.setSelected(true);
                obj.put("collectStatus", "true");
            }
            obj.put("collectCount", String.valueOf(collectCount));
            TextView collectCountText = (TextView)v.findViewById(R.id.collectCount);
            collectCountText.setText(String.valueOf(collectCount));
            
            mContext.onListViewButtonClicked(2, 1, (String)obj.get("id"));
		}
	}

//	class OnTalkButtonClick implements OnClickListener {
//
//		int position;
//
//		public void setPosition(int position) {
//			this.position = position;
//		}
//
//		@Override
//		public void onClick(View v) {
//			Log.d(TAG, String.valueOf(position));
//		}
//	}

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
//		RelativeLayout talkButton;
	}
	
	class OnImageClickListener implements OnClickListener {

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @SuppressWarnings("unchecked")
		@Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(mContext, UserPageActivity.class);
            Map<String, Object> obj = (Map<String, Object>) mData.get(position);
            String id = (String)obj.get("creatorId");
            i.putExtra("id", id);
            mContext.startActivity(i);
            mContext.overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
        }
    }

}
