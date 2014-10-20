package com.example.bybike.db.model;

import java.util.List;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;

@Table(name = "local_user")
public class UserBean {

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "user_nickname")
	private String userNickName;

	// 用户密码
	@Column(name = "password")
	private String password;

	// 个性签名
	@Column(name = "signature")
	private String signature;

	// 用户城市
	@Column(name = "city")
	private String city;

	// 用户头像
	@Column(name = "pic_url")
	private String picUrl;
	
	@Column(name ="session")
	private String session;

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public int getRideDistance() {
		return rideDistance;
	}

	public void setRideDistance(int rideDistance) {
		this.rideDistance = rideDistance;
	}

	public int getCarbonReduce() {
		return carbonReduce;
	}

	public void setCarbonReduce(int carbonReduce) {
		this.carbonReduce = carbonReduce;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public List<UserBean> getFriends() {
		return friends;
	}

	public void setFriends(List<UserBean> friends) {
		this.friends = friends;
	}

	//骑行总数
	@Column(name = "ride_distance", type = "INTEGER")
	private int rideDistance;

	//碳减排量
	@Column(name = "carbon_reduce", type = "INTEGER")
	private int carbonReduce;

	//骑友数
	@Column(name = "friends_count", type = "INTEGER")
	private int friendsCount;

	// 骑友
	@Relations(name = "friends", type = "one2many", foreignKey = "user_id", action = "query_insert")
	private List<UserBean> friends;

}
