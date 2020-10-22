package com.example.highplattest.systest;

import java.util.ArrayList;
import java.util.List;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.btutils.BluetoothManager;
import com.example.highplattest.main.btutils.BluetoothService;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_EVENT;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;

/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest27.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20150420
 * directory 		: 
 * description 		: SMART/蓝牙交叉测试
 * related document :
 * history 		 	: 变更记录			变更时间			变更人员
 *			  		 测试前置解绑事件机制		20200415		郑薛晴
 * 					新增全局变量区分M0带认证和不带认证。相关案例修改	20200703 		陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest27 extends DefaultFragment
{
	private final String TAG = SysTest27.class.getSimpleName();
	private final String TESTITEM = "SMART/BT";
	private _SMART_t card = _SMART_t.CPU_A;
	private List<_SMART_t> cardList=new ArrayList<_SMART_t>();
	private final int BUFSIZE_BT = BT_BUF_SIZE;
	private ArrayList<BluetoothDevice> pairList = new ArrayList<BluetoothDevice>();
	private ArrayList<BluetoothDevice> unPairList = new ArrayList<BluetoothDevice>();
	
	Config config;
	// 蓝牙设备
	private BluetoothManager bluetoothManager;
	private BluetoothService dataService;
	private Gui gui = null;
	private int felicaChoose=0;
	public void systest27() 
	{
		gui = new Gui(myactivity, handler);
		config = new Config(myactivity, handler);
		bluetoothManager = BluetoothManager.getInstance(myactivity);
		// 测试前置，解绑RF和IC事件
		UnRegistAllEvent(new EM_SYS_EVENT[]{EM_SYS_EVENT.SYS_EVENT_ICCARD,EM_SYS_EVENT.SYS_EVENT_RFID});
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			g_btAddr = myactivity.getParaInit().getBTAddress();
			dataService = new BluetoothService(getBtAddr());
			cardList = config.smart_config();
			for(_SMART_t cardChoose:cardList){
				if(GlobalVariable.sdkType==SdkType.SDK3 && cardChoose==_SMART_t.IC)
					continue;
				card=cardChoose;
				if(card==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_time_0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
			}
		
			return;
		}
		//测试主入口
		while(true)
		{
			int returnValue=gui.cls_show_msg("SMART/BT\n0.SMART配置\n1.BT配置\n2.交叉测试");
			switch (returnValue) 
			{
			case '0':
				// SMART配置
				card = config.smart_config().get(0);
				if(card==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(smartInit(card)!=NDK_OK)
				{
					gui.cls_show_msg1(g_time_0, "line %d:smart卡初始化失败", Tools.getLineInfo());
				}
				break;
				
			case '1':
				// 蓝牙配置
				config.btConfig(pairList, unPairList, bluetoothManager);
				dataService = new BluetoothService(getBtAddr());
				break;
				
			case '2':
				try 
				{
					cross_test();
				} catch (Exception e) 
				{
					gui.cls_show_msg1_record(TAG, TAG,g_keeptime, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
				}
				break;
			
			case ESC:
				intentSys();
				return;
				
			}
		}
	}
	
	//交叉测试具体实现函数
	public void cross_test() 
	{
		/*private & local definition*/
		int cnt = 0,succ = 0,ret = -1,bak = 0;
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		byte[] rbuf = new byte[BUFSIZE_BT];
		byte[] wbuf = new byte[BUFSIZE_BT];
		PacketBean sendPacket = new PacketBean();
		
		/*process body*/	
		if(GlobalVariable.gSequencePressFlag)
			sendPacket.setLifecycle(getCycleValue());
		else
			sendPacket.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT, ABILITY_VALUE));
		bak = cnt = sendPacket.getLifecycle();
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//提示信息
		if(GlobalVariable.gAutoFlag != ParaEnum.AutoFlag.AutoFull)
		{
			if(gui.cls_show_msg("测试前请确保，已安装射频卡%s\n完成[确认]\n退出[取消]",card)==ENTER)
			{
				handler.sendEmptyMessage(HandlerMsg.DIALOG_SYSTEST_BACK);
			}
		}
		// 注册事件
		if ((ret = SmartRegistEvent(card)) != NDK_OK&&(ret = SmartRegistEvent(card)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件注册失败(%d)",Tools.getLineInfo(), card,ret);
			return;
		}
		
		while(cnt>0)
		{
			//保护动作
			smartDeactive(card);
			if(gui.cls_show_msg1(1, "请打开BluetoothServer工具,%s交叉测试,已执行%d次,成功%d次,[取消]退出测试" , TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(GlobalVariable.sdkType==SdkType.SDK3&&card==_SMART_t.IC)
			    gui.cls_show_msg("第%d次,请插拔或重新放置%s卡,完成任意键继续",bak-cnt,card);
			// smart初始化
			if((ret = smart_detect(card, UidLen, UidBuf))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s检测失败(%d)",Tools.getLineInfo(),bak-cnt, card,ret);
				continue;
			}
			if(!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
				SystemClock.sleep(3000);
			}
			
			if(!bluetoothAdapter.isEnabled())
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次打开蓝牙串口失败", Tools.getLineInfo(),bak-cnt);
				continue;
			}
			// 蓝牙设备配对
			if(bluetoothManager.connComm(dataService,CHANEL_DATA)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:BT建立连接失败", Tools.getLineInfo(),bak-cnt);
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
			
			//射频卡激活
			if((ret = smartActive(card,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s卡激活失败(ret = %d)", Tools.getLineInfo(),bak-cnt,card,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			for (int j = 0; j < wbuf.length; j++) 
			{
				wbuf[j] = (byte) (Math.random()*128);
			}
			if(bluetoothManager.writeComm(dataService,wbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:BT发送数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			//smart读写
			if((ret = smartApduRw(card,req,UidBuf)) != NDK_OK)
			{
				rfidDeactive(card,0);
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次:%s卡读写失败(%d)", Tools.getLineInfo(),bak-cnt,card,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			
			if(bluetoothManager.readComm(dataService,rbuf)==false)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：BT接收数据失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			if(!Tools.memcmp(wbuf, rbuf, BUFSIZE_BT))
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line:%d:第%d次数据校验失败", Tools.getLineInfo(),bak-cnt);
				postEnd(bluetoothAdapter);
				continue;
			}
			//射频卡下电
			if((ret = smartDeactive(card)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:第%d次：%s关闭场失败（%d）", Tools.getLineInfo(),bak-cnt,card,ret);
				postEnd(bluetoothAdapter);
				continue;
			}
			succ++;
			postEnd(bluetoothAdapter);
		}
		postEnd(bluetoothAdapter);
		// 解绑事件
		if ((ret = SmartUnRegistEvent(card)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "cross_test", g_keeptime, "line %d:%s事件解绑失败(%d)",Tools.getLineInfo(), card,ret);
			return;
		}
		gui.cls_show_msg1_record(TAG, "cross_test", g_time_0,"(%s/BT)交叉测试完成,已执行次数为%d,成功为%d次", card,bak-cnt,succ);
	}
	
	public void postEnd(BluetoothAdapter bluetoothAdapter)
	{
		bluetoothAdapter.disable();
		SystemClock.sleep(2000);
		bluetoothManager.cancel(dataService);
	}

}
