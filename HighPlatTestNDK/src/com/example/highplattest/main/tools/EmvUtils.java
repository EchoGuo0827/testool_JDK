package com.example.highplattest.main.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public class EmvUtils {
	
//	//读取节点信息
//	public static String readFile(String sys_path) {
//        String prop = "waiting";// 默认值
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(sys_path));
//            prop = reader.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.w("eric_chen", " ***ERROR*** Here is what I know: " + e.getMessage());
//        } finally {
//            if(reader != null){
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        Log.w("eric_chen", "readFile cmd from"+sys_path + "data"+" -> prop = "+prop);
//        return prop;
//    }
    
	
	
	
	
	public  static String readDevNode(String sys_path){
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("cat " + sys_path); // 此处进行读操作
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line ;
            StringBuffer result = new StringBuffer();
            while ((line = br.readLine()) != null) {
            	result.append(line).append("\r\n");
			}
            is.close();
            Log.d("eric_chen", "result=="+result.toString());
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
//            gui.cls_show_msg1(gKeepTimeErr,SERIAL,"line %d:%s读接口节点抛出异常（%s）", Tools.getLineInfo(),TESTITEM,e.getMessage());
        }
        Log.d("eric_chen", "---读不到---");
        return null;
    }
	
	public static boolean setNodeString(String path,String value){
	    try {
	        BufferedWriter bufWriter = null;
	        bufWriter = new BufferedWriter(new FileWriter(path));
	        bufWriter.write(value);  // 写入数据
	        bufWriter.close();
	        Log.e("eric_chen","改写节点成功!");
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e("eric_chen","改写节点失败!");
	        return false;
	    }
	    return true;
	}
    

}
