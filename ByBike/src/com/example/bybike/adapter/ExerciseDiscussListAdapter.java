/*
 * @(#) ExerciseDiscussListAdapter.java
 * @Author:tangliu(mail) 2014-11-2
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.adapter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.image.AbImageLoader;
import com.example.bybike.R;
import com.example.bybike.user.UserPageActivity;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;

/**
  * @author tangliu(mail) 2014-11-2
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class ExerciseDiscussListAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private Context context;
    private List<ContentValues> list;
    // 图片下载器
    private AbImageLoader mAbImageLoader = null;

    public ExerciseDiscussListAdapter(Context context, List<ContentValues> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;

        // 图片下载器
        mAbImageLoader = AbImageLoader.newInstance(context);
        mAbImageLoader.setLoadingImage(R.drawable.image_loading);
        mAbImageLoader.setErrorImage(R.drawable.image_error);
        mAbImageLoader.setEmptyImage(R.drawable.image_empty);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        OnImageClickListener click = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.exercise_discuss_list_item, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.userName);
            holder.discussContent = (TextView) convertView.findViewById(R.id.discussContent);
            holder.discussTime = (TextView) convertView.findViewById(R.id.discussTime);
            holder.avater = (CircleImageView) convertView.findViewById(R.id.imageView);
            holder.huifu = (TextView)convertView.findViewById(R.id.huifu);
            holder.receivedName = (TextView)convertView.findViewById(R.id.receiverName);
            
            click = new OnImageClickListener();
            holder.avater.setOnClickListener(click);
            
            convertView.setTag(holder);
            convertView.setTag(holder.avater.getId(), click);// 对监听对象保存
            
        } else {
            holder = (ViewHolder) convertView.getTag();
            click = (OnImageClickListener) convertView.getTag(holder.avater.getId());// 重新获得监听对象
        }

        ContentValues v = this.list.get(position);
        holder.userName.setText(v.getAsString("userName"));
        holder.discussContent.setText(v.getAsString("discussContent"));
        holder.discussTime.setText(v.getAsString("discussTime"));
        if("".equals(v.getAsString("receiverName"))){
        	holder.huifu.setVisibility(View.INVISIBLE);
        	holder.receivedName.setVisibility(View.INVISIBLE);
        }else{
        	holder.huifu.setVisibility(View.VISIBLE);
        	holder.receivedName.setText(v.getAsString("receiverName"));
        	holder.receivedName.setVisibility(View.VISIBLE);
        }
        if(v.getAsString("avater").equals("")){
        	holder.avater.setBackgroundResource(R.drawable.user_icon_pic);
        }else if(v.getAsString("avater").startsWith("http")){
        	mAbImageLoader.display(holder.avater, v.getAsString("avater"));
        }else{
        	mAbImageLoader.display(holder.avater, Constant.serverUrl + v.getAsString("avater"));
        }
        return convertView;
    }

    private class ViewHolder {
        TextView userName;
        TextView discussContent;
        TextView discussTime;
        TextView huifu;
        TextView receivedName;
        com.example.bybike.util.CircleImageView avater;
    }
    
    class OnImageClickListener implements OnClickListener {

        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(context, UserPageActivity.class);
            context.startActivity(i);
        }
    }

}
