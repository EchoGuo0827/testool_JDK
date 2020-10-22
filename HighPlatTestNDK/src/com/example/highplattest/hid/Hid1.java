package com.example.highplattest.hid;

import java.io.File;
import java.io.UnsupportedEncodingException;

import android.newland.AnalogSerialManager;
import android.newland.content.NlContext;
import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.newland.NlBluetooth.util.FileUtil;
import com.newland.hid.UsbManager;
import com.newland.k21controller.util.Dump;
/************************************************************************
 * 
 * file name 		: Hid1.java 
 * history 		 	: 变更记录															变更时间			变更人员
 *			  		  增加F7/F10设备通过HID接口向上位机传输数据的测试案例，测试上位机使用HIDTest.exe		20200427	 	魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Hid1 extends UnitFragment{

	/*------------global variables definition-----------------------*/
	private final String TESTITEM = "K21模拟USB-Hid";
	private final String CLASS_NAME = Hid1.class.getSimpleName();
	private AnalogSerialManager usbManager = null;/**USB串口操作句柄*/
	private Gui gui = new Gui(myactivity, handler);
	private int totalCount = 0;
    private boolean ret = false;
    private int ret1 = -1;
    private static File usbmode_switch = new File("/sys/class/usb_ctrl/otg_mode");
    //成功确认
    private static final byte[] ACK_SUCCESS = new byte[]{0x02, 0x02, 0x00, 0x00, 0x00, 0x03};
    //失败确认
    private static final byte[] ACK_FAILED = new byte[]{0x02, 0x02, 0x00, 0x01, 0x00, 0x03};
    //发送间隔
    private int delay;
    //测试次数
    private int times;
    //数据长度
    private int dataLen;
    //数据内容
    private String sendData;
    //HID节点
    private String HID_PATH = "/dev/ttyS9";
    //错误节点
    private String errPath = "/dev/ttyS0";
    
	public void hid1(){
		String funcName="hid1";
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME,funcName,gScreenTime,"%s用例不支持自动化测试,请手动验证", TESTITEM);
			return;
		}
        usbManager = (AnalogSerialManager) myactivity.getSystemService(NlContext.ANALOG_SERIAL_SERVICE);
        if (usbManager == null) {
        	gui.cls_show_msg1(1,"usb初始化失败\n");
            return;
        }
		
		/*private & local definition*/
        totalCount = 0;
        
    	//前置：关闭USB主模式
        setUSBHostMode(false);
		if((ret = getUSBHostMode())!=false)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%s设置USB主模式状态失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(( ret1 = usbManager.open())<=0)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sUSB打开失败(%d)", Tools.getLineInfo(),TESTITEM,ret1);
			if(GlobalVariable.isContinue==false)
				return;
		}

		//使用上位机下发指令
        gui.cls_show_msg("请用USB线连接POS和PC端，打开HIDTest.exe设置串口、次数和发送数据，光标停留在接收数据框并设置为英文输入法，点击开始后任意键继续");
        gui.cls_show_msg1(1,"读数据测试...\n");
        byte[] buf = new byte[2048];
        while (true) {
            int read = usbManager.read(buf, 2048, 0);
            if (read > 0) {
                synchronized (this) {
            		if((ret = parseSettings(buf))!=true)
            		{
            			int failed = usbManager.write(ACK_FAILED, ACK_FAILED.length, 0);
            			gui.cls_show_msg1_record(CLASS_NAME, funcName, gKeepTimeErr, "line %d:%s解析数据失败(failed=%d)", Tools.getLineInfo(),TESTITEM,failed);
            			if(GlobalVariable.isContinue==false)
            				return;
            		}else{
            			 int success = usbManager.write(ACK_SUCCESS, ACK_SUCCESS.length, 0);
            			 gui.cls_show_msg("数据长度: " + dataLen + "\n"+"测试次数: " + times + "\n"+"接收数据: "+ "发送间隔: " + delay + "ms\n"+ sendData + "\n");
//                       gui.cls_show_msg("解析成功 usb write(" + success + ") " + Dump.getHexDump(ACK_SUCCESS)+"\n");
                       Log.e("usb write",Dump.getHexDump(ACK_SUCCESS)+"\n");
                       break;
            		}
                }
                 
            }
        }
        usbManager.close();
              
        //case1:HID功能未打开，应传送失败
        gui.cls_show_msg1(1,"case1:HID功能未打开，应传送失败");
        if((ret1 = write(sendData))>0)
        {
            gui.cls_show_msg1_record(CLASS_NAME, funcName, gKeepTimeErr, "line %d:%sHID发送数据应失败(ret1=%d)", Tools.getLineInfo(),TESTITEM,ret1);
            if(GlobalVariable.isContinue==false)
                return;
        }
        
        //case2:打开错误节点，应传送失败
        gui.cls_show_msg1(1,"case2:打开错误节点，应传送失败");
        //打开错误节点
        if((ret = UsbManager.open(errPath))==true)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID打开失败", Tools.getLineInfo(),TESTITEM);
            if(GlobalVariable.isContinue==false)
                return;
        }
        if((ret1 = UsbManager.setConfig(BpsBean.bpsValue))>=0)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%s设置HID参数应失败(ret1=%d)", Tools.getLineInfo(),TESTITEM,ret1);
            if(GlobalVariable.isContinue==false)
                return;
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }
        
        if((ret1 = write(sendData))>0)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID发送数据应该失败(ret1=%d)", Tools.getLineInfo(),TESTITEM,ret1);
            if(GlobalVariable.isContinue==false)
                return;
        }
        
        UsbManager.close();
        
        //case3:正常测试，应发送成功
        gui.cls_show_msg1(1,"case3:正常测试，应发送成功");
        
        //打开HID
        if((ret = UsbManager.open(HID_PATH))!=true)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID打开失败", Tools.getLineInfo(),TESTITEM);
            if(GlobalVariable.isContinue==false)
                return;
        }
        if((ret = UsbManager.isOpen())!=true)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID打开失败", Tools.getLineInfo(),TESTITEM);
            if(GlobalVariable.isContinue==false)
                return;
        }
        if((ret1 = UsbManager.setConfig(BpsBean.bpsValue))<0)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%s设置HID参数失败(ret1=%d)", Tools.getLineInfo(),TESTITEM,ret1);
            if(GlobalVariable.isContinue==false)
                return;
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            return;
        }
        
        gui.cls_show_msg1(1,"写数据测试...\n");
        while (true) {
            totalCount++;
            if((ret1 = write(sendData))<=0)
            {
                gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID发送数据失败(ret1=%d)", Tools.getLineInfo(),TESTITEM,ret1);
                if(GlobalVariable.isContinue==false)
                    return;
            }
            gui.cls_show_msg1(1,"第%d次发送成功(ret1=%d),共%d次",totalCount,ret1,times);
            if (totalCount >= times) {
                break;
            }
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
        }
        
		if(gui.cls_show_msg("PC端接收到的数据及次数是否与预期格式一致,一致[确认],不一致[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(GlobalVariable.isContinue==false)
				return;
		}

        //关闭HID
        if((ret = UsbManager.close())!=true)
        {
            gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gKeepTimeErr, "line %d:%sHID关闭失败", Tools.getLineInfo(),TESTITEM);
            if(GlobalVariable.isContinue==false)
                return;
        }

        gui.cls_show_msg1_record(CLASS_NAME, "usbcomm10", gScreenTime,"%s测试通过,长按确认键退出", TESTITEM);
	}
    
    /**
     * 解析参数
     * 02 [数据长度(2位)] [测试次数(4位)] [发送间隔(4位)] [数据内容] 03
     */
    private synchronized boolean parseSettings(byte[] data) {
        if (data.length < 12) {
            return false;
        }
        if (data[0] != 0x02) {
            return false;
        }

        byte[] data2Bytes = new byte[2];
        byte[] data4Bytes = new byte[4];
//        Log.e("test", "接收参数:" + Dump.getHexDump(data));
        //数据长度
        System.arraycopy(data, 1, data2Bytes, 0, 2);
        dataLen = bytes2ToInt(data2Bytes) - 8;
//        Log.e("test", "数据长度 " + dataLen);
        //测试次数
        System.arraycopy(data, 3, data4Bytes, 0, 4);
        times = bytes4ToInt(data4Bytes);
//        Log.e("test", "测试次数 " + times);
        //发送间隔
        System.arraycopy(data, 7, data4Bytes, 0, 4);
        delay = bytes4ToInt(data4Bytes);
//        Log.e("test", "发送间隔 " + delay);
        //发送内容
        byte[] tmp = new byte[dataLen];
        System.arraycopy(data, 11, tmp, 0, dataLen);
        if (data[11 + dataLen] != 0x03) {
            return false;
        }
        try {
            sendData = new String(tmp,"GBK");
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        
        return true;
    }
    
    public static int bytes4ToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24));
        return value;
    } 

    
    public static boolean getUSBHostMode() {
        if (!usbmode_switch.exists()) {
            return false;
        }
        if (FileUtil.readFileByLines(usbmode_switch).contains("1"))
            return true;
        else {
            return false;
        }
    }
    
    public static void setUSBHostMode(boolean mode) {
        if (!usbmode_switch.exists()) {
            return;
        }
        if (mode) {
            FileUtil.writeFileByString(usbmode_switch, "1", false);
        } else {
            FileUtil.writeFileByString(usbmode_switch, "0", false);
        }
    }
    
    public byte[] read() {
        byte[] buf = new byte[2048];
        int ret = UsbManager.read(buf);
        if (ret > 0) {
            return buf;
        } else {
            return null;
        }
    }
    
    public int write(String text) {
        text = "#" + text + "|";
            try {
                return UsbManager.write(text);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
			return 0;
    }

    public static int bytes2ToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)
                | ((src[1] & 0xFF) << 8));
        return value;
    }
	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
