package com.example.bybike.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.task.AbTaskQueue;
import com.example.bybike.NewMainActivity;
import com.example.bybike.R;

public class WelcomeActivity extends Activity{

	private AbTaskQueue mAbTaskQueue = null;

	ImageView welcome_background;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		mAbTaskQueue = AbTaskQueue.getInstance();
	}

	@Override
	public void onStart() {

		super.onStart();
		final AbTaskItem item1 = new AbTaskItem();
		item1.listener = new AbTaskListener() {

			@Override
			public void update() {

				goToMainActivity();
			}

			@Override
			public void get() {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			};
		};

		mAbTaskQueue.execute(item1);

	}

	private void goToMainActivity() {

		Intent intent = new Intent();
		intent.setClass(this, NewMainActivity.class);
		startActivity(intent);
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(welcome_background!=null){
			welcome_background.setImageBitmap(null);
		}
		System.gc();
	}
}
