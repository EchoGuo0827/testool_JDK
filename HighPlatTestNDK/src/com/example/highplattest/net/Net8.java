package com.example.highplattest.net;

import java.util.Arrays;
import android.newland.telephony.TelephonyManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Net8.java 
 * Author 			: chencm
 * version 			: 
 * DATE 			: 20180831
 * directory 		: 
 * description 		:
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  chencm	       2018031   		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Net8 extends UnitFragment{

	private final String TESTITEM = "IP黑名单功能";
	public final String TAG = Net8.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private TelephonyManager nlTeleManager;
	private String[] result;
	private String str,str1,ipAddress;
	
	public void net8()
	{
		String funcName ="net8";
		//该IP是百度网页地址
		ipAddress="163.177.151.110";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, funcName, gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}	
		nlTeleManager = new TelephonyManager(myactivity);
		gui.cls_show_msg("请先在manifest配置文件中添加mtms权限(测试前请先将浏览器中缓存清除，否则可能影响测试)，完成点任意键继续");	
		
	    //case1:获取黑名单中的所有IP地址
        result= nlTeleManager.getIpBlackList();
        str1 = Arrays.toString(result);
        str= (result==null || result.length==0)?"null":str1;      
        if((gui.ShowMessageBox(("首次获取到的黑名单IP列表是否为："+str).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
        	gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(黑名单IP列表 = %s)",Tools.getLineInfo(), TESTITEM,str);
			if (!GlobalVariable.isContinue)
				return;
		}
	    
	    //case2:参数异常测试
	     gui.cls_show_msg1(gScreenTime, "参数异常测试");
	     if(nlTeleManager.setIpValid(null, false))
		 {
			 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s添加异常IP失败", Tools.getLineInfo(),TESTITEM);
			 if(!GlobalVariable.isContinue)
				 return;
		 }
	     if(nlTeleManager.setIpValid("192.168.1.257", true))
		 {
			 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s添加异常IP失败", Tools.getLineInfo(),TESTITEM);
			 if(!GlobalVariable.isContinue)
				 return;		 
		 }
         if(nlTeleManager.setIpValid("ajd8*ggd*hhh*eee", false))
		 {
			 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s添加异常IP失败", Tools.getLineInfo(),TESTITEM);
			 if(!GlobalVariable.isContinue)
				 return;		
		 }
         if(nlTeleManager.setIpValid("", false))
         {
			 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s添加异常IP失败", Tools.getLineInfo(),TESTITEM);
			 if(!GlobalVariable.isContinue)
				 return;		
		 }	     
        
		//case3.1:设置黑名单地址	
        String message = "若执行过设备重启case，请点击【取消】跳过本步骤；首次执行该用例，则任意键继续";
        if (gui.cls_show_msg(message) != ESC) 
        {
    		
    	    // 测试前置
    	    if(gui.cls_show_msg("请打开浏览器输入IP地址："+ipAddress+"查看是否可以打开,预期可以打开该地址网页，打开其他网页正常[确认]是,[其他]否")!=ENTER)
    		{
    			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s打开IP地址："+ipAddress+"失败", Tools.getLineInfo(), TESTITEM);
    			if(!GlobalVariable.isContinue)
    				return;	
    		}
    	    
	       //将目前网页ip设置到黑名单IP列表中
		    if(!nlTeleManager.setIpValid(ipAddress, false))
			 {
				 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s添加IP失败", Tools.getLineInfo(),TESTITEM);
				if (!GlobalVariable.isContinue)
					return;		 
			 }else
			 {
				 if(gui.cls_show_msg("设备将添加的IP"+ipAddress+"为黑名单，预期打开网页"+ipAddress+"地址失败，打开其他网页正常，[确认]是，[其他]否")!=ENTER)
				{
					gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试不通过，添加黑名单IP"+ipAddress+"可以建立链接", Tools.getLineInfo(), TESTITEM);
					if(!GlobalVariable.isContinue)
						return;	
				}		
			 }	       
			 //获取黑名单中的所有IP地址
			 result= nlTeleManager.getIpBlackList();
		     str1 = Arrays.toString(result);
		     str= (result==null || result.length==0)?"null":str1; 
			 if((gui.ShowMessageBox(("获取黑名单IP列表为："+str+"\n请确定获取的列表中是否增加了"+ipAddress).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
			{
				gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(列表 = %s)",Tools.getLineInfo(), TESTITEM,str);
				if (!GlobalVariable.isContinue)
					return;
			}
        }
		 //case3.2 黑名单在终端重启后仍然有效
		if (gui.cls_show_msg("请将POS设备重启,打开网页地址IP："+ipAddress+"，确认该IP地址网页能否打开，预期打开网页失败，打开其他网页正常[确认]是，[其他]否") != ENTER) 
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败，预期重启后黑名单的IP地址仍可以打开该网页",Tools.getLineInfo(), TESTITEM);
			if (!GlobalVariable.isContinue)
				return;
		}
		//case3.3:如设置已有黑名单地址，再添加黑名单则失败	
		 if(nlTeleManager.setIpValid(ipAddress, false))
		 {
			 gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s预期如黑名单已有IP%s则添加IP失败", Tools.getLineInfo(),TESTITEM,ipAddress);
			 if(!GlobalVariable.isContinue)
				return;			 
		 }

		//case4.1:从黑名单中移除地址
	     if(!nlTeleManager.setIpValid(ipAddress,true))
	     {
	         gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s删除IP失败", Tools.getLineInfo(),TESTITEM);
	           if(!GlobalVariable.isContinue)
					return;
	     }
	     else
	     {
	        if(gui.cls_show_msg("已从黑名单中移除IP:"+ipAddress+"，请使用浏览器打开该IP:"+ipAddress+"网页，预期打开该网页成功，打开其他网页正常，[确认]是，[其他]否")!=ENTER)
	    	 {
	    		gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr,"line %d:%s测试不通过，没有删除IP操作成功", Tools.getLineInfo(), TESTITEM);
	    		if(!GlobalVariable.isContinue)
					return;	
	    	 }	

	      }
	     //获取黑名单中的所有IP地址
		 result= nlTeleManager.getIpBlackList();
	     str1 = Arrays.toString(result);
	     str= (result==null || result.length==0)?"null":str1; 
	     if((gui.ShowMessageBox(("获取黑名单列表为："+str+"\n是否已移除IP:"+ipAddress).getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK)
		{
			gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s测试失败(列表 = %s)",Tools.getLineInfo(), TESTITEM,str);
			if (!GlobalVariable.isContinue)
				return;
		}    
	     
		//case4.2:如黑名单中无相应IP，尝试将该IP移出黑名单将返回失败
	     if(nlTeleManager.setIpValid(ipAddress,true))
	     {
	         gui.cls_show_msg1_record(TAG, funcName, gKeepTimeErr, "line %d:%s黑名单中无相应IP删除IP失败", Tools.getLineInfo(),TESTITEM);
	           if(!GlobalVariable.isContinue)
					return;
	     }
	     
		 gui.cls_show_msg1_record(TAG, funcName, gScreenTime,"%s测试通过", TESTITEM);	 
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}

