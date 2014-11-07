package com.example.bybike;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.activity.AbActivity;

public class SearchActivity extends AbActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getTitleBar().setVisibility(View.GONE);

		final EditText searchContent = (EditText) findViewById(R.id.searchContent);
		searchContent.setImeOptions(EditorInfo.IME_ACTION_SEARCH);  
		searchContent
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEND
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//							showToast(searchContent.getText().toString());
							return true;
						}
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							showToast("what the fuck");
							return true;
						}
						return false;
					}
				});

	}

	public void onclick(View view) {

		switch (view.getId()) {
		case R.id.spaceArea:
			SearchActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
