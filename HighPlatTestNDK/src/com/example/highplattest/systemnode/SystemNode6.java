package com.example.highplattest.systemnode;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * module 			: 电池模块
 * file name 		: Battery6.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190716 
 * directory 		: 
 * description 		: 切换OTG功能的开关（开发对接马鑫汶）
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20200320	 		created
 *
 * history 		 	: 变更点			变更时间			变更人员
 * 					 由原Battery6搬移        20200604			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode6 extends UnitFragment {
	private final String TESTITEM = "手动切换OTG";
	private String CLASS_NAME = SystemNode6.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private String otg_node="/sys/kernel/debug/msm_otg/mode";
	 /**
     * 切换OTG，实现OTG功能的开关
     * 切换的时候 要先切成none 然后切 host 或者 peripheral 。
     * 比如切换为host要:
     * echo none > /sys/kernel/debug/msm_otg/mode
     * echo host > /sys/kernel/debug/msm_otg/mode
     * @param value host:打开OTG  peripheral:关闭OTG
     *
     */
	
	public void systemnode6(){
    	while(true)
    	{
        	int nkeyIn = gui.cls_show_msg("OTG切换\n0.打开OTG\n1.关闭OTG\n");
        	switch (nkeyIn) {
    		case '0':
    			if(BaseFragment.setNodeFile(otg_node,"host")!=0)
    			{
    				gui.cls_show_msg1_record(CLASS_NAME, "SystemNode8", gKeepTimeErr, "line %d:打开OTG失败，抛出异常",Tools.getLineInfo());
    			}
    			else
    				gui.cls_show_msg1(1, "打开OTG成功");
    				
    			break;
    			
    		case '1':
    			if(BaseFragment.setNodeFile(otg_node,"peripheral")!=0)
    			{
    				gui.cls_show_msg1_record(CLASS_NAME, "SystemNode8", gKeepTimeErr, "line %d:关闭OTG失败，抛出异常",Tools.getLineInfo());
    			}
    			else
    				gui.cls_show_msg1(1, "关闭OTG成功");
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
