package com.hyw.dynamic.curveview;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
/**
 * 
 * @Description: 使用surfaceview绘制动态曲线图，可以达到较高的fps而不闪屏
 * @author "Hyw"
 * @GitHub https://github.com/shallots
 * @date 2015-4-1 下午2:57:33
 */
public class CurveDataRefreshThread extends Thread {
	private static final String TAG = "CurveDataRefreshThread";
	private static final int RECVDATA = 100;

	private DynamicCurveView dynamicCurveView = null;
	private ViewHandler viewHandler = null;
	private Looper looper = null;

	private int viewHeight;
	private int viewWidth;
	private int baseLine;

	private int MAXSIZEOFDATA = 200;
	private int index = 0;
	private Bitmap bm1;

	private boolean initFlag = false;
	private int fpsCount = 0;
	private long fpsTimestamp = 0;
	private String fpsString ="";
	
	private List<DataPoint> dataList;

	public CurveDataRefreshThread(DynamicCurveView dynamicCurveView) {
		super();
		if (dynamicCurveView == null)
			throw new NullPointerException("DynamicCurveView can not be null.");
		this.dynamicCurveView = dynamicCurveView;
		viewHeight = this.dynamicCurveView.getHeight();
		viewWidth = this.dynamicCurveView.getWidth();
		baseLine = this.dynamicCurveView.getHeight() / 2;

		bm1 = Bitmap.createBitmap(viewWidth, viewHeight, Config.ARGB_8888);

		dataList = new ArrayList<DataPoint>(MAXSIZEOFDATA);
		initFlag = true;
	}

	@Override
	public void run() {
		if (!initFlag)
			return;
		Looper.prepare();
		fpsTimestamp = System.currentTimeMillis();
		looper = Looper.myLooper();
		Log.i(TAG, "thread run.");
		viewHandler = new ViewHandler();
		Looper.loop();
	}

	public void exit() {
		if (looper != null)
			looper.quit();
		initFlag = false;
	}

	public ViewHandler getViewHandler() {
		return viewHandler;
	}

	private void refreshCurve() {
		fpsCount++;
		Paint mBGPaint = new Paint();
		mBGPaint.setColor(Color.BLACK);
		Canvas canvas = new Canvas(bm1);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBGPaint); // draw
		Paint mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStrokeWidth(4);

		int start = (index - 1 + MAXSIZEOFDATA) % MAXSIZEOFDATA;
		int end = index;
		float ty = baseLine;
		float pointX = 0;
		float drawStep = (float) (viewWidth * 1.0 / (float) MAXSIZEOFDATA); // it
		int count = 0;
		while (start != end) {

			DataPoint dp;
			try {
				dp = dataList.get(start);
			} catch (Exception e) {
				break;
			}

			canvas.drawLine(pointX, ty, pointX + drawStep,
					baseLine + dp.getY(), mPaint);
			ty = baseLine + dp.getY();

			pointX += drawStep;
			start = (start - 1 + MAXSIZEOFDATA) % MAXSIZEOFDATA;
			count++;
		}
		long detaT = System.currentTimeMillis() - fpsTimestamp;
		if(detaT > 1000){
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("帧率: ");
			stringBuilder.append(fpsCount);
			stringBuilder.append(" fps.");
			fpsString = stringBuilder.toString();
			fpsCount = 0;
			fpsTimestamp = System.currentTimeMillis();
		}
		mPaint.setTextSize(22);
		mPaint.setColor(Color.WHITE);
		canvas.drawText(fpsString,20,30,mPaint);
		
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();

		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		Canvas viewRender = null;
		try {
			viewRender = dynamicCurveView.getHolder().lockCanvas();
			if (viewRender != null) {
				viewRender.drawBitmap(bm1, 0, 0, null);
			}
		} finally {
			if (viewRender != null)
				dynamicCurveView.getHolder().unlockCanvasAndPost(viewRender);
		}
	}

	@SuppressLint("HandlerLeak")
	public class ViewHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case RECVDATA:
				DataPoint dataX = (DataPoint) msg.obj;
				if(dataList.size()==MAXSIZEOFDATA){
					dataList.set(index, dataX);
				}else{
					dataList.add(index, dataX);
				}
				index = (index + 1) % MAXSIZEOFDATA;
				refreshCurve();
				break;

			default:
				break;
			}
		}

	}

}
