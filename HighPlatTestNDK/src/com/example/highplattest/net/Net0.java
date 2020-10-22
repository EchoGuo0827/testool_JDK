package com.example.highplattest.net;

public interface Net0 {
	/**
	 * 网络模块用例说明
	 * Net1：获取IMEI、MEID号、iccid
	 * Net2：wlan与移动网络共存
	 * Net3：wifi探针
	 * Net4：4G网络限制
	 * Net5：网络模式切换测试
	 * Net6：以太网开关操作
	 * Net7：网络模式切换测试(新)
	 * Net8:IP黑名单功能
	 * Net9:双卡功能
	 * Net10:DNS非法
	 * Net11:获取国家MCC
	 * Net12：获取双sim的IMEI号
	 * Net13：getEthernetStatus(N550)
	 */
	
	/**部分常量值定义*/
	public static final int DDS_SUCCESS = 1;
	public static final int DDS_FAIL_REASON_SLOT_IS_ALREADY_DDS = -1;/**目前已经是卡槽该卡槽*/
	public static final int DDS_FAIL_REASON_SLOT_ABSENT = -2;
	public static final int DDS_FAIL_REASON_NOT_DSDS_DEVICE = -3;
	public static final int DDS_FAIL_REASON_OTHER_DDS_IN_PROGRESS = -4;/**其他DDS正在设置*/
	public static final int DDS_FAIL_REASON_TIMEOUT = -5;
	public static final int DDS_FAIL_REASON_UNKNOWN = -100;
	
	/**获取网络模式GSM——ONLY=2,3G/2G=3,4G/3G/2G=4*/
	public final int Mobile_GSM_ONLEY=2;
	public final int Mobile_3G_2G=3;
	public final int Mobile_4G_3G_2G=4;
}
