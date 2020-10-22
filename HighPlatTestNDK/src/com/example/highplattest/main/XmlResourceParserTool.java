package com.example.highplattest.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.highplattest.main.tools.LoggerUtil;

import android.util.Log;

public class XmlResourceParserTool 
{
	// 获取xml的节点内容
	public static Map<String, String> getNodeContent(InputStream in,String nodeName) 
	{
		String nodeText = null;
		boolean isStart = false;
		XmlPullParserFactory factory;
		Map<String,String> nodeContent = new HashMap<String, String>();
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				switch (eventType) 
				{
				case XmlPullParser.START_DOCUMENT:
					// 不做任何操作或初开始化数据
					break;

				case XmlPullParser.START_TAG:
					// 解析XML节点数据
					// 获取当前标签名字
					String tagName = parser.getName();
					LoggerUtil.e("tagName:"+tagName);
					if (tagName.equals(nodeName)) 
					{
						// 初始化变量
						isStart = true;
						break;
					}
					if(isStart==true)
					{
						try {
							nodeText = parser.nextText();
						} catch (IOException e) {
							e.printStackTrace();
						}
						nodeContent.put(tagName, nodeText);// 键值对的形式传入
					}
					break;

				case XmlPullParser.END_TAG:
					// 单节点完成，可往集合里边添加新的数据
					if(isStart == true)
						return nodeContent;
					break;
					
				case XmlPullParser.END_DOCUMENT:
					LoggerUtil.d("END_DOCUMENT");
					break;
				}

				try {
					eventType = parser.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return nodeContent;
	}
	
	public static List<String> XmlResourceParser(InputStream in, String nodeName) 
	{
		String nodeText = null;
		List<String> listName = new ArrayList<String>();
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				switch (eventType) 
				{
				case XmlPullParser.START_DOCUMENT:
					// 不做任何操作或初开始化数据
					break;

				case XmlPullParser.START_TAG:
					// 解析XML节点数据
					// 获取当前标签名字
					String tagName = parser.getName();
					if (tagName.equals(nodeName)) 
					{
						// 通过getAttributeValue 和 nextText解析节点的属性值和节点值
						try {
							nodeText = parser.nextText();
							listName.add(nodeText);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					break;

				case XmlPullParser.END_TAG:
					// 单节点完成，可往集合里边添加新的数据
					break;
					
				case XmlPullParser.END_DOCUMENT:
					break;
				}

				try {
					eventType = parser.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return listName;
	}
	
	public static String getModuleContent(InputStream in, String moduleName,String nodeName) 
	{
		String nodeText = null;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(in, "UTF-8");
			int eventType = parser.getEventType();
			int otherEventType = 0;
			while (eventType != XmlPullParser.END_DOCUMENT) 
			{
				switch (eventType) 
				{
				case XmlPullParser.START_DOCUMENT:
					// 不做任何操作或初开始化数据
					break;

				case XmlPullParser.START_TAG:
					// 解析XML节点数据
					// 获取当前标签名字
					try {
						String tagName = parser.getName();
						if(tagName.equals("name"))
						{
							nodeText = parser.nextText();
							if(nodeText.equals(moduleName))
							{
								otherEventType = parser.next();
								while(otherEventType!=XmlPullParser.END_DOCUMENT)
								{
									switch (otherEventType) 
									{
									case XmlPullParser.START_TAG:
										tagName = parser.getName();
										if(tagName.equals(nodeName))
										{
											nodeText = parser.nextText();
											return nodeText;
										}
										break;
										
									case XmlPullParser.END_DOCUMENT:
										break;

									default:
										break;
									}
									otherEventType = parser.next();
								}
							}
								
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;

				case XmlPullParser.END_TAG:
					// 单节点完成，可往集合里边添加新的数据
					break;
					
				case XmlPullParser.END_DOCUMENT:
					break;
				}

				try {
					eventType = parser.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) 
		{
			e.printStackTrace();
		}
		return nodeText;

	}
}
