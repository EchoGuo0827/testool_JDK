package com.example.highplattest.systemnode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;

/************************************************************************
 * 
 * file name 		: SystemConfig74.java 
 * description 		: X1获取以太网MAC地址
 * history 		 	: 变更记录												变更时间				变更人员
 *			  		  /newland/facotry/macaddr保存以太网地址,X1导入		  	20200509		   	 王凯
 *					  	由原Systemconfig74搬移		  						20200604		   	 陈丁
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class SystemNode2 extends UnitFragment {
	private final String TESTITEM = "/newland/factory/macaddr节点测试";
	private Gui gui = new Gui(myactivity, handler);
	private String fileName = "SystemNode2";	
	String eth_write_node = "/newland/factory/macaddr";
	String eth_read_node = "/sys/class/net/eth0/address";
	String savaMacPath = "/sdcard/eth_mac";
	
	public void systemnode2(){
//		if(new File(eth_write_node).exists() == false)
//		{
//			gui.cls_show_msg1(3, "line %d:%s文件不存在，请导入文件(长按确认键退出)", Tools.getLineInfo(), eth_write_node);
//			return;
//		}
		while(true)
		{
			int nkey = gui.cls_show_msg("%s\n0.单元测试\n1.获取mac地址\n2.恢复默认的mac地址", TESTITEM);
			switch (nkey) {
			case '0':
				unitTest();
				break;
				
			case '1':
				String getEthMac1 = getNodeFile(eth_read_node, "0");
				gui.cls_show_msg("获取到以太网MAC地址为=%s", getEthMac1);
				break;

			case '2':
				if(new File(savaMacPath).exists()==false)
				{
					gui.cls_show_msg("line %d:未获取过mac地址，无法恢复",Tools.getLineInfo());
					break;
				}
				else
				{
					// 恢复默认的以太网的mac
					String getEthMac = savaEthMac(null, 0);
					// getEthMac
					setNodeFile(eth_write_node, "38:3C:9C:00:00:0D\n");
					File file = new File(savaMacPath);
					file.delete();
					if(gui.cls_show_msg("mac地址已恢复为%s,重启生效,是否立即重启", getEthMac)==ENTER)
						Tools.reboot(myactivity);
				}
				break;
				
			case ESC:
				unitEnd();
				return;
				
			default:
				break;
			}
		}
	}
	private void unitTest()
	{
		gui.cls_show_msg("本案例需要在以太网打开和关闭状态下都测试才可");
		// case1：获取以太网的mac,保存在文件当中
		String getEthMac = getNodeFile(eth_read_node, "0");
		if(gui.cls_show_msg("获取到以太网MAC地址为=%s，请判断是否与设置中的以太网地址一致", getEthMac)==ENTER)
		{
			if(getEthMac.contains("aa:10:11:22:33:44")||getEthMac.contains("err"))// 是已经被修改的mac就不需要保存mac地址了
			{
				;
			}
			else 
			{
				if(savaEthMac(getEthMac, 1).equals("err"))
				{
					gui.cls_show_msg1_record(fileName, "unitTest", 3, "line %d:保存以太网mac地址失败，请查看异常日志", Tools.getLineInfo());
					if(!GlobalVariable.isContinue)
						return;
				}
			}
		}
		else
		{
			gui.cls_show_msg1_record(fileName, "unitTest", 3, "line %d:获取的以太网mac地址与设置的不一致(获取的mac=%d)", Tools.getLineInfo(), getEthMac);
			if(!GlobalVariable.isContinue)
				return;
		}
				
		// case2:设置以太网的mac,并确保以太网可正常上网
		setNodeFile(eth_write_node, "aa:10:11:22:33:44\n");
		getEthMac = getNodeFile(eth_read_node, "0");
		/*if(getEthMac.contains("aa:10:11:22:33:44")==false)
		{
			gui.cls_show_msg1_record(fileName, "unitTest", 3, "line %d:获取的以太网mac地址与案例设置的不一致(获取的mac=%s)", Tools.getLineInfo(), getEthMac);
			if(!GlobalVariable.isContinue)
				return;
		}*/
		if(gui.cls_show_msg("mac地址已修改为aa:10:11:22:33:44，【重启后生效】,重启后以太网是否可正常上网，是否立即重启")==ENTER)
		{
			Tools.reboot(myactivity);
		}
			
			
		gui.cls_show_msg1_record(fileName, TESTITEM, gScreenTime, "%s测试通过，重启后以太网可正常上网才可视为测试通过(长按确认键退出测试)", TESTITEM,getEthMac);
	}
	/**保存以太网的mac地址*/
	/**
	 * 保存以太网的mac地址
	 * @param mac 以太网的mac地址
	 * @param mode 1:写操作 0 读操作
	 * @return
	 */
	public String savaEthMac(String mac,int mode)
	{
		File file = new File(savaMacPath);
		String ethMac="err";
		if(mode==1)
		{
			if(file.exists()==false)
			{
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(mac.getBytes());
				outputStream.close();
				ethMac="succ";
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		else if(file.exists()&&mode==0)
		{
			try {
				byte[] buffer = new byte[17];
				FileInputStream inputStream = new FileInputStream(file);
				inputStream.read(buffer);
				inputStream.close();
				ethMac = new String(buffer);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ethMac;
	}
	@Override
	public void onTestUp() {
		
	}
	@Override
	public void onTestDown() {
		
	}
}
