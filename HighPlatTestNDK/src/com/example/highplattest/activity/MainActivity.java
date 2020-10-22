package com.example.highplattest.activity;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.newland.os.NlBuild;
import android.newland.security.CertificateInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import com.example.highplattest.R;
import com.example.highplattest.main.FragmentCollector;
import com.example.highplattest.main.XmlResourceParserTool;
import com.example.highplattest.main.adapter.ViewAdapter;
import com.example.highplattest.main.bean.BpsBean;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.Lib;
import com.example.highplattest.main.constant.ParaEnum.AutoFlag;
import com.example.highplattest.main.constant.ParaEnum.CUSTOMER_ID;
import com.example.highplattest.main.constant.ParaEnum.EM_ICTYPE;
import com.example.highplattest.main.constant.ParaEnum.Mod_Enable;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.constant.ParaEnum.SdkType;
import com.example.highplattest.main.tools.BaseDialog;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.BaseDialog.OnDialogButtonClickListener;

public class MainActivity extends Activity implements Lib, PreviewCallback
{
	
	private ListView mLvContent;
	private List<String> typeName = new ArrayList<String>();
	IntentFilter intent = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private final String TAG = "MainActivity";
	private List<String> mGroupList;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		if(Tools.getNdkStatus(this))
		{
			MainActivity.this.finish();
			return;
		}
		// 必须在setContentView之前调用
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_lv);/**使用ListView的布局方式*/
		initPara();
		if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt)==false)// 非Poynt产品
		{
			Log.d("cd", "init_CN++++init_CustomerID");
			init_CustomerID();
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsForth)||GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct)==false)
			{
				GlobalVariable.gModuleEnable.put(Mod_Enable.isPCI, true);
			}
			else
			{
				GlobalVariable.gModuleEnable.put(Mod_Enable.isPCI, false);
			}
			init_CN();
		}
		String moduleConfigName="";// 产品配置文件
		try 
		{
			if(GlobalVariable.gModuleEnable.get(Mod_Enable.DomestProduct))// 国内
			{
				moduleConfigName = GlobalVariable.currentPlatform+"";
			}
			else// 海外
			{
				if(GlobalVariable.gModuleEnable.get(Mod_Enable.IsPoynt))
					moduleConfigName = GlobalVariable.currentPlatform+"_Poynt";
				else
					moduleConfigName = GlobalVariable.currentPlatform+"_G";
			}
			LoggerUtil.v("配置文件="+moduleConfigName+"_module_list.xml");
			//sdk类型 SDK3.0
			typeName = XmlResourceParserTool.XmlResourceParser(getAssets().open(moduleConfigName+"_module_list.xml"), "type");
		  
			if(typeName.contains("SDK3"))
		    	GlobalVariable.sdkType=SdkType.SDK3;
		    else
		    	GlobalVariable.sdkType=SdkType.SDK2;
			
		    Toast.makeText(MainActivity.this,"xml文件中sdk类型配置为:"+GlobalVariable.sdkType, Toast.LENGTH_SHORT).show();
		    
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		initView(moduleConfigName);
		
		// 自动测试
		if(GlobalVariable.gAutoFlag==AutoFlag.AutoFull)
		{
			for(String strName:mGroupList)
			{
				int index_start = strName.indexOf("(");
				int index_end = strName.indexOf(")");
				String clsName = strName.substring(index_start+1, index_end);
				String mClsName = "com.example.highplattest."+clsName.toLowerCase()+"."+clsName;
				String all = null;
				try {
					all = XmlResourceParserTool.getModuleContent(getAssets().open(moduleConfigName+"_module_list.xml"), strName, "all");
				} catch (IOException e) {
					e.printStackTrace();
				}
				String[] testNum = all.split(",");
				for(String i:testNum)
				{
					FragmentCollector.addFragmentSingleName(mClsName+i);
				}
			}
			// 用反射实例化第一个模块
			Intent autoIntent = new Intent(MainActivity.this, IntentActivity.class);
			startActivity(autoIntent);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LoggerUtil.v("MainActivity=onPause");
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void initView(final String moduleConfigName)
	{
		mLvContent = (ListView) findViewById(R.id.lv_main);
		// 读文件的耗时操作要放到线程里
		readConf(moduleConfigName);
		mLvContent.setAdapter(new ViewAdapter(mGroupList, this));
		mLvContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				LoggerUtil.d("itemClick="+position);
				// 跳转到单元用例的CaseActivity
				Intent caseIntent = new Intent(MainActivity.this, CaseActivity.class);
				caseIntent.putExtra("TESTAPI", mGroupList.get(position));
				caseIntent.putExtra("moduleConfig", moduleConfigName);
				// 如果是AndroidPort或者是PaymentPort模块USBComm要进行选波特率
				if(mGroupList.get(position).contains("AndroidPort")||mGroupList.get(position).contains("PaymentPort")||mGroupList.get(position).contains("UsbComm"))
				{
					showBps(mGroupList.get(position),moduleConfigName);
					return;
				}
				// 如果是mdm模块要进行号码的配置
				startActivity(caseIntent);
			}
		});
	}
	
	private void readConf(String moduleConfigName)
	{
		try {
			mGroupList = XmlResourceParserTool.XmlResourceParser(getAssets().open(moduleConfigName+"_module_list.xml"), "name");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 部分参数的设置，比如说SD的路径，触屏分辨率等
	 */
	public Model_Type initPara()
	{	
		GlobalVariable.sdPath = Environment.getExternalStorageDirectory().getPath()+"/";
//		GlobalVariable.TFPath = "/storage/sdcard1/";
		// n9x0系列
		switch (GlobalVariable.currentPlatform) 
		{
		case IM81_New:
		case IM81_Old:
		case N700:
		case N700_A7:
		case N850:
		case N850_A7:
			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_SAM2);// N700和N850支持双SAM卡
		case N900_3G:
		case N900_4G:
		case N910:
		case N920:
		case N920_A7:
		case N910_Poynt:
		case N910_A7:
			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_IC);
		case N510:
			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_SAM1);
		case N550:
			break;
			
		//x5无法获取屏幕分辨率 暂时写死 zhangxinj 20180314
		case X5:
		case CPOS_X5_Poynt:
			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_IC);
			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_SAM1);
//			GlobalVariable.cardNo.add(EM_ICTYPE.ICTYPE_SAM2);
//			GlobalVariable.TouchHeight=1024;
//			GlobalVariable.TouchWidth=600;
//			GlobalVariable.sdPath = "/storage/emulated/0/";
			//TF盘路径改成跟910一致 zhangxinj 2018/12/25
//			GlobalVariable.TFPath = "/storage/sdcard1/";
			//U盘路径随机读取U盘的芯片ID号。zhangxinj 2018/12/25 不在此判断 
//			GlobalVariable.uPath = "";
			break;
		
			
		default:
			break;
		}
		return GlobalVariable.currentPlatform;
	}
	
	//初始化客户识别码
	private void init_CustomerID()
	{
		String customerID;
		// 客户识别码能够判断是哪一个客户的固件  导入版本为银商的T2.1.33
		try {
			customerID = NlBuild.CUSTOMER_ID;
		} catch (NoSuchFieldError e) {
			// 找不到CUSTOMER_ID
			return;
		}
		
		LoggerUtil.d(TAG+",init_CustomerID:"+customerID);
		
		switch (customerID) {
		case "CCB":// 建行固件
			GlobalVariable.gCustomerID = CUSTOMER_ID.CCB;
			break;
			
		case "ABC":// 农行固件
			GlobalVariable.gCustomerID = CUSTOMER_ID.ABC;
			break;
			
		case "ChinaUms":// 银商版本
			GlobalVariable.gCustomerID = CUSTOMER_ID.ChinaUms;
			break;
			
		case "SDK_2.0":
			GlobalVariable.gCustomerID = CUSTOMER_ID.SDK_2;
			break;
			
		case "SDK_3.0":
			GlobalVariable.gCustomerID = CUSTOMER_ID.SDK_3;
			break;
			
		case "Lakala":
			GlobalVariable.gCustomerID = CUSTOMER_ID.Lakala;
			break;
			
		case "overseas":
			GlobalVariable.gCustomerID = CUSTOMER_ID.overseas;
			GlobalVariable.gModuleEnable.put(Mod_Enable.DomestProduct, false);// 海外版本不支持设置Android端的KeyOwner
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos, false);
			break;
			
		case "MeiTuan":// 美团固件
			GlobalVariable.gCustomerID = CUSTOMER_ID.MeiTuan;
			break;
			
		case "Brasil":
			GlobalVariable.gCustomerID=CUSTOMER_ID.BRASIL;
			GlobalVariable.gModuleEnable.put(Mod_Enable.DomestProduct, false);// 海外版本不支持设置Android端的KeyOwner
			GlobalVariable.gModuleEnable.put(Mod_Enable.SupportMpos, false);
			break;
			
		case "KouBei":
			GlobalVariable.gCustomerID=CUSTOMER_ID.KouBei;
			break;
			
		case "PSBC":
			GlobalVariable.gCustomerID=CUSTOMER_ID.PSBC;
			break;
			
		default:
			GlobalVariable.gCustomerID = CUSTOMER_ID.unkown;
			break;
		}
		
	}

	// 初始化证书名CN,默认为""
	private void init_CN() 
	{
	    CertificateInfo certificateInfo = new CertificateInfo(MainActivity.this);
	    X509Certificate  x509=certificateInfo.getCertificateInfo();
	    String[] info=null;
	    if (x509 != null) 
	    {
		    info = x509.getSubjectDN().getName().split(",");
		    for (int i = 0; i < info.length; i++) 
		    {
			    if (info[i].contains("CN=")) 
			    {
			    	GlobalVariable.gCN= info[i].substring(3);
			    }
		    }
	    }
	    LoggerUtil.e("CN="+GlobalVariable.gCN);
	}	
	// 设计显示波特率的对话框
	public void showBps(final String testAPI,final String moduleConfigName)
	{
		final int[] bps = { 300, 1200, 2400, 4800, 9600, 19200, 38400, 57600, 115200, 230400 };
		ArrayList<String> bpsString=new ArrayList<String>();
		for(int i=0;i<bps.length;i++)
			bpsString.add(bps[i]+"");
		GridView gridView = new GridView(this);
		// 设置为3列
		gridView.setNumColumns(3);
		gridView.setBackgroundColor(Color.WHITE);
		gridView.setAdapter(new ViewAdapter(bpsString,this));
		final BaseDialog dialog=new BaseDialog(this, gridView, "波特率选择", "取消", new OnDialogButtonClickListener(){

			@Override
			public void onDialogButtonClick(View view, boolean isPositive) {
			}
			
		});
		dialog.show();
		
		gridView.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) 
			{
				BpsBean.bpsValue = bps[position];
				dialog.dismiss();
				Intent caseIntent = new Intent(MainActivity.this, CaseActivity.class);
				caseIntent.putExtra("moduleConfig", moduleConfigName);
				caseIntent.putExtra("TESTAPI", testAPI);
				startActivity(caseIntent);
			}
		});
	}
	
	
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		LoggerUtil.v("MainActivity=onDestroy");
		this.finish();
	}
	
	int batCurrent;
	
	
	// 重写返回键来中断测试用例，重写home键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		// 获取返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			
			new BaseDialog(MainActivity.this, "测试中断", "真的要退出测试吗？", "是", "否", new OnDialogButtonClickListener() {
				
				@Override
				public void onDialogButtonClick(View view, boolean isPositive) {
					if(isPositive){
						MainActivity.this.finish();
					}
					
				}
			}).show();
			
		}
		return true;
	}

//	@Override
//	public void onPointerCaptureChanged(boolean hasCapture) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		
	}
}
