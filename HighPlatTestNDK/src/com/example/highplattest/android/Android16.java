package com.example.highplattest.android;

import java.text.FieldPosition;
import java.util.Date;
import java.util.Locale;
import android.annotation.TargetApi;
import android.icu.text.DateFormat;
import android.icu.text.DisplayContext;
import android.icu.text.NumberFormat;
import android.icu.text.DisplayContext.Type;
import android.icu.util.Calendar;
import android.icu.util.ChineseCalendar;
import android.icu.util.TimeZone;
import android.icu.util.ULocale;
import android.os.Build;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.LoggerUtil;
import com.example.highplattest.main.tools.Tools;
/************************************************************************
 * 
 * module 			: Android原生接口模块 
 * file name 		: Android16.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20180810
 * directory 		: DateFormat类时间日期获取
 * description 		: 
 * related document :
 * history 		 	: author			date			remarks
 *			  		 zhengxq		   20180810	 		created
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Android16 extends UnitFragment
{
	private final String TAG = Android16.class.getSimpleName();
	private final String SERIAL = "16";
	private final String TESTITEM = "android.icu.text.DateFormat(A7)";
	Gui gui = new Gui(myactivity, handler);
	
	
	public void android16()
	{
		if(Build.VERSION.SDK_INT>Build.VERSION_CODES.N)
		{
			try {
				testAndroid16();
			} catch (Exception e) {
				e.printStackTrace();
				gui.cls_show_msg1_record(TAG, "android16", gKeepTimeErr, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
			}
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "android16", gKeepTimeErr, "SDK版本低于24，不支持该案例");
		}
			
	}
	
	@TargetApi(24) 
	private void testAndroid16()
	{
		/**ABBR_GENERIC_TZ**/
		//1、实例化的第一种方式，日期 format(Date date,StringBuffer toAppendTo,FieldPosition fieldPosition)
		DateFormat dateFormat = DateFormat.getDateInstance();
		StringBuffer strBuffer = new StringBuffer();
		FieldPosition fieldPosition = new FieldPosition(DateFormat.YEAR_FIELD);// 开始索引和结束索引将分别设置为0和4
		
		// Object:must be a Number or a Date or a Calandar
		StringBuffer myString1 = dateFormat.format(new Date(), strBuffer, fieldPosition);
		LoggerUtil.d(TAG+",myString1"+strBuffer.toString());
		if(gui.cls_show_msg("1.获取的日期是否为当前日期:%s,否[取消],是[其他]",myString1)==ESC)
		{
			gui.cls_show_msg1_record(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString1.toString());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 4.getAvailableLocales():返回DateFormat的语言环境集
		Locale[] locales = DateFormat.getAvailableLocales();
		LoggerUtil.d(TAG+",locales"+locales[0].getDisplayName());
		//2、format(Calendar cal,StringBuffer toAppendTo,FieldPositon fieldPosition)
		// 6、getCalendar()
		StringBuffer myString2 = dateFormat.format(new ChineseCalendar(), new StringBuffer(), new FieldPosition(DateFormat.TIMEZONE_GENERIC_FIELD));
		if(gui.cls_show_msg("2.获取的日期是否为当前日期:%s,否[取消],是[其他]",myString2)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString2.toString());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 3、format(Date date)
		// ChineseCalendar(Date date)
		String myString3 = dateFormat.format(new ChineseCalendar(new Date()));
		if(gui.cls_show_msg("3.获取的日期是否为当前日期:%s,否[取消],是[其他]",myString3)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString3.toString());
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 5、getBooleanAttribute(DateFormat.BooleanAttribute key)
		boolean bool1 = dateFormat.getBooleanAttribute(DateFormat.BooleanAttribute.PARSE_ALLOW_NUMERIC);
		LoggerUtil.d(TAG+",bool1"+bool1);
		boolean bool2 = dateFormat.getBooleanAttribute(DateFormat.BooleanAttribute.PARSE_ALLOW_WHITESPACE);
		LoggerUtil.d(TAG+",bool2"+bool2);
		// 7、getContext(DisplayContext.Type type)
		DisplayContext displayContext = dateFormat.getContext(Type.DISPLAY_LENGTH);
		
		// 8、getDateTimeInstance():日期和时间
		DateFormat dateTime1 = DateFormat.getDateTimeInstance();
		String myString5 = dateTime1.format(new Date());
		LoggerUtil.d(TAG+",myStrign5"+myString5);
		if(gui.cls_show_msg("4.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString5)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString5);
			if(GlobalVariable.isContinue==false)
				return;
		}
		// 9、getDateTimeInstance(int dateStyple,int timeStyple)
		// ChineseCalendar(Locale locale)
		DateFormat dateTime2 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
		String myString6 = dateTime2.format(new ChineseCalendar(Locale.FRANCE));
		LoggerUtil.d(TAG+",myString6"+myString6);
		if(gui.cls_show_msg("5.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString6)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString6);
			if(GlobalVariable.isContinue==false)
				return;
		}
		//10、getDateTimeInstance(Calendar cal,int dateStyle,int timeStyle,ULocale locale)
		// ChineseCalendar(TimeZone zone)
		DateFormat dateTime3 = DateFormat.getDateTimeInstance(new ChineseCalendar(TimeZone.GMT_ZONE), DateFormat.SHORT, DateFormat.MEDIUM, ULocale.ENGLISH);
		String myString7 = dateTime3.format(dateTime3.getCalendar());
		LoggerUtil.d(TAG+",myString7"+ myString7);
		if(gui.cls_show_msg("6.获取的日期时间是否为当前日期时间-8h:%s,否[取消],是[其他]",myString7)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString7);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 11、getDateTimeInstance(Calendar cal,int dateStyle,int timeStyple,Locale locale)
		DateFormat dateTime4 = DateFormat.getDateTimeInstance(new ChineseCalendar(ULocale.CANADA), DateFormat.LONG, DateFormat.LONG, Locale.CANADA);
		String myString8 = dateTime4.format(dateTime4.getCalendar());
		LoggerUtil.d(TAG+",myString8"+myString8);
		if(gui.cls_show_msg("7.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString8)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString8);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 12、getDateTimeInstance(int dateStyple,int timeStyple,ULocale locale)
		DateFormat dateTime5 = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, ULocale.KOREA);
		String myString9 = dateTime5.format(new ChineseCalendar(100, 10, 0, 31));
		LoggerUtil.d(TAG+",myString9"+myString9);
		if(gui.cls_show_msg("8.获取的日期时间是否为(韩文显示):2084. 1. 8. 오전 12:00:00:(%s),否[取消],是[其他]",myString9)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString9);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 13、getDateTimeInstance(int dateStyle,int timeStyle,Locale aLocale)
		DateFormat dateTime6 = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRANCE);
		String myString10 = dateTime6.format(new ChineseCalendar(1, 200, 1, 1, 20));
		LoggerUtil.d(TAG+",myString10"+myString10);
		if(gui.cls_show_msg("9.获取的日期时间是否为:15/05/2438 00:00:(%s),否[取消],是[其他]",myString10)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString10);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 14、getDateTimeInstance(Calendar cal,int dateStyle,int timeStyle)
		DateFormat dateTime7 = DateFormat.getDateTimeInstance(Calendar.getInstance(Locale.CANADA), DateFormat.SHORT, DateFormat.SHORT);
		String myString11 = dateTime7.format(new Date());
		LoggerUtil.d(TAG+",myString11"+myString11);
		if(gui.cls_show_msg("10.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString11)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString11);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 15、getInstance(Calendar cal)(日期+时间)
		DateFormat instance1 = DateFormat.getInstance(Calendar.getInstance());
		String myString12 = instance1.format(new ChineseCalendar(300, 2, 0, 12, 12, 12, 12));
		LoggerUtil.d(TAG+",myString12"+myString12);
		if(gui.cls_show_msg("11.获取的日期时间是否为:10/4/2283 下午:12:12(%s),否[取消],是[其他]",myString12)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString12);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 16、getInstance(Calendar cal,Locale locale)
		DateFormat instance2 = DateFormat.getInstance(Calendar.getInstance(), Locale.ITALY);
		String myString13 = instance2.format(new Date());
		LoggerUtil.d(TAG+",myString13"+myString13);
		if(gui.cls_show_msg("12.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString13)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString13);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 17、getInstance()
		DateFormat instance3 = DateFormat.getInstance();
		String myString14 = instance3.format(new ChineseCalendar(0, 200, 7, 1, 12, 5, 5, 5));
		LoggerUtil.d(TAG+",myString14"+myString14);
		if(gui.cls_show_msg("13.获取的日期时间是否为:4/11/2498 上午:5:05(%s),否[取消],是[其他]",myString14)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString14);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 18、getInstenceForSkeleton(Calendar cal,String skeleton,Locale locale)
		DateFormat forskele1 = DateFormat.getInstanceForSkeleton(Calendar.getInstance(), DateFormat.ABBR_MONTH, Locale.GERMAN);
		String myString15 = forskele1.format(new Date());
		LoggerUtil.d(TAG+",myString15"+ myString15);
		if(gui.cls_show_msg("14.获取的月份是否为当前月份:%s,否[取消],是[其他]",myString15)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString15);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 19、getInstanceForSkeleton(Calendar cal,String skeleton,ULocale locale)
		DateFormat forskele2 = DateFormat.getInstanceForSkeleton(Calendar.getInstance(), DateFormat.MONTH_WEEKDAY_DAY, ULocale.ENGLISH);
		String myString16 = forskele2.format(new Date());
		LoggerUtil.d(TAG+",myString16"+myString16);
		if(gui.cls_show_msg("15.获取的日期时间是否为当前日期时间(星期+月的表示方式):%s,否[取消],是[其他]",myString16)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString16);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		
		// 20、getInstanceForSkeleton(String)
		DateFormat forskele3 = DateFormat.getInstanceForSkeleton(DateFormat.MONTH_WEEKDAY_DAY);
		String myString17 = forskele3.format(new Date());
		LoggerUtil.d(TAG+",myString17"+myString17);
		if(gui.cls_show_msg("16.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString17)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString17);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 21、getNumberFormat()
		NumberFormat numberFm = forskele3.getNumberFormat();
		// 22、getPatternInstance(String skeleton,Locale locale)
		DateFormat pattern1 = DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY, Locale.CANADA);
		String myString18 = pattern1.format(new Date());
		LoggerUtil.d(TAG+",myString18"+myString18);
		if(gui.cls_show_msg("17.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString18)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString18);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 22、getPatternInstance(String skeleton,ULocale locale)
		DateFormat pattern2 = DateFormat.getPatternInstance(DateFormat.ABBR_MONTH);
		String myString19 = pattern2.format(new Date());
		LoggerUtil.d(TAG+",myString19"+myString19);
		if(gui.cls_show_msg("18.获取的月份是否为月份:%s,否[取消],是[其他]",myString19)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString19);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 23、getPatternInstance(Calendar cal,String skeleton,Locale locale)
		DateFormat pattern3 = DateFormat.getPatternInstance(Calendar.getInstance(), DateFormat.ABBR_MONTH, Locale.FRANCE);
		String myString20 = pattern3.format(new Date());
		LoggerUtil.d(TAG+",myString20"+myString20);
		if(gui.cls_show_msg("19.获取的月份是否为当前月份(法文表示 ):%s,否[取消],是[其他]",myString20)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString20);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		//24、 getTimeInstance
		DateFormat timeFormat1 = DateFormat.getTimeInstance(Calendar.getInstance(Locale.CANADA), DateFormat.SHORT);
		TimeZone timeZone = timeFormat1.getTimeZone();
		LoggerUtil.d(TAG+",timeZone"+timeZone.getDisplayName());
		if(gui.cls_show_msg("20.获取的时区名是否为当前时区名:%s,否[取消],是[其他]",timeZone.getDisplayName())==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,timeZone);
			if(GlobalVariable.isContinue==false)
				return;
		}
		String myString4 = timeFormat1.format(new Date());
		LoggerUtil.d(TAG+",myString4"+myString4);
		if(gui.cls_show_msg("21.获取的时间是否为当前时间:%s,否[取消],是[其他]",myString4)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString4);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 25、getTimeInstance(int style,ULocale locale)
		DateFormat timeFormat2 = DateFormat.getTimeInstance(DateFormat.SHORT, ULocale.FRANCE);
		String myString21 = timeFormat2.format(new Date());
		LoggerUtil.d(TAG+",myString21"+myString21);
		if(gui.cls_show_msg("22.获取的时间是否为当前时间:%s,否[取消],是[其他]",myString21)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString21);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 26、getTimeInstance(Calendar cal,int timeStyle,ULocale locale)
		DateFormat timeFormat3 = DateFormat.getTimeInstance(Calendar.getInstance(), DateFormat.LONG, Locale.CHINESE);
		String myString22 = timeFormat3.format(new Date());
		LoggerUtil.d(TAG+",myString22"+myString22);
		if(gui.cls_show_msg("23.获取的时间是否为当前时间:%s,否[取消],是[其他]",myString22)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString22);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 27、getTimeInstance(int style)
		DateFormat timeFormat4 = DateFormat.getTimeInstance(DateFormat.LONG);
		String myString23 = timeFormat4.format(new Date());
		LoggerUtil.d(TAG+",myString23"+myString23);
		if(gui.cls_show_msg("24.获取的日期时间是否为当前日期时间:%s,否[取消],是[其他]",myString23)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString23);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		// 28、getTimeInstance(int style,Locale aLocale)
		DateFormat timeFormat5 = DateFormat.getTimeInstance(DateFormat.LONG, Locale.CANADA);
		String myString24 = timeFormat5.format(new Date());
		LoggerUtil.d(TAG+",mystring24"+myString24);
		if(gui.cls_show_msg("24.获取的时间是否为当前时间:%s,否[取消],是[其他]",myString24)==ESC)
		{
			gui.cls_show_msg1(gKeepTimeErr, SERIAL, "line %d:%s测试失败(%s)", Tools.getLineInfo(),TESTITEM,myString24);
			if(GlobalVariable.isContinue==false)
				return;
		}
		
		gui.cls_show_msg1_record(TAG, "Android16", gKeepTimeErr,"测试通过");
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
