package com.example.highplattest.systest;

import com.example.highplattest.fragment.BaseFragment;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import com.newland.ndk.SecKcvInfo;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest96.java 
 * description 		: 重启-安装密钥测试
 * related document :
 * history 		 	: 变更人员			变更时间			变更记录
 * 					 zhengxq		20200810                      创建
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest96 extends DefaultFragment{
	private final String TAG = SysTest96.class.getSimpleName();
	private final String TESTITEM = "重启-安装密钥压力测试";
	private final String TRANSMIT_KEY = "31313131313131313131313131313131";// 传输密钥
	
	public void systest96()
	{
		Gui gui = new Gui(myactivity, handler);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n0.设置K21端的休眠时间\n1.获取K21端的休眠时间\n2.压力测试\n",TESTITEM);
			switch (nkeyIn) {
			case '0':
				int k21SleepTime = gui.JDK_ReadData(30, 30, "请输入K21休眠的间隔时间,重启后生效");
				BaseFragment.setProperty("persist.sys.nl_suspend", k21SleepTime+"");
				if(gui.cls_show_msg("K21时间要重启后生效,是否立即重启,是[确认],否[其他]")==ENTER)
					Tools.reboot(myactivity);
				break;
				
			case '1':
				String realK21Time = BaseFragment.getProperty("persist.sys.nl_suspend", "-1");
				gui.cls_show_msg("K21端的休眠时间=%s,任意键继续", realK21Time);
				break;
				
			case '2':
				gui.cls_show_msg("测试APK放置在/SVN/Tool/部分案例APK/HighPlatTestNDK_重启压测.apk,安装APK完毕后打开APK,手动重启后就会开始自动重启测试");
//				preTest(gui);
				break;

			default:
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	private void preTest(Gui gui)
	{
		int testCount=0;
		int singleTime = gui.JDK_ReadData(30, 31, "请输入测试间隔时间");
		SecKcvInfo kcvInfo = new SecKcvInfo();
		while(true)
		{
			gui.cls_printf(String.format("正在进行第%d次%s压力测试", testCount+1,TESTITEM).getBytes());
			LoggerUtil.e("rebootTime=" + singleTime);
			try {
				Thread.sleep(singleTime * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LoggerUtil.e("enter loadKey");
			// 安装TLK和TMK
			int iRet = -1;
			if ((iRet = JniNdk.JNI_Sec_LoadKey((byte) 0,(byte) (0 | ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg()), (byte) 0, (byte) 1, 16,
							ISOUtils.hex2byte(TRANSMIT_KEY), kcvInfo)) != 0) {
				gui.cls_show_msg1_record(TAG, "systest96", 0, "line %d:%d安装TLK密钥失败(%d)", Tools.getLineInfo(),ParaEnum.EM_SEC_KEY_ALG.SEC_KEY_DES.seckeyalg(), iRet);
				return;
			}
			testCount++;
		}
	}

}
