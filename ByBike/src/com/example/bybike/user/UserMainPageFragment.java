/*
 * @(#) UserMainPageFragment.java
 * @Author:tangliu(mail) 2014-10-15
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.user;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.friends.FriendsActivity;
import com.example.bybike.setting.SettingMainActivity;

/**
  * @author tangliu(mail) 2014-10-15
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class UserMainPageFragment extends Fragment {

    private NewMainActivity mActivity = null;
    // http请求帮助类
    private AbHttpUtil mAbHttpUtil = null;
    // 图片下载类
    private AbImageDownloader mAbImageDownloader = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uer_mian_page, null);
        AbTitleBar mAbTitleBar = mActivity.getTitleBar();
        mAbTitleBar.setVisibility(View.GONE);

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(mActivity);
        mAbHttpUtil.setDebug(false);

        ImageView backgroundImage = (ImageView) view.findViewById(R.id.background_image);
        backgroundImage.setImageResource(R.drawable.image_loading);

        mAbImageDownloader = new AbImageDownloader(mActivity);
        mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
        mAbImageDownloader.setErrorImage(R.drawable.image_error);
        mAbImageDownloader.setNoImage(R.drawable.image_no);

        mAbImageDownloader.setAnimation(true);
        mAbImageDownloader.setType(AbConstant.ORIGINALIMG);
        mAbImageDownloader.display(backgroundImage, "http://img0.imgtn.bdimg.com/it/u=3944469639,1200934441&fm=21&gp=0.jpg");
        

        backgroundImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				Intent i = new Intent();
				i.setClass(mActivity, FriendsActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, 0);
			}
		});
        
        Button gotoSetting = (Button)view.findViewById(R.id.goToSetting);
        gotoSetting.setOnClickListener(click);
        Button gotoMessage = (Button)view.findViewById(R.id.goToMessage);
        gotoMessage.setOnClickListener(click);
        
        return view;
    }

    private OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.goToSetting:
				Intent i = new Intent();
				i.setClass(mActivity, SettingMainActivity.class);
				startActivity(i);
				mActivity.overridePendingTransition(R.anim.fragment_in, 0);
				break;

			case R.id.goToMessage:
			    createSystemNoticeMessage();
			    break;
			default:
				break;
			}
			
		}
	};
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (NewMainActivity) activity;
    }

    /**
     * 生成系统通知栏消息的例子
     */
    private void createSystemNoticeMessage(){
        //获取系统通知服务引用
        String ns = mActivity.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager)mActivity.getSystemService(ns);
        
        //自定义下拉视图
        Notification notification = new Notification();  
        notification.icon = R.drawable.me3;  
        notification.tickerText = "系统通知demo...";  
          
        RemoteViews contentView = new RemoteViews(mActivity.getPackageName(), R.layout.system_notice_layout);  
        contentView.setTextViewText(R.id.title, "Hello, this message is in a custom expanded view");
        contentView.setTextViewText(R.id.content, "从前有座山，山上有座庙，庙里有个老和尚，老和尚经常和小和尚说...");
        notification.contentView = contentView;  
        
        //通知被点击后，自动消失  
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        //使用自定义下拉视图时，不需要再调用setLatestEventInfo()方法  
        //但是必须定义 contentIntent  
        Intent intent = new Intent(mActivity, SettingMainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mActivity, 0, intent, 0);
        notification.contentIntent = contentIntent;  
          
        mNotificationManager.notify(3, notification);
    }
}
