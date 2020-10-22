package com.example.highplattest.systest;

import java.util.Arrays;
import com.example.highplattest.R;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.newland.NLUART3Manager;

/************************************************************************
 * 
 * module 			: 安卓串口扫描枪扫码模块 
 * file name 		: Systest79.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180614 
 * directory 		: 
 * description 		: 测试Android系统与外置串口接扫描枪通信模块的read
 * related document :
 * history 		 	: author			date			remarks
 *			  		 wangxy		      20180614	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest79 extends DefaultFragment 
{
	/*------------global variables definition-----------------------*/
	private final String TAG = SysTest79.class.getSimpleName();
	private final int MAX_SIZE = 1024*2;
	private final int MAXWAITTIME = 60;
	private NLUART3Manager uart3Manager=null;
	private String TESTITEM = "安卓串口扫描枪扫条形码";
	private Gui gui = new Gui(myactivity, handler);
	private int chooseOne=-1;//0代表USB，1代表RS232

	public void systest79()
	{
		// 实例化接口对象
		try 
		{
			uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(TAG, "systest79", g_keeptime, "line %d:未找到该类,抛出异常(%s),%s设备不支持RS232串口",Tools.getLineInfo(),e.getMessage(),GlobalVariable.currentPlatform);
			return;
		}
		
		if(GlobalVariable.gSequencePressFlag)
		{
			gui.cls_show_msg1_record(TAG, "systest79", g_keeptime, "%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
		//测试主入口
		while (true) {
			int returnValue = gui.cls_show_msg("扫描枪\n0.扫描枪配置\n1.扫码");
			switch (returnValue) {
			case '0':
				int returnValue2 = gui.cls_show_msg("扫描枪配置\n0.HID-USB\n1.RS232\n");
				switch (returnValue2) {
				case '0':
	                //弹出图来配置扫码
					 chooseOne=0;
					new Config(myactivity,handler).showScanConfig(R.drawable.scan_set_hid_usb);
					break;

				case '1':
					chooseOne=1;
					new Config(myactivity,handler).showScanConfig(R.drawable.scan_set_rs232);
					break;
				
				default:
					break;
				}
				gui.cls_show_msg("配置完成。。。。");
//				scanConfig();
//				break;
			case '1':
				scanTest79();
				break;

			case ESC:
				intentSys();
				return;

			}
		}
	}	
	
	//配置扫描枪的扫码模式（usb or rs232）
	public void scanConfig() 
	{
		while (true) 
		{
			int nkeyin = gui.cls_show_msg("扫描枪配置\n0.HID-USB\n1.RS232\n");
			switch (nkeyin) 
			{
			case '0':
                //弹出图来配置扫码
				 chooseOne=0;
				new Config(myactivity,handler).showScanConfig(R.drawable.scan_set_hid_usb);
				break;

			case '1':
				chooseOne=1;
				new Config(myactivity,handler).showScanConfig(R.drawable.scan_set_rs232);
				break;
			
			default:
				return;
			}
		}
	}
	
	//扫描枪扫条形码
	public void scanTest79() 
	{	
		
		switch (chooseOne) {
		case 0:
			if ((gui.ShowMessageBox(("接入USB串口的扫描枪后，在文件管理中新建文件夹，输入文件名时光标停留在输入框中，此时扫描枪扫条形码，是否在输入框中显示码值且码值一致？").getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) 
			{
				gui.cls_show_msg1_record(TAG, "scanTest79", g_keeptime, "line %d:%sHID-USB模式扫条形码失败", Tools.getLineInfo(), TESTITEM);
				if (!GlobalVariable.isContinue)
					return;
			}
			break;

		case 1:
			scanForRs232();
			break;

		default:
			gui.cls_show_msg1(2, "请进行扫描枪模式配置!!!");
			break;
		}
//		if (chooseOne!=-1) {
//			return;
//		}
	}
	
	//扫描枪rs232模式下扫码
	public void scanForRs232(){	
		
		/*private & local definition*/
		int  ret1 = -1;
		// 设备节点描述符
		int fd=-1;
		byte[] recvBuf = new byte[MAX_SIZE];
		String[] para={"8N1NB","8N1NN"};

		/*process body*/
		// 测试前置，关闭串口
		uart3Manager.close();
		if ((fd = uart3Manager.open()) == -1) 
		{
			gui.cls_show_msg1_record(TAG, "scanForRs232", g_keeptime,"line %d:%s打开串口失败(ret = %d)", Tools.getLineInfo(),TESTITEM,fd);
		    return;
		}
		
		gui.cls_printf("请输入扫码压力次数:".getBytes());
		int total=gui.JDK_ReadData(30,1);
		int succ_count=0,i = 0;
		for (; i <total; ) 
		{
			Arrays.fill(recvBuf, (byte) 0);
			if((ret1=uart3Manager.setconfig(BpsBean.bpsValue, 0, para[1].getBytes()))!=0)
			{
				gui.cls_show_msg1_record(TAG, "scanForRs232", g_keeptime,"line %d:%s第%d次,串口配置失败(ret = %d)", Tools.getLineInfo(),TESTITEM,i,ret1);
				continue;
			}
			if(gui.cls_show_msg1(3,"请60s内扫任意条形码，正在进行第%d次RS232串口扫描枪扫码测试,[取消]退出测试", i+1)==ESC)
			     break;
			i++;
			if ((ret1 = uart3ManagerReadForScan(uart3Manager, recvBuf, MAXWAITTIME)) <= 0) 
			{
				gui.cls_show_msg1_record(TAG, "scanForRs232", g_keeptime,"line %d:%s第%d次,读串口失败(ret = %d)", Tools.getLineInfo(), TESTITEM,i,ret1);
				uart3Manager.close();
				continue;
			}
			if ((gui.ShowMessageBox(("第"+i+"次，请确认获取的条形码码值=" + new String(recvBuf).trim()).getBytes(),(byte) (BTN_OK | BTN_CANCEL), WAITMAXTIME)) != BTN_OK) 
			{
				gui.cls_show_msg1_record(TAG, "scanForRs232", g_keeptime, "line %d:%s第%d次,RS232串口扫码失败(recvBuf=%s)", Tools.getLineInfo(), TESTITEM,i,new String(recvBuf).trim());
				continue;
			}
			succ_count++;
		}
		if ((ret1 = uart3Manager.close()) != 0)
		{
			gui.cls_show_msg1_record(TAG, "scanForRs232", g_keeptime, "line %d:%s第%d次,关闭串口失败(ret = %d)", Tools.getLineInfo(), i,TESTITEM,ret1);
		}
		gui.cls_show_msg1_record(TESTITEM, "scanForRs232",g_keeptime,"RS232串口的扫描枪扫码压力总次数：%d  成功次数：%d", i, succ_count);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}


}
