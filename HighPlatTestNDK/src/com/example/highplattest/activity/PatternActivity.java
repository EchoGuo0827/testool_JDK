package com.example.highplattest.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.highplattest.R;
import com.example.highplattest.main.FragmentCollector;
import com.example.highplattest.main.XmlResourceParserTool;
import com.example.highplattest.main.bean.PointBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.Platform_Ver;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.newland.ndk.security.NdkSecurityManager;
import android.newland.os.NlBuild;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
/************************************************************************
 * history 		 	: 变更记录							date			remarks
 *			  		 添加是否支持虚拟状态栏功能的标志位		   20200426     	郑薛晴
 ************************************************************************/
public class PatternActivity extends Activity implements OnClickListener
{
	static {
		Log.d("eric", "加载TLK------------");
		System.loadLibrary("LoadTlk");
	}
	private Button btnAuto;// 自动测试
	private Button btnHand;// 手动测试
	private Button btnAutoHand;// 手自动测试
	private final String TAG = "PatternActivity";
	//k21端密钥索引文件路径
	public final String Key_Idx_File="/appfs/ak_main.idx";
	public boolean isErr=false;
	Handler myHandler = new Handler()
	{
		@SuppressLint("ShowToast")
		public void handleMessage(android.os.Message msg) 
		{
			switch (msg.what) 
			{
			case 0:
				// 自动化操作
				Toast.makeText(getApplicationContext(), "设备安全触发，请清安全后再执行", Toast.LENGTH_SHORT).show();
				break;

			case 1:
				PatternActivity.this.finish();
				Intent intent = new Intent(PatternActivity.this, MainActivity.class);
				startActivity(intent);
				break;
				
			case 2:// 手自动
				pushDialog();
				break;
				
			case 4:
				// 因为复制文件的过程很耗时，所以要将按钮设置为不可点击，拷贝完毕后再显示可点击 modify by zhengxq 20171101
				btnAuto.setClickable(false);
				btnAutoHand.setClickable(false);
				btnHand.setClickable(false);
				Toast.makeText(PatternActivity.this, "正在复制测试文件，请耐心等待", Toast.LENGTH_LONG).show();
				break;
				
			case 3:
				btnAuto.setClickable(true);
				btnAutoHand.setClickable(true);
				btnHand.setClickable(true);
				Toast.makeText(PatternActivity.this, "测试文件已复制完毕", Toast.LENGTH_LONG).show();;
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.model_choose_layout);
		if(Tools.getNdkStatus(this))
		{
			PatternActivity.this.finish();
			Tools.setNdkStatus(this, false);
			return;
		}
		myHandler.sendEmptyMessage(2);
		btnAuto = (Button) findViewById(R.id.btn_pattern_auto);
		btnHand = (Button) findViewById(R.id.btn_pattern_hand);
		btnAutoHand = (Button) findViewById(R.id.btn_pattern_auto_hand);
		btnAuto.setOnClickListener(this);
		btnHand.setOnClickListener(this);
		btnAutoHand.setOnClickListener(this);
		initPlatVer();
		initModelType();
		initK21Enable();
		initX3();
	}
	
	private void initPlatVer()
	{
		GlobalVariable.gModuleEnable.put(Mod_Enable.IsForth, false);//Forth默认设置为false
		String version = android.os.Build.VERSION.RELEASE;
		LoggerUtil.d("002,ver="+version);
		// A7和A9平台是不支持mpos的
		if(version.startsWith("9"))
		{
			GlobalVariable.gCurPlatVer = Platform_Ver.A9;
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos,false);
		}
		else if(version.startsWith("7"))
		{
			GlobalVariable.gCurPlatVer = Platform_Ver.A7;
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos,false);
		}
		else if(version.startsWith("5"))
		{
			GlobalVariable.gCurPlatVer = Platform_Ver.A5;
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos,true);
		}
			
	}
	
	private void initX3() {
		String systemVersion = android.os.Build.MODEL;
		if(systemVersion.equals("CPOS X3")){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("X3是否支持安全模块？");
			dialog.setPositiveButton("是", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					GlobalVariable.gModuleEnable.put(Mod_Enable.SecEnable, true);
					Log.d("eric_chen", "X3-安全模块");
				}
			});
			dialog.setNegativeButton("否", new DialogInterface.OnClickListener() 
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					GlobalVariable.gModuleEnable.put(Mod_Enable.SecEnable, false);
//					GlobalVariable.currentPlatform = Model_Type.X3;
					Log.d("eric_chen", "X3-无安全模块");

				}
			});
			dialog.setCancelable(false);
			dialog.show();
		}
	}
	
	//根据机型配置支持的k21模块 add by wangxy 20180523
	private void initK21Enable() 
	{ 	
		Log.d("eric", "初始化---initK21Enable");
		GlobalVariable.gModuleEnable.put(Mod_Enable.IccEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.SamEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.MagEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.RfidEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.PinEnable, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnableReg, true);
		GlobalVariable.gModuleEnable.put(Mod_Enable.KeyBoardEnable,false);
		GlobalVariable.gModuleEnable.put(Mod_Enable.CutPaper, false);// 切纸默认是false
		
		//除了N910、N900 4g、N900 3g的机型，其他新机型的密钥属主都是存在与Android端
		GlobalVariable.gModuleEnable.put(Mod_Enable.DomestProduct, true);// 国内产品还是海外产品
		GlobalVariable.gModuleEnable.put(Mod_Enable.IsPoynt, false);//Poynt的设置默认是false
		// Android端的支持情况
		GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, false);// 钱箱功能
		GlobalVariable.gModuleEnable.put(Mod_Enable.PinPad, false);// Pinpad串口,废弃全部都不用测试20200710
		GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, false);// RS232串口
		GlobalVariable.gModuleEnable.put(Mod_Enable.PinPad, false);// PinPad串口
		GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, true);// 电池模块
		GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, false);// 以太网模块
		GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, true);// 是否物理按键
		
		GlobalVariable.gModuleEnable.put(Mod_Enable.SecEnable, true);
		
		
		switch (GlobalVariable.currentPlatform) {
		case N550:
			GlobalVariable.gModuleEnable.put(Mod_Enable.IccEnable, false);
//			GlobalVariable.gModuleEnable.put(Mod_Enable.SamEnable, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.MagEnable, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinEnable, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			break;
			
		case N510:
			GlobalVariable.gModuleEnable.put(Mod_Enable.IccEnable, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.MagEnable, false);
			break;
			
		case N700_A7:
		case N700:// 700设备是没有打印
			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnable, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnableReg, false);
			break;
			
		case X5:
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinEnable, false);//根据长威反馈，X5不支持密码键盘addbywangxy20181129
//			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnableReg, false);// X5不支持打印事件机制，X5在V1.0.33导入打印事件机制
			GlobalVariable.gModuleEnable.put(Mod_Enable.CutPaper, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinPad, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 支持以太网
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			break;
			
		case X3:// X3只有Android端 没有K21端
			//X3Sec:True 代表支持安全模块  false代表不支持
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinPad, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 以太网
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnable, false);//X3无打印模块
			break;
			
		case X1:
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 以太网模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnable, false);//X1无打印模块
			break;
		
		case IM81_Old:
		case IM81_New:
			GlobalVariable.gModuleEnable.put(Mod_Enable.KeyBoardEnable,true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);//以太网模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			break;
			
		
		case N900_4G:
		case N900_3G:
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
		case N910_A7:// 因为N910和N900密钥属主有可能存在K21端，所以需要判断
		case N910:
			int isk21=isInK21();
			if(isk21==-1){
				Toast.makeText(PatternActivity.this,"操作k21文件异常,5s后即将结束本应用，请查看result文件", Toast.LENGTH_LONG).show();
				isErr=true;
				return;
			}
			GlobalVariable.gModuleEnable.put(Mod_Enable.SecAndroidEnable, isk21==1?false:true);
			break;
			
			
		case N920_A7:
		case N920:
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			break;
			
		case CPOS_X5_Poynt:
			GlobalVariable.gModuleEnable.put(Mod_Enable.CutPaper, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, false);// Poynt不支持newland.jar的接口
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinPad, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 以太网模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			break;
			
		case N910_Poynt:
			GlobalVariable.gModuleEnable.put(Mod_Enable.DomestProduct, false);// 海外不支持在Android端设置KeyOwner
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos,false);// Poynt不支持mpos
			GlobalVariable.gModuleEnable.put(Mod_Enable.IsPoynt, true);// Poynt产品
			break;
			
		case N850:
		case N850_A7:
			GlobalVariable.gModuleEnable.put(Mod_Enable.CashBox, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 以太网模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			break;
			
		case F7:
		case F10:
			GlobalVariable.gModuleEnable.put(Mod_Enable.RS232, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.Battery, false);//F7和F10无电池模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.PinEnable, true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.SecAndroidEnable,true);
			GlobalVariable.gModuleEnable.put(Mod_Enable.EthEnable, true);// 以太网模块
			GlobalVariable.gModuleEnable.put(Mod_Enable.isPhysicalBoard, false);
			GlobalVariable.gModuleEnable.put(Mod_Enable.PrintEnable, false);//F7和F10无打印模块
			break;

		default:
			break;
		}
		
		
	}
	//判断旧机型密钥是否存在k21端
		public int isInK21() {
			final Gui gui = new Gui();
			int ret=-1,size=-1,fd=-1;
			int isK21=1;
			// 文件不存在或文件大小<=0,则密钥不存在k21端
			if ((ret = JniNdk.JNI_SP_FsExist(Key_Idx_File)) != 0|| (size = JniNdk.JNI_SP_FsFileSize(Key_Idx_File)) <= 0) 
			{
				isK21 = 0;
				LoggerUtil.e("k21端密钥索引文件不存在或文件大小<=0(ret="+ret+",filesize="+size+")");
			} else 
			{
				if (size % 8 != 0) //一条密钥记录为8字节（keyid4字节+偏移量4字节）
				{
					gui.cls_only_write_msg(TAG, "isInK21", "line %d:k21端密钥索引文件数据异常，一条密钥数据为8字节(filesize=%d)", Tools.getLineInfo(), size);
					LoggerUtil.e("k21端密钥索引文件数据异常，一条密钥数据为8字节(filesize="+size+")");
					return -1;
				} else if (size / 8 == 1) // 只有一条密钥则需判断是否是TLK密钥，即8字节中的前4个字节（从低位到高位）是否是1
				{
					if ((fd = JniNdk.JNI_SP_FsOpen(Key_Idx_File, "r")) < 0) 
					{
						gui.cls_only_write_msg(TAG, "isInK21", "line %d:打开k21密钥索引文件失败,fd= %d)", Tools.getLineInfo(), fd);
						LoggerUtil.e("打开k21密钥索引文件失败,fd=" + fd);
						return -1;
					} else {
						byte[] readbuf = new byte[size];
						if ((ret = JniNdk.JNI_SP_FsRead(fd, readbuf, size)) < 0) 
						{
							gui.cls_only_write_msg(TAG, "isInK21", "line %d:读k21密钥索引文件失败,ret= %d)", Tools.getLineInfo(), ret);
							LoggerUtil.e("readk21密钥索引文件失败,ret=" + ret);
							return -1;
						} else {
							// 判断读到的文件内容
//							LoggerUtil.e("readbuf=" + ISOUtils.hexString(readbuf).trim());
							byte[] readbuf2 = new byte[4];
							System.arraycopy(readbuf, 0, readbuf2, 0, 4);
							//LoggerUtil.e("readbuf2=" + ISOUtils.hexString(readbuf2).trim());
							int keyid=ISOUtils.bytesToInt(readbuf2, 0, 4, false);
							LoggerUtil.e("转化为十进制keyid="+keyid);
							if(keyid==1)//keyId=1则表示该密钥为TLK密钥
								isK21 = 0;
						}
						if ((ret = JniNdk.JNI_SP_FsClose(fd)) != 0) {
							gui.cls_only_write_msg(TAG, "isInK21", "line %d:关闭k21密钥索引文件失败,ret= %d)", Tools.getLineInfo(), ret);
							LoggerUtil.e("关闭k21文件失败,ret=" + ret);
							return -1;
						}
					}
				}
			}
			LoggerUtil.e("isK21="+isK21);
			return isK21;
		}
	@Override
	protected void onRestart() 
	{
		super.onRestart();
	}

	
	@Override
	protected void onStart() {
		super.onStart();
		Timer timer=new Timer();
		TimerTask task=new TimerTask(){

			@Override
			public void run() {
				if(isErr){
					PatternActivity.this.onDestroy();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			
		};
		timer.schedule(task, 5*1000);		
	}

	@Override
	public void onClick(View v) 
	{
		PointBean point = getScreenArea();
		GlobalVariable.ScreenWidth = point.getX();
		GlobalVariable.ScreenHeight = point.getY();
		getContentArea();			
		switch (v.getId()) 
		{
		case R.id.btn_pattern_auto:
			GlobalVariable.gAutoFlag= AutoFlag.AutoFull;
			NdkSecurityManager ndkSecurityManager = new NdkSecurityManager();
			int[] status = new int[2];
			ndkSecurityManager.getSecTamperStatus(status);
			if(status[0]!=0){
				myHandler.sendEmptyMessage(0);
				return;
			}
			else
			{
				Toast.makeText(getApplicationContext(), "暂时不支持自动测试", Toast.LENGTH_SHORT).show();
//				autoTest();	
				return;
			}
			
		case R.id.btn_pattern_hand:// 手动测试
			GlobalVariable.gAutoFlag= AutoFlag.HandFull;
			break;
			
		case R.id.btn_pattern_auto_hand:// 手自动测试
//			GlobalVariable.AUTOHANDFLAG = 0;	
			break;
		}
		Intent autoIntent = new Intent(PatternActivity.this, MainActivity.class);
		startActivity(autoIntent);
	}
	
	// 获取屏幕的宽高（不包括虚拟按键部分）
	@SuppressLint("NewApi")
	public PointBean getScreenArea() 
	{
		PointBean point = new PointBean();
		android.graphics.Point point2 = new android.graphics.Point();
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(point2);
		point.setX(point2.x);
		point.setY(point2.y);
		return point;
	}
	
	public int getContentArea() 
	{
		Rect rect = new Rect();
		Window window = this.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		// 状态栏高度
		int statusBarHeight = rect.top;
		GlobalVariable.StatusHeight = statusBarHeight;
		// 标题栏+状态栏高度
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		
		// 标题栏高度
		int contentViewHeight = contentViewTop - statusBarHeight;
		
		LoggerUtil.e("contentHight:"+contentViewHeight);
		GlobalVariable.TitleBarHeight = getBottomKeyboardHeight();
		LoggerUtil.e("height:"+GlobalVariable.TitleBarHeight);
		return statusBarHeight;
	}
	
	// 获取屏幕的实际高度
	public int getDpi(){
	    int dpi = 0;
	    WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
	    Display display = windowManager.getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    @SuppressWarnings("rawtypes")
	    Class c;
	    try 
	    {
	        c = Class.forName("android.view.Display");
	        @SuppressWarnings("unchecked")
	        Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
	        method.invoke(display, displayMetrics);
	        dpi=displayMetrics.heightPixels;
	    }catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	    return dpi;
	}
	
	public int getBottomKeyboardHeight(){
		int screenHeight = getAccurateScreenDpi()[1];
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int heightBottom = screenHeight-dm.heightPixels;
		return heightBottom;
	}
	

/** 
 * 获取精确的屏幕大小 
 */  
public int[] getAccurateScreenDpi()  
       {     
    int[] screenWH = new int[2];  
           Display display = getWindowManager().getDefaultDisplay();  
           DisplayMetrics dm = new DisplayMetrics();  
           try {  
               Class<?> c = Class.forName("android.view.Display");  
               Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);  
               method.invoke(display, dm);  
               screenWH[0] = dm.widthPixels;  
               screenWH[1] = dm.heightPixels;  
            }catch(Exception e){  
               e.printStackTrace();  
            }    
            return screenWH;  
   }

	
	/**
	 * 用例组合
	 */
	public void autoTest() {
		//自动测试， 连续开关压力改成true
		GlobalVariable.gSequencePressFlag=true;
		// 清除原先的数据
		FragmentCollector.finishAll();
		try {
			List<String> packName = new ArrayList<String>();
//			List<String> packNum = new ArrayList<String>();
			
			// 模块名
			packName = XmlResourceParserTool.XmlResourceParser(getAssets().open(GlobalVariable.currentPlatform+"_module_list.xml"), "name");
			
			
			// // 用例号，这边需要对用利好拆解
			// packNum =
			// XmlResourceParserTool.XmlResourceParser(getAssets().open("module_list.xml"),
			// "all");
			String packageName = this.getPackageName();
			// 排除综合模块
			// 目前还未添加综合模块
			// 需要添加每个模块的开始，好在测试报告显示具体的模块
			for (int i = 0; i < packName.size(); i++) {
				String moduleName = packName.get(i).substring(packName.get(i).indexOf("(") + 1,packName.get(i).indexOf(")"));
				// 添加所有要测试的模块名
				FragmentCollector.addAutoModule(packageName + "."
						+ moduleName.toLowerCase() + "." + moduleName);
				// String[] caseNum = packNum.get(i).split(",");
				// FragmentCollector.addFragmentName(packageName+"."+caseName.toLowerCase()+
				// "." + caseName, caseNum);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	// 初始化设备类型
	private void initModelType()
	{
		// 直接从设备的boot版本中获取对应设备
		String systemVersion = android.os.Build.MODEL;
		String user = android.os.Build.USER;
		LoggerUtil.e("Model="+systemVersion+"|||user="+user);
		if(systemVersion.contains("N900"))
		{
			// n900的产品需要细分为N900_3G以及N900_4G
			String softVersion = NlBuild.VERSION.NL_FIRMWARE.substring(1, 2);
			Log.d("eric_chen", "版本号: "+NlBuild.VERSION.NL_FIRMWARE);
			//N900_3G配置文件删除。
			if(softVersion.equals("2"))
				GlobalVariable.currentPlatform = Model_Type.N900_4G;
		}
		else if(systemVersion.contains("N910"))
		{
			if(user.contains("hey")||user.contains("jenkins"))/**jenkins是Poynt编译的，hey是何云编译的 20200424*/
			{
				GlobalVariable.currentPlatform = Model_Type.N910_Poynt;
			}
			else if(GlobalVariable.gCurPlatVer==Platform_Ver.A7)
			{
				GlobalVariable.currentPlatform = Model_Type.N910_A7;
				GlobalVariable.gModuleEnable.put(Mod_Enable.IsForth, true);/**N910_A7是forth平台*/
			}
			else
				GlobalVariable.currentPlatform = Model_Type.N910;
		}
		else if(systemVersion.equals("IM81"))
			GlobalVariable.currentPlatform = Model_Type.IM81_New;
		else if(systemVersion.contains("N700"))
		{
			if(GlobalVariable.gCurPlatVer==Platform_Ver.A7)
			{
				GlobalVariable.currentPlatform = Model_Type.N700_A7;
				GlobalVariable.gModuleEnable.put(Mod_Enable.IsForth, true);/**N700_A7是forth平台*/
			}
			else
				GlobalVariable.currentPlatform = Model_Type.N700;
		}
		else if(systemVersion.contains("N920"))
		{
			switch (GlobalVariable.gCurPlatVer) {
			case A7:
				GlobalVariable.currentPlatform=Model_Type.N920_A7;
				break;
				
			case A5:
				GlobalVariable.currentPlatform=Model_Type.N920;
				return;
			}
			GlobalVariable.gModuleEnable.put(Mod_Enable.IsForth, true);/**N920_A7和N920_A9是forth平台*/
		}
		else if(systemVersion.contains("N850")){
			if(GlobalVariable.gCurPlatVer==Platform_Ver.A7)
			{
				GlobalVariable.currentPlatform = Model_Type.N850_A7;
				GlobalVariable.gModuleEnable.put(Mod_Enable.IsForth, true);/**N850_A7是forth平台*/
			}
			else{
				GlobalVariable.currentPlatform = Model_Type.N850;
			}
			
		}
		else if(systemVersion.equals("CPOS X5")||systemVersion.equals("STAR A-6300"))// 临时给测试人员使用
			GlobalVariable.currentPlatform = Model_Type.X5;
		else if(systemVersion.equals("CPOS X3")){
			GlobalVariable.currentPlatform = Model_Type.X3;
		}
		else if(systemVersion.contains("N510"))
			GlobalVariable.currentPlatform = Model_Type.N510;
		else if(systemVersion.contains("N550"))
			GlobalVariable.currentPlatform = Model_Type.N550;
		else if(systemVersion.equals("X5_Poynt"))
			GlobalVariable.currentPlatform = Model_Type.CPOS_X5_Poynt;
		else if(systemVersion.equals("FPOS F7"))
			GlobalVariable.currentPlatform = Model_Type.F7;
		else if(systemVersion.equals("FPOS F10"))
			GlobalVariable.currentPlatform = Model_Type.F10;
//		else if(systemVersion.equals("CPOS-X5-Poynt"))
//			GlobalVariable.currentPlatform = Model_Type.CPOS_X5_Poynt;
		else if(systemVersion.equals("CPOS X1"))
			GlobalVariable.currentPlatform = Model_Type.X1;
		else{
//			GlobalVariable.currentPlatform = Model_Type.IM81_Old;// 老81版本 配置文件被删除，导致测试人员测试若版本不对应，案例为空。
			GlobalVariable.currentPlatform = Model_Type.N920_A7;
			
		}

		
		LoggerUtil.d("currentPlatform="+GlobalVariable.currentPlatform.toString());
	}
	
	ExecutorService exec = Executors.newFixedThreadPool(20); 
	/**
	 * 放置测试文件到对应的路径
	 */
	public void pushDialog()
	{
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("已导入过测试文件选[是],未导入过测试文件选[否]并将服务器上的测试文件放置到内置sdcard的testFile目录下");
		dialog.setPositiveButton("是", new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton("否", new DialogInterface.OnClickListener() 
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
				myHandler.sendEmptyMessage(4);
				CopyFileTask copyFileTask = new CopyFileTask();
				copyFileTask.executeOnExecutor(exec);

			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}
	
	/**复制文件任务*/
	class CopyFileTask extends AsyncTask<Integer, Integer, String>{
		

		@Override
		protected void onPreExecute() {
		}
		
		@Override
		protected String doInBackground(Integer... values) {
			String sdPath = Environment.getExternalStorageDirectory().getPath();
			copy(sdPath+"/testFile/LCD picture/",sdPath+"/share/");
			copy(sdPath+"/testFile/picture/", sdPath+"/picture/");
			copyFile(sdPath+"/testFile/picture/ums", "/data/share/ums");
			copy(sdPath+"/testFile/test apk/",sdPath+"/apk/");
			copy(sdPath+"/testFile/scan/",sdPath+"/scan/");
			copy(sdPath+"/testFile/payment_900/",sdPath+"/");
			copyFile(sdPath+"/testFile/configpara.json",sdPath+"/configpara.json");// 自动化的参数配置文件
	        return null;
		}
		
		@Override
	    protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
	        int progress = values[0];
	        Log.i("RfidRegisterEventTask", "RfidRegisterEventTask onProgressUpdate progress---->" + progress);
		}
		
		@Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        myHandler.sendEmptyMessage(3);
	    }
	}
	
	// 复制测试文件到sdcard的操作，因为后期的文件权限提高了 add by zhengxq 20171101
    public int copy(String fromFile, String toFile)  
    {  
        //要复制的文件目录  
        File[] currentFiles;  
        File root = new File(fromFile);  
        //如同判断SD卡是否存在或者文件是否存在  
        //如果不存在则 return出去  
        if(!root.exists())  
        {  
            return -1;  
        }  
        //如果存在则获取当前目录下的全部文件 填充数组  
        currentFiles = root.listFiles();  
           
        //目标目录  
        File targetDir = new File(toFile);  
        //创建目录  
        if(!targetDir.exists())  
        {  
            targetDir.mkdirs();  
        }  
        //遍历要复制该目录下的全部文件  
        for(int i= 0;i<currentFiles.length;i++)  
        {  
            if(currentFiles[i].isDirectory())//如果当前项为子目录 进行递归  
            {  
                copy(currentFiles[i].getPath() + "/", toFile + currentFiles[i].getName() + "/");  
                   
            }else//如果当前项为文件则进行文件拷贝  
            {  
            	copyFile(currentFiles[i].getPath(), toFile + currentFiles[i].getName());  
            }  
        }  
        return 0;  
    }
    
	/**  
	 * 复制单个文件  
	 * @param oldPath String 原文件路径 如：c:/fqf.txt  
	 * @param newPath String 复制后路径 如：f:/fqf.txt  
	 * @return boolean  
	 */   
    public void copyFile(String oldPath, String newPath) 
    {   
    	try {   
    		int bytesum = 0;   
    		int byteread = 0;   
    		File oldfile = new File(oldPath);  
    		File newFile = new File(newPath);
    		if(newFile.exists()==false)
    			newFile.createNewFile();
    		if (oldfile.exists()) 
    		{ //文件存在时   
    			InputStream inStream = new FileInputStream(oldPath); //读入原文件   
    			FileOutputStream fs = new FileOutputStream(newPath);   
    			byte[] buffer = new byte[1444];   
    			int length;   
    			while ( (byteread = inStream.read(buffer)) != -1) 
    			{   
    				bytesum += byteread; //字节数 文件大小   
//    				System.out.println(bytesum);   
    				fs.write(buffer, 0, byteread);   
    			}   
    			inStream.close();   
    		}   
    	}   
    	catch (Exception e) 
    	{   
    		System.out.println("复制单个文件操作出错");   
    		e.printStackTrace();   
    	}   
    }
    
    /**
     * 获取硬件配置码第13.14位判断是否支持安全模块（暂用于判断X3）by chending 20191212
     */
    public boolean  secConfig(){
		String dirName = "/newland/factory/DetectionAppDir/hardwareconifg.xml";
		File file = new File(dirName);
		//如果不存在
		if (!file.exists()) {
			Log.d("eric_chen", "硬件配置码文件不存在-----");
			return false;
		}else{
			try {
				  DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   
				  DocumentBuilder builder;
				  builder = factory.newDocumentBuilder();
				  Document document = builder.parse(file);   
				  NodeList hardwareInfo = document.getElementsByTagName("string");
				  Node hardwareId = hardwareInfo.item(0);
			      String hardwareString=hardwareId.getChildNodes().item(0).getTextContent();
			      String  secString=hardwareString.substring(8, 10);
				
			}catch(Exception e){
				Log.d("eric_chen","硬件配置码文件读取异常-----");
				e.printStackTrace();
				
			}
		}
    	
    	
		return true;
    	
    }
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		this.finish();
	}
}
