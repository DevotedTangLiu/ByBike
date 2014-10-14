/*
 * @(#) MainMemuFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.bybike.user.LoginActivity;

/**
 * @author tangliu(mail) 2014-9-18
 * @version 1.0
 * @modifyed by tangliu(mail) description
 * @Function 类功能说明
 */
public class MainMemuFragment extends Fragment {

	private NewMainActivity mActivity = null;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (NewMainActivity) this.getActivity();
		View view = inflater.inflate(R.layout.fragment_menu, null);

		LinearLayout userArea = (LinearLayout) view.findViewById(R.id.userArea);
		userArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mActivity, LoginActivity.class);
				startActivity(intent);
			}
		});

		OnItemClickListener l = new OnItemClickListener();
		LinearLayout exercise = (LinearLayout) view.findViewById(R.id.exercise);
		exercise.setOnClickListener(l);
		LinearLayout setting = (LinearLayout) view.findViewById(R.id.settingArea);
		setting.setOnClickListener(l);
		LinearLayout riding = (LinearLayout) view.findViewById(R.id.ridingArea);
		riding.setOnClickListener(l);
		
		return view;
	}

	private class OnItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch (v.getId()) {

			case R.id.ridingArea:
				mActivity.changeMainFragment(1);
				break;
			case R.id.settingArea:
				mActivity.changeMainFragment(0);
				break;
			default:
				return;
			}
		}

	}
}
