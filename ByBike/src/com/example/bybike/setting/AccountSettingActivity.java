package com.example.bybike.setting;

import java.io.File;
import java.util.List;

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
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.example.bybike.R;
import com.example.bybike.db.dao.UserBeanDao;
import com.example.bybike.db.model.UserBean;
import com.example.bybike.user.LoginActivity;
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

        String account = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERACCOUNT);
        String nickName = SharedPreferencesUtil.getSharedPreferences_s(this, Constant.USERNICKNAME);

        TextView userAccountText = (TextView) findViewById(R.id.account);
        userAccountText.setText(account);
        userNickNameText = (EditText) findViewById(R.id.userNickName);
        userNickNameText.setText(nickName);

        userHeadImage = (CircleImageView) findViewById(R.id.userHeadImage);
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        newPassword.addTextChangedListener(new MyTextWatcher());
        repeatPassword = (EditText) findViewById(R.id.repeatPassword);
        repeatPassword.addTextChangedListener(new MyTextWatcher2());
        tips = (TextView) findViewById(R.id.tips);

        newPasswordIcon = (ImageView) findViewById(R.id.newPasswordIcon);
        repeatPasswordIcon = (ImageView) findViewById(R.id.repeatPasswordIcon);

        mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
        Button albumButton = (Button) mAvatarView.findViewById(R.id.choose_album);
        Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
        Button cancelButton = (Button) mAvatarView.findViewById(R.id.choose_cancel);

        // 初始化图片保存路径
        String photo_dir = AbFileUtil.getFullImageDownPathDir();
        if (AbStrUtil.isEmpty(photo_dir)) {
            showToast("存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }
        albumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(AbConstant.DIALOGBOTTOM);
                // 从相册中去获取
                try {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                    intent.setType("image/*");
                    startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                } catch (ActivityNotFoundException e) {
                    showToast("没有找到照片");
                }
            }

        });

        camButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(AbConstant.DIALOGBOTTOM);
                doPickPhotoAction();
            }

        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                removeDialog(AbConstant.DIALOGBOTTOM);
            }

        });

        // 获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbHttpUtil.setDebug(false);
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            showToast("未找到系统相机程序");
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
            showToast("没有可用的存储卡");
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
                headBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath, 640, 480);
                userHeadImage.setImageBitmap(headBitMap);
            } else {
                showToast("未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
            // if(D)Log.d(TAG, "将要进行裁剪的图片的路径是 = " +
            // mCurrentPhotoFile.getPath());
            String currentFilePath2 = mCurrentPhotoFile.getPath();
            if (headBitMap != null) {
                headBitMap.recycle();
            }
            headBitMap = BitmapUtil.compressPhotoFileToBitmap(currentFilePath2, 640, 480);
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
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
            showDialog(1, mAvatarView);
            break;
        case R.id.changePicText:
            showDialog(1, mAvatarView);
            break;
        default:
            break;
        }
    }

    /**
      * updateUserInfo(这里用一句话描述这个方法的作用)
      */
    private int counts = 0;
    private boolean changed = false;
    private void updateUserInfo() {
        // TODO Auto-generated method stub
        if (!NetUtil.isConnnected(this)) {
            showDialog("温馨提示", "网络不可用，请设置您的网络后重试");
            return;
        }

        String oldPsd = oldPassword.getText().toString().trim();
        // 如果修改了密码，则调用修改密码的接口
        if (oldPsd.length() > 0) {
            changed = true;
            counts ++;
            String newPsd = newPassword.getText().toString().trim();
            String repeatPsd = repeatPassword.getText().toString().trim();
            if (oldPsd.equals(newPsd)) {
                showDialog("温馨提示", "新旧密码不能相同");
                return;
            }
            if(newPsd.length() < 6 || newPsd.length() > 15){
                showDialog("温馨提示", "密码长度限制为6到15位，请重新输入");
                return;
            }
            if (!newPsd.equals(repeatPsd)) {
                showDialog("温馨提示", "两次新密码输入不一致，请重新输入");
                return;
            }
            String urlString = Constant.serverUrl + Constant.updatePasswordUrl;
            urlString += ";jsessionid=";
            urlString += SharedPreferencesUtil.getSharedPreferences_s(AccountSettingActivity.this, Constant.SESSION);
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
                    if(counts > 0){
                        showProgressDialog("正在保存，请稍后...");
                    }
                }

                // 失败，调用
                @Override
                public void onFailure(int statusCode, String content, Throwable error) {
                }

                // 完成后调用，失败，成功
                @Override
                public void onFinish() {
                    
                    counts --;
                    if(counts <= 0){
                        removeProgressDialog();
                    }
                };

            });
            
        }

        /**
         * 如果修改了头像或者修改了昵称
         */
        final String newNickName = userNickNameText.getText().toString().trim();
        if (headBitMap != null
                || !newNickName.endsWith(SharedPreferencesUtil.getSharedPreferences_s(AccountSettingActivity.this, Constant.USERNICKNAME))) {
           
            changed = true;
            counts ++;
            String picFileName;
            if(headBitMap != null){
                picFileName = System.currentTimeMillis() + ".jpg";
                headPicFile = new File(PHOTO_DIR, picFileName);
                // 压缩图片
                BitmapUtil.compressBmpToFile(headBitMap, headPicFile);
            }
            
            String urlString = Constant.serverUrl + Constant.updateUserInfoUrl;
            urlString += ";jsessionid=";
            urlString += SharedPreferencesUtil.getSharedPreferences_s(AccountSettingActivity.this, Constant.SESSION);
            AbRequestParams p = new AbRequestParams();
            p.put("file", headPicFile,"multipart/form-data");
            // 绑定参数
            mAbHttpUtil.post(urlString, p, new AbStringHttpResponseListener() {

                // 获取数据成功会调用这里
                @Override
                public void onSuccess(int statusCode, String content) {

                    processUpdateInfoResult(content, newNickName);
                };

                // 开始执行前
                @Override
                public void onStart() {
                    
                    if(counts > 0){
                        showProgressDialog("正在保存，请稍后...");
                    }
                }

                // 失败，调用
                @Override
                public void onFailure(int statusCode, String content, Throwable error) {
                }

                // 完成后调用，失败，成功
                @Override
                public void onFinish() {
                    
                    counts --;
                    if(counts <= 0){
                        removeProgressDialog();
                    }
                };

            });
            
        }
        if(!changed){
            showToast("没有修改的内容");
        }

    }

    
    /** 处理修改用户头像、昵称的返回结果
      * processUpdateInfoResult(这里用一句话描述这个方法的作用)
      * @param content
      * @param newNickName
      */
    protected void processUpdateInfoResult(String content, String newNickName) {
        // TODO Auto-generated method stub
        try {
            JSONObject responseObj = new JSONObject(content);
            String code = responseObj.getString("code");
            if ("0".equals(code)) {

                String sessionId = responseObj.getString("jsessionid");
                Button b = (Button) findViewById(R.id.saveButton);
                b.setBackgroundResource(R.drawable.accountsetting_save_button_success);

                SharedPreferencesUtil.saveSharedPreferences_s(AccountSettingActivity.this, Constant.SESSION, sessionId);
                SharedPreferencesUtil.saveSharedPreferences_s(AccountSettingActivity.this, Constant.USERNICKNAME, newNickName);

            } else {

                showDialog("温馨提示", responseObj.getString("message"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            showDialog("温馨提示", "保存失败，请稍后重试");
        }
        
    }

    /** 处理修改密码结果
      * processUpdatePsdResult(这里用一句话描述这个方法的作用)
      * @param content
      */
    protected void processUpdatePsdResult(String content) {
        // TODO Auto-generated method stub
        try {
            JSONObject responseObj = new JSONObject(content);
            String code = responseObj.getString("code");
            if ("0".equals(code)) {

                String sessionId = responseObj.getString("jsessionid");
                Button b = (Button) findViewById(R.id.saveButton);
                b.setBackgroundResource(R.drawable.accountsetting_save_button_success);

                SharedPreferencesUtil.saveSharedPreferences_s(AccountSettingActivity.this, Constant.SESSION, sessionId);
                SharedPreferencesUtil.saveSharedPreferences_s(AccountSettingActivity.this, Constant.USERPASSWORD, newPassword.getText().toString()
                        .trim());
                
                showToast("密码修改成功");

            } else {

                showDialog("温馨提示", responseObj.getString("message"));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            showDialog("温馨提示", "登陆失败，请稍后重试");
        }

    }

    /**
     * 通过TextWatcher去观察输入框中输入的内容
     */
    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            try {
                String oldPsd = oldPassword.getText().toString().trim();
                if (!"".equals(s.toString()) && s.toString().equals(oldPsd)) {
                    newPasswordIcon.setBackgroundResource(R.drawable.account_setting_error);
                    newPasswordIcon.setVisibility(View.VISIBLE);
                    tips.setVisibility(View.VISIBLE);
                } else if (s.toString().length() < 6 || s.toString().length() > 15) {
                    newPasswordIcon.setBackgroundResource(R.drawable.account_setting_error);
                    newPasswordIcon.setVisibility(View.VISIBLE);
                    tips.setVisibility(View.INVISIBLE);
                } else {
                    newPasswordIcon.setBackgroundResource(R.drawable.account_setting_ok);
                    newPasswordIcon.setVisibility(View.VISIBLE);
                    tips.setVisibility(View.INVISIBLE);
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

    /**
     * 通过TextWatcher去观察输入框中输入的内容
     */
    class MyTextWatcher2 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            try {
                String newPsd = newPassword.getText().toString().trim();
                if (s.toString().length() > 0 && !s.toString().equals(newPsd)) {
                    repeatPasswordIcon.setBackgroundResource(R.drawable.account_setting_error);
                    repeatPasswordIcon.setVisibility(View.VISIBLE);
                } else if (s.toString().length() > 0 && s.toString().endsWith(newPsd)) {
                    repeatPasswordIcon.setBackgroundResource(R.drawable.account_setting_ok);
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
