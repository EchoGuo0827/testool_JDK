package com.example.highplattest.installapp;

import java.lang.reflect.Method;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
/************************************************************************
 * 
 * module 			: 银商签名静默卸载app
 * file name 		: InstallApp13.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20190111
 * directory 		: 
 * description 		: 测试installApp
 * related document : 
 * history 		 	: author			date			remarks
 * 					 wangxy		       20190111	 		created	
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class InstallApp13 extends UnitFragment 
{
	private final String TESTITEM = "银商签名静默卸载app";
	public final String TAG = InstallApp13.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	PackageManager packageManager=null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void installapp13() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "installapp13", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		
		if(GlobalVariable.gCustomerID != CUSTOMER_ID.ChinaUms)// 只有银商的固件才支持该接口
		{
			gui.cls_show_msg1_record(TAG, "installapp13", gScreenTime, "%s固件不支持%s接口", GlobalVariable.gCustomerID,TESTITEM);
			return;
		}
		
		/* addby20190111wangxy
		 * 目前就在N850V2.2.06固件上修改了，之前的其他版本均会报错缺少系统权限
		 *之前调用包管理器的隐藏方法deletepackage来静默卸载应用， 方法需要android.Manifest.permission.DELETE_PACKAGES权限
		 */

		packageManager = myactivity.getPackageManager();
		gui.cls_show_msg("测试前置，需安装银商证书和统一签名apk，请手动安装应用市场客户端_v2.1.4_20180313.apk,安装成功后，按任意键继续");
		gui.cls_show_msg1(2, "即将静默卸载应用市场应用...");
		Class c=packageManager.getClass();
		try {
			Method m=c.getMethod("deletePackage", String.class,IPackageDeleteObserver.class,int.class);
			m.invoke(packageManager, new Object[]{"com.ums.appstore",null,0});
		} catch (Exception e) {
			e.printStackTrace();
			gui.cls_show_msg1(2, "抛出异常"+e);
			return;
		} 
		if ((gui.ShowMessageBox(("卸载结束，查看应用是否卸载成功，预期应成功卸载应用市场").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(TAG, "InstallApp13", gKeepTimeErr,"line %d:静默卸载失败", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg1_record(TAG, "InstallApp13", gKeepTimeErr,"%s测试通过", TESTITEM);
	}
/*	private class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
		private int position;
		private int mFlag;

		public PackageDeleteObserver(int index, int flag) {
			position = index;
			mFlag = flag;// 0卸载1个包，1卸载N个包 N>1
		}

		@Override
		public void packageDeleted(String arg0, int arg1)
				throws RemoteException {
			// TODO Auto-generated method stub
         //arg0是pakname，arg1是具体没了解，卸载成功后这里是1
			Log.e("wxy","###packageDeleted +++" + arg0 + "---" + arg1);
			Message msg;
			msg = mHandle.obtainMessage();
			msg.what = FLAG_DELETE_VIRUS;
			msg.arg1 = position;
			msg.arg2 = mFlag;
			msg.sendToTarget();
		}
	}*/
	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
		gui = null;
	}
	
	

}
