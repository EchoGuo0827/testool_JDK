package com.nlscan.SDL;

import android.content.Context;

/**
 * 上述几个方法是解码过程使用 <BR/>
 * 调用流程如下 <BR/>
 * 1、初始化 <BR/>
 * SoftEngine softEngine = new SoftEngine(); <BR/>
 * //设置结果回调函数 <BR/>
 * softEngine.setScanningCallback(); <BR/>
 * 2、传送图片 softEngine.StartDecode(); <BR/>
 * 3、停止解析 softEngine.StopDecode();<BR/>
 * 4、关闭解析库 softEngine.Deinit();该函数和初始化配套使用
 */
public class SoftEngine {

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public SoftEngine(Context context) {
	}

	/**
	 * 设置结果回调函数
	 * 
	 * @param scanningCallback
	 */
	public void setScanningCallback(ScanningCallback scanningCallback) {
	}

	/**
	 * 发送一帧数据解析
	 * 
	 * @param imageData yuv图片格式（来自camera的预览回调函数）
	 * @param nWidth 宽
	 * @param nHeight 高
	 * @return
	 */
	public boolean StartDecode(byte[] imageData, int nWidth, int nHeight) {
		return false;
	}

	/**
	 * 停止解析
	 * 
	 * @return
	 */
	public boolean StopDecode() {
		return false;
	}

	/**
	 * 回调接口说明
	 * 
	 * @author weiyang
	 *
	 */
	public interface ScanningCallback {
		/**
		 * 回调函数
		 * 
		 * @param eventCode
		 *            数据返回为1时，代表解析成功
		 * @param param1
		 *            暂时无用
		 * @param param2
		 *            byte数组的结果
		 * @param length
		 *            数据长度
		 */
		public void onScanningCallback(int eventCode, int param1,
				byte[] param2, int length);
	}

	/**
	 * 释放，该函数和初始化函数SoftEngine配套使用
	 * 
	 * @return
	 */
	public boolean Deinit() {
		return false;
	}

}