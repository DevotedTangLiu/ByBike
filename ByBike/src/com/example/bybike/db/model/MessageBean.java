package com.example.bybike.db.model;

import com.ab.db.orm.annotation.Column;
import com.ab.db.orm.annotation.Id;
import com.ab.db.orm.annotation.Table;

@Table(name = "message_info")
public class MessageBean {

	// ID @Id主键,int类型,数据库建表时此字段会设为自增长
	@Id
	@Column(name = "_id")
	private int _id;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderHeadUrl() {
		return senderHeadUrl;
	}

	public void setSenderHeadUrl(String senderHeadUrl) {
		this.senderHeadUrl = senderHeadUrl;
	}

	public String getActivityTitle() {
		return activityTitle;
	}

	public void setActivityTitle(String activityTitle) {
		this.activityTitle = activityTitle;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "message_id")
	private String messageId;

	@Column(name = "message_content")
	private String messageContent;

	@Column(name = "message_type")
	private String messageType;

	// 评论
	@Column(name = "sender_id")
	private String senderId;

	@Column(name = "sender_name")
	private String senderName;

	@Column(name = "sender_head_url")
	private String senderHeadUrl;
	
	@Column(name="comment_type")
	private String commentType;
	
	@Column(name="function_id")
    private String functionId;

	/*
     * getter method
     * @return the functionId
     */
    public String getFunctionId() {
        return functionId;
    }

    /**
     * setter method
     * @param functionId the functionId to set
     */
    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    /*
     * getter method
     * @return the commentType
     */
    public String getCommentType() {
        return commentType;
    }

    /**
     * setter method
     * @param commentType the commentType to set
     */
    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    // 活动通知
	@Column(name = "activity_title")
	private String activityTitle;

	@Column(name = "activity_id")
	private String activityId;

	@Column(name = "send_time")
	private String sendTime;

	// 好友请求
	@Column(name = "remarks")
	private String remarks;

}
