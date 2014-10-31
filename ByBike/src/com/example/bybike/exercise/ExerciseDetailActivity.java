package com.example.bybike.exercise;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ab.activity.AbActivity;
import com.example.bybike.R;
import com.example.bybike.routes.ObservableScrollView;
import com.example.bybike.routes.ObservableScrollView.ScrollViewListener;

public class ExerciseDetailActivity extends AbActivity{
	
	RelativeLayout exercisePicArea = null;
	ObservableScrollView scrollView = null;
	private LinearLayout spaceArea;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_exercise_detaill);
		getTitleBar().setVisibility(View.GONE);
		scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
		scrollView.setScrollViewListener(scrollChangedListener);
		spaceArea = (LinearLayout) findViewById(R.id.spaceArea);
		exercisePicArea = (RelativeLayout)findViewById(R.id.exercisePicArea);

	}

	/**
	 * 接受调用
	 * @param source
	 */
	public void clickHandler(View source){
		
		switch (source.getId()) {
		case R.id.goBack:
			goBack();
			break;

		default:
			break;
		}
	}
	
	private ScrollViewListener scrollChangedListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x,
				int y, int oldx, int oldy) {
			// TODO Auto-generated method stub
			if(y > 0){
				scrollView.bringToFront();
				spaceArea.setVisibility(View.VISIBLE);
			}else{
				spaceArea.setVisibility(View.INVISIBLE);
				exercisePicArea.bringToFront();
			}
		}
		
	};
	/**
	 * 退出页面
	 */
	private void goBack(){
		ExerciseDetailActivity.this.finish();
		overridePendingTransition(R.anim.fragment_in, R.anim.fragment_out);
	}
}
