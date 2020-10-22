package com.example.highplattest.commandtest;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.highplattest.R;
import com.example.highplattest.activity.IntentActivity;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.HandlerMsg;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * file name 		: Commandtest1.java 
 * description 		: 指令模块(N920 A7)
 * history 		 	: 变更记录									变更时间				变更人员
 *			  		  N920_A7初始导入移联客户Demo			   		20200426	 		魏美杰
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Commandtest1 extends UnitFragment{
	
	private final String TESTITEM = "截屏、杀死进程等(移联)";
	private String fileName=Commandtest1.class.getSimpleName();
	public String str = null;
	private EditText etInput;
	/*重写界面部分*/
	private TextView textView1,data_textview,cmd_textview;
	private ImageView image_back;
	private Button btn_key_0,btn_key_1,btn_key_2,btn_key_3,btn_key_4,btn_key_5,btn_key_6,
					btn_key_7,btn_key_8,btn_key_9,btn_key_esc,btn_key_back,btn_key_point,
					btn_key_on,btn_key_under,btn_key_enter2;
	public int x=0;
	public int y=0;
	boolean ret = false;
	
	@SuppressLint("HandlerLeak")
	Handler myHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what){
			case HandlerMsg.TEXTVIEW_SHOW_PUBLIC:
				textView1.setText((CharSequence) msg.obj==null?"null":(CharSequence) msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		LoggerUtil.d("继承onCreateView");
		myactivity =  (IntentActivity) getActivity();
		LoggerUtil.d("获取当前Activity");
		View view = inflater.inflate(R.layout.command1_layout, container, false);
		etInput=view.findViewById(R.id.et_input);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		data_textview = (TextView) view.findViewById(R.id.data_textview);
		cmd_textview = (TextView) view.findViewById(R.id.cmd_textview);
		image_back = (ImageView) view.findViewById(R.id.image_back);
		btn_key_0 = (Button) view.findViewById(R.id.btn_key_0);
		btn_key_1 = (Button) view.findViewById(R.id.btn_key_1);
		btn_key_2 = (Button) view.findViewById(R.id.btn_key_2);
		btn_key_3 = (Button) view.findViewById(R.id.btn_key_3);
		btn_key_4 = (Button) view.findViewById(R.id.btn_key_4);
		btn_key_5 = (Button) view.findViewById(R.id.btn_key_5);
		btn_key_6 = (Button) view.findViewById(R.id.btn_key_6);
		btn_key_7 = (Button) view.findViewById(R.id.btn_key_7);
		btn_key_8 = (Button) view.findViewById(R.id.btn_key_8);
		btn_key_9 = (Button) view.findViewById(R.id.btn_key_9);
		btn_key_esc = (Button) view.findViewById(R.id.btn_key_esc);
		btn_key_back = (Button) view.findViewById(R.id.btn_key_back);
		btn_key_point = (Button) view.findViewById(R.id.btn_key_point);
		btn_key_on = (Button) view.findViewById(R.id.btn_key_on);
		btn_key_under = (Button) view.findViewById(R.id.btn_key_under);
		btn_key_enter2 = (Button) view.findViewById(R.id.btn_key_enter2);
		layScanView = (RelativeLayout) view.findViewById(R.id.lay_scan_view);
		
		btn_key_0.setOnClickListener(listener);
		btn_key_1.setOnClickListener(listener);
		btn_key_2.setOnClickListener(listener);
		btn_key_3.setOnClickListener(listener);
		btn_key_4.setOnClickListener(listener);
		btn_key_5.setOnClickListener(listener);
		btn_key_6.setOnClickListener(listener);
		btn_key_7.setOnClickListener(listener);
		btn_key_8.setOnClickListener(listener);
		btn_key_9.setOnClickListener(listener);
		btn_key_esc.setOnClickListener(listener);
		btn_key_back.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
//				GlobalVariable.g_nkeyIn = BACKSPACE;
				int[] location = new int[2];
				btn_key_back.getLocationOnScreen(location);

		        x = location[0];
		        y = location[1];
		        Log.e("location","x:"+x+" y:"+y);
		        Toast.makeText(myactivity, "退格键点击成功", Toast.LENGTH_SHORT).show();
			}
			
		});
		btn_key_point.setOnClickListener(listener);
		btn_key_on.setOnClickListener(listener);
		btn_key_under.setOnClickListener(listener);
		btn_key_enter2.setOnClickListener(listener);
		
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	               Log.e("=input","===========actionId :"+actionId );
	                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
	                    String keytag =  etInput.getText().toString().trim();
	                    if (TextUtils.isEmpty(keytag)) {
	                        Toast.makeText(myactivity, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
	                        return false;
	                    }
	                    Log.e("=input","============keytag::"+keytag);
	                    // 搜索功能主体
	                    Toast.makeText(myactivity, "搜索"+keytag+"成功", Toast.LENGTH_SHORT).show();
	                    return true;
	                }
				return false;
			}
        });
		return view;
	}
	
	private Gui gui = new Gui(myactivity, myHandler);
	public void commandtest1(){
		gui.cls_show_msg1(2, TESTITEM+"测试中。。。");
		
		//case1:模拟点击按键
		gui.cls_show_msg("case1:模拟点击按键->请先点击退格键，获取按键坐标，完成后点击确定");
        str= "input tap "+x+" "+y;//模拟按键坐标
        Log.e("str",str);
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s模拟点击按键测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		if(gui.cls_show_msg("是否弹出提示:退格键点击成功,[是]确认,[否]取消")==ESC){
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s模拟输入字符测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case2:模拟按键输入字符
		gui.cls_show_msg1(2,"case2:模拟按键输入字符");
		str = "input keyevent 34";//f
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s模拟输入字符测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		if(gui.cls_show_msg("输入框是否出现f,[是]确认,[否]取消")==ESC){
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s模拟输入字符测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case3:模拟按键搜索确认
		gui.cls_show_msg("case3:模拟按键搜索确认->输入框输入任意字符,完成后点击确定继续");
		str = "input keyevent 84";//搜索
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s模拟按键搜索测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		str = "input keyevent 66";//再执行以下 确定
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s模拟按键确认测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		if(gui.cls_show_msg("是否弹出提示：搜索成功,[是]确认,[否]取消")==ESC){
			gui.cls_show_msg1_record(fileName, "scan1", gKeepTimeErr,"line %d:%s模拟按键搜索确认测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		
		//case4:截图
		gui.cls_show_msg1(2,"case4:截图测试");
		str = "screencap /mnt/shell/emulated/0/screen.png";
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s截图测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		if(gui.cls_show_msg("根目录下是否有screen.png的截图,[是]确认,[否]取消")==ESC){
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s截图测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		//case5:关闭进程
		gui.cls_show_msg1(2,"case5:关闭进程测试");
		String str = "am force-stop com.example.highplattest";
		if((ret = exec(str))!= true)
		{
			gui.cls_show_msg1_record(fileName, "command1", gKeepTimeErr,"line %d:%s参数异常测试失败", Tools.getLineInfo(),TESTITEM);
			return;
		}
		
		gui.cls_show_msg1(3,"进程自动关闭则测试通过。。");
	}
	
    private boolean exec(String str){
        try {
            Runtime.getRuntime().exec(str);
            Log.e("success:", "success");
            return true;
        } catch (Exception e) {
            Log.e("err:", e.toString());
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}

