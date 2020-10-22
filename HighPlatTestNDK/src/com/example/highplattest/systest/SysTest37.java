package com.example.highplattest.systest;

import java.util.ArrayList;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.PrinterData;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest37.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150420
 * directory 		: 
 * description 		: 打印/蓝牙交叉测试
 * related document :
 * history 		 	: author			date			remarks
 * 				 	: 变更记录				变更时间			变更人员
 *					 打印交叉案例增加TTF交叉方式		 20200528		陈丁			  		 
 * 					将TTF打印和NDK打印放在一起交叉        20200601       	陈丁
 *					开发回复可以去除交叉中的切刀操作，这样不浪费纸 	 20200609		陈丁
 *					TTF打印交叉新增打印机状态判断。修复For循环失败，成功次数仍然增加问题                  20200617    陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest37 extends DefaultFragment implements PrinterData
{
	private final String TAG = SysTest37.class.getSimpleName();
	private final String TESTITEM = "打印/BT";
	SocketUtil socketUtil;
	private final int BUFSIZE_BT = BT_BUF_SIZE;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	private final int MAXWAITTIME = 10;
	private Gui gui = null;
	private PrintUtil printUtil;
	
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	Config config;
	
	public void systest37() 
	{
		String funcName = "systest37";
		gui = new Gui(myactivity, handler);
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.sysTestPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(TESTITEM, funcName, g_keeptime,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		config = new Config(myactivity, handler);
		printUtil = new PrintUtil(myactivity, handler,true);
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			//打印配置
			config.print_config();
			//蓝牙配置
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			//交叉测试
			try 
			{
				cross_test();
			} catch (Exception e) 
			{
				gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
			return;
			
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("打印/BT\n0.打印配置\n1.BT配置\n2.交叉测试\n");
			switch (returnValue) 
			{
			case '0':
				// 打印配置
				config.print_config();
				break;
				
			case '1':
				// 蓝牙配置
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '2':
				cross_test();
				break;
			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}
	}
	
	/*private void cross_ttftest() {
		int cnt = 0,succ = 0,bak = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		int prnStatus;
		int ret=-1;
		bak=cnt=gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE);
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具，%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：BT建立连接失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			byte[] tempBuf = new byte[5];
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
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
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			//打印
			prnStatus = printUtil.print_byttfScript(DATAPIC_SIGN);
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
			
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
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
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次:数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
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
				prnStatus = printUtil.print_byttfScript(DATAPIC);
				if (prnStatus != NDK_OK) 
				{
					gui.cls_show_msg1_record(TAG, "mag_ttfprintf", 5,"line %d:TTF打印测试失败(ret=%d)", Tools.getLineInfo(), prnStatus);
					continue;
				}	
			}
			
			succ++;
			postEnd(bluetoothAdapter);
		}

		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%sTTF交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
		
	}*/
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0,succ = 0,bak = 0;
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		int printerStatus;
		int ret=-1;
		/*process body*/	
		if(GlobalVariable.gSequencePressFlag)
			cnt=getCycleValue();
		else
			cnt=gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE);
		bak = cnt;
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//注册打印事件
		if (RegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue(), prnlistener) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:print事件注册失败",Tools.getLineInfo());
			return;
		}
		while(cnt>0)
		{
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具，%s交叉测试,已执行%d次,成功%d次,[取消]退出测试", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次：BT建立连接失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			byte[] tempBuf = new byte[5];
			// 等待服务器端发送的hello数据后才往下走
			if(bluetoothManager.readComm(dataService,tempBuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if(new String(tempBuf).contains("hello")==false)
			{
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				postEnd(bluetoothAdapter);
				continue;
			}
			//TTF单次图片打印
			printUtil.print_byttfScript(DATAPIC_SIGN);
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				postEnd(bluetoothAdapter);
				continue;
			}
			//打印票据
			printUtil.print_stock();
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				postEnd(bluetoothAdapter);
				continue;
			}
			//打印票据
			printUtil.print_testpage_new();
			//TTF单次指令打印
			printUtil.print_byttfScript(DATACOMM_SIGN);
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			boolean flag1 = false;
			// TTF连续图片打印
			for (int k = 0; k < 3; k++) {
				if (k == 0) {
					printUtil.print_byttfScript(FEEDLINE);
					if ((printerStatus = printUtil.getPrintStatus(MAXWAITTIME)) != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) {
						flag1 = true;
						gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:第%d次:获取打印机状态失败(status = %d)",Tools.getLineInfo(), bak - cnt, printerStatus);
						continue;
					}
				}
				printUtil.print_byttfScript(DATAPIC_SIGN);
				if ((printerStatus = printUtil.getPrintStatus(MAXWAITTIME)) != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) {
					flag1 = true;
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:第%d次:获取打印机状态失败(status = %d)",Tools.getLineInfo(), bak - cnt, printerStatus);
					continue;
				}
			}
			if (flag1) {
				// 测试后置：断开蓝牙连接
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line:%d:第%d次:数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if((printerStatus = printUtil.getPrintStatus(MAXWAITTIME))!=EM_PRN_STATUS.PRN_STATUS_OK.getValue())
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:获取打印机状态失败(status = %d)", Tools.getLineInfo(),bak-cnt,printerStatus);
				postEnd(bluetoothAdapter);
				continue;
			}
			//打印票据
			if((ret = printUtil.print_bill_add_feeding())!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test",g_keeptime, "line %d:第%d次:%s打印票据失败(ret = %d)", Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			//TTF连续指令打印
			for (int k = 0; k < 3; k++) {
				if (k == 0) {
					printUtil.print_byttfScript(FEEDLINE);
					if ((printerStatus = printUtil.getPrintStatus(MAXWAITTIME)) != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) {
						flag1 = true;
						gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:第%d次:获取打印机状态失败(status = %d)",Tools.getLineInfo(), bak - cnt, printerStatus);
						continue;
					}

				}
				printUtil.print_byttfScript(DATACOMM_SIGN);
				if ((printerStatus = printUtil.getPrintStatus(MAXWAITTIME)) != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) {
					flag1 = true;
					gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:第%d次:获取打印机状态失败(status = %d)",Tools.getLineInfo(), bak - cnt, printerStatus);
					continue;
				}
			}
			if (flag1) {
				// 测试后置：断开蓝牙
				postEnd(bluetoothAdapter);
				continue;
			}
			// 验证打印是否监听到
			if ((ret = priEventCheck()) != NDK_OK) {
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:第%d次:没有监听到打印事件(ret = %d)",Tools.getLineInfo(), bak - cnt, ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			succ++;
			postEnd(bluetoothAdapter);
		}
		// 解绑事件
		if (UnRegistEvent(EM_SYS_EVENT.SYS_EVENT_PRNTER.getValue()) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime,"line %d:print事件解绑失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次", TESTITEM,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		// 发送结束标志
		bluetoothManager.cancel(dataService);
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
	}
}
