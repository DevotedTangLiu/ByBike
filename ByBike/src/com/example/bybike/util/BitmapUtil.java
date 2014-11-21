package com.example.bybike.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtil {
	
	 public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	        int initialSize = computeInitialSampleSize( options, minSideLength, maxNumOfPixels );

	        int roundedSize;
	        if (initialSize <= 8) {
	            roundedSize = 1;
	            while (roundedSize < initialSize) {
	                roundedSize <<= 1;
	            }
	        } else {
	            roundedSize = (initialSize + 7) / 8 * 8;
	        }

	        return roundedSize;
	    }

	    public static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
	        double w = options.outWidth;
	        double h = options.outHeight;
	        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil( Math.sqrt( w * h / maxNumOfPixels ) );
	        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min( Math.floor( w / minSideLength ), Math.floor( h / minSideLength ) );
	        if (upperBound < lowerBound) {
	            // return the larger one when there is no overlapping zone.
	            return lowerBound;
	        }
	        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	            return 1;
	        } else if (minSideLength == -1) {
	            return lowerBound;
	        } else {
	            return upperBound;
	        }
	    }

	    public static int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// 源图片的高度和宽度
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;
			if (height > reqHeight || width > reqWidth) {
				// 计算出实际宽高和目标宽高的比率
				final int heightRatio = Math.round((float) height
						/ (float) reqHeight);
				final int widthRatio = Math.round((float) width / (float) reqWidth);
				// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
				// 一定都会大于等于目标的宽和高。
				inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			}
			return inSampleSize;
		}
	    
	    public static Bitmap decodeSampledBitmapFromResource(Resources res,
				int resId, int reqWidth, int reqHeight) {
			// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(res, resId, options);
			// 调用方法计算inSampleSize值
			options.inSampleSize = BitmapUtil.calculateInSampleSize(options,
					reqWidth, reqHeight);
			// 使用获取到的inSampleSize值再次解析图片
			options.inJustDecodeBounds = false;
			try {
				return BitmapFactory.decodeResource(res, resId, options);
			} catch (OutOfMemoryError err) {
				// MyLog.D( TAG, err.getMessage( ) );
			}
			return null;
		}
	    
	    public static Bitmap compressPhotoFileToBitmap(String filePath) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			// 压缩到640x640
			opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, 640 * 640);
			opts.inJustDecodeBounds = false;
			try {
				return BitmapFactory.decodeFile(filePath, opts);
			} catch (OutOfMemoryError err) {
				// MyLog.D( TAG, err.getMessage( ) );
			}
			return null;
		}
	    
	    public static Bitmap compressPhotoFileToBitmap(String filePath, int width, int height) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);
			// 压缩到640x480
			opts.inSampleSize = BitmapUtil.computeSampleSize(opts, -1, width * height);
			opts.inJustDecodeBounds = false;
			try {
				return BitmapFactory.decodeFile(filePath, opts);
			} catch (OutOfMemoryError err) {
				// MyLog.D( TAG, err.getMessage( ) );
			}
			return null;
		}
}
