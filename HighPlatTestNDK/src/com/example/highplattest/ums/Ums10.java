package com.example.highplattest.ums;

import java.util.List;
import android.content.Context;
import android.newland.ums.UmsApi;
import android.widget.TextView;


import java.util.ArrayList;

import com.example.highplattest.fragment.UnitFragment;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.tools.FileSystem;
import com.example.highplattest.main.tools.Gui;
import com.example.highplattest.main.tools.Tools;
import com.example.highplattest.main.tools.TouchView;
/************************************************************************
 * module 			: 银商安全模块
 * file name 		: Ums10.java 
 * history 		 	:          变更点								                     变更时间	    	变更人员
 * 					getProcessList(String uid)：获取指定uid的进程列表	   	  20200818	 		郑佳雯
 *                              增加白名单                                                                                      20200903          郑佳雯
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class Ums10 extends UnitFragment
{
	private final String TESTITEM = "getProcessList()(银商)";
	public final String FILE_NAME = Ums10.class.getSimpleName();
	private static String ROOTFILE = "/sdcard/root_result.txt";
	private static String WFILE = "/sdcard/wrong.txt";
	private static String SYSTEMFILE = "/sdcard/system_result.txt";
	private static String WHFILE = "/sdcard/white_lst.txt";
	private FileSystem fileSystem = new FileSystem();
	Gui gui = new Gui(myactivity, handler);
	UmsApi umsApi;
	
	public void ums10()
	{
		try {
			gui.cls_show_msg( "测试前置条件：请新建root_result.txt文档、system_result.txt文档、wrong.txt文档，并保存到sdcard中,任意键继续...");
			testUms10();
		} catch (Exception e) {
			gui.cls_show_msg1_record(FILE_NAME, "ums10", 0, "line %d:抛出异常(%s)", Tools.getLineInfo(),e.getMessage());
		}
	}
	
	public void testUms10() 
	{
		List<String> lst = new ArrayList<String>();
		String funcName = "testUms10";
		String showLst ="得到的进程列表为：\n";
		String wrongLst ="不在白名单内的进程列表为：\n";
		boolean iRet = false;

		List<String> whiteLst = new ArrayList<String>();
		whiteLst.add("/init");
		whiteLst.add("/sbin/adbd");
		whiteLst.add("/sbin/cl");
		whiteLst.add("/sbin/healthd");		
		whiteLst.add("/sbin/ueventd");		
		whiteLst.add("/system/bin/debuggerd");		
		whiteLst.add("/system/bin/dpmd");		
		whiteLst.add("/system/bin/lmkd");		
		whiteLst.add("/system/bin/logwrapper");		
		whiteLst.add("/system/bin/netd");		
		whiteLst.add("/system/bin/perfd");		
		whiteLst.add("/system/bin/qcom-system-daemon");		
		whiteLst.add("/system/bin/sh");		
		whiteLst.add("/system/bin/thermal-engine");		
		whiteLst.add("/system/bin/vold");		
		whiteLst.add("DIAG_USB_diag");		
		whiteLst.add("IPCRTR");		
		whiteLst.add("VosMCThread");		
		whiteLst.add("VosRXThread");		
		whiteLst.add("VosTXThread");		
		whiteLst.add("VosWDThread");		
		whiteLst.add("apr_driver");		
		whiteLst.add("bam_dmux_rx");		
		whiteLst.add("bam_dmux_tx");		
		whiteLst.add("binder");		
		whiteLst.add("bioset");		
		whiteLst.add("cfg80211");		
		whiteLst.add("cfinteractive");		
		whiteLst.add("core_ctl/0");		
		whiteLst.add("croot.sh");		
		whiteLst.add("crypto");		
		whiteLst.add("deferwq");		
		whiteLst.add("devfreq_wq");		
		whiteLst.add("diag_cntl_wq");		
		whiteLst.add("diag_dci_wq");		
		whiteLst.add("diag_lpass_data");		
		whiteLst.add("diag_modem_data");		
		whiteLst.add("diag_real_time_");		
		whiteLst.add("diag_sensors_da");		
		whiteLst.add("diag_wcnss_data");		
		whiteLst.add("diag_wq");		
		whiteLst.add("dm_bufio_cache");		
		whiteLst.add("ext4-dio-unwrit");		
		whiteLst.add("f_mtp");		
		whiteLst.add("file-storage");		
		whiteLst.add("fsnotify_mark");		
		whiteLst.add("governor_msm_ad");		
		whiteLst.add("gt1x_workthread");		
		whiteLst.add("gt1x_wq");		
		whiteLst.add("hwrng");		
		whiteLst.add("irq/105-msm_iom");		
		whiteLst.add("irq/107-msm_iom");		
		whiteLst.add("irq/135-msm_iom");		
		whiteLst.add("irq/136-msm_iom");		
		whiteLst.add("irq/137-msm_iom");		
		whiteLst.add("irq/138-msm_iom");		
		whiteLst.add("irq/142-msm_iom");		
		whiteLst.add("irq/143-msm_iom");		
		whiteLst.add("irq/144-msm_iom");		
		whiteLst.add("irq/145-msm_iom");		
		whiteLst.add("irq/152-msm_iom");		
		whiteLst.add("irq/153-msm_iom");		
		whiteLst.add("irq/154-msm_iom");		
		whiteLst.add("irq/170-7824900");		
		whiteLst.add("irq/182-msm_iom");		
		whiteLst.add("irq/215-408000.");		
		whiteLst.add("irq/215-410000.");		
		whiteLst.add("irq/224-spdm_bw");		
		whiteLst.add("irq/253-7864900");		
		whiteLst.add("irq/255-msm_iom");		
		whiteLst.add("irq/256-msm_iom");		
		whiteLst.add("irq/257-msm_iom");	
		whiteLst.add("irq/260-msm_iom");		
		whiteLst.add("irq/261-msm_iom");		
		whiteLst.add("irq/262-msm_iom");		
		whiteLst.add("irq/263-msm_iom");		
		whiteLst.add("irq/264-msm_iom");		
		whiteLst.add("irq/265-msm_iom");		
		whiteLst.add("irq/273-msm_iom");		
		whiteLst.add("irq/274-msm_iom");	
		whiteLst.add("irq/297-fastchg");		
		whiteLst.add("irq/300-batt_pr");		
		whiteLst.add("irq/323-sy6982.");		
		whiteLst.add("irq/424-wcnss");		
		whiteLst.add("irq/456-modem");		
		whiteLst.add("jbd2/mmcblk0p21");		
		whiteLst.add("jbd2/mmcblk0p22");		
		whiteLst.add("jbd2/mmcblk0p23");		
		whiteLst.add("jbd2/mmcblk0p29");		
		whiteLst.add("jbd2/mmcblk0p30");		
		whiteLst.add("jbd2/mmcblk0p31");		
		whiteLst.add("jbd2/mmcblk0p33");		
		whiteLst.add("jbd2/mmcblk0p34");		
		whiteLst.add("jbd2/mmcblk0p35");		
		whiteLst.add("k_bam_data");		
		whiteLst.add("k_gserial");	
		whiteLst.add("kauditd");	
		whiteLst.add("kblockd");		
		whiteLst.add("kgsl-3d0");		
		whiteLst.add("kgsl-events");		
		whiteLst.add("kgsl_devfreq_wq");		
		whiteLst.add("khelper");	
		whiteLst.add("khubd");	
		whiteLst.add("krfcommd");
		whiteLst.add("ksoftirqd/0");	
		whiteLst.add("ksoftirqd/1");		
		whiteLst.add("ksoftirqd/2");	
		whiteLst.add("ksoftirqd/3");	
		whiteLst.add("kswapd0");	
		whiteLst.add("kthreadd");	
		whiteLst.add("kworker/0:0");
		whiteLst.add("kworker/0:0H");	
		whiteLst.add("kworker/0:1");
		whiteLst.add("kworker/0:1H");	
		whiteLst.add("kworker/0:2");
		whiteLst.add("kworker/0:3");
		whiteLst.add("kworker/0:4");
		whiteLst.add("kworker/1:0");
		whiteLst.add("kworker/1:0H");
		whiteLst.add("kworker/1:1");
		whiteLst.add("kworker/1:1H");	
		whiteLst.add("kworker/1:2");	
		whiteLst.add("kworker/1:3");
		whiteLst.add("kworker/1:4");
		whiteLst.add("kworker/2:0");	
		whiteLst.add("kworker/2:0H");
		whiteLst.add("kworker/2:1");
		whiteLst.add("kworker/2:1H");
		whiteLst.add("kworker/2:2");
		whiteLst.add("kworker/2:3");
		whiteLst.add("kworker/3:0");		
		whiteLst.add("kworker/3:0H");
		whiteLst.add("kworker/3:1");
		whiteLst.add("kworker/3:1H");
		whiteLst.add("kworker/3:2");
		whiteLst.add("kworker/3:3");
		whiteLst.add("kworker/3:4");
		whiteLst.add("kworker/u8:0");
		whiteLst.add("kworker/u8:1");
		whiteLst.add("kworker/u8:2");		
		whiteLst.add("kworker/u8:3");		
		whiteLst.add("kworker/u8:4");		
		whiteLst.add("kworker/u8:5");	
		whiteLst.add("kworker/u8:6");		
		whiteLst.add("kworker/u8:7");	
		whiteLst.add("kworker/u8:8");
		whiteLst.add("kworker/u8:9");	
		whiteLst.add("kworker/u8:10");	
		whiteLst.add("kworker/u8:11");
		whiteLst.add("kworker/u9:0");
		whiteLst.add("kworker/u9:1");
		whiteLst.add("logcat");		
		whiteLst.add("mdss_dsi_event");
		whiteLst.add("mdss_fb0");		
		whiteLst.add("mem_share_svc");
		whiteLst.add("migration/0");
		whiteLst.add("migration/1");
		whiteLst.add("migration/2");
		whiteLst.add("migration/3");
		whiteLst.add("mmcqd/0");	
		whiteLst.add("mmcqd/0rpmb");
		whiteLst.add("modem_IPCRTR");	
		whiteLst.add("mpm");
		whiteLst.add("msm_ipc_router");
		whiteLst.add("msm_thermal:fre");
		whiteLst.add("msm_thermal:hot");
		whiteLst.add("msm_thermal:the");
		whiteLst.add("msm_vidc_worker");
		whiteLst.add("netns");
		whiteLst.add("pil_vote_wq");
		whiteLst.add("pm_workerq_venu");
		whiteLst.add("proximity_als");
		whiteLst.add("qmi_hndl0000000");
		whiteLst.add("rcu_bh");
		whiteLst.add("rcu_preempt");
		whiteLst.add("rcu_sched");
		whiteLst.add("rpm-smd");
		whiteLst.add("rq_stats");
		whiteLst.add("sharedmem_qmi_w");
		whiteLst.add("smd_channel_clo");
		whiteLst.add("smsm_cb_wq");
		whiteLst.add("spi0");
		whiteLst.add("system");
		whiteLst.add("uether");
		whiteLst.add("usb_bam_wq");
		whiteLst.add("usbnet");
		whiteLst.add("wcnss_IPCRTR");
		whiteLst.add("wlan_logging_th");
		whiteLst.add("writeback");
		whiteLst.add("zygote");
		whiteLst.add("k_gsmd");
		whiteLst.add("irq/312-Elect");
		whiteLst.add("irq/313-Button");
		whiteLst.add("irq/314-Button");
		whiteLst.add("irq/315-Elect");
		whiteLst.add("irq/316-mbhc");
		whiteLst.add("irq/318-HPH_R");
		whiteLst.add("irq/319-HPH_L");
		whiteLst.add("IPCRTR");
		whiteLst.add("DIAG_USB_diag");
		
		//参数异常测试
		// case2.1:参数异常测试:uid=NULL,无任何进程列表
		gui.cls_show_msg1(2, "case2.1:参数异常测试：uid=NULL");
		if((umsApi.getProcessList(null))!=null)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.1测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;	
		}
		else 
			gui.cls_show_msg1(2, "case2.1:参数异常测试通过");
		
		// case2.2:参数异常测试:uid=空,无任何进程列表
		gui.cls_show_msg1(2, "case2.2:参数异常测试：uid=空");
		lst.addAll(umsApi.getProcessList(""));
		if((iRet=lst.isEmpty())!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.2测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;	
		}
		else 
			gui.cls_show_msg( "case2.2:参数异常测试通过,任意键继续...");
		lst.clear();
		
		// case2.3:参数异常测试:参数异常测试:特殊字符测试,无任何进程列表
		gui.cls_show_msg1(2, "case2.3:参数异常测试：uid为特殊字符");
		lst.addAll(umsApi.getProcessList("~！@#￥￥%…………&**（）——+_)($^123"));
		if((iRet=lst.isEmpty())!=true)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case2.3测试失败(ret=%s)", Tools.getLineInfo(),iRet);
			if(!GlobalVariable.isContinue)
				return;	
		}
		else 
			gui.cls_show_msg( "case2.3:参数异常测试通过,任意键继续...");
		lst.clear();
		
		// case3.1:正常测试：uid="root",运行root权限的进程，获取符合要求的进程名列表.
		//返回root权限正在运行的进程名列表,与shell命令的ps获取的的列表一致
		gui.cls_show_msg( "case3.1:正常测试：uid=root,运行root权限的进程，获取符合要求的进程名列表,任意键继续...");
		lst.addAll(umsApi.getProcessList("root"));
		showLst ="得到的进程列表为：\n";
		for (int i = 0; i < lst.size(); i++) {
			showLst = showLst+(lst.get(i)+"\n");
		}
		if(fileSystem.JDK_FsWrite(ROOTFILE, showLst.toString().getBytes(), showLst.toString().getBytes().length, 2)!= showLst.toString().getBytes().length)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1写入文件失败...", Tools.getLineInfo());
			return;
		}
		
		if(gui.cls_show_msg( "对比/sdcard/root_result.txt得到的列表与 adb shell ps命令获取的列表一致？一致[确认],不一致[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.1测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;	
		}
		lst.clear(); 
		
		// case3.2:正常测试：uid="system",运行system权限的进程，获取符合要求的进程名列表.
		//返回system权限正在运行的进程名列表，与shell命令的ps获取的列表一致
		gui.cls_show_msg( "case3.2:uid=system,运行system权限的进程,获取符合要求的进程名列表,任意键继续...");
		lst.addAll(umsApi.getProcessList("system"));
		showLst ="得到的进程列表为：\n";
		for (int i = 0; i < lst.size(); i++) {
			showLst = showLst+(lst.get(i)+"\n");
		}
		if(fileSystem.JDK_FsWrite(SYSTEMFILE, showLst.toString().getBytes(), showLst.toString().getBytes().length, 2)!= showLst.toString().getBytes().length)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2写入文件失败...", Tools.getLineInfo());
			return;
		}

		if(gui.cls_show_msg( "对比/sdcard/system_result.txt得到的列表与adb shell ps命令获取的列表一致？一致[确认],不一致[其他]")!=ENTER)
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.2测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;	
		}
		lst.clear();
		
		// case3.3:正常测试:获取列表，比较是否在白名单内
		//可获取到刚安装应用的进程名
		gui.cls_show_msg("case3.3:获取root进程列表，比较是否在白名单内,任意键继续...");
		lst.addAll(umsApi.getProcessList("root"));
		int k=lst.size();
		for (int i=0; i<k-1; i++) {
			if (whiteLst.contains(lst.get(i))) {
				continue;}			
			else{
				wrongLst = wrongLst+(lst.get(i)+"\n");	
				gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr," %s 不在白名单内",lst.get(i));
				if(!GlobalVariable.isContinue)
					return;	
				}	
			}
		fileSystem.JDK_FsWrite(WFILE, wrongLst.toString().getBytes(), wrongLst.toString().getBytes().length, 2);
		gui.cls_show_msg("case3.3测试通过，请打开/sdcard/wrong.txt确认是否有不在白名单内的进程");
		
		lst.clear();
	
		// case3.4:正常测试:安装一个系统应用,运行该应用
		//可获取到刚安装应用的进程名
		gui.cls_show_msg("case3.4:请运行【设置】,任意键继续...");
		lst.addAll(umsApi.getProcessList("system"));
		if(lst.contains("com.android.settings"))
			gui.cls_show_msg("case3.4测试通过");
		else
		{
			gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr,"line %d:case3.4测试失败", Tools.getLineInfo());
			if(!GlobalVariable.isContinue)
				return;	
		}
		lst.clear();
		gui.cls_show_msg1_record(FILE_NAME, funcName, gKeepTimeErr, "%s测试通过", TESTITEM);		
		
	/*	//获取白名单
		gui.cls_show_msg("case3.5:获取白名单,任意键继续...");
		int m=10000;
		for(int n=0;n < m; n++){		
			lst.addAll(umsApi.getProcessList("root"));			
			for (int i=0; i<lst.size()-1; i++) {
				for (int j=lst.size()-1; j>i; j--) {
					if (lst.get(j).equals(lst.get(i))) {
						lst.remove(j);
						}
					}
				}
			}		
		showLst ="得到的白名单为：\n";
		for (int i = 0; i < (lst.size()-1); i++) {
			showLst = showLst+(lst.get(i)+"\n");
		}
		fileSystem.JDK_FsWrite(WHFILE, showLst.toString().getBytes(), showLst.toString().getBytes().length, 2);
		gui.cls_show_msg("case3.3测试通过");
	*/		
	}
		
	@Override
	public void onTestUp() {
		umsApi = new UmsApi(myactivity);
	}

	@Override
	public void onTestDown() {
	}

}
