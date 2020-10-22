package com.example.highplattest.systest;

import java.util.Arrays;
import android.os.SystemClock;
import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.LinkType;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.EthernetUtil;
//import com.example.highplattest.main.netutils.EthernetUtil;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest40.java 
 * Author 			: linwl
 * version 			: 
 * DATE 			: 20150316
 * directory 		: 
 * description 		: 打印/LAN交叉测试
 * related document :
 * history 		 	: author			date			remarks
 *  				 : 变更记录				变更时间			变更人员
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁	
 * 					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
 *					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
 *					修复以太网空指针异常问题                        20200611         	陈丁
 *					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 *					解决X5以太网和打印压测软复位现象	20200728			陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest40 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest40.class.getSimpleName();
	private final String TESTITEM = "打印/LAN";
	private EthernetPara ethernetPara = new EthernetPara();
	private WifiPara wifiPara = new WifiPara();
	NetWorkingBase[] netWorkingBases = {ethernetPara,wifiPara};
	SocketUtil socketUtil;
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private Config config;
	private PrintUtil printUtil;
	public void systest40()
	{
		String funcName = "systest40";
		gui = new Gui(myactivity, handler);
		boolean isConfigWifi=false;
		
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
//			return;
		}
		//初始化layer对象
		initLayer();
		config = new Config(myactivity, handler);
		printUtil = new PrintUtil(myactivity, handler,true);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			// 配置
			config.print_config();
			if(config.confConnLAN(wifiPara, ethernetPara)!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "systest40", g_keeptime, "line %d:网络配置失败！！！", Tools.getLineInfo());
				return;
			}
			else
				gui.cls_show_msg1(2, "网络配置成功！！！");
			try {
				cross_test();
			} catch (Exception e) {
				gui.cls_show_msg1_record(TAG, "systest40", g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/LAN\n0.打印配置\n1.LAN配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			
			case '0':
				config.print_config();
				break;
				
			case '1':
				//调用LAN配置函数
				config.confConnLAN(wifiPara, ethernetPara);
				isConfigWifi = true;
				break;
				
			case '2':
				if(isConfigWifi==false)
				{
					gui.cls_show_msg("网络未配置");
					break;
				}
				try {
					cross_test();
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "systest40", g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	/*private void cross_ttftest() {
		int j = GlobalVariable.chooseConfig;
		Sock_t[] sock_t = {ethernetPara.getSock_t(),wifiPara.getSock_t()};
		int i = 0, succ = 0, ret = 0;
		int send_len = 0,rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		int prnStatus;
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		socketUtil = new SocketUtil( netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		process body
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type[j]);
		while(true)
		{
			//保护动作
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			//测试退出点
			if(gui.cls_show_msg1(3, "%s/打印交叉测试,已执行%d次,成功%d次,[取消]退出测试", type[j],i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,type[j])!=NDK_OK)
				break;
			i++;
			
			if((ret = layerBase.netUp(netWorkingBases[j],type[j])) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:NetUp失败(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			
			// 传输层建立
			if((ret=layerBase.transUp(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transUp失败(%d)", Tools.getLineInfo(),i, ret);
				continue;
			}
			//打印
			prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			
			//发送数据
			if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,netWorkingBases[j]))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), send_len);
				continue;
			}
			//打印
			prnStatus = printUtil.print_byttfScript(DATACOMM_SIGN);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			prnStatus = printUtil.print_byttfScript(CUT_TEST);
			if (prnStatus != NDK_OK) 
			{
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
				continue;
			}
			
			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,netWorkingBases[j])) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), rec_len);
				continue;
			}
			
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			//打印
			for (int k = 0; k < 3; k++) {
				if (k==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if (prnStatus != NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATACOMM);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			
			// 挂断
			if((ret = layerBase.transDown(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
				continue;
			}
			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			//打印
			for (int e = 0; e < 3; e++) {
				if (e==0) {
					prnStatus = printUtil.print_byttfScript(FEEDLINE);
					if (prnStatus != NDK_OK) 
					{
						gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
						continue;
					}
				}
				prnStatus = printUtil.print_byttfScript(DATAPIC);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			succ++;
			
			
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0 ,"%s/TTF打印交叉测试完成，已执行次数为%d，成功为%d次",type[j],i,succ);
	}*/

	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int j = GlobalVariable.chooseConfig;
		Sock_t[] sock_t = {ethernetPara.getSock_t(),wifiPara.getSock_t()};
		int i = 0, succ = 0, ret = 0;
		int send_len = 0,rec_len = 0;
		byte[] buf = new byte[PACKMAXLEN];
		byte[] rbuf = new byte[PACKMAXLEN];
		PacketBean sendPacket = new PacketBean();
		int printerStatus;
		LinkType[] type = {ethernetPara.getType(),wifiPara.getType()};
		socketUtil = new SocketUtil( netWorkingBases[j].getServerIp(), netWorkingBases[j].getServerPort());
		
		/*process body*/
		init_snd_packet(sendPacket, buf);
		set_snd_packet(sendPacket,type[j]);
		
		if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s获取打印机状态失败(status = %d)", Tools.getLineInfo(),TAG,printerStatus);
			return;
		}
		//注册打印事件
		if ((ret = RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		if((ret = layerBase.netUp(netWorkingBases[j],type[j])) != NDK_OK)
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:NetUp失败(ret = %d)", Tools.getLineInfo(),i,ret);
		}
		while(true)
		{
//			//保护动作
//			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			Log.d("eric_chen","j="+j+"\n"+"netWorkingBases="+netWorkingBases[j]+"sock_t="+sock_t[j]+"type="+type[j]);
			//测试退出点
			if(gui.cls_show_msg1(3, "%s/打印交叉测试,已执行%d次,成功%d次,[取消]退出测试", type[j],i,succ)==ESC)
				break;
			if(update_snd_packet(sendPacket,type[j])!=NDK_OK)
				break;
			i++;
			
//			if((ret = layerBase.netUp(netWorkingBases[j],type[j])) != NDK_OK)
//			{
//				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:NetUp失败(ret = %d)", Tools.getLineInfo(),i,ret);
//				continue;
//			}
			//休眠2.5s 确保以太网打开
			SystemClock.sleep(2500);
			// 传输层建立
			if((ret=layerBase.transUp(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transUp失败(%d)", Tools.getLineInfo(),i, ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//打印
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			//TTF单次图片打印
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//发送数据
			if ((send_len = sockSend(socketUtil,sendPacket.getHeader(), sendPacket.getLen(), SO_TIMEO,netWorkingBases[j]))!= sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:发送数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), send_len);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：获取打印机状态失败(status=%d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//打印
			if((ret = printUtil.print_landi())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印联迪票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			//TTF单次指令打印
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}

			//接收数据
			Arrays.fill(rbuf, (byte) 0);
			if ((rec_len = sockRecv(socketUtil,rbuf, sendPacket.getLen(), SO_TIMEO,netWorkingBases[j])) != sendPacket.getLen()) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:接收数据失败(预期:%d,实际:%d)", Tools.getLineInfo(),i, sendPacket.getLen(), rec_len);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//打印
			if((ret = printUtil.print_compress())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印压缩内容失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			//TTF连续图片打印
			boolean flag1=false;
			for (int k = 0; k < 3; k++) {
			if (k==0) {
				if ((ret = printUtil.print_byttfScript(FEEDLINE))!=NDK_OK) {
					flag1=true;
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{	
					flag1=true;
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
					continue;
				}
			
			}
			if ((ret = printUtil.print_byttfScript(DATAPIC_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{	
				flag1=true;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}
			//比较收发
			if (!Tools.memcmp(sendPacket.getHeader(), rbuf, sendPacket.getLen())) 
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:数据校验失败", Tools.getLineInfo(), i);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			//打印
			if((ret = printUtil.print_testpage_new())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),i,TESTITEM,ret);
				continue;
			}
			//TTF连续指令打印
			for (int k = 0; k < 3; k++) {
			if (k==0) {
				if ((ret = printUtil.print_byttfScript(FEEDLINE))!=NDK_OK) {
					flag1=true;
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
					continue;
				}
				if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
				{
					flag1=true;
					gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
					continue;
				}
				
			}
			if ((ret = printUtil.print_byttfScript(DATACOMM_SIGN))!=NDK_OK) {
				flag1=true;
				gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), ret);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				flag1=true;
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),i,printerStatus);
				continue;
			}
			
		}
			if (flag1) {
				continue;
			}
			// 挂断
			if((ret = layerBase.transDown(socketUtil,sock_t[j]))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:transDown失败(ret = %d)", Tools.getLineInfo(), i,ret);
				continue;
			}
//			layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
			//验证打印是否监听到
			if((ret = priEventCheck())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:没有监听到打印事件(ret = %d)", Tools.getLineInfo(),i,ret);
				continue;
			}
			succ++;
		}
		layerBase.netDown(socketUtil,netWorkingBases[j],sock_t[j],type[j]);
		// 解绑事件
		if ((ret = UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue())) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件解绑失败(%d)",Tools.getLineInfo(), ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test",g_time_0 ,"%s/打印交叉测试完成，已执行次数为%d，成功为%d次",type[j],i,succ);
	}
}
