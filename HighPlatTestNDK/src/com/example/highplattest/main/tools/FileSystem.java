package com.example.highplattest.main.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;


import android.util.Log;

/************************************************************************
 * 
 * module 			: main
 * file name 		: FileSystem.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20160406
 * directory 		: 
 * description 		: 文件系统操作类
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class FileSystem implements NDK 
{
	
	/**
	 * 打开Android端文件文件
	 * @param path   文件路径
	 * @param mode   打开模式"r"（已只读方式打开，如果不存在则失败）or "w"（以写的方式打开，如果文件不存在则创建）
	 * @return 
	 * 			JDK_OK 操作成功 
	 * 			JDK_PARA_ERR 参数错误（文件名为NULL，模式不正确） 
	 * 			JDK_FS_PATH_ERR 文件路径错误
	 * 			JDK_FS_CREATE_FAIL 创建文件失败
	 * 			JDK_FS_NO_EXIST  文件不存在
	 */
	public int JDK_FsOpen(String path, String mode) 
	{
		int value = 0;
		File file;
		// 异常情况
		if (path.equals("") || path == null||(!mode.equalsIgnoreCase("r") && !mode.equalsIgnoreCase("w"))) 
		{
			value = NDK_ERR_PARA;
			return value;
		}
		file = new File(path);
		if (mode.equalsIgnoreCase("w")) // 以写模式打开 若文件不存在会创建
		{
			if (!file.exists()) 
			{
				try 
				{
					// 创建目录
					Log.d("mkdir:", file.getPath());
					JDK_FsCreateDirectory(file.getPath());
					// 创建文件
					if (file.createNewFile()) {
						value = JDK_OK;
					} else {
						value = JDK_FS_CREATE_FAIL;
					}
				} catch (IOException e) {
					e.printStackTrace();
					return JDK_FS_PATH_ERR;
				}
			}
		} else if (mode.equalsIgnoreCase("r")) 
		{
			if (!file.exists()) {
				value = JDK_FS_NO_EXIST;
			} else {
				value = JDK_OK;
			}
		}
		return value;
	}
	

	/**
	 * @description 从打开的文件当前指针读unLength个字符到缓冲区psBuffer
	 * @param path  	文件路径
	 * @param psBuffer   读缓冲区
	 * @param offset     读取文件的偏移位置
	 * @param unLength   需要读取的字符的长度
	 * @return 返回实际读到的数据长度 
	 * 		    JDK_PARA_ERR 参数错误 
	 * 			JDK_FS_READ_FAIL 读取失败
	 * 			JDK_FS_NO_EXIST 文件不存在
	 */
	public int JDK_FsRead(FileInputStream fileIn, byte[] psBuffer,int length)
	{
		int ret = JDK_OK;
		
		try {
			if ((ret = fileIn.read(psBuffer,0,length)) >0) 
			{}
			else
				ret = JDK_FS_READ_FAIL;
		} catch (IOException e) {
			e.printStackTrace();
			ret = JDK_FS_READ_FAIL;
		}
//		LoggerUtil.d("JDK_FsRead:"+ret);
		return ret;
	}
	
	/**
	 * @description 从打开的文件当前指针读unLength个字符到缓冲区psBuffer
	 * @param path  	文件路径
	 * @param psBuffer   读缓冲区
	 * @param offset     读取文件的偏移位置
	 * @param unLength   需要读取的字符的长度
	 * @return 返回实际读到的数据长度 
	 * 		    JDK_PARA_ERR 参数错误 
	 * 			JDK_FS_READ_FAIL 读取失败
	 * 			JDK_FS_NO_EXIST 文件不存在
	 */
	public long JDK_FsReadOffeset(String fileName,byte[] psBuffer,int offset,int unLength,int mode )
	{
		long rlen = 0;
		RandomAccessFile randomAccessFile=null;
		if(fileName == null||fileName.equals(""))
			return NDK_ERR_PARA;
		try 
		{
			randomAccessFile = new RandomAccessFile(fileName, "rw");
			switch (mode) 
			{
			case 0:
				randomAccessFile.read(psBuffer, 0, unLength);
				rlen = randomAccessFile.length();
				break;
				
			case 2:
				randomAccessFile.seek(offset);
				rlen = randomAccessFile.read(psBuffer, 0, unLength);
				break;

			default:
				break;
			}
			randomAccessFile.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(randomAccessFile!=null)/**存储空间写满之后可能出现该问题*/
				try {
					randomAccessFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		return rlen;
	}
	
	/**
	 * 输入流
	 * @param input
	 */
	public int JDK_FsClose(InputStream input)
	{
		int nRet = JDK_OK;
		
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
			nRet = JDK_FS_CLOSE_FAIL;
		}
		return nRet;
	}
	
	/**
	 * 一次读一行文件
	 * @param path
	 * @param psBuffer
	 * @return
	 * @throws IOException
	 */
	public int JDK_FsReadLine(String path, String[] psBuffer) 
	{
		int ret = JDK_OK;
		String readStr;
		int i = 0;
		
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) 
			ret = JDK_PARA_ERR;
		BufferedReader fis = null;
		try 
		{
			fis = new BufferedReader(new FileReader(file));
			while((readStr = fis.readLine())!=null&&i<psBuffer.length)
			{
				psBuffer[i] = readStr;
				i++;
				ret = i;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 关闭数据流
		try {
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @description 向打开的文件写入数据
	 * @param file:文件句柄	psBuffer:需要写入文件内容的缓冲区数据
	 * @return 成功返回NDK_OK，文件不存在返回NDK_ERR_PARA
	 */
	public int JDK_FsWrite(File file, byte[] psBuffer) throws IOException
	{
		int ret = NDK_OK;
		if (!file.exists() || file == null) 
		{
			ret = NDK_ERR_PARA;
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(psBuffer);
		//关闭数据流
		fos.close(); 
		
		return ret;
		
	}
	
	/**
	 * @description 向打开的文件读取数据
	 * @param file:文件句柄	psBuffer:读取文件的数据存储缓冲区
	 * @return 成功返回文件长度，文件不存在返回NDK_ERR_PARA
	 */
	public int JDK_FsRead(String fname, byte[] psBuffer,int offset,int len)
	{
		int ret = NDK_OK;
		File file = new File(fname);
		if (!file.exists() || file == null) 
		{
			ret = NDK_ERR_PARA;
		}
		
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(file);
			ret = fis.read(psBuffer,offset,len);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally
		{
			//关闭数据流
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	public long JDK_FsWrite(String fileName,byte[] psBuffer,int unLength,int mode )
	{
		long wrlen = 0;
		RandomAccessFile randomAccessFile=null;
		if(fileName == null||fileName.equals(""))
			return NDK_ERR_PARA;
		try 
		{
			randomAccessFile = new RandomAccessFile(fileName, "rw");
			long fileLength = randomAccessFile.length();
			switch (mode) 
			{
			case 0:
				randomAccessFile.write(psBuffer, 0, unLength);
				wrlen = randomAccessFile.length();
				break;
				
			case 2:
				randomAccessFile.seek(fileLength);
				randomAccessFile.write(psBuffer, 0, unLength);
				wrlen = randomAccessFile.length()-fileLength;
				break;

			default:
				break;
			}
			randomAccessFile.close();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			if(randomAccessFile!=null)/**存储空间写满之后可能出现该问题*/
				try {
					LoggerUtil.v("close file");
					randomAccessFile.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		return wrlen;
	}

	/**
	 * @param pszName 要删除的文件名
	 * @return JDK_OK 操作成功 
	 * 		   JDK_PARA_ERR 参数错误
	 * 		   JDK_FS_NO_EXIST 文件不存在
	 * 		   JDK_FS_DEL_FAIL 删除文件失败
	 */
	public int JDK_FsDel(String pszName) 
	{
		if (pszName == null || pszName.equals(""))
			return JDK_PARA_ERR;
		File file = new File(pszName);
		if (file.exists()) 
		{
			if (!file.delete())
				return JDK_FS_DEL_FAIL;
		} else
			return JDK_FS_NO_EXIST;
		return JDK_OK;
	}

	/**
	 * 文件重命名
	 * @param srcName  源文件名
	 * @param DstName  目标文件名
	 * @return NDK_OK 操作成功 NDK_ERR 操作失败 NDK_ERR_PARA 参数错误
	 */
	public int JDK_FsRename(String path, String srcName, String DstName) {
		int value = 0;
		if (path == null || path.equals("") || srcName.equals("")
				|| srcName == null || DstName.equals("") || DstName == null) {
			value = NDK_ERR_PARA;
			return value;
		}
		// 原文件跟目标文件名不同时才进行重命名
		if (!srcName.equals(DstName)) {
			File srcFile = new File(path + srcName);
			File dstFile = new File(path + DstName);
			if (dstFile.exists()) {
				value = NDK_ERR;
			} else {
				if (srcFile.renameTo(dstFile)) {
					value = NDK_OK;
				} else {
					value = NDK_ERR;
				}
			}
		}

		return value;
	}
	
	/**
	 * 测试报告是否存在不存在的话就生成测试报告
	 * @param path	文件路径
	 * @return 
	 * 			JDK_OK 操作成功（文件存在） 
	 * 			JDK_FS_NO_EXIST 文件不存在
	 * 			JDK_PARA_ERR 参数错误
	 */
	public int JDK_FsExist(String path) 
	{
		int value;
		if (path.equalsIgnoreCase("") || path == null) {
			value = JDK_PARA_ERR;
			return value;
		}
		File file = new File(path);
		if (file.exists())
			value = JDK_OK;
		else
			value = JDK_FS_NO_EXIST;
		return value;
	}
	
	

	public long JDK_FsFileSizes(File f) 
	{
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++){
			if (flist[i].isDirectory()){
				size = size + JDK_FsFileSizes(flist[i]);
			}
			else{
				LoggerUtil.d("path="+flist[i].getAbsolutePath());
				size =size + JDK_FsFileSize(flist[i].getAbsolutePath());
			}
		}
	return size;
	}
	
	/**
	 * 获取文件大小
	 * @param pszName
	 * @param punSize
	 * @return 返回文件的大小
	 * 			JDK_PARA_ERR 参数错误
	 * 			JDK_FS_SIZE_FAIL 获取文件大小失败
	 * 			JDK_FS_CLOSE_FAIL 文件关闭失败
	 * 			JDK_FS_NO_EXIST 文件不存在
	 */
	public int JDK_FsFileSize(String pszName) {
		int nRet = JDK_OK;
		if (pszName == null || pszName.equals("")) {
			return JDK_PARA_ERR;
		}
		File file = new File(pszName);
		if (file.exists()) 
		{
			try {
				FileInputStream fis = new FileInputStream(file);
				try {
					nRet = fis.available();
				} catch (IOException e) {
					e.printStackTrace();
					nRet = JDK_FS_SIZE_FAIL;
				}
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
					nRet = JDK_FS_CLOSE_FAIL;
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				nRet = JDK_FS_NO_EXIST;
			}
		} else {
			nRet = JDK_FS_NO_EXIST;
		}
		return nRet;
	}

	/**
	 * 文件截断 NDK_FsTruncate()会将参数pszPath指定的文件大小改为参数len指定的大小。如果原来的文件大小
	 * 比参数len大，则超过的部分会被删除。如果原来文件的大小比len小的话，不足的部分将会补上0xff
	 * 
	 * @param pszPath
	 *            文件路径
	 * @param len
	 *            所要截断的长度
	 * @return NDK_OK 操作成功 NDK_ERR_PARA 参数错误 NDK_ERR_PATH 文件路径非法 NDK_ERR 操作失败
	 *         NDK_ERR_WRITE 写文件失败
	 */
	int NDK_FsTruncate(String pszPath, int len) {
		int size;

		if (pszPath == null || pszPath.equals("")) {
			return NDK_ERR_PARA;
		}
		File file = new File(pszPath);
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				FileOutputStream outputStream = new FileOutputStream(file);
				size = inputStream.available();
				// 补上0xff
				if (len > size) {
					int time = (len - size) / 1024;
					int remainder = (len - size) % 1024;
					byte[] buffer = new byte[1024];
					Arrays.fill(buffer, (byte) 0xff);
					List<byte[]> list = new ArrayList<byte[]>();
					for (int i = 0; i < time; i++) {
						list.add(buffer);
					}
					byte[] buf1 = new byte[remainder];
					Arrays.fill(buf1, (byte) 0xff);
					list.add(buf1);
					for (Iterator<byte[]> i = list.iterator(); i.hasNext();) {
						// 将ArrayList里的内容重新写回之前的文件
						outputStream.write(i.next());
					}
					inputStream.close();
					outputStream.close();
				}
				// 删除多余的部分
				else if (len < size) {
					RandomAccessFile raFile = new RandomAccessFile(pszPath,
							"rw");
					raFile.seek(len);
					List<byte[]> list = new ArrayList<byte[]>();
					byte[] buffer = new byte[1024];
					while (raFile.read(buffer) != -1)
						list.add(buffer);
					for (Iterator<byte[]> i = list.iterator(); i.hasNext();) {
						// 将ArrayList里的内容重新写回之前的文件
						outputStream.write(i.next());
					}
					raFile.close();
					inputStream.close();
					outputStream.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			return NDK_ERR_PATH;
		}
		return NDK_OK;
	}

	/**
	 * 创建目录
	 * 
	 * @param pszName
	 *            目录名称
	 * @return NDK_OK 操作成功 NDK_ERR_PARA 参数错误 NDK_ERR 操作失败 NDK_ERR_PATH 文件路径错误
	 */
	public int JDK_FsCreateDirectory(String pszName) {
		if (pszName == null || pszName.equals(""))
			return NDK_ERR_PARA;
		File file = new File(pszName);
		if (file.isDirectory()) {
			if (!file.exists())
				file.mkdirs();
			if (!file.exists())
				return NDK_ERR;
		} else {
			return NDK_ERR_PATH;
		}
		return NDK_OK;
	}

	/**
	 * 删除目录
	 * 
	 * @param pszName
	 *            目录名称
	 * @return NDK_OK 操作成功 NDK_ERR 操纵失败 NDK_ERR_PARA 参数非法 NDK_ERR_PATH 文件路径错误
	 */
	public int NDK_FsRemoveDirectory(String pszName) {
		File file = new File(pszName);
		return NDK_FsRemoveFile(file);
	}

	public int NDK_FsRemoveFile(File file) {
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return NDK_OK;
			}
			for (File f : childFile) {
				NDK_FsRemoveFile(f);
			}
			file.delete();
		}
		return NDK_OK;
	}

	/**
	 * 列出制定目录下的所有文件
	 * 
	 * @param pszPath
	 *            传入的psBuf的size一定要够大，否则会出现溢出情况psBuf每20个字节存储一个文件名
	 * 
	 * @param psBuf
	 *            指定要读取的目录
	 * @param punNum
	 *            返回该文件夹目录的文件总数
	 * @return NDK_OK 操作成功 NDK_ERR_PARA 参数非法 NDK_ERR_PATH 文件路径非法
	 */
	public int NDK_FsDir(String pszPath, int mode, String psBuf, int punNum) {
		if (pszPath == null || psBuf.equals(""))
			return NDK_ERR_PARA;
		File file = new File(pszPath);
		GlobalVariable.file_count = 0;
		if (file.exists())
			listDir(file);
		else
			return NDK_ERR_PATH;
		return NDK_OK;
	}

	public void listDir(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("目录：" + file.getName());
				listDir(file);
			} else {
				// 文件
				GlobalVariable.file_count++;
			}
		}
	}


	/**
	 * 显示目录和子目录的方法.
	 */
	void display(File fileObj, String directoryName) {
		if (fileObj.isDirectory()) {
			System.out.println("目录是 : " + directoryName);
			String[] fileName = fileObj.list();

			for (int ctr = 0; ctr < fileName.length; ctr++) {
				File nextFileObj = new File(directoryName + "/" + fileName[ctr]);

				if (nextFileObj.isDirectory()) {
					System.out.println(fileName[ctr] + " 是一个目录");
				} else {
					System.out.println(fileName[ctr] + " 是一个文件");
				}
			}
		} else {
			System.out.println(directoryName + " 不是一个有效目录");
		}
	}
	/**
	 * 写入指定文件
	 * @param path 文件路径
	 * @param msg  写如内容
	 * @return
	 */
	public int JDK_FsWriteToFile(String path,String msg) {
  		if(JDK_FsOpen(path, "w")<0)
  		{
  			return -1;
  		}
  		else
  		{
  			if(JDK_FsWrite(path, msg.getBytes(), msg.getBytes().length, 2)!= msg.getBytes().length)
  			{
  				return -1;
  			}
  			
  		}
  		return 0;
  	}
	
	/**判断某个文件或目录是否存在*/
	public static boolean dirIsExists(String path)
	{
		File file = new File(path);
		return file.exists();
	}
	
	
	/**
	 * 复制文件目录
	 * @param srcDir 要复制的源目录 eg:/mnt/sdcard/DB
	 * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
	 * @return 
	 */
	public static boolean copyDir(String srcDir, String destDir){
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if(!sourceDir.exists()){
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) 
        {
        	Log.i("sourceDir", sourceDir.getPath());
            File[] fileList = sourceDir.listFiles();
            if(fileList==null)
            {
            	return false;
            }
            File targetDir = new File(destDir);
            Log.i("copyDir", "sourceDir="+fileList.length+"|||target="+targetDir);
            //创建目标目录
            if(!targetDir.exists()){
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for(int i= 0;i<fileList.length;i++){
            	Log.d("path1", "path1="+fileList[i].getPath());
                if(fileList[i].isDirectory()){//如果如果是子目录进行递归
                	copyDir(fileList[i].getPath()+ "/", 
                			destDir + fileList[i].getName() + "/");
                }else{//如果是文件则进行文件拷贝
                	copyFile(fileList[i].getPath(), destDir +fileList[i].getName());
                }
            }
            return true;
		}else {
			copyFileToDir(srcDir,destDir);
			return true;
		}
    }
	
	
    /**
     * 复制文件（非目录）
     * @param srcFile 要复制的源文件  
     * @param destFile 复制到的目标文件 
     * @return
     */
    private static boolean copyFile(String srcFile, String destFile){
    	Log.d("copyFile", "srcFile="+srcFile+"|||destFile="+destFile);
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
            	streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }
    
    
    /**
     * 把文件拷贝到某一目录下
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir){
    	File fileDir = new File(destDir);
    	if (!fileDir.exists()) {
			fileDir.mkdir();
		}
    	String destFile = destDir +"/" + new File(srcFile).getName();
    	File file = new File(destFile);
    	if(file.exists()==false)
    	{
    		try {
				file.createNewFile();
				setChmod(destFile);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
    	}
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
            	streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }
    
    private static void setChmod(String path)
    {
 	   Process p;
 	   int status;
 		try {
 			p = Runtime.getRuntime().exec("chmod 777 " + path);
 			status = p.waitFor();
 			if (status == 0) 
 			{
 				Log.d("chmod suc", "chmod suc");
 			} else {
 				Log.d("chmod fail", "chmod fail");
 			}
 		} catch (IOException e) {
 			e.printStackTrace();
 		} catch (InterruptedException e) {
 			e.printStackTrace();
 		}
    }
 
    
    /**
     * 移动文件目录到某一路径下
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean moveFile(String srcFile, String destDir) {
    	//复制后删除原目录
		if (copyDir(srcFile, destDir)) {
			deleteFile(new File(srcFile));
			return true;
		}
    	return false;
	}
 
	/**
	 * 删除文件（包括目录）
	 * @param delFile
	 */
	public static boolean deleteFile(File delFile) {
		return delFile.delete();
//		//如果是目录递归删除
//	  if (delFile.isDirectory()) {
//	   File[] files = delFile.listFiles();
//	   for (File file : files) {
//	     deleteFile(file);
//	   }
//	  }else{
//		  delFile.delete();
//	  }
////	  //如果不执行下面这句，目录下所有文件都删除了，但是还剩下子目录空文件夹
////	  delFile.delete();
	}
}
