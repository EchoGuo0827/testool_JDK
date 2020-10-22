package com.example.highplattest.systemnode;

import com.example.highplattest.battery.Battery4;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * file name 		: SystemNode5.java 
 * description 		: 获取是否原装充电器（T2.1.21导入） 部分节点值获取
 * history 		 	: 变更记录							变更时间			变更人员
 *			  		      获取是否原装充电器		   		20171114	 		郑薛晴
 *					  X1新增ddr频率节点获取			20200511			郑薛晴
 *					  F10新增系统进程的统计信息节点获取	20200519 			魏美杰
 *					  CPOS新增获取钱箱状态的节点		20200526			郑薛晴
 *					 由原Battery4搬移                                         20200604				陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode5 extends UnitFragment {
	private final String TESTITEM = "部分节点值获取";
	private String fileName=SystemNode5.class.getSimpleName();
	private final String strAdapterDev = "/sys/class/sy6982_charger/chargertype";/**是否原装充电器节点*/
	private final String mddrNodePath = "/sys/class/devfreq/dmc/cur_freq";/**X1新增获取ddr频率20200511*/
	private final String statNodePath="/proc/stat";/**系统进程的统计信息20200519*/
	private final String cash_status = "/sys/class/cashbox_ctrl/det_status";/**新增钱箱状态监测节点 CPOS 20200526*/
	Gui gui = new Gui(myactivity, handler);
	public void systemnode5(){
		int nkeyIn = gui.cls_show_msg("部分文件节点值获取\n0.获取是否原装充电器(N910)\n1.获取ddr频率(X1)\n2.系统进程的统计信息(F10)\n3.获取CPOS的钱箱状态(CPOS)\n");
		switch (nkeyIn) {
		case '0':
			batteryTest();
			break;
			
		case '1':
			String ddrValue = getNodeFile(mddrNodePath,"-10086");
			gui.cls_show_msg("ddr频率=%s", ddrValue);
			break;
			
		case '2':
			String statValue = getNodeFile(statNodePath,"-10086");
			gui.cls_show_msg("系统进程的统计信息=%s", statValue);
			break;
			
		case '3':
			String cashValue = getNodeFile(cash_status, "-10086");
			gui.cls_show_msg("CPOS 获取钱箱状态=%s",cashValue);
			break;
			
			
		default:
			break;
			
		case ESC:
			unitEnd();
		return;
		}
	}
	
	private void batteryTest()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.Battery)==false)
		{
			gui.cls_show_msg1(1, "该设备不支持本用例，长按确认键退出");
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "SystemNode5", gKeepTimeErr,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		String adapterValue;
		
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		// case1:插入的适配器非newland原装适配器，获取/sys/class/sy6982_charger/chargertype的节点值应为1
		gui.cls_show_msg("请插入非newland适配器，完成后任意键继续");
		adapterValue = LinuxCmd.readDevNode(strAdapterDev);
		if(gui.ShowMessageBox(("获取到的值="+adapterValue+"，获取到的值是否为1").getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "SystemNode5", gKeepTimeErr, "line %d:获取插入的适配器值错误（预期：1，实际：%s）", Tools.getLineInfo(),adapterValue);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:插入的设配器为newland的原装适配器，获取/sys/class/sy6982_charger/chargertype的节点值为0
		gui.cls_show_msg("请插入newland原装适配器，完成后任意键继续");
		adapterValue = LinuxCmd.readDevNode(strAdapterDev);
		if(gui.ShowMessageBox(("获取到的值="+adapterValue+"，获取到的值是否为0").getBytes(), (byte) (BTN_OK|BTN_CANCEL), MAXWAITTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName, "SystemNode5", gKeepTimeErr, "line %d:获取插入的适配器值错误（预期：0，实际：%s）", Tools.getLineInfo(),adapterValue);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(fileName, "SystemNode5", gScreenTime, "%s测试通过，多测试几次均测试通过才可视为测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
