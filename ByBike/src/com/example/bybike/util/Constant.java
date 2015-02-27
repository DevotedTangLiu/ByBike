package com.example.bybike.util;

public class Constant {

	public static final String SHARED_PREFERENCES_NAME = "bybike.sharedPreferences";
	 /**
     * getSharedPreferences_i的默认值
     */
    public static final int GETSHAREDPREFERENCES_I = -1;
    /**
     * getSharedPreferences_b的默认值
     */
    public static final Boolean GETSHAREDPREFERENCES_B = false;
    /**
     * 标志用户是否已经登录
     */
	public static final String ISLOGINED = "islogined";
	/**
	 * 当前session
	 */
	public static final String SESSION = "session";
	/**
	 * 用户id
	 */
	public static final String USERID = "userId";
	/**
	 * 用户账号
	 */
	public static final String USERACCOUNT = "accountNumber";
	
	/**
	 * 用户昵称
	 */
	public static final String USERNICKNAME = "userNickName";
	/**
	 * 用户密码
	 */
	public static final String USERPASSWORD = "userPassword";
	/**
	 * 用户头像地址
	 */
	public static final String USERAVATARURL = "userAvatarUrl";
	/**
	 * 第一次启动应用
	 */
	public static final String FIRSTUSED = "firstUsed";
	
	public static final String carbonCount = "carbonCount";
	
	public static final String totalDistance = "totalDistance";
	
	public static final String FRIENDSCOUNT = "friendsCount";
	
	public static final String hasLogined = "hasLogined";
	
	public static final String CHECKTIME = "checkTime";
	/**
	 * 服务器地址
	 */
	public static final String serverUrl = "http://115.29.209.37:8080";
	/**
	 * 注册接口地址
	 */
    public static final String registerUrl = "/bybike_ser/mc/registerByEmail";
    /**
     * 登陆接口地址
     */
    public static final String loginUrl = "/bybike_ser/m/login";
    /**
     * 提交建议接口地址
     */
    public static final String suggestionUrl = "/bybike_ser/m/suggestion/save";
    
    /**
     * 退出登录接口
     */
    public static final String logoutUrl = "/bybike_ser/m/mobileLogout";
    /**
     * 修改密码接口
     */
    public static final String updatePasswordUrl = "/bybike_ser/m/modifyPwd";
    /**
     * 更新用户昵称接口
     */
    public static final String updateUserInfoUrl = "/bybike_ser/m/editUser";
    /**
     * 更新用户头像接口
     */
    public static final String updateUserHeadPicUrl = "/bybike_ser/m/editHeadImg";
    /**
     * 通过session判断是否在线的url
     */
    public static final String checkIfOnLineUrl = "/bybike_ser/m/checkLogin";
    /**
     * 设置推送消息获取的间隔，单位毫秒
     */
    public static final long NOTIFICATION_PUSH_INTERVAL = 600000;
    /**
     * 消息推送接口
     */
    public static final String pushUrl = "/bybike_ser/mc/propellingList";
    /**
     * 友好点列表
     */
	public static final String getMarkerListUrl = "/bybike_ser/mc/marker/list";
	/**
	 * 友好点详情
	 */
	public static final String markerDetailUrl = "/bybike_ser/mc/marker/form";
	/**
	 *友好点点赞
	 */
	public static final String markerLikeClicked = "/bybike_ser/m/marker/updateLikeStatus";
	/**
	 * 友好点收藏
	 */
	public static final String markerCollectClicked = "/bybike_ser/m/marker/updateCollectStatus";
	/**
	 * 活动列表
	 */
	public static final String exerciseListUrl = "/bybike_ser/mc/activity/list";
	/**
	 * 活动详情
	 */
	public static final String exerciseDetailUrl = "/bybike_ser/mc/activity/form";
	/**
	 * 活动点赞、取消点赞
	 */
	public static final String exerciseLikeClicked = "/bybike_ser/m/activity/updateLikeStatus";
	/**
	 * 活动收藏、取消收藏
	 */
    public static final String exerciseCollectClicked = "/bybike_ser/m/activity/updateCollectStatus";
	
	/**
	 * 路书列表
	 */
	public static final String routeListUrl = "/bybike_ser/mc/ridingBook/list";
	/**
	 * 路书详情
	 */
	public static final String routeDetailUrl = "/bybike_ser/mc/ridingBook/form";
	/**
	 * 获取路线图
	 */
    public static final String routeLineUrl = "/bybike_ser/m/ridingBook/getPointList";
    /**
     * 保存路线图
     */
    public static final String saveRouteLineUrl = "/bybike_ser/m/ridingBook/savePoints";
	
	/**
	 * 路书点赞、取消点赞
	 */
	public static final String routeLikeClicked = "/bybike_ser/m/ridingBook/updateLikeStatus";
	/**
	 * 路书收藏、取消收藏
	 */
	public static final String routeCollectClicked = "/bybike_ser/m/ridingBook/updateCollectStatus";
	/**
	 * 添加路书
	 */
	public static final String addRouteBookUrl = "/bybike_ser/m/ridingBook/save";
	/**
	 * 路书评论列表
	 */
	public static final String routeCommentUrl = "/bybike_ser/mc/ridingBook/getDiscusslist";
	/**
	 * 添加路书评论
	 */
	public static final String addRouteCommentUrl = "/bybike_ser/m/ridingBook/discuss";
	/**
	 * 忘记密码
	 */
	public static final String forgetPasswrodUrl = "/bybike_ser/mc/forgetPwd";
	
	/**
	 * 查询活动评论列表
	 */
	public static final String getCommentList = "/bybike_ser/mc/activity/getDiscusslist";
	/**
	 * 添加活动评论Url
	 */
    public static final String addActivityCommentUrl = "/bybike_ser/m/activity/discuss";
	/**
	 * 查询友好点评论列表
	 */
	public static final String getMarkerCommentList = "/bybike_ser/mc/marker/getDiscusslist";
    /**
     * 添加标记点评论	
     */
	public static final String commentMarkerUrl = "/bybike_ser/m/marker/discuss";

	/**
	 * 活动报名Url
	 */
	public static final String applyForActivityUrl = "/bybike_ser/m/activity/joinTheActivity";
	/**
	 * 添加友好点
	 */
	public static final String addMarkerUrl = "/bybike_ser/m/marker/save";
	/**
	 * 查询用户
	 */
	public static final String findUserUrl = "/bybike_ser/m/findUser";
	/**
	 * 获取好友列表
	 */
	public static final String getFriendsListUrl = "/bybike_ser/m/getFriends";
	/**
	 * 获取私人消息
	 */
	public static final String getPrivateMessage = "/bybike_ser/m/news/getPrivateNews";
	/**
	 * 获取公共消息
	 */
	public static final String getPublicMessage = "/bybike_ser/mc/propellingList";
	/**
	 * 获取用户信息
	 */
	public static final String getUserInfoUrl = "/bybike_ser/m/getUserInfoById";
	/**
	 * 添加好友url
	 */
	public static final String addFriendUrl = "/bybike_ser/m/addFriendApply";
	/**
	 * 删除好友url
	 */
	public static final String deleteFriendUrl = "/bybike_ser/m/removeFrinend";
	
	public static final String markerDataLoaded = "markerDataLoaded";
	/**
	 * 获取最新版本
	 */
    public static final String getVersionUrl = "/bybike_ser/mc/getVersion";
    /**
     * 更新路程和减碳量
     */
	public static final String saveDistanceUrl = "/bybike_ser/m/addDistance";
	
}
