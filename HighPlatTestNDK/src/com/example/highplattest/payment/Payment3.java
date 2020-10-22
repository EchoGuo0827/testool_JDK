package com.example.highplattest.payment;

import java.io.ByteArrayInputStream;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.newland.os.DeviceStatisticsManager;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.XmlResourceParserTool;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.OtaUpdateBroadcastReceiver;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Payment3.java 
 * description 		: android.intent.extra.ota.silent.installation.result:OTA升级广播结果
 * related document : 
 * history 		 	: 变更点											变更时间			变更人员
 *			  		  错误码-102获取不到包名为正常现象							20200513     	陈丁
 *					延时升级版本命名错误只会返回state=0，其他不返回      				20200526                        陈丁
 *					广播比acvitity先启动，导致在接收广播中获取全局变量失败崩                        20200526                        陈丁
 *					溃。修改为从数据库中获取。
 *					X5的ota存放目录修改为"/mnt/sdcard/fota_update"		20200622		郑薛晴
 *					A7A9除X5外其他机型ota升级优化，延时升级校验签名(即内容错误情况),若校验失败直接发送广播，结束升级流程。 20200805 陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Payment3 extends UnitFragment{
	private final String TESTITEM = "OTA升级";
	private final String ACTION_OTA_UPDATE = "android.intent.extra.ota.silent.installation.result";
	private Gui gui = new Gui(myactivity, handler);
	private final int state_succ = 0;
	private final int state_fail = 1;// 陈阵反馈1和3都是错误码，判断异常测试时只要非0即可 by zhengxq 20200313
	//private final int state_continue = 2;//开发陈阵解释状态码2为单包正常升级时，重启设备前发送，不好收到，故关注升级成功重启后收到的状态码0即可
	//-100 -101 是有包名返回的  -102无包名返回
	private final int errCode_unzipfailed = -100;
	private final int errCode_lowbattery = -101;
	private final int errCode_errname = -102;//升级OTA包名不匹配 bywangxy20180807
	private boolean isDelay=false;
	private String fileName="Payment3";
	OtaUpdateBroadcastReceiver otaUpdateBroadcastReceiver;
	private static SharedPreferences sharedPreferences;
	private static SharedPreferences.Editor editor;
	boolean isforth=false;
	String otaUpdateDir = "/mnt/sdcard/newland_update";
	boolean newpaymentota=false;
	public void payment3()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"payment3",gScreenTime,"%s用例不支持自动化测试,请手动验证",  TESTITEM);
			return;
		}
		//A7 A9有进行ota升级优化。延迟升级会直接校验。不符合就返回1； （X5不在该优化范围内）
		if ((GlobalVariable.gCurPlatVer==Platform_Ver.A7||GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				&&!(GlobalVariable.currentPlatform==Model_Type.X5)) {
			
			newpaymentota=true;
		}
		Log.d("eric_chen", "newpaymentota=="+newpaymentota);
		
		/* private & local definition */
		if(GlobalVariable.currentPlatform==Model_Type.X5)//X5的ota存放路径修改为fota_update
		{
			otaUpdateDir = "/mnt/sdcard/fota_update";
		}
		
		isforth=GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth);
		Log.d("eric_chen", "Payment3 isforth: "+isforth);
		
		otaUpdateBroadcastReceiver=new OtaUpdateBroadcastReceiver();
		registota();
		
		sharedPreferences=myactivity.getSharedPreferences("ISForth", Context.MODE_PRIVATE);
		editor=sharedPreferences.edit();
		editor.putBoolean("isforth", isforth);
		editor.commit();
		
		int nkeyIn = gui.cls_show_msg("OTA测试\n0.OTA升级测试\n1.OTA升级时间查看\n2.OTA包删除查看");
		switch (nkeyIn) {
		case '0':
			otaUpdate();
			break;
			
		case '1':
			otaTime();
			break;
			
		case '2':// 新增ota升级成功之后会自动删除下载包的操作 20190124 zhengxq
			otaDelete();
			break;

		case ESC:
			unitEnd();
			return;
		}
	}
	
	private void otaUpdate()
	{
		/* private & local definition */
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"otaUpdate",gScreenTime,"%s用例不支持自动化测试,请手动验证",  TESTITEM);
			return;
		}
		String message;
		int state,errCode;
    	String failedName;
    	
    	DeviceStatisticsManager service;
    	String xmlInfo ;
    	Map<String, String> patchInfo;
    	
    	//正常广播升级,自动重启设备升级
    	Intent intent = new Intent();
    	intent.setAction("android.intent.extra.ota.silent.installation");
    	intent.setPackage("android");
		/*process body*/
    	gui.cls_show_msg("本用例中,用adb push方式导入的OTA包格式为单次压缩的zip包,例如‘N910_SA2_OTA_V2.1.26...V2.1.37.zip’,用下载工具导入的OTA包为两层压缩的zip包,例如‘N910_SA2_OTA.ALL_V2.1.37_fb524f2275_201701091419’");
    	//开发说补丁级别信息与延迟升级功能一起导入固件的,通过判断有无补丁级别信息来判断该固件是否支持延迟升级广播
    	service = DeviceStatisticsManager.getInstance(myactivity);
    	xmlInfo=service.getDeviceStatisticsInfo();
		patchInfo=XmlResourceParserTool.getNodeContent(new ByteArrayInputStream(xmlInfo.getBytes()), "patch_info");
		if(patchInfo == null || patchInfo.size() == 0){
			//没有patch_info相应节点则未导入补丁级别信息，直接正常升级
			isDelay=false;
		}else{
			//是否进行延时升级或正常升级
	    	if ((gui.ShowMessageBox(("是否进行延时升级测试"+"\n是则点击确认,否则点击其他任意键,需确认当前固件版本是否支持，若支持则正常、延时两种情况均要测试到").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))==BTN_OK) 
	    	{
	    		isDelay=true;
	    		intent.putExtra("extraCmd", "delayToNextBoot");
	    	}
			else
				isDelay=false;
		}
		
    	
    	/*process body*/
		//case0  不存在OTA包,发送OTA升级广播应无响应
		gui.cls_show_msg("F7错误的ota包不会弹出升级广播进度条，若未进入升级广播，则不会有吐司提示。");
		gui.cls_show_msg("ota升级失败状态返回1或3都是正常");
    	message = "不存在OTA包时,发送OTA升级广播应无响应,任意键继续,【取消】跳过";
    	if(gui.cls_show_msg(message) != ESC){
    		myactivity.sendBroadcast(intent);
			if(gui.cls_show_msg("已发送升级广播,请等待60s观察是否出现接收到OTA升级广播的弹框提示,【确认】是,【其他】否")==ENTER)
			{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:OTA升级广播响应异常", Tools.getLineInfo());
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		//case1.1 错误的OTA升级包,内容错误,预期state=1,errCode=-100     A9经过试验不会弹出OTA升级广播弹框
    	//A7 A9 ota延时升级优化，若签名错误即内容错误。直接返回1。无需重启。 20200805
		message = "解析包错误,测试前请从要升级的OTA包中删除部分文件(或将别的压缩文件改名为OTA包格式),构造内容错误命名正确的OTA包,用adb push等方式导入OTA包到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				OtaUpdateBroadcastReceiver.reset(myactivity);
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("已发送升级广播,待出现OTA升级广播弹框提示后点击任意键继续");
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后进入此用例,且出现OTA升级广播弹框提示后,请点其他键继续(A7 A9机型的错误包延时升级无需重启,发送广播后会直接校验,出现吐司信息属于正常现象)") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					if (newpaymentota) {
						gui.cls_show_msg("A7A9无需重启---按任意键继续");
					}else {
						gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后【跳过前面】直接进入此子用例");
						return;
					}
				}
			}
			
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state != state_succ && errCode == errCode_unzipfailed){
				if(gui.cls_show_msg("获取的OTA包名为：%s\n是否正确,【确认】是,【其他】否",failedName)!=ENTER)
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:设备获取的失败OTA包名与实际OTA包名获取不一致（设备：%s）", Tools.getLineInfo(),failedName);
					if(GlobalVariable.isContinue==false)
						return;
				}
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态或返回值错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
    	//case1.2 错误的OTA升级包,命名错误,预期state=1,errCode=-102
		
		message = "解析包错误,将正确的OTA包重命名,构造命名错误的OTA包,用adb push等方式导入OTA包到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				OtaUpdateBroadcastReceiver.reset(myactivity);
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("已发送升级广播,待出现OTA升级广播弹框提示后点击任意键继续");
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后进入此用例,且出现OTA升级广播弹框提示后,请点其他键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后【跳过前面】直接进入此子用例");
					return;
				}
			}
			
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			//开发回复：错误码-102获取不到包名为正常现象 20200513 陈丁
			//延时升级与非延时升级逻辑不同。 延时升级情况下，如果是错误的命名就只会返回一个state=0  20200526陈丁
			if (!isDelay) {
				if(state == state_fail && errCode == errCode_errname){
					if(gui.cls_show_msg("获取的OTA包名为：%s\n是否正确,【确认】是,【其他】否\n错误码-102获取不到包名为正常现象",failedName)!=ENTER)
					{
						gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:设备获取的失败OTA包名与实际OTA包名获取不一致（设备：%s）", Tools.getLineInfo(),failedName);
						if(GlobalVariable.isContinue==false)
							return;
					}
				} else{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态或返回值错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				}
			}else {
				if (state==state_succ) {
					gui.cls_show_msg("获取到的状态为：%d.预期state应该为0",state);
				}else {
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d)",Tools.getLineInfo(),state);
				}
				
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}

		//case1.3 错误的OTA升级包,版本错误,预期state=1,errCode=-102
		message = "解析包错误,将正确但与当前固件版本不符合的OTA包,用adb push等方式导入OTA包到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				OtaUpdateBroadcastReceiver.reset(myactivity);
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("已发送升级广播,待出现OTA升级广播弹框提示后点击任意键继续");
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后进入此用例,且出现OTA升级广播弹框提示后,请点其他键继续（）") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后【跳过前面】直接进入此子用例");
					return;
				}
			}

			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			//延时升级与非延时升级逻辑不同。 延时升级情况下，如果是错误的命名就只会返回一个state=0  20200526陈丁
			if (!isDelay) {
				if(state != state_succ && errCode == errCode_errname){
					if(gui.cls_show_msg("获取的OTA包名为：%s\n是否正确,【确认】是,【其他】否，错误码-102获取不到包名为正常现象",failedName)!=ENTER)
					{
						gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:设备获取的失败OTA包名与实际OTA包名获取不一致（设备：%s）", Tools.getLineInfo(),failedName);
						if(GlobalVariable.isContinue==false)
							return;
					}
				} else{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态或返回值错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				}
			}else {
				if (state==state_succ) {
					gui.cls_show_msg("获取到的状态为：%d.预期state应该为0",state);
				}else {
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d)",Tools.getLineInfo(),state);
				}
			}
			gui.cls_show_msg("本子用例测试通过,完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		
		//case2  电量低升级失败,预期state=1,errCode=-101
		message = "电量低OTA升级失败,测试前请确认POS电量低于百分30,用adb push等方式(不能用下载工具)将正确的OTA包下载到到POS机目录"+otaUpdateDir+",【拔掉USB】！！任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				OtaUpdateBroadcastReceiver.reset(myactivity);
				myactivity.sendBroadcast(intent);
				gui.cls_show_msg("已发送升级广播,待出现OTA升级广播弹框提示后点击任意键继续");
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后进入此用例,且出现OTA升级广播弹框提示后,请点其他键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后【跳过前面】直接进入此子用例");
					return;       
				}
			}
			
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state != state_succ && errCode == errCode_lowbattery){
				if(gui.cls_show_msg("获取的OTA包名为：%s\n是否正确,【确认】是,【其他】否",failedName)!=ENTER)
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:设备获取的失败OTA包名与实际OTA包名获取不一致(失败包名：%s)", Tools.getLineInfo(),failedName);
					if(GlobalVariable.isContinue==false)
						return;
				}
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态或返回值错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		
		//case3多包升级, 升级过程不会反复重启进入系统，预期state=0
		message = "连续进行OTA升级的继续状态,用adb push等方式将两个能连续升级的OTA包下载到到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已连续升级成功,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					gui.cls_show_msg("即将发送升级广播并自动重启升级,升级成功开机后请【跳过前面】直接进入此子用例,任意键继续");
					myactivity.sendBroadcast(intent);    
					return;
				}
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后连续升级成功,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后再进入此子用例,升级成功开机后,请【跳过前面】直接进入此子用例,任意键继续");
					return;
				}
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state == state_succ){
				if(errCode != 0 || !(failedName.equals("null")))
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的错误码或失败包名有误(errCode:%d,failedName:%s)",Tools.getLineInfo(),errCode,failedName);;
					if(GlobalVariable.isContinue==false)
						return;
				} 
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		
		//case4  单包升级成功,预期state=0
		message = "选择正确的OTA包,用adb push等方式将OTA包下载到到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (!isDelay) 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已升级成功,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					gui.cls_show_msg("即将发送升级广播并自动重启升级,升级成功开机后请【跳过前面】直接进入此子用例,任意键继续");
					myactivity.sendBroadcast(intent); 
					return;
				}
			} else 
			{
				if (gui.cls_show_msg("首次进入此子用例请点【确认】,已手动重启后升级成功,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) 
				{	
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					gui.cls_show_msg("延迟升级OTA包需手动重启设备才能进行升级操作,请手动重启设备后再进入此子用例,升级成功开机后,请【跳过前面】直接进入此子用例,任意键继续");
					return;
				}
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			
			if(state == state_succ){
				if(errCode != 0 || !(failedName.equals("null")))
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的错误码或失败包名有误(errCode:%d,failedName:%s)",Tools.getLineInfo(),errCode,failedName);;
					if(GlobalVariable.isContinue==false)
						return;
				}
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		//4.1 下载过程中断电或者重启。重新进行ota升级应正常 by20191105
		message = "选择正确的OTA包,用adb push等方式将OTA包下载到到POS机目录"+otaUpdateDir+"(在升级过程中将机器断电重启),任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			if (gui.cls_show_msg("首次进入此子用例请点【确认】,已经断电重启过点【其他】,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) 
			{	
				OtaUpdateBroadcastReceiver.reset(myactivity);
				gui.cls_show_msg("即将发送升级广播并自动重启升级,升级成功开机后请【跳过前面】直接进入此子用例(后面会提示断电---),任意键继续");
				myactivity.sendBroadcast(intent); 
				gui.cls_show_msg("请断电---------------------------");
				return;
			}else {
				if (gui.cls_show_msg("已经断电重启后再次进入此子用例请点【确认】,且屏幕下方出现OTA升级弹框后进入点任意键继续") == ENTER) {
					OtaUpdateBroadcastReceiver.reset(myactivity);
					myactivity.sendBroadcast(intent);
					return;
				}
				
			}
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state == state_succ){
				if(errCode != 0 || !(failedName.equals("null")))
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的错误码或失败包名有误(errCode:%d,failedName:%s)",Tools.getLineInfo(),errCode,failedName);;
					if(GlobalVariable.isContinue==false)
						return;
				}
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg("本子用例测试通过,测试完成请删除下载的OTA包以免影响后续测试,完成后任意键继续");
		}
		
		//case5 下载工具升级OTA成功,预期state=0   开发评审不使用下载工具测试返回值 by zhengxq 20200311
		/**message = "选择正确匹配的两次压缩格式的OTA包,用PC下载工具导入OTA包到POS机,升级成功重启后请【跳过前面】直接进入此子用例,待出现OTA升级广播弹框提示后,任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			state = OtaUpdateBroadcastReceiver.getState(myactivity);
			errCode = OtaUpdateBroadcastReceiver.getErrCode(myactivity);
			failedName = OtaUpdateBroadcastReceiver.getFailedName(myactivity);
			if(state == state_succ){
				if(errCode != 0 || !(failedName.equals("null")))
				{
					gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的错误码或失败包名有误(errCode:%d,failedName:%s)",Tools.getLineInfo(),errCode,failedName);;
					if(GlobalVariable.isContinue==false)
						return;
				}
			} else{
				gui.cls_show_msg1_record(fileName,"otaUpdate",gKeepTimeErr, "line %d:获取的状态错误(state:%d,errCode:%d,failedName:%s)",Tools.getLineInfo(),state,errCode,failedName);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}*/
		
		
		gui.cls_show_msg1_record(fileName,"otaUpdate",gScreenTime,"%s测试通过", TESTITEM);
	}
	
	private void otaTime()
	{
    	String otaTime;
    	String k21Status;
		// 要在安全升级成功之后才发送OTA广播 modify by zhengxq sys.k21UpdateStatus  升级值为working
		k21Status = OtaUpdateBroadcastReceiver.getK21Status(myactivity);
		otaTime = OtaUpdateBroadcastReceiver.getOtaTime(myactivity);
		if(gui.cls_show_msg("OTA更新的接收时间：%s，K21的状态：%s,K21的状态应为非'working',ota的更新时间应在K21更新之后,是[确认],否[其他]", otaTime,k21Status)!=ENTER)
		{
			gui.cls_show_msg1_record(fileName,"otaTime",gKeepTimeErr,"line %d:获取ota更新时间错误(k21Status:%s,otaTime:%s)",Tools.getLineInfo(),k21Status,otaTime);
			if(GlobalVariable.isContinue==false)
				return;
		}
		gui.cls_show_msg1_record(fileName,"otaTime",gScreenTime,"OTA时间查看测试通过");
	}
	
	private void otaDelete()
	{
		gui.cls_show_msg("本用例中,用adb push方式导入的OTA包格式为单次压缩的zip包,例如‘N910_SA2_OTA_V2.1.26...V2.1.37.zip’,用下载工具导入的OTA包为两层压缩的zip包,例如‘N910_SA2_OTA.ALL_V2.1.37_fb524f2275_201701091419’");
		
    	Intent intent = new Intent();
    	intent.setAction("android.intent.extra.ota.silent.installation");
		//case4  单包升级成功,预期state=0
		String message = "选择正确的OTA包,用adb push等方式将OTA包下载到到POS机目录"+otaUpdateDir+",任意键继续,【取消】跳过";
		if(gui.cls_show_msg(message) != ESC){
			OtaUpdateBroadcastReceiver.reset(myactivity);
			gui.cls_show_msg("即将发送升级广播并自动重启升级,升级成功后"+otaUpdateDir+"目录下对应的升级包被删除才可视为测试通过");
			myactivity.sendBroadcast(intent); 
			return;
		}
	}
	
	
	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {
		 myactivity.unregisterReceiver(otaUpdateBroadcastReceiver);
		gui = null;
	}
	
	private void registota()
	{
	       IntentFilter intentFilter = new IntentFilter();  
	         intentFilter.addAction(ACTION_OTA_UPDATE);
	         myactivity.registerReceiver(otaUpdateBroadcastReceiver, intentFilter);  
	}
   


}
