package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.PacketBean;
import com.example.highplattest.main.constant.DataConstant;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.EM_SEC_KEY_TYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.CalDataLrc;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.ControllerException;
import com.newland.k21controller.K21ControllerManager;
import com.newland.k21controller.K21DeviceCommand;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest36.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180524
 * directory 		: 
 * description 		: mpos与NDK方式密钥区交叉
 * related document :
 * history 		 	: author			date			remarks
 *			  		 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest36 extends DefaultFragment
{
	private final String TAG = SysTest36.class.getSimpleName();
	private final String TESTITEM = "mpos/NDK密钥";
	private K21ControllerManager k21ControllerManager;
	private Gui gui = null;
	
	// 加密密钥数据
	byte[][] keyValue = {
			ISOUtils.hex2byte("31313131313131313131313131313131"),// TLK(16个0x13)
			ISOUtils.hex2byte("E14647E8A135061AE14647E8A135061A"),// 明文16个0x17
			ISOUtils.hex2byte("8AEEE91A8C2F7197"),
			ISOUtils.hex2byte("373737373737373737373737373737373737373737373737"),
			ISOUtils.hex2byte("3B3B3B3B3B3B3B3B3B3B3B3B3B3B3B3B"),
	};
	
	byte[][] mposValue = {
			ISOUtils.hex2byte("0200241A022FE505010016655EA628CF62585F655EA628CF62585F03E7"),//TLK
			ISOUtils.hex2byte("0200311A022F8102010016253C9D9D7C2FBBFA253C9D9D7C2FBBFAFF000482E13665035A"),// ID=1，TMK,明文16字节11
			ISOUtils.hex2byte("0200311A022F79026500169BC9FB82A59257269BC9FB82A5925726FF000400962B60032B"),//ID=101，TMK,16字节22
			ISOUtils.hex2byte("0200321A052FD503010A0016C0B757413B847925C0B757413B8479250004A8B7B5BD0003D9"),// ID=10，TAK 16字节的13
			ISOUtils.hex2byte("0200321A052FE10201C90016A929DB826A498C15145F5C6E3D9144570004E6513BFE0003EF"),// ID=201，TPK 8字节12+8字节15
			ISOUtils.hex2byte("0200321A052FAE03010A00164BCE5825E910F99E4BCE5825E910F99E00049DA493AA0203B7"),//5 kcv ID=10,TAK 16字节的19
			ISOUtils.hex2byte("0200321A052F050201C9001677C4B0F42256D284145F5C6E3D91445700047269893202036D"),//6 kcv,ID=201,TPK 8字节20+8字节15
	};
	
	public void systest36()
	{
		int kcvLen = GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)?4:3;// 国内的KCV长度是4，海外的是3
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gSequencePressFlag){
			mpos_ndk(kcvLen);
			return;
		}
		while(true)
		{
			int nkeyIn=gui.cls_show_msg("mpos/NDK密钥\n0.交叉压力");
			switch (nkeyIn) 
			{
			case '0':
				try {
					mpos_ndk(kcvLen);
				} catch (Exception e) {
					e.printStackTrace();
					gui.cls_show_msg1_record(TAG, "systest36", 0, "line %d:抛出异常%s", Tools.getLineInfo(),e.getMessage());
				}
				break;

			case ESC:
				intentSys();
				return;
				
			default:
				break;
			}
		}

	}
	
	public void mpos_ndk(int KCV_LEN)// 因为存在mtms应用使用NDK方式调用，而应用使用mpos方式调用的情况
	{
		/*private & local definition*/
		int ret = -1,cnt = 0, bak = 0,succ=0;
		String retCode;
		byte[] retContent=new byte[128];
		SecKcvInfo kcvInfo = new SecKcvInfo();
		byte[] kcv = new byte[KCV_LEN];
		byte[] ksn = new byte[10];
		/*process body*/
		//设置压力次数
		final PacketBean packet = new PacketBean();
		if(GlobalVariable.gSequencePressFlag)
			packet.setLifecycle(getCycleValue());
		else
			packet.setLifecycle(gui.JDK_ReadData(TIMEOUT_INPUT,ABILITY_VALUE));;
		bak = cnt = packet.getLifecycle();
		
		// 测试前置，连接mpos
		k21ControllerManager = K21ControllerManager.getInstance(myactivity);
		try 
		{
			k21ControllerManager.connect();
		} catch (ControllerException e) {
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:mpos方式连接失败(%s)",Tools.getLineInfo(),e.getMessage());
			return;
		}
		
		while(cnt > 0)
		{
			if(gui.cls_show_msg1(3, "%s交叉测试,已执行%d次,成功%d次,[取消]退出测试...", TESTITEM,bak-cnt,succ)==ESC)
				break;
			cnt--;
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
			{
				JniNdk.JNI_Sec_KeyErase();
				JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), new SecKcvInfo());
			}
			
			//case1:使用NDK_SetKeyOwner设置密钥区在[共享密钥端]（*）
			if((ret = JniNdk.JNI_Sec_SetKeyOwner("*"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(), bak-cnt,TESTITEM,ret);
				continue;
			}
			
			// case2:使用mpos方式安装主密钥、工作密钥(ID=1、10、101、201)dukpt密钥(ID=5)
			for (int i = 0; i < 5; i++) // kcv校验值在命令里面已做
			{
				LoggerUtil.e("i="+i);
				retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(mposValue[i]), null).getResponse();
				if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
				{
					gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,(i+1),TESTITEM,retCode);
					continue;
				}
			}
			
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(DataConstant.Sec_1A17_TIK), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			
			// case3.1:使用NDK方式检测mpos方式安装的主密钥、工作密钥应能检测到(ID=1,10,101,201)
			kcvInfo.nCheckMode=1;
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)1, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:检测mpos方式ID=1的TMK测试失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
				continue;
			}
			
			if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte("82E13665"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(kcv=%s)",Tools.getLineInfo(), bak-cnt,TESTITEM,ISOUtils.hexString(kcvInfo.sCheckBuf, 0, 4));
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)10, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:检测mpos方式ID=10的TAK测试失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
				continue;
			}
			if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte("A8B7B5BD"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(kcv=%s)",Tools.getLineInfo(), bak-cnt,TESTITEM,ISOUtils.hexString(kcvInfo.sCheckBuf, 0, 4));
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)101, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:检测mpos方式ID=101的TMK测试失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
				continue;
			}
			if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte("00962B60"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(kcv=%s)",Tools.getLineInfo(), bak-cnt,TESTITEM,ISOUtils.hexString(kcvInfo.sCheckBuf, 0, 4));
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)201, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:检测mpos方式ID=201的TDK测试失败(%d)",Tools.getLineInfo(), bak-cnt,ret);
				continue;
			}
			if(Tools.memcmp(kcvInfo.sCheckBuf, ISOUtils.hex2byte("E6513BFE"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(kcv=%s)",Tools.getLineInfo(), bak-cnt,TESTITEM,ISOUtils.hexString(kcvInfo.sCheckBuf, 0, 4));
				continue;
			}
			// 获取dukpt的ksn
			if((ret = JniNdk.JNI_Sec_GetDukptKsn((byte)5, ksn))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(ret=%d)",Tools.getLineInfo(), bak-cnt,TESTITEM,ret);
				continue;
			}
			if(Tools.memcmp(ksn, ISOUtils.hex2byte("FFFFFFFFFFFFFFE00001"), 10)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(ksn=%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,ISOUtils.hexString(ksn));
				continue;
			}
			
			// case3.2:使用mpos方式检测mpos方式安装的主密钥、工作密钥应能检测到(无法检测主密钥，智能检测工作密钥)
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(mposValue[5]), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			// 校验kcv值
			System.arraycopy(retContent, 11, kcv, 0, KCV_LEN);
			if(Tools.memcmp(kcv, ISOUtils.hex2byte("A8B7B5BD"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,kcv);
				continue;
			}
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(mposValue[6]), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			
			System.arraycopy(retContent, 11, kcv, 0, KCV_LEN);
			if(Tools.memcmp(kcv, ISOUtils.hex2byte("E6513BFE"), KCV_LEN)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,kcv);
				continue;
			}
			// case4:使用NDK方式安装主密钥、工作密钥（ID=2,56,111,222）,dukpt密钥（ID=2）
			// ID=2，TMK
			kcvInfo.nCheckMode = 0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)2, 16, keyValue[1], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// ID=111,TDK
			kcvInfo.nCheckMode = 0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TDK.ordinal(), (byte)1, (byte)111, 16, keyValue[2], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// ID=222,TPK
			kcvInfo.nCheckMode = 0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TPK.ordinal(), (byte)0, (byte)222, 24, keyValue[3], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// ID=56,TAK
			kcvInfo.nCheckMode = 0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal(), (byte)0, (byte)56, 16, keyValue[4], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			
			if((ret = JniNdk.JNI_Sec_LoadTIK((byte)2, (byte)0, (byte)16, ISOUtils.hex2byte("21212121212121212121212121212121"), ISOUtils.hex2byte("12121212121212121212"), null))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			// case5.1:使用mpos方式检测NDK安装的主密钥、工作密钥应能检测到
//			byte[] kcv_TDK = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("03026F0000000002"));// 检测不到TDK，实际是TAK，故修改为TAK
			byte[] kcv_TPK = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("0202DE0000000002"));
			byte[] kcv_TAK = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("0302380000000002"));
			
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TPK), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TAK), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			
			// case5.2:使用NDK方式检测NDK安装的主密钥、工作密钥应能检测到
			kcvInfo.nCheckMode = 1;
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)2, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)4, (byte)111, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)222, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)3, (byte)56, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			if((ret = JniNdk.JNI_Sec_GetDukptKsn((byte)2, ksn))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if(Tools.memcmp(ksn, ISOUtils.hex2byte("12121212121212000001"), 10)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(ksn=%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,ISOUtils.hexString(ksn));
				continue;
			}
			// case6:切换NDK_SetKeyOwner到[Android端]（com.newland.jdk）,切换到一个新的表需要安装TLK
			if((ret = JniNdk.JNI_Sec_SetKeyOwner("com.newland.jdk"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			kcvInfo.nCheckMode=0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, keyValue[0], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			// case7:使用NDK方式安装主密钥、工作密钥（索引20）
			kcvInfo.nCheckMode = 0;
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TAK.ordinal(), (byte)0, (byte)20, 16, keyValue[4], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TMK.ordinal(), (byte)1, (byte)20, 16, keyValue[1], kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// case8.1:使用mpos方式检测NDK方式安装的主密钥、工作密钥应失败（ID=20检测失败，ID=10检测成功）
			byte[] kcv_TAK_20 = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("0302140000000002"));
			byte[] kcv_TAK_10 = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("03010A0000000002"));
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TAK_20), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("47")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TAK_10), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			
			// case8.2:使用NDK方式检测NDK方式安装的主密钥、工作密钥应成功（ID=20可检测到，ID=1检测不到）
			kcvInfo.nCheckMode = 1;
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)20, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)1, kcvInfo))!=NDK_ERR_SECKM_READ_REC)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// case9：切换NDK_SetKeyOwner设置密钥区在[共享密钥端]（*）
			if((ret = JniNdk.JNI_Sec_SetKeyOwner("*"))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			// case10.1:使用NDK方式检测[共享密钥区]主密钥、工作密钥应能检测到（ID=1、2、201）
			kcvInfo.nCheckMode=1;
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)1, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)1, (byte)2, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if((ret = JniNdk.JNI_Sec_GetKcv((byte)2, (byte)201, kcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			
			if((ret = JniNdk.JNI_Sec_GetDukptKsn((byte)5, ksn))!=NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%d)",Tools.getLineInfo(),bak-cnt,TESTITEM,ret);
				continue;
			}
			if(Tools.memcmp(ksn, ISOUtils.hex2byte("FFFFFFFFFFFFFFE00001"), 10)==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(ksn=%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,ISOUtils.hexString(ksn));
				continue;
			}
			
			// case10.2：使用mpos方式检测[共享密钥去]主密钥、工作密钥应能检测到（ID=201,56）
			byte[] kcv_TPK_201 = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("0201C90000000002"));
			byte[] kcv_TAK_56 = CalDataLrc.mposPack(new byte[]{0x1A,0x05}, ISOUtils.hex2byte("0302380000000002"));
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TPK_201), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			retContent = k21ControllerManager.sendCmd(new K21DeviceCommand(kcv_TAK_56), null).getResponse();
			if((retCode = ISOUtils.dumpString(retContent, 9, 2)).equals("00")==false)
			{
				gui.cls_show_msg1_record(TAG, "mpos_ndk", g_keeptime, "line %d:第%d次:%s测试失败(%s)",Tools.getLineInfo(),bak-cnt,TESTITEM,retCode);
				continue;
			}
			
			succ++;
		}
		// 测试后置，擦除密钥，重置TLK
		byte[] pszOwner=new byte[100];
		JniNdk.JNI_Sec_GetKeyOwner(pszOwner.length, pszOwner);
		//设置成共享密钥 擦除密钥后会自动装TLK
		if( !ISOUtils.byteToStr(pszOwner).equals("*")){
			JniNdk.JNI_Sec_LoadKey((byte)0, (byte)EM_SEC_KEY_TYPE.SEC_KEY_TYPE_TLK.ordinal(), (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), new SecKcvInfo());
		}
		gui.cls_show_msg1_record(TAG, "mpos_ndk", g_time_0,"%s交叉测试完成,已执行次数为%d,成功为%d次",TESTITEM, bak-cnt,succ);
	}
	
}
