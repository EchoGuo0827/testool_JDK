package com.example.highplattest.battery;

import java.io.IOException;

import android.content.Intent;
import android.content.IntentFilter;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 电池模块
 * file name 		: Battery3.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171110 
 * directory 		: 
 * description 		: 获取纽扣电池电压（纽扣电池电压暂时不测，因为硬件电路都未接）
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20171110	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Battery3 extends UnitFragment
{
	private final String TESTITEM = "获取锂电池和纽扣电池电压";
	private String fileName=Battery3.class.getSimpleName();
	private String mStrButPath;// 读取电池电压的路径
	private String mStrButVolDev;// 读取电池电压的节点
	private Gui gui = new Gui(myactivity, handler);
	private ReceiverTracker.BatteryReceiver mBatteryReceiver;
	
	public void battery3()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.Battery))
		{
			switch (GlobalVariable.gCurPlatVer) {
			// A7以上，电池路径统一修改为/sys/class/button_battery/butvoltage
			case A7:
			case A9:
				mStrButPath = "/sys/class/button_battery/";
				mStrButVolDev = "/sys/class/button_battery/butvoltage";
				break;
			
			default:
				// 第二层判断
				switch (GlobalVariable.currentPlatform) {
				default:
				mStrButPath = "/sys/class/sy6982_charger/";
				mStrButVolDev = "/sys/class/sy6982_charger/butvoltage";
				break;
				
			case N700:
			case N550:
				mStrButPath = "/sys/class/button_battery/";
				mStrButVolDev = "/sys/class/button_battery/butvoltage";
				break;
				}
				break;
			}
		}
		else
		{
			gui.cls_show_msg1(1, "该设备不支持本用例，长按确认键退出");
			return;
		}

		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			testOne();
			gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr,"%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
			return;
		}
		while(true)
		{
			/**纽扣电池电压可暂不获取，硬件未接 by 20200326 陈镇江*/
			int nkeyIn = gui.cls_show_msg("测试项\n0.配置节点\n2.获取电池电压\n");/**1.单元测试\n*/
			switch (nkeyIn) 
			{
			case '0':
				testConfig();
				break;
			
			/**此为测试锂电池和纽扣电池切换的，暂不测试*/
			/*case '1':
				testOne();
				break;*/
				
			case '2':
				testTwo();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
	}
	
	/**
	 * 节点配置
	 */
	private void testConfig()
	{
		int nkeyIn = gui.cls_show_msg("配置\n0.获取锂电池电压\n1.获取纽扣电池电压(硬件未接，暂不支持)\n");
		switch (nkeyIn) {
		case '0':
			try {
				LinuxCmd.execCmd(mStrButPath, "echo "+(nkeyIn-48)+" > "+mStrButVolDev);
			} catch (IOException e) {
				gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr, "line %d:配置节点失败：%s", Tools.getLineInfo(),e.getMessage());
				e.printStackTrace();
				return;
			}
			gui.cls_show_msg1(30, "配置锂电池成功");
			break;
		case '1':
			gui.cls_show_msg1(2, "暂不支持获取纽扣电池电压");
			/*try {
				LinuxCmd.execCmd(mStrButPath, "echo "+(nkeyIn-48)+" > "+mStrButVolDev);
			} catch (IOException e) {
				gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr, "line %d:配置节点失败：%s", Tools.getLineInfo(),e.getMessage());
				e.printStackTrace();
				return;
			}
			gui.cls_show_msg1(30, "配置纽扣成功");*/
			break;

		default:
			break;
		}
	}
	
	private void testOne()
	{
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		// case1：节点切换到0，读取锂电池的电压
		try {
			LinuxCmd.execCmd(mStrButPath, "echo 0 > "+mStrButVolDev);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String volLi1 = mBatteryReceiver.getBatVol()+"";
		if(gui.ShowMessageBox(("获取到的锂电池电压："+volLi1+"mV，锂电池电压是否正确").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr, "line %d:设备获取的锂电池电压错误（电压：%s）", Tools.getLineInfo(),volLi1);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:节点切换到1，读取纽扣电池的电压
		try {
			LinuxCmd.execCmd(mStrButPath, "echo 1 > "+mStrButVolDev);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String volButton = LinuxCmd.readDevNode(mStrButVolDev).trim();
		if(gui.ShowMessageBox(("获取的纽扣电池电压为："+volButton+"mV，纽扣电池电压是否正确（纽扣电池电压范围为2.8V-3.3V，电压为1.1V会安全触发）").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr, "line %d:设备获取的纽扣电池电压错误（电压：%s）", Tools.getLineInfo(),volButton);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg("获取的纽扣电池电压为：%smV（纽扣电池电压范围为2.8V-3.3V，电压为1.1V会安全触发）", volButton);
		String volLi2 = mBatteryReceiver.getBatVol()+"";
		if(gui.ShowMessageBox(("获取到的锂电池电压："+volLi2+"mV，锂电池电压是否正确").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
				WAITMAXTIME) != BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "battery3", gKeepTimeErr, "line %d:设备获取的锂电池电压错误（电压：%s）", Tools.getLineInfo(),volLi2);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 测试后置:节点要切换为锂电池，不然锂电池的电压不会变化
		try {
			LinuxCmd.execCmd(mStrButPath, "echo 0 > "+mStrButVolDev);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "battery3", gScreenTime,"节点为读取锂电池电压时，锂电池电压："+volLi1+"mv");
			gui.cls_show_msg1_record(fileName, "battery3", gScreenTime,"节点为读取纽扣电池电压时，纽扣电池电压："+volButton+"mv");
			gui.cls_show_msg1_record(fileName, "battery3", gScreenTime,"节点为读取纽扣电池电压时，锂电池电压："+volLi2+"mv");
		}
		gui.cls_show_msg1(30, "%s测试通过，最后测试完要切换为锂电池否则锂电池电压不会变化（请使用高、中、低电池、安全触发等情况下测试，均测试通过才可视为测试通过）", TESTITEM);
	}
	
	/**
	 * 获取节点电压值
	 */
	private void testTwo()
	{
		/**String volButton = LinuxCmd.readDevNode(mStrButVolDev);纽扣电池硬件未接暂时不测试*/
		String volLi = mBatteryReceiver.getBatVol()+"";
		gui.cls_show_msg("获取锂电池电压值%smV",volLi);
//		gui.cls_show_msg("获取纽扣电池电压值%smV（纽扣电池电压范围为2.8V-3.3V，电压为1.1V会安全触发），锂电池电压为%smV", volButton,volLi);
		
	}
	
	
	/**
	 * 电池广播注册
	 */
	public void batteryRegist()
	{
		IntentFilter intent1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		IntentFilter intent2 = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
		myactivity.registerReceiver(mBatteryReceiver, intent1);
		myactivity.registerReceiver(mBatteryReceiver, intent2);
	}
	
	@Override
	public void onTestUp() {
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		batteryRegist();
	}

	@Override
	public void onTestDown() {
		gui = null;
		myactivity.unregisterReceiver(mBatteryReceiver);
	}
	
}
