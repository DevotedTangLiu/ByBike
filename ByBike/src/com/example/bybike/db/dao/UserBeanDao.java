package com.example.bybike.db.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.example.bybike.db.DBInsideHelper;
import com.example.bybike.db.model.UserBean;
/**
 * 
 * Copyright (c) 2012 All rights reserved
 * 名称：UserBeanDao.java 
 * 描述：本地数据库 在data下面
 * @author tangliu
 * @date：2014-10-16 下午4:12:36
 * @version v1.0
 */
public class UserBeanDao extends AbDBDaoImpl<UserBean> {
	public UserBeanDao(Context context) {
		super(new DBInsideHelper(context),UserBean.class);
	}
}
