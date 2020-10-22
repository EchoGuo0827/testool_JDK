package com.example.highplattest.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * module 			: 其他相关
 * file name 		: other23.java 
 * history 		 	: 变更记录									变更时间			变更人员
 *			  	     MD5文件校验(850导入) 						20200717  		郑薛晴
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Other23 extends UnitFragment{
	public final String TAG = Other23.class.getSimpleName();
	private final String TESTITEM = "MD5校验(N850)";
	Gui gui = new Gui(myactivity, handler);
	
	public void other23()
	{
		// 校验md5文件
		if(checkCaFile())
		{
			gui.cls_show_msg1_record(TAG, "other23", 2,"md5校验通过，测试通过");
		}
		else
		{
			gui.cls_show_msg1_record(TAG, "other23", 2,"line %d:md5校验失败",Tools.getLineInfo());
		}
	}
	
	private String getMD5(File file) {
		FileInputStream fileInputStream = null;
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			BigInteger bigInt = new BigInteger(1, MD5.digest());
			String md5 = bigInt.toString(16);
			return md5;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	    
	private boolean checkCaFile() {
		Map<String, String> calist = new HashMap<String, String>();
		calist.put("/newland/factory/ca.tar.gz","b5ee211e8dc0c85d3c217689c977ab43");
		calist.put("/newland/factory/ca/Controller_CA_v1.crt","7cafca1950592eed9a130f341beab11");
		calist.put("/newland/factory/ca/Newland_Controller_CA.crt","d46d7d991d96eb66a135a0a7416f5582");
		calist.put("/newland/factory/ca/Newland_MFG_CA.crt","b93e039825a6de38f4b6429e75856c75");
		calist.put("/newland/factory/ca/Newland_Production_CA.crt","30eb5fd4a22a995e6285cb48152b54a1");
		calist.put("/newland/factory/ca/Newland_Root_CA.crt","9c00b1e870b239794afc1c7ef2a312ac");
		calist.put("/newland/factory/ca/Production_CA_v1.crt","d5249b2fc529e0d449afdad614d64498");
		Set set = calist.entrySet();
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			 if(!new File(key).exists() ||!((value).equals(getMD5(new File(key)))))
			 return false;
		}
		return true;
	}

	@Override
	public void onTestUp() {
		
	}

	@Override
	public void onTestDown() {
		
	}

}
