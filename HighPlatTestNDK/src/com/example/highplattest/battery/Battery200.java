package com.example.highplattest.battery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.ReceiverTracker;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.ReceiverTracker.BatteryReceiver;
import android.content.Intent;
import android.content.IntentFilter;
/************************************************************************
 * module 			: 电池模块
 * file name 		: Battery3.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170825 
 * directory 		: 
 * description 		: 模块内随机测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20170825	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/

public class Battery200 extends UnitFragment{
	private ReceiverTracker.BatteryReceiver mBatteryReceiver;
	private final String TESTITEM = "电池模块内随机";
	private String fileName=Battery200.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private String BatteryFunArr[] = {"getBatMag","getPlugType","getCharge","getBatVol","readDevNode"};
	private boolean chargeFlag;
	private boolean getPlugType=false;
	private String volButton ,volLi;
	private String mStrButPath;// 读取电池电压的路径
	private String mStrButVolDev;// 读取电池电压的节点
	private Random random = new Random();
	
	List <String> type=new ArrayList<String>(Arrays.asList("【未插入】适配器","【插入】圆孔适配器","【插入】USB适配器","【插入】USB线","【接入】蓝牙底座"));//适配器包括圆孔适配器和USB适配器//"未插入适配器","【插入】适配器","【插入】USB线","【接入】蓝牙底座"
	int id=-1;
	private String funcStr1,funcStr2;

	public void battery200(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			type.remove("【插入】圆孔适配器");
			type.remove("【接入】蓝牙底座");
		}
		
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		// 设置次数
		int succ = 0, cnt = g_RandomTime, bak = g_RandomTime;
		if(GlobalVariable.currentPlatform.toString().contains("N910")==false){
			type.remove("【插入】圆孔适配器");
		}
		if(GlobalVariable.currentPlatform.toString().contains("N900")==false&&GlobalVariable.currentPlatform.toString().contains("N910")==false){
			type.remove("【接入】蓝牙底座");
		}
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
		else{
			gui.cls_show_msg1(1, "该设备不支持本用例,长按确认键退出");
			return;
		}
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "电池模块内随机组合测试中...\n还剩%d次（已成功%d次），按【取消】退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=BatteryFunArr[random.nextInt(BatteryFunArr.length)];
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
			gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gScreenTime, funcStr2);
			cnt--;
			boolean ret=false;
			 id=-1;
			 getPlugType=false;
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				BatteryFuncName fname = BatteryFuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname,mBatteryReceiver)))
					break;
			}
			
			if(ret)
			succ++;
		}
		//测试后置
		mBatteryReceiver.setCharge();
		gui.cls_show_msg1_record(fileName, "battery200", gScreenTime, "电池模块内随机组合测试测试完成，已执行次数为%d，成功为%d次", bak-cnt,succ);
	}
	private boolean RandomTest(BatteryFuncName fname, BatteryReceiver mBatteryReceiver2) {
		String batMsg ;
		boolean is =true;
		String chargeType ;
		switch(fname){
		case getBatMag:
			batMsg = mBatteryReceiver.getBatMsg();
			gui.cls_show_msg1(gScreenTime, "获取的电池信息是："+batMsg);
			break;
			
		case getPlugType:
			mBatteryReceiver.setCharge();
			id = random.nextInt(type.size());
			gui.cls_show_msg("请确保重新" + type.get(id) + "充电，完成任意键继续");
			chargeType = mBatteryReceiver.getPlugType();
			if ((gui.ShowMessageBox(("充电类型：" + chargeType+",是【确认】，否【取消】").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) {
				gui.cls_only_write_msg(fileName, "RandomTest", "%s模块内测试顺序为：\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(fileName, "RandomTest", gKeepTimeErr, "line %d:获取充电类型错误(%s)", Tools.getLineInfo(),chargeType);
				is=false;
			} else {
				getPlugType = true;
			}	
			break;
		
		case getCharge:
			chargeFlag = mBatteryReceiver.getCharge();
			if (getPlugType && (type.get(id).equals("【插入】适配器") || type.get(id).equals("【插入】USB线")|| type.get(id).equals("【接入】蓝牙底座"))) {
				if (chargeFlag == false) {
					gui.cls_only_write_msg(fileName, "RandomTest",  "%s模块内测试顺序为：\n" + funcStr1 + funcStr2, TESTITEM);// 只写不显示
					gui.cls_show_msg1_record(fileName, "RandomTest", gKeepTimeErr, "line %d:充电状态未改变(%s)", Tools.getLineInfo(), chargeFlag);
					is = false;
				} else {
					// 提示正在充电中
					gui.cls_show_msg1(gScreenTime, "已%s，充电状态已改变。。。", type.get(id));
					mBatteryReceiver.setCharge();
					getPlugType=false;
				}
				mBatteryReceiver.setCharge();
			} else {
				if (chargeFlag) {
					gui.cls_only_write_msg(fileName, "RandomTest",  "%s模块内测试顺序为：\n" + funcStr1 + funcStr2, TESTITEM);// 只写不显示
					gui.cls_show_msg1_record(fileName, "RandomTest", gKeepTimeErr, "line %d:获取充电状态错误(%s)", Tools.getLineInfo(), chargeFlag);
					is = false;
				} else {
					// 提示充电状态未改变
					gui.cls_show_msg1(gScreenTime, "充电状态无变化。。。");
				}
			}
			
			break;
		case getBatVol:
			//节点切换到0，读取锂电池的电压
			try {
				LinuxCmd.execCmd(mStrButPath, "echo 0 > "+mStrButVolDev);
			} catch (IOException e) {
				e.printStackTrace();
			}
			volLi = mBatteryReceiver.getBatVol()+"";
			if(gui.ShowMessageBox(("获取到的锂电池电压："+volLi+"mV，锂电池电压是否正确").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
			{
				gui.cls_only_write_msg(fileName, "RandomTest", "%s模块内测试顺序为：\n" + funcStr1 + funcStr2, TESTITEM);// 只写不显示
				gui.cls_show_msg1_record(fileName, "RandomTest", gKeepTimeErr, "line %d:设备获取的锂电池电压错误（电压：%s）", Tools.getLineInfo(),volLi);
			}
			break;
		case readDevNode:
			// 节点切换到1，读取纽扣电池的电压
			try {
				LinuxCmd.execCmd(mStrButPath, "echo 1 > "+mStrButVolDev);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    volButton = LinuxCmd.readDevNode(mStrButVolDev).trim();
			if(gui.ShowMessageBox(("获取的纽扣电池电压为："+volButton+"mV，纽扣电池电压是否正确（纽扣电池电压范围为2.8V-3.3V，电压为1.1V会安全触发）").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME) != BTN_OK)
			{
				gui.cls_only_write_msg(fileName, "RandomTest", "%s模块内测试顺序为：\n" + funcStr1 + funcStr2, TESTITEM);// 只写不显示
				gui.cls_show_msg1_record(fileName, "RandomTest", gKeepTimeErr, "line %d:设备获取的纽扣电池电压错误（电压：%s）", Tools.getLineInfo(),volButton);
			}
			break;
		default:
			break;
		}
		return is;
	}
	private enum BatteryFuncName {getBatMag,getPlugType,getCharge,getBatVol,readDevNode}
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
