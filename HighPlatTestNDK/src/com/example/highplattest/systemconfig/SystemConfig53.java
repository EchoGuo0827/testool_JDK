package com.example.highplattest.systemconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.newland.SettingsManager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: Android系统设置相关的接口
 * file name 		: SystemConfig53.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171213 
 * directory 		: 
 * description 		: 自定义菜单键键值
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20171213	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig53 extends UnitFragment{
	private String TESTITEM = "setMenuKeyValue";
	private String fileName="SystemConfig53";
	private Gui gui = new Gui(myactivity, handler);
	private SettingsManager settings;
	private HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	private List<String> keyList = new ArrayList<String>()
			{
				private static final long serialVersionUID = 1L;
			{add("Switch Key");add("Menu Key");add("Volume_Up Key");add("Volume_Down Key");}};
	
	public void systemconfig53()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.isPhysicalBoard)==false)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig53",gScreenTime, "该产品非物理按键，不支持该用例");
			return;
		}
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N910)
		{
			gui.cls_show_msg1_record(fileName, "systemconfig53", 1, "N910海外不支持setMenuKeyValue");
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig53",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		// 添加键-值对
		hashMap.put("Switch Key", 187);
		hashMap.put("Menu Key", 82);
//		hashMap.put("Home Key", 3);// 这个键取消
//		hashMap.put("Back Key", 4); // 这个键取消
		hashMap.put("Volume_Up Key", 24);
		hashMap.put("Volume_Down Key", 25);
//		hashMap.put("Power Key", 26);// 这个键取消
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("菜单键\n0.单元测试\n1.单个按键测试\n");
			switch (nkeyIn) 
			{
			case '0':
				unitTest();
				break;
				
			case '1':
				singleKey();
				break;
			
			case ESC:
				if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.MulAuto)
					return;
				else
					unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	private void unitTest()
	{
		boolean ret1 = false,ret2=false,ret3=false,ret4=false;
		
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		
		// case1:参数异常测试，不在键值的范围内-1,2,55,255
		gui.cls_printf("参数异常测试中...".getBytes());
		if((ret1 = settings.setMenuKeyValue(-1))==true||(ret2=settings.setMenuKeyValue(2))==true||(ret3 = settings.setMenuKeyValue(55))==true||(ret4=settings.setMenuKeyValue(255))==true)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig53",gKeepTimeErr,"line %d:%s测试失败(%s,%s,%s,%s)", Tools.getLineInfo(),TESTITEM,ret1,ret2,ret3,ret4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:menu键设置为switch key、menu key、VOLUM_UP、VOLUME_DOWN，应设置成功
		for (String key:keyList) 
		{
			gui.cls_printf(("Menu键将设置为"+key).getBytes());
			if((ret1=settings.setMenuKeyValue(hashMap.get(key)))==false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig53",gKeepTimeErr,"line %d:将Menu键设置为%s失败(%s)", Tools.getLineInfo(),TESTITEM,key,ret1);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(gui.cls_show_msg("去桌面点击Menu键是否效果为%s，【确认】是，【其他】否",key)!=ENTER)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig53",gKeepTimeErr,"line %d:将Menu键设置为%s未生效", Tools.getLineInfo(),TESTITEM,key);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		// case3:重启后设置的按键应保持
		if(gui.cls_show_msg("目前Menu键设置为Volume_Down Key，重启后也应为Volume_Down Key，是否立即重启，【确认】重启，【其他】不重启")==ENTER)
			Tools.reboot(myactivity);
		gui.cls_show_msg1_record(fileName,"systemconfig53",gScreenTime, "%s测试通过，Menu键设置立即生效，只有恢复出厂设置才会变为默认状态", TESTITEM);
	}
	
	// 单个按键测试
	private void singleKey()
	{
		int nKeyIn = gui.cls_show_msg("0.Switch Key\n1.Menu Key\n2.Volume_Up Key\n3.Volume_Down Key\n");
		if(nKeyIn>='0'&&nKeyIn<='3')
		{
			settings.setMenuKeyValue(hashMap.get(keyList.get(nKeyIn-'0')));
			if(gui.cls_show_msg("Menu Key已被设置为%s，是否立即重启，【确认】重启，【其他】不重启", keyList.get(nKeyIn-'0'))==ENTER)
			{
				gui.cls_printf(("即将重启，重启后Menu键应为"+keyList.get(nKeyIn-'0')).getBytes());
				Tools.reboot(myactivity);
				
			}	
		}
		else
			gui.cls_show_msg1(1, "输入的按键值不在范围内");
		
	}
	
	@Override
	public void onTestUp() {
		settings = (SettingsManager) myactivity.getSystemService(NlContext.SETTINGS_MANAGER_SERVICE);
	}

	@Override
	public void onTestDown() {
		gui = null;
		settings = null;
	}
}
