package com.example.highplattest.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.annotation.SuppressLint;
import android.util.Log;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other14.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20190102
 * directory 		: 
 * description 		: 巴西固件白名单功能测试
 * related document :
 * history 		 	: author			date			remarks
 *			  	      zsh		   	20190102 			created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class Other14 extends UnitFragment {
	private final String TESTITEM = "巴西固件白名单功能";
	public final String TAG = Other14.class.getSimpleName();	
	private Gui gui = new Gui(myactivity, handler);
	private String hostname=null;
	private String ip=null;
	private String ipv6=null;
	public String result=null;
	public boolean message=false;
	String result1=null;
	String result2=null;
	boolean success=false;
	int ret=-1;
	public void other14()
	{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, "other14", gScreenTime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		gui.cls_show_msg("该用例需要在巴西固件运行,首次进入该用例,使用ctrl+shift+c取消AndroidManifest.xml文件application标签下'仅百度在白名单内'的meta-data注释,重新安装apk,pos联网,进入子案例1" );
		//测试主入口
		while(true){
			int returnValue=gui.cls_show_msg("1.验证ip格式立即生效,域名格式联网生效,验证网络状态变化时重设规则\n2.验证apk升级时重设白名单,apk卸载后删除白名单规则");
			switch(returnValue){
			case '1':
				//第一次运行,验证ip格式立即生效,域名格式联网生效,验证网络状态变化时重设规则(该固件不支持adb,断网下的验证暂时无法实现,先注释掉)
//				cmd("iptables -vn -L");//这里Android端不支持此命令 ,先注释掉
//				gui.cls_show_msg("结果为"+message);
//				if(success==false);
//				{
//					gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，断网情况下白名单限制未生效", Tools.getLineInfo(), TESTITEM);
//					if(!GlobalVariable.isContinue)
//						return;	
//				}
//				if(gui.cls_show_msg("请通过pc端工具进入adb shell,在iptables -vn -L的fw_OUTPUT标签下查看取消注释的ip是否生效,[确认]是，[其他]否" )!=ENTER)
//				{
//					 gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，断网情况下白名单限制未生效", Tools.getLineInfo(), TESTITEM);
//						if(!GlobalVariable.isContinue)
//							return;	
//				}
				gui.cls_show_msg("请先将pos联网,然后任意键继续,等待ping结果..." );
				pingtest();
				if(result1!="success"||result2!="fail")
				{
					gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，ping baidu =%s,ping sogou=%s", Tools.getLineInfo(), TESTITEM,result1,result2);
					if(!GlobalVariable.isContinue)
						return;
				}
				gui.cls_show_msg("子案例1测试结束,请恢复application标签下'仅百度在白名单内'的meta-data注释,并取消'仅搜狗在白名单内'的meta-data注释,在第4、5行的当前版本号的基础上修改为增加0.1(如1.0改为1.1),重新安装apk,进入子案例2" );
				break;
			case '2':
				//第二次运行,验证apk升级时重设白名单,apk卸载后删除白名单规则
				gui.cls_show_msg("该子案例需在执行子案例1后执行,任意键继续...");
				pingtest();
				if(result1!="fail"||result2!="success")
				{
					gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，ping baidu =%s,ping sogou=%s", Tools.getLineInfo(), TESTITEM,result1,result2);
					if(!GlobalVariable.isContinue)
						return;	
				}
				gui.cls_show_msg("子案例测试结束,请卸载apk,请通过pc端工具进入adb shell,在iptables -vn -L的fw_OUTPUT标签下查看ip限制是否存在,若不存在则本案例通过,否则不通过,再次使用测试工具,请恢复AndroidManifest.xml文件为初始状态" );
				break;
			}
		}
	}
	
	public boolean cmd(String cmd) 
	{
		try{
			Process pro=Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));//缓存流
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) //一次读一行 直到读完
			 {
				Log.e(TAG,line);//将adb的返回输出到日志
				stringBuffer.append(line+"\n");
				if(line.contains("fw_OUTPUT"))
				{
					message=true;
					break;
				}
				
			 }
			in.close();//关闭流
		}catch(IOException e){
			e.printStackTrace();
		}
		return message;
	}
	//ping百度和搜狗的设置
	public void pingtest() 
	{
		//ping百度,结果为result1
		String name="百度";
		hostname="www.baidu.com";
		ip="14.215.177.39";
		ipv6="0:0:0:0:0:FFFF:0ED7:B127";
		ping(name,hostname,ip,ipv6);
		result1=result;
		//ping搜狗,结果为result2
		name="搜狗";
		hostname="www.sogou.com";
		ip="183.36.114.44";
		ipv6="0:0:0:0:0:FFFF:B724:722C";
		ping(name,hostname,ip,ipv6);
		result2=result;
	}
	//ping的具体实现
	public void ping(String name,String hostname,String ip,String ipv6) {
			try{
				Process p1=Runtime.getRuntime().exec("ping -c 3 "+hostname);
				int status_hostname=p1.waitFor();
				//gui.cls_show_msg("ping hostname="+status_hostname);
				Process p2=Runtime.getRuntime().exec("ping -c 3 "+ip);
				int status_ipv4=p2.waitFor();
				//gui.cls_show_msg("ping status_ipv4="+status_ipv4);
				Process p3=Runtime.getRuntime().exec("ping -c 3 "+ipv6);
				int status_ipv6=p3.waitFor();
				//gui.cls_show_msg("ping status_ipv6="+status_ipv6);
				//联网时,白名单内网址的ping得到的result应为success,白名单外网址为fail,其他情况为异常
				if(status_hostname==0&&status_ipv4==0&&status_ipv6==0)
				{
					result="sucess";
					//gui.cls_show_msg("域名:"+hostname+"和ip地址:"+ip+"均ping成功");	
				}
				else if(status_hostname!=0&&status_ipv4!=0&&status_ipv6!=0)
				{
					result="fail";
					//gui.cls_show_msg("域名:"+hostname+"和ip地址:"+ip+"均ping失败");
				}else{
					result="error";
					//gui.cls_show_msg("同个网址的ip或域名单个ping异常");	
				}
				//异常时,将异常ping失败输出.
				if(result=="error")
				{
					if((ret=status_hostname)!=0)
					{
						gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，ping%s的域名失败,ret=%d", Tools.getLineInfo(), TESTITEM,name,status_hostname);
					}
					if((ret=status_ipv4)!=0)
					{
						gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，ping%s的Ipv4失败,ret=%d", Tools.getLineInfo(), TESTITEM,name,status_ipv4);
					}if((ret=status_ipv6)!=0)
					{
						gui.cls_show_msg1_record(TAG, "Other14", gKeepTimeErr,"line %d:%s测试不通过，ping%s的Ipv6失败,ret=%d", Tools.getLineInfo(), TESTITEM,name,status_ipv6);
					}
				}
			}
		catch(IOException e)
		{
			result="IOException";
		}
		catch(InterruptedException e)
		{
			result="InterruptedException";
		}

	
	}
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() 
	{
		gui = null;
	}
}
