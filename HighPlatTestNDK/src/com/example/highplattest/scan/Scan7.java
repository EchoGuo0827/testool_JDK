package com.example.highplattest.scan;

import java.io.IOException;
import android.annotation.SuppressLint;
import android.view.View;
import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.ScanDefineInfo;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Scan_Mode;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan7.java 
 * Author 			: zhangxj
 * version 			: 
 * DATE 			: 20160323
 * directory 		:  可设置开启闪光灯、关闭闪光灯
 * description 		: openLight()、closeLight()
 * related document : 
 * history 		 	: 变更记录					变更时间			变更人员
 *			  		  created				20160328     	zhangxj
 *					  屏蔽zxing旧接口的测试		20200512		郑薛晴
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Scan7 extends UnitFragment 
{
	private String fileName=Scan7.class.getSimpleName();
	private final String TESTITEM = "(ScanUtil+软)openLight和closeLight";
	private boolean isOpenLight = false;
	private Gui gui = new Gui(myactivity, handler);
	private String[] scanModeStr = {"zxing旧接口","Nls兼容zxing扫码","Nls自动识别扫码"};
	private ScanDefineInfo mScanDefineInfo;

	public void scan7() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		mScanDefineInfo = getCameraInfo();
		while(true)
		{	
			int nkeyIn = gui.cls_show_msg("扫码配置(长按退出键可以开关闪光灯)\n2.自动识别接口测试自动识别扫码\n");//0.zxing旧接口\n1.自动识别接口兼容zxing扫码\n3.闪关灯BUG验证\n
			switch (nkeyIn) 
			{
			/**zxing接口不需测试20200710*/
			/*case '0':
				if(GlobalVariable.gCurPlatVer==Platform_Ver.A9)
				{
					gui.cls_show_msg("A9平台不支持zxing旧接口测试");
					break;
				}
				scan7Test(scanModeStr[0],Scan_Mode.ZXING);
				break;*/
				
			/*case '1':
				scan7Test(scanModeStr[1],Scan_Mode.NLS_0);
				break;*/
				
			case '2':
				scan7Test(scanModeStr[2],Scan_Mode.NLS_1);
				break;
				
		/*	case '3':
				gui.cls_show_msg1(1, "请长按数字键8进入测试");
				break;*/
				
			case '4':
				gui.cls_show_msg1(1, "请长按数字键2进入测试");
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
		
		
	}
	
	/**
	 * NLS、ZXING及兼容，修改20180123
	 * @throws IOException
	 */
	public void scan7Test(String scanModeStr,Scan_Mode scan_Mode) 
	{
		/*private & local definition*/
		// 获取扫描的工具包
		int ret = -1,cameraId;
		
		/*process body*/
		gui.cls_show_msg1(1, "%s测试中...",scanModeStr);
		
		try 
		{
			if((cameraId=mScanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
			{
				releaseScan();
				gui.cls_show_msg1(3,"注意：扫码中闪光灯是用来照明的，所以不闪烁属于正常现象");
				//case1.1:若支持后置摄像头闪光灯，开启闪光灯测试，应能成功开启
				initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, 15000);
				gui.cls_show_msg("请将条形码或二维码放在后置摄像头20-30cm处,若支持闪光灯,[长按取消]开关闪光灯,完成任意键继续");
				if((ret = scanDialog("应有闪光灯闪烁并且实际码值应为:",handler))!=NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"line %d:%s扫码失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				
				//case1.2:若支持后置摄像头闪关灯，关闭闪光灯测试，应能关闭成功
				gui.cls_show_msg("请将条形码或二维码放在后置摄像头20-30cm处,若支持闪光灯,[长按取消]开关闪光灯,完成任意键继续");
				if((ret = scanDialog("应无闪光灯闪烁并且实际码值应为:", handler))!= NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"line %d:%s扫码失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
				handler.sendEmptyMessage(HandlerMsg.SURFACEVIEW_GONE);
			}

			if((cameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
			{
				boolean frontPreview=false;
				if(mScanDefineInfo.getCameraCnt()==1)
					frontPreview=true;
				// case2.1:若支持前置摄像头闪光灯，开启闪光灯测试，应能开启成功
				releaseScan();
				initScanMode(scan_Mode,myactivity, frontPreview?surfaceView:null, cameraId, true, 15000);
				gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30cm处,若支持闪光灯,[长按取消]开关闪光灯按钮,完成任意键继续");
				if((ret = scanDialog("应有闪光灯闪烁并且实际码值应为:", handler))!= NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"line %d:%s扫码失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				// case2.2:若支持前置摄像头闪光灯，关闭闪光灯测试，应能关闭成功
				gui.cls_show_msg("请将条形码或二维码放在前置摄像头20-30cm处,若支持闪光灯,[长按取消]开关闪光灯按钮,完成任意键继续");
				if((ret = scanDialog("应无闪光灯闪烁并且实际码值应为:", handler))!= NDK_SCAN_OK)
				{
					gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"line %d:%s扫码失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
					if (!GlobalVariable.isContinue)
						return;
				}
				releaseScan();
			}
			/**USB摄像头和支付摄像头设备不带闪光灯，不需验证*/
			
			
			/*// case3:重复初始化摄像头，不会抛出异常，会报重复注册 modify by zhengxq 20181030
			releaseScan();
			initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, 15000);
			initScanMode(scan_Mode,myactivity, surfaceView, cameraId, true, 15000);
			gui.cls_show_msg("请将条形码或二维码放在%s摄像头20-30cm处,完成任意键继续",cameraId==0?"后置":"前置");
			if((ret = scanDialog("实际码值应为:", handler))!= NDK_SCAN_OK)
			{
				gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"line %d:%s扫码失败(ret = %d,code = %s)", Tools.getLineInfo(), TESTITEM,ret,mCodeResult);
				if (!GlobalVariable.isContinue)
					return;
			}*/
			gui.cls_show_msg1_record(fileName, "scan7", gScreenTime, "%s测试通过",scanModeStr);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr, "%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (NoSuchMethodError e) 
		{
			gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"%s不支持该用例", GlobalVariable.currentPlatform);
		}
		catch (Exception e) 
		{
			gui.cls_show_msg1_record(fileName, "scan7", gKeepTimeErr,"抛出异常(%s)", e.getMessage());
		}
	}
	
	
	@Override
	public boolean onLongClick(View view) {
		super.onLongClick(view);
		LoggerUtil.d("onLongClick,view="+view.getId());
		switch (view.getId()) 
		{
		case R.id.btn_key_esc:
			LoggerUtil.i("onLongClick|||btn_key_esc,isOpenLight="+isOpenLight);
			if(isOpenLight)
			{
				scanCloseLight();
				//关闭闪光灯
				isOpenLight = false;
			}
				
			else
			{
				// 打开闪光灯
				scanOpenLight();
				isOpenLight = true;
			}
			break;
			
		case R.id.btn_key_8:// 长按‘8’进行扫码初始化和打开闪光灯
			LoggerUtil.d("onLongClick|||btn_key_8");
			gui.cls_show_msg1(3, "进入闪光灯BUG验证");
			releaseScan();
			int cameraId;
			if((cameraId=mScanDefineInfo.cameraReal.get(FONT_CAMERA))!=-1)
				initScanMode(Scan_Mode.NLS_1,myactivity, surfaceView, cameraId, true, 15000);
			if((cameraId=mScanDefineInfo.cameraReal.get(BACK_CAMERA))!=-1)
				initScanMode(Scan_Mode.NLS_1,myactivity, surfaceView, cameraId, true, 15000);
			if(isOpenLight)
			{
				//关闭闪光灯
				scanCloseLight();
				isOpenLight = false;
				gui.cls_show_msg1(3, "闪光灯关闭成功(需要分别测试开关闪光灯操作才可视为测试通过),按任意键回到菜单界面");
			}
			else
			{
				// 打开闪光灯
				scanOpenLight();
				isOpenLight = true;
				gui.cls_show_msg1(3,"闪光灯打开成功(需要分别测试开关闪光灯操作才可视为测试通过),按任意键回到菜单界面");
			}
			releaseScan();
			break;
			
		
		default:
			break;
		}
		return true;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		releaseScan();
	}

}
