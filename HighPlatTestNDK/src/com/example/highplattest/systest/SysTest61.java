package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum._SMART_t;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class SysTest61 extends DefaultFragment
{
	private final String TESTITEM = "射频/键盘";//物理键盘
	private boolean isInit = false;
	private final String TAG = SysTest61.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private Config config;
	private int felicaChoose=0;
	private int ret=-1;
	public void systest61() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试，请手动验证", TESTITEM);
			return;
		}
		_SMART_t type = _SMART_t.CPU_A;
		config = new Config(myactivity, handler);
		while(true)
		{
			int returnValue=gui.cls_show_msg("射频/键盘\n0.射频卡配置\n1.射频键盘交叉");
			switch (returnValue) 
			{
			case '0':
				type = config.rfid_config();
				if(type==_SMART_t.FELICA){
					felicaChoose=config.felica_config();
				}
				if(rfidInit(type)!=NDK_OK)
				{
					isInit = false;
					gui.cls_show_msg1_record(TAG, "systest2",g_keeptime, "line %d:初始化失败！请检查配置是否正确ret=%d", Tools.getLineInfo(),ret);
				}
				else
				{
					isInit = true;
					gui.cls_show_msg1(2,"%s初始化成功!!!", type);
				}
				break;
				
			case '1':
				if(isInit == false)
				{
					gui.cls_show_msg1(2, "请先对射频卡进行初始化");
				}
				else
				{
					try
					{
						rfid_input(type);
					}catch(Exception e){
						e.printStackTrace();
						gui.cls_show_msg1_record(TAG, TAG, g_keeptime, "line %d:抛出异常（%s）", Tools.getLineInfo(),e.getMessage());
					}
				}
				break;
				
				
			case ESC:
				intentSys();
				return;

			}
		}
	}
	
	public void rfid_input(_SMART_t type)
	{
		int i = 0,ret = 0;
		int[] pnCode= new int[1];
		int[] UidLen = new int[1];
		byte[] UidBuf = new byte[20];
		// 提示信息
		gui.cls_show_msg("测试前请确保已经配置过，并已安装smart卡，完成点任意键继续");
		// 注册事件
		if ((ret = SmartRegistEvent(type)) != NDK_OK&&(ret = SmartRegistEvent(type)) != NDK_NO_SUPPORT_LISTENER) 
		{
			gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:smart事件注册失败(%d)", Tools.getLineInfo(),ret);
			return;
		}
		while(true)
		{
			// 测试前置，下电
			rfidDeactive(type,0);
			//测试退出点
			gui.cls_printf(String.format("请输入[%s]", kbName[i]).getBytes());
			if((ret = getInputHit(pnCode))!=NDK_OK||pnCode[0]!=kbCode[i])
			{
				gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime,"line %d:物理键盘按键测试失败（ret = %d）", Tools.getLineInfo(),ret);
				return;
			}
			else
			{
				i++;// 按键+1到下一个按键
			}
			if(i==kbName.length)
			{
				if(gui.ShowMessageBox("测试完成，是否会感觉到卡键".getBytes(), (byte) (BTN_OK|BTN_CANCEL), GlobalVariable.WAITMAXTIME)==BTN_OK)
				{
					gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
					return;
				}
				else
					gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "%s测试通过", TESTITEM);
				return;
			}
			//初始化寻卡
			if((ret = rfid_detect(type, UidLen, UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:射频卡寻卡失败（%d）", Tools.getLineInfo(),ret);
				continue;
			}	
			// 激活
			if((ret = rfidActive(type,felicaChoose,UidLen,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:射频卡激活失败（%d）", Tools.getLineInfo(),ret);
				continue;
			}
			
			if((ret = rfidApduRw(type,pnCode,UidBuf)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:射频卡APDU失败（%d）", Tools.getLineInfo(),ret);
				break;
			}
			
			// 下电
			if((ret = rfidDeactive(type,0)) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime,"line %d:射频卡关闭场失败（%d）", Tools.getLineInfo(),ret);
				continue;
			}
		}
		// 解绑事件
		if ((ret = SmartUnRegistEvent(type)) != NDK_OK) 
		{
			gui.cls_show_msg1_record(TAG, "rfid_input", g_keeptime, "line %d:smart事件解绑失败(%d)", Tools.getLineInfo(),ret);
			return;
		}

	}
}
