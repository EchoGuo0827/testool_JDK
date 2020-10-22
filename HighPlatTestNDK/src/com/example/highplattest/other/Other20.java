package com.example.highplattest.other;

import android.content.Intent;
import android.net.Uri;
import android.newland.content.NlIntent;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module           : 其他
 * file name        : Other20.java 
 * Author           : weimj
 * version          : 
 * DATE             : 20190814
 * directory        : 
 * description      : SystemAPP标记的应用禁止卸载测试(适用于X5设备)
 * related document :
 * history          : author            date            remarks
 *                    weimj        	  20190814        	created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 * @param <Method>
 ************************************************************************/
public class Other20 extends UnitFragment
{
	private final String TESTITEM = "SystemAPP标记的应用禁止卸载测试";
	private String fileName=Other20.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	
	public void other20()
	{
		//前置配置
		gui.cls_show_msg("请先将测试apk'wxpay-release_1.18.300.52_legu_signed_zipalign.apk'和'merchant-app'使用下载工具安装到设备,完成后任意键继续");
		String systemPackage ="com.tencent.wxpayface";//要卸载的软件包名
		String funcName = "other20";
		
		//case1:系统弹窗方式（显示方式）卸载软件
		gui.cls_show_msg1(1, "case1:系统弹窗卸载软件");
		Uri uri = Uri.fromParts("package", systemPackage, null);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		myactivity.startActivity(intent);
		
		if(gui.ShowMessageBox("是否弹出卸载提示弹框,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:测试失败,不应出现卸载提示弹框", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置,在应用选项中查看'是否已经卸载,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:应用被卸载,测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case2:静默方式（隐性方式）卸载软件
		gui.cls_show_msg1(1, "case2:静默卸载软件");
		Uri uri1 = Uri.parse("package:"+systemPackage);
		Intent intentDel1 = new Intent(NlIntent.ACTION_DELETE_HIDE,uri1);
		intentDel1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		myactivity.startActivity(intentDel1);
		
		if(gui.ShowMessageBox("是否弹出卸载提示弹框,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:测试失败,不应出现卸载提示弹框", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}else if(gui.ShowMessageBox("进入设置,在应用选项中查看'是否已经卸载,是[确定]否[取消]'".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK){
			gui.cls_show_msg1_record(fileName,funcName,gKeepTimeErr,"line %d:应用被卸载，测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		
		//case3:尝试手动卸载
		if (gui.cls_show_msg("请手动卸载wxpayface.apk，是否无法卸载，是[确认]，其他[取消]") != ENTER) 
		{
			gui.cls_show_msg1_record(fileName, "Other20", gKeepTimeErr,"line %d:手动卸载成功，预期失败",Tools.getLineInfo());
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime,"%s测试通过",TESTITEM);
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
