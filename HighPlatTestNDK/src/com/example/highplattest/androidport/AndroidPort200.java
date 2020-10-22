package com.example.highplattest.androidport;

import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.newland.NLUART3Manager;
/************************************************************************
 * 
 * module 			: Android系统与外置串口通信模块 
 * file name 		: AndroidPort6.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20170829 
 * directory 		: 
 * description 		: 模块内随机测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20170829	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class AndroidPort200 extends UnitFragment{
	private final String CLASS_NAME=AndroidPort200.class.getSimpleName();
	private final String TESTITEM = "Android系统与外置串口通信模块内随机";
	private NLUART3Manager uart3Manager = null;
	private Gui gui = new Gui(myactivity, handler);
	private String uart3FunArr[] = {"close","setconfig","open","write","read","getVersion","isValid"};
	Random random = new Random();
	String[] para={"8N1NB","8N1NN"};//阻塞，非阻塞
	private final int MAX_SIZE = 1024*2;
	private boolean openFlag = false;	//串口是否打开
	private boolean configFlag=false;//串口是否配置
	private boolean writeFlag=false;//是否已写数据
//  private boolean blockFlag=false;//阻塞非阻塞
	private String funcStr1,funcStr2 ;
	
	private boolean isNewRs232 = false;/**默认使用旧的RS232方式，为了兼容非X5的机型*/

	public void androidport200()
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.RS232)==false&&GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad)==false)
		{
			gui.cls_show_msg1(1, "%s产品不支持物理串口，长按确认键退出",GlobalVariable.currentPlatform);
			return;
		}
		// X5设备有新的RS232和旧的RS232,需要让测试人员进行一次选择
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.PinPad))
		{
			int nkeyIn = gui.cls_show_msg("是否要测试PinPad串口,是[确认],否[取消]");
			isNewRs232 = nkeyIn==ENTER?true:false;
		}
		
		gui.cls_show_msg1(gScreenTime, TESTITEM+"测试中...");
		int succ=0,cnt=g_RandomTime,bak =g_RandomTime;
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(RS232_SERIAL_SERVICE);
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(gScreenTime, "Android系统与外置串口通信模块内随机组合测试中...\n还剩%d次(已成功%d次),按[取消]退出测试...",cnt,succ)==ESC)
				break;
			String[] func = new String[g_RandomCycle];
			for (int i = 0; i < g_RandomCycle; i++) {
				func[i]=uart3FunArr[random.nextInt(uart3FunArr.length)];
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
			if(openFlag){
				//每次测试前置
				uart3Manager.close();
				openFlag = false;
			}
			//测试前置
			configFlag=false;
			writeFlag=false;
			boolean ret=false;
			
			for(int i=0;i<g_RandomCycle;i++){
				gui.cls_show_msg1(gScreenTime,"正在测试%s",func[i]);
				uart3FuncName fname = uart3FuncName.valueOf(func[i]);
				if(!(ret=RandomTest(fname,uart3Manager)))
					break;
			}
			if(ret)
			succ++;
		}
		//测试后置
		uart3Manager.close();
		gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "Android系统与外置串口通信模块内随机组合测试测试完成,已执行次数为%d,成功为%d次", bak-cnt,succ);
	}
	public boolean RandomTest(uart3FuncName fname , NLUART3Manager uart3Manager)
	{
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		int ret;
		byte[] sendBuf = new byte[MAX_SIZE];
		final byte[] recvBuf = new byte[MAX_SIZE];
		boolean is =true;
		switch(fname){
		case open:
			/**兼容X5*/
			ret = isNewRs232==true?this.uart3Manager.open(62):this.uart3Manager.open();
			if(ret!=NDK_OK)
			{
				gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sopen方法测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				is=false;
			} else {
				openFlag = true;
			}
			break;
		case close:
			if(openFlag){				
				if((ret = uart3Manager.close())!=NDK_OK)
				{
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sclose方法测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				} else {
					openFlag = false;
				}
			} else{
				if((ret = uart3Manager.close())!=NDK_ERR)
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sclose方法测试失败(ret = %d)", Tools.getLineInfo(),TESTITEM,ret);
				    is=false;
			}
			break;
		case setconfig:
			if (openFlag) {
				if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[random.nextInt(2)].getBytes())) != NDK_OK) {
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%ssetconfig方法测试失败(ret = %d)", Tools.getLineInfo(), TESTITEM,ret);
					is=false;
				}else{
				     configFlag=true;	
				}
			} else {
				if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[random.nextInt(2)].getBytes())) != NDK_ERR) {
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%ssetconfig方法测试失败(ret = %d)", Tools.getLineInfo(), TESTITEM,ret);
				    is=false;
				}
			}
			break;
		case write:
			for (int i = 0; i < sendBuf.length; i++) {
				sendBuf[i] = (byte) (Math.random() * 256);
			}
			
			if (configFlag&&openFlag) {//已打开且配置串口
				if ((ret = uart3ManagerWrite(uart3Manager,sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) != MAX_SIZE) {
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:%s写串口数据失败%d", Tools.getLineInfo(), TESTITEM, ret);
					is=false;
				}else{
					writeFlag=true;
				}
			}else{
				if ((ret = uart3ManagerWrite(uart3Manager,sendBuf, MAX_SIZE, MAXWAITTIME / (BpsBean.bpsId + 1))) == MAX_SIZE) {
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME, funcName, gKeepTimeErr,"line %d:%s写串口数据失败%d", Tools.getLineInfo(), TESTITEM, ret);
				    is=false;
				}
			}
			break;
		case read://没有写数据的情况下，不管是否阻塞，read数据都会卡住??
			Arrays.fill(recvBuf, (byte) 0);
			if(openFlag&configFlag&writeFlag){
				if((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, MAXWAITTIME/(BpsBean.bpsId+1)))!=MAX_SIZE)
				{
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败%d", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				}
			}else{
				//非阻塞会卡住，先注释，如果也未配置呢
				if((ret = uart3ManagerRead(uart3Manager,recvBuf, MAX_SIZE, 1))!= ANDROID_PORT_READ_FAIL)
				{
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s读串口数据失败%d", Tools.getLineInfo(),TESTITEM,ret);
					is=false;
				}
			}
				
			break;
		case isValid:
			if(openFlag){
				if (!uart3Manager.isValid()) 
				{
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sisValid方法测试失败,false", Tools.getLineInfo(),TESTITEM);
					is=false;
				}
				
			}else{
				if (uart3Manager.isValid()) {
					gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
					gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%sisValid方法测试失败,true", Tools.getLineInfo(),TESTITEM);
				    is=false;
				}
			}
			break;
		case getVersion:
			if(uart3Manager.getVersion()!=null&&!uart3Manager.getVersion().equals("")){//不为null和“”
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime, "JNI版本信息=%s", uart3Manager.getVersion());
			}else{
				gui.cls_only_write_msg(CLASS_NAME,funcName,"%s模块内测试顺序为:\n"+funcStr1+funcStr2,TESTITEM);//只写不显示
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr,"line %d:%s获取JNI版本信息测试失败", Tools.getLineInfo(),TESTITEM);
			    is=false;	
			}
			
			break;
			
		default:
			break;
		}
		return is;
	}
	
	private enum uart3FuncName {
		close, setconfig, open, write, read, getVersion, isValid
	}
	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
		if(uart3Manager!=null)
			uart3Manager.close();
		uart3Manager = null;
		gui = null;
	}

}
