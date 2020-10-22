package com.example.highplattest.systemnode;

import android.provider.Settings;
import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * file name 		: SystemNode9.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20200407 
 * directory 		: 
 * description 		: 获取OTG状态（开发对接曾智彬）
 * related document : 
 * history 		 	: 变更点                             						 		变更时间			案例人员
 *			  		  F7 V1.0.06导入        							20200407		zhengxq
 *					  F10产品 取消otg控制开关，开关节点保留但不生效，  			20200519		郑薛晴
 *					      即：只能通过拔插usb线来切换otg状态。(插着US
 *				      B线的时候是 devices状态。  没有插就是host状态)
 *					      由原Battery7搬移							      20200604                        陈丁
 *                    F7产品 otg控制开关节点保留但不生效                                                        20200925        郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode7 extends UnitFragment {
	private final String TESTITEM = "获取OTG状态";
	private String CLASS_NAME = SystemNode7.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	String otg_node="/sys/class/usb_ctrl/otg_mode";

	/**	曾智彬：获取实际状态的OTG值/sys/class/usb_ctrl/otg_mode
	 * 获取系统设置中的状态Settings.Secure中的accessibility_usb_hostmode_enabled*/
	
	
	public void systemnode7(){
		String funcName="systemnode7";

		while(true)
		{
			int nkeyIn = gui.cls_show_msg("OTG状态(0表示关闭，1表示开启)\n0.获取实际状态的OTG值\n1.获取系统设置中的OTG状态值\n2.OTG控制开关节点无效测试");
			switch (nkeyIn) {
			case '0':
				String value = BaseFragment.getNodeFile(otg_node, "0");
				gui.cls_show_msg1(3,"获取到的OTG状态=%s", value);
				break;
				
			case '1':
				String setValue = Settings.Secure.getString(myactivity.getContentResolver(), "accessibility_usb_hostmode_enabled");
				gui.cls_show_msg1(3, "系统设置中的OTG状态=%s", setValue);
				break;
				
			case '2':
				if(GlobalVariable.currentPlatform==Model_Type.F10)
				{
					if(BaseFragment.setNodeFile(otg_node, "1")!=0)
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:打开otg节点失败", Tools.getLineInfo());
						break;
					}
					gui.cls_show_msg("F10取消OTG控制开关,(1)节点打开->【未插USB线】U盘，摄像头等无法识别，(2)节点打开->【插USB线】时U盘、摄像头可正常识别");
					if(BaseFragment.setNodeFile(otg_node, "0")!=0)
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:关闭otg节点失败", Tools.getLineInfo());
						break;
					}
					gui.cls_show_msg("F10取消OTG控制开关,(1)节点关闭->【未插USB线】U盘，摄像头等无法识别，(2)节点关闭->【插USB线】时U盘、摄像头可正常识别");
				}
				else
				{
					String S1 = "1";
					String S2 = "0";
					gui.cls_show_msg("请手动打开设置->辅助功能的OTG选项,确认打开后按任意键继续。。。");
					if(BaseFragment.setNodeFile(otg_node, "0")!=0)
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:关闭otg节点失败", Tools.getLineInfo());
						break;
					}
					String setValue1 = Settings.Secure.getString(myactivity.getContentResolver(), "accessibility_usb_hostmode_enabled");
					if(setValue1.equals(S2))
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:关闭OTG节点有效，测试失败", Tools.getLineInfo());				
					}
					else
						gui.cls_show_msg1(2,"关闭OTG节点已失效，测试通过");
					
					gui.cls_show_msg("请手动关闭设置->辅助功能的OTG选项,确认关闭后按任意键继续。。。");
					if(BaseFragment.setNodeFile(otg_node, "1")!=0)
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:打开otg节点失败", Tools.getLineInfo());
						break;
					}
					String setValue2 = Settings.Secure.getString(myactivity.getContentResolver(), "accessibility_usb_hostmode_enabled");
					if(setValue2.equals(S1))
					{
						gui.cls_show_msg1_record(CLASS_NAME, "systemnode7", 2, "line %d:打开OTG节点有效，测试失败", Tools.getLineInfo());				
					}
					else
						gui.cls_show_msg1(2,"打开OTG节点已失效，测试通过");
				}
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	
		
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
