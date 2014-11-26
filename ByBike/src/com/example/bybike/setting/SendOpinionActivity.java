/*
 * @(#) SendOpinionActivity.java
 * @Author:tangliu(mail) 2014-11-26
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ab.activity.AbActivity;
import com.example.bybike.R;

/**
  * @author tangliu(mail) 2014-11-26
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class SendOpinionActivity extends AbActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_opinion);
        getTitleBar().setVisibility(View.GONE);

    }

    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent i = new Intent();
        switch (v.getId()) {
        case R.id.goBack:
            this.finish();
            break;
        default:
            break;
        }
    }

}
