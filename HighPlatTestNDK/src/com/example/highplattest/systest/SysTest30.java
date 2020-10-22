package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.DiskType;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest30.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150416
 * directory 		: 
 * description 		: SD卡U盘/SMART交叉测试
 * related document :
 * history 		 	: 变更记录				变更时间			变更人员
 *			  		 测试前置解绑事件			20200415		郑薛晴
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest30 extends DefaultFragment
{
	private final String TAG = SysTest30.class.getSimpleName();
	private final String TESTITEM = "SMART/SD卡(U盘)";
	private _SMART_t type = _SMART_t.CPU_A;
	private List<_SMART_t> typeCradList = new ArrayList<_SMART_t>();
	private DiskType diskType = DiskType.SDDSK;
	private Gui gui = null;
	private Config config;
	private String diskString;
	private int felicaChoose=0;
	private int ret=-1;
	public void systest30()
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		// 自动测试
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			typeCradList = config.smart_config();
			diskString = config.confSDU();
			String[] diskTypes=diskString.split(",");
			for(_SMART_t cardChoose:typeCradList){
				if(GlobalVariable.sdkType==SdkType.SDK3&&cardChoose==_SMART_t.IC){
					continue;
				}
				type=cardChoose;
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				for (int i = 0; i < diskTypes.length; i++) {
					diskType=getDiskType(diskTypes[i]);
					try {
						cross_test();
					} catch (Exception e) {
						gui.cls_show_msg1_record(TAG, TAG,g_time_0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
					}
				}
			}
			return;
		}
		
		//测试程序入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("SMART/SD卡(U盘)\n0.SMART配置\n1.U盘SD卡配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				type = config.smart_config().get(0);
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(type)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
				diskType=getDiskType(config.confSDU());
				break;
				
			case '2':
				try {
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1(g_time_0, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
				}
				break;
			
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0, bak = 0, ret = 0, succ=0;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		
		/*process body*/
		//设置压力次数
		PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = packet.getLifecycle();
		//提示信息
		gui.cls_show_msg("测试前请确保，已安装"+diskType+"，放置射频卡" + type+"，完成点任意键继续");
		
		//注册事件
		if((ret=SmartRegistEvent(type))!=NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER)
		{
			gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:%s事件注册失败(%d)", Tools.getLineInfo(),type,ret);
			return;
		}
		

		while(cnt > 0)
		{
			//测试前置，保护卡
			smartDeactive(type);
			//测试退出点
			if(gui.cls_show_msg1(2, "%s/%s交叉测试，已执行%d次，成功%d次，【取消】退出测试",type,diskType,bak-cnt,succ)==ESC)
				break;
			if(GlobalVariable.sdkType==SdkType.SDK3&&type==_SMART_t.IC)
			     gui.cls_show_msg("请插拔或重新放置"+type+"卡，完成任意键继续");
			cnt--;
			// smart卡检测
			if((ret = smart_detect(type, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡检测失败（%d）",Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//smart卡激活
			if((ret = smartActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡激活失败（%d）", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			//SD卡U盘
			if((ret = systestSdCard(diskType)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s测试失败（%d）", Tools.getLineInfo(),bak-cnt,diskType,ret);
				continue;
			}
			
			//射频卡读写
			if((ret = smartApduRw(type,req,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s卡APDU失败（%d）", Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}
			
			//SD卡U盘
			if((ret = systestSdCard(diskType)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：%s测试失败（%d）", Tools.getLineInfo(),bak-cnt,diskType,ret);
				continue;
			}
			
			//射频卡下电
			if((ret = smartDeactive(type)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:%s卡关闭场失败（ %d）",Tools.getLineInfo(),bak-cnt,type,ret);
				continue;
			}

			succ++;
		}
		postEnd();
		smartDeactive(type);
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"(%s/%s)交叉测试完成，已执行次数为%d，成功为%d次", type,diskType,bak-cnt,succ);
	}
	
	public void postEnd()
	{
		//解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "postEnd", g_keeptime, "line %d:%s卡事件解绑失败(%d)",Tools.getLineInfo(),type, ret);
			return;
		}
	}
}
