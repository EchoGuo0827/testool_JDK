package com.example.highplattest.android;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android14.java 
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
public class Android14 extends UnitFragment{
	public final String TAG = Android14.class.getSimpleName();
	private final String TESTITEM = "java.math.BigDecimal";
	private Gui gui = new Gui(myactivity, handler);
	
	public void android14()
	{
		gui.cls_printf("BigDecimal类测试...".getBytes());
		try 
		{
			// case1:int类型转为toString显示
			BigDecimal deciInt = new BigDecimal(100);
			if(deciInt.toString().equals("100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s的int类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			// case2:double类型转为toString显示
			BigDecimal deciDouble = new BigDecimal(0.002);
			String transmit=deciDouble.toString().substring(0, 5);
			if(transmit.equals("0.002")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s的double类型转为String报错(%s)", Tools.getLineInfo(),TESTITEM,transmit);
				return;
			}
			// case3:long类型转为toString显示
			BigDecimal deciLong = new BigDecimal(10000L);
			if(deciLong.toString().equals("10000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s的long类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			// case4:String类型转为toString显示
			BigDecimal deciStr = new BigDecimal("1000");
			if(deciStr.toString().equals("1000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s的String类型转为String报错", Tools.getLineInfo(),TESTITEM);
				return;
			}
			
			// case5:BigDecimal类绝对值
			BigDecimal absValue;
			BigDecimal absBig1 = new BigDecimal(-1.2555);
			absValue = absBig1.abs();
			if(absValue.toString().equals("1.255500000000000060396132539608515799045562744140625")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL32:7 digit precision,Round Mode:四舍五入*/
			absValue = absBig1.abs(MathContext.DECIMAL32);
			if(absValue.toString().equals("1.255500")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL128:34 digit precision,Round Mode:四舍五入*/
			absValue = absBig1.abs(MathContext.DECIMAL128);
			if(absValue.toString().equals("1.255500000000000060396132539608516")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL64:16 digit precision,Round Mode:四舍五入*/
			absValue = absBig1.abs(MathContext.DECIMAL64);
			if(absValue.toString().equals("1.255500000000000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**UNLIMITED:unlimited precision,Round Mode:四舍五入*/
			absValue = absBig1.abs(MathContext.UNLIMITED);
			if(absValue.toString().equals("1.255500000000000060396132539608515799045562744140625")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,absValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case6:BigDecimal类加法
			BigDecimal addValue;
			BigDecimal addBig1 = new BigDecimal(2221.235);
			BigDecimal addBig2 = new BigDecimal(11112.3456);
			addValue = addBig1.add(addBig2);
			/**RoundingMode.CEILING:舍入模式向正向无穷大
			 * RoundingMode.DOWN:舍入模式，其中值向下舍入为零
			 * RoundingMode.FLOOR:舍入模式向负向无穷大
			 * RoundingMode.HALF_DOWN:舍入模式，其中值向最近邻居舍入
			 * RoundingMode.HALF_EVEN:舍入模式，其中值向最近邻居舍入
			 * RoundingMode.HALF_UP:舍入模式，其中值向最近邻居舍入
			 * */
			
			addValue.setScale(3, RoundingMode.DOWN);
			if(addValue.toString().equals("13333.58060000000068612280301749706268310546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,addValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			addValue = addBig1.add(addBig2, MathContext.DECIMAL64);// 保留16位数字
			if(addValue.toPlainString().equals("13333.58060000000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,addValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case7:数据转换
			byte bValue = deciInt.byteValueExact();
			if(bValue!=100)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,bValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case8:compareTo
			int comValue = addBig2.compareTo(addBig1);
			if(comValue!=1)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,comValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case9:除法
			BigDecimal divideValue;
			BigDecimal divBig1 = new BigDecimal(1247.88);
			BigDecimal divBig2 = new BigDecimal(562.0);
			divideValue = divBig1.divide(divBig2, 10,BigDecimal.ROUND_HALF_UP);
			if(divideValue.toString().equals("2.2204270463")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			divideValue = divBig1.divide(divBig2, 16, RoundingMode.HALF_DOWN);
			if(divideValue.toString().equals("2.2204270462633454")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**int scale: HALF_DOWN:舍入模式，其中值向最近邻居舍入*/
			divideValue = divBig1.divide(divBig2, 20, RoundingMode.HALF_DOWN);
			if(divideValue.toString().equals("2.22042704626334538993")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			divideValue = divBig1.divide(divBig2, BigDecimal.ROUND_HALF_UP);
			if(divideValue.toString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**java中如果用BigDecimal做除法的时候一定要在divide方法中传递第二个参数，定义精确到小数点后几位，否则在不整除的情况下，结果是无限循环小数时，就会抛出java.lang.ArithmeticException*/
//			divideValue = divBig1.divide(divBig2);
//			if(divideValue.toPlainString().equals("23.940000000000001278976924368180334568023681640625")==false)
//			{
//				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
//				if(GlobalVariable.isContinue==false)
//					return;
//			}
			/**DECIMAL32:precision 7 digit,舍入模式：四舍五入*/
			divideValue = divBig1.divide(divBig2,MathContext.DECIMAL32);
			if(divideValue.toPlainString().equals("2.220427")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			divideValue = divBig1.divide(divBig2,RoundingMode.HALF_DOWN);
			if(divideValue.toPlainString().equals("2.2204270462633453899276943287391255334603")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,divideValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			/**divideAndRemainder:返回一个BigDecimal数组，索引0为除以除数后得到的整数部分，索引1为余数部分*/
			BigDecimal[] resultArr=divBig1.divideAndRemainder(divBig2);
			if(resultArr[0].toString().equals("2.0000000000000000000000000000000000000000")==false||resultArr[1].toString().equals("123.8800000000001091393642127513885498046875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,resultArr[0].toString(),resultArr[1].toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL32:precision 7 digit*/
			// 疑问：余数怎么没有精度控制,使用华为手机测试过也是一样的值
			resultArr = divBig1.divideAndRemainder(divBig2, MathContext.DECIMAL32);
			if(resultArr[0].toString().equals("2.000000")==false||resultArr[1].toString().equals("123.8800000000001091393642127513885498046875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s,%s)", Tools.getLineInfo(),TESTITEM,resultArr[0].toString(),resultArr[1].toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**divideToIntegralValue:商，商将向下舍入为零到下一个整数*/
			BigDecimal intValue = divBig1.divideToIntegralValue(divBig2);
			if(intValue.toString().equals("2.0000000000000000000000000000000000000000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,intValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL32:precision 7 digit*/
			intValue = divBig1.divideToIntegralValue(divBig2, MathContext.DECIMAL32);
			if(intValue.toString().equals("2.000000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,intValue.toPlainString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case10:Value值
			double douValue = addBig2.doubleValue();
			if(douValue!=11112.345600)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%f)", Tools.getLineInfo(),TESTITEM,douValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			float fValue = addBig2.floatValue();
			if(fValue!=11112.345703f)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%f)", Tools.getLineInfo(),TESTITEM,fValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			int iValue = deciInt.intValue();
			if(iValue!=100)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,iValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			long lValue = deciLong.longValue();
			if(lValue!=10000L)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,lValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			lValue = new BigDecimal(-100).longValueExact();
			if(lValue!=-100)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)", Tools.getLineInfo(),TESTITEM,lValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case11:max与min
			BigDecimal max = addBig1.max(addBig2);
			if(max.equals(addBig2)==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,max.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal min = addBig1.min(addBig2);
			if(min.equals(addBig1)==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,min.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			// case12:减法操作
			BigDecimal subValue;
			BigDecimal subBig1 = new BigDecimal(49.88);
			BigDecimal subBig2 = new BigDecimal(1.78);
			subValue = subBig1.subtract(subBig2);
			if(subValue.toString().equals("48.1000000000000025313084961453569121658802032470703125")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,subValue.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			subValue = subBig1.subtract(subBig2, MathContext.DECIMAL32);
			if(subValue.toString().equals("48.10000")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,subValue.toString());
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
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,mulValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**DECIMAL32:precision 7 digit*/
			mulValue = mulBig1.multiply(mulBig2, MathContext.DECIMAL32);
			if(mulValue.toString().equals("15.86316")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,mulValue);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case14:左移位，右移位
			BigDecimal moveBig;
			moveBig = addBig1.movePointLeft(3);
			if(moveBig.toString().equals("2.22123500000000012732925824820995330810546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,moveBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			moveBig = addBig1.movePointRight(3);
			if(moveBig.toString().equals("2221235.00000000012732925824820995330810546875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,moveBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case15:取反
			BigDecimal negBig;
			BigDecimal negBig1 = new BigDecimal(-100);
			negBig = negBig1.negate();
			if(negBig.toString().equals("100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,negBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal negBig2 = new BigDecimal(-110);
			negBig = negBig2.negate(MathContext.DECIMAL32);
			if(negBig.toString().equals("110")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,negBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			/**plus:value is +this*/
			BigDecimal plusBig;
			plusBig = negBig1.plus();
			if(plusBig.toString().equals("-100")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,plusBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			plusBig = negBig2.plus(MathContext.DECIMAL32);
			if(plusBig.toString().equals("-110")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,plusBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			// case16:阶层
			BigDecimal powBig;
			BigDecimal powBig1 = new BigDecimal(2);
			powBig = powBig1.pow(10);
			if(powBig.toString().equals("1024")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,powBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			powBig = powBig1.pow(10,MathContext.DECIMAL32);
			if(powBig.toString().equals("1024")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,powBig);
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case17:precision
			BigDecimal presisionBig = new BigDecimal(12342.567);
			int precision = presisionBig.precision();
			if(precision!=43)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,precision);
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
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,remainBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// 这个小数位数不对，与手机对比过一致
			remainBig = remainBig1.remainder(remainBig2,MathContext.DECIMAL32);
			if(remainBig.toString().equals("3.84000000000000607514039074885658919811248779296875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,remainBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case19:取位数
			BigDecimal roundBig;
			roundBig = remainBig.round(MathContext.DECIMAL64);
			if(roundBig.toString().equals("3.840000000000006")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,roundBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			// case20:scale
			int scale = remainBig.scale();// 默认scale
			if(scale!=50)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			BigDecimal scaleBig = new BigDecimal(1.111111);
			scaleBig = remainBig.setScale(60);
			scale = scaleBig.scale();
			if(scale!=60)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			
			scaleBig = remainBig.setScale(60,RoundingMode.DOWN);
			scale = scaleBig.scale();
			if(scale!=60)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%d)",Tools.getLineInfo(),TESTITEM,scale);
				if(GlobalVariable.isContinue==false)
					return;
			}
			scaleBig = remainBig.scaleByPowerOfTen(5);// 相当于向右边移位
			if(scaleBig.toString().equals("384000.000000000607514039074885658919811248779296875")==false)
			{
				gui.cls_show_msg1_record(TAG, "android14", gKeepTimeErr, "line %d:%s测试失败(%s)",Tools.getLineInfo(),TESTITEM,scaleBig.toString());
				if(GlobalVariable.isContinue==false)
					return;
			}
			gui.cls_show_msg1_record(TAG, "android14",gScreenTime,"%s测试通过", TESTITEM);
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
