package com.example.highplattest.mpos;

import java.util.concurrent.TimeUnit;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.k21controller.K21DeviceResponse;
/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec6
 * Author 			: zhengxq 
 * version 			: 
 * DATE 			: 20180518
 * directory 		: 
 * description 		: mpos指令集删除密钥
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180518		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos14 extends UnitFragment
{
	private final String TESTITEM = "密钥删除(mpos)";
	public final String TAG = Mpos14.class.getSimpleName();
	private K21ControllerManager k21ControllerManager;
	private K21DeviceResponse response;
	private Gui gui = new Gui(myactivity, handler);
	
	private String[][] secLoad = 
		{
			{"13131313131313131313131313131313",null}, 					// 0	//TLK (key=16个0x13, TLK,ID=1)
			{"E14647E8A135061AE14647E8A135061A",	"3ADEBBE0"}, 	// 1	//TMK SEC_KCV_ZERO方式(TMK ID=2, 密文由TLK发散,明文KEY=16个0x17)
			{"CB62659448AC8A72CB62659448AC8A72","265A7ABF"},	// 2	//TMK SEC_KCV_ZERO方式(TMK ID=3, 明文安装,明文KEY=16个0x15)
			{"2E381D92018858222E381D9201885822","4BB0C823"},	// 3	//TAK SEC_KCV_VAL方式(TAK ID=4, TMK发散,key明文16个0x1f)
			{"FF4CAFA1330976ECFF4CAFA1330976EC","C335C9D8"}, 	// 4	//TPK SEC_KCV_VAL方式(TPK ID=5, 由ID=3,type=TMK发散,key明文16个0x25)
			{"8AEEE91A8C2F71978AEEE91A8C2F7197","50AF41CD"},	// 5	//TDK SEC_KCV_VAL方式key:8位(TDK ID=6, 由ID=3,type=TMK发散,len=8,key明文8个0x29)
			//后6组为SM4算法密钥
			{"19191919191919191919191919191919",null},                       // 6  //TLK (key=16个0x19, TLK,ID=1)
			{"51E96A1A869F3B7C9BD33F6C344C7632","594F262E"}, // 7  //TMK SEC_KCV_ZERO方式(TMK ID=2, 密文由TLK发散,明文KEY=16个0X1F)
			{"21212121212121212121212121212121","6CD76C59"}, // 8  //TMK SEC_KCV_ZERO方式(TMK ID=3, 明文安装,明文KEY=16个0X21) 
		    {"C0D3EB69B10647EF76D2496275A48BFB","933875D7"},	//9  //TAK SEC_KCV_ZERO方式(TAK ID=4, TMK发散,key明文16个0X27 )
			{"444E8D2A2AAA91B4AD479478E67D7175","2E368F3C"},	//10  //TPK SEC_KCV_ZERO方式(TPK ID=5, 由ID=3,type=TMK发散,key明文16个0X2D)
		    {"0848698377F71B33C54D5C4FF513B2C7","A43FDDA6"}};   //11  //TDK SEC_KCV_ZERO方式(TDK ID=11, 由ID=6,type=TMK发散,key明文16个0X33) 
	
	public void mpos14()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",TAG,GlobalVariable.currentPlatform);
			return;
		}
		// 测试前置：删除所有密钥装载各种密钥
		/*private & local definition*/
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		String retCode;
		byte[] retContent;
		String kcv;
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		gui.cls_show_msg1(1, "%s测试中...", TESTITEM);
		// 删除所有密钥 mpos会自己安装TLK
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
//		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A02_TLK), null);
		// TLK
		// 装载主密钥,ID=02
		byte[] pack_TMK = CalDataLrc.mposPack(new byte[]{0x1A,0x02}, ISOUtils.hex2byte("02020016"+secLoad[2][0]+"FF0004265A7ABF"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TMK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TMK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 密文装载TAK,ID=4
		byte[] pack_TAK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302040016"+secLoad[3][0]+"0004"+secLoad[3][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 密文装载TAK,ID=201
		byte[] pack_TAK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302C90016"+secLoad[3][0]+"0004"+secLoad[3][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TAK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 密文装载TPK,ID=5
		byte[] pack_TPK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202050016"+secLoad[4][0]+"0004"+secLoad[4][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TPK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 密文装载TPK,ID=201
		byte[] pack_TPK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202C90016"+secLoad[4][0]+"0004"+secLoad[4][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TPK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 密文装载TDK,ID=6
		byte[] pack_TDK1 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102060016"+secLoad[5][0]+"0004"+secLoad[5][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK1), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TDK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 密文装载TDK,ID=201
		byte[] pack_TDK2 = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102C90016"+secLoad[5][0]+"0004"+secLoad[5][1]+"00"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:安装TDK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case1:删除正确的ID=6的TDK密钥后获取kcv失败
		byte[] pack_del_TDK = CalDataLrc.mposPack(new byte[]{0x1A,0x20}, ISOUtils.hex2byte("01000106"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_del_TDK), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:删除TDK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		byte[] pack_TDK_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0102060016"+secLoad[5][0]+"0008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv返回值有误(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case2:删除后不影响其他未删除的ID密钥，获取ID=4的TAK密钥kcv应成功
		byte[] pack_TAK_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302040016"+secLoad[3][0]+"0008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		if((kcv=ISOUtils.hexString(retContent, 11, 4)).equals(secLoad[3][1])==false)
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv失败(%s)",Tools.getLineInfo(),kcv);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case5:擦除索引1-200的主密钥和工作密钥，200以后的密钥应不受影响
		byte[] pack_del_Some = CalDataLrc.mposPack(new byte[]{0x1A,0x20}, ISOUtils.hex2byte("050000"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_del_Some), 20,TimeUnit.SECONDS,null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:删除TDK失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		byte[] pack_TAK2_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302C90016"+secLoad[3][0]+"0008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK2_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		byte[] pack_TDK2_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0302C90016"+secLoad[3][0]+"0008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case6:验证索引200之后的密钥未被擦除
		byte[] pack_TPK_kcv = CalDataLrc.mposPack(new byte[]{0x1A,0x05},ISOUtils.hex2byte("0202C90016"+secLoad[4][0]+"0008000000000000000002"));
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// case7:删除全部密钥
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 7, 2)).equals("00")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:删除所有密钥失败(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// case8:验证所有密钥已擦除(TAK,TPK,TDK)
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TAK2_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TDK2_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		response = k21ControllerManager.sendCmd(new K21DeviceCommand(pack_TPK_kcv), null);
		retContent = response.getResponse();
		if((retCode=ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)	
		{
			gui.cls_show_msg1_record(TAG, "sec6", gKeepTimeErr, "line %d:获取kcv成功(%s)",Tools.getLineInfo(),retCode);
			if(GlobalVariable.isContinue==false)
				return;
		}
		

		
		gui.cls_show_msg1_record(TAG, "sec6", gScreenTime, "%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A20_All), null);
		k21ControllerManager.close();
	}

}
