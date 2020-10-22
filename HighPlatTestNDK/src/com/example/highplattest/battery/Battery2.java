package com.example.highplattest.battery;

import android.content.Intent;
import android.content.IntentFilter;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.BatteryReceiver;

public class Battery2 extends UnitFragment
{
	private final String TESTITEM = "电池充电类型";
	private String fileName=Battery2.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private BatteryReceiver mBatteryReceiver;
	private String chargeType;
	private boolean chargeStatus;
	
	public void battery2()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.Battery)==false)
		{
			gui.cls_show_msg1(1, "该设备不支持本用例，长按确认键退出");
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		// case1:未插入圆孔充电器应无法接收到ACTION_POWER_CONNECTED广播，可接收到ACTION_BATTERY_CHANGED广播
		gui.cls_show_msg("请确保[未插入]充电器，完成任意键继续");
		batteryRegist();
		chargeType = mBatteryReceiver.getPlugType();
		if(gui.cls_show_msg("充电类型："+chargeType+"，是【确认】，否【取消】")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case2:插入圆孔充电器应可接收到ACTION_POWER_CONNECTED广播（只有N910支持该case）
		if(GlobalVariable.currentPlatform.toString().contains("N910"))
		{
			mBatteryReceiver.setCharge();
			gui.cls_show_msg("请确保[插入]圆孔适配器充电，等待3s后任意键继续");
			batteryRegist();
			chargeType = mBatteryReceiver.getPlugType();
			if(gui.cls_show_msg("充电类型:"+chargeType+",是否为圆孔适配器,是[确认],否[取消]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
				if (!GlobalVariable.isContinue)
					return;
			}
			chargeStatus = mBatteryReceiver.getCharge();
			if(chargeStatus==false)
			{
				gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电状态错误(%s)", Tools.getLineInfo(),chargeStatus);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		// case3.1:插入USB充电
		mBatteryReceiver.setCharge();
		gui.cls_show_msg("请确保[插入]USB适配器充电，完成任意键继续");
		chargeType = mBatteryReceiver.getPlugType();
		if(gui.cls_show_msg("充电类型:"+chargeType+",是否为AC,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
			if (!GlobalVariable.isContinue)
				return;
		}
		chargeStatus = mBatteryReceiver.getCharge();
		if(chargeStatus==false)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电状态错误(%s)", Tools.getLineInfo(),chargeStatus);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case3.2:插入USB线充电
		mBatteryReceiver.setCharge();
		gui.cls_show_msg("请确保[插入]USB线进行充电,完成任意键继续");
		chargeType = mBatteryReceiver.getPlugType();
		if(gui.cls_show_msg("充电类型:"+chargeType+",是否为USB方式充电,是[确认],否[取消]")!=ENTER)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
			if (!GlobalVariable.isContinue)
				return;
		}
		chargeStatus = mBatteryReceiver.getCharge();
		if(chargeStatus == false)
		{
			gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电状态错误(%s)", Tools.getLineInfo(),chargeStatus);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case4:蓝牙底座充电方式
		if(gui.ShowMessageBox("是否支持蓝牙底座充电".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			mBatteryReceiver.setCharge();
			gui.cls_show_msg("请确保[接入]蓝牙底座充电,完成任意键继续");
			batteryRegist();
			chargeType = mBatteryReceiver.getPlugType();
			if(gui.cls_show_msg("充电类型:"+chargeType+",是否为蓝牙底座,是[确认],否[取消]")!=ENTER)
			{
				gui.cls_show_msg1_record(fileName, "battery2", gKeepTimeErr,"line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		gui.cls_show_msg1_record(fileName, "battery2", gScreenTime,"%s测试通过",TESTITEM);
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
	public void onTestUp() 
	{
		mBatteryReceiver = new ReceiverTracker().new BatteryReceiver();
		batteryRegist();
	}

	@Override
	public void onTestDown() 
	{
		myactivity.unregisterReceiver(mBatteryReceiver);
	}
}
