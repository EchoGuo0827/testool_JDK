package com.example.highplattest.mpos;

import android.newland.NlManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec32
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180910
 * directory 		: 
 * description 		:在设置为共享密钥时 擦除密钥会自动安装31的TLK(mpos方式)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180910		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos19 extends UnitFragment
{
	public final String TAG = Mpos19.class.getSimpleName();
	private final String TESTITEM = "擦除密钥或清安全后自动安装TLK(mpos)";
	private NlManager mNlManager;
	byte[] ioctrl = {2};
	
	public void mpos19()
	{
		Gui gui = new Gui(myactivity, handler);
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoHand)
			return;
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)// 开发反馈PCI规范海外版本不支持NDK_SecKeyErase后自动装载TLK密钥20190718
		{
			gui.cls_show_msg1(1,"海外版本不支持该用例，长按确认键退出");
			return;
		}
		/*private & local definition*/
		byte[] recvBuf = new byte[30];
		String retCode;
		
		/*process body*/
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		
		if(gui.ShowMessageBox("测试擦除密钥后会自动安装TLK,继续按确认,跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_printf("擦除密钥后自动安装TLK...".getBytes());
			// case1：擦除密钥之后 会自动安装TLK 此时不主动安装TLK 也能安装TMK
			mNlManager.write(DataConstant.Sec_1A20_All, 20, 10);
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(recvBuf, 30, 10);
			if((retCode=ISOUtils.dumpString(recvBuf, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec32", gKeepTimeErr,"line %d:清安全寄存器失败(ret = %s)", Tools.getLineInfo(), retCode);
				if (GlobalVariable.isContinue == false)
					return;
			}
			
			// mpos方式安装TMK，已经带KCV校验
			mNlManager.write(DataConstant.Sec_1A02_TMK, 50, 10);
			mNlManager.ioctl(0x540B, ioctrl);
			// 返回报文中有带checkValue值
			mNlManager.read(recvBuf, 30, 10);
			if((retCode=ISOUtils.dumpString(recvBuf, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec32", gKeepTimeErr,"line %d:清安全寄存器失败(ret = %s)", Tools.getLineInfo(), retCode);
				if (GlobalVariable.isContinue == false)
					return;
			}

		}
		if(gui.ShowMessageBox("测试清安全后会自动安装TLK,此操作要保证机子安全触发后进入,继续按确认,跳过按取消".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{	
			gui.cls_printf("安全触发后自动安装TLK...".getBytes());
			// 清安全触发
			byte[] clearBuf = CalDataLrc.mposPack(new byte[]{(byte) 0xFF,0x03}, ISOUtils.hex2byte("08"));
			mNlManager.write(clearBuf, clearBuf.length, 30);// 该指令无响应报文
			mNlManager.ioctl(0x540B, ioctrl);
			mNlManager.read(recvBuf, 20, 10);
			LoggerUtil.d("Sec32,recv buf1:"+ISOUtils.hexString(recvBuf));
			if((retCode=ISOUtils.dumpString(recvBuf, 7, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec32", gKeepTimeErr,"line %d:清安全寄存器失败(ret = %s)", Tools.getLineInfo(), retCode);
				if (GlobalVariable.isContinue == false)
					return;
			}
			//case2:清安全之后会自动安装TLK
			// mpos方式安装TMK，已经带KCV校验
			mNlManager.write(DataConstant.Sec_1A02_TMK, 50, 10);
			mNlManager.ioctl(0x540B, ioctrl);
			// 返回报文中有带checkValue值
			mNlManager.read(recvBuf, 30, 10);
			if((retCode=ISOUtils.dumpString(recvBuf, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "sec32", gKeepTimeErr,"line %d:清安全寄存器失败(ret = %s)", Tools.getLineInfo(), retCode);
				if (GlobalVariable.isContinue == false)
					return;
			}
		}
		gui.cls_show_msg1_record(TAG, "sec32", gScreenTime,"%s测试通过", TESTITEM);
		gui = null;
	}

	@Override
	public void onTestUp() 
	{
		// 测试前置，连接K21端
		mNlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		mNlManager.setconfig(115200, 0, "8N1NN".getBytes());
		mNlManager.connect(false);
	}

	@Override
	public void onTestDown() 
	{
		mNlManager.disconnect();
	}

}
