package com.hyw.dynamic.curveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class DynamicCurveView extends SurfaceView implements Callback {
	private static final String TAG = "DynamicCurveView";
	
	private SurfaceHolder surfaceHolder;

	public DynamicCurveView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		
//		/**
//		 * 设置透明
//		 */
//		this.setZOrderOnTop(true);
//		surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
	}

	public DynamicCurveView(Context context) {
		this(context, null);
	}

	public DynamicCurveView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}
}
