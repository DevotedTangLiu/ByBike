package com.example.bybike.routes;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.view.sliding.AbSlidingPlayView;
import com.example.bybike.R;
import com.example.bybike.routes.ObservableScrollView.ScrollViewListener;

public class RouteDetailActivity extends AbActivity {

	AbSlidingPlayView mSlidingPlayView = null;
	ObservableScrollView scrollView = null;
	private LinearLayout spaceArea;
	// 图片下载类
	private AbImageDownloader mAbImageDownloader = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setAbContentView(R.layout.activity_route_detail);

		mSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);

		// 图片的下载
		mAbImageDownloader = new AbImageDownloader(this);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setNoImage(R.drawable.image_no);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);

		for (int i = 0; i < 3; i++) {

			final View mPlayView = mInflater.inflate(R.layout.play_view_item,
					null);
			ImageView mPlayImage = (ImageView) mPlayView
					.findViewById(R.id.mPlayImage);
			mSlidingPlayView.setPageLineHorizontalGravity(Gravity.CENTER);
			mSlidingPlayView.addView(mPlayView);

			mAbImageDownloader
					.display(mPlayImage,
							"http://img0.imgtn.bdimg.com/it/u=1196327338,3394668792&fm=21&gp=0.jpg");
		}

		scrollView = (ObservableScrollView) findViewById(R.id.scrollView);
		scrollView.setScrollViewListener(scrollChangedListener);
		spaceArea = (LinearLayout) findViewById(R.id.spaceArea);

	}


	private ScrollViewListener scrollChangedListener = new ScrollViewListener() {

		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x,
				int y, int oldx, int oldy) {
			// TODO Auto-generated method stub
//			System.out.println("x:" + x + "  y:"+ y + "  oldx:" + oldx + "  oldy:" + oldy);
			if(y > 0){
				scrollView.bringToFront();
				spaceArea.setVisibility(View.VISIBLE);
			}else{
				spaceArea.setVisibility(View.INVISIBLE);
				mSlidingPlayView.bringToFront();
			}
		}
		
	};
	
}
