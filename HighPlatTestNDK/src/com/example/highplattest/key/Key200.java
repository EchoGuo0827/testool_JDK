package com.example.highplattest.key;

import java.util.Random;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/************************************************************************
 * module 			: Android设定APP获取KEY
 * file name 		: Key3.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170823 
 * directory 		: 
 * description 		: 模块内随机测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20170823	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Key200 extends UnitFragment{
	private final String TESTITEM = "Key模块内随机";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName="Key200";
	private Random random = new Random();
	boolean back=false;
	boolean menu=false;
	private String funcStr1,funcStr2 ;
	private int keyCode;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		myactivity.getWindow().addFlags(3);
		myactivity.getWindow().addFlags(5);
		return view;
	}
	public void key200(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)// 该用例不支持自动化测试
		{
			gui.cls_show_msg1_record(fileName,"key200",gScreenTime,"%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		gui.cls_show_msg1(gKeepTimeErr, TESTITEM+"测试中...");
		// 支持物理按键
		String KeyFunArr2[] = {"HOME","MENU","BACK"};
		Random_Test(KeyFunArr2);
		
//		 String KeyFunArr1[] = {"HOME","BACK"};
//		 Random_Test(KeyFunArr1);
	}
	public void Random_Test(String [] KeyFunArr){
		
		// 设置次数
		int succ = 0, cnt = g_RandomTime, bak = g_RandomTime;
		boolean ret=false;
		while(cnt >0)
		{
			if(gui.cls_show_msg1(gKeepTimeErr, "Key模块内随机组合测试中...\n还剩%d次(已成功%d次),按[取消]退出测试...",cnt,succ)==ESC)
				break;
			
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=KeyFunArr[random.nextInt(KeyFunArr.length)];
			}
			funcStr1 = "";
			funcStr2 = "";
			for(int i=0;i<g_RandomCycle;i++){
				if(i<10){
					funcStr1 = funcStr1 + func[i] + "-->\n";
				}else{
					funcStr2 = funcStr2 + func[i] + "-->\n";
				}
				
			}
			gui.cls_show_msg1(gKeepTimeErr,"第%d次模块内随机测试顺序为：\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gKeepTimeErr, funcStr2);
			cnt--;
			ret=false;
			
			for(int i=0;i<g_RandomCycle;i++){
				LoggerUtil.d("func[i]:"+func[i]);
				keyFuncName fname = keyFuncName.valueOf(func[i]);
				
				if(!(ret=RandomTest(fname)))
					break ;
			}
			if(ret)
				succ++;
			
		}
		gui.cls_show_msg1_record(fileName,"Random_Test",gKeepTimeErr, "Key模块内随机组合测试测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}
	
	private boolean RandomTest(keyFuncName fname) {
		boolean is =true;
		GlobalVariable.virtualKey = -1;
		back=false;
		menu=false;
		switch(fname){
		case HOME:
			gui.cls_show_msg("请点击HOME键，点击完毕后[确认]继续");
			keyCode = GlobalVariable.virtualKey;
			if(keyCode==KeyEvent.KEYCODE_HOME)
			{
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"成功点击HOME键");
			}
			else
			{
				is=false;
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"line %d:%sHome键测试失败(key = %d)", Tools.getLineInfo(),TESTITEM,keyCode);
			}
			
			break;
		case RECENTAPP:
			gui.cls_show_msg("点击RECENTAPP键,点击完毕点[确认]继续");
			keyCode = GlobalVariable.virtualKey;
			if(keyCode==KeyEvent.KEYCODE_APP_SWITCH)
			{
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"成功点击RECENTAPP键");
			}
			else
			{
				is=false;
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"line %d:%sRecentApp键测试失败(key=%d)", Tools.getLineInfo(),TESTITEM,keyCode);
			}
			
			break;
			
		case BACK:
			gui.cls_show_msg("点击BACK键,点击完毕点[确认]继续");
			keyCode = GlobalVariable.virtualKey;
			if(keyCode==KeyEvent.KEYCODE_BACK)
			{
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"成功点击BACK键");
			}else{
				is=false;
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"line %d:%s点击BACK键测试失败", Tools.getLineInfo(),TESTITEM);
			}
			break;
			
		case MENU:
			gui.cls_show_msg("点击MUNE键,点击完毕点[确认]继续");
			keyCode = KeyEvent.KEYCODE_MENU;
			if(keyCode==KeyEvent.KEYCODE_MENU){
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"成功点击MENU键");
			}else{
				is=false;
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(fileName,"RandomTest",gKeepTimeErr,"line %d:%s点击MENU键测试失败", Tools.getLineInfo(),TESTITEM);
			}
			break;
		
		default:
			break;
	}
		return is;
	}
	private enum keyFuncName {HOME,RECENTAPP,BACK,MENU}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
}
