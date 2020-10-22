package com.example.highplattest.mpos;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.CommandInvokeResult;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
import com.newland.k21controller.util.BCDUtil;
/************************************************************************
 * 
 * module 			: 设备认证模块
 * file name 		: Auth6
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190429
 * directory 		: 
 * description 		: 指令集方式保存SN到文件(mpos)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 	zsh		  	20190429		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos26 extends UnitFragment{
	private final String TESTITEM = "指令集方式保存SN到文件(mpos)";
	private String fileName=Mpos26.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private Gui gui = new Gui(myactivity, handler);
	public enum NumerType
	{
		SN((byte)0x01),PN((byte)0x02),CSN((byte)0x04),KSN((byte)0x10);
		private byte value=0;
		
		NumerType(byte value)
		{
			this.value = value;
		}
		
		public byte num()
		{
			return value;
		}
	}
    private static String origin_PN="12345678";
    private static String origin_SN="12345678";
    private static String origin_KSN="12345678";
    private static String origin_CSN="12345678";
    private String RESULT;
	K21DeviceResponse k21DeviceResponse;
	
	public void mpos26() throws Exception
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",fileName,GlobalVariable.currentPlatform);
			return;
		}
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		byte psBuffer[] =new byte[2048] ;
		String message;
		boolean ret;
		boolean ret1=false;
		boolean ret2=false;
		boolean ret3=false;
		boolean ret4=false;
		String SN_PATH="/newland/appFsLocal/posInfo/posInfo.bin";
		String PN_number="N6NL99990100";
		String SN_number="N600001NL001002";
		String CSN_number="12345678";
		String KSN_number="ZSH";
		String MAX_number="123456789123456789123456789Zs";//PN,SN的最大传入长度为29,超过这个长度会设置失败
		String ABC_number="ABC";
		File SN_NOTE = new File(SN_PATH);
		
		// 测试前置:读取设备信息,获取初始的PN SN等,在测试结束后恢复
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		if(getPosNum(NumerType.PN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取本机PN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}else{
			origin_PN=RESULT;
		}
		LoggerUtil.v("origin_PN="+origin_PN);
		if(getPosNum(NumerType.SN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取本机SN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}else{
			origin_SN=RESULT;
		}
		if(getPosNum(NumerType.CSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取本机CSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}else{
			origin_CSN=RESULT;
		}
		if(getPosNum(NumerType.KSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取本机KSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}else{
			origin_KSN=RESULT;
		}
		
		//异常测试
		//case1 PN SN传空字符应返回false,且失败后应保持原值,
		if((ret1=setPosNum("", NumerType.PN))!=false||(ret2=setPosNum("", NumerType.SN))!=false){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:除KSN,CSN外传空字符应返回false,实际返回值与预期不符,PN(%s),SN(%s),CSN(%s),KSN(%s)", Tools.getLineInfo(),ret1,ret2,ret3,ret4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		else//进入else,测试是否保持原值
		{
			if(ret=getPosNum(NumerType.PN)==false||!RESULT.equals(origin_PN)){
				gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取到的PN发生改变,实际应保持不变(%s)(%s)(%s)", Tools.getLineInfo(),ret,RESULT,origin_PN);
				if(GlobalVariable.isContinue==false)
					return;
			}
			if(ret=getPosNum(NumerType.SN)==false||!RESULT.equals(origin_SN)){
				gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取到的SN发生改变,实际应保持不变(%s)(%s)(%s)", Tools.getLineInfo(),ret,RESULT,origin_SN);
				if(GlobalVariable.isContinue==false)
					return;
			}	
		}
		//case2 KSN,CSN传空字符应返回true
		if((ret3=setPosNum("", NumerType.CSN))!=true||(ret4=setPosNum("", NumerType.KSN))!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:KSN,CSN传空字符应返回true,实际返回值与预期不符,PN(%s),SN(%s),CSN(%s),KSN(%s)", Tools.getLineInfo(),ret1,ret2,ret3,ret4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//case3 传入最大长度,应设置成功,超过最大长度,应设置失败,失败后应保持原值(CSN和KSN的最大长度开发不清楚,暂时不测)
		if(setPosNum(MAX_number,NumerType.SN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:SN为最大长度时,应设置成功,实际设置失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(MAX_number+"h",NumerType.SN)==true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:SN超出最大长度,应设置失败,实际设置成功", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.SN)==false||!RESULT.equals(MAX_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取到的SN发生改变,实际应保持不变(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}	
		if(setPosNum(MAX_number,NumerType.PN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:SN为最大长度时,应设置成功,实际设置失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(MAX_number+"h",NumerType.PN)==true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:SN超出最大长度,应设置失败,实际设置成功", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.PN)==false||!RESULT.equals(MAX_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取到的PN发生改变,实际应保持不变(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//正常流程测试
		//case1 设置PN
		gui.cls_show_msg1(2, " case1:设置PN为数字和字母的自由组合,应设置成功");
		if(setPosNum(PN_number, NumerType.PN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机PN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		if(ret=getPosNum(NumerType.PN)!=true||!RESULT.equals(PN_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取PN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(PN_number+ABC_number, NumerType.PN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机PN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.PN)!=true||!RESULT.equals(PN_number+ABC_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取PN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(ABC_number, NumerType.PN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机PN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.PN)!=true||!RESULT.equals(ABC_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取PN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		
		//case2 设置SN,检验文件是否存在且内容正确
		gui.cls_show_msg1(2, " case2:设置SN为数字和字母的自由组合,检验文件是否存在且内容正确...");
		if(setPosNum(SN_number, NumerType.SN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机SN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.SN)!=true||!RESULT.equals(SN_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取SN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(SN_number+ABC_number, NumerType.SN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机SN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.SN)!=true||!RESULT.equals(SN_number+ABC_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取SN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(ABC_number, NumerType.SN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机SN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.SN)!=true||!RESULT.equals(ABC_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取SN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(!SN_NOTE.exists()){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:测试不通过,存储SN的文件不存在", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		//文件的读取需要修改权限并找振龙签名.
		if(gui.cls_show_msg("当前apk是否已经获得系统签名,是[确认]进行文件内容的检验,否[取消]跳过文件读取")!=ESC){
			FileSystem file=new FileSystem();
			file.JDK_FsRead(SN_PATH,psBuffer,0,psBuffer.length);
			message=psBuffer.toString();
			if(message.equals(ABC_number)){
				gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:文件获取到的SN与设置不一致(%s)", Tools.getLineInfo(),message);
				if(GlobalVariable.isContinue==false)
					return;
			}
		}
		
		//case3 设置CSN
		gui.cls_show_msg1(2, " case3:设置CSN为数字和字母的自由组合...");
		if(setPosNum(CSN_number, NumerType.CSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机CSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(CSN_number+ABC_number, NumerType.CSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机CSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(setPosNum(ABC_number, NumerType.CSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机CSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.CSN)!=true||!RESULT.equals(ABC_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取CSN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//case4 设置KSN(开发没有给具体的格式规范和非正常预期,自测时传入长度稍长的数字会显示其他符号或字母,这里仅测试了最常规的情况)
		gui.cls_show_msg1(2, "case4:设置KSN为ZSH...");
		if(setPosNum(KSN_number, NumerType.KSN)!=true){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:设置本机KSN失败", Tools.getLineInfo());
			if(GlobalVariable.isContinue==false)
				return;
		}
		if(ret=getPosNum(NumerType.KSN)!=true||!RESULT.equals(KSN_number)){
			gui.cls_show_msg1_record(fileName, "auth6", gKeepTimeErr, "line %d:获取KSN失败(%s)(%s)", Tools.getLineInfo(),ret,RESULT);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(TESTITEM, "auth6", gKeepTimeErr, "测试通过,长按确认退出测试");
	}
    
	public boolean setPosNum(String data, NumerType numstyle) {
		byte[] packcontent = CalDataLrc.setProCmd(ISOUtils.hex2byte("FF02"), new byte[]{numstyle.num()}, data.getBytes());
        K21DeviceResponse Resp = k21ControllerManager.sendCmd(new K21DeviceCommand(packcontent),null);
        if (Resp.getInvokeResult() == CommandInvokeResult.SUCCESS) {
            if (Resp.getResponse()[7] == 0x30 && Resp.getResponse()[8] == 0x30) {
            	return true;
            } else {
            	return false;
            }
        }
        return true;
    }
    
	   public  boolean getPosNum(NumerType numstyle) 
	   {
	        byte[] packcontent = CalDataLrc.setProCmd(ISOUtils.hex2byte("FF01"), new byte[]{numstyle.num()}, new byte[]{});
	        K21DeviceResponse Resp = k21ControllerManager.sendCmd(new K21DeviceCommand(packcontent),null);
	        if (Resp.getInvokeResult() == CommandInvokeResult.SUCCESS) {
	            if (Resp.getResponse()[7] == 0x30 && Resp.getResponse()[8] == 0x30) {
	                byte[] response = Resp.getResponse();
	                byte[] len = new byte[2];
	                System.arraycopy(response, 10, len, 0, 2);
	                int lenth = BCDUtil.bcd2Int(len, 4);
	                byte[] target = new byte[lenth];
	                System.arraycopy(response, 12, target, 0, lenth);
	                RESULT=ISOUtils.ASCII2String(target);
	            } else {
	            	return false;
	            }
	        }
			return true;
	    }
	   
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		//测试后置:恢复初始的PN SN KSN CSN等
		setPosNum(origin_KSN, NumerType.KSN);
		setPosNum(origin_SN, NumerType.SN);
		setPosNum(origin_PN, NumerType.PN);
		setPosNum(origin_CSN, NumerType.CSN);
	}
	

}