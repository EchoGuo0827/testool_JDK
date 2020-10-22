package com.example.highplattest.systemconfig;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.newland.SettingsManager;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.example.highplattest.R;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android系统的开机启动项
 * file name 		: SystemConfig6.java 
 * Author 			: huangjianb
 * version 			: 
 * DATE 			: 20141024
 * directory 		: 
 * description 		: 测试setLaucher，是否能够设置开机启动项
 * related document : 
 * history 		 	: author			date			remarks
 * 					 huangjianb		   20141024	 		created	
* history 		 	: 变更点										    				变更人员				变更时间
 * 					修改提示语，新增在onCreate就崩溃的laucher，确保可以在3s内崩溃，启动mtmslaucher  	陈丁			     20200601
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemConfig6 extends UnitFragment 
{
	private final String TESTITEM = "开机Launcher启动规则";
	private String fileName="SystemConfig6";
	String title;
	String headName = GlobalVariable.sdPath+"/launcher/";
	// 异常Launcher机制已经被刘坤坤去除 modify by zhengxq 20171018
	String[] appName = {headName+"OrdinaryLauncher.apk",headName+"MTMSLauncher.apk",
			headName+"DelayLauncher.apk",headName/*+"ExceptionLauncher.apk"*/};
	String[] packageName = {"com.newland.launcher.ordinary","com.newland.launcher.mtms"
			,"com.newland.launcher.delay"/*,"com.newland.launcher.exception"*/};
	String[][] appNameMul = {
			{headName+"OrdinaryLauncher.apk",headName+"OrdinaryLauncher1.apk"},
			{headName+"OrdinaryLauncher.apk",headName+"MTMSLauncher.apk"},
			{headName+"OrdinaryLauncher.apk",headName+"DelayLauncher.apk"},
			{headName+"OrdinaryLauncher.apk",headName+"ExceptionLauncher.apk"},
			{headName+"MTMSLauncher.apk",headName+"MTMSLauncher1.apk"},
			{headName+"MTMSLauncher.apk",headName+"DelayLauncher.apk"},
			{headName+"MTMSLauncher.apk",headName+"ExceptionLauncher.apk"},
			{headName+"DelayLauncher.apk",headName+"DelayLauncher1.apk"},
			{headName+"DelayLauncher.apk",headName+"ExceptionLaucher.apk"}
			/*{headName+"ExceptionLauncher.apk",headName+"ExceptionLauncher1.apk"}*/};
	String[] appCorr = {"普通","普通","普通","普通","MTMS","MTMS","MTMS","延迟","延迟"/*,"异常恢复"*/};
	String[] defineLauncher ={"com.newland.launcher.ordinary","com.newland.launcher.mtms"
			,"com.newland.launcher.delay","com.newland.launcher.exception","com.newland.launcher.ordinary1",
			"com.newland.launcher.mtms1","com.newland.launcher.delay1"/*,"com.newland.launcher.exception1"*/};
	private Gui gui = new Gui(myactivity, handler);
	private HashSet<String> checkName = new HashSet<String>();
	private StringBuffer strBuffer = new StringBuffer();
	
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) {
			case 0:
				showDialog();
				break;

			default:
				break;
			}
		};
	};
	public void systemconfig6() 
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gScreenTime, "%s用例不支持自动化测试，请手动验证",TESTITEM);
			return;
		}
		/* private & local definition */
		boolean ret = false;
		
		SettingsManager settingsManager;
		title = "launcher选择";
		try 
		{
			settingsManager = (SettingsManager) myactivity.getSystemService(SETTINGS_MANAGER_SERVICE);
		} catch (NoClassDefFoundError e) 
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gScreenTime, "line %d:找不到该类，抛出异常（%s）",Tools.getLineInfo(),e.getMessage());
			return;
		}
				
		/* process body */
		// 测试手机中存在的每个laucher，是否能够设置为特定的launcher
		gui.cls_show_msg1(2, "%s测试中...", TESTITEM);
		
		// 测试前置，只对K21的Apk进行签名验证
		if(GlobalVariable.gCurPlatVer==Platform_Ver.A9||GlobalVariable.gCurPlatVer==Platform_Ver.A7) {
			gui.cls_show_msg1(1,"9.0平台不支持setAllApkVerifyDisable接口");
		}else {
			if((ret = settingsManager.setAllApkVerifyDisable()) == false)
			{
				gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败（%s）", Tools.getLineInfo(),TESTITEM,ret);
				if(!GlobalVariable.isContinue)
					return;
			}
		}

		// 卸载所有的自定义launcher
		gui.cls_show_msg("请手动卸载所有自定义的Launcher，卸载完成点任意键");
	
		gui.cls_show_msg("请确保将服务器的launcher放置于"+GlobalVariable.sdPath+"launcher/目录下，完成点击任意键");
		// case1:当只存在原生Launcher，应启动原生Launcher
		gui.cls_show_msg1(gScreenTime, "正在进行一个原生Launcher测试，请确保只安装了原生Launcher");
		if(gui.cls_show_msg("请确保只安装了原生的Launcher,是否立即重启，重启后应显示为原生Launcher,按确认键重启")==ENTER)
			Tools.reboot(myactivity);
		
		// case2：当存在两个launcher，根据Launcher的优先级别进行选择
        // case2.1:一个原生，一个普通Launcher，开机启动普通launcher
		// case2.2：一个原生，一个MTMS Launcher，开机启动MTMS Launcher
        // case2.3:一个原生，一个延迟Launcher，开机启动延迟Laucher
        // case2.4:一个原生，一个异常恢复Launcher，开启启动异常恢复launcher
		gui.cls_show_msg1(gScreenTime, "正在进行两个Launcher测试，请选择一个Launcher，另外一个为默认的原生Launcher（共有四种组合，原生+Ordinary、原生+MTMS、原生+Delay，原生+Eception）");
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showDialog();
			}
		});
		synchronized (g_lock) {
			try {
				g_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(checkName.size()!=1)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr,"line %d:选择的Launcher个数不等于1个，测试人员选择错误", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		if(gui.cls_show_msg("请确保已安装过 原生Launcher和"+checkName.toArray()[0]+",是否立即重启，重启后应显示为"+checkName.toArray()[0]+"按确认键重启")==ENTER)
			Tools.reboot(myactivity);
		
		// case3:系统存在三个Launcher
        // case3.1：一个原生laucher，两个普通Launcher，启动普通Launcher对话框
        // case3.2:一个原生launcher，一个普通launcher，一个MTMS，开机启动普通launcher
        // case3.3:一个原生launcher，一个普通launcher，一个延迟Launcher，开机启动普通launcher
        // case3.4：一个原生launcher，一个普通launcher，一个异常恢复launcher，开机启动普通launcher（已失效）
        // case3.5:一个原生launcher，两个MTMSLauncher，启动MTMS选择对话框
        // case3.6:一个原生launcher，一个MTMSlauncher，一个延迟Launcher，开机启动MTMSLauncher
        // case3.7:一个原生launcher，一个MTMSLauncher，一个异常恢复Launcher，开机启动MTMS Launcher（已失效）
        // case3.8:一个原生Launcher，两个延迟Launcher，启动延迟Launcher对话框
        // case3.9:一个原生Launcher，一个延迟Launcher，一个异常恢复Launcher，开机启动延迟Launcher（已失效）
		// case3.10:一个原生Launcher，两个ExceptionLauncher（已失效）
		gui.cls_show_msg1(2, "正在进行三个Launcher测试，请选择两个Launcher，第三个为默认的原生Launcher（共有6种组合，1个原生+2个Oridnary、1个原生+1个普通+1个MTMS、" +
				"1个原生+1个Ordinary+1个Delay、1个原生+2个MTMS、1个原生+1个MTMS+1个Delay、1个原生+2个Delay");
		myactivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showDialog();
			}
		});
		synchronized (g_lock) {
			try {
				g_lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(checkName.size()!=2)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr,"line %d:选择的Launcher个数不等于2个，测试人员选择错误", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;
		}
		// 优先级如何判断
		String levelName = null;
		for (int i = 0; i < checkName.size(); i++) 
		{
			strBuffer.append("  "+checkName.toArray()[i]);
		}
		if(strBuffer.toString().contains("Ordinary"))
			levelName="Ordinary Laucher";
		else if(strBuffer.toString().contains("MTMS"))
			levelName = "MTMS Launcher";
		else 
			levelName = "Delay Launcher";
//		else 
//			levelName = "Exception Launcher";
			
		if(gui.cls_show_msg("请确保已安装过 原生Launcher和"+strBuffer+",是否立即重启，重启后应显示为"+levelName+"按确认键重启")==ENTER)
			Tools.reboot(myactivity);
		
		// 取消异常Launcher后case4的情况实际已在Case3测过
       /* // case4:系统存在四个launcher，一个原生Launcher，一个延迟Launcher，一个MTMS Launcher，一个异常恢复Launcher，开机启动延迟Launcher
		gui.cls_show_msg1(2, "正在进行4个Launcher的测试");
		if(gui.ShowMessageBox("请安装MTMS、Delay、Exception、原生 Launcher，是否立即重启，重启后应为MTMS Launcher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)==BTN_OK)
			Tools.reboot(myactivity);*/
		
		// case5:系统存在四个launcher，一个原生，一个普通，一个Delay，一个MTMS，开机启动普通Launcher
		gui.cls_show_msg1(2, "正在进行四个Launcher的测试");
		if(gui.cls_show_msg("请先安装Ordinary、MTMS、Delay、原生 Launcher，是否立即重启，重启后应为Ordinary Launcher,按确认键重启")==ENTER)
			Tools.reboot(myactivity);
			
		
		// case6:构造Launcher崩溃
		// case6.1:MTMSLauncher崩溃，则启动恢复Launcher，无Exception Launcher则一直重启MTMSLauncher
		if(gui.cls_show_msg("请先安装MTMS Launcher，点击MTMS的崩溃按钮，MTMS崩溃后是否启动MTMsLauncher,按确认键重启")==ENTER)
			Tools.reboot(myactivity);
		/*// 卸载异常恢复Launcher
		if(gui.ShowMessageBox("请先卸载Exception Launcher，卸载完Exception Launcher并重启，MTMS Launcher崩溃后是否启动MTMsLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		
		// case6.2:延迟Launcher崩溃，则启动异常恢复Launcher，否则一直重启延迟Launcher
		if(gui.cls_show_msg("请先卸载MTMS Launcher并安装Delay Launcher并重启，重启后点击Delay的崩溃按钮，Delay launcher崩溃后 是否启动EDelayLauncher,按确认键重启")==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
//		if(gui.ShowMessageBox("请卸载Exception Launcher，卸载完Exception Launcher并重启，Delay Launcher崩溃后是否启动DelayLauncher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
//		// case6.3:异常恢复Launcher崩溃则一直重启原生Launcher
//		if(gui.ShowMessageBox("请卸载Delay Launcher，卸载完Delay Launcher并重启，Exception Laucnher崩溃后是否启动Exception Launcher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		gui.ShowMessageBox("请卸载Exception Launcher，任意键继续".getBytes(), (byte) 0, WAITMAXTIME);
		// case6.4:普通launcher崩溃，则启动异常恢复Launcher，若未安装异常恢复launcher，启动MTMS，否则一直重启普通Launcher
		if(gui.cls_show_msg("请安装Ericlaucher、Delay、Mtms并重启，重启后，Ericlaucher崩溃后是否启动MTMS Launcher,按确认键重启")==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
//		if(gui.ShowMessageBox("请先卸载Exception Launcher并重启，Ordinary崩溃后是否启动MTMS Launcher".getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME)!=BTN_OK)
//		{
//			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		
		if(gui.cls_show_msg("请先卸载Delay、MTMS Launcher，卸载完Delay、MTMS Launcher并重启后 ，Ericlaucher是否启动Ericlaucher Launcher(Ericlaucher崩溃属于正常现象),按确认键重启")==ENTER)
		{
			gui.cls_show_msg1_record(fileName,"systemconfig6",gKeepTimeErr, "line %d:%s测试失败", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(fileName,"systemconfig6",gScreenTime, "确保每种情况测试通过才可视为通过(长按确认键退出测试)");
	}
	
	
	/**
	 * 显示checkBox的对话框
	 */
	private void showDialog()
	{
		AlertDialog.Builder checkDialog = new AlertDialog.Builder(myactivity);
		checkDialog.setTitle("Launcher选择界面");
		LayoutInflater layout = LayoutInflater.from(myactivity);
		View view = layout.inflate(R.layout.launcher_layout, null);
		final CheckBox ordinaryBox = (CheckBox) view.findViewById(R.id.cb_ordinary_launcher);
		final CheckBox mtmsBox = (CheckBox) view.findViewById(R.id.cb_mtms_launcher);
		final CheckBox delayBox = (CheckBox) view.findViewById(R.id.cb_delay_launcher);
//		final CheckBox exceptionBox = (CheckBox) view.findViewById(R.id.cb_exception_launcher);
		
		final CheckBox ordinaryBox_1 = (CheckBox) view.findViewById(R.id.cb_ordinary_1_launcher);
		final CheckBox mtmsBox_1 = (CheckBox) view.findViewById(R.id.cb_mtms_1_launcher);
		final CheckBox delayBox_1 = (CheckBox) view.findViewById(R.id.cb_delay_1_launcher);
//		final CheckBox exceptionBox_1 = (CheckBox) view.findViewById(R.id.cb_exception_1_launcher);
		checkDialog.setView(view);
		checkDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 选择获取的check
				// 每次确定需要清除数据
				checkName.removeAll(checkName);
				if(ordinaryBox.isChecked())
					checkName.add(ordinaryBox.getText().toString());
				if(mtmsBox.isChecked())
					checkName.add(mtmsBox.getText().toString());
				if(delayBox.isChecked())
					checkName.add(delayBox.getText().toString());
//				if(exceptionBox.isChecked())
//					checkName.add(exceptionBox.getText().toString());
				
				if(ordinaryBox_1.isChecked())
					checkName.add(ordinaryBox_1.getText().toString());
				if(mtmsBox_1.isChecked())
					checkName.add(mtmsBox_1.getText().toString());
				if(delayBox_1.isChecked())
					checkName.add(delayBox_1.getText().toString());
//				if(exceptionBox_1.isChecked())
//					checkName.add(exceptionBox_1.getText().toString());
				SystemClock.sleep(100);
				synchronized (g_lock) {
					g_lock.notify();
				}//标志位的赋值移到最后并加一点延时，解决checkName.size()未及时更新导致line165行报错
			}
		});
//		nativeBox.setOnCheckedChangeListener(this);
//		mtmsBox.setOnCheckedChangeListener(this);
//		delayBox.setOnCheckedChangeListener(this);
//		exceptionBox.setOnCheckedChangeListener(this);
		checkDialog.create().show();
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}
}