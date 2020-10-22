package com.example.highplattest.systest;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.LinkStatus;
import com.example.highplattest.main.constant.ParaEnum.Sock_t;
import com.example.highplattest.main.netutils.Layer;
import com.example.highplattest.main.netutils.WifiPara;
import com.example.highplattest.main.netutils.WifiUtil;
import com.example.highplattest.main.tools.Config;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.SocketUtil;
import com.example.highplattest.main.tools.Tools;
import android.annotation.SuppressLint;
import android.util.Log;
/************************************************************************
 * 
 * module 			: SysTest综合模块
 * file name 		: SysTest85.java 
 * Author 			: zsh
 * version 			: 
 * DATE 			: 20190125
 * directory 		: 
 * description 		: 八佰伴应用问题复现(socket通信)
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  zsh		   	20190125	 		created
  *						变更记录			时间				变更人
 *					wifiutil改为单例模式	20200727        陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
@SuppressLint("NewApi")
public class SysTest86 extends DefaultFragment {
	private final String TESTITEM="八佰伴应用问题复现(socket通信)";
	private final String CLASS_NAME = SysTest86.class.getSimpleName();
	private Gui gui = new Gui(myactivity, handler);
	private WifiPara wifiPara;
    private static String ServerIP;
    private static int PORT=8000;
    private final int DATASIZE=128;
    private byte[] mSendBuf=new byte[DATASIZE];
    private byte[] mRecBuf =new byte[DATASIZE];
    private SocketUtil socketUtil;
    private Sock_t sock_t=Sock_t.SOCK_TCP;
    private int ret=-1;
	public void systest86() throws IOException{
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(CLASS_NAME, "systest86", g_keeptime,"%s用例不支持自动化测试，请手动验证", TESTITEM);
			return;
		}
		initLayer();
		gui.cls_show_msg(TESTITEM+"测试,该用例需要作为server的设备和client的设备处于同一路由器的热点下,且路由器不要接入网线,请确保本前置条件" );
		for (int i = 0; i < DATASIZE; i++) //在这里初始化mSendBuf,这样server和client就可以校验
			mSendBuf[i]=(byte) i;
		//测试主入口
		while(true){
			int returnValue=gui.cls_show_msg("1.本机作为Server端\n2.本机作为Client端");
			switch (returnValue) {
			case '1':
				Server();//server设置
				break;
				
			case '2':
				Client();//client设置
				break;
				
			case ESC:
				intentSys();
				return;
			}
		}
	}
	
	//客户端
	public void Client(){
		gui.cls_show_msg("请先用另一台pos作为Server端,接入wifi并打开server项获取服务器的ip地址和端口号,已完成点击继续配置所需连接的wifi的ip地址和端口号.");
		if(confWifi()!=NDK_OK){
			gui.cls_show_msg("line %d:wifi未连接!!!",Tools.getLineInfo());
			return;
		}
		Log.e("2ServerIP=", ServerIP);
		try {
			socketUtil.setSocket(sock_t, gui);
			socketUtil.send(sock_t, mSendBuf, DATASIZE, 20);
			gui.cls_show_msg("验证信息发送成功,请在服务器查看校验结果.");
		} catch (IOException e) {
			 e.printStackTrace();  
			 gui.cls_show_msg1_record(CLASS_NAME, "Client", g_keeptime,"line %d:抛出异常(%s)",Tools.getLineInfo(),e.getMessage());
		}
	}
	
	//wifi配置
	public int confWifi() 
	{
		wifiPara=new WifiPara();
		wifiPara.setInput_way(false);
		switch (new Config(myactivity,handler).confConnWlan(wifiPara)) 
		{
		case NDK_OK:
			socketUtil = new SocketUtil(wifiPara.getServerIp(),wifiPara.getServerPort());
			gui.cls_show_msg1(1, "wlan参数配置完毕！！！");
			break;

		case NDK_ERR:
			return NDK_ERR;

		case NDK_ERR_QUIT:
		default:
			break;
		}
		ServerIP=wifiPara.getServerIp();
		PORT=wifiPara.getServerPort();
		layerBase.netDown(socketUtil, wifiPara, Sock_t.SOCK_TCP,  wifiPara.getType());
		Layer.linkStatus =LinkStatus.linkdown;
		if ((ret = layerBase.netUp(wifiPara, wifiPara.getType()))!= SUCC) 
		{
			gui.cls_show_msg1_record(CLASS_NAME,"confWifi",g_keeptime,"line %d:NetUp失败(ret = %d)", Tools.getLineInfo(),ret);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	//服务器端
	public void Server() 
	{  
		gui.cls_show_msg("该子用例需提前连入WiFi且对应路由器不接入网线,连接后点击任意键继续");
		ServerSocket serverSocket;
		Socket socket;
		InputStream SocketinputStream;
//		WifiUtil wifiUtil = new WifiUtil(myactivity);
		WifiUtil wifiUtil =WifiUtil.getInstance(myactivity,handler);
		ServerIP= wifiUtil.getIp();//使用工具类获取ip
		int size;
        try {
             serverSocket = new ServerSocket(PORT);  
             gui.cls_printf(("服务器端开启成功,当前服务器的ip地址为"+ServerIP+",端口号为"+PORT+",请在客户端pos连接本服务器").getBytes());
             socket = serverSocket.accept();//如果有连接过来,就把连接给对象socket
             SocketinputStream = socket.getInputStream();//输入流
             size=SocketinputStream.read(mRecBuf);
             if(size>0){
            	if(!Tools.memcmp(mSendBuf, mRecBuf, DATASIZE))
        		{
	    			gui.cls_show_msg1_record(CLASS_NAME,"Server",g_keeptime,"line %d:%s数据校验失败,请查看日志信息", Tools.getLineInfo(),TESTITEM);
	    			Log.e("mRecBuf=", Arrays.toString(mRecBuf));
        		}else{
        			serverSocket.close();
        			SocketinputStream.close();
        			gui.cls_show_msg("数据校验成功,socket通信收发测试通过.");
        		}
             }	 
            //当这个异常发生时，说明客户端那边的连接已经断开
        } catch (IOException e) {
            e.printStackTrace();
            gui.cls_show_msg("客户端连接已断开,请检查连接");
            //暂时没有加入关闭流
        } 
	}
}
