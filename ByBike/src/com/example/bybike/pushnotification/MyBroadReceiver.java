
/*
 * @(#) MyBroadRecivier.java
 * @Author:tangliu(mail) 2014-12-14
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.pushnotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
  * @author tangliu(mail) 2014-12-14
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class MyBroadReceiver extends BroadcastReceiver{

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    
    /*
      * @param context
      * @param intent
      * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
      */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        
        // 开机自动启动服务
        if (intent.getAction().equals(ACTION)) {
            Intent service = new Intent(context, PushMsgRealService.class);
            service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(service);
        }
        
    }

    

}

