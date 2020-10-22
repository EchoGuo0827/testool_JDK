package com.example.highplattest.mpos;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum.EM_SYS_HWINFO;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;
import com.newland.ndk.JniNdk;

/************************************************************************
 * 
 * module 			: sys模块
 * file name 		: K21Sys.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180815
 * directory 		: 
 * description 		: PN/SN/CSN/KSN/机器号存储方案变更
 * related document : 
 * history 		 	: 变更记录			                                                               变更时间			变更人员
 *			  		     设置SN号和设置PN号增加手动输入的案例		    20200509                      魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Mpos3 extends UnitFragment
{
	public final String TAG = Mpos3.class.getSimpleName();
	private final String TESTITEM = "PN/SN设置及删除";
	private Gui gui = new Gui(myactivity, handler);
	private final String[] snStr = {"N600001NL001001","N600001NL001009"};
	private final String[] pnStr = {"N6NL99990100","N6NL99990101"};
	byte[] ioctrl = {2};
	
	public String sn = "";
	public String pn = "";
	public String input = "";
	private IntentActivity activity;
	public volatile boolean stop = true;
	
	public void mpos3()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("%s\n1.擦除PN/SN\n2.设置PN/SN\n", TESTITEM);
			switch (nkeyIn) {
			case '1':
				delFile();
				break;
				
			case '2':
				setFile();
				break;
				
			case ESC:
				unitEnd();
				return;
			}
		}
		
	}
	
	private void setFile()
	{
		int keyIn = gui.cls_show_msg("设置\n0.SN号\n1.PN号\n");
		switch (keyIn) {
		case '0':
			int keySN = gui.cls_show_msg("设置SN号\n0.设置默认值\n1.手动设置\n");
			switch(keySN){
			case '0':
				gui.cls_printf("正在设置SN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), snStr[0]);
				gui.cls_show_msg("已设置SN为%s", snStr[0]);
				break;
			case '1':
				stop = true;
				input = null;
				activity = (IntentActivity)myactivity;
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						setConfig(activity);
					}
				});
				while(stop){
					SystemClock.sleep(10);
				}
				if(input==null)
					return;
				sn = input;
				Log.e("sn",sn+"");
				gui.cls_printf("正在设置SN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_USN.secsyshwinfo(), sn);
				gui.cls_show_msg("已设置SN为%s", sn);
				break;
			}
			break;
			
		case '1':
			
			int keyPN = gui.cls_show_msg("设置PN号\n0.设置默认值\n1.手动设置\n");
			switch(keyPN){
			case '0':
				gui.cls_printf("正在设置PN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(),pnStr[0]);
				gui.cls_show_msg("已设置PN为%s", pnStr[0]);
				break;
			
			case '1':
				stop = true;
				input = null;
				activity = (IntentActivity)myactivity;
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						setConfig(activity);
					}
				});
				while(stop){
					SystemClock.sleep(10);
				}
				if(input==null)
					return;
				pn = input;
				Log.e("pn",pn+"");
				gui.cls_printf("正在设置PN号".getBytes());
				JniNdk.JNI_SYS_SetPosInfo(EM_SYS_HWINFO.SYS_HWINFO_GET_POS_PSN.secsyshwinfo(),pn);
				gui.cls_show_msg("已设置PN为%s", pn);
				break;
			}
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 擦除PN/SN/CSN/KSN号
	 */
	private void delFile()
	{
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("擦除\n1.擦除PN和SN号\n");
			switch (nkeyIn) {
			case '1':
				gui.cls_show_msg("使用SVN的擦除PN和SN的master固件,下载擦除的master固件后要重新下载测试固件才可正常使用K21端,操作成功之后可使用自检查看PN和SN是否已擦除,任意键继续");
				break;
				
			case ESC:
				return;
				
			default:
				break;
			}
		}

	}
	
	public final void setConfig(final Activity activity) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.input_config, null);

		final EditText inputTest = (EditText) view.findViewById(R.id.pn_config);

		new BaseDialog(activity, view, "输入", "确定","取消", new OnDialogButtonClickListener() {
			
			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
				if(isPositive){
					// 静态
					Log.e(TAG, " "+inputTest.getText().toString());
					if(!inputTest.getText().toString().equals("")){
						input = inputTest.getText().toString();
					}
				}
				stop = false;
			}
		}).show();
	}
	
	@Override
	public void onTestUp() {
	}

	@Override
	public void onTestDown() {
	}

}
