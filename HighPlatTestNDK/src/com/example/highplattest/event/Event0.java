package com.example.highplattest.event;

import java.util.Arrays;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;

public class Event0 {
	/**
	 * 事件注册用例说明
	 * Event1：注册和退出事件监听
	 * Event2：重启事件监听，NDK_SYS_ResumeEvent(A7以上平台不支持)
	 * Event3：复位卡片，NDK_RfidPiccResetCard
	 * Event4：事件挂起，NDK_SysSuspenedEvent
	 */
	
	/**初始化打印*/
	public static void initPrint(int unSwitch)
	{
		//初始化
		JniNdk.JNI_Print_Init(unSwitch);
		//设置下划线属性
		JniNdk.JNI_Print_SetUnderLine(1);//0开，1关
		//设置灰度
		JniNdk.JNI_Print_SetGrey(3);//默认灰度3
	}
	
	public static void initPin()
	{
		
		StringBuffer str=new StringBuffer();
		
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		secKcvInfo.nCheckMode = 0;
		JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)0, (byte)1, 16, 
				ISOUtils.hex2byte("19191919191919191919191919191919"),secKcvInfo);
		//初始化
		BaseFragment.touchscreen_getnum(str);

	}
}
