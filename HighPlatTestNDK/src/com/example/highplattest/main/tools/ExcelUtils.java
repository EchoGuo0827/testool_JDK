package com.example.highplattest.main.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;



import android.R.integer;
import android.util.Log;


/**
 * 
 * @author chending 20200618
 * excel工具类
 */
public class ExcelUtils {
	
	/**
	 * 读取Excel文件返回路由器的行数  
	 */
	public int readxlsx2(String wifiname,int excelsheet){
		//文件固定 
		String filepath="sdcard/wifiV1.6.xls";
		String wifissid="";
		int rowline = 50;
		File file=new File(filepath);
		try {
			FileInputStream fileStream = new FileInputStream(file);
			HSSFWorkbook workbook=new HSSFWorkbook(fileStream);
			HSSFSheet sheet=workbook.getSheetAt(excelsheet);
			if (sheet==null) {
				  throw new IOException("无该sheet"); 
			}
			for (int i = 6; i < sheet.getLastRowNum(); i++) {
				HSSFRow row=sheet.getRow(i);
				HSSFCell cell=row.getCell(4);
				wifissid=cell.getStringCellValue();
				
				if (wifissid.contains(wifiname)) {
					rowline=row.getRowNum();
					Log.d("eric_chen", "rowline:"+rowline);
					return rowline;
				}
				Log.d("eric_chen", "wifissid:"+wifissid);
			}
			
			  fileStream.close();
			
		} catch (IOException e) {
			// TODO: handle exception
		}
		return rowline;		
	}
	
	/**
	 * 查找要写入EXCEL文件的位置
	 * @param fileName excel的文件名字
	 * @param colValue 查找第几列的行值
	 * @param rowName 行名字的判断
	 * @param colName 列名字的判断
	 * @return
	 */
	public HashMap<String,Integer> searchLocatXls(String fileName,int colValue,String rowName,String colName){
		LoggerUtil.e("searchLocatXls->model="+colName);
		HashMap<String,Integer> searchMap = new HashMap<String,Integer>();
		searchMap.put("A", -1);
		searchMap.put("B", -1);
		//文件固定 
		int rowline = 50;
		int columnLine = 10;
		File file=new File(fileName);
		try {
			FileInputStream fileStream = new FileInputStream(file);
			HSSFWorkbook workbook=new HSSFWorkbook(fileStream);
			HSSFSheet sheet=workbook.getSheetAt(1);// 第几个表格
			if (sheet==null) {
				  throw new IOException("无该sheet"); 
			}
			HSSFRow modelRow = sheet.getRow(2);// 固定第几行
			for (int i = 4; i <modelRow.getLastCellNum(); i++) {
				HSSFCell cell = modelRow.getCell(i);
				LoggerUtil.d("readxlsRow->colomn="+cell.getStringCellValue());
				if (cell.getStringCellValue().contains(colName)) {
					columnLine=i;
					LoggerUtil.d("readxlsRow->colomn="+columnLine);
					break;
				}
			}
			for (int i = 3; i < sheet.getLastRowNum(); i++) {
				HSSFRow row=sheet.getRow(i);
				HSSFCell cell=row.getCell(colValue);// 去比对第3列的值是否有符合 或者第2列的值是否符合
				if(cell==null)
					continue;
				if (cell.getStringCellValue().contains(rowName)) {
					rowline=row.getRowNum();
					Log.d("eric_chen", "rowline:"+rowline+"==="+cell.getStringCellValue());
					break;
				}
			}
			searchMap.put("A", rowline);
			searchMap.put("B", columnLine);
			fileStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchMap;		
	}
	
	public int writeDataXls(String fileName,HashMap<String, Integer> locationMap,String data){
		
		LoggerUtil.d("writeDataXls->"+locationMap.get("A")+"==="+locationMap.get("B")+"===="+data);
		if(locationMap.get("A")<0||locationMap.get("B")<0)
			return -1;
		
		FileInputStream fileInputStream=null;
		FileOutputStream out = null;
		try {
			
			fileInputStream=new FileInputStream(fileName);
			POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream); 
			HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
			HSSFSheet sheet=Workbook.getSheetAt(1);
			HSSFRow row=sheet.getRow(locationMap.get("A"));  
		    out=new FileOutputStream(fileName); 
		    Log.d("eric_chen", "ROW: "+ row.getCell(2).getStringCellValue()+"======"+row.getLastCellNum());

	       
//	        //从第23列开始 下标22
//	        for (int i = 0; i < datalist.size(); i++) {
////	        	row.createCell(i+22).setCellStyle(style);
//	        	if (datalist.get(i)==null||datalist.get(i).equals("n")) {
		    	row.createCell(locationMap.get("B"));
	        		row.getCell(locationMap.get("B")).setCellValue(data);
//	        		 row.createCell(i+22).setCellValue("null");
//				}else {
////					 row.createCell(i+22).setCellValue(datalist.get(i)); 
//						row.getCell(i+22).setCellValue(datalist.get(i));
//				}
	
//			}

	        		out.flush();
	    	        Workbook.write(out);		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{

	        try {
				out.close();
				fileInputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		}
		
		return 0;		
	}
	
	public int writeDataXlsArrays(String fileName,HashMap<String, Integer> locationMap,String[] data){
		
		LoggerUtil.d("writeDataXls->"+locationMap.get("A")+"==="+locationMap.get("B"));
		if(locationMap.get("A")<0||locationMap.get("B")<0)
			return -1;
		
		FileInputStream fileInputStream=null;
		FileOutputStream out = null;
		try {
			
			fileInputStream=new FileInputStream(fileName);
			POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream); 
			HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
			HSSFSheet sheet=Workbook.getSheetAt(1);
			
			for (int j = 0; j < data.length; j++) {
				HSSFRow row=sheet.getRow(locationMap.get("A")+j); // 每次行数移动
			    out=new FileOutputStream(fileName); 
			    Log.d("eric_chen", "ROW: "+ row.getCell(2).getStringCellValue()+"======"+data[j]);
				row.createCell(locationMap.get("B"));
				if(data[j].equals("NA"))
					row.getCell(locationMap.get("B")).setCellValue(data[j]);
				else if (data[j].equals("NULL")) 
					row.getCell(locationMap.get("B")).setCellValue(data[j]);
				else if(data[j].equals("ERR"))
					row.getCell(locationMap.get("B")).setCellValue(data[j]);
				else
					row.getCell(locationMap.get("B")).setCellValue(Double.valueOf(data[j]));
			    
			    out.flush();
			    Workbook.write(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
	        try {
				out.close();
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return 0;		
	}
	
	
	/**
	 * 创建Excel表格  by chending20200615
	 */
	public void createExcel(){
		
		//获取当前时间
		Date d=new Date();
		SimpleDateFormat  sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowdate=sdf.format(d);
		
		String filepath="sdcard/wifi.xls";
		File file=new File(filepath);
		FileOutputStream fileOutputStream=null;
		HSSFWorkbook workbook =new HSSFWorkbook();
		//创建工作表
		HSSFSheet sheet = workbook.createSheet("wifi兼容性");
	
		workbook.setSheetName(0, "wifi兼容性");
		// 创建字体
		HSSFFont font = workbook.createFont();
//		font.setColor(HSSFFont.COLOR_NORMAL);		
		font.setFontHeight((short)3);;
		font.setFontName("宋体");
	    font.setFontHeightInPoints((short) 15);
	 // 创建样式
	    HSSFCellStyle style = workbook.createCellStyle();
	    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
	    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平   
	    style.setFont(font);
	  //创建第一行
	    HSSFRow row=sheet.createRow(0);
	    createExcelCell(row,0,style,nowdate);
	    //创建第二行
	    HSSFRow row1 = sheet.createRow((short) 1); 
	    String []  wifiname={"路由器名称","连接加数传","建链压力","性能成功率","双向速率","打开wifi时间","扫描wifi时间","连接wifi时间","断开wifi时间","关闭wifi时间"};
	    for (int i = 0; i < wifiname.length; i++) {
	    	sheet.setColumnWidth(i, 50*256);
	    	createExcelCell(row1,i,style,wifiname[i]);
		}
	    try {
			fileOutputStream = new FileOutputStream(file);
			workbook.write(fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();// 操作结束，关闭文件
		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
	    
	    
		
	}
	/*
	 * 创建单元格 by chending 20200615
	 */
	public  void createExcelCell(HSSFRow rows, int col,HSSFCellStyle style, String contents) {
		HSSFCell cell1 = rows.createCell((short) col);
		cell1.setCellStyle(style);
		cell1.setCellValue(contents);
	}
	
	/*
	 * 封装wifi兼容测试使用方法 by chending 20200615
	 */
	public void insertExceldata(int Row,ArrayList<Integer> datalist,int excelsheet){
		try {
			
			FileInputStream fileInputStream=new FileInputStream("sdcard/wifiV1.6.xls");
			POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream); 
			HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
//			HSSFSheet sheet=Workbook.getSheet("wifi兼容性");  //根据name获取sheet表
			HSSFSheet sheet=Workbook.getSheetAt(excelsheet);
			HSSFRow row=sheet.getRow(Row);  
			
//		//设置字体		
//			HSSFFont font = Workbook.createFont();
////			font.setColor(HSSFFont.COLOR_NORMAL);		
//			font.setFontHeight((short)3);;
//			font.setFontName("宋体");
//		    font.setFontHeightInPoints((short) 15);
//		 // 创建样式
//		    HSSFCellStyle style = Workbook.createCellStyle();
//		    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
//		    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平   
//		    style.setFont(font);
		    
		    FileOutputStream out=new FileOutputStream("sdcard/wifiV1.6.xls"); 
	
		    Log.d("eric_chen", "ROW: "+ sheet.getLastRowNum());
//	        row=sheet.createRow(Row); 

	       
	        //从第23列开始 下标22
	        for (int i = 0; i < datalist.size(); i++) {
//	        	row.createCell(i+22).setCellStyle(style);
	        	if (datalist.get(i)==null||datalist.get(i)==-100) {
	        		row.getCell(i+22).setCellValue("null");
//	        		 row.createCell(i+22).setCellValue("null");
				}else {
//					 row.createCell(i+22).setCellValue(datalist.get(i)); 
						row.getCell(i+22).setCellValue(datalist.get(i));
				}
	
			}

	        out.flush();
	        Workbook.write(out);
	        out.close();    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void insertExceldata2(int Row,ArrayList<String> datalist,int excelsheet){
		try {
			
			FileInputStream fileInputStream=new FileInputStream("sdcard/wifiV1.6.xls");
			POIFSFileSystem poifsFileSystem=new POIFSFileSystem(fileInputStream); 
			HSSFWorkbook Workbook=new HSSFWorkbook(poifsFileSystem);//得到文档对象
//			HSSFSheet sheet=Workbook.getSheet("wifi兼容性");  //根据name获取sheet表
			HSSFSheet sheet=Workbook.getSheetAt(excelsheet);
			HSSFRow row=sheet.getRow(Row);  
			
//		//设置字体		
//			HSSFFont font = Workbook.createFont();
////			font.setColor(HSSFFont.COLOR_NORMAL);		
//			font.setFontHeight((short)3);;
//			font.setFontName("宋体");
//		    font.setFontHeightInPoints((short) 15);
//		 // 创建样式
//		    HSSFCellStyle style = Workbook.createCellStyle();
//		    style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直   
//		    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平   
//		    style.setFont(font);
		    
		    FileOutputStream out=new FileOutputStream("sdcard/wifiV1.6.xls"); 
	
		    Log.d("eric_chen", "ROW: "+ sheet.getLastRowNum());
//	        row=sheet.createRow(Row); 

	       
	        //从第23列开始 下标22
	        for (int i = 0; i < datalist.size(); i++) {
//	        	row.createCell(i+22).setCellStyle(style);
	        	if (datalist.get(i)==null) {
	        		row.getCell(i+25).setCellValue("null");
//	        		 row.createCell(i+22).setCellValue("null");
				}else {
//					 row.createCell(i+22).setCellValue(datalist.get(i)); 
						row.getCell(i+25).setCellValue(datalist.get(i));
				}
	
			}

	        out.flush();
	        Workbook.write(out);
	        out.close();    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	

}
