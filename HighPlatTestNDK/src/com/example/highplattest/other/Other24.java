package com.example.highplattest.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other24.java 
 * history 		 	: 变更记录															变更时间			变更人员
 *			  	    IP网址统计优化：不接收来自本机外的其他Client，添加头部字段 						20200916  		陈丁
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other24 extends UnitFragment {
	public final String TAG = Other24.class.getSimpleName();
	private final String TESTITEM = "51222端口优化验证(CPOSX5)";
	Gui gui = new Gui(myactivity, handler);
	private WifiPara wifiPara = new WifiPara();
	Socket socket;
	String text="qwerASDF2323@#$%789";
	String ip="null";
	public void other24(){
		gui.cls_show_msg("其他机器与X5进行验证则预期无法连接51222端口(请确保同一网段)。X5本机进行验证则预期可以连接51222端口，但是发送字符串应无法被统计服务记录。按任意键继续");
		
		new Config(myactivity,handler).set_wifi_excelsheet(wifiPara);
		ip=wifiPara.getother24wifi();
		gui.cls_show_msg1(5, "开始建立socket连接。当前连接ip:%s",ip);
		try {
			socket=new Socket(ip, 51222);
			InputStream is = socket.getInputStream();
	        InputStreamReader isr = new InputStreamReader(is, "GBK");
	        // 2.获取客户端输出流
	        OutputStream dos = socket.getOutputStream();
	        dos.write(text.getBytes());
	        dos.flush();
	        Log.d("eric_chen", "成功向服务器发送消息");
	        gui.cls_show_msg1(2, "成功向服务器发送消息");
	        // 4.获取输入流，并读取服务器端的响应信息
//	        BufferedReader br = new BufferedReader(isr);
//	        String returnInfo = br.readLine();
//	        Log.d("eric_chen", "服务器端返回数据为：" + returnInfo);
//	        gui.cls_show_msg1(2, "服务器端返回数据为：" + returnInfo);
	        // 4.关闭资源
//	        br.close();
	        isr.close();
	        is.close();
	        dos.close();
	        socket.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "other24", 2,e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			gui.cls_show_msg1_record(TAG, "other24", 2,"IO异常。。。");
		}
		gui.cls_show_msg1_record(TAG, "other24", 0,"51222端口优化验证(CPOSX5)，测试通过,请验证统计服务的ip统计");

		
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
