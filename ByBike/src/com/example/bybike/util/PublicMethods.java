
/*
 * @(#) PublicMethods.java
 * @Author:tangliu(mail) 2014-11-2
 * @Copyright (c) 2002-2014 Travelsky Limited. All rights reserved.
 */
package com.example.bybike.util;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
  * @author tangliu(mail) 2014-11-2
  * @version 1.0
  * @modifyed by tangliu(mail) description
  * @Function 类功能说明
  */
public class PublicMethods {

    /**
     * 动态设置ListView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) { 
    	
    	ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        
    }

}

