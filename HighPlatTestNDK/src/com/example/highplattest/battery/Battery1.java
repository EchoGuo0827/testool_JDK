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

public class Battery1 extends UnitFragment
{
	private ReceiverTracker.BatteryReceiver mBatteryReceiver;
	private final String TESTITEM = "电池信息";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName=Battery1.class.getSimpleName();

	public void battery1()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.Battery)==false)
		{
			gui.cls_show_msg1(1, "该设备不支持本用例，长按确认键退出");
			return;
		}
		/* private & local definition */
		String batMsg="";
		
		// case1:获取电池电量，分别采用高、中、低的电池进行测试
		// case2:获取充电类型（已在sys3测试，故此不进行各种类型测试）
		// case3:电池电压值
		// case4:电池健康状况（COLD、DEAD、GOOD）
		// case5:电池状态，分别测试充电、放电状态
		// case6:电池温度
		// case7:电池技术
		// case8:电池在位默认返回true
		// 根据陈程建议，N700的电压是3-4V与USB的电压是接近的，故判断到电池不在位的时候不要显示电池状态信息
		batMsg = mBatteryReceiver.getBatHealth();
		if(batMsg!=null)
		{
			if("COLD".equals(batMsg))
			{
				gui.cls_show_msg("未装电池，获取的信息非电池信息故不显示，点击任意键继续");
			}
			else
			{
				batMsg = mBatteryReceiver.getBatMsg();
				if(gui.ShowMessageBox((batMsg+"，获取的电池信息是否正确").getBytes(), (byte) (BTN_OK | BTN_CANCEL),
						WAITMAXTIME) != BTN_OK)
				{
					gui.cls_show_msg1_record(fileName, "battery1", gKeepTimeErr, "line %d:设备获取的电池信息与手机端获取不一致(设备:%s)", Tools.getLineInfo(),batMsg);
					if(GlobalVariable.isContinue==false)
						return;
				}
			}
			if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
			{
				gui.cls_show_msg1_record(fileName, "battery1", gKeepTimeErr,batMsg);
			}
			gui.cls_show_msg1_record(fileName, "battery1", gScreenTime, "%s测试通过(请使用高、中、低电池和插入各种充电器和不插入充电器测试,均测试通过才可视为测试通过)", TESTITEM);
		}
		else
		{
			gui.cls_show_msg("未获取到电池信息,请退出后再进入本用例获取");
		}
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
