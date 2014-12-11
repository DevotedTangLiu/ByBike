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
	/**
	 * 服务器地址
	 */
	public static final String serverUrl = "http://115.29.209.37:8080/bybike_ser";
	/**
	 * 注册接口地址
	 */
    public static final String registerUrl = "/mc/registerByEmail";
    /**
     * 登陆接口地址
     */
    public static final String loginUrl = "/m/login";
    /**
     * 提交建议接口地址
     */
    public static final String suggestionUrl = "/m/suggestion/save";
    
    /**
     * 退出登录接口
     */
    public static final String logoutUrl = "/m/mobileLogout";
    /**
     * 修改密码接口
     */
    public static final String updatePasswordUrl = "/m/modifyPwd";
    /**
     * 更新用户资料接口
     */
    public static final String updateUserInfoUrl = "/m/editUser";
}
