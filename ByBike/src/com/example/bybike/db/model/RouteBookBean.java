package com.example.bybike.db.model;

import java.util.List;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Relations;
import com.ab.db.orm.annotation.Table;

@Table(name = "route_book")
public class RouteBookBean {

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;

	@Column(name = "route_book_id")
	private String routeBookId;

	@Column(name = "route_book_author_avator")
	private String routeBookAuthorAvator;

	@Column(name = "route_book_author_name")
	private String routeBookAuthorName;

	@Column(name = "route_book_author_id")
	private String routeBookAuthorId;

	@Column(name = "route_book_title")
	private String routeBookTitle;

	@Column(name = "route_distance")
	private String routeDistance;

	@Column(name = "route_address")
	private String routeAddress;

	@Column(name = "route_book_detail")
	private String routeBookDetail;

	@Column(name = "create_date")
	private String createDate;

	@Column(name = "create_time")
	private String createTime;

	@Column(name = "like_count", type = "INTEGER")
	private int likeCount;

	@Column(name = "if_lick")
	private String ifLike;

	@Column(name = "collect_count", type = "INTEGER")
	private int collectCount;

	@Column(name = "if_collect")
	private String ifCollect;

	@Column(name = "talk_count", type = "INTEGER")
	private int talkCount;

	@Column(name = "if_talk")
	private String ifTalk;

	// 将图片列表转为字符串存储
	@Column(name = "route_pics")
	private String routePics;

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getRouteBookId() {
		return routeBookId;
	}

	public void setRouteBookId(String routeBookId) {
		this.routeBookId = routeBookId;
	}

	public String getRouteBookAuthorAvator() {
		return routeBookAuthorAvator;
	}

	public void setRouteBookAuthorAvator(String routeBookAuthorAvator) {
		this.routeBookAuthorAvator = routeBookAuthorAvator;
	}

	public String getRouteBookAuthorName() {
		return routeBookAuthorName;
	}

	public void setRouteBookAuthorName(String routeBookAuthorName) {
		this.routeBookAuthorName = routeBookAuthorName;
	}

	public String getRouteBookAuthorId() {
		return routeBookAuthorId;
	}

	public void setRouteBookAuthorId(String routeBookAuthorId) {
		this.routeBookAuthorId = routeBookAuthorId;
	}

	public String getRouteBookTitle() {
		return routeBookTitle;
	}

	public void setRouteBookTitle(String routeBookTitle) {
		this.routeBookTitle = routeBookTitle;
	}

	public String getRouteDistance() {
		return routeDistance;
	}

	public void setRouteDistance(String routeDistance) {
		this.routeDistance = routeDistance;
	}

	public String getRouteAddress() {
		return routeAddress;
	}

	public void setRouteAddress(String routeAddress) {
		this.routeAddress = routeAddress;
	}

	public String getRouteBookDetail() {
		return routeBookDetail;
	}

	public void setRouteBookDetail(String routeBookDetail) {
		this.routeBookDetail = routeBookDetail;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getIfLike() {
		return ifLike;
	}

	public void setIfLike(String ifLike) {
		this.ifLike = ifLike;
	}

	public int getCollectCount() {
		return collectCount;
	}

	public void setCollectCount(int collectCount) {
		this.collectCount = collectCount;
	}

	public String getIfCollect() {
		return ifCollect;
	}

	public void setIfCollect(String ifCollect) {
		this.ifCollect = ifCollect;
	}

	public int getTalkCount() {
		return talkCount;
	}

	public void setTalkCount(int talkCount) {
		this.talkCount = talkCount;
	}

	public String getIfTalk() {
		return ifTalk;
	}

	public void setIfTalk(String ifTalk) {
		this.ifTalk = ifTalk;
	}

	public String getRoutePics() {
		return routePics;
	}

	public void setRoutePics(String routePics) {
		this.routePics = routePics;
	}
}
