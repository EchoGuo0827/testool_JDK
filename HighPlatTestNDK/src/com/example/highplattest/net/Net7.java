package com.example.highplattest.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.newland.SettingsManager;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.ChangeWireType;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

public class Net7 extends UnitFragment {
	private final String TESTITEM = "网络模式切换测试(新)";
	private final String CLASS_NAME = Net7.class.getSimpleName();
	static public final int NETWORK_TYPE_4G3G2G_AUTO = 4;
	static public final int NETWORK_TYPE_3G2G_AUTO = 3;
	static public final int NETWORK_TYPE_2G_ONLY = 2;
	private int cardchoose=100;   //联通卡为1  移动卡为2  电信卡为3
	Gui gui = new Gui(myactivity, handler);
	int getNetMode;
	boolean ret=false;
	String mUrl="https://www.baidu.com";//用于检验网络是否可用
	private TelephonyManager tm;
	private ChangeWireType change = null;
	
	
	/** 根据赵明权的建议，移动网络这块都不要使用原生的TelephonyManager测试这两点使用到了0.网络模式测试\n1.卡片选择\n by zhengxq 20200317*/
	public void net7(){
		// N700的海外和巴西不支持改接口
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false&&GlobalVariable.currentPlatform==Model_Type.N700)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "net7", 1,"line %d:该产品的海外版本不支持getPreferredNetworkType接口");
			return;
		}
		while(true){
			int returnValue=gui.cls_show_msg("网络模式切换测试\n2.无卡网络测试");
			switch (returnValue) 
			{
			/*case '0':
				if (cardchoose==100) {
					gui.cls_show_msg1(1,"请先选择卡片类型---");
					break;
				}
				normalNetModeTest();
				break;
			case '1':
				int nkeyIn = gui.cls_show_msg("卡片选择\n0.联通卡\n1.移动卡\n2.电信卡");
				switch (nkeyIn) {
				case '0':
					cardchoose=1;
					break;
				case '1':
					cardchoose=2;
					break;
				case '2':
					cardchoose=3;
					break;
				default:
					gui.cls_show_msg("请选择卡片类型---");
					break;
					}
				break;	*/
				
			case '2':
				noSimNetModeTest();
				break;
			case ESC:
				return;
			}
		}
	}
	private void normalNetModeTest() 
	{
		// TODO Auto-generated method stub
		String funcName = Thread.currentThread().getStackTrace()[1].getMethodName();
		tm = (TelephonyManager) myactivity.getSystemService(Context.TELEPHONY_SERVICE);
		change = new ChangeWireType(myactivity);
        int simState = tm.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT || simState == TelephonyManager.SIM_STATE_UNKNOWN){
        	gui.cls_show_msg("本案例测试网络模式切换,需要移动卡,联通卡,电信卡,请先插入sim卡并重启,任意键继续..");
        	return;
        }
        gui.cls_show_msg("本案例测试网络模式切换,需确保网络已经开启,任意键继续..");
        
        gui.cls_show_msg1(1,"正在切换网络至4G,请等待..");
        if(!change.changeWire(22))
        {
        	gui.cls_show_msg1(1,"切换4G网络失败，测试失败");
        	return;
        }
        for(int i=0;i<=10;i++){
			gui.cls_show_msg1(1,"等待10s至网络模式切换成功,剩余%d秒",10-i);
			SystemClock.sleep(1000);
		}
        if(change.getWire()!= 22){
        	gui.cls_show_msg1(1,"切换4G网络失败，测试失败");
        	return;
        }
        gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
		if((ret=isNetAvailable(mUrl))!=true){
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
			return;
		}
        
        //3G网络（联通卡和电信卡）
        if((cardchoose==1) || (cardchoose==3)){
        	gui.cls_show_msg1(1,"正在切换网络至3G,请等待..");
            if(!change.changeWire(21))
            {
            	gui.cls_show_msg1(1,"切换3G网络失败，测试失败");
            	return;
            }
            for(int i=0;i<=20;i++){
    			gui.cls_show_msg1(1,"等待20s至网络模式切换成功,剩余%d秒",20-i);
    			SystemClock.sleep(1000);
    		}
            if(change.getWire()!= 21){
            	gui.cls_show_msg1(1,"切换3G网络失败，测试失败");
            	return;
            }
            gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				return;
			}
        }
        
      //2G网络（移动卡和电信卡）
        if((cardchoose==2) || (cardchoose==3)){
        	gui.cls_show_msg1(1,"正在切换网络至2G,请等待..");
            if(!change.changeWire(24))
            {
            	gui.cls_show_msg1(1,"切换2G网络失败，测试失败");
            	return;
            }
            for(int i=0;i<=20;i++){
    			gui.cls_show_msg1(1,"等待20s至网络模式切换成功,剩余%d秒",20-i);
    			SystemClock.sleep(1000);
    		}
            if(change.getWire()!= 24){
            	gui.cls_show_msg1(1,"切换2G网络失败，测试失败");
            	return;
            }
            gui.cls_show_msg1(1,"正在测试当前网络是否可用,请等待..");
			if((ret=isNetAvailable(mUrl))!=true){
				gui.cls_show_msg1_record(CLASS_NAME,funcName,gKeepTimeErr, "line %d:测试失败,当前网络不可用(getNetMode=%d,ret=%s)",Tools.getLineInfo(),getNetMode,ret+"");
				return;
			}
        }
        
        gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s测试通过", TESTITEM);
	}
	
	public boolean isNetAvailable(String netFileUrl)
	{
		InputStream netFileInputStream =null;
		try{
		     URL url= new URL(netFileUrl);   
		     URLConnection urlConn= url.openConnection();   
		     netFileInputStream = urlConn.getInputStream(); 
		 }catch (IOException e)
		 {
		     return false;
		 }
		 if(null!=netFileInputStream){
		      return true;
		 }else{
		     return false;
		 }
	}
	
	//by huhuij
	private void noSimNetModeTest()
	{
		gui.cls_show_msg("本案例测试无卡状态下获取的网络类型值为4G/3G/2G(4)，请确认未插入sim卡,任意键继续..");
		int workType = -1;
		SettingsManager mSettingsManager=(SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		if((workType=mSettingsManager.getPreferredNetworkType())==4)
		{
			gui.cls_show_msg("无卡状态下获取的网络类型值为4，测试通过");
		}
		else
		{
			gui.cls_show_msg("无卡状态下获取的网络类型值不为4(获取到的值为：%d)，测试失败",workType);
		}
	}
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
