package com.example.highplattest.android;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import android.util.Log;
/************************************************************************
 * 
 * module 			: Android原生BigInteger大数据工具类模块 
 * file name 		: Android12.java 
 * Author 			: wangxy
 * version 			: 
 * DATE 			: 20180606 
 * directory 		: 
 * description 		: 测试Android原生BigInteger大数据工具类
 * related document : 
 * history 		 	: author			date			remarks
 *			  		  wangxy		   20180606 		created
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android12 extends UnitFragment {
	public final String TAG = Android12.class.getSimpleName();
	private String TESTITEM = "BigInter大数据工具类测试";
	private Gui gui = new Gui(myactivity, handler);
	private BigInteger big;
	private byte[] magnitude=new byte[1];;
		
	@SuppressWarnings("static-access")
	public void android12(){
		gui.cls_show_msg1(gScreenTime, "%s测试中...", TESTITEM);
		BigInteger big1=new BigInteger("12");
		BigInteger big2=new BigInteger("9");
		BigInteger big4=new BigInteger("5");
		Arrays.fill(magnitude, (byte)4);
		BigInteger big3=new BigInteger(-1, magnitude) ;
//		boolean is=false;
		//case1:加
		if(!(big=big1.add(big2)).toString().equals("21"))
		{
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的加法运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case2:取绝对值
		if(!(big=big3.abs()).toString().equals("4"))
		{
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的取绝对值运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case3：与
		if(!(big=big1.and(big2)).toString().equals("8"))
		{
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的与运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case4：big1 & ~big2
		if(!(big=big1.andNot(big2)).toString().equals("4"))
		{
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的big1 & ~big2运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case5：bit数，是1的位数
		int count=-1;
		if((count=big1.bitCount())!=2){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的为1的bit位数计算错误(%d)", Tools.getLineInfo(),TESTITEM,count);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case6：返回此 BigInteger 的最小的二进制补码表示形式的位数，不包括 符号位
		int lenth=-1;
		if((lenth=big1.bitLength())!=4){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的计算最小的二进制补码表示形式的位数错误(%d)", Tools.getLineInfo(),TESTITEM,lenth);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case7:清除某个bit位,从0开始
		if(!(big=big1.clearBit(2)).toString().equals("8")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的第三位清0运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case8:比较大小，-1小于0等于1大于
		int back=-1;
		if((back=big1.compareTo(big2))!=1){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的比较大小运算报错(%d)", Tools.getLineInfo(),TESTITEM,back);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case9：除
		if(!(big=big1.divide(big2)).toString().equals("1")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的除法运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case10：除、取余后的2个值存在数组中
		BigInteger[] bigArray=big1.divideAndRemainder(big2);
		if(!bigArray[0].toString().equals("1")&&!bigArray[1].toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的除法且取余运算报错(%s，%s)", Tools.getLineInfo(),TESTITEM,bigArray[0].toString(),bigArray[1].toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case11：转成double类型
		double mDouble=0.0;
		if((mDouble=big1.doubleValue())!=12.0){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的转double运算报错(%f)", Tools.getLineInfo(),TESTITEM,mDouble);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case12：是否相等
		if(big1.equals(big2)){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的是否相等运算报错(%s,%s)", Tools.getLineInfo(),TESTITEM,big1.toString(),big2.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case13:翻转某个bit位,0和1互转
		if(!(big=big1.flipBit(0)).toString().equals("13")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的指定bit位数据翻转运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case14：转float
		float mFolat=0f;
		if((mFolat=big2.floatValue())!=9.0f){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的转float运算报错(%f)", Tools.getLineInfo(),TESTITEM,mFolat);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case15：两者的最大公约数
		if(!(big=big1.gcd(big2)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的取最大公约数运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case16:返回此 BigInteger 最右端（最低位）1 比特的索引
		int local=-1;
		if((local=big1.getLowestSetBit())!=2){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取bit最低位数据运算报错(%d)", Tools.getLineInfo(),TESTITEM,local);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case17：获取hashCode
		int hashCode=-1;
		if((hashCode=big1.hashCode())!=12){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取hashCode运算报错(%d)", Tools.getLineInfo(),TESTITEM,hashCode);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case16：转int
		int value=-1;
		if((value=big1.intValue())!=12){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的转int运算报错(%d)", Tools.getLineInfo(),TESTITEM,value);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case17:如果此 BigInteger 可能为素数，则返回 true，如果它一定为合数，则返回 false。如果 certainty <= 0，则返回 true
		//参数表示 - 调用方允许的不确定性的度量。如果该调用返回 true，则此 BigInteger 是素数的概率超出 (1 - 1/2certainty)
		if(big1.isProbablePrime(1)){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的是否为质数运算报错(true)", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case18：转long
		long mLong=0l;
		if((mLong=big1.longValue())!=12l){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的转long类型运算报错(%d)", Tools.getLineInfo(),TESTITEM,mLong);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case19:比较大小并返回大的值
		if(!(big=big1.max(big2)).toString().equals("12")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的比较大小并返回较大的值运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case20:比较大小并返回小的值
		if(!(big=big1.min(big2)).toString().equals("9")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的比较大小并返回较小的值运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case21：取模
		if(!(big=big1.mod(big2)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取模运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		
		//case22：返回其值为 (this-1 mod m) 的 BigInteger
		//m <= 0，或者此 BigInteger 没有乘法可逆元 mod m（即此 BigInteger 不是 m 的 相对素数）时抛出异常ArithmeticException（即当出现异常的运算条件时，抛出此异常。例如，一个整数“除以零”时，抛出此类的一个实例）
		if(!(big=big3.modInverse(big4)).toString().equals("1")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的big1-1 取模 big4运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case23:返回其值为 (this的exponent次方 mod m) 的 BigInteger
		//m <= 0时抛出异常ArithmeticException
		if(!(big=big1.modPow(big3,big4)).toString().equals("1")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的big1的big3指数倍 mod big2运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case24：相乘
		if(!(big=big1.multiply(big2)).toString().equals("108")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的乘法运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case25:-big1
		if(!(big=big1.negate()).toString().equals("-12")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取相反数运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
       //case 26:返回大于此 BigInteger 的可能为素数的第一个整数。
		if(!(big=big1.nextProbablePrime()).toString().equals("13")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的大于big1的最小素数运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case27：取非，-this-1
		if(!(big=big1.not()).toString().equals("-13")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取非运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case 28:或
		if(!(big=big1.or(big2)).toString().equals("13")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的或运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case29:big1的2次方
		if(!(big=big1.pow(2)).toString().equals("144")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的次方运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case30：返回大于此 BigInteger的可能为素数的且长度为2的
		int bitLength=2;
		Random rnd=new Random(1);
		if(!(big=big1.probablePrime(bitLength,rnd)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的返回大于此 BigInteger的可能为素数的且长度为2的运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case31:%取余
		if(!(big=big1.remainder(big2)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的取余运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case32:this | (1<<n)
		if(!(big=big1.setBit(0)).toString().equals("13")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的设置第i位bit为1运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case33:this << n向左偏移几位
		if(!(big=big1.shiftLeft(1)).toString().equals("24")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的向左偏移运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case34:this >> n向右偏移几位
		if(!(big=big1.shiftRight(2)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的向右偏移运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case35：返回符号
		int signum=-1;
		if((signum=big1.signum())!=1){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的取符号运算报错(%d)", Tools.getLineInfo(),TESTITEM,signum);
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case36：减
		if(!(big=big1.subtract(big2)).toString().equals("3")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的减法运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case37：当且仅当设置了指定的位时，返回 true。（计算 ((this & (1<<n)) != 0)。）
		if(big1.testBit(1)){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的减法运算报错(true)", Tools.getLineInfo(),TESTITEM);
			if(!GlobalVariable.isContinue)
			    return;
		}
        //case38:转成byte数组
		byte[] mByte=new byte[10];
		mByte=big1.toByteArray();
		Log.v("mByte 转成byte数组", new String(mByte).trim());
		//case39:toString
		if(!(big1.toString().equals("12"))){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的toString报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		if(!(big1.toString(2).equals("1100"))){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的2进制toString报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString(2));
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case40:转long
		if(!(big=big1.valueOf((long)111)).toString().equals("111")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr,"line %d:%s的转成long报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		//case41:this ^ val异或
		if(!(big=big1.xor(big2)).toString().equals("5")){
			gui.cls_show_msg1_record(TAG, "android12", gKeepTimeErr, "line %d:%s的异或运算报错(%s)", Tools.getLineInfo(),TESTITEM,big.toString());
			if(!GlobalVariable.isContinue)
			    return;
		}
		
		gui.cls_show_msg1_record(TAG, "android12", gScreenTime,"%s测试通过", TESTITEM);
	}

	
	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}


	

}
