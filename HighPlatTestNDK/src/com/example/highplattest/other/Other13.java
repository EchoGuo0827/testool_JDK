package com.example.highplattest.other;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
/************************************************************************
 * 
 * module 			: 其他模块
 * file name 		: Other12.java 
 * Author 			: zhangxinj
 * version 			: 
 * DATE 			: 20181022
 * directory 		: 
 * description 		:删除/newland目录底下除了/factory之外的所有文件
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zhangxinj	       20181022   		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other13 extends UnitFragment{

	private final String TESTITEM = "删除/newland目录底下除了/factory之外的所有文件";
	public final String TAG = Other13.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	public void other13(){
		gui.cls_show_msg("删除时要注意不要拿扫码的机器，否则会把扫码证书删除");	
		File f=new File("/newland");
		delNotDir(f);
		gui.cls_show_msg1_record(TAG, "other13", gScreenTime,"成功%s", TESTITEM);	 
	}

	public void delNotDir(File file) {
		if (!file.exists())
			return;
		if (file.isFile() || file.list() == null) {
			file.delete();
			LoggerUtil.e("删除了xxx" + file.getName());
		} else {
			File[] files = file.listFiles();
			for (File a : files) {
				if(a.getName().equals("factory"))
					continue;
				delete(a);
			}
		}

	}
	public void delete(File file) {
		if(!file.exists()) 
			return;
		if(file.isFile() || file.list()==null) {
			file.delete();
			LoggerUtil.e("删除了"+file.getName());
		}else {
			File[] files = file.listFiles();
			for(File a:files) {
				delete(a);					
			}
			file.delete();
			LoggerUtil.e("删除了"+file.getName());
		}
	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
