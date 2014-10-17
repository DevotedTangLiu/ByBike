package com.example.bybike.friends;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.example.bybike.R;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.friends.MyLetterListView.OnTouchingLetterChangedListener;

public class FriendsActivity extends AbActivity
{

	private BaseAdapter adapter;

	private ListView listview;

	private TextView overlay;

	private MyLetterListView letterListView;

	private AsyncQueryHandler asyncQuery;

	private static final String NAME = "name", NUMBER = "number",
			SORT_KEY = "sort_key";

	private HashMap<String, Integer> alphaIndexer;

	private String[] sections;

	public List<ContentValues> list = new ArrayList<ContentValues>();

	private WindowManager windowManager;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getTitleBar().setVisibility(View.GONE);
		setContentView(R.layout.friends);
		windowManager =
				(WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		listview = (ListView) findViewById(R.id.list_view);
		letterListView = (MyLetterListView) findViewById(R.id.my_list_view);
		letterListView
				.setOnTouchingLetterChangedListener(new LetterListViewListener());

		alphaIndexer = new HashMap<String, Integer>();
		new Handler();
		new OverlayThread();
		initOverlay();
		if (list.size() > 0)
		{
		}
	}

	@SuppressWarnings("deprecation")
	public void getContent()
	{
		Cursor cur =
				getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, null);
		startManagingCursor(cur);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Uri uri = Uri.parse("content://com.android.contacts/data/phones");
		String[] projection = { "_id", "display_name", "data1", "sort_key" };
		asyncQuery.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");
	}

	private class MyAsyncQueryHandler extends AsyncQueryHandler
	{

		public MyAsyncQueryHandler(ContentResolver cr)
		{
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor)
		{

			cursor.moveToFirst();
			Log.d("ccccc",
					cursor.getString(0) + " 000 " + cursor.getString(1)
							+ " 000 " + cursor.getString(2) + " 000 "
							+ cursor.getString(3));

			while (cursor.moveToNext())
			{
				ContentValues cv = new ContentValues();
				cv.put(NAME, cursor.getString(1));
				cv.put(NUMBER, cursor.getString(2));
				cv.put(SORT_KEY, cursor.getString(3));
				list.add(cv);
			}
			if (list.size() > 0)
			{
				setAdapter(list);
			}
		}

	}

	private void setAdapter(List<ContentValues> list)
	{
		adapter = new ListAdapter(this, list);
		listview.setAdapter(adapter);

	}

	private class ListAdapter extends BaseAdapter
	{

		private LayoutInflater inflater;

		private List<ContentValues> list;
		// 图片下载器
		private AbImageDownloader mAbImageDownloader = null;
		//用户列表
		private List<UserBean> userList;

		public ListAdapter(Context context, List<ContentValues> list)
		{
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			
			// 图片下载器
			mAbImageDownloader = new AbImageDownloader(context);
	        mAbImageDownloader.setType(AbConstant.ORIGINALIMG);
			mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
			mAbImageDownloader.setErrorImage(R.drawable.image_error);
			mAbImageDownloader.setNoImage(R.drawable.image_no);
			
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++)
			{
				String currentStr = getAlpha(list.get(i).getAsString(SORT_KEY));
				String previewStr =
						(i - 1) >= 0 ? getAlpha(list.get(i - 1).getAsString(
								SORT_KEY)) : " ";
				if (!previewStr.equals(currentStr))
				{
					String name = getAlpha(list.get(i).getAsString(SORT_KEY));
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			ViewHolder holder;

			if (convertView == null)
			{
				convertView = inflater.inflate(R.layout.friends_list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.number =
						(TextView) convertView.findViewById(R.id.number);
				holder.avater = (com.example.bybike.util.CircleImageView)convertView.findViewById(R.id.imageView);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}

			ContentValues cv = list.get(position);
			holder.name.setText(cv.getAsString(NAME));
			holder.number.setText(cv.getAsString(NUMBER));
			String currentStr =
					getAlpha(list.get(position).getAsString(SORT_KEY));
			String previewStr =
					(position - 1) >= 0 ? getAlpha(list.get(position - 1)
							.getAsString(SORT_KEY)) : " ";

			if (!previewStr.equals(currentStr))
			{
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			}
			else
			{
				holder.alpha.setVisibility(View.GONE);
			}
			// 图片的下载
			mAbImageDownloader.display(holder.avater, "http://t12.baidu.com/it/u=3697398959,3070188371&fm=56");
			
			return convertView;
		}

		private class ViewHolder
		{

			TextView alpha;

			TextView name;

			TextView number;
			
			com.example.bybike.util.CircleImageView avater;
		}
	}

	private void initOverlay()
	{
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		WindowManager.LayoutParams lp =
				new WindowManager.LayoutParams(
						120,
						120,
						100,
						0,
						WindowManager.LayoutParams.TYPE_APPLICATION,
						WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
								| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
						PixelFormat.TRANSLUCENT);
		// WindowManager windowManager = (WindowManager)
		// this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s, float y, float x)
		{
			if (alphaIndexer.get(s) != null)
			{
				int position = alphaIndexer.get(s);

				listview.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);

			}
		}

		@Override
		public void onTouchingLetterEnd()
		{
			overlay.setVisibility(View.GONE);
		}
	}

	private class OverlayThread implements Runnable
	{

		@Override
		public void run()
		{
			overlay.setVisibility(View.GONE);
		}
	}

	private String getAlpha(String str)
	{
		if (str == null)
		{
			return "#";
		}

		if (str.trim().length() == 0)
		{
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches())
		{
			return (c + "").toUpperCase();
		}
		else
		{
			return "#";
		}
	}

	@Override
	protected void onDestroy()
	{
		if (windowManager != null)// 防止内存泄露
		{
			windowManager.removeView(overlay);
		}
		super.onDestroy();
	}

}
