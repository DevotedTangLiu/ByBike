/*
 * @(#) PostMessage.java
 * @Author:tangliu(mail) 2014-12-14
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.db.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.bybike.R;
import com.example.bybike.message.MessageListActivity;
import com.example.bybike.setting.SettingMainActivity;

/**
  * @author tangliu(mail) 2014-12-14
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class PostMessage {

    private Context context;
    private NotificationManager notificationManager;

    // 声明Notification对象
    private Notification notification = null;

    /**
     * 创建一个新的实例 
     * @param context
     */
    public PostMessage(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * sendMessage(这里用一句话描述这个方法的作用)
     * @param notificationID
     * @param content
     * @param flightDate
     * @param flightNumber
     */
    public void sendMessage(int notificationID, String title, String content, int messageType) {


        Intent intent = getDetailsIntent(context, content, messageType);
        // 主要设置点击通知的时显示内容的类
        // last_id需要指定，否则不能传参
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 构造Notification对象
        notification = new Notification();

        // 设置点击通知栏里的消息后自动消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        // 通知时发出的声音
        notification.defaults = Notification.DEFAULT_VIBRATE;

        // 设置通知在状态栏显示的图标
        notification.icon = R.drawable.ic_launcher;

        // 状态栏弹出的提醒
        notification.tickerText = title;

       
        RemoteViews contentView = new RemoteViews(context.getPackageName(),
                R.layout.system_notice_layout);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.content, content);
        notification.contentView = contentView;
        
        notification.contentIntent = pendingIntent;

        // 通知
        notificationManager.notify(notificationID, notification);
    }

    private Intent getDetailsIntent(Context cnt, String content, int messageType) {
        
        Intent intent = new Intent();
        intent.setClass(context, MessageListActivity.class);
        intent.putExtra("messageType", messageType);
        return intent;
    }

}
