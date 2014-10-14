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
	public static final String serverUrl = "";
	/**
	 * 注册接口地址
	 */
    public static final String registerUrl = "/users/register/email";
    /**
     * 登陆接口地址
     */
    public static final String loginUrl = "/users/login";
}
