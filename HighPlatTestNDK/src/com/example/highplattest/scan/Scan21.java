package com.example.highplattest.scan;
import java.util.HashMap;

import android.newland.scan.ScanUtil;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
/************************************************************************
 * 
 * module 			: 扫码模块
 * file name 		: Scan27.java 
 * Author 			: chending
 * version 			: 
 * DATE 			: 20190924
 * directory 		: N700专用扫码枪测试
 * description 		: 
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.ParaEnum.Code_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.newland.NlBluetooth.util.LogUtil;

public class Scan21 extends UnitFragment {
	private final String TESTITEM =  "音量键扫码测试(N700)";
	private String fileName=Scan21.class.getSimpleName();
	private int ret ;
	String text;
//	private Gui gui = new Gui(myactivity, handler);
	private HashMap<String, Boolean> mCameraConfig;
	private Button btnExit;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		super.onCreateView(inflater, container, savedInstanceState);
		myactivity =  (IntentActivity) myactivity;
		Gui gui = new Gui(myactivity, handler);
		View view = inflater.inflate(R.layout.n700_layouts, container, false);
		final EditText editText1= (EditText) view.findViewById(R.id.editText1);
		EditText editText2= (EditText) view.findViewById(R.id.editText2);
		EditText editText3= (EditText) view.findViewById(R.id.editText3);
		EditText editText4= (EditText) view.findViewById(R.id.editText4);
		EditText editText5= (EditText) view.findViewById(R.id.editText5);
		EditText editText6= (EditText) view.findViewById(R.id.editText6);
		EditText editText7= (EditText) view.findViewById(R.id.editText7);
		EditText editText8= (EditText) view.findViewById(R.id.editText8);
		TextView textView=(TextView) view.findViewById(R.id.info);
		btnExit = (Button) view.findViewById(R.id.exitButton);
		final TextView scaneric=(TextView)view.findViewById(R.id.scaneric);
		
		// 判断是否有导入这个功能
		String sideKey = getProperty("ro.epay.sidekey.type","100");
		LoggerUtil.d("slideKey="+sideKey);
		if(!sideKey.equals("1"))
		{
			textView.setText("该版本不支持音量键扫码,退出键退出");
		}
		//根据华燕需要增加一个密码输入框的属性为显示密码
		editText4.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		editText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			text=editText1.getText().toString();
			scaneric.setText(text);
				return false;
			}
		} );
		btnExit.setOnClickListener(new Button.OnClickListener(){ 
            public void onClick(View v) {    
            	myactivity.finish();
            }    
        });
		
		 LogUtil.d("eric------------");
		return view;
		
	}

	public void scan21()
	{

	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
