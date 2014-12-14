
/*
 * @(#) PushMsgRealService.java
 * @Author:tangliu(mail) 2014-12-14
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.pushnotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

/**
  * @author tangliu(mail) 2014-12-14
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class PushMsgRealService extends Service{
    
    boolean flag;
    String tag = "pushservice";

    
    /*
      * @param intent
      * @return
      * @see android.app.Service#onBind(android.content.Intent)
      */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        doing();
        Log.i(tag, "Service is Created");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // doing();
        Log.i(tag, "Service is Started");
        return 1;

    }
    
    /**
     * doing(这里用一句话描述这个方法的作用)
     */
   public void doing() {
       new Thread(new Runnable() {
           @Override
           public void run() {
               Looper.prepare();
               PushMsgService contact = new PushMsgService(PushMsgRealService.this);
               contact.turnOn();
               contact.contact();
               Looper.loop();
           }
       }).start();
   }
   
   
   @Override
   public void onDestroy() {
       PushMsgService contact = new PushMsgService(PushMsgRealService.this);
       contact.turnOff();
       super.onDestroy();
       Log.i(tag, "Service is Destroyed");
   }

    

}

