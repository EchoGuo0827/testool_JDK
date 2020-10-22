package com.example.highplattest.paymentport;

import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NlManager;
/************************************************************************
 * 
 * module 			: Android系统和支付模块通信串口
 * file name 		: PaymentPort7.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170829 
 * directory 		: 
 * description 		: 模块内随机测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy	    	20170829	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class PaymentPort200 extends UnitFragment
{
	private final String CLASS_NAME = PaymentPort200.class.getSimpleName();
	private NlManager nlManager = null;
	String TESTITEM = "通信串口模块内随机测试";
	private Gui gui = new Gui(myactivity, handler);
	private String paymentPortFunArr[] = {"getVersion","isValid","disconnect","setconfig","connect","write","read"};
	Random random = new Random();
	public final int MAX_SIZE = 1024;
	private boolean connectFlag=false;
	private boolean writeFlag=false;
	private boolean setconfigFlag=false;
	private int pszAttr;
	private byte[] recvBuf = new byte[MAX_SIZE];
	private byte[] sendBuf = new byte[MAX_SIZE - 2];
	private String[] para={"8N1NB","8N1NN"};
	private String funcStr1,funcStr2 ;

	public void paymentport200() 
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持PinPad串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		String funcName="paymentport200";
		nlManager = (NlManager) myactivity.getSystemService(PINPAD_SERIAL_SERVICE);
		
		gui.cls_show_msg1(gScreenTime, TESTITEM + "测试中...");
		gui.cls_show_msg("请短接PINPAD串口的23脚,完成点[确认]继续");
		BpsBean.bpsValue=115200;
		 pszAttr = BpsBean.bpsValue;
		// 对1k数据初始化,并计算LRC值
		sendBuf[0] = 0x02;
		sendBuf[1] = 0x10;
		sendBuf[2] = 0x17;
		sendBuf[3] = 0x1D;
		sendBuf[4] = 0x0A;
		sendBuf[5] = 0x2F;
		sendBuf[6] = 0x01;
		sendBuf[7] = 0x10;
		sendBuf[8] = 0x11;
		sendBuf[sendBuf.length - 2] = 0x03;
		sendBuf[sendBuf.length - 1] = 0x00;
		for (int i = 0; i < sendBuf.length - 11; i++) {
			sendBuf[9 + i] = (byte) (Math.random() * 256);
		}
		for (int i = 0; i < sendBuf.length - 2; i++) {
			sendBuf[sendBuf.length - 1] = (byte) (sendBuf[sendBuf.length - 1] ^ sendBuf[1 + i]);
		}
		
		int succ = 0, cnt = g_RandomTime, bak = g_RandomTime;

		boolean ret;
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "Android系统和支付模块通信串口模块内随机组合测试中...\n还剩%d次(已成功%d次),按[取消]退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=paymentPortFunArr[random.nextInt(paymentPortFunArr.length)];
			}
			funcStr1 = "";
			funcStr2 = "";
			for(int i=0;i<g_RandomCycle;i++){
				if(i<10){
					funcStr1 = funcStr1 + func[i] + "-->\n";
				}else{
					funcStr2 = funcStr2 + func[i] + "-->\n";
				}
				
			}
			gui.cls_show_msg1(gScreenTime,"第%d次模块内随机测试顺序为:\n" + funcStr1,bak-cnt+1);
			gui.cls_show_msg1(gScreenTime, funcStr2);
			cnt--;
			ret=false;
			//测试前置

			if(connectFlag){
				nlManager.disconnect();
				connectFlag=false;
			}
			writeFlag=false;
			setconfigFlag=false;
			
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				paymentPortFuncName fname = paymentPortFuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname,nlManager)))
					break;
			}
			if(ret)
			succ++;
		}
		//测试后置
		nlManager.disconnect();
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "Android系统和支付模块通信串口模块内随机组合测试测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}
    private boolean RandomTest(paymentPortFuncName fname, NlManager nlManager2) 
    {
    	String funcName="RandomTest";
		String version;
		boolean ret;
		int ret2=-1;
		
		boolean is =true;
		switch (fname) {
		case getVersion:
			version = nlManager.getVersion();
			gui.cls_show_msg1(gScreenTime,"获得的版本号为:%s",version);
			break;
		case isValid:
			if ((ret = nlManager.isValid()) != true) 
			{
				gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sisVaild方法测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				is=false;
			} 
			break;
		case disconnect:
			if ((ret2 = nlManager.disconnect()) != ANDROID_OK) 
			{
				gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:断开串口失败(%d)", Tools.getLineInfo(),ret2);
				connectFlag=false;
				is=false;
			}else{
				connectFlag=true;
			}
			break;
		case setconfig:
			int config=random.nextInt(2);
			if(connectFlag){
				if ((ret2 = nlManager.setconfig(pszAttr, 0, (para[config]).getBytes())) != ANDROID_OK) 
				{
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(%d)",Tools.getLineInfo(),ret2);
					setconfigFlag=false;
					is=false;
				}else{
					setconfigFlag=true;
				}
			}else{
				if ((ret2 = nlManager.setconfig(pszAttr, 0, (para[config]).getBytes())) == ANDROID_OK) 
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:配置串口测试失败(%d)",Tools.getLineInfo(),ret2);
					setconfigFlag=true;
					is=false;
				}else{
					setconfigFlag=false;
					
				}
			}
			break;
		case connect:
			if ((ret = nlManager.connect(false)) != true) 
			{
				gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:连接串口测试失败(%s)",Tools.getLineInfo(),ret);
				connectFlag=false;
				is=false;
			}else{
				connectFlag=true;
			}
			break;
		case write:
			if(connectFlag&&setconfigFlag){
				if ((ret2 = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME / (BpsBean.bpsId + 1))) != sendBuf.length) 
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口测试失败(预期=%d,实际=%d)",Tools.getLineInfo(),sendBuf.length,ret2);
					writeFlag=false;
					is=false;
				}else{
					writeFlag=true;
				}
			}else{
				if ((ret2 = nlManager.write(sendBuf, sendBuf.length, MAXWAITTIME / (BpsBean.bpsId + 1))) == sendBuf.length) 
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:写数据到串口测试失败(预期=%d,实际=%d)",Tools.getLineInfo(),sendBuf.length,ret2);
					writeFlag=true;
					is=false;
				}else{
					writeFlag=false;
				}
			}
			break;
		case read:
			Arrays.fill(recvBuf, (byte) 0);
			if(connectFlag&&setconfigFlag&&writeFlag){
				if ((ret2 = nlManager.read(recvBuf, recvBuf.length, MAXWAITTIME/ (BpsBean.bpsId + 1))) != recvBuf.length) {
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据测试失败(预期=%d,实际=%d)",Tools.getLineInfo(),recvBuf.length,ret2);
				    is=false;
				}
			}else{
				//阻塞时,未写就读会卡住
				if ((ret2 = nlManager.read(recvBuf, MAX_SIZE, 1)) == recvBuf.length) 
				{
					gui.cls_only_write_msg(CLASS_NAME, funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:读串口数据测试失败(ret=%d)",Tools.getLineInfo(),ret2);
					 is=false;
				}
			}
			break;

		default:
			break;
		}
		return is;
	}
	private enum paymentPortFuncName{getVersion,isValid,disconnect,setconfig,connect,write,read};
 	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		if(nlManager!=null)
			nlManager.disconnect();
		gui = null;
		nlManager = null;
	}

}
