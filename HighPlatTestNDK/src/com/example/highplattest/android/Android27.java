package com.example.highplattest.android;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android27.java 
 * Author 			: chencm
 * version 			: 
 * DATE 			: 20180823
 * directory 		: Android 5.0电池使用开发者工具
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 chencm		      20180823	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android27 extends UnitFragment {
	public final String TAG = Android27.class.getSimpleName();
	private final String TESTITEM = "电池使用开发者工具";
	private Gui gui = new Gui(myactivity, handler);
	public void android27()
	{
		
		gui.cls_show_msg("请先配置电量分析工具Battery Historian并运行，完成点任意键继续");	
	    // 测试前置
	    if(gui.cls_show_msg("请将电池充满电后，开始放电到百分之十,[确认]是,[其他]否")!=ENTER)
		{
	    	gui.cls_show_msg1_record(TAG, "android27", gKeepTimeErr,"line %d:%s测试失败", Tools.getLineInfo(), TESTITEM);
			if(!GlobalVariable.isContinue)
				return;	
		}
		gui.cls_show_msg("等电池电量只剩百分之十后，导出手机Bugreport文件：adb bugreport > bugreport.txt，完成点任意键继续");
		gui.cls_show_msg("将导出的Bugreport文件，上传至http://localhost:9999，就得到整个设备电池耗电历史，完成点任意键继续");
		gui.cls_show_msg1_record(TAG, "android27",gScreenTime,"%s测试通过", TESTITEM);
		
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
	
}
