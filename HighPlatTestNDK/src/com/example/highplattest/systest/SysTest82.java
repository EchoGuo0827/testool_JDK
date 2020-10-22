package com.example.highplattest.systest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.newland.NLUART3Manager;
import android.newland.content.NlContext;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_MODE;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.k21controller.util.Dump;
import com.newland.ndk.JniNdk;

/************************************************************************
 * module 			: Systest综合模块
 * file name 		: Systest82.java
 * Author 			: xuess
 * version 			: 
 * DATE 			: 20180709
 * directory 		: 
 * description 		: 外接密码键盘签名板指令测试
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  xuess		   		20180709	 	created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SysTest82 extends DefaultFragment {
	private final String TAG = SysTest82.class.getSimpleName();
	private final String TESTITEM = "签名板测试";
	private NLUART3Manager uart3Manager=null;
	private Gui gui;
	private final int MAXWAITTIME = 60;//<=0表示永不超时
	private static final byte[] STX = new byte[] { 0x02 };
	private static final byte[] ETX = new byte[] { 0x03 };
	private static final int LEN_STX = STX.length;
	private static final int LEN_ETX = ETX.length;
	private static final int LEN_LENGTH = 2;
	private static final int LEN_LRC = 1;
	private static final int LEN_CMD_SP10 = 1;
	private static final int LEN_STATIC = LEN_STX + LEN_LENGTH + LEN_CMD_SP10 + LEN_ETX + LEN_LRC; //=6，请求报文、响应报文的固定部分长度都是6
	private static final int LEN_PICDATA = 10240;
	//命令定义
	private static final byte SP10_T_HANDSHAKE_REQUEST       = (byte) 0xA0;
	private static final byte SP10_T_HANDSHAKE_RESPONSE      = (byte) 0xB0;
	private static final byte SP10_T_FRONT_STATUS_INFORM     = (byte) 0xA1;
	private static final byte SP10_T_FRONT_STATUS_RESPONSE   = (byte) 0xB1;
	private static final byte SP10_T_SIGN_INPUT_REQUEST      = (byte) 0xA2;
	private static final byte SP10_T_SIGN_SUCC_RESPONSE      = (byte) 0xB2;
	private static final byte SP10_T_SIGN_FAIL_RESPONSE      = (byte) 0xC2;
	private static final byte SP10_T_SIGN_END_REQUEST        = (byte) 0xA3;
	private static final byte SP10_T_SIGN_END_RESPONSE       = (byte) 0xB3;
	private static final byte SP10_T_BULK_TRANSFER_REQUEST   = (byte) 0xA4;
	private static final byte SP10_T_BULK_TRANSFER_RESPONSE  = (byte) 0xB4;
	private static final byte SP10_T_BULK_END_REQUEST        = (byte) 0xA5;
	private static final byte SP10_T_BULK_END_RESPONSE       = (byte) 0xB5;
	private static final byte SP10_T_BULK_TRANSFER_SUCC      = (byte) 0xA8;
	private static final byte SP10_T_BULK_TRANSFER_FAIL      = (byte) 0xA9;
	private static final byte SP10_T_SWITCH_SIGN_MODE        = (byte) 0xF3;
	private static final byte SP10_T_SIGN_STANDBY_IMAGE      = (byte) 0xF5;
	private static final byte SP10_T_SET_SIGN_TIMEOUT        = (byte) 0xF7;
	private static final byte SP10_T_SET_SIGN_NUM            = (byte) 0xF8;
	private static final byte SP10_T_SET_SIGN_WRITING        = (byte) 0xF9;
	private static final byte SP10_T_SET_IMAGE_SIZE          = (byte) 0xE3;
	private static final byte SP10_T_SET_IMAGE_BACKGROUND    = (byte) 0xE4;
	private static final byte SP10_T_RESPONSE_STATUS_SUCC    = (byte) 0x00;
	//private static final byte SP10_T_RESPONSE_STATUS_FAIL    = (byte) 0xFE;
	private int ret=-1;
	//private byte[] recvBuf;
	private boolean isInit = false;
	private String[] para={"8N1NB","8N1NN"};// para[0]:阻塞      para[1]:非阻塞
	
	public void systest82()
	{
		gui = new Gui(myactivity, handler);
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(TAG, TAG, g_keeptime,"%s不支持自动测试,请手动验证", TESTITEM);
			return;
		}
		uart3Manager = (NLUART3Manager) myactivity.getSystemService(NlContext.UART3_SERVICE);
		while(true)
		{
			int returnValue=gui.cls_show_msg("签名板测试\n0.配置\n1.银联指令\n2.自定义指令\n");
			switch (returnValue) 
			{
				case '0':
					BpsBean.bpsValue = 115200 ;//说明文档中指定通讯波特率为115200 
					gui.cls_show_msg("请确保POS与外接密码键盘已通过RS232串口连接!任意键继续");
					//初始化串口
					if((ret = portOpen())!= NDK_OK){
						gui.cls_show_msg1_record(TAG, "setBps", g_keeptime,"line %d:RS232串口初始化失败(%d)", Tools.getLineInfo(),ret);
						portClose();
						break;
					}
					gui.cls_show_msg1(2, "串口初始化成功!");
					isInit = true;
					break;
					
				case ESC:
					portClose();
					intentSys();
					return;
					
				case '1':
				case '2':
					initJudge(returnValue);
					break;
			}
		}
	}
	
	public void initJudge(int value) 
	{
		if(!isInit)
		{
			gui.cls_show_msg("请先进行配置,点任意键继续");
			return;
		}
		switch (value) 
		{
		case '1':
			test_sign_function();
			break;
			
		case '2':
			test_set_function();
			break;
			
		
		}
	}
	
	//银联指令测试
	public void test_sign_function(){
		/*private & local definition*/
		int ret = -1;
		byte[] saveflag = new byte[1];
		byte[] signNum = new byte[3];
		byte[] picData = new byte[LEN_PICDATA];
		byte[] signinfNum = new byte[1];
		
		/*process body*/
		gui.cls_printf("签名板银联指令测试...".getBytes());
		//case1:握手请求
		if((ret=SP10_T_SendHandshake(saveflag))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:握手请求失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		//case2.1:签字输入请求，失败响应
		gui.cls_printf("签名板提示签名时候请尽快点取消".getBytes());
		if((ret=test_base_sign(SP10_T_SIGN_FAIL_RESPONSE,signNum,picData))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:签字流程失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
				
		//case2.2:签字输入请求，成功响应
		gui.cls_printf("签名板提示签名时候请尽快签名并点确认".getBytes());
		if((ret=test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:签字流程失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_printf("签名板提示签名时候请尽快签名->点重输再签名->点确认".getBytes());
		if((ret=test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:签字流程失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case3.1:前一笔状态通知:发送签字板保存签字报文，预期响应为电子签字成功存储0x02
		if((ret=SP10_T_FrontStatus(signNum,(byte)0x00,(byte)0x02))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:前一笔状态通知请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case3.2:前一笔状态通知:发送签字板不保存签字报文，预期响应为电子签字已删除0x01
		if((ret=SP10_T_FrontStatus(signNum,(byte)0x01,(byte)0x01))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:前一笔状态通知请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case4.1:批量传输请求--批量传输接收失败应答 --签字板响应的signinfnum应没有变化,与批量传输请求时响应的一样
		byte[] signinfNum1 = new byte[1];
		if((ret=SP10_T_BulkTransfer(signinfNum,signNum))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//获取signinfnum的值,如果为0表示签字板无签字信息就没有的传输就不执行应答,如果大于0才执行应答
		//如果值为0xF1表示不支持存储那么也就无需传输
		if(signinfNum[0]>0 && signinfNum[0] != 0xF1) 
		{
			if((ret=SP10_T_BulkTransferFail(signNum,signinfNum1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求失败应答失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			if(signinfNum[0] != signinfNum1[0]){
				gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求失败应答失败(%02x,%02x)", Tools.getLineInfo(), signinfNum[0],signinfNum1[0]);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case4.2:批量传输请求--批量传输接收成功应答 --签字板响应的signinfnum要减1,与批量传输请求时响应的不一样
		if((ret=SP10_T_BulkTransfer(signinfNum,signNum))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		//获取signinfnum的值,大于1才执行应答,因为如果只剩一笔了发送接收成功应答后不再返回(设计流程上决定的)
		if(signinfNum[0]>1 && signinfNum[0] != 0xF1) 
		{
			if((ret=SP10_T_BulkTransferSucc(signNum,signinfNum1))!=NDK_OK){	
				gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求成功应答失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			if((signinfNum[0]-1) != signinfNum1[0]){
				gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量传输请求成功应答失败(%02x,%02x)", Tools.getLineInfo(), signinfNum[0],signinfNum1[0]);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case5:批量结束请求
		if((ret=SP10_T_BulkEnd())!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_sign_function", g_keeptime,"line %d:批量结束请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		gui.cls_show_msg1_record(TAG,"test_sign_function",g_time_0,"签名板银联指令测试通过");
	}
	
	//自定义指令测试
	public void test_set_function(){
		/*private & local definition*/
		int ret = -1;
		byte[] saveflag = new byte[1];
		byte[] signNum = new byte[3];
		byte[] picData = new byte[LEN_PICDATA];
		byte[] imgBuf;
		
		/*process body*/
		//case1.1:设置切换签名板模式,设置签名板不保存签名0x00
		if((ret=SP10_T_SwitchSignMode((byte)0x00))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板不保存签名失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=SP10_T_SendHandshake(saveflag))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:握手请求失败(%d)", Tools.getLineInfo(), ret);
			return;
		}
		if(saveflag[0] != 0x00){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:签名板是否支持存储返回校验错误(%02x)", Tools.getLineInfo(), saveflag[0]);
		}
		//case1.2:设置切换签名板模式,设置签名板保存签名0x01,默认是保存
		if((ret=SP10_T_SwitchSignMode((byte)0x01))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板不保存签名失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if((ret=SP10_T_SendHandshake(saveflag))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:握手请求失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		if(saveflag[0] != 0x01){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:签名板是否支持存储返回校验错误(%02x)", Tools.getLineInfo(), saveflag[0]);
		}
		
		//case2:设置签名板签名超时时间 
		if((ret=SP10_T_SignTimeout(100))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名超时时间失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时,右上角超时时间应为10s,不操作等待超时".getBytes());
		test_base_sign(SP10_T_SIGN_FAIL_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名板右上角超时时间是否为10s,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名超时时间失败", Tools.getLineInfo());
		}
		//恢复默认时间150s
		if((ret=SP10_T_SignTimeout(1500))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名超时时间失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case3:设置签名板签名次数
		//设为无限次
		if((ret=SP10_T_SignNum(0))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名次数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时,请签字后按重输再签字,重复5次以上再点确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名板上的签名重输次数是否可反复至少5次以上,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名次数失败", Tools.getLineInfo());
		}
		//设为1次
		if((ret=SP10_T_SignNum(1))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名次数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时,验证签名板上的签名不可重输,请签字后确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名板上是否无重输选项,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名次数失败", Tools.getLineInfo());
		}
		//恢复默认次数2次
		if((ret=SP10_T_SignNum(2))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名次数失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case4:设置签名板签名笔迹粗细程度
		//设为较粗
		if((ret=SP10_T_SignWriting(1))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名笔迹粗细程度失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时,验证签名板上的笔迹略粗,请签字后确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名板上的笔迹是否略粗,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名笔迹粗细程度失败", Tools.getLineInfo());
		}
		//恢复默认程度为较细
		if((ret=SP10_T_SignWriting(0))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置签名板签名笔迹粗细程度失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case5：设置输出图片大小
		if((ret=SP10_T_OutImageSize(120,50))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片大小失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时,验证签名区域大小为120*50,请签字后确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名板上的签字区域是否为120*50,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片大小失败", Tools.getLineInfo());
		}
		//恢复默认大小为300*100
		if((ret=SP10_T_OutImageSize(300,100))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片大小失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case6.1:设置输出图片背景为黑底白字
		gui.cls_show_msg("请安装打印纸后任意键继续");
		if((ret=SP10_T_OutBackImage((byte)0x10))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片背景失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时候请尽快签名并点确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		//打印
		imgBuf = new byte[picData.length-62];
		System.arraycopy(picData,62,imgBuf,0,picData.length-62);//根据systest92,实际图片是data2域中第63个字节开始，62是开发帮忙定位出来的，文档中没有明确说明；
		prnImage(320,100,0,imgBuf);
		if(gui.cls_show_msg("打印出来的是否为黑底白字,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片背景失败", Tools.getLineInfo());
		}
		//case6.2:设置输出图片背景为默认的白底黑字
		if((ret=SP10_T_OutBackImage((byte)0x00))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片背景失败(%d)", Tools.getLineInfo(), ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		gui.cls_printf("签名板提示签名时候请尽快签名并点确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		//打印
		imgBuf = new byte[picData.length-62];
		System.arraycopy(picData,62,imgBuf,0,picData.length-62);//根据systest92,实际图片是data2域中第63个字节开始，62是开发帮忙定位出来的，文档中没有明确说明；
		prnImage(320,100,0,imgBuf);
		if(gui.cls_show_msg("打印出来的是否为白底黑字,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:设置输出图片背景失败", Tools.getLineInfo());
		}
		
		//case7:设置签名板待机图片测试
		test_standby_image();
		
		//验证是否都恢复默认设置
		gui.cls_printf("提示签名时候请观察各项属性是否恢复默认:超时时间150s、签名次数2次、笔迹粗细较细、签名区域大小300*100,验证后尽快签名并点确认".getBytes());
		test_base_sign(SP10_T_SIGN_SUCC_RESPONSE,signNum,picData);
		if(gui.cls_show_msg("签名版各项属性是否恢复默认,是[确认],否[其他]") != ENTER){
			gui.cls_show_msg1_record(TAG, "test_set_function", g_keeptime,"line %d:自定义设置类指令测试失败", Tools.getLineInfo());
		}
		gui.cls_show_msg1_record(TAG,"test_set_function",g_time_0,"自定义指令测试通过");
	}
	
	//整合签字输入请求和签字结束请求
	public int test_base_sign(byte response,byte[] signNum,byte[] picData){
		if((ret=SP10_T_SignInput(response,signNum,picData))!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_base_sign", g_keeptime,"line %d:签字输入请求失败(%d)", Tools.getLineInfo(), ret);
			SP10_T_SignEnd();
			return NDK_ERR;
		}
		//签字结束请求
		if((ret=SP10_T_SignEnd())!=NDK_OK)
		{	
			gui.cls_show_msg1_record(TAG, "test_base_sign", g_keeptime,"line %d:签字结束请求失败(%d)", Tools.getLineInfo(), ret);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	//设置签名板待机图片测试
	public void test_standby_image(){
		int cnum,snum,cnt,value,num;
		int ret = -1;
		int filesize = 0;
		byte[] data,sdata;
		String[] imagestr = {"彩屏POS开机界面","新大陆logo图标"};
		String[] imagepath = {GlobalVariable.sdPath+"test.bmp",GlobalVariable.sdPath+"ico_nc.bmp"};
		FileSystem fileSystem = new FileSystem();
		/*process body*/
		if(gui.cls_show_msg("子用例设置签名板待机图片测试,请下载SVN上SP10-T文件夹里的ico_nc.bmp,test.bmp到内置SD卡根目录下,[取消]退出,其他继续")==ESC)
			return;
			
		for(int j = 0;j<2;j++){
			//读取图片文件数据
			if((ret = fileSystem.JDK_FsOpen(imagepath[j], "r")) != NDK_OK)
			{
				gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime, "line %d:开打图片文件失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			if((filesize = fileSystem.JDK_FsFileSize(imagepath[j])) < 0)
			{
				gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime, "line %d:获取图片文件大小失败(%d)", Tools.getLineInfo(), filesize);
				if (!GlobalVariable.isContinue)
					return;
			}
			data = new byte[filesize];
			FileInputStream fileIn = null;
			try {
				fileIn = new FileInputStream(new File(imagepath[j]));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if((ret = (fileSystem.JDK_FsRead(fileIn, data,filesize))) != filesize)
			{
				gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime, "line %d:读取图片文件内容失败(%d)", Tools.getLineInfo(), ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			LoggerUtil.e("filesize:"+filesize);
			//计算图片大小,及分几包传输等
			cnt = filesize/256;
			value = filesize%256;
			if(value != 0)
				num=cnt;
			else
				num=cnt-1;
			
			gui.cls_show_msg1(1,"将设置待机图片为%s,大约需要3分钟,传输中请耐心等待...",imagestr[j]);
			for(int i=0;i<cnt;i++)
			{
				cnum = i;
				snum = num - i;
				sdata = new byte[256];
				System.arraycopy(data,256*i,sdata,0,256);
				if((ret=SP10_T_SignStandbyImage(cnum,snum,sdata,256)) !=NDK_OK) {	
					gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime,"line %d:设置签名板待机图片失败(%d)", Tools.getLineInfo(), ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if(value != 0)
			{
				cnum = cnt;
				snum = 0;
				sdata = new byte[value];
				System.arraycopy(data,256*cnt,sdata,0,value);
				if((ret=SP10_T_SignStandbyImage(cnum,snum,sdata,value)) !=NDK_OK) 
				{	
					gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime,"line %d:设置签名板待机图片失败(%d)", Tools.getLineInfo(), ret);
					if (!GlobalVariable.isContinue)
						return;
				}
			}
			if(gui.cls_show_msg("签名板上的待机图片已变为%s,是[确认],否[其他]",imagestr[j]) != ENTER){
				gui.cls_show_msg1_record(TAG, "test_standby_image", g_keeptime,"line %d:加载显示图片测试失败", Tools.getLineInfo());
			}
		}
		gui.cls_show_msg1(2, "子用例设置签名板待机图片测试通过");
	}
	
	/**握手请求发送，握手响应判断
	 * @param saveflag 返回值，签字板是否支持存储的标志
	 * @return
	 */
	public int SP10_T_SendHandshake(byte[] saveflag){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1+6+52+1];//附加数据最大为1字节的响应状态+6字节的软件版号+52字节的设备序列号+1字节的支持存储标志
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_HANDSHAKE_REQUEST};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, LEN_CMD_SP10,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SendHandshake", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_HANDSHAKE_RESPONSE){
			if(appendlen[0] > 0 && append[0] == 0x00){
				gui.cls_show_msg1_record(TAG, "SP10_T_SendHandshake", g_keeptime,"line %d:签名板未准备好(%02x)", Tools.getLineInfo(),append[0] == 0x00);
				return NDK_ERR;
			}
			saveflag[0] = append[appendlen[0]-1];
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SendHandshake", g_keeptime,"line %d:握手请求响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**签字输入请求及响应
	 * @param response 传入值，预期响应命令
	 * @param signNum 返回值，电子签名编号
	 * @param picData 返回值，图片信息数据
	 * @return
	 */
	public int SP10_T_SignInput(byte response,byte[] signNum,byte[] picData){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[LEN_PICDATA+3];//附加数据最大为3字节的电子签名编号+10k的图片信息数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] inputdata = ISOUtils.hex2byte("6E65776C616E6438");//用newland8作为要传入的8位特征码
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_SIGN_INPUT_REQUEST}, inputdata);
		
		/*process body*/
		if((ret=cmd_frame_factory(data, LEN_CMD_SP10+8,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignInput", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == response){//跟预期响应值一样再按照成功响应和失败响应分别处理
			if(answerCmd[0] == SP10_T_SIGN_SUCC_RESPONSE){
				System.arraycopy(append,0,signNum,0,3);
				System.arraycopy(append,3,picData,0,appendlen[0]-3);
				return NDK_OK;
			}
			if(answerCmd[0] == SP10_T_SIGN_FAIL_RESPONSE){
				return NDK_OK;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SignInput", g_keeptime,"line %d:签字输入请求响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**签字结束请求及响应
	 * @return
	 */
	public int SP10_T_SignEnd(){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//该指令的响应报文没有附加数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_SIGN_END_REQUEST};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, LEN_CMD_SP10,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignEnd", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] != SP10_T_SIGN_END_RESPONSE){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignEnd", g_keeptime,"line %d:签字结束请求响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**前一笔状态通知报文发送及响应
	 * @param signNum 传入值，要操作的电子签名的编号
	 * @param state 传入值，前一笔交易传输到收到平台的状态(1：成功上送或压缩失败终端打印，签字板不保存签字、0： 上送失败，签字板保存签字)
	 * @param response 传入值，预期的1位响应状态码
	 * @return
	 */
	public int SP10_T_FrontStatus(byte[] signNum,byte state,byte response){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1位的响应状态(0x01：电子签字已删除、0x02：电子签字成功存储、0x03：电子签字成功存储， 剩于存储空间低于10笔、0x04：电子签字存储失败)
		int[] appendlen = new int[1];
		int ret = -1;
		//构造data
		byte[] data = new byte[5];//1位命令+3位signNum+1位state
		data[0] = SP10_T_FRONT_STATUS_INFORM;
		System.arraycopy(signNum,0,data,1,3);
		data[5-1] = state;
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_FrontStatus", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_FRONT_STATUS_RESPONSE){
			if(append[0] != response){
				gui.cls_show_msg1_record(TAG, "SP10_T_FrontStatus", g_keeptime,"line %d:前一笔状态通知响应状态码错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_FrontStatus", g_keeptime,"line %d:前一笔状态通知响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**批量传输请求报文发送及响应
	 * @param signinfNum 返回值，剩余签字总数
	 * @param signNum 返回值，当前签字编号
	 * @return
	 */
	public int SP10_T_BulkTransfer(byte[] signinfNum,byte[] signNum){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[LEN_PICDATA+4];//附加数据为1字节的剩余签字总数+3字节的电子签名编号+10k的图片信息数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_BULK_TRANSFER_REQUEST};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, LEN_CMD_SP10,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransfer", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_BULK_TRANSFER_RESPONSE){
			signinfNum[0] = append[0];
			System.arraycopy(append,1,signNum,0,3);
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransfer", g_keeptime,"line %d:批量传输请求响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**批量传输接收成功报文发送及响应
	 * @param signNum 传入值，上送成功签字编号
	 * @param signinfNum 返回值，剩余签字总数
	 * @return
	 */
	public int SP10_T_BulkTransferSucc(byte[] signNum,byte[] signinfNum){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[LEN_PICDATA+4];//附加数据为1字节的剩余签字总数+3字节的电子签名编号+10k的图片信息数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_BULK_TRANSFER_SUCC}, signNum);
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransferSucc", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_BULK_TRANSFER_RESPONSE){
			signinfNum[0] = append[0];
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransferSucc", g_keeptime,"line %d:批量传输接收成功响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		}
		return NDK_OK;
	}
	
	/**批量传输接收失败报文发送及响应
	 * @param signNum 传入值，上送失败签字编号
	 * @param signinfNum 返回值，剩余签字总数
	 * @return
	 */
	public int SP10_T_BulkTransferFail(byte[] signNum,byte[] signinfNum){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[LEN_PICDATA+4];//附加数据为1字节的剩余签字总数+3字节的电子签名编号+10k的图片信息数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_BULK_TRANSFER_FAIL}, signNum);
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransferFail", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_BULK_TRANSFER_RESPONSE){
			signinfNum[0] = append[0];
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkTransferFail", g_keeptime,"line %d:批量传输接收失败响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**批量结束请求及响应
	 * @return
	 */
	public int SP10_T_BulkEnd(){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//该指令的响应报文没有附加数据
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_BULK_END_REQUEST};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, LEN_CMD_SP10,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkEnd", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] != SP10_T_BULK_END_RESPONSE){
			gui.cls_show_msg1_record(TAG, "SP10_T_BulkEnd", g_keeptime,"line %d:批量结束请求响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	/**设置签名板的待机图片
	 * @param cnum 前第几个包
	 * @param snum 后续还有几个包
	 * @param picData 图片数据(前面每包固定为256字节 最后一包按实际长度)
	 * @param picDataLen 图片数据长度
	 * @return
	 */
	public int SP10_T_SignStandbyImage(int cnum, int snum ,byte[] picData, int picDataLen){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1,offset=0;
		byte[] data = new byte[5+picDataLen];//1字节命令+2字节当前第几个包+2字节后续还有几个包+长度为picDataLen的图片数据
		//拼装Data
		data[0] = SP10_T_SIGN_STANDBY_IMAGE;
		offset +=1;
		//转换当前第几个包cnum为2字节byte
		byte[] cnumBuf = ISOUtils.intToBytes(cnum, 2, true);
		System.arraycopy(cnumBuf, 0, data, offset, 2);
		offset += 2;
		//转换后续还有几个包snum为2字节byte
		byte[] snumBuf = ISOUtils.intToBytes(snum, 2, true);
		System.arraycopy(snumBuf, 0, data, offset, 2);
		offset += 2;
		//拼装图片数据
		System.arraycopy(picData, 0, data, offset, picDataLen);
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignStandbyImage", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SIGN_STANDBY_IMAGE){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_SignStandbyImage", g_keeptime,"line %d:设置签名板的待机图片响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SignStandbyImage", g_keeptime,"line %d:设置签名板的待机图片响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置切换签名板模式
	 * @param signMode 0：不保存签名(默认)；1：保存签名
	 * @return
	 */
	public int SP10_T_SwitchSignMode(byte signMode){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_SWITCH_SIGN_MODE,signMode};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SwitchSignMode", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SWITCH_SIGN_MODE){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_SwitchSignMode", g_keeptime,"line %d:设置切换签名板模式响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SwitchSignMode", g_keeptime,"line %d:设置切换签名板模式响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置签名板签名超时时间 
	 * @param timeout 超时时间，单位0.1s；0为无超时
	 * @return
	 */
	public int SP10_T_SignTimeout(int timeout){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_SET_SIGN_TIMEOUT}, ISOUtils.intToBytes(timeout, 2, true));
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignTimeout", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SET_SIGN_TIMEOUT){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_SignTimeout", g_keeptime,"line %d:设置签名板签名超时时间响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SignTimeout", g_keeptime,"line %d:设置签名板签名超时时间 响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置签名板签名次数
	 * @param signnum 签名次数,0表示无限制
	 * @return
	 */
	public int SP10_T_SignNum(int signnum){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_SET_SIGN_NUM}, ISOUtils.intToBytes(signnum, 1, true));
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignNum", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SET_SIGN_NUM){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_SignNum", g_keeptime,"line %d:设置签名板签名次数响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SignNum", g_keeptime,"line %d:设置签名板签名次数响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置签名笔迹粗细
	 * @param signwriting 笔迹粗细程度，0，默认; 1，默认X2; 2,默认/2(实际不支持)
	 * @return
	 */
	public int SP10_T_SignWriting(int signwriting){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = ISOUtils.concat(new byte[]{SP10_T_SET_SIGN_WRITING}, ISOUtils.intToBytes(signwriting, 1, true));
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_SignWriting", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SET_SIGN_WRITING){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_SignWriting", g_keeptime,"line %d:设置签名笔迹粗细响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_SignWriting", g_keeptime,"line %d:设置签名笔迹粗细响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置输出图片大小
	 * @param width 签名区域大小宽
	 * @param height 签名区域大小高
	 * @return
	 */
	public int SP10_T_OutImageSize(int width,int height){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1,offset=0;
		byte[] data = new byte[5];//1字节命令+2字节签名区域大小宽+2字节签名区域大小高
		//拼装Data
		data[0] = SP10_T_SET_IMAGE_SIZE;
		offset +=1;
		//转换签名区域大小宽width为2字节byte
		byte[] wBuf = ISOUtils.intToBytes(width, 2, true);
		System.arraycopy(wBuf, 0, data, offset, 2);
		offset += 2;
		//转换签名区域大小高height为2字节byte
		byte[] hBuf = ISOUtils.intToBytes(height, 2, true);
		System.arraycopy(hBuf, 0, data, offset, 2);
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_OutImageSize", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SET_IMAGE_SIZE){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_OutImageSize", g_keeptime,"line %d:设置输出图片大小响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_OutImageSize", g_keeptime,"line %d:设置输出图片大小响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**设置输出图片背景
	 * @param outmode 输出图片背景，0x00:白底黑字（默认）、0x10：黑底白字
	 * @return
	 */
	public int SP10_T_OutBackImage(byte outMode){
		/*private & local definition*/
		byte[] answerCmd = new byte[1];
		byte[] append = new byte[1];//附加数据为1字节的响应状态
		int[] appendlen = new int[1];
		int ret = -1;
		byte[] data = new byte[]{SP10_T_SET_IMAGE_BACKGROUND,outMode};
		
		/*process body*/
		if((ret=cmd_frame_factory(data, data.length,answerCmd,append,appendlen))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "SP10_T_OutBackImage", g_keeptime,"line %d:测试失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		} 
		//解包返回的数据
		if(answerCmd[0] == SP10_T_SET_IMAGE_BACKGROUND){
			if(append[0] != SP10_T_RESPONSE_STATUS_SUCC){
				gui.cls_show_msg1_record(TAG, "SP10_T_OutBackImage", g_keeptime,"line %d:设置输出图片背景响应状态错误(%02x)", Tools.getLineInfo(), append[0]);
				return NDK_ERR;
			}
		} else{
			gui.cls_show_msg1_record(TAG, "SP10_T_OutBackImage", g_keeptime,"line %d:设置输出图片背景响应命令错误(%02x)", Tools.getLineInfo(), answerCmd[0]);
			return NDK_ERR;
		} 
		return NDK_OK;
	}
	
	/**
	 * 封装与发送命令报文
	 * 
	 * @param data 需传输的数据单元
	 * @param datalen 数据单元长度
	 * @param answerCmd 获取1字节的响应命令放入此数组
	 * @param append 获取附加数据放入此数组
	 * @param appendlen 获取附加数据的长度放入此数组
	 * @return 
	 */
	private int cmd_frame_factory(byte[] data,int datalen,byte[] answerCmd,byte[] append,int[] appendlen){
		/*private & local definition*/
		byte[] cmdPack;
		byte[] recvBuf;
		byte[] temp = new byte[3];
		int len;
		/*process body*/
		//组包
		cmdPack = cmdFrame_SP10(data, datalen);
		
		//发包收包根据串口
		if ((ret = uart3Manager.write(cmdPack, cmdPack.length, MAXWAITTIME)) != cmdPack.length) 
		{
			gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232写失败(ret=%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		//先接收3个字节，分别是STX和2字节的长度数据
		if ((ret = uart3Manager.read(temp, 3, MAXWAITTIME)) < 0){
			gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232读失败(ret=%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		len = ISOUtils.hexInt(temp, 1, 2);//获取响应报文中的长度数据来确定要接下来接收的长度
		byte[] buf = new byte[len+1];//接下来要接收长度为len的数据和1位lrc校验位
		if ((ret = uart3Manager.read(buf, len+1, MAXWAITTIME)) < 0){
			gui.cls_show_msg1_record(TAG, "cmd_frame_factory", 2,"line %d:RS232读失败(ret=%d)", Tools.getLineInfo(),ret);
			return ret;
		}
		uart3Manager.ioctl(0x540B, new byte[]{(byte)2});//清串口缓存
		recvBuf = ISOUtils.concat(temp, buf);
		LoggerUtil.e("recvBuf:"+Dump.getHexDump(recvBuf));
		
		//返回响应命令、附加数据、附加数据长度
		answerCmd[0] = recvBuf[LEN_STX + LEN_LENGTH ];
		if(recvBuf.length == LEN_STATIC)//返回的响应报文长度为6说明没有附加数据
		{
			appendlen[0] = 0;
		} else if(recvBuf.length > LEN_STATIC)
		{
			appendlen[0] = recvBuf.length - LEN_STATIC;//附加数据长度
			System.arraycopy(recvBuf,4,append,0,appendlen[0]);
		} else
		{
			return NDK_ERR;
		}

		return NDK_OK;
	}
	
	/**
	 * 构造签字板通讯报文格式
	 * 
	 * @param data 包括命令和其后的数据
	 * @param datalen 数据单元长度
	 * @return rslt 构造好的报文
	 */
	private byte[] cmdFrame_SP10(byte[] data,int datalen){
		int offset = 0;
		byte[] rslt = new byte[LEN_STX + LEN_LENGTH + datalen + LEN_ETX + LEN_LRC ];//完整的请求报文长度
		byte[] lrcdata = new byte[LEN_LENGTH + datalen + LEN_ETX];//需要计算lrc的部分
		byte[] len = new byte[LEN_LENGTH];
		
		//计算2字节的长度
		len = ISOUtils.intToBytes(datalen + LEN_ETX, LEN_LENGTH, true);//长度包括命令和其后的数据加上ETX
		
		//拼装需要计算lrc的部分
		System.arraycopy(len, 0, lrcdata, 0, LEN_LENGTH);
		offset += LEN_LENGTH;
		
		System.arraycopy(data, 0, lrcdata, offset, datalen);
		offset += datalen;
		
		System.arraycopy(ETX, 0, lrcdata, offset, LEN_ETX);
		
		//计算校验位
		byte[] lrc = caculateLRC(lrcdata);
		
		//拼装完整命令
		offset = 0;
		System.arraycopy(STX, 0, rslt, 0, LEN_STX);
		offset += LEN_STX;
		
		System.arraycopy(lrcdata, 0, rslt, offset, LEN_LENGTH + datalen + LEN_ETX);
		offset += LEN_LENGTH + datalen + LEN_ETX;
		
		System.arraycopy(lrc, 0, rslt, offset, LEN_LRC);
		
		LoggerUtil.e("cmdPack:"+Dump.getHexDump(rslt));
		
		return rslt;
	}
	
	/**
	 * 计算lrc
	 * 
	 * @param payload
	 * @return lrc
	 */
	private byte[] caculateLRC(byte[] payload) {
		int offset = 0;
		byte lrc = payload[0];
		do {
			offset++;
			lrc ^= payload[offset];
		} while (offset < payload.length - 1);

		return new byte[] { lrc };
	}
	
	/**
	 * 整合串口初始化
	 */
	private int portOpen() {
		int fd = -1,ret = -1;
		if ((fd = uart3Manager.open()) == -1) {
			gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:打开串口失败，fd=%d", Tools.getLineInfo(), fd);
			return NDK_ERR;
		}

		if ((ret = uart3Manager.setconfig(BpsBean.bpsValue, 0,para[1].getBytes())) != ANDROID_OK) {
			gui.cls_show_msg1_record(TAG, "rs232_portOpen", 2,"line %d:波特率设置失败，ret=%d", Tools.getLineInfo(), ret);
			return NDK_ERR;
		}
		
		return NDK_OK;
	}

	/**
	 * 整合串口关闭
	 */
	private void portClose() {
		uart3Manager.close();
	}
	
	//打印输出的签字图片
	private int prnImage(int width, int height, int position, byte[]imgBuf){
		int ret = -1;
		if((ret = JniNdk.JNI_Print_Init(1)) != NDK_OK){
			gui.cls_show_msg1_record(TAG, "prnImage", 2,"line %d:打印初始化失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		// 设置单倍宽、单倍高
		if((ret=JniNdk.JNI_Print_SetMode(EM_PRN_MODE.PRN_MODE_NORMAL.getValue(), 0))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "prnImage", 2,"line %d:设置打印模式失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if((ret=JniNdk.JNI_Print_Image(width, height, position, imgBuf))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "prnImage", 2,"line %d:设置图片失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if((ret=JniNdk.JNI_Print_FeedByPix(50))!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "prnImage", 2,"line %d:按像素走纸失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		if((ret=JniNdk.JNI_Print_Start())!=NDK_OK){
			gui.cls_show_msg1_record(TAG, "prnImage", 2,"line %d:启动打印失败(%d)", Tools.getLineInfo(), ret);
			return ret;
		}
		return NDK_OK;
	}
}
