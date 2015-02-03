package com.example.bybike.db.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.example.bybike.db.DBInsideHelper;
import com.example.bybike.db.model.MarkerBean;
import com.example.bybike.db.model.MessageBean;
/**
 * 
 * Copyright (c) 2012 All rights reserved
 * 名称：MarkerBeanDao.java 
 * 描述：本地数据库 在data下面
 * @author tangliu
 * @date：2014-10-16 下午4:12:36
 * @version v1.0
 */
public class MessageBeanDao extends AbDBDaoImpl<MessageBean> {
	public MessageBeanDao(Context context) {
		super(new DBInsideHelper(context),MessageBean.class);
	}
}
