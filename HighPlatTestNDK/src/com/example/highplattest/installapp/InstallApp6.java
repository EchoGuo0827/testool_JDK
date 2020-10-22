package com.example.highplattest.installapp;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module           : 其他
 * file name        : Other17.java 
 * Author           : zsh
 * version          : 
 * DATE             : 20190403
 * directory        : 
 * description      : 反射方式卸载apk
 * related document :
 * history          : author            date            remarks
 *                    zsh        	20190403        	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 * @param <Method>
 ************************************************************************/
public class InstallApp6 extends UnitFragment
{
	private final String TESTITEM = "反射方式安装和卸载apk";
	private String fileName="InstallApp6";
	private Gui gui = new Gui(myactivity, handler);
//	private UserHandle mUser;
	public void installapp6()
	{	
		//前置配置
		gui.cls_show_msg("即将进行反射方式安装和卸载apk测试,请先将测试apk'YINSHANG_test1.apk'使用adb导入到/storage/emulated/0/Download路径下,完成后任意键继续");
		String path="storage/emulated/0/Download/YINSHANG_test1.apk";
		String ApkPackage ="com.ums.upos.uapi";//银商U架构的包名
		
		//反射方式安装apk
		installAPK(path);
		if(gui.ShowMessageBox("等待片刻,进入设置,在应用选项中查看'银商U架构是否安装,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:反射方式安装apk失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}else{
			gui.cls_show_msg("反射方式安装成功,即将进行反射方式卸载测试,任意键继续");
		}
		
		//反射方式卸载apk
		deleteAPK(ApkPackage);
		if(gui.ShowMessageBox("是否弹出卸载提示弹框,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:测试失败,未出现要求的弹框提示", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置,在应用选项中查看'银商U架构是否已经卸载,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK){
			gui.cls_show_msg1_record(fileName,"other18",gKeepTimeErr,"line %d:反射方式卸载apk失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}else
		{
			gui.cls_show_msg1_record(fileName,"other18",gScreenTime,"%s测试通过(长按确认键退出测试)", TESTITEM);
		}		
	}
	
	public void installAPK(String path){
		 try {
			 PackageManager mPm=myactivity.getPackageManager();
             Method installPackageAsUser = PackageManager.class.getMethod("installPackage", Uri.class, IPackageInstallObserver.class, int.class,String.class);
             android.os.Process.myUserHandle();
             File apkFile = new File(path);
             installPackageAsUser.invoke(mPm, Uri.fromFile(apkFile), null,0x00000040,"com.example.highplattest");//com.sharemore.nfc.UIDReader
         } catch ( NoSuchMethodException|IllegalAccessException|InvocationTargetException e ) {
             e.printStackTrace();
         }
	}
	
	public void deleteAPK(String ApkPackage){
		
		 try {
			PackageManager mPm=myactivity.getPackageManager();
			Method deletePackageAsUser= (Method) PackageManager.class.getMethod("deletePackageAsUser", String.class, IPackageDeleteObserver.class, int.class,int.class);
			PackageDeleteObserver observer=new PackageDeleteObserver();
//			mUser = android.os.Process.myUserHandle();
			deletePackageAsUser.invoke(mPm,ApkPackage,observer, 0,0);
		} catch (NoSuchMethodException |IllegalAccessException|InvocationTargetException e) {
			e.printStackTrace();
		}
	} 
	class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        public void packageDeleted(String packageName, int returnCode) {
        }
    }

	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
}
