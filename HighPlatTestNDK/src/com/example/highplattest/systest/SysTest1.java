package com.example.highplattest.systest;

import java.util.concurrent.TimeUnit;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.CommandInvokeResult;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.ndk.JniNdk;
/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest.java
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150209
 * directory 		: 
 * description 		: 磁卡综合的测试用例
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhengxq		   20150209	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest1 extends DefaultFragment 
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest1.class.getSimpleName();
	private final String TESTITEM = "磁卡性能、压力";
	private final int MAXWAITTIME = 30*1000;
	private Gui gui = null;
	private K21DeviceResponse response;
	private byte[] retContent;
	private String ERR_CANCEL_SWIPE="取消刷卡";//myactivity.getString(R.string.systest_mag_cancel);// 取消刷卡
	private String NO_SUPPORT_AUTO="不支持自动化测试";//myactivity.getString(R.string.systest_tip);
	
	/*----------global variables declaration------------------------*/
	int tk = 0, keyin = 0;
	private boolean Isdisp = true;
	PacketBean packet = new PacketBean();
	
	public void systest1() 
	{
		gui = new Gui(myactivity, handler);
		// 自动运行
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			mag_press();
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s"+NO_SUPPORT_AUTO, TESTITEM);
			return;
		}
		while(true)
		{
			tk = GlobalVariable.selTK;
			Isdisp = GlobalVariable.isdisp;
			int nkeyIn = gui.cls_show_msg("磁卡综合测试\n0.配置\n1.磁卡综合\n");
			switch (nkeyIn) 
			{

			case '0':
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						handler.sendMessage(handler.obtainMessage(HandlerMsg.DIALOG_SYSTEST_MAG_CONFIG, packet));
					}
				}).start();
				LoggerUtil.e("wait mag config");
				synchronized (packet) {
					try {
						packet.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				LoggerUtil.e("mag config finish");
				break;

			case '1':
				mag_press();
				break;
				
			case ESC:
				gui.cls_show_msg("请换其它强/普/弱磁卡重新测试!任意键继续...");
				intentSys();
				return;

			}
		}
	}
	
	private void mag_press()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)// Poynt和X5的产品不支持mpos
		{
			mag_jni_press(tk, Isdisp, packet.getLifecycle(),packet.getCard_number());
		}
		else
		{
			int nkeyIn = gui.cls_show_msg("磁卡综合压力\n0.压力测试(jni)\n1.压力测试(mpos)");
			switch (nkeyIn) 
			{
			case '0':
				mag_jni_press(tk, Isdisp, packet.getLifecycle(),packet.getCard_number());
				break;
				
			case '1':
				mag_mpos_press(packet.getLifecycle(),packet.getCard_number());
				break;

			default:
				break;
			}
		}
	}
	
	//add by 20180521 wangxy
	private void mag_mpos_press(int test_times,int card_num) 
	{
		/*private & local definition*/
		K21ControllerManager k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		CommandInvokeResult ret;
		int[] strCountErr = new int[card_num],strCountOK = new int[card_num];
		StringBuffer strResult = new StringBuffer();
		int len=7;//1=第一磁道，2=第二磁道，4=第三磁道,可组合使用
		byte[] pack = CalDataLrc.mposPack(new byte[]{(byte) 0xD1,0x04},new byte[]{(byte) len});
		/*process body*/
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		int len1=0,len2=0,len3=0,begin1=0,begin2=0,begin3=0;
		String content1,content2,content3;
		for (int i = 0; i < card_num; i++) 
		{
			for (int j = 0; j < test_times; j++) 
			{
				if(gui.cls_show_msg1(3,"正在进行第%d张卡,第%d次测试,[取消]退出测试", i+1,j+1)==ESC)
				{
					break;
				}
				gui.cls_printf(String.format("请刷银行卡(%ds)--->\n", MAXWAITTIME/1000).getBytes());
				response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D101),MAXWAITTIME,TimeUnit.MILLISECONDS, null);
				if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
				{// 刷卡成功
					response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack), null);// 获取磁卡三个磁道的信息
					if ((ret = response.getInvokeResult()) == CommandInvokeResult.SUCCESS) 
					{
						retContent = response.getResponse();
						begin1 = 41;
						if ((len1 = Integer.valueOf(ISOUtils.hexString(retContent, begin1, 2))) != 0) 
						{// 一磁道有数据
							content1 = ISOUtils.dumpString(retContent, begin1 + 2, len1);
						} else 
						{
							len1 = 0;
							content1 = "";
						}

						if ((len2 = Integer.valueOf(ISOUtils.hexString(retContent, begin1 + 2 + len1, 2))) != 0) 
						{
							begin2 = begin1 + 2 + len1;
							content2 = ISOUtils.dumpString(retContent, begin2 + 2, len2);
						} else 
						{
							len2 = 0;
							content2 = "";
						}

						if ((len3 = Integer.valueOf(ISOUtils.hexString(retContent, begin2 + 2 + len2, 2))) != 0) 
						{
							begin3 = begin2 + 2 + len2;
							content3 = ISOUtils.dumpString(retContent, begin3 + 2, len3);
						} else 
						{
							len3 = 0;
							content3 = "";
						}

						gui.cls_show_msg1(2,"1道数据(%d):%s\n2道数据(%d):%s\n3道数据(%d):%s\n", len1, content1, len2, content2,len3, content3);
						strCountOK[i]++;
					} else 
					{
						gui.cls_show_msg1_record(TAG, "mag_mpos_press", g_keeptime,"line %d:%s刷磁卡测试失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
						strCountErr[i]++;
						if (GlobalVariable.isContinue == false)
							return;
					}
				    //case5:关闭读卡器
					response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Mag_D102), null);
					if ((ret = response.getInvokeResult()) != CommandInvokeResult.SUCCESS) 
					{
						gui.cls_show_msg1_record(TAG, "mag_mpos_press", g_keeptime, "line %d:%s关闭读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
						strCountErr[i]++;
						if (GlobalVariable.isContinue == false)
							return;
					}
				} else 
				{
					gui.cls_show_msg1_record(TAG, "mag_mpos_press", g_keeptime, "line %d:%s打开读卡器失败(%s)", Tools.getLineInfo(), TESTITEM, ret);
					strCountErr[i]++;
					if (GlobalVariable.isContinue == false)
						return;
				}
			}
			strResult.append(String.format("第%d张卡,共错误%d次刷卡成功率:正确%d次/总%d次\n", i+1,strCountErr[i],strCountOK[i],test_times));
			// 每张卡成功率
			gui.cls_show_msg("第%d张卡,共错误%d次刷卡成功率:正确%d次/总%d次\n", i+1,strCountErr[i],strCountOK[i],test_times);
		}
		
		gui.cls_show_msg1_record(TAG, "mag_mpos_press", g_time_0,strResult.toString());
	}

	// add by 20150210
	// 磁卡的压力
	public void mag_jni_press(int selTK,boolean isdisp,int test_times,int card_num) 
	{
		/*private & local definition*/
		int ret = 0;
		int[] strCountErr = new int[card_num],strCountOK = new int[card_num];
		StringBuffer strResult = new StringBuffer();
		
		/*process body*/
		for (int i = 0; i < card_num; i++) 
		{
			for (int j = 0; j < test_times; j++) 
			{
				if(gui.cls_show_msg1(3,"%dst card is being tested,the %d test,Cancel button to exit the test", i+1,j+1)==ESC)
					break;
				
				//磁卡注册
				if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue(),maglistener)) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_jni_press", g_keeptime, "line %d:mag事件注册失败(%d)",Tools.getLineInfo(), ret);
					//注销事件
					JniNdk.JNI_SYSUnRegisterEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
					return;
				}
				
				switch ((ret = MagcardReadTest(selTK, isdisp, MAXWAITTIME))) 
				{
				case NDK_ERR_QUIT:
					gui.cls_show_msg1(2, "%s,line %d:"+ERR_CANCEL_SWIPE, TAG,Tools.getLineInfo());
					break;

				case STRIPE:
					strCountOK[i]++;
					break;

				case NDK_ERR_TIMEOUT:
				case NDK_ERR:
				default:
					strCountErr[i]++;
					break;
				}
				if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue())) != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_jni_press", g_keeptime, "line %d:mag事件解绑失败(%d)",Tools.getLineInfo(), ret);
					return;
				}
			}
			// 每张卡成功率
			strResult.append(String.format("%dst card,error count:%d,success count:%d,total count:%d\n", i+1,strCountErr[i],strCountOK[i],test_times));
			gui.cls_show_msg("%dst card,error count:%d,success count:%d,total count:%d\n", i+1,strCountErr[i],strCountOK[i],test_times);
		}
		UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_MAGCARD.getValue());
		gui.cls_show_msg1_record(TAG, "mag_jni_press", g_time_0,strResult.toString());
	}
	// end by 20150210
}
