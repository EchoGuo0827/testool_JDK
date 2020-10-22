package com.example.highplattest.main.constant;


/************************************************************************
 * module 			: main
 * file name 		: ParaEnum.java 
 * Author 			: zhengxq
 * version 			: 
 * DATE 			: 20141113
 * directory 		: 
 * description 		: 参数控制
 * related document : 
 * 
 ************************************************************************ 
 * log : Revision no message(created for Android platform)
 ************************************************************************/
public class ParaEnum 
{
	/*---------------functions definition---------------------------*/
	public enum SdkType
	{
		SDK2,SDK3;
	}
	public enum AutoFlag
	{
		AutoFull,HandFull,AutoHand,MulAuto;
	}
	
	public enum LinkStatus
	{
		linkup,linkdown;
	}
	
	public enum TransStatus
	{
		TRANSUP,TRANSDOWN;
	}
	
	public enum LinkType
	{
		NONE,GPRS,WCDMA,CDMA,TD,LTE,ASYN,SYNC,ETH,SERIAL,WLAN,BT,AP
	}
	
	public enum DiskType
	{
		UDISK,SDDSK,TFDSK
	}
	
	public enum NetStatus
	{
		NETUP,NETDOWN
	}
	
	public enum Sock_t
	{
		SOCK_DEFAULT,SOCK_TCP,SOCK_UDP,SOCK_SSL
	}
	
	public enum _SMART_t
	{
		CPU_A,CPU_B,MIFARE_1,ISO15693,FELICA,/*A_BCard,A_M1Card,B_M1Card,A_B_M1Card,*/MIFARE_0,SAM1,SAM2,IC,MIFARE_0_C/*新增M0带认证枚举 */
	}
	
	public enum _MEMORY_t
	{
		AT24C01,AT24C02,AT24C04,AT24C08,AT24C016,AT24C032,AT24C064,SLE4432_42,
		SLE4418_28,SLE5528,AT88SC102,AT88SC1604,AT88SC1608
	}
	
	// LCD画矩形的画笔实心/空心
	public enum Paintter_ns
	{
		RECT_PATTERNS_NO_FILL,// 空心
		RECT_PATTERNS_SOLID_FILL;// 实心
	}
	
	// wifi ap设置方式
	public enum Wifi_Ap_Create 
	{
		ETH, WLM
	}

	// wifi加密模式
	public enum Wifi_Ap_Enctyp 
	{
		WIFI_NET_SEC_WEP_OPEN, WIFI_NET_SEC_WPA, WIFI_NET_SEC_WPA2
	}
	
	// 断开类型
	public enum Dis_Type
	{
		N900_DIS,Dongle_DIS
	}
	
	// 机型
//	CPOS_X5_Poynt/**新增CPOS_X5_Poynt机型20191011*/
	public enum Model_Type
	{
		/*X3Sec*//**新增X3支持安全模块机型20191213*/
		
		IM81_Old,IM81_New,N900_3G,N900_4G,N910,N700/**新增N700的机型20171228*/,N920/**新增N920的机型20170207*/,
		N850/**新增N850的机型20170209*/,X5/**新增X5的机型20180305*/,N510/**新增N510的机型20180502,非接模块选配*/,
		N910_Poynt/**新增PoyntP5的机型20180716*/,N550/**磁卡、IC模块，只支持打印、射频模块*/,
		X3/**新增X3机型，只有Android模块20190724*/,F7/**新增F7机型20190819*/,X1/**新增X1机型20191028*/,
		N910_A7/**新增N910A7机型20200312*/,CPOS_X5_Poynt,N700_A7/**新增N700A7机型20200410*/,N920_A7/**新增N920_A7机型20200413*/,
		F10/**新增F10机型20200413*/,N850_A7/**新增N850_A7 20201012*/

	}
	
	// 代表高端目前的平台
	public enum Platform_Ver
	{
		A5,A7,A9
	}
	
	// 各种码型
	public enum Code_Type
	{
		QR_UTF8_1,QR_UTF8_2,QR_GBK,QR_ECI,CodeBar,Code11,Code39,Code93,Code128,EAN_8,EAN_8_ADD,EAN_13,EAN_13_ADD,EAN_128,/*ITF*/ITF_14,UPC_A,UPC_A_ADD,UPC_E,UPC_E_ADD,ISBN_ISSN,
		IBSN_ISSN_ADD,PDF417,UCC_EAN_128,Interleaved_2OF5,Industrial_2OF5,Standard_2OF5,Matrix_2OF5,GSI_Databar,MSI_Plessey,Plessey,DataMatrix,China_Code,GS1_128/*汉信码*/
	}
	
	// 扫码模式
	public enum Scan_Mode
	{
		MODE_MANUALLY,MODE_ONCE,MODE_CONTINUALLY,ZXING,ZXING_MANUALLY,NLS_0,NLS_1,NLS_picture
	}
	
	public enum Camera_Id
	{
		Camera_Back,Camera_Font
	}
	
	// 射频卡模式
	public enum Nfc_Card
	{
		NFC_A,NFC_B,NFC_M1
	}
	
	// 密码输入密文明文
	public enum Input_Way
	{
		PLAIN,SECRET
	}
	
	public enum Pair_Result
	{
		BOND_BONDED,BOND_NONE
	}
	
	public enum WIFI_SEC
	{
		WPA,WEP,NOPASS
	}
	
	//X5打印机类型
	public enum EM_PRN_TYPE 
	{
		PRN_TYPE_TP(0), /** <热敏打印机 */
		PRN_TYPE_HIP(1), /** <穿孔针打 */
		PRN_TYPE_FIP(2), /** <摩擦针打 */
		PRN_TYPE_TP_LOW_VOL(3), /** <低压热敏打印机 */
		PRN_TYPE_TP_NORMAL(4), /** <正常热敏打印机 */
		PRN_TYPE_TP_THREE_INCH(5), /** <3寸热敏打印机 */
		PRN_TYPE_END(6);/** <无 */

		private int value = 0;
		
		EM_PRN_TYPE(int value) 
		{
			this.value = value;
		}

		public int prntype() 
		{
			return value;
		}
	}
	
	// 打印中文枚举值
	public enum HZ_FONT
	{
		// 枚举值从1开始
		PRN_HZ_FONT_24x24,
		PRN_HZ_FONT_16x32,
		PRN_HZ_FONT_32x32,
		PRN_HZ_FONT_32x16,
		PRN_HZ_FONT_24x32,
		PRN_HZ_FONT_16x16,
		PRN_HZ_FONT_12x16,
		PRN_HZ_FONT_16x8,
		PRN_HZ_FONT_24x24A,
		PRN_HZ_FONT_24x24B,
		PRN_HZ_FONT_24x24C,
		PRN_HZ_FONT_24x24USER,
		PRN_HZ_FONT_12x12A,//13号中文不支持
		PRN_HZ_FONT_16x24,
		PRN_HZ_FONT_16x16BL,// 中文粗体
		PRN_HZ_FONT_24x24BL,// 中文粗体
		/*48的字体都不支持 ，会返回-6*/
		PRN_HZ_FONT_48x24A,
		PRN_HZ_FONT_48x24B,
		PRN_HZ_FONT_48x24C,
		PRN_HZ_FONT_24x48A,
		PRN_HZ_FONT_24x48B,
		PRN_HZ_FONT_24x48C,
		PRN_HZ_FONT_48x48A,
		PRN_HZ_FONT_48x48B,
		PRN_HZ_FONT_48x48C,
	}
	
	// 打印西文枚举值
	public enum ZM_FONT
	{
		PRN_ZM_FONT_8x16(1),
		PRN_ZM_FONT_16x16(2),
		PRN_ZM_FONT_16x32(3),
		PRN_ZM_FONT_24x32(4),
		PRN_ZM_FONT_6x8(5),
		PRN_ZM_FONT_8x8(6),
		PRN_ZM_FONT_5x7(7),
		PRN_ZM_FONT_5x16(8),
		PRN_ZM_FONT_10x16(9),
		PRN_ZM_FONT_10x8(10),
		PRN_ZM_FONT_12x16A(11),//10
		PRN_ZM_FONT_12x24A(12),
		PRN_ZM_FONT_16x32A(13),
		PRN_ZM_FONT_12x16B(14),
		PRN_ZM_FONT_12x24B(15),
		PRN_ZM_FONT_16x32B(16),
		PRN_ZM_FONT_12x16C(17),
		PRN_ZM_FONT_12x24C(18),
		PRN_ZM_FONT_16x32C(19),
		PRN_ZM_FONT_24x24A(20),
		PRN_ZM_FONT_32x32A(21),
		PRN_ZM_FONT_24x24B(22),
		PRN_ZM_FONT_32x32B(23),
		PRN_ZM_FONT_24x24C(24),
		PRN_ZM_FONT_32x32C(25),
		PRN_ZM_FONT_12x12(26),
		PRN_ZM_FONT_12x12A(27),
		PRN_ZM_FONT_12x12B(28),
		PRN_ZM_FONT_12x12C(29),
		PRN_ZM_FONT_8x12(30),
		PRN_ZM_FONT_8x24(31),
		PRN_ZM_FONT_8x32(32),
		PRN_ZM_FONT_12x32A(33),
		PRN_ZM_FONT_12x32B(34),
		PRN_ZM_FONT_12x32C(35),
		PRN_ZM_FONT_8x16BL(36),// 西文粗体，modify by 20190516 PRN_ZM_FONT_8x16BL以及PRN_ZM_FONT_12x24BL两种字号中的点'.'和冒号':'的打印效果，将其点阵由横杆改为圆点
		PRN_ZM_FONT_16x16BL(37),// 西文粗体 37号英文不支持
		PRN_ZM_FONT_12x24BL(38),// 西文粗体,modify by 20190516 加粗字体枚举对应使用黑体，打印效果，笔画粘连。优化方式通过替换12x24.hzk字库将其替换为宋体加粗
		PRN_ZM_FONT_8x16SR(39),// 新增字体 add by 20190516 对应8x16宋体，枚举值39
		PRN_ZM_FONT_8X16SBL(40),// 宋体处理8X16，枚举值40 add by 20190805
		PRN_ZM_FONT_12X24SR(41),// 宋体常规12X24，枚举值41
		PRN_ZM_FONT_12X24SBL(42);// 宋体粗体12X24，枚举值42
		
		private int value;
		private ZM_FONT(int value)
		{
			this.value = value;
		}
		
		public int getValue()
		{
			return value;
		}
	}
	//SysGetPosInfo接口获取硬件信息
	public enum EM_SYS_HWINFO
	{
		SYS_HWINFO_GET_POS_TYPE(0), /** <取pos机器类型 */
		SYS_HWINFO_GET_HARDWARE_INFO(1), /** <获取POS机上所支持硬件类型，详细返回值如上所述 */
		SYS_HWINFO_GET_BIOS_VER(2), /** <取bios版本信息 */
		SYS_HWINFO_GET_POS_USN(3), /** <取机器序列号 */
		SYS_HWINFO_GET_POS_PSN(4), /** <取机器机器号 */
		SYS_HWINFO_GET_BOARD_VER(5), /** <取主板号 */
		SYS_HWINFO_GET_CREDITCARD_COUNT(6), /** <取pos刷卡总数 */
		SYS_HWINFO_GET_PRN_LEN(7), /** <取pos打印总长度 */
		SYS_HWINFO_GET_POS_RUNTIME(8), /** <取pos机开机运行时间 */
		SYS_HWINFO_GET_KEY_COUNT(9), /** <取pos机按键次数 */
		SYS_HWINFO_GET_CPU_TYPE(10), /** <取pos机cpu类型 */
		SYS_HWINFO_GET_BOOT_VER(11), 
		SYS_HWINFO_GET_BOARD_NUMBER(12), /** <取pos机板号 */
		SYS_HWINFO_GET_KLA1_VER(13), 
		SYS_HWINFO_GET_KLA2_VER(14), 
		SYS_HWINFO_GET_POS_TUSN(15),/** 取TUSN信息 */
		SYS_HWINFO_GET_RFID_CHIP_TYPE(16),/**获取非接芯片类型*/	
		SYS_HWINFO_GET_MAX(17);
		private int value=0;
		
		EM_SYS_HWINFO(int value)
		{
			this.value = value;
		}
		
		public int secsyshwinfo()
		{
			return value;
		}
	}
	// 读取系统配置信息的索引号
	public enum EM_SYS_CONFIG{
		SYS_CONFIG_SLEEP_ENABLE(0),	    /**<休眠使能 0:禁止 1:启用 */
		SYS_CONFIG_SLEEP_TIME(1),      	/**<进入休眠时间前待机时间*/
		SYS_CONFIG_SLEEP_MODE(2),      	/**<休眠模式 1:浅休眠 2:深休眠*/
		SYS_CONFIG_LANGUAGE(3),			/**<获取系统语言 0:中文 1:english */
		SYS_CONFIG_APP_AUTORUN(4);      	/**<开机自动运行主控程序 0:禁用 1:启用*/
        private int value=0;
		
        EM_SYS_CONFIG(int value)
		{
			this.value = value;
		}
		
		public int sysConfig()
		{
			return value;
		}
	}
	// 控制所有LED亮灭情况
	public enum EM_LED
	{
		LED_RFID_RED_ON(0x01),/**控制射频红色灯亮*/
		LED_RFID_RED_OFF(0x02),/**控制射频红色灯灭*/
		LED_RFID_RED_FLICK(0x03),/**控制射频红色灯闪*/
		LED_RFID_YELLOW_ON(0x04),/**控制射频黄色灯亮*/
		LED_RFID_YELLOW_OFF(0x08),/**控制射频黄色灯灭*/
		LED_RFID_YELLOW_FLICK(0x0C),/**控制射频黄色灯闪*/
		LED_RFID_GREEN_ON(0x10),/**控制射频绿色灯亮*/
		LED_RFID_GREEN_OFF(0x20),/**控制射频绿色灯灭*/
		LED_RFID_GREEN_FLICK(0x30),/**控制射频绿色灯闪*/
		LED_RFID_BLUE_ON(0x40),/**控制射频蓝色灯亮*/
		LED_RFID_BLUE_OFF(0x80),/**控制射频蓝色灯灭*/
		LED_RFID_BLUE_FLICK(0xC0),/**控制射频蓝色灯闪*/
		LED_COM_ON(0x100),/**控制通讯灯亮*/
		LED_COM_OFF(0x200),/**控制通讯灯灭*/
		LED_COM_FLICK(0x300),/**控制通讯灯闪*/
		LED_ONL_ON(0x400),/**控制联机灯亮*/
		LED_ONL_OFF(0x800),/**控制联机灯灭*/
		LED_ONL_FLICK(0xC00),/**控制联机灯闪烁*/
		LED_DETECT_ON(0x1000),
		LED_DETECT_OFF(0x2000),
		LED_DETECT_FLICK(0x3000),
		LED_MAG_ON(0x4000),/**控制磁卡灯亮*/
		LED_MAG_OFF(0x8000),/**控制磁卡灯灭*/
		LED_MAG_FLICK(0xC000);/**控制磁卡灯闪*/
		
		private int value = 0;
		EM_LED(int value)
		{
			this.value = value;
		}
		
		public int led()
		{
			return value;
		}
	}
	
	// 终端密钥类型
	public enum EM_SEC_KEY_TYPE
	{
		SEC_KEY_TYPE_TLK,// 终端装载密钥
		SEC_KEY_TYPE_TMK,// 终端主密钥
		SEC_KEY_TYPE_TPK,// 终端PIN密钥
		SEC_KEY_TYPE_TAK,// 终端MAC密钥
		SEC_KEY_TYPE_TDK,// 终端数据加解密密钥
	}
	
	
	// 密钥校验模式
	public enum EM_SEC_KCV
	{
		SEC_KCV_NONE,// 无验证
		SEC_KCV_ZERO,// 对8个字节的0x00计算DES/TDES加密，或对16字节的0x00进行SM4加密，得到的密文的前四个字节即为KCV
		SEC_KCV_VAL,// 首先对密钥明文进行奇校验，再对"\x12\x34\x56\x78\x90\x12\x34\x56"进行DES/TDES加密运算，得到密文的
		// 前四个字节即为KCV,暂不支持
		SEC_KCV_DATA// 传入一串数据KcvData，使用源密钥对[aucDstKeyValue(密文)+kcvData]进行指定模式的MAC运算，得到8个字节
		// 的MAC即为KCV，暂不支持
	}
	
	public enum EM_SEC_MAC
	{
		SEC_MAC_X99,/**x99算法：数据分为8字节block，不足补0，每个block加密后与下一个block异或后按密钥长度加密*/
		SEC_MAC_X919,/**x919算法：数据分为8字节block，不足补0，每个block加密后与下一个block异或后按密钥DES加密，最后帧如果密钥长度为16字节则按3DES，
		如果为8字节按DES*/
		SEC_MAC_ECB,/**全部数据异或后，将异或后数据做DES后进行变换，参考银联规范中关于ECB算法说明*/
		SEC_MAC_9606,/**全部数据异或后，最后将异或数据做des运算*/
		SEC_MAC_SM4,/**数据分为16字节的block，不足补0，每个block进行SM4加密后与下一个block异或后按SM4加密*/
	}
	
	public enum EM_SEC_VPP_KEY
	{
		SEC_VPP_KEY_PIN(0),			/**有pin键码按下，应用应该显示*/
		SEC_VPP_KEY_BACKSPACE(1),	/**退格键按下*/
		SEC_VPP_KEY_CLEAR(2),		/**清除键按下*/
		SEC_VPP_KEY_ENTER(3),		/**确认键按下*/
		SEC_VPP_KEY_ESC(4),			/**pin输入取消*/
		SEC_VPP_KEY_NULL(5);		/**pin无事件产生*/	
		private int value=0;
		
		EM_SEC_VPP_KEY(int value)
		{
			this.value = value;
		}
		
		public int secvppkey()
		{
			return value;
		}
	}
	
	public enum EM_SEC_KEY_ALG
	{
		SEC_KEY_DES(0),			/**<DES/TDES 算法*/
		SEC_KEY_SM4(1<<6),		/**<SM4 算法*/
		SEC_KEY_AES(1<<7),		/**<AES 算法*/
		SEC_KEY_CBC(10);        /**<CBC模式>*/
		
		private int value;
		
		EM_SEC_KEY_ALG(int value)
		{
			this.value = value;
		}
		
		public int seckeyalg()
		{
			return value;
		}
	}
	
	public enum EM_SEC_DES
	{
		SEC_DES_ENCRYPT(0),				/**DES加密*/
		SEC_DES_DECRYPT (1),			/**DES解密*/
		SEC_DES_KEYLEN_DEFAULT(0<<1),	/**使用安装长度的密钥进行加密*/
		SEC_DES_KEYLEN_8(1<<1),			/**使用8字节密钥进行加密*/
		SEC_DES_KEYLEN_16(2<<1),		/**使用16字节密钥进行加密*/
		SEC_DES_KEYLEN_24(3<<1),		/**使用24字节密钥进行加密*/
		SEC_DES_MASK(7),				/**des计算类型使用的映射值，超过该映射值位数无效*/
		SEC_SM4_ENCRYPT(1<<4),			/**SM4加密*/
		SEC_SM4_DECRYPT(1<<5),/**SM4解密*/
		SEC_AES_ENCRYPT(1<<6),
		SEC_AES_DECRYPT(1<<7);
		
		private int value;
		
		EM_SEC_DES(int value)
		{
			this.value = value;
		}
		
		public int secdes()
		{
			return value;
		}
	}
	
	public enum EM_SEC_PIN// 用于实现PIN输入过程的超时控制的变量
	{
		
		SEC_PIN_ISO9564_0(3),/**使用主账号加密，密码不足位数补F*/
		SEC_PIN_ISO9564_1(4),/**不使用主账号加密，密码不足位数补随机数*/
		SEC_PIN_ISO9564_2(5),/**不使用主账号加密，密码不足位数补F*/
		SEC_PIN_ISO9564_3(6),/**使用主账号加密，密码不足位数补随机数*/
		SEC_PIN_SM4_1(7),	/**不使用主账号，密码不足位数补F*/
		SEC_PIN_SM4_2(8),	/**使用主账号填充方式1，密码不足位数补F*/
		SEC_PIN_SM4_3(9),	/**使用主账号填充方式1，密码不足位数补随机数*/
		SEC_PIN_SM4_4(10),	/**使用主账号填充方式2，密码不足位数补F*/
		SEC_PIN_SM4_5(11);/**使用主账号填充方式2，密码不足位数补随机数*/
		private int value;
		
		EM_SEC_PIN(int value)
		{
			this.value = value;
		}

		public int secpin()
		{
			return value;
		}
	}
	/**
	 *@brief  打印机状态以及错误定义，取打印机状态返回值存在两个或多个或上的关系
	*/
	public enum EM_PRN_STATUS
	{
		
		PRN_STATUS_OK(0),			/**<打印机正常*/
		PRN_STATUS_BUSY(8),		/**<打印机正在打印*/
		PRN_STATUS_NOPAPER(2),       /**<打印机缺纸*/
		PRN_STATUS_OVERHEAT(4),      /**<打印机过热*/
		PRN_STATUS_VOLERR(12),       /**<打印机电压异常*/
		PRN_STATUS_PPSERR(2048);       /**<X5轴不在位*/
		private int value;

		public int getValue() {
			return value;
		}

		EM_PRN_STATUS(int value) {
			this.value = value;
		}
		
	}
	/**
	 *@brief  打印机打印模式
	*/
	public enum EM_PRN_MODE{
		PRN_MODE_ALL_DOUBLE(0),			/**<横向放大、纵向放大*/
		PRN_MODE_WIDTH_DOUBLE(1) ,		/**<横向放大、纵向正常*/
		PRN_MODE_HEIGHT_DOUBLE(2),      /**<横向正常、纵向放大*/
		PRN_MODE_NORMAL(3),				/**<横向正常、纵向正常*/
		PRN_MODE_ALL_THREE(4),          /**<横向纵向放大3倍*/
		PRN_MODE_WIDTH_THREE(5),        /**<横向放大3倍，纵向正常*/
		PRN_MODE_HEIGHT_THREE(6);       /**<横向正常，纵向放大3倍*/
		private int value;				

		public int getValue() {
			return value;
		}

		EM_PRN_MODE(int value) {
			this.value = value;
		}
	};
	
	public enum EM_ICTYPE
	{
		ICTYPE_IC,// 接触式IC卡
		ICTYPE_SAM1,// SAM1卡
		ICTYPE_SAM2,// SAM2卡
		ICTYPE_SAM3,// SAM3卡
		ICTYPE_SAM4,// SAM4卡
		ICTYPE_M_1,// at24c32
		ICTYPE_M_2,//sle44x2
		ICTYPE_M_3,//sle44x8
		ICTYPE_M_4,//at88sc102
		ICTYPE_M_5,//at88sc1604
		ICTYPE_M_6,//at88sc1608
		ICTYPE_ISO7816,//ISO7816 standard
		ICTYPE_M_7,//at88sc153
		ICTYPE_M_1_1,
		ICTYPE_M_1_2,
		ICTYPE_M_1_4,
		ICTYPE_M_1_8,
		ICTYPE_M_1_16,
		ICTYPE_M_1_32,
		ICTYPE_M_1_64,
		
		
		
	}
		
	public enum MEMORY_TYPE
	{
		AT24C01,AT24C02,AT24C04,AT24C08,AT24C016,AT24C032,AT24C064,SLE4432_42,SLE4418_28,SLE5528,AT88SC102,
		AT88SC1604,AT88SC1608,AT88SC153
	}
	//
	public enum sys{
		aaa(1);
		private int value;				

		public int getValue() {
			return value;
		}

		sys(int value) {
			this.value = value;
		}
	}

	// 15张异常卡测试 by wangxy 20170912
	public enum SYS_ERRORCODE {
		MAGCARD_TK1_LRC_ERR1(0xC01), // 一道LRC校验失败
		MAGCARD_TK2_LRC_ERR1(0xC02), // 二道LRC校验失败
		MAGCARD_TK3_LRC_ERR1(0xC08), // 三道LRC校验失败
		MAGCARD_TK12_LRC_ERR(0xC01 | 0xC02), // 一、二道LRC校验失败
		MAGCARD_TK23_LRC_ERR(0xC02 | 0xC08), // 二、三道LRC校验失败
		MAGCARD_TK13_LRC_ERR(0xC01 | 0xC08), // 一、三道LRC校验失败
		MAGCARD_TK123_LRC_ERR(0xC01 | 0xC02 | 0xC08); // 一、二、三道LRC校验失败
		private int value;

		public int getValue() {
			return value;
		}

		SYS_ERRORCODE(int value) {
			this.value = value;
		}
	}
	public enum POWERUP_STANDARD{
		EMV_ADJBAUD_MODE(1), // EMV2000 + auto-adjust ATR transfer baud
		EMV_BPS_57600_MODE(2), // EMV2000 + ATR transfer baud : 57600bps
		EMV_BPS_38400_MODE(3), // EMV2000 + ATR transfer baud : 38400bps
		EMV_BPS_19200_MODE(4), // EMV2000 + ATR transfer baud : 19200bps
		SSC_ADJBAUD_MODE(5), // Social Security Card + auto-adjust ATR transfer baud
		ISO7816_BAUD_CFG_MODE(6), // ISO7816 + ATR transfer baud : 9600bps + (Special Mode && PPS exchange)
		ISO7816_BPS_9600_MODE(7), // ISO7816 + ATR transfer baud : 9600bps
		ISO7816_ADJBAUD_MODE(8); // ISO7816 + auto-adjust ATR transfer baud
		private int value;
		public int getValue() {
			return value;
		}
		POWERUP_STANDARD(int value) {
			this.value = value;
		}
	};
	public enum POWERUP_VOL{
		ICC_VOL_OFF(0),
		ICC_VOL_3V(1),
		ICC_VOL_5V(2),
		ICC_VOL_1P8V(3);
		private int value;
		public int getValue() {
			return value;
		}
		POWERUP_VOL(int value) {
			this.value = value;
		}
	};
	
	public enum EM_CFGTYPE{
		SCCFG_PWRMODE(1),
		SCCFG_PWRVOL(2);
		
		private int value;

		public int getValue() {
			return value;
		}
		EM_CFGTYPE(int value){
			this.value=value;
		}
		
	};

	public enum RF_CARD
	{
		TYPE_A((byte)0xCC),
		TYPE_B((byte)0xCB),
		TYPE_AB((byte)0xCD);
		private byte value;				

		public byte getValue() {
			return value;
		}

		RF_CARD(byte value) {
			this.value = value;
		}
	}
	public enum EM_SYS_EVENT{
		SYS_EVENT_NONE(0),    			   /*无事件 -注册事件超时时将发送 */
		SYS_EVENT_MAGCARD(0X00000004),	   /*检测到磁卡*/
		SYS_EVENT_ICCARD(0X00000008),      /*检测到IC卡插入*/
		SYS_EVENT_RFID(0X00000010),		  /*检测到非接卡*/
		SYS_EVENT_PIN(0X00000020),		   /*PIN输入事件*/
		SYS_EVENT_PRNTER(0X00000040),	   /*打印机状态*/
		SYS_EVENT_KEYPAD(0X00000080),	  /*键盘事件*/
		SYS_EVENT_MAX ( 0X00000100);
		private int value;				

		public int getValue() {
			return value;
		}

		EM_SYS_EVENT(int value) {
			this.value = value;
		}
	};
		
	public enum AYSNCTASK_LIST_K21
	{
		RfidRegTask_SDK3(3),
		IccRegTask_SDK3(4),
		MagRegTask_SDK3(6),
		PrnRegTask_SDK3(7),
		PinRegTask_SDK3(9),
		SecTask(8),
		SamTask(5),
		FsTask(10),
		KeyBoardRegTask_SDK3(11),
		LEDTask(12),
		SysPosTimeTask(15),
		SysGetPosInfoTask(17),
		MagTask_SDK2(18),
		IccTask_SDK2(19),
		RfidTask_SDK2(20),
		PrnTask_SDK2(21),
		PinTask_SDK2(22),
		KeyBoardTask_SDK2(23),
		SysVersion(24),
		SysDelayTask(13),
		SysMsDelayTask(14),
		SysTimeTask(16),
		THK88Task(25);/**THK88模块 add by zhengxq 20181221*/
		private int value;
		public int getValue() {
			return value;
		}
		AYSNCTASK_LIST_K21(int value) {
			this.value = value;
		}
	}
	
	
	
	public enum AYSNCTASK_LIST_EMV
	{
		RfidRegTask_SDK3(3),
		BeepTask(4),
		SecSm4Task(5);
		
		int value;
		public int getValue() {
			return value;
		}
		AYSNCTASK_LIST_EMV(int value) {
			this.value = value;
		}
	}
	
	public enum AYSNCTASK_LIST_ANDROID{
		WLM_WLANTask(30),
		BTTask(31),
		SDFs_Task(32),
		SYSCONFIG_TASK(33);
		private int value;
		public int getValue() {
			return value;
		}
		AYSNCTASK_LIST_ANDROID(int value) {
			this.value = value;
		}
	}
	
	public enum SYSTEST_LIST_CONFIG
	{
//		AndroidPortConfig,
//		RfidConfig,
//		IccConfig,
		SmartConfig,
		PrnConfig,
		WLANConfig,
		WLMConfig,
		BTConfig,
		DiskConfig,
	}
	
	/**
	 * 客户识别码，使用英文缩写
	 * @author zhengxq date by 20171201
	 */
	public enum CUSTOMER_ID{
		CCB,		// 建行版本
		ABC,		// 农行版本
		ChinaUms,	// 银商版本
		SDK_2,		// sdk2.0版本
		SDK_3,		// sdk3.0版本
		Lakala,		// 拉卡拉版本
		overseas,	// 海外版本，欧洲版本
		unkown,		// 未知版本
		MeiTuan,    //美团版本
		PSBC,       //邮储版本
		BRASIL,     //巴西版本
		AliBaBa,	// 阿里版本，代码里还没有添加阿里版本
		KouBei,		// 口碑
		
	}
	
	/**
	 * 用于识别产品对各个模块的支持情况
	 * @author zhengxq add by 20190729
	 *
	 */
	public enum Mod_Enable
	{	
		IccEnable,
		SamEnable,
		MagEnable,
		RfidEnable,
		PrintEnable,
		PinEnable,
		PrintEnableReg,
		KeyBoardEnable,
		DomestProduct,// true国内产品 false海外产品，海外主要指巴西产品和oversea
		SecAndroidEnable,// 密钥安装存储位置，true android端 false K21端
		IsPoynt,// 是否是Poynt产品
		SupportMpos,// true 支持mpos false 不支持mpos
		CutPaper,// true支持切纸功能 false不支持切纸功能
		CashBox,// true支持钱箱 false不支持钱箱功能
		PinPad,//PinPad串口
		RS232,// 旧RS232串口
//		New_RS232,// 新RS232串口
		Battery,// 电池模块
		IsForth,// 是否forth平台产品,forth就是新的支付架构
		EthEnable,// 是否支持以太网
		isPhysicalBoard,// 是否物理键盘
		SecEnable,// 是否有安全模块
		isPCI,// 是否需要根据PCI规范，true是，否不是
	}
	
	public enum SysCfg{
		Volume_Stream_Music,
		Screen_Brightness,
		Acceleromter_Rotation,
		Alarm_Alert,
		
		Persist_Sys_Nltest,
		Sys_Nltest,
		Persist_UsbCam,
		Screen_Vice_Brightness,
		Addr_Size,
		Manufacturer_type,
		Serial_No,
		Custom_Id,
		Pci_reboot,
		Log_Size;
	}
	
	public enum UsbModule{
		HuaJie,
		YuCong,
		AoBi,
	}
	
	/*------------global variables definition-----------------------*/
	public static LinkStatus linkStatus;
	public static LinkType linktype = LinkType.SYNC;
	public static NetStatus netStatus;
	public static TransStatus transStatus;
	public static Sock_t 	sockt;
	public static DiskType  diskType;
	public static Paintter_ns patterNs;
	public static _SMART_t   smart_t;
	public static AYSNCTASK_LIST_K21 aysnctask_k21;
}
