/*
 * @(#) MainMemuFragment.java
 * @Author:tangliu(mail) 2014-9-18
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

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

		
		return view;
	}

	private class OnItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}

	}
}
