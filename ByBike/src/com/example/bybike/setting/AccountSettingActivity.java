package com.example.bybike.setting;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.example.bybike.R;
import com.example.bybike.marker.AddMarkerActivity;
import com.example.bybike.util.BitmapUtil;
import com.example.bybike.util.CircleImageView;
import com.example.bybike.util.Constant;
import com.example.bybike.util.NetUtil;
import com.example.bybike.util.SharedPreferencesUtil;

public class AccountSettingActivity extends AbActivity {

	private View mAvatarView = null;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	/* 用来标识请求裁剪图片后的activity */
	private static final int CAMERA_CROP_DATA = 3022;
	/* 拍照的照片存储位置 */
	private File PHOTO_DIR = null;
	// 照相机拍照得到的图片
	private File mCurrentPhotoFile;
	private String mFileName;

	// 图片下载类
	private AbImageLoader mAbImageLoader = null;
	// 头像图片
	CircleImageView userHeadImage;
	Bitmap headBitMap;
	File headPicFile;

	EditText userNickNameText;
	EditText oldPassword;
	EditText newPassword;
	EditText repeatPassword;
	ImageView newPasswordIcon;
	ImageView repeatPasswordIcon;
	TextView tips;

	// http请求帮助类
	private AbHttpUtil mAbHttpUtil = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_setting_accountsetting);
		getTitleBar().setVisibility(View.GONE);

		// 获取Http工具类
		mAbHttpUtil = AbHttpUtil.getInstance(this);

		// 设置用户账号和昵称
		String account = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.USERACCOUNT);
		String nickName = SharedPreferencesUtil.getSharedPreferences_s(this,
				Constant.USERNICKNAME);
		TextView userAccountText = (TextView) findViewById(R.id.account);
		userAccountText.setText(account);
		userNickNameText = (EditText) findViewById(R.id.userNickName);
		userNickNameText.setText(nickName);

		// 设置头像
		mAbImageLoader = AbImageLoader.newInstance(AccountSettingActivity.this);
		userHeadImage = (CircleImageView) findViewById(R.id.userHeadImage);
		String userHeadPicUrl = SharedPreferencesUtil.getSharedPreferences_s(
				AccountSettingActivity.this, Constant.USERAVATARURL);
		if (userHeadPicUrl.length() > 0) {
			mAbImageLoader.display(userHeadImage, Constant.serverUrl
					+ userHeadPicUrl);
		}
		oldPassword = (EditText) findViewById(R.id.oldPassword);
		newPassword = (EditText) findViewById(R.id.newPassword);
		newPassword.addTextChangedListener(mtw);
		repeatPassword = (EditText) findViewById(R.id.repeatPassword);
		repeatPassword.addTextChangedListener(mtw);
		tips = (TextView) findViewById(R.id.tips);

		newPasswordIcon = (ImageView) findViewById(R.id.newPasswordIcon);
		repeatPasswordIcon = (ImageView) findViewById(R.id.repeatPasswordIcon);

		// 初始化图片保存路径
		String photo_dir = AbFileUtil.getImageDownloadDir(this);
		if (AbStrUtil.isEmpty(photo_dir)) {
			AbToastUtil.showToast(AccountSettingActivity.this, "存储卡不存在");
		} else {
			PHOTO_DIR = new File(photo_dir);
		}

	}

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			AbToastUtil.showToast(AccountSettingActivity.this, "未找到系统相机程序");
		}
	}

	/**
	 * 描述：从照相机获取
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhoto();
		} else {
			AbToastUtil.showToast(AccountSettingActivity.this, "没有可用的存储卡");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			Uri uri = data.getData();
			String currentFilePath = getPath(uri);
			if (!AbStrUtil.isEmpty(currentFilePath)) {
				if (headBitMap != null) {
					headBitMap.recycle();
				}
				headBitMap = BitmapUtil.compressPhotoFileToBitmap(
						currentFilePath, 640, 480);
				userHeadImage.setImageBitmap(headBitMap);
			} else {
				AbToastUtil.showToast(AccountSettingActivity.this,
						"未在存储卡中找到这个文件");
			}
			break;
		case CAMERA_WITH_DATA:
			// if(D)Log.d(TAG, "将要进行裁剪的图片的路径是 = " +
			// mCurrentPhotoFile.getPath());
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			/**
			 * 部分手机拍照后会旋转显示
			 */
			int degree = BitmapUtil.readPictureDegree(currentFilePath2);
			AbToastUtil.showToast(AccountSettingActivity.this, "照片旋转角度：" + degree);
			if (headBitMap != null) {
				headBitMap.recycle();
			}
			headBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath2,
					640, 480);
			headBitMap = BitmapUtil.rotaingImageView(degree, headBitMap);  
			userHeadImage.setImageBitmap(headBitMap);
			break;
		default:
			break;
		}
	}

	/**
	 * 从相册得到的url转换为SD卡中图片路径
	 */
	public String getPath(Uri uri) {
		if (AbStrUtil.isEmpty(uri.getAuthority())) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		return path;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.saveButton:
			updateUserInfo();
			break;
		case R.id.goBack:
			AccountSettingActivity.this.finish();
			break;
		case R.id.userHeadImage:
			dialogInitTest();
			AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
			break;
		case R.id.changePicText:
			dialogInitTest();
			AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
			break;
		default:
			break;
		}
	}
	
	private void dialogInitTest(){
		mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
		Button albumButton = (Button) mAvatarView
				.findViewById(R.id.choose_album);
		Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
		Button cancelButton = (Button) mAvatarView
				.findViewById(R.id.choose_cancel);
		albumButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(AccountSettingActivity.this);
				// 从相册中去获取
				try {
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
					intent.setType("image/*");
					startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
				} catch (ActivityNotFoundException e) {
					AbToastUtil
							.showToast(AccountSettingActivity.this, "没有找到照片");
				}
			}

		});

		camButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(AccountSettingActivity.this);
				doPickPhotoAction();
			}

		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(AccountSettingActivity.this);
			}

		});
	}

	/**
	 * updateUserInfo(这里用一句话描述这个方法的作用)
	 */
	private int counts = 0;
	private boolean changed = false;

	private void updateUserInfo() {
		// TODO Auto-generated method stub
		if (!NetUtil.isConnnected(this)) {
			AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
					"温馨提示", "网络不可用，请设置您的网络后重试", null);
			return;
		}

		String oldPsd = oldPassword.getText().toString().trim();
		// 如果修改了密码，则调用修改密码的接口
		if (oldPsd.length() > 0) {
			changed = true;
			counts++;
			String newPsd = newPassword.getText().toString().trim();
			String repeatPsd = repeatPassword.getText().toString().trim();
			if (oldPsd.equals(newPsd)) {
				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", "新旧密码不能相同", null);
				return;
			}
			if (newPsd.length() < 6 || newPsd.length() > 15) {
				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", "密码长度限制为6到15位，请重新输入", null);
				return;
			}
			if (!newPsd.equals(repeatPsd)) {
				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", "两次新密码输入不一致，请重新输入", null);
				return;
			}
			String urlString = Constant.serverUrl + Constant.updatePasswordUrl;
			urlString += ";jsessionid=";
			urlString += SharedPreferencesUtil.getSharedPreferences_s(
					AccountSettingActivity.this, Constant.SESSION);
			AbRequestParams p = new AbRequestParams();
			p.put("newPassword", newPsd);
			p.put("oldPassword", oldPsd);
			// 绑定参数
			mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

				// 获取数据成功会调用这里
				@Override
				public void onSuccess(int statusCode, String content) {

					processUpdatePsdResult(content);
				};

				// 开始执行前
				@Override
				public void onStart() {
					if (counts > 0) {
						AbDialogUtil.showProgressDialog(
								AccountSettingActivity.this, 0, "正在保存，请稍后...");
					}
				}

				// 失败，调用
				@Override
				public void onFailure(int statusCode, String content,
						Throwable error) {
				}

				// 完成后调用，失败，成功
				@Override
				public void onFinish() {

					counts--;
					if (counts <= 0) {
						AbDialogUtil.removeDialog(AccountSettingActivity.this);
					}
				};

			});

		}

		/**
		 * 如果修改了修改了昵称
		 */
		final String newNickName = userNickNameText.getText().toString().trim();
		if (!newNickName.endsWith(SharedPreferencesUtil.getSharedPreferences_s(
				AccountSettingActivity.this, Constant.USERNICKNAME))) {

			changed = true;
			counts++;
			String urlString = Constant.serverUrl + Constant.updateUserInfoUrl;
			urlString += ";jsessionid=";
			urlString += SharedPreferencesUtil.getSharedPreferences_s(
					AccountSettingActivity.this, Constant.SESSION);
			AbRequestParams p = new AbRequestParams();
			p.put("name", newNickName, "multipart/form-data");
			// 绑定参数
			mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

				// 获取数据成功会调用这里
				@Override
				public void onSuccess(int statusCode, String content) {

					processUpdateInfoResult(content);
				};

				// 开始执行前
				@Override
				public void onStart() {

					if (counts > 0) {
						AbDialogUtil.showProgressDialog(
								AccountSettingActivity.this, 0, "正在保存，请稍后...");
					}
				}

				// 失败，调用
				@Override
				public void onFailure(int statusCode, String content,
						Throwable error) {
				}

				// 完成后调用，失败，成功
				@Override
				public void onFinish() {

					counts--;
					if (counts <= 0) {
						AbDialogUtil.removeDialog(AccountSettingActivity.this);
					}
				};

			});

		}
		/**
		 * 如果修改了头像
		 */
		if (headBitMap != null) {

			changed = true;
			counts++;
			String picFileName;
			if (headBitMap != null) {
				picFileName = System.currentTimeMillis() + ".jpg";
				headPicFile = new File(PHOTO_DIR, picFileName);
				// 压缩图片
				BitmapUtil.compressBmpToFile(headBitMap, headPicFile);
			}

			String urlString = Constant.serverUrl
					+ Constant.updateUserHeadPicUrl;
			urlString += ";jsessionid=";
			urlString += SharedPreferencesUtil.getSharedPreferences_s(
					AccountSettingActivity.this, Constant.SESSION);
			AbRequestParams p = new AbRequestParams();
			p.put("headImg", headPicFile, "multipart/form-data");
			// 绑定参数
			mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

				// 获取数据成功会调用这里
				@Override
				public void onSuccess(int statusCode, String content) {

					processUpdateHeadPicResult(content);
				};

				// 开始执行前
				@Override
				public void onStart() {

					if (counts > 0) {
						AbDialogUtil.showProgressDialog(
								AccountSettingActivity.this, 0, "正在保存，请稍后...");
					}
				}

				// 失败，调用
				@Override
				public void onFailure(int statusCode, String content,
						Throwable error) {
				}

				// 完成后调用，失败，成功
				@Override
				public void onFinish() {

					counts--;
					if (counts <= 0) {
						AbDialogUtil.removeDialog(AccountSettingActivity.this);
					}
				};

			});

		}
		if (!changed) {
			AbToastUtil.showToast(AccountSettingActivity.this, "没有修改的内容");
		}

	}

	/**
	 * 处理修改用户昵称的返回结果 processUpdateInfoResult(这里用一句话描述这个方法的作用)
	 * 
	 * @param content
	 * @param newNickName
	 */
	protected void processUpdateInfoResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if ("0".equals(code)) {

				JSONObject dataObject = responseObj.getJSONObject("data");
				String nickname = dataObject.getString("name");

				Button b = (Button) findViewById(R.id.saveButton);
				b.setBackgroundResource(R.drawable.accountsetting_save_button_success);
				SharedPreferencesUtil.saveSharedPreferences_s(
						AccountSettingActivity.this, Constant.USERNICKNAME,
						nickname);

				AbToastUtil.showToast(AccountSettingActivity.this, "保存成功");

			} else {

				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", responseObj.getString("message"), null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
					"温馨提示", "保存失败，请稍后重试", null);
		}

	}

	/**
	 * 处理修改用户头像的返回结果 processUpdateInfoResult(这里用一句话描述这个方法的作用)
	 * 
	 * @param content
	 * @param newNickName
	 */
	protected void processUpdateHeadPicResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if ("0".equals(code)) {

				JSONObject dataObject = responseObj.getJSONObject("data");
				String headUrl = dataObject.getString("headUrl");

				Button b = (Button) findViewById(R.id.saveButton);
				b.setBackgroundResource(R.drawable.accountsetting_save_button_success);
				SharedPreferencesUtil.saveSharedPreferences_s(
						AccountSettingActivity.this, Constant.USERAVATARURL,
						headUrl);
				AbToastUtil.showToast(AccountSettingActivity.this, "修改成功");
			} else {

				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", responseObj.getString("message"), null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
					"温馨提示", "保存失败，请稍后重试", null);
		}

	}

	/**
	 * 处理修改密码结果 processUpdatePsdResult(这里用一句话描述这个方法的作用)
	 * 
	 * @param content
	 */
	protected void processUpdatePsdResult(String content) {
		// TODO Auto-generated method stub
		try {
			JSONObject responseObj = new JSONObject(content);
			String code = responseObj.getString("code");
			if ("0".equals(code)) {

				Button b = (Button) findViewById(R.id.saveButton);
				b.setBackgroundResource(R.drawable.accountsetting_save_button_success);
				SharedPreferencesUtil.saveSharedPreferences_s(
						AccountSettingActivity.this, Constant.USERPASSWORD,
						newPassword.getText().toString().trim());
				AbToastUtil.showToast(AccountSettingActivity.this, "密码修改成功");

			} else {

				AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
						"温馨提示", responseObj.getString("message"), null);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbDialogUtil.showAlertDialog(AccountSettingActivity.this, 0,
					"温馨提示", "保存失败，请稍后重试", null);
		}

	}

	/**
	 * 通过TextWatcher去观察输入框中输入的内容
	 */
	private MyTextWatcher mtw = new MyTextWatcher();

	class MyTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			try {
				String oldPsd = oldPassword.getText().toString().trim();
				if (!"".equals(s.toString()) && s.toString().equals(oldPsd)) {
					newPasswordIcon
							.setBackgroundResource(R.drawable.account_setting_error);
					newPasswordIcon.setVisibility(View.VISIBLE);
					tips.setVisibility(View.VISIBLE);
				} else if (s.toString().length() < 6
						|| s.toString().length() > 15) {
					newPasswordIcon
							.setBackgroundResource(R.drawable.account_setting_error);
					newPasswordIcon.setVisibility(View.VISIBLE);
					tips.setVisibility(View.INVISIBLE);
				} else {
					newPasswordIcon
							.setBackgroundResource(R.drawable.account_setting_ok);
					newPasswordIcon.setVisibility(View.VISIBLE);
					tips.setVisibility(View.INVISIBLE);
				}

				String newPsd = newPassword.getText().toString().trim();
				if (s.toString().length() > 0 && !s.toString().equals(newPsd)) {
					repeatPasswordIcon
							.setBackgroundResource(R.drawable.account_setting_error);
					repeatPasswordIcon.setVisibility(View.VISIBLE);
				} else if (s.toString().length() > 0
						&& s.toString().endsWith(newPsd)) {
					repeatPasswordIcon
							.setBackgroundResource(R.drawable.account_setting_ok);
					repeatPasswordIcon.setVisibility(View.VISIBLE);
				} else {
					repeatPasswordIcon.setVisibility(View.INVISIBLE);
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	}

}
