package com.example.highplattest.apn;

import java.util.List;
import android.newland.telephony.ApnEntity;
import android.newland.telephony.ApnUtils;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Apn模块
 * file name 		: Apn6.java 
 * directory 		: 判断Macau的APN删除
 * 
 * history 		 	: 变更记录									变更时间			变更人员
*					判断Macau的APN删除（N910_V2.2.52）                               20200609                         郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Apn6 extends UnitFragment
{
	private final String TESTITEM = "判断Macau的APN删除";
	private Gui gui = new Gui(myactivity, handler);
	private ApnUtils apnUtils;
	private String fileName=Apn6.class.getSimpleName();
	
	public void apn6()
	{
		String funcName = "apn6";
		try 
		{
			apnUtils = new ApnUtils(myactivity);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		List<ApnEntity> listAllApn = apnUtils.getAllApnList();
		for (ApnEntity apnEntity:listAllApn) {
			String tempApnMcc = apnEntity.getMcc();
			String tempApn = apnEntity.getApn();
			if(tempApnMcc==null||tempApn==null)
			{
				LoggerUtil.d("tempApnMcc==null 或tempApn==null");
				continue;
			}
			if(tempApnMcc.equals("455")&&(tempApn.contains("ctm-")||tempApn.contains("three")))
			{
				gui.cls_show_msg("Id=%d,apn=%s:mcc=%s,Proxy=%s,Port=%s\n固件修改前存在Proxy和Port字段，修改后Proxy和Port字段为空", 
						apnEntity.getId(), apnEntity.getApn(),apnEntity.getMcc(),apnEntity.getProxy(),apnEntity.getPort());
			}
		}
		
		gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "ctm-mobile和three的APN的字段Proxy和Port为空才可测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
