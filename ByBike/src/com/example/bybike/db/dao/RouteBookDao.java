package com.example.bybike.db.dao;

import android.content.Context;

import com.ab.db.orm.dao.AbDBDaoImpl;
import com.example.bybike.db.DBInsideHelper;
import com.example.bybike.db.model.RouteBookBean;

/**
 * 
 * @author tangliu
 *
 */
public class RouteBookDao extends AbDBDaoImpl<RouteBookBean> {
	public RouteBookDao(Context context) {
		super(new DBInsideHelper(context),RouteBookBean.class);
	}
}
