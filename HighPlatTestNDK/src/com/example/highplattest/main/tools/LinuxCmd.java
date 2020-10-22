package com.example.highplattest.main.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


import android.util.Log;

/**
 * 执行linux命令
 * 
 * @author maxw
 * 
 */
public class LinuxCmd {

	/**
	 * 执行linux命令
	 * 
	 * @param path
	 *            工作目录
	 * @param cmd
	 *            执行指令
	 * @throws IOException 
	 */
	public static String execCmd(String path, String cmd) throws IOException {
		Log.e(LinuxCmd.class.getSimpleName(), cmd);
		// 创建进程生成器
		ProcessBuilder processBuilder = new ProcessBuilder("/system/bin/sh");
		// 设置新建进程的工作目录
		File dir = new File(path);
		processBuilder.directory(dir);
		// 将errorinputstream也定义到inputstream中
		processBuilder.redirectErrorStream(true);
		// 启动新进程
		Process process = processBuilder.start();

		// 获取进程输入流
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		// 获取进程输出流
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		// 执行linux命令似乎必须用PrintWriter
		PrintWriter pw = new PrintWriter(bw, true);

		// 执行命令
		pw.println(cmd);
		// 执行结束，退出
		pw.println("exit");

		StringBuffer sb = new StringBuffer();
		String tempStr;
		while ((tempStr = br.readLine()) != null) 
		{
			sb.append(tempStr);
			sb.append("\n");
		}
		LoggerUtil.i("the output is : " + sb.toString());

		br.close();
		bw.flush();
		bw.close();
		pw.flush();
		pw.close();
		process.destroy();
		return sb.toString();
	}
	
	/**
	 * 读设备节点内容
	 * @param sys_path 要读取的节点
	 * @return	返回读取到的节点值
	 */
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
            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
//            gui.cls_show_msg1(gKeepTimeErr,SERIAL,"line %d:%s读接口节点抛出异常（%s）", Tools.getLineInfo(),TESTITEM,e.getMessage());
        }
        return null;
    }
}
