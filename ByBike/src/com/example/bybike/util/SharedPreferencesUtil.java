package com.example.bybike.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author dell
 *
 */
public class SharedPreferencesUtil {
	/**
	 * 存储SharedPreferences_s（字符串 ）
	 * @param key
	 * @param value
	 */
	public static void saveSharedPreferences_s(Context context,String key, String value) {
		if (key == null || value == null) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(key, value);

		if (editor.commit()) {
			Log.i("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved successful.");
		} else {
			Log.e("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved fail!");
		}
	}
	
	/**
	 * 获取getSharedPreferences_s(字符串)
	 * @param key
	 * @return
	 */
	public static String getSharedPreferences_s(Context context,String key) {
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, "");
	}
	
	/**
	 * 存储SharedPreferences_b（布尔类型）
	 * @param key
	 * @param value
	 */
	public static void saveSharedPreferences_b(Context context,String key, Boolean value) {
		if (key == null || value == null) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);

		if (editor.commit()) {
			Log.i("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved successful.");
		} else {
			Log.e("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved fail!");
		}
	}
	
	/**
	 * getSharedPreferences_b(布尔类型)
	 * @param key
	 * @return
	 */
	public static Boolean getSharedPreferences_b(Context context,String key) {
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, Constant.GETSHAREDPREFERENCES_B);
	}
	
	/**
	 * 存储SharedPreferences_b（int型）
	 * @param key
	 * @param value
	 */
	public static void saveSharedPreferences_i(Context context,String key, Integer value) {
		if (key == null || value == null) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(key, value);

		if (editor.commit()) {
			Log.i("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved successful.");
		} else {
			Log.e("saveSharedPreferencesForPermanent",
					"SHARED_PREFERENCES_NAME_VARIABLES.xml saved fail!");
		}
	}
	
	/**
	 * getSharedPreferences_s(int型)
	 * @param key
	 * @return
	 */
	public static int getSharedPreferences_i(Context context,String key) {
		SharedPreferences sp = context.getSharedPreferences(
				Constant.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		return sp.getInt(key, Constant.GETSHAREDPREFERENCES_I);
	}
}
