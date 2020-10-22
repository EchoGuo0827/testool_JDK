package com.example.highplattest.main.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFileJudge {
	
	public static int sysTestPrintJudge(String testName,StringBuffer strBuffer)
	{
		int ret=0;
		// other系列16张
		final File otherFile1 = new File("/sdcard/picture/other1.png");
		final File otherFile2 = new File("/sdcard/picture/other2.png");
		final File otherFile3 = new File("/sdcard/picture/other3.png");
		final File otherFile4 = new File("/sdcard/picture/other4.png");
		final File otherFile5 = new File("/sdcard/picture/other5.png");
		final File otherFile6 = new File("/sdcard/picture/other6.png");
		final File otherFile7 = new File("/sdcard/picture/other7.png");
		final File otherFile8 = new File("/sdcard/picture/other8.png");
		final File otherFile9 = new File("/sdcard/picture/other9.png");
		final File otherFile10 = new File("/sdcard/picture/other10.png");
		final File otherFile11 = new File("/sdcard/picture/other11.png");
		final File otherFile12 = new File("/sdcard/picture/other12.png");
		final File otherFile13 = new File("/sdcard/picture/other13.png");
		final File otherFile14 = new File("/sdcard/picture/other14.png");
		final File otherFile15 = new File("/sdcard/picture/other15.png");
		final File otherFile16 = new File("/sdcard/picture/other16.png");
		
		final File ihdrFile7 = new File("/sdcard/picture/IHDR7.png");
		final File colorFile1 = new File("/sdcard/picture/color1.png");
		
		final File simSunTTF = new File("/sdcard/picture/simsun.ttc");
		final File carreFour1PNG = new File("/sdcard/picture/carrefour1.png");
		final File carreFour2PNG = new File("/sdcard/picture/carrefour2.png");
		
		List<File> filesList = null;
		if(testName.contains("systest"))
		{
			filesList = new ArrayList<File>(){{
				add(carreFour1PNG);add(carreFour2PNG);add(simSunTTF);add(colorFile1);add(ihdrFile7);
				add(otherFile1);add(otherFile2);add(otherFile3);add(otherFile4);add(otherFile5);add(otherFile6);add(otherFile7);
				add(otherFile8);add(otherFile9);add(otherFile10);add(otherFile11);add(otherFile12);add(otherFile13);add(otherFile14);
				add(otherFile15);add(otherFile16);
				}};
		}
		for (File file:filesList) 
		{
			if(file.exists()==false)
			{
				strBuffer.append(file.getName()+"+++");
				ret = -1;
			}
		}
		if(strBuffer.length()>0)
		{
			strBuffer.delete(strBuffer.length()-3, strBuffer.length());
		}
		strBuffer.append(strBuffer.length()==0?"全部测试文件已导入":"文件未导入");
		return ret;
	}
	
	/***
	 * 打印测试图片是否全部存在
	 * @return
	 */
	public static int unitPrintJudge(String testName,StringBuffer strBuffer)
	{
		int ret=0;
		if(testName.equals("printer9"))
		{
			strBuffer.append("无需导入任何测试数据");
			return 0;
		}
		// printer1
		final File file1 = new File("/sdcard/picture/font.png");
		final File file2 = new File("/sdcard/picture/png.jpg");
		final File file3 = new File("/sdcard/picture/bmp01.bmp");
		final File abcLogoBmp = new File("/sdcard/picture/abclogo.bmp");
		
		final File xdlFileJPG = new File("/sdcard/picture/xdl.jpg");
		final File xdlFilePng = new File("/sdcard/picture/xdl.png");
		final File kbdFileJPG = new File("/sdcard/picture/kbd.jpg");
		final File kbdFilePNG = new File("/sdcard/picture/kbd.png");
		final File zxFileJPG = new File("/sdcard/picture/zx.jpg");
		final File zxFilePNG = new File("/sdcard/picture/zx.png");
		
		final File landiBlackPNG = new File("/sdcard/picture/landi_black.png");
		final File carreFour1PNG = new File("/sdcard/picture/carrefour1.png");
		final File carreFour2PNG = new File("/sdcard/picture/carrefour2.png");
		final File umsFile = new File("/data/share/ums");
		
		// color系列6张
		final File colorFile1 = new File("/sdcard/picture/color1.png");
		final File colorFile2 = new File("/sdcard/picture/color2.png");
		final File colorFile3 = new File("/sdcard/picture/color3.png");
		final File colorFile4 = new File("/sdcard/picture/color4.png");
		final File colorFile5 = new File("/sdcard/picture/color5.png");
		final File colorFile6 = new File("/sdcard/picture/color6.png");
		
		// other系列16张
		final File otherFile1 = new File("/sdcard/picture/other1.png");
		final File otherFile2 = new File("/sdcard/picture/other2.png");
		final File otherFile3 = new File("/sdcard/picture/other3.png");
		final File otherFile4 = new File("/sdcard/picture/other4.png");
		final File otherFile5 = new File("/sdcard/picture/other5.png");
		final File otherFile6 = new File("/sdcard/picture/other6.png");
		final File otherFile7 = new File("/sdcard/picture/other7.png");
		final File otherFile8 = new File("/sdcard/picture/other8.png");
		final File otherFile9 = new File("/sdcard/picture/other9.png");
		final File otherFile10 = new File("/sdcard/picture/other10.png");
		final File otherFile11 = new File("/sdcard/picture/other11.png");
		final File otherFile12 = new File("/sdcard/picture/other12.png");
		final File otherFile13 = new File("/sdcard/picture/other13.png");
		final File otherFile14 = new File("/sdcard/picture/other14.png");
		final File otherFile15 = new File("/sdcard/picture/other15.png");
		final File otherFile16 = new File("/sdcard/picture/other16.png");
		
		// 艺术字系列7张
		final File yszFile1 = new File("/sdcard/picture/ysz1.png");
		final File yszFile2 = new File("/sdcard/picture/ysz2.png");
		final File yszFile3 = new File("/sdcard/picture/ysz3.png");
		final File yszFile4 = new File("/sdcard/picture/ysz4.png");
		final File yszFile5 = new File("/sdcard/picture/ysz5.png");
		final File yszFile6 = new File("/sdcard/picture/ysz6.png");
		final File yszFile7 = new File("/sdcard/picture/ysz7.png");
		
		// IHDR系列15张
		final File ihdrFile1 = new File("/sdcard/picture/IHDR1.png");
		final File ihdrFile2 = new File("/sdcard/picture/IHDR2.png");
		final File ihdrFile3 = new File("/sdcard/picture/IHDR3.png");
		final File ihdrFile4 = new File("/sdcard/picture/IHDR4.png");
		final File ihdrFile5 = new File("/sdcard/picture/IHDR5.png");
		final File ihdrFile6 = new File("/sdcard/picture/IHDR6.png");
		final File ihdrFile7 = new File("/sdcard/picture/IHDR7.png");
		final File ihdrFile8 = new File("/sdcard/picture/IHDR8.png");
		final File ihdrFile9 = new File("/sdcard/picture/IHDR9.png");
		final File ihdrFile10 = new File("/sdcard/picture/IHDR10.png");
		final File ihdrFile11 = new File("/sdcard/picture/IHDR11.png");
		final File ihdrFile12 = new File("/sdcard/picture/IHDR12.png");
		final File ihdrFile13 = new File("/sdcard/picture/IHDR13.png");
		final File ihdrFile14 = new File("/sdcard/picture/IHDR14.png");
		final File ihdrFile15 = new File("/sdcard/picture/IHDR15.png");
		
		// ttf文件
		final File simSunTTF = new File("/sdcard/picture/simsun.ttc");
		final File iransansTTF = new File("/sdcard/picture/iransans.ttf");
		final File DroidSansFallbackTTF = new File("/sdcard/picture/DroidSansFallback.ttf");
		
		
		List<File> filesList = null;
		LoggerUtil.d("TestFileJudge unitPrintJudge->"+testName);
		if(testName.equals("printer1"))
		{
			filesList = new ArrayList<File>(){{add(file1);add(file2);add(file3);}};
		}
		else if(testName.equals("printer2"))
		{
			filesList = new ArrayList<File>(){{
						add(xdlFileJPG);add(simSunTTF);add(kbdFilePNG);add(landiBlackPNG);add(umsFile);
						
						add(yszFile1);add(yszFile2);add(yszFile3);add(yszFile4);add(yszFile5);add(yszFile6);add(yszFile7);
						add(colorFile1);add(colorFile2);add(colorFile3);add(colorFile4);add(colorFile5);add(colorFile6);
						add(ihdrFile1);add(ihdrFile2);add(ihdrFile3);add(ihdrFile4);add(ihdrFile5);add(ihdrFile6);add(ihdrFile7);add(ihdrFile8);
						add(ihdrFile9);add(ihdrFile10);add(ihdrFile11);add(ihdrFile12);add(ihdrFile13);add(ihdrFile14);add(ihdrFile15);
						add(otherFile1);add(otherFile2);add(otherFile3);add(otherFile4);add(otherFile5);add(otherFile6);add(otherFile7);
						add(otherFile8);add(otherFile9);add(otherFile10);add(otherFile11);add(otherFile12);add(otherFile13);add(otherFile14);
						add(otherFile15);add(otherFile16);
					}};
		}
		else if(testName.equals("printer3"))
		{
			filesList = new ArrayList<File>(){{add(iransansTTF);add(DroidSansFallbackTTF);}};
		}
		else if(testName.equals("printer4")||testName.equals("printer7"))
		{
			filesList = new ArrayList<File>(){{
				add(abcLogoBmp);add(zxFileJPG);
				
				add(yszFile1);add(yszFile2);add(yszFile3);add(yszFile4);add(yszFile5);add(yszFile6);add(yszFile7);
				add(colorFile1);add(colorFile2);add(colorFile3);add(colorFile4);add(colorFile5);add(colorFile6);
				add(ihdrFile1);add(ihdrFile2);add(ihdrFile3);add(ihdrFile4);add(ihdrFile5);add(ihdrFile6);add(ihdrFile7);add(ihdrFile8);
				add(ihdrFile9);add(ihdrFile10);add(ihdrFile11);add(ihdrFile12);add(ihdrFile13);add(ihdrFile14);add(ihdrFile15);
				add(otherFile1);add(otherFile2);add(otherFile3);add(otherFile4);add(otherFile5);add(otherFile6);add(otherFile7);
				add(otherFile8);add(otherFile9);add(otherFile10);add(otherFile11);add(otherFile12);add(otherFile13);add(otherFile14);
				add(otherFile15);add(otherFile16);
			}};
		}
		else if(testName.equals("printer5")||testName.equals("printer8"))
		{
			filesList = new ArrayList<File>(){{add(carreFour1PNG);add(carreFour2PNG);}};
		}
		else if(testName.equals("printer6"))
		{
			filesList = new ArrayList<File>(){{add(otherFile3);}};
		}
		for (File file:filesList) 
		{
			if(file.exists()==false)
			{
				strBuffer.append(file.getName()+"+++");
				ret = -1;
			}
		}
		if(strBuffer.length()>0)
		{
			strBuffer.delete(strBuffer.length()-3, strBuffer.length());
		}
		strBuffer.append(strBuffer.length()==0?"全部测试文件已导入":"文件未导入");
		return ret;
	}

}
