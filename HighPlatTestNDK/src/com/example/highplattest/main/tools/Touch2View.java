package com.example.highplattest.main.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.telephony.mbms.MbmsErrors.InitializationErrors;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Touch2View extends View{

private final String TAG="Eric_chen";

private Activity mActivity;

private int mScreenHeight;   //屏幕的高度

private int mScreenWidth; //屏幕的宽度

private int mGridHeight; //小格子的高度

private int mGridWidth; //小格子的宽度

private int mRowCount; //每行格子数

private int mColCount;//每列格子数

private int mRectCount;//格子总数

private int mIndex = 0;  //格子框和被触碰的位置

private Rect[] rect; //矩形

private boolean[] isTouch;  //用于标记格子是否被触碰

private int[] coordinateX;  //所有格子的X 坐标

private int[] coordinateY; //所有格子的Y 坐标

private int Remainder_Height,Remainder_Width;

private boolean isVice = false;

	public Touch2View(Activity activity) {
		super(activity);
		this.mActivity = activity;
		Init();
	}
	//初始化
	private void Init() {
		int i=0,j=0;
		getScreenHeightAndWidth();
		//适配不同屏幕
		if( mScreenHeight == 800 )
			mGridHeight = 25;
		else if( mScreenHeight == 1024 )
			mGridHeight = 20;
		else if( mScreenHeight == 1008 )
			mGridHeight = 28;
		else if( mScreenHeight == 952 )
			mGridHeight = 25;
		else
			mGridHeight = 32;
		
		if( mScreenHeight == 480 )
			mGridWidth = 24;
		else if( mScreenHeight == 600 )
			mGridWidth = 20;
		else if( mScreenHeight == 800 || mScreenHeight == 1280 )
			mGridWidth = 32;
		else
			mGridWidth = 30;
		
		mRowCount = mScreenWidth / mGridWidth + 1;

		mColCount = mScreenHeight / mGridHeight + 1;

		mRectCount = 2 * ( (mRowCount -1 ) + (mColCount - 1 ) ) - 4;
		Log.i(TAG, "mScreenHeight = " + mScreenHeight+", mScreenWidth = " + mScreenWidth+",");
		Log.i(TAG, "mGridHeight = " + mGridHeight+", mGridWidth = " + mGridWidth+",");
		Log.i(TAG, "mRowCount = " + mRowCount+", mColCount = " + mColCount+",");
		Log.i(TAG, "mRectCount = " + mRectCount);
		
		coordinateX = new int[mRowCount];
		coordinateY = new int[mColCount];

		rect = new Rect[mRectCount];
		isTouch = new boolean[mRectCount];
		
		//初始化格子均未被触碰
		for( i = 0; i < mRectCount; i++){
			isTouch[i] = false;
		}
		Remainder_Height = mScreenHeight % mGridHeight;
		Remainder_Width = mScreenWidth % mGridWidth;
		
		 //初始化所有格子的起始点X轴坐标
		for ( i = 0; i < mRowCount; i++) {
			coordinateX[i] = i * mGridWidth;
			if( i >= 2 ) {
				if ( Remainder_Width != 0) {
					coordinateX[i] += Remainder_Width;
				} 	
			}
	
		}
		 //初始化所有格子的起始点Y轴坐标
		for ( i = 0; i < mColCount; i++) {
			coordinateY[i] = i * mGridHeight;
			if( i >= mColCount - 2 ){
				if( Remainder_Height != 0 ) {
					coordinateY[i] += Remainder_Height;
				}
			}
		}

		for ( i = 0; i < mRowCount - 1; i++) {
			for (j = 0; j < mColCount - 1; j++)
			{
				if ( j == 0 || j == mColCount-2  || i==0 || i==mRowCount-2 )
					rect[mIndex++] = new Rect(coordinateX[i], coordinateY[j], coordinateX[i + 1], coordinateY[j + 1]);
			}
		}
		
		
	}
	//用于获取屏幕的宽高
	private void getScreenHeightAndWidth() {

		DisplayMetrics displayMestrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMestrics);
		mScreenHeight = displayMestrics.heightPixels;
		mScreenWidth = displayMestrics.widthPixels;

	}
	
	@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Log.d(TAG, "onDraw");	
			canvas.drawColor(Color.WHITE);
			Paint paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.STROKE);// 描边

			for (int i = 0; i < mRectCount; i++){
				//Log.v(TAG, "index="+i);
				canvas.drawRect(rect[i], paint);
			}
			
			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.FILL);// 填充
			
			for (int i = 0; i < mRectCount; i++){
				if(isTouch[i] == true){
					canvas.drawRect(rect[i], paint);
				}
			}
			
			
		}
	
	//判断是否所有格子都已全部触碰 
	private boolean allRectChange(){
		boolean isAllRectChange = false;
		int i = 0;
		for( i = 0; i < mRectCount; i++){
			if(isTouch[i]){
				continue;
			}
			else{
				break;
			}
		}
		if( i == mRectCount ){
			isAllRectChange = true;
			Log.v(TAG,"isAllRectChange = true");
		}
		return isAllRectChange;
	}
	
	@Override
		public boolean onTouchEvent(MotionEvent event) {
//		boolean isSecondScreen = false;
//		String devName = event.getDevice().getName();
//		Log.i(TAG, "devName:" + devName);
//		if ( devName != null ) {
//			isSecondScreen = devName.lastIndexOf("sec") == (devName.length() - 3);
//		}
		// 所有格子全部触碰，则退出应用
		if(allRectChange()){
			mActivity.setResult(Activity.RESULT_OK);
			mActivity.finish();
		}
		int touchX = (int)event.getX();	// 触点X坐标
		int touchY = (int)event.getY();	// 触点Y坐标

		if( touchX > mGridWidth && touchX < (mScreenWidth - mGridWidth ) && touchY > mGridHeight  && touchY < (mScreenHeight - mGridHeight )) {
			return true;
		}
		if(touchX >= mScreenWidth){
			touchX = mScreenWidth - 1;
		}
		if(touchY >= mScreenHeight){
			touchY = mScreenHeight - 1;
		}

		touchWhere(touchX, touchY);
		invalidate();
		return true;
			
		}
	
	private void touchWhere(int touchX, int touchY) {
		// TODO Auto-generated method stub
		int indexX = touchX / mGridWidth;
		int indexY = touchY / mGridHeight;
		if(indexY >= mColCount-1)
			indexY = mColCount-2;
		if(indexX >= mRowCount-1)
			indexX = mRowCount-2;

		//Log.d(TAG, "indexXY->("+indexX+","+indexY+"), ("+touchX+","+touchY+")");

		int index = 0,i,j;;

		for ( i = 0; i < mRowCount - 1; i++) {
			for (j = 0; j < mColCount - 1; j++)
			{
				if( i == indexX && j == indexY )
					break;
				if ( j == 0 || j == mColCount-2  || i==0 || i==mRowCount-2 )
					index ++;
			}
			if(j != mColCount - 1)
				break;
		}
		//Log.i(TAG, "indexXY->"+"["+index+"]");

		isTouch[index] = true;
		
	}

}
