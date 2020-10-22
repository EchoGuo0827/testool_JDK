package com.example.highplattest.android;

import java.math.MathContext;
import java.math.RoundingMode;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android13.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180620 
 * directory 		: 
 * description 		: 测试Android的MathContext类
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180620	 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android13 extends UnitFragment{
	public final String TAG = Android13.class.getSimpleName();
	private String TESTITEM = "MathContext数据工具类测试";
	private Gui gui = new Gui(myactivity, handler);
	private MathContext math,math2,math3,math4;
	private int precision=-1,hashCode=-1;
	public void android13(){
		
		math=new MathContext(3);//默认舍入模式为RoundingMode.HALF_UP
		math2=new MathContext("precision=4 roundingMode=HALF_UP");
		math3=new MathContext(3, RoundingMode.HALF_DOWN);
		math4=new MathContext(3, RoundingMode.HALF_UP);
		
		//case1:获取精度
		if((precision=math.getPrecision())!=3){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr,"line %d:%s的获取精度报错(%d)", Tools.getLineInfo(),TESTITEM,precision);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case2:获取hashCode
		if((hashCode=math.hashCode())!=28){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr,"line %d:%s的获取hashCode报错(%d)", Tools.getLineInfo(),TESTITEM,hashCode);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case3:获取舍入模式
		if(!(math.getRoundingMode().toString().equals("HALF_UP"))){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr, "line %d:%s的获取精度报错(%s)", Tools.getLineInfo(),TESTITEM,math.getRoundingMode().toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		if(math.equals(math2)){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr, "line %d:%s的相等比较报错(%s,%s)", Tools.getLineInfo(),TESTITEM,math.toString(),math2.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		if(math.equals(math3)){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr, "line %d:%s的相等比较报错(%s,%s)", Tools.getLineInfo(),TESTITEM,math.toString(),math3.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		if(!math.equals(math4)){
			gui.cls_show_msg1_record(TAG, "android13", gKeepTimeErr, "line %d:%s的相等比较报错(%s,%s)", Tools.getLineInfo(),TESTITEM,math.toString(),math4.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		gui.cls_show_msg1_record(TAG, "android13",gScreenTime,"%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTestDown() {
		// TODO Auto-generated method stub
		
	}

}
