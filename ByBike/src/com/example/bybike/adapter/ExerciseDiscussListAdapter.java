/*
 * @(#) ExerciseDiscussListAdapter.java
 * @Author:tangliu(mail) 2014-11-2
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.adapter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.example.bybike.R;
import com.example.bybike.util.CircleImageView;

/**
  * @author tangliu(mail) 2014-11-2
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class ExerciseDiscussListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<ContentValues> list;
    // 图片下载器
    private AbImageDownloader mAbImageDownloader = null;

    public ExerciseDiscussListAdapter(Context context, List<ContentValues> list) {
        this.inflater = LayoutInflater.from(context);
        this.list = list;

        // 图片下载器
        mAbImageDownloader = new AbImageDownloader(context);
        mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
        mAbImageDownloader.setErrorImage(R.drawable.image_error);
        mAbImageDownloader.setNoImage(R.drawable.image_no);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    int i = 1;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        Log.i("", String.valueOf(i++));
        Log.i("", "position:"+String.valueOf(position));
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.exercise_discuss_list_item, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.discussContent = (TextView) convertView.findViewById(R.id.discussContent);
            holder.discussTime = (TextView) convertView.findViewById(R.id.discussTime);
            holder.avater = (CircleImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContentValues v = this.list.get(position);
        holder.userName.setText(v.getAsString("userName"));
        holder.discussContent.setText(v.getAsString("discussContent"));
        holder.discussTime.setText(v.getAsString("discussTime"));
        mAbImageDownloader.display(holder.avater, v.getAsString("avater"));
        
        return convertView;
    }

    private class ViewHolder {
        TextView userName;
        TextView discussContent;
        TextView discussTime;
        com.example.bybike.util.CircleImageView avater;
    }

}
