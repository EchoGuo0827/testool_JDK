package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.ModemBean;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.newland.NlModemManager;
import android.newland.content.NlContext;
import android.os.RemoteException;
import android.util.Log;
/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest8.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20150123
 * directory 		: 
 * description 		: Modem压力、性能测试
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq     	  20150123	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest8 extends DefaultFragment implements NDK
{
	/*---------------constants/macro definition---------------------*/
	private final String TAG = SysTest8.class.getSimpleName();
	private final String TESTITEM = "Modem压力、性能测试";
	private final int MAX_PHONENUM_SIZE = 16;
	private final String LOG_NACTEST = GlobalVariable.sdPath+"system2.txt";
	private final String phonenumFile = GlobalVariable.sdPath+"phonenum.txt";
	private NlModemManager nlModemManager;
	private Gui gui = new Gui(myactivity, handler);
	private Layer layer;
	
	/*------------global variables definition-----------------------*/
	int isucc = 0;
	float call_time = 0.0f,hangdowntime = 0.0f;
	int g_keeptime = 0;
	
	
	public void systest8() 
	{
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, TAG, 2,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		nlModemManager =  (NlModemManager) myactivity.getSystemService(NlContext.NLMODEM_SERVICE);
		layer = new Layer(myactivity, handler);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("MODEM综合测试\n0.MODEM配置\n1.压力\n2.性能\n3.MODEM异常测试\n");
			switch (nkeyIn) 
			{
			
			case '0':
				new Config(myactivity,handler).config_para();
				break;
		
			case '1':
				modem_press();
				break;
			
			case '2':
				modem_ability();
				break;
			
			case '3':
				MDM_abnormal();
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	// modem的压力测试
	public void modem_press() 
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("MODEM综合测试\n0.通讯压力\n1.拨号压力\n2.通讯拨号压力\n3.复位拨号压力\n");
			switch (nkeyIn) 
			{
			case '0':
				comm_press(ModemBean.MDMDialStr);
				break;
			
			case '1':
				call_press();
				break;
				
			case '2':
				call_comm_press();
				break;
				
			case '3':
				reset_call_press();
				break;
				
			case ESC:
				return;
			}
		}

	}
	
	/**
	 *  modem性能测试
	 */
	public void modem_ability() 
	{
		/* private & local definition */
		int nkeyIn = gui.cls_show_msg("MODEM性能测试\n0.同步MODEM简易\n1.同步MODEM高级\n2.异步MODEM\n");
		switch (nkeyIn) 
		{
		case 0:
			if (ModemBean.type_MDM != LinkType.SYNC) 
			{
				gui.cls_show_msg1(1,"MODEM类型配置错误!\n请先进行配置...");
				break;
			}
			call_press();
			break;

		case 1:
			if (ModemBean.type_MDM != LinkType.SYNC)
			{
				gui.cls_show_msg1(1, "MODEM类型配置错误!\n请先进行配置...");
				break;
			}
			reset_call_press();
			break;

		case 2:
			if (ModemBean.type_MDM != LinkType.ASYN) 
			{
				gui.cls_show_msg1(1,"MODEM类型配置错误!\n请先进行配置...");
				break;
			}
			call_press();
			break;

		case 3:
			NAC_test();
			break;

		case 4:
			break;

		}
	}
	
	// modem异常测试
	public void MDM_abnormal() 
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("MODEM异常测试\n0.非法号码拨号\n1.同步异步切换\n");
			switch (nkeyIn) 
			{
			
			case '0':
				modem_abnormal();
				break;

			case '1':
				modem_abnormal2();
				break;

			case ESC:
				return;
			}
		}
	}
	
	/*// 显示modem测试的选择界面
	public void showModem(final String title,final String[] listName) 
	{
		mActivity.runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				new ShowDialog().baseDialog(mActivity, title, listName);
				if(GlobalVariable.IS_THREAD_OVER)
					return;
			}
		});
		mInstrumentation.waitForIdleSync();
	}*/
	
	@SuppressLint("DefaultLocale")
	public void NAC_test() 
	{
		/*private & local definition*/
		String[] allphonenum = new String[MAX_PHONENUM_SIZE]; 
		FileSystem fileSystem = new FileSystem();
		// 拨号次数和成功次数
		int icount = 0,isucc = 0;
		// 拨号时间和挂断时间
		float call_time = 0.0f,hangdowntime = 0.0f;
		String resultbuf ;
		
		/*process body*/
		// 导出还是删除旧的log文件
		if(fileSystem.JDK_FsExist(LOG_NACTEST) == NDK_OK)
		{
			// 删除
			int nkeyIn = gui.cls_show_msg("检测到旧LOG\n0.删除\n1.继续测试\n");
			switch (nkeyIn) 
			{
				
			case '0':
				fileSystem.JDK_FsDel(LOG_NACTEST);
				break;
				
			case '1':
				break;
			
			case ESC:
				return;
			}
		}
		
		try 
		{
			if(getAllPhoneNum(allphonenum, allphonenum.length)!=NDK_OK)
				return;
		} catch (Exception e) 
		{
			return;
		}
		if(new FileSystem().JDK_FsOpen(LOG_NACTEST, "W")<0)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:创建%s失败", LOG_NACTEST);
			return;
		}
		Log.e(TAG, allphonenum.length+"");
		for (int i = 0; i < allphonenum.length; i++) 
		{
			try 
			{
				icount = nacMdmDialPress(allphonenum[i], 5);
				if(isucc !=0)
					resultbuf = String.format("%s拨%d次，成功%d次。拨号:%2.3fs/次,挂断:%2.3fs/次\r\n", allphonenum[i],icount,isucc,call_time/isucc,hangdowntime/isucc);
				else
					resultbuf = String.format(Locale.CHINA,"%s拨%d次，成功%d次。\r\n", allphonenum[i],icount,isucc);
				if(gui.cls_show_msg1_record(TAG, TESTITEM,5, resultbuf+"，【取消】键退出测试...")==ESC)
					break;

				if(fileSystem.JDK_FsWrite(LOG_NACTEST, resultbuf.getBytes(), resultbuf.length(), 2) != resultbuf.length())
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:写%s失败", Tools.getLineInfo(),resultbuf);
					break;
				}
				
			} catch (Exception e) {
 				e.printStackTrace();
			}
		}
		gui.cls_show_msg1(1,"测试完成");
	}
	
	private int getAllPhoneNum(String[] allPhoneNum,final int maxbufLen) throws Exception
	{
		Log.e(TAG, "getAllPhoneNum");
		/*private & local definition*/
		int readLen = 0;
		int phoneNumFileLen = 0;
		FileSystem fileSystem = new FileSystem();
		
		/*process body*/
		// 1.读取全部电话号码文件到缓冲
		if(fileSystem.JDK_FsExist(phonenumFile)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:测试文件不存在", Tools.getLineInfo());
			return NDK_ERR;
		}
		
		if(fileSystem.JDK_FsOpen(phonenumFile, "r")<0)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:打开%s失败", Tools.getLineInfo(),phonenumFile);
			return NDK_ERR;
		}
		
		if(fileSystem.JDK_FsFileSize(phonenumFile)<0)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:获取%s文件长度失败", Tools.getLineInfo(),phonenumFile);
			return NDK_ERR;
		}
//		if(ret>maxbufLen-1)
//		{
//			new Gui().cls_show_msg1(2, "line %d:无法读出完整文件内容！", getLineInfo());
//			return NDK_ERR;
//		}
		
		if(fileSystem.JDK_FsReadLine(phonenumFile, allPhoneNum)<0)
		{
			gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:读文件出错%d", Tools.getLineInfo(),readLen);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	private int nacMdmDialPress(String dialStr,int maxCnt) 
	{
		int icount = 0,ret = -1;
		long startTime;
		
		while(true)
		{
			if(gui.cls_show_msg1(3, "第%d次拨%s中（已成功%d次，【取消】键退出测试）", icount+1,dialStr,isucc)==ESC)
				break;
			if(maxCnt == 0 || maxCnt == -1);
			else if(icount == Math.abs(maxCnt))
				break;
			icount++;
			if(maxCnt<0)
			{
				// modem复位
				gui.cls_show_msg1(2, "复位Modem中（第%d次）...", icount);
				mdm_reset(nlModemManager);
			}
			if(maxCnt>=0 && icount ==1)
			{
				mdm_reset(nlModemManager);
			}
			// 初始化modem
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", icount);
			startTime = System.currentTimeMillis();
			if((ret = mdm_init(nlModemManager))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"modem第%d次初始化失败%d", icount,ret);
				continue;
			}
			
			// 拨号
			gui.cls_show_msg1(2, "MODEM拨%s中（第%d次）...", dialStr,icount);
			startTime = System.currentTimeMillis();
			if((ret = layer.mdm_dial(dialStr, nlModemManager))!= NDK_OK)
			{
				layer.mdm_hangup(nlModemManager);
				// 出错结果输出
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"modem第%d次拨%s失败%d", icount,dialStr,ret);
				continue;
			}
			call_time = call_time+Tools.getStopTime(startTime);
			isucc++;
			
			// 挂断
			gui.cls_show_msg1(1, "MODEM挂断中（第%d次）...",icount);
			startTime = System.currentTimeMillis();
			if((ret = layer.mdm_hangup(nlModemManager))!= NDK_OK)
			{
				// 出错信息
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"modem第%d次拨%s失败%d", icount,dialStr,ret);
				continue;
			}
			hangdowntime = hangdowntime+Tools.getStopTime(startTime)-(ModemBean.type_MDM == LinkType.SYNC?5:0);
		}
		return icount;
	}
	
	
	// modem异常测试
	public void modem_abnormal() 
	{
		/*private & local definition*/
		int icount = 0,ret = -1;
		String dialStr1 = "5840";
		String dialStr2 = "7540";
		String dialStr = "0";
		
		/*process body*/
		gui.cls_show_msg1(1, "modem异常测试中");
		while(true)
		{
			if(++icount == 4)
				break;
			// 初始化modem
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", icount);
			if((ret = mdm_init(nlModemManager)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line  %d:初始化MODEM（第%d次）失败（%d）", Tools.getLineInfo(),icount,ret);
				return;
			}
			
			// 拨号
			dialStr = "0";
			if(icount == 1)
				dialStr = dialStr1;
			else if(icount == 2)
				dialStr = dialStr2;
			else
				dialStr = ModemBean.MDMDialStr;
			gui.cls_show_msg1(2, "MODEM拨%s中（第%d次）...", dialStr,icount);
			if(layer.mdm_dial(dialStr, nlModemManager)!= NDK_OK)
			{
				if(icount == 3)
				{
					layer.mdm_hangup(nlModemManager);
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:MODEM异常测试失败", Tools.getLineInfo());
					return;
				}
				layer.mdm_hangup(nlModemManager);
				continue;
			}
			// 挂断
			gui.cls_show_msg1(2,"MODEM挂断中（第%d次）...", icount);
			layer.mdm_hangup(nlModemManager);
			break;
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"MODEM异常测试通过");
	}
	
	// 验证进行同步/异步初始化切换后应该能够正常拨号
	public void modem_abnormal2() 
	{
		/*private & local definition*/
		String dialStr = "0";
		LinkType type = ModemBean.type_MDM;
		int ret = -1;
		
		/*process body*/
		gui.cls_show_msg1(2, "MODEM异常测试中");
		if(type == LinkType.ASYN)
			try {
				nlModemManager.sdlcInit(ModemBean.MDMPatchType);
			} catch (RemoteException e) {
				e.printStackTrace();
				return;
			}
		else if(type == LinkType.SYNC)
		{
			try {
				nlModemManager.sdlcInit(ModemBean.MDMPatchType);
				nlModemManager.asynInit(ModemBean.MDMPatchType);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}
		for (int j = 0; j < 2; j++) 
		{
			gui.cls_show_msg1(2, "初始化MODEM中...");
			if((ret = mdm_init(nlModemManager))!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:第%d次初始化MODEM失败（%d）异常测试失败", Tools.getLineInfo(),j+1,ret);
				return;
			}
			dialStr = ModemBean.MDMDialStr;
			if(layer.mdm_dial(dialStr, nlModemManager)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime,"line %d:第%d次MODEM拨号测试失败%d", Tools.getLineInfo(),j+1,ret);
				if((ret = layer.mdm_hangup(nlModemManager))!=NDK_OK)
				{
					gui.cls_show_msg1_record(TAG, TESTITEM, g_keeptime, "line %d:modem链路挂断失败（ret = %d）", Tools.getLineInfo(),ret);
				}
				return;
			}
			// 挂断
			gui.cls_show_msg1(1, "MODEM挂断中...");
			if(layer.mdm_hangup(nlModemManager)!= NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM, g_keeptime,"line %d:第%d次MODEM挂断失败异常测试失败%d", Tools.getLineInfo(),j+1,ret);
				return;
			}
		}
		gui.cls_show_msg1_record(TAG, TESTITEM, g_time_0,"MODEM异常测试通过");
	}
	
	
	/**
	 *  modem通讯压力
	 * @param dailStr 拨号的号码，同步拨号号码7289
	 */
	public void comm_press(String dailStr) 
	{
		Log.e("comm_press", ModemBean.type_MDM+"");
		int ret = -1;
		long startTime;
		LinkType type = ModemBean.type_MDM;
		int i = 0,succ_count = 0;
		int[] compare = {0,-1};
		int sen_len = 0,rec_len = 0,len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		float sendTime = 0.0f,revTime = 0.0f;
		
		/*process body*/
		sendPacket = init_snd_packet(sendPacket, buf);
		sendPacket = set_snd_packet(sendPacket,type);
//		mdm_clrportbuf_all(nlModemManager);
		mdm_reset(nlModemManager);
		if((ret =layer.linkUP(nlModemManager,type))!=NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime,"line %d:创建链路失败(ret=%d)", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			if(gui.cls_show_msg1(3, "进行第%d次通讯中（已成功%d次），【取消】键退出测试", i+1,succ_count)==ESC)
				break;

			if(update_snd_packet(sendPacket,type)!= NDK_OK)
				break;
			i++;
			compare[0]++;
			// 发送数据
			startTime = System.currentTimeMillis();
			if((len = mdm_send(nlModemManager,sendPacket.getHeader(), sendPacket.getLen()))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime, "line %d:发送数据失败（实际%d，预期%d）",Tools.getLineInfo(),len,sendPacket.getLen());
				mdm_clrportbuf_all(nlModemManager);
				if(Tools.IsContinuous(compare))
				{
					layer.linkDown(nlModemManager,type);
				}
				else
					continue;
			}
			
			sendTime = sendTime + Tools.getStopTime(startTime);
			sen_len = sen_len + len;
			// 接收数据
			startTime = System.currentTimeMillis();
			if((len = mdm_rev(nlModemManager,rbuf, sendPacket.getLen(), 30,type))!= sendPacket.getLen())
			{
				gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime, "line %d:接收数据失败（实际%d，预期%d）",Tools.getLineInfo(),len,sendPacket.getLen());
				mdm_clrportbuf_all(nlModemManager);
				if(Tools.IsContinuous(compare))
				{
					layer.linkDown(nlModemManager,type);
				}
				else 
					continue;
			}
			revTime = revTime+Tools.getStopTime(startTime);
			rec_len = rec_len +len;
			// 比较数据
			if(Tools.MemCmp(sendPacket.getHeader(), rbuf, sendPacket.getLen(),type)!= NDK_OK)
			{
				mdm_clrportbuf_all(nlModemManager);
				if(Tools.IsContinuous(compare))
				{
					if((ret = layer.linkDown(nlModemManager,type))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime, "line %d:modem挂断失败（ret = %d）", Tools.getLineInfo(),ret);
						return;
					}
					// 循环
				}
				else
					continue;
			}
			succ_count++;
		}
		
		if(type == LinkType.SYNC)
		{
			try {
				if((ret = nlModemManager.check()) != MODEM_CONNECT_AFTERPREDIAL)
				{
					if((ret = layer.linkDown(nlModemManager,type))!=NDK_OK)
					{
						gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime, "line %d:modem挂断失败（ret = %d）", Tools.getLineInfo(),ret);
						return;
					}
					gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime,"line %d:SDLC_getmodemstatus发生错误%d", Tools.getLineInfo(),ret);
					return;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime,"line %d:SDLC_getmodemstatus发生错误(%s)", Tools.getLineInfo(),e.getMessage());
				return;
			}
		}
		
		if(layer.linkDown(nlModemManager,type) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "comm_press",g_keeptime, "line %d:断开链路失败", Tools.getLineInfo());
			return;
		}
		if(type == LinkType.SYNC)
		{
			try {
				if((ret = nlModemManager.check())!= MODEM_NOPREDIAL)
				{
					gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime,"line %d:SDLC_getmodemstatus发生错误", Tools.getLineInfo());
					return;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "comm_press", g_keeptime,"line %d:SDLC_getmodemstatus发生错误(%s)", Tools.getLineInfo(),e.getMessage());
				return;
			}
		}
		gui.cls_show_msg1_record(TAG, "comm_press", g_time_0,"Total:%d,succ:%d", i,succ_count);
		if(sendTime!=0&&revTime!=0)
			gui.cls_show_msg1_record(TAG, "comm_press",g_time_0, "发送:%fB/s,接收:%fB/s\n", sen_len/sendTime,rec_len/revTime);
		return;
	}
	
	/**
	 * 拨号压力，20次
	 */
	public void call_press()
	{
		/*private & local definition*/
		
		/*process body*/
		// 连续压力
		if(!GlobalVariable.gSequencePressFlag)//20170116wangxiaoyu
			gui.cls_show_msg1_record(TAG, "call_press", g_time_0,"循环了%d次，成功了%d次", _mdm_dial_press(ModemBean.MDMDialStr, getCycleValue()),isucc);
		else
			gui.cls_show_msg1_record(TAG, "call_press", g_time_0,"循环了%d次，成功了%d次", _mdm_dial_press(ModemBean.MDMDialStr, 20),isucc);
		if(isucc != 0)
		{
			gui.cls_show_msg1_record(TAG, "call_press",g_time_0, "拨%s平均时间%3.5f秒/次，挂断平均时间%3.5f秒/次", ModemBean.MDMDialStr,call_time/isucc,hangdowntime/isucc);
		}
	}
	
	/**
	 * 通讯拨号压力
	 */
	public void call_comm_press() 
	{
		LinkType type = ModemBean.type_MDM;
		float[] caltime = new float[2];
		int icount = 0,isucc = 0;
		float call_time = 0.0f,hangdowntime = 0.0f;
		PacketBean sendpacket=new PacketBean();
		byte[] buf = new byte[PACKMAXLEN];

		/*process body*/	
		sendpacket = init_snd_packet(sendpacket, buf);
		sendpacket = set_snd_packet(sendpacket,type);
		mdm_clrportbuf_all(nlModemManager);
		
		while(true)
		{
			if(gui.cls_show_msg1(3,"第%d次拨%s通讯中(已成功%d次)，【取消】退出测试", icount + 1, ModemBean.MDMDialStr,isucc)==ESC)
				break;
			if(update_snd_packet(sendpacket,type)!=NDK_OK)
				break;
			icount++;
			if(mdm_dial_press(nlModemManager,ModemBean.MDMDialStr, sendpacket, true, caltime)==NDK_OK)
			{
				isucc++;
				call_time = call_time+caltime[0];
				hangdowntime = hangdowntime+caltime[1];
				// 如果拨号失败的话还要继续
			}
		}
		gui.cls_show_msg1_record(TAG, "call_comm_press", g_time_0,"循环了%d次，成功了%d次", icount,isucc);
		if(isucc !=0)
			gui.cls_show_msg1_record(TAG, "call_comm_press",g_time_0, "拨%s平均时间%3.5f秒/次,挂断平均时间(含初始化)%3.5f秒/次\n", ModemBean.MDMDialStr,call_time/isucc,hangdowntime/isucc);
	}
	
	public int _mdm_dial_press(String dialStr,int maxcnt) 
	{
		/*private & local definition*/
		int icount = 0;
		float init_time = 0.0f;
		long startTime;
		isucc = 0;
		call_time = 0.0f;
		hangdowntime = 0.0f;
		Log.e(TAG, ModemBean.type_MDM+"");
		
		/*process body*/
		while(true)
		{
			if(gui.cls_show_msg1(3,"第%d次拨%s中（已成功%d次），【取消】退出测试", icount+1,ModemBean.MDMDialStr,isucc)==ESC)
				break;
			if(maxcnt == 0 || maxcnt == -1)
				;
			else if((icount - Math.abs(maxcnt))>=0)
				break;
			icount++;
			if(maxcnt<0)
			{
				// modem复位
				gui.cls_show_msg1(2, "复位Modem中（第%d次）...",icount);
				mdm_reset(nlModemManager);
			}
			if(maxcnt>=0 && icount ==1)
			{
				mdm_reset(nlModemManager);
			}
			// 初始化modem
			gui.cls_show_msg1(2, "初始化MODEM中（第%d次）...", icount);
			startTime = System.currentTimeMillis();
			if(mdm_init(nlModemManager)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG,TESTITEM,2,"modem第%d次初始化失败", icount);
				continue;
			}
			init_time = init_time+Tools.getStopTime(startTime);
			
			// 拨号
			gui.cls_show_msg1(2, "MODEM拨%s中（第%d次）...", ModemBean.MDMDialStr,icount);
			startTime = System.currentTimeMillis();
			if(layer.mdm_dial(ModemBean.MDMDialStr, nlModemManager)!= NDK_OK)
			{
				layer.mdm_hangup(nlModemManager);
				// 出错结果输出
				gui.cls_show_msg1_record(TAG,TESTITEM,2,"modem第%d次拨%s失败", icount,ModemBean.MDMDialStr);
				continue;
			}
			call_time = call_time+Tools.getStopTime(startTime);
			isucc++;
			
			// 挂断
			gui.cls_show_msg1(2, "MODEM挂断中（第%d次）...", icount);
			startTime = System.currentTimeMillis();
			if(layer.mdm_hangup(nlModemManager)!= NDK_OK)
			{
				// 出错信息
				gui.cls_show_msg1_record(TAG,TESTITEM,2,"modem第%d次拨%s失败", icount,ModemBean.MDMDialStr);
				continue;
			}
			hangdowntime = hangdowntime+Tools.getStopTime(startTime)-(ModemBean.type_MDM == LinkType.SYNC?2:0);
		}
		return icount;
	}
	
	/**
	 * 复位拨号压力，测试20次
	 */
	public void reset_call_press()  
	{
		/*process body*/
		if(!GlobalVariable.gSequencePressFlag)//20170116wangxiaoyu
			gui.cls_show_msg1_record(TAG, "reset_call_press", g_time_0,"循环了%d次，成功了%d次", _mdm_dial_press(ModemBean.MDMDialStr, -(getCycleValue())),isucc);
		else
			gui.cls_show_msg1_record(TAG, "reset_call_press",g_time_0, "循环了%d次，成功了%d次", _mdm_dial_press(ModemBean.MDMDialStr, -1),isucc);
		if(isucc != 0)
			gui.cls_show_msg1_record(TAG, "reset_call_press", g_time_0,"拨%s平均时间%3.5f秒/次，挂断平均时间%3.5f秒/次", ModemBean.MDMDialStr,call_time/isucc,hangdowntime/isucc);
		return;
	}
}
