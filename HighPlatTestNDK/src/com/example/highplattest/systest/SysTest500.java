package com.example.highplattest.systest;

import com.example.highplattest.fragment.DefaultFragment;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.ISOUtils;
import com.example.highplattest.main.tools.Tools;
import com.newland.ndk.JniNdk;

public class SysTest500 extends DefaultFragment
{
	private final String TESTITEM = "文件测试";
	Gui gui;
	
	public void systest500() 
	{
		gui = new Gui(myactivity, handler);
		while(true)
		{
			int nkeyIn = gui.cls_show_msg("文件系统\n0.运行\n");
			switch (nkeyIn) 
			{
				case '0':
					fileTest();
					break;
					
				case ESC:
					intentSys();
					return;
			}
		}
	}
		
	public void fileTest()
	{
		int fd,ret;
		if((fd=JniNdk.JNI_FsOpen("/appfs/yy", "w"))<0)
		{
			gui.cls_show_msg1_record("SysTest500", "fileTest", g_time_0,"line %d:打开文件失败(ret=%d)",Tools.getLineInfo(),fd);
			return;
		}
		byte[] content = ISOUtils.hex2byte("30313233");
		if((ret = JniNdk.JNI_FsWrite(fd, content, content.length))!=4)
		{
			gui.cls_show_msg1_record("SysTest500", "fileTest", g_time_0,"line %d:写文件失败(ret=%d)",Tools.getLineInfo(),ret);
			return;
		}
		byte[] readBuf = new byte[100];
		if((ret = JniNdk.JNI_FsRead(fd, readBuf, 4))!=NDK_OK)
		{
			gui.cls_show_msg1_record("SysTest500", "fileTest", g_time_0,"line %d:读文件失败(ret=%d)",Tools.getLineInfo(),ret);
			return;
		}
		if((ret = JniNdk.JNI_FsClose(fd))!=NDK_OK)
		{
			gui.cls_show_msg1_record("SysTest500", "fileTest", g_time_0,"line %d:关闭文件失败(ret=%d)",Tools.getLineInfo(),ret);
			return;
		}
		gui.cls_show_msg1_record("SysTest500", "fileTest", g_time_0,"测试通过");
	}
}
