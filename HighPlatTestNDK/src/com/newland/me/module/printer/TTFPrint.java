package com.newland.me.module.printer;

public class TTFPrint {
	static {
		System.loadLibrary("nlprintex");
	}
	
	/**
	 * 参数flushFlag=0时，该接口在脚本命令处理完成后即返回，打印未完全结束，此时获取打印机状态可能为打印机正在打印。
		参数flushFlag为非0时，该接口会阻塞等待打印完全结束再返回，获取打印状态应为打印机正常。
	 * */
	public static native int PrintScipt(byte[] data, int nLen,int flushFlag);
	
	/**
	* @brief 获取字符串打印的宽高度  20200818
	* @detail 
	* @param [in]   str     字符串数据
	* @param [in]   nLen    字符串数据长度
	* @param [out]  width   打印宽度
	* @param [out]  height  打印高度
	* @return
	* @li NDK_ERR 失败
	* @li NDK_OK 成功
	*/

	public static native int GetStrPrnSize(byte[] str, int strLen, int[] width, int[] height);
}
