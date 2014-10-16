package com.example.bybike.db.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "exercise")
public class ExerciseBean {

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;
	
	@Column(name = "exercise_id")
	private String exerciseId;

	@Column(name = "pic_url")
	private String picUrl;

	@Column(name = "exercise_name")
	private String exerciseName;

	@Column(name = "exercise_time")
	private String exerciseTime;

	@Column(name = "exercise_address")
	private String exerciseAddress;
	
	@Column(name = "participate_count", type = "INTEGER")
	private int participateCount;
	
	@Column(name = "if_participate")
	private String ifParticipate;
	
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

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getExerciseId() {
		return exerciseId;
	}

	public void setExerciseId(String exerciseId) {
		this.exerciseId = exerciseId;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getExerciseName() {
		return exerciseName;
	}

	public void setExerciseName(String exerciseName) {
		this.exerciseName = exerciseName;
	}

	public String getExerciseTime() {
		return exerciseTime;
	}

	public void setExerciseTime(String exerciseTime) {
		this.exerciseTime = exerciseTime;
	}

	public String getExerciseAddress() {
		return exerciseAddress;
	}

	public void setExerciseAddress(String exerciseAddress) {
		this.exerciseAddress = exerciseAddress;
	}

	public int getParticipateCount() {
		return participateCount;
	}

	public void setParticipateCount(int participateCount) {
		this.participateCount = participateCount;
	}

	public String getIfParticipate() {
		return ifParticipate;
	}

	public void setIfParticipate(String ifParticipate) {
		this.ifParticipate = ifParticipate;
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

}
