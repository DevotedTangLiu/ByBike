package com.example.bybike.marker;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.image.AbImageLoader;
import com.ab.view.sliding.AbSlidingPlayView;
import com.example.bybike.R;
import com.example.bybike.util.Constant;

public class ViewPicturesActivity extends AbActivity {

	AbSlidingPlayView mAbSlidingPlayView = null;
	// 图片下载类
	private AbImageLoader mAbImageLoader = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_view_picutresl);
		getTitleBar().setVisibility(View.GONE);

		ArrayList<String> pictureUrls = getIntent().getStringArrayListExtra(
				"pictureUrls");

		mAbSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);
		mAbImageLoader = AbImageLoader.newInstance(ViewPicturesActivity.this);
		mAbImageLoader.setLoadingImage(R.drawable.image_loading);
		mAbImageLoader.setErrorImage(R.drawable.image_error);
		mAbImageLoader.setEmptyImage(R.drawable.image_empty);

		for (String url : pictureUrls) {

			final View mPlayView = mInflater.inflate(R.layout.play_view_item2,
					null);
			ImageView mPlayImage = (ImageView) mPlayView
					.findViewById(R.id.mPlayImage);
			// mAbSlidingPlayView.setPageLineHorizontalGravity(Gravity.CENTER);
			mAbSlidingPlayView.addView(mPlayView);

			mAbImageLoader.display(mPlayImage, Constant.serverUrl + url);
		}
	}

	public void clickHandler(View v) {

		switch (v.getId()) {
		case R.id.mPlayImage:
			ViewPicturesActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
