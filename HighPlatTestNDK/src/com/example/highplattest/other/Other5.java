package com.example.highplattest.other;

import android.newland.BootProvider;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other5.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180730
 * directory 		: 
 * description 		: setCustomBootLogo、setCustomBootAnimation、RemoveCustomBootAnimation
 * related document :
 * history 		 	: 变更记录						变更时间			变更人员
 *			  	                 创建		   				20180730	 		郑薛晴
 *					N700_巴西导入(V2.3.05)			20200623		郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other5 extends UnitFragment
{
	private final String TESTITEM = "开机动画与logo";
	public final String TAG = Other5.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	/**之前的路径放在/mnt/sdcard/,坤坤说sdcard的路径权限不够,要放到/data/share/下,长威反馈/data/share/boot/下也没有权限，只有/data/share/有权限*/
	private final String LOGO_NOSIGN = "/data/share/splash_unsign.img";
//	private final String LOGO_SIGN = "/data/share/splash_sign.dls";
	private final String LOGO_SIGN = "/data/share/splash_sign.img";
	private final String ANIMATION_NOSIGN = "/data/share/animation_unsign.zip";
	private final String ANIMATION_SIGN = "/data/share/animation_sign.zip";
	
	public void other5()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other5", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		/* private & local definition */
		boolean iRet1=false;
		BootProvider bootProvider = new BootProvider(myactivity);
		
		gui.cls_printf((TESTITEM+"测试中...").getBytes());
		gui.cls_show_msg("【测试旧验签使用旧验签测试开机Logo和动画,测试新验签使用新验签开机Logo和动画】请确保/data/share目录下已放置测试文件且更新了服务器上的配套签名证书,按任意键继续");
		//case1.1:参数异常测试:"",null,不存在的以及非法格式（开机动画）的开机Logo，应设置失败
		if((iRet1 = bootProvider.SetCustomBootLogo(""))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootLogo(null))==true)	
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootLogo("/data/share/unExist"))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootLogo(ANIMATION_SIGN))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case1.2:设置不带验签的开机Logo，应设置失败
		if((iRet1 = bootProvider.SetCustomBootLogo(LOGO_NOSIGN))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case1.3:设置带验签的开机Logo，应设置成功
		if((iRet1 = bootProvider.SetCustomBootLogo(LOGO_SIGN))!=true)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(1)开机logo设置完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		// case1.4:重复设置验签的开机Logo，应设置成功
		if((iRet1 = bootProvider.SetCustomBootLogo(LOGO_SIGN))!=true)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:setCustomBootLogo测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(2)重复设置开机logo设置完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		// case2.1:参数异常测试：不存在的以及非法格式（开机Logo）开机动画，应设置失败
		if((iRet1 = bootProvider.SetCustomBootAnimation(""))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootAnimation(null))==true)	
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootAnimation("/data/share/unExist"))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((iRet1 = bootProvider.SetCustomBootAnimation(LOGO_SIGN))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		// case2.2:设置不带验签的开启动画，应设置失败
		if((iRet1 = bootProvider.SetCustomBootAnimation(ANIMATION_NOSIGN))!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		// case2.3:设置带验签的开机动画，应设置成功
		if((iRet1 = bootProvider.SetCustomBootAnimation(ANIMATION_SIGN))!=true)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(1)开机动画设置完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		
		// case2.4:重复设置带验签的开机动画，应设置成功
		if((iRet1 = bootProvider.SetCustomBootAnimation(ANIMATION_SIGN))!=true)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:SetCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(2)重复开机动画设置完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}

		// case3.1:移除存在的开机动画，应移除成功
		if((iRet1 = bootProvider.RemoveCustomBootAnimation())!=true)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:RemoveCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(1)移除开机动画完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		// case3.2:再次移除开机动画，应返回false
		if((iRet1 = bootProvider.RemoveCustomBootAnimation())!=false)
		{
			gui.cls_show_msg1_record(TAG, "other5", gKeepTimeErr, "line %d:RemoveCustomBootAnimation测试失败(%s)", Tools.getLineInfo(),iRet1);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("(2)重复移除开机动画完毕,重启后生效,是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
		gui.cls_show_msg1_record(TAG, "other5", gScreenTime,"%s测试通过",TESTITEM);
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
