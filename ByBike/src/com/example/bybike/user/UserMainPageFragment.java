/*
 * @(#) UserMainPageFragment.java
 * @Author:tangliu(mail) 2014-10-15
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;
import com.example.bybike.friends.FriendsActivity;

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
			}
		});
        
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (NewMainActivity) activity;
    }

}
