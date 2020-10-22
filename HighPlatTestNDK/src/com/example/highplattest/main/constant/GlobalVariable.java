package com.example.highplattest.main.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Pair_Result;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
/************************************************************************
 * 
 * module 			: main
 * file name 		: GlobalVariable.java 
 * Author 			: 
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 全局变量
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class GlobalVariable implements Lib
{
		// 自动测试的控制
		// 说明：AUTOHANDFLAG = 1代表全自动   AUTOHANDFLAG = 2代表手自动，AutoModule = 0 全手动，AUTOHANDFLAG = 3，代表自选多个用例运行
		public static ParaEnum.AutoFlag gAutoFlag=AutoFlag.HandFull;
		
		public static String changePack;
		// 连续压力的开关  false：不进行连续压力测试    true：进行连续压力测试  自动化的时候要配置成连续压力测试
		public static boolean gSequencePressFlag =false;
		public static int WAITMAXTIME = 30;
		// 一个集合用来保存模块包名
		public static List<String> listPackage = new ArrayList<String>();
		// 一个集合用来保存模块用例号
		public static List<List<String>> listModuleNum = new ArrayList<List<String>>();
		
		// 控制生成测试报告的格式
		public static int TestCount=0;
		// 控制测试报告中的模块的值
		public static String module="";
		// 全局变量
		public static String PHONE_PATH;
		
		public static int RETURN_VALUE = 0;
		
		public static int chooseConfig;
		
		// 休眠唤醒标志位
		public static boolean isWakeUp = false;
		//是否继续测试的标志
		public static boolean isContinue = true;
		
		// 打补丁值
		public static int MDM_PatchType5=5;
		public static int MDM_PatchType4=4;
		public static int MDM_PatchType3=3;
		public static int MDM_PatchType2=2;
		public static int MDM_PatchType1=1;
		public static int MDM_PatchType0=0;
		// modem检测状态的值
		public static int CONNECT_AFTERPREDIAL=2;
		public static int OK_AFTERPREDIAL=1;
		public static int NORETURN_AFTERPREDIAL=0;
		public static int NOPREDIAL=-11;
		public static int MS_NODIALTONE=-2;
		public static int MS_NOCARRIER=-3;
		public static int MS_BUSY=-4;
		public static int MS_ERROR=-5;
		public static int NDK_ERR_PARA =-6;
		public static int NDK_ERR_MODEM_INIT_NOT=-505;
		// 每种机器配置文件的存放路径
		public static int Position;
		public static int index = 0;//用于显示页号的索引
		
		
		public static final int SUCC=0;
		public static final int FAIL = -1;
		public static final int STD_OUT = 0;
		public static final int STD_ON = 1;
		public static int IS_RETURN;	
		public static boolean FLAG_SYSTEM_SIGN = true;
		public static boolean FLAG_FALSE = false;
		public static boolean FLAG_True = true;
		//磁卡
		public static int selTK = TK2_3;
		public static boolean isdisp = false;
		
		
		// 线程结束标志位
		public static boolean IS_THREAD_OVER = false;
		
		// 文件个数控制
		public static int file_count = 0;
		
		// 屏幕的宽高
		public static int ScreenHeight;
		public static int ScreenWidth;
		// 状态栏高度
		public static int StatusHeight;
		// 标题栏高度
		public static int TitleBarHeight;
		// 触屏校验
		public static float gScreenX;
		public static float gScreenY;
		public static int screenState;
		
		// 蓝牙
		public static boolean isDongle = false;
		
//		// wifi扫描结果，直接修改为输入AP的SSID与密码进行WIFI测试，但是wifi是否连接上还需要该参数 modify by zhengxq
//		public static boolean gWifiScan = false;
		
		//wifi连接结果
		public static boolean isWifiConnected = false;
		
		// IC/SAM卡
		public static List<EM_ICTYPE> cardNo = new ArrayList<EM_ICTYPE>();
		
		// home和recentapp的标志位
		public static int virtualKey = 0;
		public static boolean isBackkey = false;
		
		// 内部SD卡的路径
		public static String sdPath="/mnt/shell/emulated/0/";
		// 外部SD卡的路径
		public static String TFPath="/storage/sdcard1/";
		// u盘的路径
		public static String uPath="/storage/usbotg/";
		
		// 存放全部的模块
		public static List<String> testPackName = new ArrayList<String>();
		
		public static Model_Type currentPlatform = Model_Type.N910;
		
		public static Platform_Ver gCurPlatVer = Platform_Ver.A5;// add by 20200317 zhengxq
		
		// 蓝牙配对结果
		public static ParaEnum.Pair_Result pairResult=Pair_Result.BOND_NONE;
		
		// 测试退出点
		public static boolean isInterrupt = false;
		
		// 是否显示返回键的测试点
		public static boolean isShowBack = false;
		
		//验签方案的集合
		public static String[] signatureList;
		
		//蓝牙底座是否开启认证控制，默认开启认证
		public static int Auth_Control = 1;
		
		// 按键值
		public static int g_nkeyIn = 0;
		
		// 客户识别码 默认为银商版本 add by 20171110
		public static ParaEnum.CUSTOMER_ID gCustomerID = CUSTOMER_ID.SDK_2;
		
	    // CN证书名  add by wangxy20181010
	    public static String gCN = "";
		
		public static boolean isWifiNode = false;
		//sdk2.0或3.0
		public static SdkType sdkType=SdkType.SDK2;
		
		//各机型的各k21模块的支持情况
		public static Map<Mod_Enable,Boolean> gModuleEnable=new HashMap<Mod_Enable,Boolean>();
		
//		public static List<String> gAllK21Module = new ArrayList<String>();
		
		//测试双通道蓝牙底座回连标志位bywangxy20181128
		public static boolean isBluetoothReboot=false;


		public static boolean BtAbility=false;
}
