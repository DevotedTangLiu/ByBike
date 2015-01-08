package com.example.bybike.db.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;

@Table(name = "marker_info")
public class MarkerBean {

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getMarkerId() {
		return markerId;
	}

	public void setMarkerId(String markerId) {
		this.markerId = markerId;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public String getImgurl1() {
		return imgurl1;
	}

	public void setImgurl1(String imgurl1) {
		this.imgurl1 = imgurl1;
	}

	public String getImgurl2() {
		return imgurl2;
	}

	public void setImgurl2(String imgurl2) {
		this.imgurl2 = imgurl2;
	}

	public String getImgurl3() {
		return imgurl3;
	}

	public void setImgurl3(String imgurl3) {
		this.imgurl3 = imgurl3;
	}

	public String getImgurl4() {
		return imgurl4;
	}

	public void setImgurl4(String imgurl4) {
		this.imgurl4 = imgurl4;
	}

	public String getImgurl5() {
		return imgurl5;
	}

	public void setImgurl5(String imgurl5) {
		this.imgurl5 = imgurl5;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UserBean getCreater() {
		return creater;
	}

	public void setCreater(UserBean creater) {
		this.creater = creater;
	}

	public String getCreaterId() {
		return createrId;
	}

	public void setCreaterId(String createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(String likeCount) {
		this.likeCount = likeCount;
	}

	public String getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(String collectCount) {
		this.collectCount = collectCount;
	}

	@Column(name = "marker_id")
	private String markerId;

	@Column(name = "marker_type")
	private String markerType;

	@Column(name = "operating_type")
	private String operatingType;
	
	public String getOperatingType() {
		return operatingType;
	}

	public void setOperatingType(String operatingType) {
		this.operatingType = operatingType;
	}

	@Column(name = "longitude")
	private double longitude;

	@Column(name = "latitude")
	private double latitude;

	@Column(name = "marker_name")
	private String markerName;

	@Column(name = "imgurl1")
	private String imgurl1;

	@Column(name = "imgurl2")
	private String imgurl2;

	@Column(name = "imgurl3")
	private String imgurl3;

	@Column(name = "imgurl4")
	private String imgurl4;

	@Column(name = "imgurl5")
	private String imgurl5;

	@Column(name = "phone")
	private String phone;

	@Column(name = "address")
	private String address;

	// 包含实体的存储，指定外键
	@Relations(name = "creater", type = "one2one", foreignKey = "marker_id", action = "query_insert")
	private UserBean creater;

	@Column(name = "creater_id")
	private String createrId;

	@Column(name = "creater_name")
	private String createrName;

	@Column(name = "creater_time")
	private String createTime;

	@Column(name = "like_count")
	private String likeCount;

	@Column(name = "collect_count")
	private String collectCount;
	
	@Column(name = "description")
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
