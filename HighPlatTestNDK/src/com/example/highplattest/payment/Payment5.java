package com.example.highplattest.payment;

import android.content.Intent;
import android.newland.os.DeviceStatisticsManager;
import java.io.ByteArrayInputStream;
import java.util.Map;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.XmlResourceParserTool;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.OtaUpdateBroadcastReceiver;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Payment5.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180625
 * directory 		: 补丁级别列表的读取
 * description 		: 
 * related document :
 * history 		 	: 变更记录		 									变更时间			变更人员
 *			  		  创建		    		 							20180625	 	 王小钰
 * 					X5的ota存放目录修改为"/mnt/sdcard/fota_update"		20200622		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Payment5 extends UnitFragment
{
	private final String TESTITEM = "补丁级别列表测试";
	private Gui gui = new Gui(myactivity, handler);
	private String patch;
	private DeviceStatisticsManager service;
	private String xmlInfo ;
	private Map<String, String> patchInfo;
	private final int state_fail_patch = 3;//补丁包升级失败
	private final int errCode_unzipfailed = -100;
	private int state=-1;
	private int errCode=-1;
	private String failedName;
	private boolean isDelay=false;
	private String fileName="Payment5";
	String otaUpdateDir = "/mnt/sdcard/newland_update";
	
	public void payment5()
	{
		if(GlobalVariable.currentPlatform==Model_Type.X5)//X5的ota存放路径修改为fota_update
		{
			otaUpdateDir = "/mnt/sdcard/fota_update";
		}
		
		WAITMAXTIME=60;
		service = DeviceStatisticsManager.getInstance(myactivity);
		xmlInfo=service.getDeviceStatisticsInfo("patch_info");
		patchInfo=XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "patch_info");
		//正常广播升级，自动重启设备升级
		Intent intent = new Intent();
    	intent.setAction("android.intent.extra.ota.silent.installation");
    
    	gui.cls_show_msg("设置-关于设备中连续点击三次“Android版本”后返回键退出关于设备,再次进入则可在版本号中可查看到设备的补丁信息;本案例中通过两种方法获取补丁级别列表,对于获取的补丁级别列表内容可带后六位校验码或不带,均是正常的;预期第一次进入本案例时未进行过任何补丁升级,故补丁级别列表为空;确认后点任意键继续");
        //是否进行延时升级或正常升级
    	if ((gui.ShowMessageBox(("是否进行延时升级补丁包"+"\n是则点击确认,否则点击其他任意键,正常升级和延时升级两种情况均要测试到").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))==BTN_OK) 
    	{
    		isDelay=true;
    		intent.putExtra("extraCmd", "delayToNextBoot");
    	}
		else
			isDelay=false;
    	
    	//获取测试前的补丁级别列表,通过getDeviceStatisticsInfo("patch_info")接口
		if ((gui.ShowMessageBox(("请确认设备的补丁级别列表是否为:" + patchInfo.get("patch_level_list")+"\n是则点击确认,否则点击其他任意键").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败(补丁级列表=%s)",Tools.getLineInfo(), TESTITEM,patchInfo.get("patch_level_list"));
			if (!GlobalVariable.isContinue)
				return;
		}
		
		// case1.1:正确补丁包升级后(单个升级),查看补丁级别列表,预期补丁级列表中应加入升级的补丁信息,通过系统属性读取
		String message = "更新正确补丁包,请仅将一个正确补丁包放在POS设备的"+otaUpdateDir+"路径下,仅可存放一个正确的补丁包,任意键继续,【取消】跳过";
		if (gui.cls_show_msg(message) != ESC) 
		{
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】则升级重启,单个正确补丁包升级成功后进入点任意键") == ENTER) 
				{	
					myactivity.sendBroadcast(intent);
					return;
				}
			} else 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级单个补丁包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if (state == NDK_OK) 
			{
				patch = BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认获取的补丁级别列表:" + patch + "\n是否在原有补丁级别列表中加入这次升级的补丁信息").getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败(补丁级列表=%s,失败包=%s,errCode=%d)", Tools.getLineInfo(),TESTITEM, patch, failedName,errCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的补丁包以免影响后续测试,完成后任意键继续");
		}
		
		
		// case1.2:正确补丁包升级后(连续升级),查看补丁级别列表,预期补丁级列表中应加入升级的补丁信息,通过系统属性读取
		message = "更新正确补丁包,请将多个正确补丁包放在POS设备的"+otaUpdateDir+"路径下,仅可存放正确的补丁包,任意键继续,【取消】跳过";
		if (gui.cls_show_msg(message) != ESC) 
		{
			if(!isDelay)
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】则升级重启,多个正确补丁包升级成功后进入点任意键") == ENTER) 
				{
					myactivity.sendBroadcast(intent);
					return;
				}
			}else
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级多个补丁包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if (state == NDK_OK) 
			{
				patch = BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认获取的补丁级别列表:" + patch + "\n是否在原有补丁级别列表中加入这次升级的补丁信息").getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr,"line %d:%s测试失败(补丁级列表=%s,errCode=%d,失败包=%s,成功包=%s,失败包和成功包个数之和应等于实际放置的补丁包个数)", Tools.getLineInfo(),TESTITEM, patch,errCode,failedName,intent.getStringExtra("updatedPackageName"));
					if (!GlobalVariable.isContinue)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的补丁包以免影响后续测试,完成后任意键继续");
		}	
		
		//case2:错误补丁包升级后，查看补丁级别列表，预期设备不重启，补丁级列表不变，通过系统属性读取
		message = "更新错误补丁包,请将错误补丁包放在POS设备的"+otaUpdateDir+"路径下,仅可存放错误的补丁包,任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC)
		{
			if (!isDelay) 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("已发送升级广播，待出现补丁升级广播弹框提示后点击任意键继续");
			} else 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级错误补丁包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state == state_fail_patch && errCode == errCode_unzipfailed)
			{
				patch=BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认获取的补丁级别列表:" + patch+"\n是否和未升级错误补丁包前的补丁级别列表一致").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败(补丁级列表=%s,errCode=%d)",Tools.getLineInfo(), TESTITEM,patch,errCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的补丁包以免影响后续测试,完成后任意键继续");
		}
		
		//case3：升级补丁后，后续OTA方式升级固件也应正常
		message = "更新补丁包后,后续进行OTA包固件升级,请将相应版本的正确的OTA固件升级包放在POS设备的"+otaUpdateDir+"路径下,仅可存放正确的OTA固件升级包,任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC)
		{
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】则升级重启,正确OTA固件升级成功后进入点任意键") == ENTER)
				{
					myactivity.sendBroadcast(intent);
					return;
				}
			} else 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级OTA固件包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if (state == NDK_OK) 
			{
				patch = BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认OTA固件是否升级成功,OTA固件升级前的补丁列表预期会清空").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME)) != BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败，失败包=%s,errCode=%d", Tools.getLineInfo(), TESTITEM,failedName,errCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		
		//case4：OTA方式升级固件后，升级当前最新固件版本的补丁包
		message = "OTA包固件升级成功后,请将设备当前固件版本号的正确的补丁包放在POS设备的"+otaUpdateDir+"路径下,仅可存放正确的补丁包,任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC)
		{
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】则升级重启,补丁升级成功后进入点任意键") == ENTER) 
				{
					myactivity.sendBroadcast(intent);
					return;
				}
			} else 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级补丁包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if (state == NDK_OK) 
			{
				patch = BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认OTA固件升级成功后进行的该版本相应补丁包升级是否正常,补丁级别列表是否加入了该补丁号").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME)) != BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败，失败包=%s,errCode=%d", Tools.getLineInfo(), TESTITEM,failedName,errCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的补丁包以免影响后续测试,完成后任意键继续");
		}
		
		// case5:正确补丁包升级后(通用补丁包升级）,查看补丁级别列表,预期补丁级列表中应加入升级的补丁信息,通过系统属性读取
		message = "更新正确通用补丁包,请将通用补丁包放在POS设备的"+otaUpdateDir+"路径下,仅可存放通用的补丁包,任意键继续,【取消】跳过";
		if (gui.cls_show_msg(message) != ESC) 
		{
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】则升级重启,通用补丁升级成功后进入点任意键") == ENTER) 
				{
					myactivity.sendBroadcast(intent);
					return;
				}
			} else 
			{
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("延迟升级通用补丁包需手动重启设备才能进行补丁包升级,请手动重启设备,完成后任意键继续");
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if (state == NDK_OK) 
			{
				patch = BaseFragment.getProperty("persist.sys.patchLevel","-10086");
				if ((gui.ShowMessageBox(("请确认获取的补丁级别列表:" + patch + "\n是否在原有补丁级别列表中加入这次升级的补丁信息").getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) 
				{
					gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:%s测试失败(补丁级列表=%s,失败包=%s,errCode=%d)", Tools.getLineInfo(),TESTITEM, patch, failedName,errCode);
					if (!GlobalVariable.isContinue)
						return;
				}
			} else 
			{
				gui.cls_show_msg1_record(fileName,"payment5",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s,succName:%s)",Tools.getLineInfo(), state, errCode, failedName,intent.getStringExtra("updatedPackageName"));
				if (!GlobalVariable.isContinue)
					return;
			}
			
			gui.cls_show_msg("本子用例测试完成请删除下载的补丁包以免影响后续测试,完成后任意键继续");
		}
		
		gui.cls_show_msg1_record(fileName,"payment5",gScreenTime, "%s测试通过", TESTITEM);
	}
	
	@Override
	public void onTestUp() 
	{
		
	}

	@Override
	public void onTestDown() 
	{
		gui=null;
	}

}
