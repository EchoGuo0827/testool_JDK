package com.example.highplattest.other;

import java.io.File;
import android.os.Environment;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other9.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20180529
 * directory 		: 系统获取文件路径
 * description 		: 
 * related document :
 * history 		 	: 变更记录						变更时间			变更人员
 *			  		 修复获取文件系统路径错误问题		   20200426	 		 陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other9 extends UnitFragment
{
	private final String TESTITEM = "系统获取文件路径";
	public final String TAG = Other9.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	String file=GlobalVariable.sdPath;
	public void other9()
	{	
		
		file = file.substring(0, file.length()-1);
		LoggerUtil.d("file="+file);
	
		//android9路径不一致
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A9) {
			file=getSDPath();
			LoggerUtil.d("file="+file);
		}
		String path;
		
		if (!(GlobalVariable.gCurPlatVer==Platform_Ver.A9)) {
			if(!(path=Environment.getExternalStoragePublicDirectory(null).getPath()).equals(file)){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
		}
//		if(!(path=Environment.getExternalStoragePublicDirectory(null).getPath()).equals(file)){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()).equals(file+"/DCIM")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS).getPath()).equals(file+"/Alarms")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()).equals(file+"/Documents")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()).equals(file+"/Download")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getPath()).equals(file+"/Movies")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath()).equals(file+"/Music")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getPath()).equals(file+"/Notifications")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()).equals(file+"/Pictures")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS).getPath()).equals(file+"/Podcasts")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		if(!(path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES).getPath()).equals(file+"/Ringtones")){
			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
			if(!GlobalVariable.isContinue)
				return;
		}
		

		//getExternalFilesDir方法获取的是/storage/emulated/0/Android/data/包名/files  by chending 20200422 A7 A9平台
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A5) {
			if(!(path=myactivity.getExternalFilesDir(null).getPath()).equals(file) ){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
		}else{
			if(!(path=myactivity.getExternalFilesDir(null).getPath()).equals(file+"/Android/data/com.example.highplattest/files") ){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			
		}
		if (GlobalVariable.gCurPlatVer==Platform_Ver.A5) {
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath()).equals(file+"/DCIM")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_ALARMS).getPath()).equals(file+"/Alarms")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath()).equals(file+"/Documents")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()).equals(file+"/Download")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath()).equals(file+"/Movies")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath()).equals(file+"/Music")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getPath()).equals(file+"/Notifications")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()).equals(file+"/Pictures")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getPath()).equals(file+"/Podcasts")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getPath()).equals(file+"/Ringtones")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			
			}
		}else {
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath()).equals(file+"/Android/data/com.example.highplattest/files/DCIM")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_ALARMS).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Alarms")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Documents")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Download")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Movies")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Music")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Notifications")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Pictures")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Podcasts")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			}
			if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getPath()).equals(file+"/Android/data/com.example.highplattest/files/Ringtones")){
				gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
				if(!GlobalVariable.isContinue)
					return;
			
			}
		}
		
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath()).equals(file+"/DCIM")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		
//		Log.d("eric", "file==="+file+"/DCIM");
//		Log.d("eric", "path==="+path);
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_ALARMS).getPath()).equals(file+"/Alarms")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath()).equals(file+"/Documents")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath()).equals(file+"/Download")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath()).equals(file+"/Movies")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath()).equals(file+"/Music")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS).getPath()).equals(file+"/Notifications")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()).equals(file+"/Pictures")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getPath()).equals(file+"/Podcasts")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		}
//		if(!(path=myactivity.getExternalFilesDir(Environment.DIRECTORY_RINGTONES).getPath()).equals(file+"/Ringtones")){
//			gui.cls_show_msg1_record(TAG, "other9", gKeepTimeErr, "line %d:%s获取路径失败(%s)", Tools.getLineInfo(),TESTITEM,path);
//			if(!GlobalVariable.isContinue)
//				return;
//		
//		}
		gui.cls_show_msg1_record(TAG, "other9", gScreenTime, "%s测试通过", TESTITEM);
		
		
		
		
		
	}
	
	public void test() {
		
	}
	public String getSDPath(){ 
	       File sdDir = null; 
	       boolean sdCardExist = Environment.getExternalStorageState()   
	       .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
	       if(sdCardExist)   
	       {                               
	         sdDir = Environment.getExternalStorageDirectory();//获取跟目录
	      }   
	       return sdDir.toString(); 
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
