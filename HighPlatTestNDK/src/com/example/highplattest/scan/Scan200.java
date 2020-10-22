package com.example.highplattest.scan;

import java.io.InputStream;
import java.util.Random;

import android.content.Context;
import android.newland.scan.ScanUtil;
import android.os.Handler;
import android.view.SurfaceView;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.BitmapDeal;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan200.java 
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20170825
 * directory 		: 
 * description 		: Scan模块内随机
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20170825    	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Scan200 extends UnitFragment{
	private String fileName=Scan200.class.getSimpleName();
	private String TESTITEM = "Scan模块内随机";
	private Gui gui = new Gui(myactivity, handler);	
	private ScanUtil scan_domestic;
//	private com.android.newland.scan.ScanUtil scan_oversea;
	private int MAXWAITTIME=15*1000;
	private String corPath = GlobalVariable.sdPath+"scan/correct.PNG";
	private boolean initflag = false;
	private String [] codeArr = new String[]{"CODE128","CODE39","CODE93","CODEBAR","QR","UCCEAN128","ISBN","ISSN","DM","PDF417","EAN8","EAN13","UPCA","UPCE"};//支持的码制
	
	Random random = new Random();
	private String[] scanModeStr = {"硬解码","zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
	
	public void scan200(){
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan200", gKeepTimeErr, "%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		while(true)
		{
			// 1.zxing旧接口\n2.自动识别接口兼容zxing扫码\n zxing接口不需要测试 20200710
			int nkeyIn = gui.cls_show_msg("扫码配置\n0.硬解码(默认前置)\n3.自动识别接口测试自动识别扫码\n");
			switch (nkeyIn) 
			{
			case '0':
				String [] scanFuncArr0 = {"Init1","Init2","setModeContinuous","hard_doScan","release"};
				scan200Test(scanModeStr[0],Scan_Mode.MODE_ONCE,scanFuncArr0);
				break;
			/*case '1':
				String [] scanFuncArr1 = {"release","openLight","closeLight","soft_doscan","setDecodeScreenResolution","zxing_scanUtil"};
				scan200Test(scanModeStr[1],Scan_Mode.ZXING,scanFuncArr1);
				break;
			case '2':
				String [] scanFuncArr2 = {"release","openLight","closeLight","soft_doscan","setDecodeScreenResolution","doScan_InputStream",
						"getThk88ID","getNLSVersion","setNlsScn","nls0_scanUtil"};
				scan200Test(scanModeStr[2],Scan_Mode.NLS_0,scanFuncArr2);
				break;*/
			case '3':
				if(GlobalVariable.currentPlatform==Model_Type.N700||GlobalVariable.currentPlatform==Model_Type.N700_A7)
				{
					String [] scanFuncArr4 = {"release","openLight","closeLight","soft_doscan","setDecodeScreenResolution","doScan_InputStream",
							"getThk88ID","getNLSVersion","setNlsScn","nls1_scanUtil","setLedsetRedLed"};
					scan200Test(scanModeStr[3],Scan_Mode.NLS_1,scanFuncArr4);
				}
				else
				{
					String [] scanFuncArr3 = {"release","openLight","closeLight","soft_doscan","setDecodeScreenResolution","doScan_InputStream",
							"getThk88ID","getNLSVersion","setNlsScn","nls1_scanUtil"};
					scan200Test(scanModeStr[3],Scan_Mode.NLS_1,scanFuncArr3);
				}
				break;
					
			case ESC:
				unitEnd();
				return;
			}
		}
//		case IM81_New:
//		case IM81_Old:
//			String [] scanFuncArr1 = {"release","openLight","closeLight","soft_doscan","setDecodeScreenResolution","zxing_scanUtil"};
//			scan200Test(scanModeStr[1],Scan_Mode.ZXING,scanFuncArr1);
//			break;
//		case N900_3G:
//			//只支持硬解
//			String [] scanFuncArr0 = {"Init1","Init2","setModeContinuous","hard_doScan","release"};
//			scan200Test(scanModeStr[0],Scan_Mode.MODE_ONCE,scanFuncArr0);
//			break;
//		default:
//			break;
//		}
	}
	
	public void scan200Test(String scanModeStr,Scan_Mode scan_Mode,String[] scanFuncArr){
		
		int len = scanFuncArr.length;
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
    	gui.cls_show_msg1(1, "%s随机组合测试中...",scanModeStr);
		gui.cls_show_msg("测试过程中机具不应进入休眠,请先将休眠时间设大,完成任意键继续");
		if(scan_Mode != Scan_Mode.MODE_ONCE){
			handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_VIEW);
		}
		try 
		{
			initScanModeRan(scan_Mode,myactivity, surfaceView, 0, true, 15*1000);
			initflag = true;
			String[] func = new String[g_RandomCycle];
			StringBuilder funcStr = new StringBuilder();
			while(cnt > 0)
			{
				if(gui.cls_show_msg1(gScreenTime, "%s随机组合测试中...\n还剩%d次(已成功%d次),按[取消]退出测试...",scanModeStr,cnt,succ)==ESC)
					break;
							
				for(int i=0;i<g_RandomCycle;i++){
					func[i] = scanFuncArr[random.nextInt(len)];
					funcStr.append(func[i]).append("-->");
					if((i+1)%10 == 0 || i == g_RandomCycle-1){
						gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为：\n" + funcStr.toString(),bak-cnt+1);
						funcStr.setLength(0);
					}
				}
				cnt--;
				boolean ret = false;
				
				for(int i=0;i<g_RandomCycle;i++){
					gui.cls_show_msg1(gScreenTime,"随机第%d组第%d项,正在测试%s",bak-cnt,i+1,func[i]);
					ScanFuncName fname = ScanFuncName.valueOf(func[i]);
					if(!(ret=test(fname))){
						gui.cls_show_msg1_record(fileName, "scan200Test", gKeepTimeErr,"%s第%d组第%d项,%s方法测试失败",TESTITEM,bak-cnt,i+1,func[i]);
						break;
					}
				}
				
				if(!ret){
					for(int i=0;i<g_RandomCycle;i++){
						funcStr.append(func[i]).append("-->\n");
					}
					gui.cls_show_msg1_record(fileName, "scan200Test", gKeepTimeErr,"第%d组随机测试失败,测试顺序为：%s",bak-cnt,funcStr.toString());
					funcStr.setLength(0);
				} else{
					succ++;
				}
				//后置
				releaseScan();
				initflag = false;
			}
			if(scan_Mode != Scan_Mode.MODE_ONCE){
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}
			gui.cls_show_msg1_record(fileName, "scan200Test", gScreenTime, "%s随机组合测试完成,已执行次数为%d,成功为%d次\n请检查Scan模块内其他用例是否能正常使用！",scanModeStr, bak-cnt,succ);

		} catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan200Test", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan200Test", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			releaseScan();
			gui.cls_show_msg1_record(fileName, "scan200Test", gKeepTimeErr,"抛出%s异常", e.getMessage());
		}
	}
	
	public boolean test(ScanFuncName fname){
		
		int ret = -1;
		String resultCode;
		StringBuffer result = new StringBuffer();
		byte[] sn32 = new byte[32];
		int rand;
		int total = 10;
		
		switch(fname){
		case Init1:
			
			// 初始化单次扫码，超时时间15s，对焦灯常亮，蜂鸣器不响
			if((ret = scan_domestic.init(ScanUtil.MODE_ONCE,MAXWAITTIME,ScanUtil.FOCUS_ON,false))!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:初始化硬解码(1)失败（ret = %d）", Tools.getLineInfo(),ret);
				return false;
			}
			initflag = true;
			break;
		case Init2:
			// 默认初始化单次扫码，超时时间3s，对焦灯识读时闪，蜂鸣器响
			if((ret = scan_domestic.init())!=ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:初始化硬解码(2)失败（ret = %d）", Tools.getLineInfo(),ret);
				return false;
			}
			initflag = true;
			break;
		case setModeContinuous:
			//初始化为单次，应不起任何效果
			if((ret = scan_domestic.setModeContinuous(ScanUtil.CONTINUOUS_GAP_TIME, ScanUtil.CONTINUOUS_COUNT))!= ScanUtil.SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:硬解码模式设置失败（%d）", Tools.getLineInfo(),ret);
				return false;
			}
			break;
		case hard_doScan:
			if(initflag){
				while (total>0) 
				{
					if (gui.cls_show_msg1(1, total+"s后开始扫码,请将条形码或二维码放在前置摄像头20-30处,[取消]退出")==ESC)
						return false;
					total = total -1;
				}
				if((ret = scanTip( "", handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:硬解码扫码失败（ret = %d）", Tools.getLineInfo(),ret);
					return false;
				}
			} else{
				
				resultCode = (String)scan_domestic.doScan();
				if(resultCode!=null)
				{
					gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:硬解码扫码流程异常失败", Tools.getLineInfo());
					return false;
				}
			}
			break;
		/*case stopScan:
			break;*/
		case release:
			releaseScan();
			initflag = false;
			break;
		case openLight:
			
			scan_domestic.openLight();
			break;
		case closeLight:
		
			scan_domestic.closeLight();
			break;
		case soft_doscan:
			if(initflag){
				/*if(setpower){
					gui.cls_show_msg("芯片上下电会清除设备认证状态，请将设备休眠后再唤醒，完成任意键继续");
					setpower = false;
				}*/
				while (total>0) 
				{
					if (gui.cls_show_msg1(1, total+"s后开始扫码，软解，有预览界面，请将条形码或二维码放在后置摄像头20-30cm处，【取消】退出")==ESC)
						return false;
					total = total -1;
				}
				/*if(gui.cls_show_msg("软解，有预览界面，请将条形码或二维码放在后置摄像头20-30cm处，【其他】完成，【取消】退出")==ESC)
					return false;*/
				if((ret = scanTip("",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:软解码扫码失败（ret = %d）", Tools.getLineInfo(),ret);
					return false;
				}
			}else {
				
				resultCode = (String) scan_domestic.doScan();
				if(resultCode!=null)
				{
					gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:软解码扫码流程异常失败", Tools.getLineInfo());
					return false;
				}
			}	
			break;
		case setDecodeScreenResolution:
			//参数为null，应全屏显示解析框
			if(initflag){
				scan_domestic.setDecodeScreenResolution(null);
			}
			break;
		case doScan_InputStream:
			InputStream corrIn = BitmapDeal.bitmap2InputStream(BitmapDeal.getDiskBitmap(corPath));
			result.delete(0, result.length());
			result.append(ScanUtil.doScan(corrIn));
			if(!result.toString().equals("285614263499135991"))
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:图片解析扫码错误(预期码值：285614263499135991，实际码值：%s)",Tools.getLineInfo(),result.toString());
				return false;
			}
			break;
		/*case setThk88Power:
			//随机进行上电(1)或下电(0),芯片上下电会清除设备认证状态，需要休眠唤醒或重启后才可使用扫码
			rand = random.nextInt(2);
			setpower = true;
			if((ret = scanUtil.setThk88Power(rand))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1(gKeepTimeErr, SERIAL,"line %d:芯片随机上下电失败ret = %d", Tools.getLineInfo(),ret);
				return false;
			}
			String power = rand == 0? "下电":"上电";
			gui.cls_show_msg1(1,power + "成功！");
			break;*/
		case getThk88ID:
			if((ret = scan_domestic.getThk88ID(sn32))!=ScanUtil.NLS_SUCCESS)
			{
				gui.cls_show_msg1_record(fileName, "test", gKeepTimeErr,"line %d:读取芯片SN长度=32测试失败(%d)", Tools.getLineInfo(),ret);
				return false;
			}

			break;
		case getNLSVersion:
			String version = scan_domestic.getNLSVersion();
			gui.cls_show_msg1(1, "NLS解码库版本为:"+version);
			break;
		case setNlsScn:
			//随机开启码制
			rand = random.nextInt(codeArr.length);
			scan_domestic.setNlsScn(codeArr[rand], "Enable", "1"); //上下电也会影响设置成败，判断较麻烦，故先忽略结果
			/*if(initflag){
				if((ret = scanUtil.setNlsScn(codeArr[rand], "Enable", "1"))!=1)
				{
					gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:打开%s码制失败(ret=%d)", Tools.getLineInfo(),codeArr[rand],ret);
					return false;
				}
			} else{
				if((ret = scanUtil.setNlsScn(codeArr[rand], "Enable", "1"))!=-1)
				{
					gui.cls_show_msg1(gKeepTimeErr,SERIAL, "line %d:打开%s码制流程异常返回错误(ret=%d)", Tools.getLineInfo(),codeArr[rand],ret);
					return false;
				}
			}*/
			break;
		case zxing_scanUtil:
			if(!initflag){
				scan_domestic = new ScanUtil(myactivity, surfaceView, 0, true, 15*1000);
				
				initflag = true;
			}
			break;
		case nls0_scanUtil:
			if(!initflag){
				scan_domestic = new ScanUtil(myactivity, surfaceView, 0, true, 15*1000,0);
				initflag = true;
			}
			break;
		case nls1_scanUtil:
			if(!initflag){
				
				scan_domestic = new ScanUtil(myactivity, surfaceView, 0, true, 15*1000,1);
				initflag = true;
			}
			break;
			
		case setLedsetRedLed:
			int value = random.nextInt(2);
			scan_domestic.setLED(value);
			scan_domestic.setRedLED(value);

			break;
		default:
			break;
		}
		return true;
	} 
	public enum ScanFuncName{
		Init1,
		Init2,
		setModeContinuous,
		hard_doScan,
		/*stopScan, //只针对手动模式进行手动关闭扫码,未开启手动模式时调用到会终止用例进程，故不加入随机组合*/
		release,
		openLight,
		closeLight,
		soft_doscan,
		setDecodeScreenResolution,
		doScan_InputStream,//解析图片
		/*setThk88Power,	//上下电之后要手动休眠唤醒才能扫码，为实现自动先不加入随机组合*/
		getThk88ID,
		getNLSVersion,
		setNlsScn,
		setLedsetRedLed,          // 控制对焦灯和LED灯
		zxing_scanUtil,
		nls0_scanUtil,
		nls1_scanUtil
	}
	
	public int scanTip(String message2,Handler handler) 
	{
		StringBuffer result = new StringBuffer();
		result.delete(0, result.length());
		result.append(scan_domestic.doScan());
		int total = 5;
		if(result.toString().startsWith("F"))
		{
			mCodeResult = result.toString().substring(1);
			return NDK_SCAN_FAULT;
		}
		else if(result.toString().equals("null"))
		{
			return NDK_SCAN_COTINUE_NULL;
		}
		else 
		{
			// 获取扫码的结果
			mCodeResult = result.toString().startsWith("S")==true?  result.toString().substring(1):result.toString();
			while (true) 
			{
				if (gui.cls_show_msg1(1, message2+"码值:"+mCodeResult+",若与实际码值不一致请在"+total+"s内点[取消]")==ESC){
					return NDK_SCAN_DATA_ERR;
				}
				total = total -1;
				if(total == 1){
					return NDK_SCAN_OK;
				}
			}
		}
	}
	
	public void initScanModeRan(Scan_Mode scan_Mode,Context myactivity,SurfaceView surfaceView,int cameraId,boolean soundEnable,int timeout)
	{
		switch (scan_Mode) 
		{
		case MODE_ONCE:
		case MODE_CONTINUALLY:
		case MODE_MANUALLY:
			scan_domestic = new ScanUtil(myactivity);
			scan_domestic.init(scan_Mode.ordinal(), timeout, ScanUtil.FOCUS_ON, true);
			break;
			
		case ZXING:
			scan_domestic = new ScanUtil(myactivity);
			break;
			
		case ZXING_MANUALLY:
			scan_domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable);
			break;
			
		case NLS_0:
			scan_domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,0);
			break;
			
		case NLS_1:
			scan_domestic = new ScanUtil(myactivity, surfaceView, cameraId, soundEnable, timeout,1);
			
			break;

		default:
			break;
		}
	}
	
	
	public void releaseScan()
	{
		if(scan_domestic!=null)
		{
			if(GlobalVariable.currentPlatform==Model_Type.N900_3G)
				scan_domestic.relese();
			else
				scan_domestic.release();
		}
	}
	
	@Override
	public void onTestUp() {

	}

	@Override
	public void onTestDown() {
		releaseScan();
		gui = null;
	}

}
