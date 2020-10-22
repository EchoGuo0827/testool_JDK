package com.example.highplattest.android;

import java.math.BigInteger;

import android.annotation.TargetApi;
import android.icu.math.BigDecimal;
import android.icu.math.MathContext;
import android.os.Build;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android15.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20171130
 * directory 		: BigDecimal工具类各种类型转换测试
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20171130	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android15 extends UnitFragment{
	public final String TAG = Android15.class.getSimpleName();
	private final String TESTITEM = "android.icu.math.BigDecimal(A7)";
	private Gui gui = new Gui(myactivity, handler);
	
	public void android15()
	{
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N)
		{
			try {
				testAndroid15();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "SDK版本低于24,不支持该用例");
		}
	}
	
	
	/**
	 * 要到Android7.0才开始支持
	 */
	@TargetApi(24) 
	private void testAndroid15()
	{
		gui.cls_printf("BigDecimal类测试...".getBytes());
		try 
		{
			// case1:int类型转为toString显示
			BigDecimal deciInt = new BigDecimal(100);
			if(deciInt.toString().equals("100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s的int类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			// case2:double类型转为toString显示
			BigDecimal deciDouble = new BigDecimal(0.002);
			String transmit=deciDouble.toString().substring(0, 5);
			if(transmit.equals("0.002")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s的double类型转为String报错(%s)", Tools.getLineInfo(),TESTITEM,transmit);
				return;
			}
			// case3:long类型转为toString显示
			BigDecimal deciLong = new BigDecimal(10000L);
			if(deciLong.toString().equals("10000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s的long类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			// case4:String类型转为toString显示
			BigDecimal deciStr = new BigDecimal("1000");
			if(deciStr.toString().equals("1000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s的String类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			
			// case5:BigDecimal类绝对值
			BigDecimal absValue;
			BigDecimal absBig1 = new BigDecimal(-1.2555);
			absValue = absBig1.abs();
			if(absValue.toString().equals("1.255500000000000060396132539608515799045562744140625")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**ENGINEERING:标准浮点表示法（采用工程指数格式，其中10的幂是3的倍数）
			 * PLAIN:普通（固定点）表示法，没有任何指数
			 * ROUND_CEILING:舍入模式可以舍入到更正数
			 * ROUND_DOWN:舍入模式向零舍入
			 * ROUND_FLOOR:舍入模式舍入到更负数
			 * ROUND_HALF_DOWN:舍入模式以舍入到最近邻居，其中等距值向下舍入
			 * ROUND_HALF_EVEN:舍入模式以舍入到最近邻居，其中等距值舍入到最近的偶数邻居
			 * ROUND_HALF_UP:舍入模式以舍入到最近邻居，其中等距值被向上舍入
			 * ROUND_UNNECESSARY:舍入模式断言不需要舍入
			 * ROUND_UP:舍入模式从零开始舍入
			 * SCIENTIFIC:标准浮点表示法（采用科学指数格式，其中小数点前有一位数）*/
			absValue = absBig1.abs(new MathContext(MathContext.ROUND_CEILING));
			if(absValue.toString().equals("1.3")==false)// 对照Android手机修改 20181115
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			absValue = absBig1.abs(new MathContext(MathContext.ROUND_DOWN));
			if(absValue.toString().equals("1")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			absValue = absBig1.abs(new MathContext(MathContext.ROUND_FLOOR));
			if(absValue.toString().equals("1.26")==false)// 对照安卓手机修改10181115
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			absValue = absBig1.abs(new MathContext(MathContext.ROUND_HALF_DOWN));
			if(absValue.toString().equals("1.2555")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case6:BigDecimal类加法
			BigDecimal addValue;
			BigDecimal addBig1 = new BigDecimal(2221.235);
			BigDecimal addBig2 = new BigDecimal(11112.3456);
			addValue = addBig1.add(addBig2);
			
			addValue.setScale(3, BigDecimal.ROUND_HALF_EVEN);
			if(addValue.toString().equals("13333.58060000000068612280301749706268310546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,addValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			addValue = addBig1.add(addBig2, new MathContext(MathContext.ROUND_HALF_UP));// 保留16位数字
			if(addValue.toString().equals("1.333E+4")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,addValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case7:数据转换
			byte bValue = deciInt.byteValueExact();
			if(bValue!=100)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,bValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case8.1:compareTo(BigDecimal rhs)
			int comValue = addBig2.compareTo(addBig1);
			if(comValue!=1)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,comValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case8.2:compareTo(BigDecimal rhs,MathContext set)
			int comValue1 = addBig2.compareTo(addBig1, new MathContext(MathContext.ROUND_CEILING));
			if(comValue1!=1)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,comValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case9:除法
			BigDecimal divideValue;
			BigDecimal divBig1 = new BigDecimal(1247.88);
			BigDecimal divBig2 = new BigDecimal(562.0);
			divideValue = divBig1.divide(divBig2, 10,MathContext.ROUND_HALF_UP);
			if(divideValue.toString().equals("2.2204270463")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			divideValue = divBig1.divide(divBig2, 16, MathContext.ROUND_CEILING);
			if(divideValue.toString().equals("2.2204270462633454")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**int scale: HALF_DOWN:舍入模式，其中值向最近邻居舍入*/
			divideValue = divBig1.divide(divBig2, 20, MathContext.ROUND_DOWN);
			if(divideValue.toString().equals("2.22042704626334538992")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			divideValue = divBig1.divide(divBig2, MathContext.ROUND_HALF_UP);
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**java中如果用BigDecimal做除法的时候一定要在divide方法中传递第二个参数，定义精确到小数点后几位，否则在不整除的情况下，结果是无限循环小数时，就会抛出java.lang.ArithmeticException*/
//			divideValue = divBig1.divide(divBig2);
//			if(divideValue.toPlainString().equals("23.940000000000001278976924368180334568023681640625")==false)
//			{
//				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
//				if(GlobalVariable.isContinue==false)
//					return;
//			}
			/**DECIMAL32:precision 7 digit,舍入模式：四舍五入*/
			divideValue = divBig1.divide(divBig2,MathContext.PLAIN);
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			divideValue = divBig1.divide(divBig2,MathContext.ENGINEERING);
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			// divide(BigDecimal rhs,MathContext set)
			divideValue = divBig1.divide(divBig2, new MathContext(MathContext.PLAIN));
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// divide(BigDecimal rhs)
			divideValue = divBig1.divide(divBig2);
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// divideInteger(BigDecimal rhs):返回一个普通的BigDecimal,其值是this/rhs的整数部分
			divideValue = divBig1.divideInteger(divBig2);
			if(divideValue.toString().equals("2")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// divideInteger(BigDecimal rhs,MathContext set)
			divideValue = divBig1.divide(divBig2,new MathContext(MathContext.ROUND_HALF_DOWN));
			if(divideValue.toString().equals("2.2205")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			// case10:Value值
			double douValue = addBig2.doubleValue();
			if(douValue!=11112.345600)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%f)", Tools.getLineInfo(),TESTITEM,douValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			float fValue = addBig2.floatValue();
			if(fValue!=11112.345703f)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%f)", Tools.getLineInfo(),TESTITEM,fValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			int iValue = deciInt.intValue();
			if(iValue!=100)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			long lValue = deciLong.longValue();
			if(lValue!=10000L)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,lValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			lValue = new BigDecimal(-100).longValueExact();
			if(lValue!=-100)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,lValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case11:max与min
			BigDecimal max = addBig1.max(addBig2);
			if(max.equals(addBig2)==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,max.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			max = addBig1.max(addBig2, new MathContext(MathContext.ROUND_HALF_UP));
			if(max.toString().equals("1.111E+4")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,max.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal min = addBig1.min(addBig2);
			if(min.equals(addBig1)==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,min.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			min = addBig1.min(addBig2,new MathContext(MathContext.ROUND_HALF_EVEN));
			if(min.toString().equals("2221.24")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,min.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case17:格式化
			// format(int before,int after)
			// format(int before,int after,int explaces,int exdigits,int exformint,int exround)
			
			// case12:减法操作
			BigDecimal subValue;
			BigDecimal subBig1 = new BigDecimal(49.88);
			BigDecimal subBig2 = new BigDecimal(1.78);
			subValue = subBig1.subtract(subBig2);
			if(subValue.toString().equals("48.1000000000000025313084961453569121658802032470703125")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,subValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			subValue = subBig1.subtract(subBig2, new MathContext(MathContext.ROUND_CEILING));
			if(subValue.toString().equals("48")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,subValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			//case13:乘法操作
			BigDecimal mulValue;
			BigDecimal mulBig1 = new BigDecimal(3.45678);
			BigDecimal mulBig2 = new BigDecimal(4.589);
			mulValue = mulBig1.multiply(mulBig2);
			if(mulValue.toString().equals("15.86316342000000227758420123791446187711657345323672661688112206501699574801023118197917938232421875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,mulValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL32:precision 7 digit*/
			mulValue = mulBig1.multiply(mulBig2, new MathContext(MathContext.ROUND_DOWN));
			if(mulValue.toString().equals("2E+1")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,mulValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case14:左移位，右移位
			BigDecimal moveBig;
			moveBig = addBig1.movePointLeft(3);
			if(moveBig.toString().equals("2.22123500000000012732925824820995330810546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,moveBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			moveBig = addBig1.movePointRight(3);
			if(moveBig.toString().equals("2221235.00000000012732925824820995330810546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,moveBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case15:取反
			BigDecimal negBig;
			BigDecimal negBig1 = new BigDecimal(-100);
			negBig = negBig1.negate();
			if(negBig.toString().equals("100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,negBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal negBig2 = new BigDecimal(-110);
			negBig = negBig2.negate(new MathContext(MathContext.ROUND_FLOOR));
			if(negBig.toString().equals("110")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,negBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**plus:value is +this*/
			BigDecimal plusBig;
			plusBig = negBig1.plus();
			if(plusBig.toString().equals("-100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,plusBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			plusBig = negBig2.plus(new MathContext(MathContext.ROUND_HALF_DOWN));
			if(plusBig.toString().equals("-110")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,plusBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			// case16:阶层
			BigDecimal powBig = new BigDecimal(10);;
			BigDecimal powBig1 = new BigDecimal(2);
			powBig = powBig1.pow(powBig);
			if(powBig.toString().equals("1024")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,powBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			powBig = powBig1.pow(powBig,new MathContext(MathContext.ROUND_HALF_EVEN));
			if(powBig.toString().equals("1.79769E+308")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,powBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case18:前模后
			BigDecimal remainBig;
			BigDecimal remainBig1 = new BigDecimal(155.22);
			BigDecimal remainBig2 = new BigDecimal(5.22);
			remainBig = remainBig1.remainder(remainBig2);
			if(remainBig.toString().equals("3.84000000000000607514039074885658919811248779296875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,remainBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 这个小数位数不对，与手机对比过一致
			remainBig = remainBig1.remainder(remainBig2,new MathContext(MathContext.PLAIN));
			if(remainBig.toString().equals("3.84000000000000607514039074885658919811248779296875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,remainBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case20:scale
			int scale = remainBig.scale();// 默认scale
			if(scale!=50)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal scaleBig = new BigDecimal(1.111111);
			scaleBig = remainBig.setScale(60);
			scale = scaleBig.scale();
			if(scale!=60)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			scaleBig = remainBig.setScale(60,MathContext.ROUND_UNNECESSARY);
			scale = scaleBig.scale();
			if(scale!=60)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case21:signum():返回此BigDecimal的符号，作为int
			int signum = remainBig.signum();
			if(signum !=1)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,signum);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case21:toBigDecimal():转换为java.math.BigDecimal
			java.math.BigDecimal javaDecimal = remainBig.toBigDecimal();
			if(javaDecimal.toPlainString().equals("3.84000000000000607514039074885658919811248779296875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,javaDecimal.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigInteger javaInt = remainBig.toBigInteger();
			if(javaInt.toString().equals("3")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,javaInt.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case22:valueof相关
			BigDecimal doubleBig = BigDecimal.valueOf(3.45677);
			if(doubleBig.toString().equals("3.45677")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,doubleBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal longBig = BigDecimal.valueOf(1000l);
			if(longBig.toString().equals("1000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,longBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal longDouble = BigDecimal.valueOf(3000l, 10);
			if(longDouble.toString().equals("0.0000003000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android15", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,longDouble);
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg1_record(TAG, "android15",gScreenTime,"%s测试通过", TESTITEM);
		} catch (Exception e) 
		{
			e.printStackTrace();
			gui.cls_show_msg1(1, "抛出%s异常", e.getMessage());
		}

	}
	

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
