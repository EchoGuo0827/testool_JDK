package com.example.highplattest.systest;

import android.util.Log;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.netutils.EthernetPara;
import com.example.highplattest.main.netutils.MobilePara;
import com.example.highplattest.main.netutils.NetWorkingBase;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.uartport.Node;
import com.newland.uartport.UartPort;

/************************************************************************
 * module 			: SysTest综合模块
 * file name 		: SysTest89.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20200716
 * directory 		: 
 * description 		: N850串口变更(银商)
 * related document :
 * history 		 	: 变更记录									变更人员			变更时间

 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest95 extends DefaultFragment {
	private final String TAG = SysTest95.class.getSimpleName();
	private final String TESTITEM = "N850串口变更(银商)";
	private Gui gui = null;
	private int ret=-1;
	private int NODE=-1;  //节点
	
	public void systest95(){
		
		gui = new Gui(myactivity, handler);
//		gui.cls_show_msg("请先确保案例为第三方应用");
		
		while(true)
		{
			int returnValue=gui.cls_show_msg("N850串口变更\n0.单元\n");
			switch (returnValue) 
			{
			case '0':
				int returnValue2=gui.cls_show_msg("单元\n0.打开串口\n1.读数据\n2.写数据\n3.清串口缓存\n4.是否有数据\n5.关闭串口");
				switch (returnValue2) 
				{
				case'0':
					byte[] data = new byte["8N1NN".getBytes().length+1];
					System.arraycopy("8N1NN".getBytes(), 0, data, 0, data.length-1);
					gui.cls_show_msg("按任意键打开串口");
					if ((ret=UartPort.JNI_openPort(Node.SerialPort.getValue(), 115200, data))<0) {
						gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口打开失败（%d）", Tools.getLineInfo(),ret);
						
					}
					NODE=ret;
					gui.cls_show_msg("当前打开的节点为%d",NODE);
					break;
				case'1':
					String datatem;
					gui.cls_show_msg("请打开PC串口工具,接入RS323,并发送任意七个字节的数据。按任意键测试读串口");
					datatem=readUartPort();
					gui.cls_show_msg("读取到的数据为%s",datatem);
					break;
				case'2':
					gui.cls_show_msg("请打开PC串口工具,接入RS323。按任意键测试写串口");
					byte[] data1 = new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37};
					ret=UartPort.JNI_write(NODE,data1,data1.length,100);
					if (ret<=0) {
						gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口写入失败（%d）", Tools.getLineInfo(),ret);
					}else {
						gui.cls_show_msg("串口写入数据成功");
					}
					gui.cls_show_msg("PC串口工具有接收到31323334353637，则测试通过。");
					
					break;
				case'3':
					gui.cls_show_msg("按任意键清除串口数据");
					if ((ret=UartPort.JNI_clearBuf(NODE,0))!=0) {
						gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口清除数据失败（%d）", Tools.getLineInfo(),ret);
						
					}
					gui.cls_show_msg("清除串口数据成功");
					break;
				case'4':
					gui.cls_show_msg("按任意键判断串口是否有数据(需打开串口工具验证有数据和无数据两种情况)");
					ret=UartPort.JNI_isBufferEmpty(NODE,1);
					Log.d("eric_chen", "ret=="+ret);
					if (ret==0) {
						gui.cls_show_msg("当前串口无数据");
					}else if (ret>0) {
						gui.cls_show_msg("当前串口还有%d字节数据",ret);
					}else {
						gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:串口异常（%d）", Tools.getLineInfo(),ret);
					}
					break;
					
				case'5':
					gui.cls_show_msg("按任意键关闭串口");
					if ((ret=UartPort.JNI_close(NODE))!=0) {
						gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:关闭串口异常（%d）", Tools.getLineInfo(),ret);
					}
					gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "关闭串口测试通过");
					break;
					
				default:
					break;
					
				
				}
				break;
				
			default:
				break;
				
			case ESC:
				intentSys();
				return;
			
			}
		
		
		
		}
		
	}
	
	public String readUartPort(){
		byte[] buffer = new byte[500];
		String datastr="";
		ret = UartPort.JNI_read(NODE,buffer,7,200);
		Log.d("eric_chen", "readUartPort---NODE=="+NODE+"ret=="+ret);
		LoggerUtil.e("buffer:"+Dump.getHexDump(buffer));
		if (ret==-1) {
			gui.cls_show_msg1_record(TAG, TESTITEM,g_keeptime, "line %d:读串口异常（%d）", Tools.getLineInfo(),ret);
		}
		if (ret>0) {
			byte[] temp = new byte[ret];
			System.arraycopy(buffer, 0, temp, 0, ret);
			LoggerUtil.e("temp:"+Dump.getHexDump(temp));
			datastr=Dump.getHexDump(temp);
			return datastr;
			
		}
			return datastr;
			
		
	}

}
