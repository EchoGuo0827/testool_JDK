package com.example.highplattest.printer;

import java.io.File;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.ParaEnum;
import com.example.highplattest.main.constant.ParaEnum.EM_PRN_STATUS;
import com.example.highplattest.main.constant.ParaEnum.Model_Type;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.PrintUtil;
import com.example.highplattest.main.tools.TestFileJudge;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;
/************************************************************************
 * 
 * module 			: 打印类
 * file name 		: Printer6.java 
 * description 		: X5打印机类型
 * history 		 	: author			date			remarks
 *			  		 wangxy			  20180917	  	  created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Printer6 extends UnitFragment
{
	/*private & local definition*/
	private final String TESTITEM = "X5打印";
	boolean flat = true;
	private Gui gui = new Gui(myactivity, handler);
	private int ret=-1;
	private String fileName=Printer6.class.getSimpleName();
	
	public void printer6()
	{
		String funcName="printer6";
		/**目前该案例只有X5支持*/
		if(GlobalVariable.currentPlatform!=Model_Type.X5)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"%s设备不支持切刀测试",GlobalVariable.currentPlatform);
			return;
		}
		if(GlobalVariable.gAutoFlag == ParaEnum.AutoFlag.AutoFull)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "%s自动测试不能作为最终测试结果，请结合手动测试验证",  TESTITEM);
		}
		
		// 判断是否存在picture文件夹，存在继续，不存在让测试人员导入
		StringBuffer strBuffer = new StringBuffer();
		if(TestFileJudge.unitPrintJudge(funcName,strBuffer)!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,"line %d:%s,请先放置测试文件", Tools.getLineInfo(),strBuffer);
			return;
		}
		
		PrintUtil printUtil = new PrintUtil(myactivity, handler,true);
		int[] typeId = new int[2];
		String[] type={"热敏打印机","穿孔针打","摩擦针打","低压热敏打印机","正常热敏打印机","3寸热敏打印机","无"};
				
		gui.cls_show_msg1(2, TESTITEM+"测试中...");
		gui.cls_show_msg("请打开打印纸仓，使打印机处于未关闭状态,确认按任意键继续");//轴不在位
		if (printUtil.getPrintStatus(MAXWAITTIME)!=EM_PRN_STATUS.PRN_STATUS_PPSERR.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:打印机状态异常!", Tools.getLineInfo());
			return;
		}
		gui.cls_show_msg("请确保设备打印机中放入3寸打印纸且正常关闭打印纸仓,确认按任意键继续");
		gui.cls_show_msg1(2,"未对打印机设置任何参数，目前为默认参数，即将进行打印。。。");
		//case1:未设置任何，默认尺寸打印国字
		printUtil.print_for_x5();
		// 打印PNG格式的图片
		int printerResult = printUtil.print_bitmap("other3.png", "PNG");
		if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,printerResult);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=JniNdk.JNI_PrnGetType(typeId))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s获取打印机类型测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((gui.ShowMessageBox(("请确认获取的打印机类型为：" + type[typeId[0]]+",预期为"+type[5]+"\n且打印效果与预期打印内容和图片与提示的一致").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s获取的打印机类型不一致(type = %d)",Tools.getLineInfo(), TESTITEM,typeId[0]);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//低压热敏打印机,X5设备只有适配器供电没有电池，故不存在低电情况
		
		//case2 正常热敏打印机（2寸）
		gui.cls_show_msg1(2,"将打印纸尺寸设置为2寸,即将进行打印。。。");
		if((ret=JniNdk.JNI_PrnSetPaperSize(2))!=NDK_OK)//设置为2寸，则脚本中打印一行的最大为384/8=48字节，超过则自动换行；
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印纸设置为2寸失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		printUtil.print_for_x5();// 打印国字
		printerResult = printUtil.print_bitmap("other3.png", "PNG");
		if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,printerResult);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=JniNdk.JNI_PrnGetType(typeId))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s获取打印机类型测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((gui.ShowMessageBox(("请确认获取的打印机类型为：" + type[typeId[0]]+",预期为"+type[4]+"\n且打印效果与预期打印内容和图片与提示的一致").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s获取的打印机类型不一致(type = %d)",Tools.getLineInfo(), TESTITEM,typeId[0]);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case3 3寸热敏打印机
		gui.cls_show_msg1(2,"将打印纸尺寸设置为3寸,即将进行打印。。。");
		if((ret=JniNdk.JNI_PrnSetPaperSize(3))!=NDK_OK)//设置为3寸，则脚本中打印一行的最大为576/8=72字节，超过则自动换行；
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s打印纸设置为3寸失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		printUtil.print_for_x5();// 打印国字
		printerResult = printUtil.print_bitmap("other3.png", "PNG");
		if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,printerResult);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=JniNdk.JNI_PrnGetType(typeId))!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s获取打印机类型测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if ((gui.ShowMessageBox(("请确认获取的打印机类型为：" + type[typeId[0]]+",预期为"+type[5]+"\n且打印效果与预期打印内容和图片与提示的一致").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s获取的打印机类型不一致(type = %d)",Tools.getLineInfo(), TESTITEM,typeId[0]);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case4:打印机切刀
		gui.cls_show_msg1(2,"即将进行打印后切刀测试。。。");
		// case4.1不切割
		printUtil.print_bill();// 打印票据
		printerResult = printUtil.print_bitmap("other3.png", "PNG");
		if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,printerResult);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if ((gui.ShowMessageBox(("请确认打印结束后，打印纸是否未切割掉落").getBytes(), (byte) (BTN_OK | BTN_CANCEL),WAITMAXTIME)) != BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s切割效果不一致(type = %s)", Tools.getLineInfo(), TESTITEM,type[typeId[0]]);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case4.2切割，切刀初始化
		if((ret=JniNdk.JNI_PrnCutterInit())!=NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s初始化切刀测试失败(ret = %s)",Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		printUtil.print_bill();//打印票据
		printerResult = printUtil.print_bitmap("other3.png", "PNG");
		if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,printerResult);
			if (GlobalVariable.isContinue == false)
				return;
		}
		if((ret=JniNdk.JNI_PrnCutterPerformance())!=NDK_OK)
		{
			JniNdk.JNI_PrnCutterInit();
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s切刀结束测试失败(ret = %s)",Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}
		JniNdk.JNI_PrnCutterInit();//开发许耿鹏要求流程为切刀初始化--切刀--切刀初始化 addby wangxy 20180920
		if ((gui.ShowMessageBox(("请确认打印结束后，打印纸是否切割掉落").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s切割效果不一致(type = %s)",Tools.getLineInfo(), TESTITEM,type[typeId[0]]);
			if (!GlobalVariable.isContinue)
				return;
		}
		
		//case4.3多次切刀，模拟实际交易两联单
		gui.cls_show_msg1(2,"即将进行二联单打印并切刀。。。");
		for(int i=0;i<2;i++)
		{
			if((ret=JniNdk.JNI_PrnCutterInit())!=NDK_OK)
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s初始化切刀测试失败(ret = %s)",Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			printUtil.print_bill();//打印票据
			printerResult = printUtil.print_bitmap("other3.png", "PNG");
			if (printerResult != EM_PRN_STATUS.PRN_STATUS_OK.getValue()) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr, "line %d:%s第%d次，打印PNG图片失败(status = %d)", Tools.getLineInfo(), TESTITEM,i,printerResult);
				if (GlobalVariable.isContinue == false)
					return;
			}
			if((ret=JniNdk.JNI_PrnCutterPerformance())!=NDK_OK)
			{
				JniNdk.JNI_PrnCutterInit();
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s切刀结束测试失败(ret = %s)",Tools.getLineInfo(), TESTITEM,ret);
				if (!GlobalVariable.isContinue)
					return;
			}
			JniNdk.JNI_PrnCutterInit();
			if ((gui.ShowMessageBox(("第"+(i+1)+"联，请确认打印结束后，打印纸是否切割掉落").getBytes(), (byte) (BTN_OK|BTN_CANCEL), WAITMAXTIME))!=BTN_OK) 
			{
				gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s第%d次切割效果不一致(type = %s)",Tools.getLineInfo(), TESTITEM,i,type[typeId[0]]);
				if (!GlobalVariable.isContinue)
					return;
			}
		}
		
		//case5:异常测试
		//case5.1:参数异常测试
		if((ret=JniNdk.JNI_PrnSetPaperSize(-1))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印纸尺寸异常测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret=JniNdk.JNI_PrnSetPaperSize(0))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印纸尺寸异常测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		if((ret=JniNdk.JNI_PrnSetPaperSize(1))==NDK_OK)
		{
			gui.cls_show_msg1_record(fileName, funcName, gKeepTimeErr,  "line %d:%s打印纸尺寸异常测试失败(ret=%s)", Tools.getLineInfo(),TESTITEM,ret);
			if(!GlobalVariable.isContinue)
				return;
		}
		//case5.2:切刀流程异常测试，不初始化切刀，预期切刀应返回错误
		/*未初始化切刀，仍可切刀
		 *开发许耿鹏说后续将在底层增加未初始化切刀，调用切刀方法时返回错误的功能，目前暂未实现 addby wangxy20180920
		gui.cls_show_msg("切刀流程异常测试，请在打印纸槽中放入打印纸,确认按任意键继续");
		printUtil.print_bill();//打印票据
		if((ret=JniNdk.JNI_PrnCutterPerformance())==NDK_OK)
		{
			JniNdk.JNI_PrnCutterInit();
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s切刀异常测试失败(ret = %s)",Tools.getLineInfo(), TESTITEM,ret);
			if (!GlobalVariable.isContinue)
				return;
		}*/
		gui.cls_show_msg1_record(fileName, funcName, gScreenTime, "以上都正确则%s测试通过", TESTITEM);
	}

	@Override
	public void onTestUp() {}

	@Override
	public void onTestDown() {}
}
