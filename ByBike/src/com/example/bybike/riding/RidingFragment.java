package com.example.bybike.riding;

import com.ab.view.titlebar.AbTitleBar;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RidingFragment extends Fragment{
	
	private NewMainActivity mActivity = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_riding, null);

		AbTitleBar mAbTitleBar = mActivity.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		
	    return view;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (NewMainActivity) activity;
	}


	public void showTitleBar() {
		// TODO Auto-generated method stub
		if(mActivity != null){
		}
		
	}
}
