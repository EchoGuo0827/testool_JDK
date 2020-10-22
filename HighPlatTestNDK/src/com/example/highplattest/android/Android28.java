package com.example.highplattest.android;

import java.io.File;
import java.math.BigDecimal;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android28.java 
 * Author 			: chencm
 * version 			: 
 * DATE 			: 20180903
 * directory 		: 
 * description 		: Android6.0可采用的存储设备
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  chencm	       20180903 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android28 extends UnitFragment{
	
	public final String TAG = Android28.class.getSimpleName();
	private final String TESTITEM = "可采用的存储设备";
	private Gui gui = new Gui(myactivity, handler);
	private DiskType diskType = DiskType.SDDSK;
	public void android28(){

		/*需要注意：手机将外置储存卡视为内部闪存芯片，手机里的应用数据将会自动地储存到外置储存卡里，
		*用户根本无需手动修改任何文件。不过，这一系列的操作也伴随着一定的风险：
		*一旦外置储存卡被「吸收」了，那么它就变成了系统的一部分了，
		*并且不可移除——这并不是说你不可以拔出内存卡，而是若你强行物理移除外置储存卡，那么设备上的应用将会陷入无限崩溃的状态。*/
		gui.cls_show_msg("请确保已插上%s,任意键继续", diskType == DiskType.SDDSK?"SD卡":diskType == DiskType.UDISK?"U盘":"TF卡");
		//case1:外置SD卡
		float total=0f;
		if((total=tfcardDetect())>0f)//是否挂载tf
		{
			if (gui.cls_show_msg("查看设置--存储中的已挂载外部SD卡，存储空间总容量为%.2fGB，[确认]是，[其他]否",total) != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr, "line %d:%s获取外部SD卡状态异常，总容量为%f", Tools.getLineInfo(), TESTITEM,total);
			}
		}else
		{
			if (gui.cls_show_msg("查看设置--存储中的未挂载外部SD卡，[确认]是，[其他]否") != ENTER) 
			{
				gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr, "line %d:%s获取外部SD卡状态异常", Tools.getLineInfo(), TESTITEM);
			}
		}
		//case2:格式化外置SD卡
		if(gui.cls_show_msg("请到设置--存储中格式化SD卡并设置为内存储设备，之后会提示“将所有数据移动过去”并且执行此步骤，[确认]是，[其他]否",total) != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr,"line%d:%测试失败",Tools.getLineInfo(),TESTITEM);
		}
		//case3:USB OTG设备安装为可采用的存储（通过USB OTG连接1TB硬盘。永远无法移除那个硬盘）
		if(gui.cls_show_msg("请用USB OTG连接硬盘，[确认]是，[其他]否",total) != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr,"line%d:%连接异常",Tools.getLineInfo(),TESTITEM);
		}
		//case4:强制通过USB OTG连接的“任何”存储设备成为可采用的存储，并执行命令
		if(gui.cls_show_msg("并执行命令 adb shell sm set-force-adoptable true 之后存储设备将无法移除，[确认]是，[其他]否",total) != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr,"line%d:%测试异常",Tools.getLineInfo(),TESTITEM);
		}
		//case5:通过USB OTG连接移除设备
		if(gui.cls_show_msg("并执行命令 adb shell sm set-force-adoptable false 之后存储设备将移除，[确认]是，[其他]否",total) != ENTER)
		{
			gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr,"line%d:%测试异常",Tools.getLineInfo(),TESTITEM);
		}
		gui.cls_show_msg1_record(TAG, "android28", gKeepTimeErr,"%s测试通过", TESTITEM);
	}
    
	// 获取TF是否挂载及大小
	public float tfcardDetect( ) {
		String tfcardPath = "/storage/sdcard1/";
		File tfcardDir = new File(tfcardPath);
		// TF卡已经挂载
		if (tfcardDir.getTotalSpace() > 0) {
			double f = (double) tfcardDir.getTotalSpace() / (1024 * 1024 * 1024);
			BigDecimal b = new BigDecimal(f);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			float f1 = b.floatValue();
			return f1;
		} else {
			return 0f;
		}
	}
     
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
