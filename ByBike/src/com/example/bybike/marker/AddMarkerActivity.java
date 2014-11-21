package com.example.bybike.marker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.global.AbConstant;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.example.bybike.R;
import com.example.bybike.util.BitmapUtil;

public class AddMarkerActivity extends AbActivity {

	/**
	 * 高德地图相关
	 */
	private MapView mMapView;
	private AMap aMap;
	private UiSettings mUiSettings;

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

	private int currentPhotoId;
	private Bitmap[] photos = new Bitmap[4];
	private HashMap<Integer, Bitmap> photoIdAndBitMap = new HashMap<Integer, Bitmap>();
	private int[] photosIds = new int[] { R.id.photo1, R.id.photo2,
			R.id.photo3, R.id.photo4 };

	private int[] markerTypeIds = new int[] { R.id.type1, R.id.type2,
			R.id.type3, R.id.type4, R.id.type5, R.id.type6, R.id.type7,
			R.id.type8 };
	private int[] markerTypeTextIds = new int[] { R.id.type1text,
			R.id.type2text, R.id.type3text, R.id.type4text, R.id.type5text,
			R.id.type6text, R.id.type7text, R.id.type8text };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_create_marker);
		getTitleBar().setVisibility(View.GONE);
		// ===============初始化地图========================
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.onCreate(savedInstanceState);// 必须要写
		if (aMap == null) {
			aMap = mMapView.getMap();
			mUiSettings = aMap.getUiSettings();
		}
		// 设置原生缩放按钮不可用不可见
		mUiSettings.setZoomControlsEnabled(false);
		// 设置比例尺不可见
		mUiSettings.setScaleControlsEnabled(false);
		mUiSettings.setMyLocationButtonEnabled(false);
		aMap.animateCamera(CameraUpdateFactory.zoomTo(15), 100, null);
		// ===============================================

		// 初始化图片保存路径
		String photo_dir = AbFileUtil.getFullImageDownPathDir();
		if (AbStrUtil.isEmpty(photo_dir)) {
			showToast("存储卡不存在");
		} else {
			PHOTO_DIR = new File(photo_dir);
		}

		mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
		Button albumButton = (Button) mAvatarView
				.findViewById(R.id.choose_album);
		Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
		Button cancelButton = (Button) mAvatarView
				.findViewById(R.id.choose_cancel);

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
		
		/**
		 * 初始化友好点显示
		 */
		changeAlpher(1);
	}

	/**
	 * 接受调用
	 * 
	 * @param source
	 */
	public void clickHandler(View source) {

		switch (source.getId()) {
		case R.id.goBack:
			goBack();
			break;
		case R.id.photo1:
			currentPhotoId = 1;
			showDialog(1, mAvatarView);
			break;
		case R.id.photo2:
			currentPhotoId = 2;
			showDialog(1, mAvatarView);
			break;
		case R.id.photo3:
			currentPhotoId = 3;
			showDialog(1, mAvatarView);
			break;
		case R.id.photo4:
			currentPhotoId = 4;
			showDialog(1, mAvatarView);
			break;
		case R.id.type1:
			changeAlpher(1);
			break;
		case R.id.type2:
			changeAlpher(2);
			break;
		case R.id.type3:
			changeAlpher(3);
			break;
		case R.id.type4:
			changeAlpher(4);
			break;
		case R.id.type5:
			changeAlpher(5);
			break;
		case R.id.type6:
			changeAlpher(6);
			break;
		case R.id.type7:
			changeAlpher(7);
			break;
		case R.id.type8:
			changeAlpher(8);
			break;
		default:
			break;
		}
	}

	/**
	 * 修改button背景透明度
	 */
	private void changeAlpher(int target){

		for(int i = 1; i <= markerTypeIds.length; i ++){
			Button b = (Button)findViewById(markerTypeIds[i-1]);
			TextView t = (TextView)findViewById(markerTypeTextIds[i-1]);
			if(i != target){
				b.setAlpha(0.5f);
				t.setAlpha(0.5f);
			}else{
				b.setAlpha(1f);
				t.setAlpha(1f);
			}
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

	/**
	 * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA:
			Uri uri = mIntent.getData();
			String currentFilePath = getPath(uri);
			if (!AbStrUtil.isEmpty(currentFilePath)) {
				Bitmap currentBitMap = BitmapUtil.compressPhotoFileToBitmap(
						currentFilePath, 640, 480);
				ImageView image = (ImageView) findViewById(photosIds[currentPhotoId - 1]);
				image.setImageBitmap(currentBitMap);
			} else {
				showToast("未在存储卡中找到这个文件");
			}
			break;
		case CAMERA_WITH_DATA:
			// if(D)Log.d(TAG, "将要进行裁剪的图片的路径是 = " +
			// mCurrentPhotoFile.getPath());
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			Bitmap currentBitMap = BitmapUtil.compressPhotoFileToBitmap(
					currentFilePath2, 640, 480);
			ImageView image = (ImageView) findViewById(photosIds[currentPhotoId - 1]);
			image.setImageBitmap(currentBitMap);
			break;
		// case CAMERA_CROP_DATA:
		// String path = mIntent.getStringExtra("PATH");
		// // if(D)Log.d(TAG, "裁剪后得到的图片的路径是 = " + path);
		// break;
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

	/**
	 * 
	 * zoomImage(压缩图片)
	 * 
	 * @param bgimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 退出页面
	 */
	private void goBack() {
		AddMarkerActivity.this.finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出时销毁定位
		mMapView.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

}
