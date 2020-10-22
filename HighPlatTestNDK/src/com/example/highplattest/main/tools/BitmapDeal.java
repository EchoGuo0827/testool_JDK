package com.example.highplattest.main.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapDeal 
{
	// 将Bitmap转换成InputStream
		public static InputStream bitmap2InputStream(Bitmap bm) 
		{
			if(bm==null)
				return null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			return is;
		}
		
		//从本地端路径获取图片并转成Bitmap 
		public static Bitmap getDiskBitmap(String pathString) {
			Bitmap bitmap = null;
			try 
			{
				File file = new File(pathString);
				if (file.exists()) 
				{
					bitmap = BitmapFactory.decodeFile(pathString);
				}
			} catch (Exception e) {
			}
			return bitmap;
		}
		
		// 获取 网络指定路径的图片
		public static Bitmap getImage(String urlpath) {
			URL url;
			Bitmap bitmap = null;
			try 
			{
				url = new URL(urlpath);
				HttpURLConnection conn;
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5 * 1000);
				
				if (conn.getResponseCode() == 200) 
				{
					InputStream inputStream = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(inputStream);
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			return bitmap;
		}

}
