package com.example.highplattest.mpos;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;

/************************************************************************
 * 
 * module 			: 安全模块
 * file name 		: sec33
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190201
 * directory 		: 
 * description 		: 验证共享密钥表位置
 * 					: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		 zhengxq		  20180201		     create
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos21 extends UnitFragment
{
	private Gui gui = new Gui(myactivity, handler);
	public final String FILE_NAME = Mpos21.class.getSimpleName();
	private final String TESTITEM = "验证共享密钥表位置(mpos)";
	private SharedPreferences mSharedPreferences;
	private SharedPreferences.Editor mEditor;
	
	public void mpos21()
	{
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.SupportMpos)==false)
		{
			gui.cls_show_msg1(1, "%s,%s该平台不支持mpos案例",FILE_NAME,GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(FILE_NAME, "sec34", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		mSharedPreferences = myactivity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		mEditor = mSharedPreferences.edit();
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n1.K21端存在密钥测试\n2.K21端不存在密钥测试\n", TESTITEM);
			switch (nkeyIn) {
			case '1':
				k21_key_exist();
				break;
				
			case '2':
				k21_key_noexist();
				break;
				
			case ESC:
				unitEnd();
				return;

			default:
				break;
			}
		}
	}
	
	/**密钥位置设置在K21端,安装除了TLK ID1之外的密钥，之后将密钥位置设置为Android端,然后重启查看密钥位置应该K21端*/
	private void k21_key_exist()
	{
		int ret = -1;
		int preCnt = gui.JDK_ReadData(30, 30, "设置重启压力测试次数");
		// 设置压力次数存放到share中，用于重启测试
		mEditor.putInt("sec_test", preCnt);
		mEditor.commit();
		mEditor.putInt("sec_cnt", preCnt);
		mEditor.commit();
		mEditor.putString("owner", "*");
		mEditor.commit();
		mEditor.putBoolean("sec34", true);
		mEditor.commit();
		mEditor.putInt("flag", 0);
		mEditor.commit();
		
		gui.cls_printf("密钥存在在K21端测试".getBytes());
		byte[] pszOwner = "*".getBytes();
		if((ret = JniNdk.JNI_Sec_SetMposKeyOwner(pszOwner, pszOwner.length, (byte) 0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_exist",gKeepTimeErr,"line %d:%s测试失败(ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		// 随机安装工作密钥以及密钥索引
		int count = (int) (Math.random()*10);
		for(int i=0;i<count;i++)
		{
			byte[] keyTempData = new byte[16];
			for(int j=0;i<16;i++)// 因海外固件的各个密钥索引的密钥值要不一样，故安装这边全部采用随机的方式
				keyTempData[j] = (byte) (Math.random()*255);
			SecKcvInfo secKcvInfo = new SecKcvInfo();
			byte keyIndex = (byte) (Math.random()*255);
			byte keyType = (byte) (Math.random()*3+2);
			if((ret=JniNdk.JNI_Sec_LoadKey((byte)0, keyType, (byte)0, keyIndex, 16, keyTempData, secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_exist",gKeepTimeErr,"line %d:%s测试失败(keyType=%d,ret=%d)",Tools.getLineInfo(),TESTITEM,keyType,ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		gui.cls_show_msg("点击任意键设备即将重启");
		Tools.reboot(myactivity);
	}
	
	/**密钥位置设置在K21端,删除设置在K21端的密钥(或K21端只存在TLK ID1密钥),之后将密钥位置设为K21端，重启查看密钥位置在Android端*/
	private void k21_key_noexist()
	{
		int ret = -1;
		int nkeyIn = gui.cls_show_msg("1.K21端只存在TLK\n2.K21端不存在任何密钥\n");
		if(nkeyIn!='1'&&nkeyIn!='2')
			return;
		
		// 设置重启压力次数
		int preCnt = gui.JDK_ReadData(30, 30, "设置重启压力测试次数");
		// 设置压力次数存放到share中，用于重启测试
		mEditor.putInt("sec_test", preCnt);
		mEditor.commit();
		mEditor.putInt("sec_cnt", preCnt);
		mEditor.commit();
		mEditor.putString("owner", "Android_Share_Key_Table");
		mEditor.commit();
		mEditor.putInt("flag", 1);
		mEditor.commit();
		mEditor.putBoolean("sec34", true);
		mEditor.commit();
		
		byte[] pszOwner = "*".getBytes();
		if((ret = JniNdk.JNI_Sec_SetMposKeyOwner(pszOwner, pszOwner.length, (byte)0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_noexist",gKeepTimeErr,"line %d:%s测试失败(ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		if((ret = JniNdk.JNI_Sec_KeyErase())!=NDK_OK)
		{
			gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_noexist",gKeepTimeErr,"line %d:删除密钥失败(ret=%d)",Tools.getLineInfo(),ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		SecKcvInfo secKcvInfo = new SecKcvInfo();
		if(nkeyIn=='1')
		{
			if((ret = JniNdk.JNI_Sec_LoadKey((byte)0, (byte)0, (byte)0, (byte)1, 16, ISOUtils.hex2byte("31313131313131313131313131313131"), secKcvInfo))!=NDK_OK)
			{
				gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_noexist",gKeepTimeErr,"line %d:安装TLK失败(ret=%d)",Tools.getLineInfo(),ret);
				if(GlobalVariable.isContinue == false)
					return;
			}
		}
		if((ret = JniNdk.JNI_Sec_SetMposKeyOwner(pszOwner, pszOwner.length, (byte)0))!=NDK_OK)
		{
			gui.cls_show_msg1_record(RED_COLOR,FILE_NAME,"k21_key_noexist",gKeepTimeErr,"line %d:%s测试失败(ret=%d)",Tools.getLineInfo(),TESTITEM,ret);
			if(GlobalVariable.isContinue == false)
				return;
		}
		gui.cls_show_msg("点击任意键即将重启");
		Tools.reboot(myactivity);
	}
	
	@Override
	public void onTestUp() 
	{
		
	}

	@Override
	public void onTestDown() 
	{
		
	}

}
