package com.example.highplattest.battery;

import java.util.ArrayList;
import java.util.List;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LinuxCmd;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * module 			: 电池模块
 * file name 		: Battery5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190716 
 * directory 		: 
 * description 		: 设置OTG是否对外供电（农行版本支持）N900
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20190716	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Battery4 extends UnitFragment{
	private final String TESTITEM = "设置OTG是否对外供电";
	private String CLASS_NAME = Battery4.class.getSimpleName();
	private final String strOtgPath = "/sys/class/sy6982_charger/charger_en";
	private final String strOtgPath_910 = "/sys/class/sy6982_charger/otg_chg_en";
	private Gui gui = new Gui(myactivity, handler);
	
	public void battery4()
	{
		String funcName = "battery5";
		int nkeyIn = gui.cls_show_msg("%s\n0.OTG不对外供电\n1.OTG对外供电\n2.单元测试\n3.获取节点\n", TESTITEM);
		switch (nkeyIn) {
		case '0':
			if(GlobalVariable.currentPlatform==Model_Type.N900_4G){
				if(BaseFragment.setNodeFile(strOtgPath, "0")!=SUCC)
				{
					gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
					return;
				}
			}
			else if (GlobalVariable.currentPlatform == Model_Type.N910) {
				if(BaseFragment.setNodeFile(strOtgPath_910, "0")!=SUCC)
				{
					gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
					return;
				}
			} else {
				gui.cls_show_msg("机型不属于N900或者N910");
			}
			
			gui.cls_show_msg("%s设置成功", "OTG不对外供电");
			break;
			
		case '1':
			if(GlobalVariable.currentPlatform==Model_Type.N900_4G){
				if(BaseFragment.setNodeFile(strOtgPath, "1")!=SUCC)
				{
					gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
					return;
				}
			}
			else if (GlobalVariable.currentPlatform==Model_Type.N910) {
				if(BaseFragment.setNodeFile(strOtgPath_910, "1")!=SUCC)
				{
					gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
					return;
				}
			}
			else {
				gui.cls_show_msg("机型不属于N900或者N910");
			}
			
			gui.cls_show_msg("%s设置成功", "OTG对外供电");
			break;
			
		case '2':
			unitTest();
			break;
		case '3':
			getnode();
			break;
		default:
			break;
		}

	}
	
	private void getnode() {
		String node="";
			node = LinuxCmd.readDevNode(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910).replace("\r\n", "");
			gui.cls_show_msg("当前节点为%s", node);

		
	}

	public void unitTest()
	{
		String funcName = "unitTest";
		String node;
		// case1:节点值从0切换到1 modify by zhengxq 不支持从0切换到1的场景，默认一开机就是1 20190725
		// case2:节点值从1切换到0
		List<String> nodeValues = new ArrayList<>();
		for(int i=0;i<1;i++)
		{
			gui.cls_show_msg1(1, "测试节点值从%s", i==0?"0切换到1":"1切换到0");
			nodeValues.removeAll(nodeValues);
//			if(i==0)
//			{
//				nodeValues.add("0");
//				nodeValues.add("1");
//			}
//			else
//			{
				nodeValues.add("1");
				nodeValues.add("0");
//			}
			if(BaseFragment.setNodeFile(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910, nodeValues.get(0))!=SUCC)
			{
				gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
				return;
			}
			node = LinuxCmd.readDevNode(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910).replace("\r\n", "");
			if(node.equals(nodeValues.get(0))==false)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:第%d次切换节点值失败(node=%s)", i+1,Tools.getLineInfo(),node);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(BaseFragment.setNodeFile(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910, nodeValues.get(1))!=SUCC)
			{
				gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
				return;
			}
			node = LinuxCmd.readDevNode(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910).replace("\r\n", "");
			if(node.equals(nodeValues.get(1))==false)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:第%d次切换节点值失败(node=%s)", i+1,Tools.getLineInfo(),node);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		// case3:设置为非0和1的节点值(-1,2,x)，应设置失败
		gui.cls_show_msg1(1, "参数异常测试-1,2,x,AA,B,100");
		String[] errParas = {"-1","2","x","AA","B","100"};
		for(int i =0;i<errParas.length;i++)
		{
			if(BaseFragment.setNodeFile(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910, errParas[i])!=SUCC)
			{
				gui.cls_show_msg1_record(CLASS_NAME, funcName, gScreenTime, "line %d:设置节点测试失败", Tools.getLineInfo());
				return;
			}
			node = LinuxCmd.readDevNode(GlobalVariable.currentPlatform==Model_Type.N900_4G?strOtgPath:strOtgPath_910).replace("\r\n", "");
			if(node.equals("0")==false)
			{
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:第%d次切换节点值失败(node=%s)", Tools.getLineInfo(),i+1,node);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "%s测试通过", TESTITEM);
	}
	
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
