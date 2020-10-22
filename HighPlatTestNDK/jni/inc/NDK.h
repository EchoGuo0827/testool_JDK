#include <stdio.h>
#include <time.h>
#include <sys/socket.h>
//#include <linux/delay.h>
#include <fcntl.h>
#include <termios.h>
#ifndef __NDKAPI__H
#define __NDKAPI__H

/** @addtogroup �����붨��
* @{
*/
typedef void *	NDK_HANDLE;         ///<SSL��Timer���
//typedef unsigned int 	uint;
typedef unsigned char 	uchar;
typedef unsigned short 	ushort;
typedef unsigned long 	ulong;

/**
 *@brief ��������뷵��
*/
typedef enum {
	NDK_OK,							/**<�����ɹ�*/
	NDK_ERR=-1,						/**<����ʧ��*/
	NDK_ERR_INIT_CONFIG = -2,	 	/**<��ʼ������ʧ��*/
	NDK_ERR_CREAT_WIDGET = -3,		/**<�����������*/
	NDK_ERR_OPEN_DEV = -4,			/**<���豸�ļ�����*/
	NDK_ERR_IOCTL = -5,				/**<����ô���*/
	NDK_ERR_PARA = -6,				/**<����Ƿ�*/
	NDK_ERR_PATH = -7,				/**<�ļ�·���Ƿ�*/
	NDK_ERR_DECODE_IMAGE = -8,		/**<ͼ�����ʧ��*/
	NDK_ERR_MACLLOC = -9,			/**<�ڴ�ռ䲻��*/
	NDK_ERR_TIMEOUT = -10,			/**<��ʱ����*/
	NDK_ERR_QUIT = -11,				/**<��ȡ���˳�*/
	NDK_ERR_WRITE = -12, 			/**<д�ļ�ʧ��*/
	NDK_ERR_READ = -13, 			/**<���ļ�ʧ��*/
	NDK_ERR_OVERFLOW = -15,			/**<�������*/
	NDK_ERR_SHM = -16,				/**<�����ڴ����*/
	NDK_ERR_NO_DEVICES=-17,			/**<POS�޸��豸*/
	NDK_ERR_NOT_SUPPORT=-18, 		/**<��֧�ָù���*/
	NDK_ERR_NOSWIPED = -50,			/**<�޴ſ�ˢ����¼*/
	NDK_ERR_SWIPED_DATA=-51,		/**<��ſ���ݸ�ʽ��*/
	NDK_ERR_USB_LINE_UNCONNECT = -100,  /**<USB��δ����*/
	NDK_ERR_NO_SIMCARD = -201,		/**<��SIM��*/
	NDK_ERR_PIN = -202, 			/**<SIM���������*/
	NDK_ERR_PIN_LOCKED = -203,		/**<SIM������*/
	NDK_ERR_PIN_UNDEFINE = -204,	/**<SIM��δ�������*/
	NDK_ERR_EMPTY = -205,			/**<���ؿմ�*/
	NDK_ERR_PPP_PARAM = -301,		/**<PPP�������*/
	NDK_ERR_PPP_DEVICE = -302,		/**<PPP��Ч�豸*/
	NDK_ERR_PPP_OPEN = -303, 		/**<PPP�Ѵ�*/
	NDK_ERR_TCP_ALLOC = -304,	/**<�޷�����*/
	NDK_ERR_TCP_PARAM = -305,	/**<��Ч����*/
	NDK_ERR_TCP_TIMEOUT = -306,	/**<���䳬ʱ*/
	NDK_ERR_TCP_INVADDR = -307,	/**<��Ч��ַ*/
	NDK_ERR_TCP_CONNECT = -308,	/**<û������*/
	NDK_ERR_TCP_PROTOCOL = -309,/**<Э�����*/
	NDK_ERR_TCP_NETWORK = -310,	/**<�������*/
	NDK_ERR_TCP_SEND = -311,	/**<���ʹ���*/
	NDK_ERR_TCP_RECV = -312,	/**<���մ���*/

	NDK_ERR_SSL_PARAM = -350,       	/**<��Ч����*/
	NDK_ERR_SSL_ALREADCLOSE = -351, 	/**<�����ѹر�*/
	NDK_ERR_SSL_ALLOC = -352,       	/**<�޷�����*/
	NDK_ERR_SSL_INVADDR = -353,     	/**<��Ч��ַ*/
	NDK_ERR_SSL_TIMEOUT = -354,     	/**<���ӳ�ʱ*/
	NDK_ERR_SSL_MODEUNSUPPORTED = -355, /**<ģʽ��֧��*/
	NDK_ERR_SSL_SEND = -356,        	/**<���ʹ���*/
	NDK_ERR_SSL_RECV = -357,        	/**<���մ���*/
	NDK_ERR_SSL_CONNECT = -358,       	/**<û������*/

	NDK_ERR_NET_GETADDR = -401,			/**<��ȡ���ص�ַ����������ʧ��*/
	NDK_ERR_NET_GATEWAY = -402,			/**<��ȡ��ص�ַʧ��*/
	NDK_ERR_NET_ADDRILLEGAL =-403,		/**<��ȡ��ַ��ʽ����*/
	NDK_ERR_NET_UNKNOWN_COMMTYPE=-404,	/**<δ֪��ͨ������*/
	NDK_ERR_NET_INVALIDIPSTR=-405,		/**<��Ч��IP�ַ�*/
	NDK_ERR_NET_UNSUPPORT_COMMTYPE=-406,	/**<��֧�ֵ�ͨ������*/

	NDK_ERR_THREAD_PARAM = -450,     	/**<��Ч����*/
	NDK_ERR_THREAD_ALLOC = -451,     	/**<��Ч����*/
	NDK_ERR_THREAD_CMDUNSUPPORTED = -452,     /**<���֧��*/
	
	NDK_ERR_MODEM_INIT_NOT=-505,			/**<MODEM δ���г�ʼ��*/
	NDK_ERR_MODEM_SDLCWRITEFAIL=-506,		/**<MODEM ͬ��дʧ��*/
	NDK_ERR_MODEM_SDLCHANGUPFAIL=-510,		/**<MODEM ͬ���Ҷ�ʧ��*/
	NDK_ERR_MODEM_ASYNHANGUPFAIL=-511,		/**<MODEM �첽�Ҷ�ʧ��*/
	NDK_ERR_MODEM_SDLCCLRBUFFAIL=-512,		/**<MODEM ͬ���建��ʧ��*/
	NDK_ERR_MODEM_ASYNCLRBUFFAIL=-513,		/**<MODEM �첽�建��ʧ��*/
	NDK_ERR_MODEM_ATCOMNORESPONSE=-514,		/**<MODEM AT��������Ӧ*/
	NDK_ERR_MODEM_PORTWRITEFAIL=-515,		/**<MODEM �˿�д���ʧ��*/
	NDK_ERR_MODEM_SETCHIPFAIL=-516,			/**<MODEM ģ��Ĵ�������ʧ��*/
	NDK_ERR_MODEM_STARTSDLCTASK=-517,		/**<MODEM ����ʱ����SDLC ����ʧ��*/
	NDK_ERR_MODEM_QUIT=-519,				/**<MODEM �ֶ��˳�*/
	NDK_ERR_MODEM_NOPREDIAL=-520,			/**<MODEM δ����*/
	NDK_ERR_MODEM_NOCARRIER=-521,			/**<MODEM û�ز�*/
	NDK_ERR_MODEM_NOLINE=-523,				/**<MODEM δ����*/
	NDK_ERR_MODEM_OTHERMACHINE=-524,		/**<MODEM ���ڲ���*/
	NDK_ERR_MODEM_PORTREADFAIL=-525,		/**<MODEM �˿ڶ����ʧ��*/
	NDK_ERR_MODEM_CLRBUFFAIL=-526,			/**<MODEM ��ջ���ʧ��*/
	NDK_ERR_MODEM_ATCOMMANDERR=-527,		/**<MODEM AT�������*/
	NDK_ERR_MODEM_STATUSUNDEFINE=-528,		/**<MODEM ״̬δȷ��״̬*/

	NDK_ERR_ICC_WRITE_ERR =			-601,	/**<д����83c26����*/
	NDK_ERR_ICC_COPYERR=			-602,	/**<�ں���ݿ�������*/
	NDK_ERR_ICC_POWERON_ERR=		-603,	/**<�ϵ����*/
	NDK_ERR_ICC_COM_ERR=			-604,	/**<�������*/
	NDK_ERR_ICC_CARDPULL_ERR=		-605,	/**<���γ���*/
	NDK_ERR_ICC_CARDNOREADY_ERR=	-606,	/**<��δ׼����*/

	NDK_ERR_USDDISK_PARAM =  -650,          /**<��Ч����*/
	NDK_ERR_USDDISK_DRIVELOADFAIL =  -651,  /**<U�̻�SD�������ʧ��*/
	NDK_ERR_USDDISK_NONSUPPORTTYPE =  -652, /**<��֧�ֵ�����*/
	NDK_ERR_USDDISK_UNMOUNTFAIL =  -653,    /**<����ʧ��*/
	NDK_ERR_USDDISK_UNLOADDRIFAIL =  -654,  /**<ж����ʧ��*/
	NDK_ERR_USDDISK_IOCFAIL =  -655,        /**<����ô���*/

	NDK_ERR_APP_BASE=(-800),						/**<Ӧ�ýӿڴ������*/
	NDK_ERR_APP_NOT_EXIST=(NDK_ERR_APP_BASE-1),		/**<Ӧ�������*/
	NDK_ERR_APP_NOT_MATCH=(NDK_ERR_APP_BASE-2),	    /**<�������ļ���ƥ��*/
	NDK_ERR_APP_FAIL_SEC=(NDK_ERR_APP_BASE-3),	   	/**<��ȡ��ȫ����״̬ʧ��*/
	NDK_ERR_APP_SEC_ATT=(NDK_ERR_APP_BASE-4),	  	/**<���ڰ�ȫ����*/
	NDK_ERR_APP_FILE_EXIST=(NDK_ERR_APP_BASE-5),	/**<Ӧ���и��ļ��Ѵ���*/
	NDK_ERR_APP_FILE_NOT_EXIST=(NDK_ERR_APP_BASE-6),/**<Ӧ���и��ļ�������*/
	NDK_ERR_APP_FAIL_AUTH=(NDK_ERR_APP_BASE-7),	  	/**<֤����֤ʧ��*/
	NDK_ERR_APP_LOW_VERSION=(NDK_ERR_APP_BASE-8),	/**<������İ汾��Ӧ�ð汾��*/

	NDK_ERR_APP_MAX_CHILD=(NDK_ERR_APP_BASE-9),			/**<��Ӧ������������������Ŀ*/
	NDK_ERR_APP_CREAT_CHILD=(NDK_ERR_APP_BASE-10),		/**<�����ӽ�̴���*/
	NDK_ERR_APP_WAIT_CHILD=(NDK_ERR_APP_BASE-11),		/**<�ȴ��ӽ�̽������*/
	NDK_ERR_APP_FILE_READ=(NDK_ERR_APP_BASE-12),		/**<���ļ�����*/
	NDK_ERR_APP_FILE_WRITE=(NDK_ERR_APP_BASE-13),		/**<д�ļ�����*/
	NDK_ERR_APP_FILE_STAT=(NDK_ERR_APP_BASE-14),		/**<��ȡ�ļ���Ϣ����*/
	NDK_ERR_APP_FILE_OPEN=(NDK_ERR_APP_BASE-15),		/**<�ļ��򿪴���*/
	NDK_ERR_APP_NLD_HEAD_LEN=(NDK_ERR_APP_BASE-16),		/**<NLD�ļ���ȡͷ��Ϣ���ȴ���*/
	NDK_ERR_APP_PUBKEY_EXPIRED=(NDK_ERR_APP_BASE-17),	/**<��Կ��Ч��*/
	NDK_ERR_APP_MMAP=(NDK_ERR_APP_BASE-18),				/**<�ڴ�ӳ�����*/
	NDK_ERR_APP_MALLOC=(NDK_ERR_APP_BASE-19),			/**<��̬�ڴ�������*/
	NDK_ERR_APP_SIGN_DECRYPT=(NDK_ERR_APP_BASE-20),		/**<ǩ����ݽ�ǩ����*/
	NDK_ERR_APP_SIGN_CHECK=(NDK_ERR_APP_BASE-21),		/**<ǩ�����У�����*/
	NDK_ERR_APP_MUNMAP=(NDK_ERR_APP_BASE-22),			/**<�ڴ�ӳ���ͷŴ���*/
	NDK_ERR_APP_TAR=(NDK_ERR_APP_BASE-23),				/**<tar����ִ��ʧ��*/
	NDK_ERR_APP_KEY_UPDATE_BAN=(NDK_ERR_APP_BASE-24),				/**<����״̬��ֹ��Կ��*/
	NDK_ERR_APP_FIRM_PATCH_VERSION=(NDK_ERR_APP_BASE-25),				/**�̼�����������汾��ƥ��*/

    NDK_ERR_SECP_BASE = (-1000),								/**<δ֪����*/
    NDK_ERR_SECP_TIMEOUT = (NDK_ERR_SECP_BASE - 1),             /**<��ȡ��ֵ��ʱ*/
    NDK_ERR_SECP_PARAM = (NDK_ERR_SECP_BASE - 2),               /**<�������Ƿ�*/
    NDK_ERR_SECP_DBUS = (NDK_ERR_SECP_BASE - 3),                /**<DBUSͨѶ����*/
    NDK_ERR_SECP_MALLOC = (NDK_ERR_SECP_BASE - 4),              /**<��̬�ڴ�������*/
    NDK_ERR_SECP_OPEN_SEC = (NDK_ERR_SECP_BASE - 5),            /**<�򿪰�ȫ�豸����*/
    NDK_ERR_SECP_SEC_DRV = (NDK_ERR_SECP_BASE - 6),             /**<��ȫ�豸��������*/
    NDK_ERR_SECP_GET_RNG = (NDK_ERR_SECP_BASE - 7),             /**<��ȡ�����*/
    NDK_ERR_SECP_GET_KEY = (NDK_ERR_SECP_BASE - 8),             /**<��ȡ��Կֵ*/
    NDK_ERR_SECP_KCV_CHK = (NDK_ERR_SECP_BASE - 9),             /**<KCVУ�����*/
    NDK_ERR_SECP_GET_CALLER = (NDK_ERR_SECP_BASE - 10),         /**<��ȡ��������Ϣ����*/
    NDK_ERR_SECP_OVERRUN = (NDK_ERR_SECP_BASE - 11),            /**<���д������*/
    NDK_ERR_SECP_NO_PERMIT = (NDK_ERR_SECP_BASE - 12),          /**<Ȩ�޲�����*/
	NDK_ERR_SECP_TAMPER = (NDK_ERR_SECP_BASE - 13),          	/**<��ȫ����*/

    NDK_ERR_SECVP_BASE = (-1100),                           /**<δ֪����*/
    NDK_ERR_SECVP_TIMEOUT = (NDK_ERR_SECVP_BASE - 1),       /**<��ȡ��ֵ��ʱ*/
    NDK_ERR_SECVP_PARAM = (NDK_ERR_SECVP_BASE - 2),         /**<�������Ƿ�*/
    NDK_ERR_SECVP_DBUS = (NDK_ERR_SECVP_BASE - 3),          /**<DBUSͨѶ����*/
    NDK_ERR_SECVP_OPEN_EVENT0 =	(NDK_ERR_SECVP_BASE - 4),   /**<��event0�豸����*/
    NDK_ERR_SECVP_SCAN_VAL = (NDK_ERR_SECVP_BASE - 5),      /**<ɨ��ֵ��������*/
    NDK_ERR_SECVP_OPEN_RNG = (NDK_ERR_SECVP_BASE - 6),      /**<��������豸����*/
    NDK_ERR_SECVP_GET_RNG = (NDK_ERR_SECVP_BASE - 7),       /**<��ȡ��������*/
    NDK_ERR_SECVP_GET_ESC = (NDK_ERR_SECVP_BASE - 8),       /**<�û�ȡ����˳�*/
    NDK_ERR_SECVP_VPP = (-1120),                            /**<δ֪����*/
    NDK_ERR_SECVP_INVALID_KEY=(NDK_ERR_SECVP_VPP),  		/**<��Ч��Կ,�ڲ�ʹ��.*/
	NDK_ERR_SECVP_NOT_ACTIVE=(NDK_ERR_SECVP_VPP-1),  		/**<VPPû�м����һ�ε���VPPInit.*/
	NDK_ERR_SECVP_TIMED_OUT=(NDK_ERR_SECVP_VPP-2),			/**<�Ѿ�����VPP��ʼ����ʱ��.*/
	NDK_ERR_SECVP_ENCRYPT_ERROR=(NDK_ERR_SECVP_VPP-3),		/**<��ȷ�ϼ�󣬼��ܴ���.*/
	NDK_ERR_SECVP_BUFFER_FULL=(NDK_ERR_SECVP_VPP-4),		/**<����BUFԽ�磬�������PIN̫����*/
	NDK_ERR_SECVP_PIN_KEY=(NDK_ERR_SECVP_VPP-5),  			/**<��ݼ��£�����"*".*/
	NDK_ERR_SECVP_ENTER_KEY=(NDK_ERR_SECVP_VPP-6),			/**<ȷ�ϼ��£�PIN����.*/
	NDK_ERR_SECVP_BACKSPACE_KEY=(NDK_ERR_SECVP_VPP-7),		/**<�˸����.*/
	NDK_ERR_SECVP_CLEAR_KEY=(NDK_ERR_SECVP_VPP-8),  		/**<�����£��������'*'��ʾ.*/
	NDK_ERR_SECVP_CANCEL_KEY=(NDK_ERR_SECVP_VPP-9),  		/**<ȡ�����.*/
	NDK_ERR_SECVP_GENERALERROR=(NDK_ERR_SECVP_VPP-10),  	/**<�ý���޷������ڲ�����.*/
	NDK_ERR_SECVP_CUSTOMERCARDNOTPRESENT=(NDK_ERR_SECVP_VPP-11), /**<IC�����γ�*/
	NDK_ERR_SECVP_HTCCARDERROR=(NDK_ERR_SECVP_VPP-12),  	/**<�������ܿ�����.*/
	NDK_ERR_SECVP_WRONG_PIN_LAST_TRY=(NDK_ERR_SECVP_VPP-13),/**<���ܿ�-���벻��ȷ������һ��.*/
	NDK_ERR_SECVP_WRONG_PIN=(NDK_ERR_SECVP_VPP-14), 		/**<���ܿ�-�����һ��.*/
	NDK_ERR_SECVP_ICCERROR=(NDK_ERR_SECVP_VPP-15),  		/**<���ܿ�-����̫���*/
	NDK_ERR_SECVP_PIN_BYPASS=(NDK_ERR_SECVP_VPP-16),  		/**<���ܿ�-PIN��֤ͨ��,����PIN��0����*/
	NDK_ERR_SECVP_ICCFAILURE=(NDK_ERR_SECVP_VPP-17),  		/**<���ܿ�-�������.*/
	NDK_ERR_SECVP_GETCHALLENGE_BAD=(NDK_ERR_SECVP_VPP-18),  /**<���ܿ�-Ӧ����90 00.*/
	NDK_ERR_SECVP_GETCHALLENGE_NOT8=(NDK_ERR_SECVP_VPP-19), /**<���ܿ�-��Ч��Ӧ�𳤶�.*/
 	NDK_ERR_SECVP_PIN_ATTACK_TIMER=(NDK_ERR_SECVP_VPP-20),  /**<PIN������ʱ��������*/

    NDK_ERR_SECCR_BASE = (-1200),                           /**<δ֪����*/
    NDK_ERR_SECCR_TIMEOUT = (NDK_ERR_SECCR_BASE - 1),       /**<��ȡ��ֵ��ʱ*/
    NDK_ERR_SECCR_PARAM = (NDK_ERR_SECCR_BASE - 2),         /**<�������Ƿ�*/
    NDK_ERR_SECCR_DBUS = (NDK_ERR_SECCR_BASE - 3),          /**<DBUSͨѶ����*/
    NDK_ERR_SECCR_MALLOC = (NDK_ERR_SECCR_BASE - 4),        /**<��̬�ڴ�������*/
    NDK_ERR_SECCR_OPEN_RNG = (NDK_ERR_SECCR_BASE - 5),      /**<��������豸����*/
    NDK_ERR_SECCR_DRV = (NDK_ERR_SECCR_BASE - 6),           /**<����ܴ���*/
    NDK_ERR_SECCR_KEY_TYPE = (NDK_ERR_SECCR_BASE - 7),      /**<��Կ���ʹ���*/
    NDK_ERR_SECCR_KEY_LEN = (NDK_ERR_SECCR_BASE - 8),       /**<��Կ���ȴ���*/
    NDK_ERR_SECCR_GET_KEY = (NDK_ERR_SECCR_BASE - 9),       /**<��ȡ��Կ����*/

    NDK_ERR_SECKM_BASE = (-1300),								/**<δ֪����*/
    NDK_ERR_SECKM_TIMEOUT = (NDK_ERR_SECKM_BASE - 1),           /**<��ȡ��ֵ��ʱ*/
    NDK_ERR_SECKM_PARAM = (NDK_ERR_SECKM_BASE - 2),             /**<�������Ƿ�*/
    NDK_ERR_SECKM_DBUS = (NDK_ERR_SECKM_BASE - 3),              /**<DBUSͨѶ����*/
    NDK_ERR_SECKM_MALLOC = (NDK_ERR_SECKM_BASE - 4),            /**<��̬�ڴ�������*/
    NDK_ERR_SECKM_OPEN_DB = (NDK_ERR_SECKM_BASE - 5),           /**<��ݿ�򿪴���*/
    NDK_ERR_SECKM_DEL_DB = (NDK_ERR_SECKM_BASE - 6),            /**<ɾ����ݿ����*/
    NDK_ERR_SECKM_DEL_REC = (NDK_ERR_SECKM_BASE - 7),           /**<ɾ���¼����*/
    NDK_ERR_SECKM_INSTALL_REC = (NDK_ERR_SECKM_BASE - 8),       /**<��װ��Կ��¼����*/
    NDK_ERR_SECKM_READ_REC = (NDK_ERR_SECKM_BASE - 9),          /**<����Կ��¼����*/
    NDK_ERR_SECKM_OPT_NOALLOW = (NDK_ERR_SECKM_BASE - 10),      /**<����������*/
    NDK_ERR_SECKM_KEY_MAC = (NDK_ERR_SECKM_BASE - 11),          /**<��ԿMACУ�����*/
    NDK_ERR_SECKM_KEY_TYPE = (NDK_ERR_SECKM_BASE - 12),         /**<��Կ���ʹ���*/
    NDK_ERR_SECKM_KEY_ARCH = (NDK_ERR_SECKM_BASE - 13),         /**<��Կ��ϵ����*/
    NDK_ERR_SECKM_KEY_LEN  = (NDK_ERR_SECKM_BASE - 14),         /**<��Կ���ȴ���*/

	NDK_ERR_RFID_INITSTA=			-2005,  /**<�ǽӴ���-��Ƶ�ӿ��������ϻ���δ����*/
	NDK_ERR_RFID_NOCARD=			-2008,  /**<�ǽӴ���-�޿�  0x0D*/
	NDK_ERR_RFID_MULTICARD=			-2009,  /**<�ǽӴ���-�࿨״̬*/
	NDK_ERR_RFID_SEEKING=			-2010,  /**<�ǽӴ���-Ѱ��/��������ʧ��*/
	NDK_ERR_RFID_PROTOCOL=			-2011,  /**<�ǽӴ���-��֧��ISO1444-4Э�飬��M1��  F*/

	NDK_ERR_RFID_NOPICCTYPE=		-2012,  /**<�ǽӴ���-δ���ÿ� 0x01*/
	NDK_ERR_RFID_NOTDETE=			-2013,  /**<�ǽӴ���-δѰ��   0x02*/
	NDK_ERR_RFID_AANTI=				-2014,  /**<�ǽӴ���-A����ͻ(���ſ�����)  0x03*/
	NDK_ERR_RFID_RATS=				-2015,  /**<�ǽӴ���-A��RATS��̳���   0x04*/
	NDK_ERR_RFID_BACTIV=			-2016,  /**<�ǽӴ���-B������ʧ��   0x07*/
	NDK_ERR_RFID_ASEEK=				-2017,  /**<�ǽӴ���-A��Ѱ��ʧ��(���ܶ��ſ�����)   0x0A*/
	NDK_ERR_RFID_BSEEK=				-2018,  /**<�ǽӴ���-B��Ѱ��ʧ��(���ܶ��ſ�����)   0x0B*/
	NDK_ERR_RFID_ABON=				-2019,  /**<�ǽӴ���-A��B��ͬʱ����   0x0C*/
	NDK_ERR_RFID_UPED=				-2020,  /**<�ǽӴ���-�Ѿ�����(�ϵ�)   0x0E*/
	NDK_ERR_RFID_NOTACTIV=			-2021,  /**<�ǽӴ���-δ����*/
	NDK_ERR_RFID_COLLISION_A=       -2022,  /**<�ǽӴ���-A����ͻ*/
	NDK_ERR_RFID_COLLISION_B=       -2023,  /**<�ǽӴ���-B����ͻ*/

	NDK_ERR_MI_NOTAGERR=			-2030,  /**<�ǽӴ���-�޿�,				0xff*/
	NDK_ERR_MI_CRCERR=				-2031,  /**<�ǽӴ���-CRC��,				0xfe*/
	NDK_ERR_MI_EMPTY=				-2032,  /**<�ǽӴ���-�ǿ�,				0xfd*/
	NDK_ERR_MI_AUTHERR=				-2033,  /**<�ǽӴ���-��֤��,			0xfc*/
	NDK_ERR_MI_PARITYERR=			-2034,  /**<�ǽӴ���-��ż��,			0xfb*/
	NDK_ERR_MI_CODEERR=				-2035,  /**<�ǽӴ���-���մ����			0xfa*/
	NDK_ERR_MI_SERNRERR=            -2036,  /**<�ǽӴ���-����ͻ���У���	0xf8*/
	NDK_ERR_MI_KEYERR=              -2037,  /**<�ǽӴ���-��֤KEY��			0xf7*/
	NDK_ERR_MI_NOTAUTHERR=          -2038,  /**<�ǽӴ���-δ��֤				0xf6*/
	NDK_ERR_MI_BITCOUNTERR=         -2039,  /**<�ǽӴ���-����BIT��			0xf5*/
	NDK_ERR_MI_BYTECOUNTERR=        -2040,  /**<�ǽӴ���-�����ֽڴ�			0xf4*/
	NDK_ERR_MI_WriteFifo=           -2041,  /**<�ǽӴ���-FIFOд����			0xf3*/
	NDK_ERR_MI_TRANSERR=            -2042,  /**<�ǽӴ���-���Ͳ�������		0xf2*/
	NDK_ERR_MI_WRITEERR=            -2043,  /**<�ǽӴ���-д��������			0xf1*/
	NDK_ERR_MI_INCRERR=				-2044,  /**<�ǽӴ���-������������		0xf0*/
	NDK_ERR_MI_DECRERR=             -2045,  /**<�ǽӴ���-������������		0xef*/
	NDK_ERR_MI_OVFLERR=             -2046,  /**<�ǽӴ���-�������			0xed*/
	NDK_ERR_MI_FRAMINGERR=          -2047,  /**<�ǽӴ���-֡��				0xeb*/
	NDK_ERR_MI_COLLERR=             -2048,  /**<�ǽӴ���-��ͻ				0xe8*/
	NDK_ERR_MI_INTERFACEERR=        -2049,  /**<�ǽӴ���-��λ�ӿڶ�д��		0xe6*/
	NDK_ERR_MI_ACCESSTIMEOUT=       -2050,  /**<�ǽӴ���-���ճ�ʱ			0xe5*/
	NDK_ERR_MI_PROTOCOLERR=			-2051,  /**<�ǽӴ���-Э���				0xe4*/
	NDK_ERR_MI_QUIT=                -2052,  /**<�ǽӴ���-�쳣��ֹ			0xe2*/
	NDK_ERR_MI_PPSErr=				-2053,  /**<�ǽӴ���-PPS������			0xe1*/
	NDK_ERR_MI_SpiRequest=			-2054,  /**<�ǽӴ���-����SPIʧ��		0xa0*/
	NDK_ERR_MI_NY_IMPLEMENTED=		-2055,  /**<�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c*/
	NDK_ERR_MI_CardTypeErr=			-2056,  /**<�ǽӴ���-�����ʹ�			0x83*/
	NDK_ERR_MI_ParaErrInIoctl=		-2057,  /**<�ǽӴ���-IOCTL�����		0x82*/
	NDK_ERR_MI_Para=				-2059,  /**<�ǽӴ���-�ڲ������			0xa9*/

	NDK_ERR_NFC_NODEVICE=           -2080,  /**NFC-û���豸*/
	NDK_ERR_NFC_INVDATA=            -2081,  /**NFC-��ȡ��������*/

	NDK_ERR_WIFI_INVDATA=           -3001,  /**<WIFI-��Ч����*/
    NDK_ERR_WIFI_DEVICE_FAULT=      -3002,  /**<WIFI-�豸״̬����*/
    NDK_ERR_WIFI_CMD_UNSUPPORTED=   -3003,  /**<WIFI-��֧�ֵ�����*/
    NDK_ERR_WIFI_DEVICE_UNAVAILABLE=-3004,  /**<WIFI-�豸������*/
    NDK_ERR_WIFI_DEVICE_NOTOPEN=    -3005,  /**<WIFI-û��ɨ�赽AP*/
    NDK_ERR_WIFI_DEVICE_BUSY=       -3006,  /**<WIFI-�豸æ*/
    NDK_ERR_WIFI_UNKNOWN_ERROR=     -3007,  /**<WIFI-δ֪����*/
    NDK_ERR_WIFI_PROCESS_INBADSTATE=-3008,  /**<WIFI-�޷����ӵ�AP*/
    NDK_ERR_WIFI_SEARCH_FAULT=      -3009,  /**<WIFI-ɨ��״̬����*/
    NDK_ERR_WIFI_DEVICE_TIMEOUT=    -3010,  /**<WIFI-�豸��ʱ*/
    NDK_ERR_RFID_BUSY = -3101,                      /**<��Ƶ��״̬æ*/
    NDK_ERR_PRN_BUSY = -3102,                       /**<��ӡ״̬æ*/
    NDK_ERR_ICCARD_BUSY = -3103,                        /**<IC��״̬æ*/
    NDK_ERR_MAG_BUSY = -3104,                       /**<�ſ�״̬æ*/
    NDK_ERR_PIN_BUSY = -3107,                       /*����PIN����״̬*/

	NDK_ERR_LINUX_ERRNO_BASE=		-5000, /**<<LINUX>ϵͳ�����ERROR����ǰ׺*/
	NDK_ERR_LINUX_TCP_TIMEOUT=  (NDK_ERR_LINUX_ERRNO_BASE-110),/**<TCPԶ�̶˿ڴ���*/
	NDK_ERR_LINUX_TCP_REFUSE=  (NDK_ERR_LINUX_ERRNO_BASE-111),/**<TCPԶ�̶˿ڱ��ܾ�*/
	NDK_ERR_LINUX_TCP_NOT_OPEN=		 (NDK_ERR_LINUX_ERRNO_BASE-88),/**<TCP���δ�򿪴���*/
}EM_NDK_ERR;

/** @} */ // ������������

/** @addtogroup ��ʾ
* @{
*/


#ifndef NL_COLOR_T
#define NL_COLOR_T
typedef unsigned int color_t;/**<RGBɫ����ֵ,0(��ɫ) - 0xFFFF(��ɫ)*/
#endif

/**
 *@brief ��ʾģʽ����
*/
typedef enum {
	TEXT_ATTRIBUTE_NORMAL = 1<<0,			/**<��������ʾ*/
	TEXT_ATTRIBUTE_REVERSE = 1<<1,		/**<���巴����ʾ*/
	TEXT_ATTRIBUTE_UNDERLINE = 1<<2,		/**<������»���*/
	TEXT_ATTRIBUTE_NOBACKCOLOR = 1<<3,		/**<�����ޱ���ɫ*/
}EM_TEXT_ATTRIBUTE;

/**
 *@brief �������
*/
typedef enum {
	BACKLIGHT_OFF ,			/**<����Һ������*/
	BACKLIGHT_ON,			/**<�ر�Һ��*/
	BACKLIGHT_LOCKON,		/**<��ֹҺ�������Զ��ر�*/
}EM_BACKLIGHT;

/**
 *@brief ϵͳ����ѡ����
*/
typedef enum {
	DISPFONT_CUSTOM,			/**<ϵͳĬ�ϵ���ʾ����*/
	DISPFONT_EXTRA,			/**<����ߴ����ʾ����*/
	DISPFONT_USER,				/**<�û��Զ�������*/
}EM_DISPFONT;

/**
 *@brief ϵͳ������ɫ���ö���
*/
typedef enum {
	FONTCOLOR_NORMAL,				/**<��������ʾʱ������ɫ*/
	FONTCOLOR_REVERSE,				/**<���巴����ʾʱ������ɫ*/
	FONTCOLOR_BG_REVERSE,				/**<���巴����ʾʱ������ɫ*/
}EM_FONTCOLOR;

/**
 *@brief �������ģʽ����
*/
typedef	enum  {
    RECT_PATTERNS_NO_FILL,        /**<����䣬ֻ���Ʊ߿�*/
    RECT_PATTERNS_SOLID_FILL      /**<���ģʽ*/
}EM_RECT_PATTERNS;

/**
 *@brief	�û���������ʼ����
 *@details  �����ڳ���������ã��ú���ɹ����ú���ʾģ���API������ʹ�á�
 *@return
 *@li	    NDK_OK		                        �����ɹ�
 *@li       \ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG"                  ��ʼ������ʧ��
 *@li       \ref NDK_ERR_CREAT_WIDGET "NDK_ERR_CREAT_WIDGET"                 �����������
*/
int NDK_ScrInitGui(void);/**	*@example NDK_disp_example.c**/


/**
 *@brief	��ȡ��ʾģ��汾��
 *@retval	pszVer	����ģ��汾,�����pszVerӦ�ò�С��16�ֽڡ�
 *@return
 *@li	    NDK_OK	                �����ɹ�
 *@li       \ref NDK_ERR_PARA "NDK_ERR_PARA" 		    ����Ƿ�
*/
int NDK_ScrGetVer(char *pszVer);

/**
 *@brief	������ʾģʽ������ȡ֮ǰ����ʾģʽ��
 *@param	emNewAttr 	Ҫ���õ�����ʾģʽ��
 *@retval	pemOldAttr 	���֮ǰ����ʾģʽ��pemOldAttrΪNULLʱ�����ء�
 *@return
 *@li		NDK_OK					�����ɹ�
 *@li		\ref NDK_ERR_PARA "NDK_ERR_PARA" 		    ����Ƿ�
*/
int NDK_ScrSetAttr(EM_TEXT_ATTRIBUTE emNewAttr, EM_TEXT_ATTRIBUTE *pemOldAttr);

/**
 *@brief	���浱ǰ��Ļ
 *@details	������ʾ���ݡ����λ�ü���ʾģʽ���ñ�����ɵ���\ref NDK_ScrPop "NDK_ScrPop"���ٻָ���ʾ��
			NDK_ScrPush��\ref NDK_ScrPop "NDK_ScrPop"�ɶ�ʹ�ã�����Ƕ�ס�
 *@return
 *@li	NDK_OK				�����ɹ�
*/
int NDK_ScrPush(void);

/**
 *@brief	���ٻָ�����\ref NDK_ScrPush "NDK_ScrPush"�������ʾ״̬��������ʾ���ݡ����λ�ü��ı���ʾ���ԡ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ�ܣ�δ������ʾ״̬��
*/
int NDK_ScrPop(void);

/**
 *@brief	���ñ���ͼƬ��ͼƬ�ļ�֧�ָ�ʽ��鿴�����͵�ͼƬ��ʽ���ơ�
 *@param	pszFilePath 	ͼƬ�ļ�·��
 *@return
 *@li	NDK_OK						�����ɹ�
 *@li	\ref NDK_ERR_PATH "NDK_ERR_PATH" 		        �ļ�·���Ƿ�
 *@li   \ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE"         ͼ�����ʧ��
*/
int NDK_ScrSetbgPic(char *pszFilePath);

/**
 *@brief	ȡ��ͼƬ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrClrbgPic(void);

/**
 *@brief	�������ѹ���Ƶ��������(0,0)��ͬʱ����Ļ��ʾģʽ����Ϊ\ref TEXT_ATTRIBUTE_NORMAL "TEXT_ATTRIBUTE_NORMAL"��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrClrs(void);

/**
 *@brief	���������(��λ������)���ѹ���Ƶ�(0,unStartY)����\n
		  	���������ӿڱ߽�ʱ�����ӿڱ߽�Ϊ׼
 *@param	unStartY		��ʼ�кţ�����꣬��λ�����أ�����0��ʼ����
 *@param	unEndY			�����кţ�����꣬��λ�����أ�����0��ʼ����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrClrLine(uint unStartY,uint unEndY);


/**
 *@brief	Һ����ʾ���λ���Ƶ��������(unX,unY)����
			����������Ƿ������걣��λ�ò���,���ش�����Ϣ��
 *@param	unX 	����꣨��λ�����أ�
 *@param	unY 	����꣨��λ�����أ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGotoxy(uint unX, uint unY);


/**
 *@brief	��ȡ��ǰ���ع��λ�õĺ���������ꡣ
 *@retval	punX ���ع��λ�õĺ���꣨��λ�����أ���punXΪNULLʱ��ʾ����ȡ�����
 *@retval	punY ���ع��λ�õ�����꣨��λ�����أ���punYΪNULLʱ��ʾ����ȡ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGetxy(uint *punX, uint *punY);


/**
 *@brief	������ʾ����ߴ硣
 *@details  δ�����������ʾ����Ϊʵ����Ļ�ߴ磬ͨ��ýӿ�������ʾ���������API����ʾ����ֻ�ڸ���������Ч��\n
			������(10,10,100,100)ΪӦ����ʾ������Ӧ�ó�����ʹ�õ��������(0,0)ʵ����
			����Ļ�������(10,10),��������Ҳֻ����������(10,10,100,100)��Χ�ڵ���ʾ��ݡ�
 *@param	unX		Ӧ�ó�����ʾ������ʼ���ĺ���꣨��λ�����أ���
 *@param	unY		Ӧ�ó�����ʾ������ʼ���ĺ���꣨��λ�����أ���
 *@param	unWidth	Ӧ�ó�����ʾ�����ȣ���λ�����أ���
 *@param	unHeight 	Ӧ�ó�����ʾ����߶ȣ���λ�����أ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrSetViewPort(uint unX,uint unY,uint unWidth, uint unHeight);

/**
 *@brief	��ȡ��ǰ��ʾ����ߴ硣
 *@retval	punX		��ʾ������ʼ���ĺ���꣨��λ�����أ���
 *@retval	punY		��ʾ������ʼ��������꣨��λ�����أ���
 *@retval	punWidth	��ʾ����߶ȣ���λ�����أ���
 *@retval	punHeight	��ʾ����߶ȣ���λ�����أ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrGetViewPort(uint *punX,uint *punY,uint *punWidth,uint *punHeight);


/**
 *@brief	����ʾ������ʾBitmapͼƬ��
 *@details  bitmap��ʽ��1byte��Ӧ8�����ص�,0��ʾ�׵㣬1��ʾ�ڵ㣬��ʾ��ݺ������У�����ͼ��ʾ:\n
-----------------D7~~D0--------------D7~~D0------------------\n
Byte 1: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte2	\n
Byte 3: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte4	\n
Byte 5: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte6	\n
Byte 7: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte8	\n
Byte 9: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte10	\n
Byte11: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte12	\n
Byte13: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte14	\n
Byte15: �� �� �� �� �� �� �� ��  ��  �� �� �� �� �� �� �� Byte16	\n
---------------------------------------------------------------\n
	�����ʾͼƬ��Χ������Ļ��Χ��ͨ��\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"���õ��û�ʹ������ʱ����ú��������Ч�����ص���ʧ�ܡ�
 *@param	unX 		ͼƬ����ʾ��������ϽǺ���꣨��λ�����أ�
 *@param	unY 		ͼƬ����ʾ��������Ͻ�����꣨��λ�����أ�
 *@param	unWidth 	ͼƬ��ȣ���λ�����أ�
 *@param	unHeight 	ͼƬ�߶ȣ���λ�����أ�
 *@param	psBuf 		BitmapͼƬ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrDrawBitmap(uint unX,uint unY,uint unWidth, uint unHeight, const char *psBuf);
int NDK_ScrDrawBitmapV(uint unX,uint unY,uint unWidth,uint unHeight, const uchar *psBuf);

/**
 *@brief	����ʾ�������������(unStartX,unStartY)��(unEndX,unEndY)��ֱ�ߣ�unColor��ʾ���ߵ�RGBɫ��ֵ��
 *@param	unStartX 	ֱ�ߵ�������꣨��λ�����أ�
 *@param	unStartY 	ֱ�ߵ��������꣨��λ�����أ�
 *@param	unEndX 		ֱ�ߵ��յ����꣨��λ�����أ�
 *@param	unEndY 		ֱ�ߵ��յ�����꣨��λ�����أ�
 *@param	unColor 		��ɫ��ֵ <0-0xFFFF>
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrLine(uint unStartX, uint unStartY, uint unEndX, uint unEndY, color_t unColor);


/**
 *@brief	����ʾ����һ�����Ρ�
 *@details	�����α߽糬����Ļ��Χ��ͨ��\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"���õ��û�ʹ������ʱ����ú��������Ч�����ص���ʧ�ܡ�
 *@param	unX 		���ε�������꣨��λ�����أ�
 *@param	unY 		���ε��������꣨��λ�����أ�
 *@param	unWidth 		���εĿ?��λ�����أ�
 *@param	unHeight 		���εĸߣ���λ�����أ�
 *@param	emFillPattern 	0Ϊ���ģʽ��1Ϊ���ģʽ
 *@param	unColor	��ɫ��ֵ <0-0xFFFF>
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrRectangle(uint unX, uint unY, uint unWidth, uint unHeight, EM_RECT_PATTERNS emFillPattern, color_t unColor);


/**
 *@brief	����ʾ������ʾָ����ͼƬ��ͼƬ�ļ�֧�ָ�ʽ��鿴�����͵�ͼƬ��ʽ���ơ�
 *@details	�����ʾͼƬ��Χ������Ļ��Χ��ͨ��\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"���õ��û�ʹ������ʱ����ú��������Ч�����ص���ʧ�ܡ�
 *@param	unX 		ͼƬ��ʾ�����ϽǺ���꣨��λ�����أ�
 *@param	unY 		ͼƬ��ʾ�����Ͻ�����꣨��λ�����أ�
 *@param	unWidth 	ͼƬ�Ŀ?��λ�����أ�
 *@param	unHeight 	ͼƬ��ʾ�ĸߣ���λ�����أ�
 *@param	pszPic 	ͼƬ�ļ����ڵ�·����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE" 		ͼ�����ʧ��
*/
int NDK_ScrDispPic(uint unX,uint unY,uint unWidth, uint unHeight, const char *pszPic);

/**
 *@brief	ȡ��ʾ������ָ�������������ɫ��ֵ��
 *@param	unX 		����꣨��λ�����أ�
 *@param	unY 		����꣨��λ�����أ�
 *@retval	punColor	���ص���ɫֵ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGetPixel(uint unX, uint unY, color_t *punColor);


/**
 *@brief	����ʾ������ָ��������껭�㡣
 *@param	unX 		����꣨��λ�����أ�
 *@param	unY		����꣨��λ�����أ�
 *@param	unColor 	��ɫ��ֵ <0-0xFFFF>
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrPutPixel(uint unX, uint unY, color_t unColor);


/**
 *@brief	���Դ����û����õ���ʾ�����ڵ����ˢ�µ�Һ��������ʾ��
 *@details	ϵͳȱʡΪ�Զ�ˢ�¡�Ϊ��������������ͨ��\ref NDK_ScrAutoUpdate "NDK_ScrAutoUpdate()"����Ϊ���Զ�ˢ�£���NDK_ScrRefresh
			���ú�ϵͳ�Ž��Դ��е����ˢ�µ�Һ�����ϡ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrRefresh(void);

/**
 *@brief	���Դ���ȫ�����ˢ�µ�LCDȫ������ʾ��
 *@details	�ýӿ���\ref NDK_ScrRefresh "NDK_ScrRefresh()"������ڲ���������ʾ����Ĵ�С����ͨ��\ref NDK_ScrSetViewPort "NDK_ScrSetViewPort()"���õ���ʾ����Ϊȫ��ʱ
			����\ref NDK_ScrRefresh "NDK_ScrRefresh()"��ýӿ�Ч��һ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrFullRefresh(void);

/**
 *@brief	�����Ƿ��Զ�ˢ�¡�
 *@param	nNewAuto
					��0:�Զ�ˢ��
					0:���Զ�ˢ�£�ֻ�е���\ref NDK_ScrRefresh "NDK_ScrRefresh()"����ʾ�Դ��е���ݡ�
 *@retval	pnOldAuto	��������֮ǰ���Զ�ˢ��״̬��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrAutoUpdate(int nNewAuto, int *pnOldAuto);


/**
 *@brief	��ȡҺ�����ߴ硣
 *@retval		punWidth	����LCD��ȣ���λ�����أ���֧��punWidthΪNULL
 *@retval		punHeight	����LCD�߶ȣ���λ�����أ���֧��punHeightΪNULL
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGetLcdSize(uint *punWidth,uint *punHeight);

/**
 *@brief	��ȡҺ����ɫ�
 *@details	�������ж�Һ�����ǵ�ɫ�������
 *@retval		punColorDepth	����Һ����ɫ�1----�ڰ���ɫ,
										16----16λɫ������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGetColorDepth(uint *punColorDepth);

/**
 *@brief	���ر��������
 *@param	emBL	BACKLIGHT_OFF �C �ر�Һ������
 					BACKLIGHT_ON �C��Һ������
 					BACKLIGHT_LOCKON �CҺ�����ⳣ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrBackLight(EM_BACKLIGHT emBL);

/**
 *@brief		������Ļ�Աȶȣ�ֻ�Ժڰ�����Ч��
 *@param		unContrast	�Աȶȼ���Ϊ0~63,0�,63����,Ĭ��Ϊ32��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���
*/
int NDK_ScrSetContrast(uint unContrast);


/**
 *@brief	����ʹ�õ���ʾ���塣
 *@details  ϵͳĬ��֧��2�ֳߴ�����С������ɹ��л�,��ͬ�����趨��ϵͳĬ������ߴ粻һ����ͬ\n\n
			����֧��:\n����16x16 ASCII:8x16 (DISPFONT_CUSTOM)\n
					 ����24x24 ASCII:12x24 (DISPFONT_EXTRA)\n
			�ڰ���֧��:\n����12x12 ASCII:6x12 (DISPFONT_CUSTOM)\n
						����16x16 ASCII:8x16 (DISPFONT_EXTRA)\n\n
			������ʾ����Ժ�����ʾ���������ã���֮ǰ��ˢ����ʾ��������Ч
 *@param	emType	ѡ������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(�����û��Զ�������ʧ��)
*/
int NDK_ScrSetFontType(EM_DISPFONT emType);


/**
 *@brief	��ȡ��ǰϵͳʹ�õĺ�����ʾ�����͸ߡ�
 *@details  ϵͳʹ�õ�ASCII�ַ���������ȹ̶�Ϊ���ֵ�һ��
 *@retval	punWidth	���ص�ǰϵͳ��ǰ��ʾ����ĺ��ֵ����,֧��ΪNULL
 *@retval	punHeight	���ص�ǰϵͳ��ǰ��ʾ����ĺ��ֵ����,֧��ΪNULL
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrGetFontSize(uint *punWidth,uint *punHeight);


/**
 *@brief		����û��Զ������塣
 *@details		���óɹ���ͨ�� \ref NDK_ScrSetFontType "NDK_ScrSetFontType" ��������\ref DISPFONT_USER��ʹ���Զ��������\n
				ע��:Ϊ��ϵͳͳһ����ʱ��ʾ�Ű棬Ӣ���ֿ�����Ϊ�����ֿ���һ�룬�߶��뺺���ֿ���ͬ��
 *@param		psCnPath	�����ֿ⡣
 *@param		psEnPath	Ӣ���ֿ⡣
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrFontAdd(const char *psCnPath,const char *psEnPath);


/**
 *@brief	�����м����ּ�ࡣ
 *@details  unWspace���ڵ�ǰʹ�õ�Ӣ���������ؿ�ȵ�2��ʱ���ּ����ΪӢ���������ؿ�ȵ�2��
			unHspace���ڵ�ǰʹ�õ�Ӣ���������ظ߶�ʱ���м����ΪӢ���������ظ߶�
 *@param	unWspace	�ּ�ࣨ��λ�����أ�
 *@param	unHspace		�м�ࣨ��λ�����أ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_ScrSetSpace(uint unWspace,uint unHspace);


/**
 *@brief	����������ɫ�������ԡ����ԡ����Ա���ɫ��
 *@param	unColor	��ɫ��ֵ
 *@param	emType	ѡ�����ö���

 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrSetFontColor(color_t unColor, EM_FONTCOLOR emType);



/**
 *@brief	��ʾ�ַ�
 *@param	unX	��ʾ�ַ�λ�õĺ����
 *@param	unY	��ʾ�ַ�λ�õ������
 *@param	pszS	Ҫ��ʾ���ַ�ָ��
 *@param	unMode	������ʾASCII�ַ�ʱ�ߴ�\n
					1��ʹ��С��Ӣ�����壬�������ڰ��ֵ��ַ�\n
						�ڰ�����8x8�ߴ�Ӣ������\n
						������8x16�ߴ�Ӣ������\n
					0��ϵͳ��ǰʹ�õ���ʾ������ASCII����ߴ�\n
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrDispString(uint unX,uint unY,const char *pszS,uint unMode);

/**
 *@brief	��Ļ��ʾ��ʽ�������ʹ�÷���ͬprintf
 *@param	psFormat	��������ĸ�ʽ
  *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrPrintf(const char *psFormat, ...);

/**
 *@brief	������Ļ�ײ���ʾ״̬��
 *@details	δ���øýӿ�ʱ��ϵͳĬ���ǹر�״̬����ʾ��ͨ��ýӿڹر�״̬��ʱ��ԭ״̬����ʾλ�õ���ݲ���ָ���������ˢ����ʾ���ǰ���ùرջ��״̬��
 *@param	unFlag	0 �ر�״̬����ʾ\n
					1 ��ȫ��ʾ״̬�������������źš�ʱ�䡢����\n
					2 ֻ��ʾʱ��\n
					4 ֻ��ʾ����\n
					8 ֻ��ʾ�����ź�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
*/
int NDK_ScrStatusbar(uint unFlag);

/**
 *@brief	����BDF����
 *@details	�û��Զ���BDF��ʽ�����壬֧�ּ��ض��BDF�����ļ�
 *@param	unFontId �Զ�������ID,��0,1,2�ȡ����unFontId��֮ǰ���õ�BDF������ͬ�����滻֮ǰBDF����
 *@param    pszFile BDF�ļ�·��+�ļ���
 *@param    punWidth ��ȡ��BDF����������ؿ�,֧��ֵΪNULL
 *@param    punHeight ��ȡ��BDF����������ظ�,֧��ֵΪNULL
 *@return
 *@li	NDK_OK	�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		BDF�����ļ���ʽ�޷�ʶ��
*/
int NDK_ScrLoadBDF(uint unFontId,char *pszFile,uint *punWidth,uint *punHeight);


/**
 *@brief	ʹ���Ѽ��ص�BDF������ʾ���
 *@details
 *@param	unFontId \ref NDK_ScrLoadBDF "NDK_ScrLoadBDF()"�����������ӦID��
 *@param	unX	��ʾ�ַ�λ�õĺ����
 *@param	unY	��ʾ�ַ�λ�õ������
 *@param    pusText ��ʾ���ַ���ֵ��BDF�ļ����ַ����Ӧ�ı���ֵ�������UNICODE��������BDF�ļ���
 *@return
 *@li	NDK_OK	�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		BDF�����ļ���ʽ�޷�ʶ��
*/
int NDK_ScrDispBDFText(uint unFontId,uint unX,uint unY,ushort *pusText);

/** @} */ // ��ʾģ�����

/** @addtogroup ����
* @{
*/
/**
 *@brief ���ܼ�ֵ����
*/
#define  K_F1		0x01
#define  K_F2		0x02
#define  K_F3		0x03
#define  K_F4		0x04
#define  K_F5		0x05
#define  K_F6		0x06
#define	 K_F7		0x07
#define	 K_F8		0x08
#define	 K_F9		0x09
#define  K_BASP		0x0a    /**<�˸��*/
#define  K_ENTER	0x0D	/**<ȷ�ϼ�*/
#define  K_ESC		0x1B	/**<ȡ���*/
#define	 K_ZMK		0x1C	/**<��ĸ��*/
#define	 K_DOT		0x2E	/**<С���*/

#define	 K_QUIT		0x9B	/**<�˳�����*/
#define	 K_CLEAR	0x9C	/**<�������*/

/**
 *@brief ���ּ�ֵ����
*/
#define K_ZERO		0x30    /**<����0��*/
#define K_ONE		0x31    /**<����1��*/
#define K_TWO		0x32	/**<����2��*/
#define K_THREE		0x33	/**<����3��*/
#define K_FOUR		0x34	/**<����4��*/
#define K_FIVE		0x35	/**<����5��*/
#define K_SIX		0x36	/**<����6��*/
#define K_SEVEN		0x37	/**<����7��*/
#define K_EIGHT		0x38	/**<����8��*/
#define K_NINE		0x39	/**<����9��*/

/**
 *@brief �������
*/
typedef enum {
	INPUT_CONTRL_NOLIMIT, 		/**<����������ASCII���ַ������ֱ�ӷ���*/
	INPUT_CONTRL_LIMIT,		/**<ֻ��������������С��㣬�����ֱ�ӷ���*/
	INPUT_CONTRL_NOLIMIT_ERETURN,		/**<����������ASCII���ַ������ȴ�ȷ�ϼ�*/
	INPUT_CONTRL_LIMIT_ERETURN,			/**<ֻ��������������С��㣬�����ȴ�ȷ�ϼ��*/
}EM_INPUT_CONTRL;

/**
 *@brief �����ַ���ʾ����
*/
typedef enum {
	INPUTDISP_NORMAL, 		/**<������������ʾ�ַ�*/
	INPUTDISP_PASSWD,			/**<����������ʾΪ'*' */
	INPUTDISP_OTHER,			/**<֧�ִ�Ԥ��ֵ�Ľ������뻺��*/
}EM_INPUTDISP;

/**
 *@brief ���뷨����
*/
typedef enum {
    IME_NUMPY,	  /**<��ƴ*/
    IME_ENGLISH,  /**<Ӣ��*/
	IME_NUM,	  /**<����*/
	IME_BIHUA,	  /**<�ʻ�*/
    IME_QUWEI,	  /**<��λ*/
    IME_GBK,      /**<����*/
	IME_HANDWRITE, /**<��д*/
    IME_MAXNUM=IME_HANDWRITE,	/**<�����ڴ����ж�*/
}EM_IME;
/**
 *@brief �������״̬
*/
typedef enum{
        PADSTATE_UP,             /**<�������㵯��*/
        PADSTATE_DOWN,       /**<�������㰴��*/
        PADSTATE_KEY,           /**<�а�����*/
}EM_PADSTATE;
/**
 *@brief ������Ϣ
*/
typedef struct __st_PADDATA{
        uint unPadX;    /**<�����*/
        uint unPadY;    /**<�����*/
        uint unKeycode;/**<����ֵ*/
        EM_PADSTATE emPadState;     /**<���״̬*/
}ST_PADDATA;
/**
 *@brief	�����̻�����
 *@return
 *@li		NDK_OK			   �����ɹ�
*/
int NDK_KbFlush(void);

/**
 *@brief	���س��������ϼ��ܣ���δ֧�֣���
 *@param	nSelect 	0   �ر� 1   ����
 *@param	nMode 		0   ������  1   ��ϼ�
 *@param	pnState 	��ȡ���������ϼ�ԭ����״̬��0--�ر� 1---������
 *@return
 *@li		NDK_OK				  �����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"    ����ʧ��
*/
int NDK_KbSwitch(int nSelect, int nMode,int *pnState);

/**
 *@brief 	��ȡ���������ϼ�Ŀ���״̬����δ֧�֣���
 *@param	nMode 	0   ������  1   ��ϼ�
 *@param	pnState 	��ȡ���������ϼ�״̬��0--�ر� 1---������
 *@return
 *@li		NDK_OK 				   �����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"	   ����ʧ��
*/
int NDK_KbGetSwitch(int nMode,int *pnState);

/**
 *@brief 	��ʱʱ���ڶ�ȡ���̰���ֵ
 *@details	�ڹ涨��ʱ������������������:����һ����ȴ�ſ������ؼ��롣
 *@param	unTime	С�ڵ���0 :�޳�ʱ��һֱ�ȴ������
							����:Ϊ�ȴ�ʱ��(����Ϊ��λ)
 *@param	pnCode	��ȡ������룬���ڹ涨��ʱ����û�а����£�pnCode��ֵΪ0
 *@return
 *@li       NDK_OK 				   �����ɹ�
 *@li   	\ref NDK_ERR "NDK_ERR" 	                ����ʧ��
*/
int NDK_KbGetCode(uint unTime, int *pnCode);

/**
 *@brief    ��K21�ӹܴ��������������ȡ�����ĺ�����꣬��Ҫ������Ļ��������Ļ�����ʹ�á�
 *@brief    �˽ӿ���NDK_TSKbd_Ctrl���ʹ��
 *@details  �ڹ涨��ʱ�����������꣬����������:����һ����ȴ�ſ������ؼ��롣
 *@param    unTime  С�ڵ���0 :�޳�ʱ��һֱ�ȴ������
                            ����:Ϊ�ȴ�ʱ��(����Ϊ��λ)
 *@param    x  ��ȡ�������꣬���ڹ涨��ʱ����û�а����£�x��ֵΪ0
 *@param    y  ��ȡ��������꣬���ڹ涨��ʱ����û�а����£�y��ֵΪ0
 *@return
 *@li       NDK_OK                  �����ɹ�
 *@li       \ref NDK_ERR "NDK_ERR"    ����ʧ��
 *@li       \ref NDK_ERR "NDK_ERR"    K21δ�ӹܴ�����
*/
int NDK_TSKbdGetXY(uint unTime, uint *x, uint *y);

/**
 *@brief    ��K21�ӹܻ��ͷŴ�����
 *@param    ctrl  ��0��K21�ӹܴ�����  0��K21�ͷŴ������Ŀ���
 *@return
 *@li       NDK_OK                 �����ɹ�
 *@li       \ref NDK_ERR "NDK_ERR"                  ����ʧ��
*/
int NDK_TSKbd_Ctrl(uint ctrl);

/**
 *@brief	��ȡ�������е��׸����̼�ֵ����������
 *@details	��鰴������Ƿ��а������ж���ؼ���,��û�м�����������0��
   			һ���API����һ������ѭ����ʹ�ã�����ʹ��֮ǰӦ��\ref NDK_KbFlush "NDK_KbFlush"�ѻ��������
 			��\ref NDK_KbGetCode "NDK_KbGetCode"����ڱ�������еȴ�����������ء�
 *@param	pnCode	��ȡ������룬�ް�����ʱpnCode��ֵΪ0
 *@return
 *@li        	NDK_OK 				   �����ɹ�
 *@li   		\ref NDK_ERR_PARA "NDK_ERR_PARA" 	   ����Ƿ�
*/
int NDK_KbHit(int *pnCode);

/**
*@brief		�����ַ�
*@details	�Ӽ��̶���һ���Ի��з�Ϊ�ս����ַ�������뻺����pszBuf�С�
			ESC��ز���ʧ��,�س�������ɷ���,�����ܼ���Ч��
*@param		pszBuf	�����ַ����
*@param		unMin	��С���봮��
*@param		unMaxLen	������봮��
*@param		punLen	��ȡʵ�����봮�ĳ���(>0)
*@param		emMode	��ʾ���ͣ�
					ȡֵINPUTDISP_NORMALʱ��ʾ�ַ�
					ȡֵINPUTDISP_PASSWDʱ��ʾ'*'��
					ȡֵΪINPUTDISP_OTHERʱ��pcBuf�������ݣ���\0Ϊ��β���ַ��൱���Ѿ��Ӽ�������������,������������ʾ������
*@param		unWaitTime	�ȴ������ʱ�䣬����0һֱ�ȴ�����ֵΪ�ȴ����������ʱû�а��»س����Զ����أ�����TimeOut��
*@param		emControl	INPUT_CONTRL_NOLIMIT������ASCII���ַ������ֱ�ӷ���\n
						INPUT_CONTRL_LIMIT��ֻ��������С��㣬�����ֱ�ӷ���\n
						INPUT_CONTRL_NOLIMIT_ERETURN������ASCII���ַ������ȴ�ȷ�ϼ��\n
						INPUT_CONTRL_LIMIT_ERETURN��ֻ��������С��㣬�����ȴ�ȷ�ϼ��\n
 *@return
 *@li        	NDK_OK 			   �����ɹ�
 *@li   		\ref NDK_ERR_PARA "NDK_ERR_PARA" 	   ����Ƿ�
 *@li           \ref NDK_ERR "NDK_ERR"             ����ʧ��
*/
int NDK_KbGetInput(char *pszBuf,uint unMin,uint unMaxLen,uint *punLen,EM_INPUTDISP emMode,uint unWaitTime, EM_INPUT_CONTRL emControl);

/**
 *@brief	�������뷨
 *@details 	ͨ����ĸ��ѡ�����뷨��
			���벽�裺
			a. ��ƴ��������ֱ������ƴ���硰xin�����롰946������ѡ�񡣰����˸�������룬���������������ƶ�ƴ��ѡ��
			b. ��ȷ�ϡ����뱸ѡ��������ѡ����Ҫ�ĺ��֣������������������ơ�����˸񡱼���˻ء�a�����衣
    					����ȷ�ϡ���ѡ����Ҫ�ĺ��֡�
			c. �����뷨״̬�£���ѡ�ֵ�ʱ�򣬿���ͨ����������л���
	 		��������룺
				�����ӿ������뷨������£������ּ�0�����ֱ���ţ����������������ƶ�ѡ���ţ���ȷ�ϼ�ѡ����ţ�

 *@param	pszS 		��������ַ�pcS�������ݣ���\0Ϊ��β���ַ��൱���Ѿ��Ӽ������������ݡ�
 *@param	unMaxLen 	��������ַ����󳤶ȡ�
 *@param	emMethod		���뷨ѡ��,����emMethodȡEM_IME֮�������ֵ����ú���Ĭ�ϼ�����ƴ���뷨��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li   \ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"              �ڴ�ռ䲻��
 *@li   \ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"              ��֧�ָù���
*/
int NDK_KbHZInput(char *pszS,uint unMaxLen,EM_IME emMethod);

/**
 *@brief	��ȡ��������Ĵ���״̬
  *@details	��API���Ի�ȡ���ص�Ĵ���״̬(\ref EM_PADSTATE "EM_PADSTATE"),��APIҲ���Ի�ȡ����ֵ��\n
                                ��ʹ�ø�API��ʱ��Ҫע��:����ڵ��ø�API֮���ٵ���\ref NDK_KbHit "NDK_KbHit()"����\ref NDK_KbGetCode "NDK_KbGetCode()"
                                ��Ӱ�촥�����״̬�ķ��ء�\nͨ���API��ȡ����ֵʱ����״ֵ̬����\ref   PADSTATE_KEY "PADSTATE_KEY" ʱ��
                                ��ʾ��ʱ�а����£����ȡ�İ���ֵ��pstPaddata->unKeycode�С�
 *@param	pstPaddata	�����������Ϣ(�ο�\ref ST_PADDATA "ST_PADDATA")
 *@param	unTimeOut	��ʱʱ��(0��ʾ�������0��ʾ������ʱʱ��ֵΪunTimeOut��λΪ:����)
 *@return
 *@li        	NDK_OK 				   �����ɹ�
 *@li   	\ref NDK_ERR_PARA     "NDK_ERR_PARA"	   ����Ƿ�(pstPaddataΪNULL)
 *@li   	\ref NDK_ERR_TIMEOUT  "NDK_ERR_TIMEOUT"	  ��ʱ����
*/
int NDK_KbGetPad(ST_PADDATA *pstPaddata,uint unTimeOut);

/** @} */ // ����ģ�����


/** @addtogroup ��ӡ
* @{
*/

/**
 *@brief ���嶨��ֵ
*/
typedef enum {
	PRN_HZ_FONT_24x24 = 1,
	PRN_HZ_FONT_16x32 ,
	PRN_HZ_FONT_32x32 ,
	PRN_HZ_FONT_32x16 ,
	PRN_HZ_FONT_24x32 ,
	PRN_HZ_FONT_16x16 ,
	PRN_HZ_FONT_12x16 ,
	PRN_HZ_FONT_16x8 ,
	PRN_HZ_FONT_24x24A ,			/**<��������24x24����*/
	PRN_HZ_FONT_24x24B ,			/**<����24x24����*/
	PRN_HZ_FONT_24x24C ,			/**<����24x24����*/
	PRN_HZ_FONT_24x24USER ,
	PRN_HZ_FONT_12x12A ,			/**<����12x12����*/
	PRN_HZ_FONT_16x24 ,
	PRN_HZ_FONT_16x16BL,			/*16x16 ����*/
	PRN_HZ_FONT_24x24BL,			/*24x24 ����*/
	PRN_HZ_FONT_48x24A ,
	PRN_HZ_FONT_48x24B ,
	PRN_HZ_FONT_48x24C ,
	PRN_HZ_FONT_24x48A ,
	PRN_HZ_FONT_24x48B ,
	PRN_HZ_FONT_24x48C ,
	PRN_HZ_FONT_48x48A ,
	PRN_HZ_FONT_48x48B ,
	PRN_HZ_FONT_48x48C
}EM_PRN_HZ_FONT;

typedef enum {
	PRN_ZM_FONT_8x16 = 1,
	PRN_ZM_FONT_16x16 ,
	PRN_ZM_FONT_16x32 ,
	PRN_ZM_FONT_24x32 ,
	PRN_ZM_FONT_6x8 ,
	PRN_ZM_FONT_8x8 ,
	PRN_ZM_FONT_5x7 ,
	PRN_ZM_FONT_5x16 ,
	PRN_ZM_FONT_10x16 ,
	PRN_ZM_FONT_10x8 ,
	PRN_ZM_FONT_12x16A ,       /**<MSGothic����12x16����*/
	PRN_ZM_FONT_12x24A ,				/**<Gulimche����12x24����*/
	PRN_ZM_FONT_16x32A ,				/**<MSGothic����16x32����*/
	PRN_ZM_FONT_12x16B ,				/**<MSGothic����12x16����*/
	PRN_ZM_FONT_12x24B ,				/**<MSGothic����12x24����*/
	PRN_ZM_FONT_16x32B ,				/**<MSGothic����16x32����*/
	PRN_ZM_FONT_12x16C ,				/**<�������12x16����*/
	PRN_ZM_FONT_12x24C ,				/**<�������12x24����*/
	PRN_ZM_FONT_16x32C ,				/**<�������16x32����*/
	PRN_ZM_FONT_24x24A ,
	PRN_ZM_FONT_32x32A ,
	PRN_ZM_FONT_24x24B ,
	PRN_ZM_FONT_32x32B ,
	PRN_ZM_FONT_24x24C ,
	PRN_ZM_FONT_32x32C ,
	PRN_ZM_FONT_12x12 ,
    PRN_ZM_FONT_12x12A ,
    PRN_ZM_FONT_12x12B ,
    PRN_ZM_FONT_12x12C ,
    PRN_ZM_FONT_8x12 ,
    PRN_ZM_FONT_8x24,
    PRN_ZM_FONT_8x32,
    PRN_ZM_FONT_12x32A,
    PRN_ZM_FONT_12x32B,
    PRN_ZM_FONT_12x32C,
    PRN_ZM_FONT_8x16BL,					/*8x16 ����,ö��ֵ 36*/
    PRN_ZM_FONT_16x16BL,				/*��֧��*/
    PRN_ZM_FONT_12x24BL					/*12x24 ����,ö��ֵ 38*/
}EM_PRN_ZM_FONT;

/**
 *@brief  ��ӡ��״̬�Լ������壬ȡ��ӡ��״̬����ֵ���������������ϵĹ�ϵ
*/

typedef enum{
	PRN_STATUS_OK = 0,			/**<��ӡ����*/
	PRN_STATUS_BUSY = 8,		/**<��ӡ�����ڴ�ӡ*/
	PRN_STATUS_NOPAPER = 2,       /**<��ӡ��ȱֽ*/
	PRN_STATUS_OVERHEAT = 4,      /**<��ӡ�����*/
	PRN_STATUS_VOLERR = 112       /**<��ӡ���ѹ�쳣*/
}EM_PRN_STATUS;

/**
 *@brief  ��ӡ��ADͨ����
*/
typedef enum{
	PRN_CHANELNUM_VOLTAGE, 		/**<��ѹ����*/
	PRN_CHANELNUM_TEMPERATURE	/**<�¶Ȳ���*/
}EM_PRN_CHANELNUM;

/**
 *@brief ��ӡ������ö������ֵ
*/

typedef enum {
    PRN_TYPE_TP = 0,             /**<������ӡ��*/
    PRN_TYPE_HIP,              /**<�������*/
    PRN_TYPE_FIP,              /**<Ħ�����*/
    PRN_TYPE_END               /**<��*/
}EM_PRN_TYPE;

/**
 *@brief  ��ӡ���ӡģʽ
*/
typedef enum{
	PRN_MODE_ALL_DOUBLE = 0,			/**<����Ŵ�����Ŵ�*/
	PRN_MODE_WIDTH_DOUBLE ,			/**<����Ŵ�������*/
	PRN_MODE_HEIGHT_DOUBLE,       /**<����������Ŵ�*/
	PRN_MODE_NORMAL               /**<������������*/
}EM_PRN_MODE;


/**
 *@brief �»��߹��ܿ��ص�ö������ֵ
*/
typedef enum{
	PRN_UNDERLINE_STATUS_OPEN = 0,			/**<�»��߹��ܿ�*/
	PRN_UNDERLINE_STATUS_CLOSE			   /**<�»��߹��ܹ�*/
}EM_PRN_UNDERLINE_STATUS;

/**
 *@brief �ֿ�ע����Ϣ
*/
typedef struct
{
	int nOffset;						/**<��Ҫ��ȡ�������ֿ��е�������*/
	int nFontByte;					   /**<��Ҫ��ȡ�������ֿ���ռ�ֽ���*/
}ST_PRN_RECMSG;


/**
 *@brief �»��߹��ܿ��ص�ö������ֵ
*/
typedef struct
{
	char *pszName;	    			/**<�ֿ�����ֿ�����·��*/
	int  nNum;				    	/**<ע���,֧��0~7ע���*/
	int  nDirection;   				/**<0:�������壻 1:��������*/
	int  w;							/**<������ʾ�Ŀ��*/
	int  h;							/**<������ʾ�ĸ߶�*/
	int  (*func)(char *, ST_PRN_RECMSG *);	/**<��ȡ���Ƶ�ַ��ռ���ֽ���ĺ���*/
	int  nIsHZ;                     /**<ע����ֿ��Ǻ����ֿ⻹��ASCII�ֿ⣬1:HZ  0:ZM*/
}ST_PRN_FONTMSG;

/*
*@brief ����������ӡ���ڴ�ӡ��ҳǰ���ӡ��ɺ�Ľ���ֽ������ö������
*/
typedef enum {
    PRN_FEEDPAPER_BEFORE = 0,         /**<��ҳ��ӡǰ��ֽ*/
    PRN_FEEDPAPER_AFTER             /**<��ҳ��ӡ��ɺ��ֽ*/
}EM_PRN_FEEDPAPER;


/**
 *@brief 		  ��ӡ���ʼ��
 *@details  	���建����,���ô�ӡ����(�������塢�߾��ģʽ)��
 *@param      unPrnDirSwitch  �Ƿ������ͱߴ��ܡ�
              0--�رձ��ͱߴ���(Ĭ��)
	              �ڸ�ģʽ�£����е�NDK_PrnStr,NDK_PrnImage����ɵ���ת����������������ݴ浽��ݻ�����
	              �ڵ���NDK_PrnStart֮��ſ�ʼ���кʹ�ӡ��صĹ�����������ֽ�ʹ�ӡ��
              1--�������ͱߴ���
	              �ڸ�ģʽ�£�ֻҪ��һ����ݣ��ͻ������ӡ������\ref NDK_PrnFeedByPixel "NDK_PrnFeedByPixel()"����������ֽ���ء�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		��ӡ�豸��ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����Դ���(���ӡ������ʧ�ܡ���ӡ��������ʧ��)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
*/
int NDK_PrnInit(uint unPrnDirSwitch);
/**
 *@brief 		��ӡ�ַ�
 *@details 		�ú�����ת�����д�ӡ���ַ����󻺳����ӡ�����ڵ���Start֮��ʼ�����ӡ���ú���Ϊ�����������
 *@param		pszBuf Ϊ��\0Ϊ��β�Ĵ�,�������ݿ�ΪASC�룬���� ����"\n"��"\r"(��ʾ�����У����ڿ�����ֱ�ӽ�ֽ)��
 				��pszBuf�����к��ֺ�ASCII�Ļ�ϴ�ʱ,��ĸ�ͺ���ֻ�����һ�������йء�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����ʧ��
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		��ʼ������ʧ��(��ӡδ��ʼ��)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"   ��ӡ��״ֵ̬
*/
int NDK_PrnStr(const char *pszBuf);
/**
 *@brief 		��ȡ��ӡ��ͨ������ֵ(�ײ���δ֧��)
 *@details 	��ӡ��ͨ������ֵ��ȡ��
 *@param	emChanelNum    	ADͨ����(�ο�\ref EM_PRN_CHANELNUM "EM_PRN_CHANELNUM")
 *@retval 	punAdValue     	AD����ֵ(��ѹ��ֵ��VΪ��λ���¶���0.1CΪ��λ)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_PrnGetAd(EM_PRN_CHANELNUM emChanelNum,uint *punAdValue);
/**
 *@brief 		��ʼ������ӡ.
 *@details 	NDK_PrnStr��NDK_PrnImage����������ת���ɵ���洢���������й��������øú���ʼ�����ӡ��
			 			����NDK_PrnStart��ӡ�����Ҫ�жϷ���ֵ�Ƿ�Ϊ0������-1��˵�����ӡ����ʧ�ܣ����������ش�ӡ��״ֵ̬�������м������������
			 			\ref NDK_PrnStart "NDK_PrnStart()"��ӡ����֮�������ȴ�ش�ӡ��״̬��ֵ(�������ͱߴ�͵ȴ��ӡģʽ)��Ӧ�ÿɸ��NDK_PrnStart���ص�ֵ���жϴ�ӡ��״̬�Ƿ���
			 			(���صķǴ�ӡ��״ֵ̬����NDK_OK��������ϵͳ����ʱ��ҪӦ��ȥȡ��ӡ��״̬���ÿ����ԱȽ�С)
 *@return
 *@li	NDK_OK				��ӡ������ȡ��ӡ��״̬��
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		��ʼ������ʧ��(��ӡδ��ʼ��)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		��ӡ�豸��ʧ��
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"   ��ӡ��״ֵ̬
*/
int NDK_PrnStart(void);
/**
 *@brief 		��ӡͼ��(�ú���Ҳ��ת����ӡ���󵽵��󻺳������NDK_PrnStart��ʼ��ӡ)
 *@details  	�����������384���㡣���unXsize��unXpos���֮�ʹ�������������ƻ᷵��ʧ�ܣ�����Ǻ���Ŵ�ģʽ�Ļ����ܳ���384/2��
 *@param 		unXsize ͼ�εĿ�ȣ����أ�
 *@param 		unYsize ͼ�εĸ߶ȣ����أ�
 *@param 		unXpos  ͼ�ε����Ͻǵ���λ�ã��ұ�������unXpos+unXsize<=ndk_PR_MAXLINEWIDE����ģʽΪ384������Ŵ�ʱΪ384/2���ò���Ϊ�����겻����߾�Ӱ�죩
 *@param 		psImgBuf ͼ��������,Ϊ�������У���һ���ֽڵ�һ�е�ǰ8���㣬D7Ϊ��һ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
*/
int NDK_PrnImage(uint unXsize,uint unYsize,uint unXpos,const char *psImgBuf);
/**
 *@brief 		ȡ��ӡ��İ汾��Ϣ.
 *@retval   pszVer ���ڴ洢���ذ汾�ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		��ӡ�豸��ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		�����ʧ��(��ȡ��ӡ�汾ʧ��)
*/
int NDK_PrnGetVersion(char *pszVer);
/**
 *@brief 		���ô�ӡ����
 *@details  ����ASCII��ӡ����ͺ������塣Ӧ�ò�ɲο��ײ��Ӧ�ò�Ľӿ��ļ��е���ض��塣
 *@param 	emHZFont 	���ú��������ʽ��0���ֵ�ǰ���岻�䡣
 *@param    emZMFont	����ASCII�����ʽ��0���ֵ�ǰ���岻�䡣
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_PrnSetFont(EM_PRN_HZ_FONT emHZFont,EM_PRN_ZM_FONT emZMFont);
/**
 *@brief		��ȡ��ӡ��״ֵ̬.
 *@details		�ڴ�ӡ֮ǰ��ʹ�øú����жϴ�ӡ���Ƿ�ȱֽ��
 *@retval	    pemStatus ���ڷ��ش�ӡ��״ֵ̬
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		��ӡ�豸��ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		�����ʧ��(��ȡ��ӡ״̬ʧ��)
*/
int NDK_PrnGetStatus(EM_PRN_STATUS *pemStatus);
/**
 *@brief 	���ô�ӡģʽ.
 *@param 	emMode ��ӡģʽ(Ĭ����ʹ����ģʽ)
 *@param     unSigOrDou ��ӡ��˫��ѡ��0--���� 1--˫��(ֻ�������Ч����������)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
*/
int NDK_PrnSetMode(EM_PRN_MODE emMode,uint unSigOrDou);
/**
 *@brief		���ô�ӡ�Ҷ�
 *@details		���ô�ӡ�Ҷ�(����ʱ��)���Ա���ڲ�ͬ�Ĵ�ӡֽ���д�ӡЧ��΢��.(ֻ��������Ч���������Ч)
 *@param    unGrey �Ҷ�ֵ����Χ0~5��0Ϊ���Ч��5Ϊ��Ũ�Ĵ�ӡЧ���ӡ��Ĭ�ϵĻҶȼ���Ϊ3��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
*/
int NDK_PrnSetGreyScale(uint unGrey);
/**
 *@brief  	���ô�ӡ��߽硢�ּ�ࡢ�м�ࡣ�ڶԴ�ӡ����Ч���ú�һֱ��Ч��ֱ���´�
 *@param  	unBorder ��߾� ֵ��Ϊ��0-288(Ĭ��Ϊ0)
 *@param    unColumn �ּ�� ֵ��Ϊ��0-255(Ĭ��Ϊ0)
 *@param    unRow �м�� ֵ��Ϊ��0-255(Ĭ��Ϊ0)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		��ӡ�豸��ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		�����ʧ��(���ࡢ�ּ�ࡢ�м������ʧ�ܡ�ͼ�ζ��뷽ʽ����ʧ��)
*/
int NDK_PrnSetForm(uint unBorder,uint unColumn, uint unRow);
/**
 *@brief 	  ��������ֽ
 *@details	�ô�ӡ����ֽ������Ϊ���ص㡣(���ͱߴ�ģʽ�¸ú���Ϊֱ����ֽ����ȴ�Star��ӡģʽ�´洢�������ȴ�star��ʼ��ֽ)
 *@param    unPixel ��ֽ���ص� ����ֵ��Ϊ��0<=unPixel<=1024�������Ϊ��-792<=unPixel<=792��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		��ʼ������ʧ��(��ӡδ��ʼ������)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
*/
int NDK_PrnFeedByPixel(uint unPixel);

/**
 *@brief	��ӡ�Ƿ����»��߹���.
 *@param  emStatus 0�����»��ߣ�1�����»���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
*/
int NDK_PrnSetUnderLine(EM_PRN_UNDERLINE_STATUS emStatus);

/**
 *@brief		�Զ�������ע�ᡣ(ֻ��������Ч)
 *@param		pstMsg \ref ST_PRN_FONTMSG "ST_PRN_FONTMSG"����ָ�룬ʹ���Զ���ע��Ҫ�����Ӧ��Ϣ����д
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_PrnFontRegister(ST_PRN_FONTMSG *pstMsg);
/**
 *@brief	���ע�����ѡ���ӡ����.(ֻ��������Ч)
 *@param  unFontId ע�������id(�����ú�Ḳ��\ref NDK_PrnSetFont "NDK_PrnSetFont()"���趨������)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_PrnSetUsrFont(uint unFontId);
/**
 *@brief	��øôδ�ӡ�ĵ�������.
 *@details �ú���ֻ�����ڵȴ����star������ӡ������������ͱߴ�ģʽ���øú����ֵ�ǲ�׼ȷ�ġ��ú������ʹ���ڵ���NDK_PrnStar֮ǰȡ�������ж�����ݵ����
 *@retval  punLine ��������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				�������
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		��ʼ������ʧ��(��ӡδ��ʼ������)
*/
int NDK_PrnGetDotLine(uint *punLine);
/**
 *@brief	��ӡbmp��png��jpg��ʽ��ͼƬ
 *@details  �ú���洢��pos�ϵ�ͼƬ���н����洢�����󻺳���  ����ͼƬ���������һ����ʱ�䣬��Ҫ��ʱ����Ҫ��һ���ĵȴ�ʱ��
 *@param  pszPath ͼƬ���ڵ�·��
 *@param  unXpos  ͼ�ε����Ͻǵ���λ�ã��ұ�������xpos+xsize(�����ͼƬ�Ŀ��ֵ)<=ndk_PR_MAXLINEWIDE����ģʽΪ384������Ŵ�ʱΪ384/2��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				�������
 *@li	\ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG" 		��ʼ������ʧ��(��ӡδ��ʼ������)
 *@li	\ref NDK_ERR_DECODE_IMAGE "NDK_ERR_DECODE_IMAGE" 				ͼ�����ʧ��
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"                  �ڴ�ռ䲻��
*/
int NDK_PrnPicture(uint unXpos,const char *pszPath);

/**
 *@brief	���ô�ӡҳ����ֻ�������Ч��
 *@details   �Դ�ӡ���ӡҳ����������
 *@param  unLen	ҳ�� ֵ��Ϊ(0<=unLen <=792)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        ��֧�ָù���
*/
int NDK_PrnSetPageLen(uint unLen);
/**
 *@brief	����BDF����
 *@details  ʹ�øú������BDF���嵽�ڴ��У��Ƚϴ�������ķ�һЩʱ�䡣
 *@param  pszPath BDF���ڵ�·��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        ��֧�ָù���
 *@li	\ref NDK_ERR "NDK_ERR"        ����ʧ��(��ʼ������ͷ�ڵ㣬ʹ��calloc��֤�����0ʧ��)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"        �ڴ�ռ䲻��
*/

int NDK_PrnLoadBDFFont(const char *pszPath);
/**
 *@brief	��ӡBDF�����е�����
 *@param  	pusText Ҫ��ӡ����ݡ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        ��֧�ָù���
*/

int NDK_PrnBDFStr(ushort *pusText);

/**
 *@brief	����BDF��������
 *@param  unXpos  ��ƫ�� ֵ��Ϊ��0-288(Ĭ��Ϊ0)
 *@param  unLineSpace  �м�� ֵ��Ϊ��0-255(Ĭ��Ϊ0)
 *@param  unWordSpace  �ּ�� ֵ��Ϊ��0-255(Ĭ��Ϊ0)
 *@param  unXmode  ����Ŵ���(ע�⣬�����MaxWidth*unXmode���벻�ܳ���384������ʧ��)
 *@param  unYmode  ����Ŵ���(ע�⣬�����MaxHeight*unYmode���벻�ܳ���48������ʧ��)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"        ����ʧ��(��ʼ������ͷ�ڵ㣬ʹ��calloc��֤�����0ʧ��)
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        ��֧�ָù���
*/

int NDK_PrnSetBDF(uint unXpos,uint unLineSpace,uint unWordSpace,uint unXmode,uint unYmode);
/**
 *@brief  ����������ӡ���ڴ�ӡ��ҳǰ���ӡ��ɺ�Ľ���ֽ����
 *@param        emType		�ο�\ref EM_PRN_FEEDPAPER "EM_PRN_FEEDPAPER"
 *@return
 *@li   NDK_OK                          �����ɹ�
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA"                           �������
 *@li   \ref NDK_ERR_INIT_CONFIG "NDK_ERR_INIT_CONFIG"      ��ʼ������ʧ��(��ӡδ��ʼ������)
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"      ��ӡ�豸��ʧ��
 *@li   \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"      �����ʧ��(��ӡ����ֽ��˺ֽ�������ʧ�ܡ���ֽ�����ʧ��)
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"        ��֧�ָù���
*/
int NDK_PrnFeedPaper(EM_PRN_FEEDPAPER emType);

/** @} */ // ��ӡģ�����


/** @addtogroup �ļ�ϵͳ
* @{
*/

/**
 *@brief 		���ļ�.
 *@details
 *@param    pszName �ļ���
 *@param    pszMode ��ģʽ "r"(��ֻ����ʽ�򿪣���������ʧ��) or "w"(��д�ķ�ʽ�򿪣�����ļ��������򴴽�)��
 *@return
 *@li	 fd				�����ɹ������ļ�������
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(�ļ���ΪNULL��ģʽ����ȷ)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		�ļ���ʧ��
*/
int NDK_FsOpen(const char *pszName,const char *pszMode);


/**
 *@brief 		�ر��ļ�.
 *@details
 *@param    nHandle �ļ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(��رյ��ļ����ǵ���NDK_FsOpen�򿪵ġ�����close()�ر�ʧ��)
*/
int NDK_FsClose(int nHandle);

/**
 *@brief 		�Ӵ򿪵�nHandle�ļ���ǰָ���unLength���ַ�����psBuffer.
 *@details
 *@param    nHandle �ļ����
 *@param    unLength	��Ҫ��ȡ���ַ�ĳ���
 *@retval   psBuffer	��Ҫ����Ļ�����ע��Ҫ�㹻����unLength�ֽ�
 *@return
 *@li	����ʵ�ʶ�����ݳ���
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				�������(psBufferΪNULL)
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 		���ļ�ʧ��(������ļ����ǵ���NDK_FsOpen�򿪵ġ�����read()ʧ�ܷ���)
*/
int NDK_FsRead(int nHandle, char *psBuffer, uint unLength );

/**
 *@brief 		��򿪵�nHandle�ļ�д��unLength���ֽ�.
 *@details
 *@param    nHandle �ļ����
 *@param    psBuffer	��Ҫд���ļ����ݵĻ�����
 *@param    unLength	��Ҫд��ĳ���
 *@return
 *@li	����ʵ��д����ݳ���
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 				�������(psBufferΪNULL)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		д�ļ�ʧ��(��д���ļ����ǵ���NDK_FsOpen�򿪵ġ�����write()ʧ�ܷ���)
*/
int NDK_FsWrite(int nHandle, const char *psBuffer, uint unLength );

/**
 *@brief 		�ƶ��ļ�ָ�뵽��unPosition���ulDistance��λ��
 *@details
 *@param    nHandle �ļ����
 *@param    ulDistance	��ݲ���unPosition���ƶ���дλ�õ�λ����
 *@param    unPosition	��Ҫ��ȡ���ַ�ĳ���
 						SEEK_SET ����offset��Ϊ�µĶ�дλ�á�
						SEEK_CUR ��Ŀǰ�Ķ�дλ���������offset��λ������
						SEEK_END ����дλ��ָ���ļ�β��������offset��λ������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(���ƶ����ļ����ǵ���NDK_FsOpen�򿪵ġ�����lseek()ʧ�ܷ���)
*/
int NDK_FsSeek(int nHandle, ulong ulDistance, uint unPosition );

/**
 *@brief 		ɾ��ָ���ļ�
 *@details
 *@param    pszName Ҫɾ����ļ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pszNameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(����remove()ʧ�ܷ���)
*/
int NDK_FsDel(const char *pszName);

/**
 *@brief 		�ļ�����
 *@details
 *@param    pszName �ļ���
 *@retval   punSize �ļ���С����ֵ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pszName��punSizeΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(���ļ�ʧ�ܡ�����fstat()ʧ�ܷ���)
*/
int NDK_FsFileSize(const char *pszName,uint *punSize);

/**
 *@brief 		�ļ�������
 *@details
 *@param    pszSrcName ԭ�ļ���
 *@param    pszDstName Ŀ���ļ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pszsSrcname��pszDstnameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(����rename()ʧ�ܷ���)
*/
int NDK_FsRename(const char *pszSrcName, const char *pszDstName );

/**
 *@brief 		�����ļ��Ƿ����
 *@details
 *@param    pszName �ļ���
 *@return
 *@li	NDK_OK				�����ɹ�(�ļ�����)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pszNameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(����access()ʧ�ܷ���)
*/

int NDK_FsExist(const char *pszName);

/**
 *@brief 		�ļ��ض�
 *@details   NDK_FsTruncate()�Ὣ����pszPath ָ�����ļ���С��Ϊ����unLen ָ���Ĵ�С�����ԭ�����ļ���С�Ȳ���unLen���򳬹�Ĳ��ֻᱻɾȥ��
 		   			���ԭ���ļ��Ĵ�С��unLenС�Ļ�������Ĳ��ֽ�����0xff
 *@param    pszPath �ļ�·��
 *@param    unLen ��Ҫ�ض̳���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pszPathΪNULL)
 *@li	\ref NDK_ERR_PATH "NDK_ERR_PATH" 		�ļ�·���Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(�����ļ���Сʧ�ܡ�����lseek()ʧ�ܷ��ء�����truncate()ʧ�ܷ���)
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		д�ļ�ʧ��(����write()ʧ�ܷ���)
*/
int NDK_FsTruncate(const char *pszPath ,uint unLen );

/**
 *@brief 	  	��ȡ�ļ���λ��
 *@details   ����ȡ���ļ���Ŀǰ�Ķ�дλ��
 *@param    nHandle �ļ����
 *@retval    pulRet �ļ���λ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pulRetΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(�������ļ����ǵ���NDK_FsOpen�򿪵��ļ�������lseek()ʧ�ܷ���)
*/
int NDK_FsTell(int nHandle,ulong *pulRet);

/**
 *@brief 	  	ȡ�ļ�ϵͳ���̿ռ��ʹ�����
 *@details
 *@param    	unWhich :0--�Ѿ�ʹ�õĴ��̿ռ�1--ʣ��Ĵ��̿ռ�
 *@retval     pulSpace ���ش��̿ռ�ʹ��������ʣ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pulSpaceΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(����statfs()ʧ�ܷ���)
*/
int NDK_FsGetDiskSpace(uint unWhich,ulong *pulSpace);
/**
 *@brief 		����Ŀ¼.
 *@details
 *@param    pszName Ŀ¼���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszNameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(����mkdir����ʧ�ܷ���)
*/

int NDK_FsCreateDirectory(const char *pszName);


/**
 *@brief 		ɾ��Ŀ¼.
 *@details
 *@param    pszName Ŀ¼���
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszNameΪNULL)
 *@li	 \ref NDK_ERR "NDK_ERR" 		����ʧ��(����remove()ʧ�ܷ���)
*/

int NDK_FsRemoveDirectory(const char *pszName);
/**
 *@brief 	�ļ�ϵͳ��ʽ��(��Ϊ֧��)
 *@details	�ù��ܽ����ڴ�ͳpos��gpƽ̨posֱ�ӷ���-1
 *@return
 *@li	 NDK_OK				�����ɹ�����
 *@li	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT" 		δ֧�ָù���
*/

int NDK_FsFormat(void);


/**
 *@brief 		�г��ƶ�Ŀ¼�µ������ļ�
 *@details	�����psBuf�ĵ�sizeһ��Ҫ���󣬷�������������psBuf ÿ20���ֽڴ洢һ���ļ���
 						ǰ19 Ϊ�ļ������Զ��ض̡���20�ֽ����Ϊ1���ʾ���ļ�Ϊ�ļ��У�0Ϊ��ͨ�ļ�
 *@param       psPath ָ��Ҫ��ȡ��Ŀ¼
 *@retval      psBuf ���ļ���洢��psBuf�ܷ���
 *@retval      punNum ���ظ��ļ���Ŀ¼���ļ�����
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pPath��psBuf��punNumΪNULL)
 *@li	 \ref NDK_ERR_PATH "NDK_ERR_PATH" 		�ļ�·���Ƿ�(����opendir()ʧ�ܷ���)
*/
int NDK_FsDir(const char *psPath,char *psBuf,uint *punNum);

/** @} */ // �ļ�ϵͳģ�����


/** @addtogroup Ӧ�ù���
* @{
*/

#define NDK_APP_NAME_MAX_LEN		32	/**< Ӧ�������󳤶�*/
#define NDK_APP_VER_MAX_LEN			16	/**< �汾��󳤶�*/
#define NDK_APP_BUILD_TIME_LEN		32	/**< ����ʱ����󳤶�*/

/**
 *@brief Ӧ����Ϣ,�ýṹ��NLD�ṹ��ʹ�ã��漰�������⡣�ṹ���岻�����޸ģ����������ع��ߴ�����Ϣ��һ��
*/
typedef struct APPINFO{
    uchar 	szAppName[NDK_APP_NAME_MAX_LEN + 1];		/**<�û��������*/
    uchar	szVerBuf[NDK_APP_VER_MAX_LEN + 1];			/**<�û�����汾��Ϣ*/
    int		nSeriNo;									/**<���,V2�汣��,�����岻ͬ	*/
    uchar	szBuildTime[NDK_APP_BUILD_TIME_LEN + 1];	/**<�ļ�����ʱ��*/
    int		nIsMaster;									/**<���ر�־ */
    uint	sunReverse[3];
}ST_APPINFO;

typedef int (*CallbackMock)(int, void *,int,void **,int *);

/**
 *@brief	���ûص�����ָ��
 *@param	NDK_EventMain	�����ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(NDK_EventMainΪNULL)
*/
int NDK_AppSetEventCallBack(CallbackMock NDK_EventMain);

/**
 *@brief	����Ӧ�ó���
 *@param	pszAppName	Ӧ����ơ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszAppNameΪNULL)
 *@li	\ref NDK_ERR_APP_MAX_CHILD "NDK_ERR_APP_MAX_CHILD" 	��Ӧ������������������Ŀ
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��дӦ�������ļ�ʧ��)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	Ӧ�������
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 	���ļ�ʧ��
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 	д�ļ�ʧ��
 *@li	\ref NDK_ERR_APP_CREAT_CHILD "NDK_ERR_APP_CREAT_CHILD" 	�ȴ��ӽ�̽������
*/
int NDK_AppRun(const char *pszAppName);
int NDK_LibLoad(const char * pszLibName, void ** apiStruct);
/**
 *@brief	ִ���¼�����
 *@param	pszFileName	Ӧ�����
 *@param	nModuleId		ִ���¼�ID
 *@param	pvInEventMsg	�����¼���Ϣ
 *@param	nInlen			�����¼�����
 *@param	nMaxOutLen		���������¼��ĳ���
 *@retval	pvOutEventMsg	��ȡ����Ӧ����Ϣ
 *@retval	pnOutLen		ʵ���������ݳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_APP_MAX_CHILD "NDK_ERR_APP_MAX_CHILD" 	��Ӧ������������������Ŀ
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��дӦ�������ļ�ʧ��)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	Ӧ�������
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 	���ļ�ʧ��
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 	д�ļ�ʧ��
 *@li	\ref NDK_ERR_APP_CREAT_CHILD "NDK_ERR_APP_CREAT_CHILD" 	�ȴ��ӽ��н������
*/
int NDK_AppDoEvent(const uchar *pszFileName,int nModuleId,const void *pvInEventMsg, int nInlen, void *pvOutEventMsg, int nMaxOutLen, int *pnOutLen);

/**
 *@brief	��ȡ��װ��Ӧ�ó����Ƿ���Ҫ�����ı�־λ
 *@param	pnRebootFlag		�������1��ʾ��װ��Ӧ�ó�����Ҫ����������Ч��0����Ҫ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnRebootFlagΪNULL)
*/
int NDK_AppGetReboot(int *pnRebootFlag);
/**
 *@brief	װ��Ӧ��
 *@param	pszFileName		Ӧ�����
 *@param	nRebootFlag		��װӦ�ó���ɹ��󣬺�̨�����Ƿ���Ҫ������1-��Ҫ������0-����Ҫ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszFilenameΪNULL)
 *@li	\ref NDK_ERR_APP_FILE_STAT "NDK_ERR_APP_FILE_STAT" 		��ȡ�ļ���Ϣ����
 *@li	\ref NDK_ERR_APP_FILE_OPEN "NDK_ERR_APP_FILE_OPEN" 		�ļ��򿪴���
 *@li	\ref NDK_ERR_APP_FILE_READ "NDK_ERR_APP_FILE_READ" 		���ļ�����
 *@li	\ref NDK_ERR_APP_FILE_WRITE "NDK_ERR_APP_FILE_WRITE" 		д�ļ�����
 *@li	\ref NDK_ERR_APP_MALLOC "NDK_ERR_APP_MALLOC" 		��̬�ڴ�������
 *@li	\ref NDK_ERR_APP_NLD_HEAD_LEN "NDK_ERR_APP_NLD_HEAD_LEN" 	NLD�ļ���ȡͷ��Ϣ���ȴ���
 *@li	\ref NDK_ERR_APP_SIGN_CHECK "NDK_ERR_APP_SIGN_CHECK" 	ǩ�����У�����
 *@li	\ref NDK_ERR_APP_SIGN_DECRYPT "NDK_ERR_APP_SIGN_DECRYPT" 	ǩ����ݽ�ǩ����
*/
int NDK_AppLoad(const char *pszFileName, int nRebootFlag);

/**
 *@brief	��ȡӦ����Ϣ
 *@param	pszAppName	Ӧ�����, ��������ΪNULL��
 *@param	nPos		ƫ�ƣ���ƫ��λ��Ϊ��ϵͳ����Ϣ��ƫ�ƣ�������˳����ء�
 *@param	nSizeofInfo	���Ӧ����Ϣ����󳤶ȡ�
 *@retval	pstAppInfo	��ȡ����Ӧ����Ϣ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(Ӧ�������ļ�����ʧ��)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 		Ӧ�������
*/
int NDK_AppGetInfo(const uchar *pszAppName, int nPos, ST_APPINFO *pstAppInfo, int nSizeofInfo);

/**
 *@brief	ɾ��Ӧ�ó���
 *@param	pszAppName	Ӧ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszAppNameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��дӦ�������ļ�ʧ��)
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 	Ӧ�������
*/
int NDK_AppDel(const char *pszAppName);

/**
 *@brief	���к�̨�������
 *@param	pszAppName	Ӧ����ơ�
 *@param	psArgv    Ӧ�����в���
 *@param	cBlock   1 ����ֱ����̨Ӧ���˳�
                    0 ��������

 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_APP_NOT_EXIST "NDK_ERR_APP_NOT_EXIST" 		Ӧ�������
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 		���ļ�ʧ��
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 		д�ļ�ʧ��
*/
int NDK_AppEXECV(const uchar *pszAppName, char * const psArgv[],char cBlock);
/** @} */ // ��Ӧ��ģ�����

/** @addtogroup �㷨
* @{
*/

#define MAX_RSA_MODULUS_LEN		512				/**< ���ģ���� */
#define MAX_RSA_PRIME_LEN		256				/**< ���ģ����� */

/**
 *@brief �Գ���Կ�㷨
*/
typedef enum{
	ALG_TDS_MODE_ENC = 0,		/**< DES���� */
	ALG_TDS_MODE_DEC = 1,		/**< DES���� */
}EM_ALG_TDS_MODE;

/**
 *@brief RSA�㷨��Կ����
*/
typedef enum{
	RSA_KEY_LEN_512  = 512,
	RSA_KEY_LEN_1024 = 1024,
	RSA_KEY_LEN_2048 = 2048,
}EM_RSA_KEY_LEN;

/**
 *@brief RSA�㷨ָ��
*/
typedef enum{
	RSA_EXP_3 = 0x03,
	RSA_EXP_10001 = 0x10001,
}EM_RSA_EXP;

/**
 *@brief RSA��Կ
*/
typedef struct {
    ushort bits;       							/**< ģλ�� */
    uchar modulus[MAX_RSA_MODULUS_LEN+1];      	/**< ģ */
	uchar publicExponent[MAX_RSA_MODULUS_LEN+1]; 	/**< ��Կָ�� */
    uchar exponent[MAX_RSA_MODULUS_LEN+1];     	/**< ˽Կָ�� */
    uchar prime[2][MAX_RSA_PRIME_LEN+1];      	/**< pq���� */
    uchar primeExponent[2][MAX_RSA_PRIME_LEN+1]; 	/**< ������ָ���ֵ */
    uchar coefficient[MAX_RSA_PRIME_LEN+1];  	 	/**< �����������ֵ */
}ST_RSA_PRIVATE_KEY;

/**
 *@brief RSA˽Կ
*/
typedef struct {
    ushort bits;                    			/**< ģλ�� */
    uchar modulus[MAX_RSA_MODULUS_LEN+1];  		/**< ģ */
    uchar exponent[MAX_RSA_MODULUS_LEN+1];		/**< ָ�� */
}ST_RSA_PUBLIC_KEY;
/**
 *@brief SM4�㷨ģʽ
*/
typedef enum{
	ALG_SM4_ENCRYPT_ECB=0,                  /**<SM4 ECB����*/
	ALG_SM4_DECRYPT_ECB,       	           /**<SM4 ECB����*/
	ALG_SM4_ENCRYPT_CBC,                  /**<SM4 CBC����*/
	ALG_SM4_DECRYPT_CBC,                  /**<SM4 CBC����*/
	ALG_SM4_MAX,                  
}EM_ALG_SM4;

/**
 *@brief	����des
 *@param	psDataIn	������ݻ���
 *@param	psKey		��Կ����,����8,16,24
 *@param    nKeyLen     ��Կ���ȣ�ֵֻ��Ϊ8,16,24
 *@param	nMode		����ģʽ �μ�\ref EM_ALG_TDS_MODE "EM_ALG_TDS_MODE"
 *@retval	psDataOut	������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataIn/psDataOut/psKeyΪNULL����Կ����ֵ����8/16/24������ģʽ�Ƿ�)
*/
int NDK_AlgTDes(uchar *psDataIn, uchar *psDataOut, uchar *psKey, int nKeyLen, int nMode);


/**
 *@brief	����sha1
 *@param	psDataIn	�������
 *@param	nInlen		��ݳ���
 *@retval	psDataOut	�����ݣ�sha1�������Ϊ20�ֽڣ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataIn/psDataOutΪNULL��nInlen<0������ģʽ�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgSHA1(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	����sha256
 *@param	psDataIn	�������
 *@param	nInlen		��ݳ���
 *@retval	psDataOut	�����ݣ�sha256�������Ϊ  �ֽڣ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataIn/psDataOutΪNULL��nInlen<0������ģʽ�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgSHA256(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	����sha512
 *@param	psDataIn	�������
 *@param	nInlen		����ģʽ
 *@retval	psDataOut	�����ݣ�sha512�������Ϊ �ֽڣ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataIn/psDataOutΪNULL��nInlen<0������ģʽ�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgSHA512(uchar *psDataIn, int nInlen, uchar *psDataOut);

/**
 *@brief	RSA��Կ�����
 *@param	nProtoKeyBit		��Կλ��ǰ֧��512��1024��2048λ �ο�\ref EM_RSA_KEY_LEN "EM_RSA_KEY_LEN"
 *@param	nPubEType			ָ�����ͣ��ο�\ref EM_RSA_EXP "EM_RSA_EXP"
 *@retval	pstPublicKeyOut		��Կ
 *@retval	pstPrivateKeyOut	˽Կ
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(nProtoKeyBit��Կλ��Ƿ���pstPublicKeyOut\pstPrivateKeyOutΪNULL��nPubETypeָ�����ͷǷ�)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgRSAKeyPairGen( int nProtoKeyBit, int nPubEType, ST_RSA_PUBLIC_KEY *pstPublicKeyOut, ST_RSA_PRIVATE_KEY *pstPrivateKeyOut);

/**
 *@brief	RSA��Կ�Լӽ���
 *@details	�ú������RSA���ܻ��������,���ܻ����ͨ��ѡ�ò�ͬ����Կʵ�֡���(Modul,Exp)ѡ��˽����Կ,����м���;��ѡ�ù�����Կ,����н��ܡ�
 			psDataIn�ĵ�һ���ֽڱ���С��psModule�ĵ�һ���ֽڡ� �ú����ʵ�ֳ��Ȳ�����2048 bits ��RSA���㡣
 			�������ݿ��ٵĻ�������ģ����+1��
 *@param	psModule		ģ����,�ַ����ʽ����,��"31323334"
 *@param	nModuleLen	ģ�ĳ��� ֻ������ѡ��512/8,1024/8,2048/8
 *@param	psExp			���RSA�����ָ�����ָ�롣����e.����λ��ǰ,��λ�ں��˳��洢,��"10001"
 *@param	psDataIn		��ݻ���,������Ĵ�С���ģ�ĳ��ȴ�1
 *@retval	psDataOut		������,�������ݳ��ȵ���ģ�ĳ��ȡ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(nModuleLenģ�ĳ��ȷǷ���psModule\psExp\psDataIn\psDataOutΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgRSARecover(uchar *psModule, int nModuleLen, uchar *psExp, uchar *psDataIn, uchar *psDataOut);

/**
 *@brief	RSA��Կ��У��
 *@param	pstPublicKey		��Կ
 *@param	pstPrivateKey		˽Կ
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pstPublicKey\pstPrivateKeyΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_AlgRSAKeyPairVerify(ST_RSA_PUBLIC_KEY *pstPublicKey, ST_RSA_PRIVATE_KEY *pstPrivateKey);

/**
 *@brief ���SM2��Կ��
 *@retval  	eccpubKey    		��Կ (64�ֽ�)
 *@retval  	eccprikey	    	˽Կ (32�ֽ�)
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
*/
int NDK_AlgSM2KeyPairGen( unsigned char *eccpubkey, unsigned char *eccprikey );

/**
 *@brief 	SM2��Կ����
 *@param   eccpubkey      	���ܹ�Կ
 *@param   Message     		�������
 *@param   MessageLen     	��ݳ���
 *@retval   Crypto    		�������(����C1C3C2��˳���������)
 *@retval   CryptoLen    		������ݳ���(������ݳ��ȱ�������ݳ�96���ֽ�)
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(����ΪNULL�����ĳ��� >��1024 - 96���ֽ�)
*/
int NDK_AlgSM2Encrypt( unsigned char *eccpubkey, unsigned char *Message, unsigned short MessageLen, unsigned char *Crypto, unsigned short *CryptoLen );

/**
 *@brief SM2˽Կ���ܣ�Ŀǰ�汾Ӧ�Ե�����Ӧ��C1C3C2����
 *@param   eccprikey      	����˽Կ
 *@param   Crypto     		�������
 *@param   CryptoLen     		������ݳ���
 *@retval  Message    		�������
 *@retval  MessageLen    	��ݳ���
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�(����Ϊ�ա����ĳ��� > 1024�ֽ�)
*/
int NDK_AlgSM2Decrypt( unsigned char *eccprikey, unsigned char *Crypto, unsigned short CryptoLen, unsigned char *Message, unsigned short *MessageLen );

/**
 *@brief    SM2ǩ��
 *@details  ��ժҪ��ɹ��ܣ���Ҫֱ�����������ϵ�e: (r,s)=sign(e,key)
 *@param   eccprikey      	ǩ��˽Կ
 *@param   e     			��ǩ����ݵ�ժҪֵ��32�ֽڣ�
 *@retval  output    		ǩ�����ݣ�64�ֽڣ�
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM2Sign(unsigned char *eccprikey, unsigned char *e, unsigned char *output );

/**
 *@brief    SM2��ǩ����
 *@param   pPublicKey      	��֤��Կ
 *@param   e     			��ǩ����ݵ�ժҪֵ��32�ֽڣ�
 *@param   pSignedData    	ǩ�����ݣ�64�ֽڣ�
 *@return
 *@li	NDK_OK ��ǩ�ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		��ǩʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/  
int NDK_AlgSM2Verify( unsigned char *pPublicKey, unsigned char *e, unsigned char *pSignedData );

/**
 *@brief    SM2ǩ��ժҪ���
 *@details  �������ID,Message�͹�Կ�����������ǩ���ժҪ���e
 *@param  usID    ID����
 *@param	pID		ID�������(*������ΪNULLʱ,ʹ��PBOC3.0Ĭ��ID-"1234567812345678"������)
 *@param  usM    	Message����
 *@param	pM		Message�������
 *@param	pubKey	��Կ�������
 *@retval pHashData: ���������������ǩ���32�ֽ�ժҪe
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM2GenE( unsigned short usID, unsigned char *pID, unsigned short usM, unsigned char *pM, unsigned char *pubKey, unsigned char *pHashData);

/**
 *@brief SM3�����ʼ��
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
*/
int NDK_AlgSM3Start(void);

/**
 *@brief updateһ��������ݣ����Ϊ64�ֽ�����
 *@param	pDat һ���������
 *@param	len  ������ݳ���(64�ֽ�����)
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM3Update( unsigned char *pDat,  unsigned int len );

/**
 *@brief �������һ����ݣ����ժҪ
 *@param	pDat	���һ���������
 *@param	len		���һ����ݳ���
 *@retval		pHashDat ���ժҪ��ݣ�32�ֽ�
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM3Final( unsigned char *pDat, unsigned int len, unsigned char *pHashDat );

/**
 *@brief SM3����
 *@details 	����������ݵ�ժҪ�������ڲ������䣬���ժҪ
 *@param	pDat	�������
 *@param	len		������ݳ���
 *@retval		pHashDat ���ժҪ��ݣ�32�ֽ�
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM3Compute( unsigned char *pDat, unsigned int len, unsigned char *pHashDat );

/**
 *@brief SM4����
 *@details ����������Կ��ģʽ���������ݣ�16�ֽ��������SM4����
 *@param	pKey	������Կ������Ϊ16�ֽ�
 *@param	pIVector	��ʼ����������Ϊ16�ֽڣ�ECBģʽ����Ϊ�գ�
 *@param	len	������ݳ���
 *@param	pSm4Input �������
 *@param	mode	����ģʽ(�ο�\ref EM_ALG_SM4 "EM_ALG_SM4")
 *@retval	pSm4Output ������
 *@return
 *@li	NDK_OK �����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	    ����Ƿ�
*/
int NDK_AlgSM4Compute(unsigned char *pKey, unsigned char *pIVector, unsigned int len, unsigned char *pSm4Input, unsigned char *pSm4Output, unsigned char mode);
/** @} */ // �㷨ģ�����



/**
 *@brief   ָ���
 *@param    sendlen            ���͵�ָ���
 *@param    sendbuff            ���͵�ָ���(�255�ֽ�)
 *@param    recvlen            Ҫ���յ���ݳ���
 *@retval   recvbuff         ���ܵ���ݻ���(�����뷢�ͻ��湲��һ��buff)
 *@return
 *@li   NDK_OK �����ɹ�
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    �豸�ļ���ʧ��
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��
*/
int NDK_CosCmdRW(uint sendlen, uchar *sendbuf, uint recvlen, uchar *recvbuf);

/**
 *@brief   ��ȡ��ǰCOSģʽ
 *@retval   mode
 *@return
 *@li   NDK_OK �����ɹ�
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    �豸�ļ���ʧ��
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��
*/
int NDK_CosGetMode(uchar *mode);

/**
 *@brief   ����COSģʽ
 *@param    mode            ����ģʽ
 *@return
 *@li   NDK_OK �����ɹ�
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    �豸�ļ���ʧ��
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��
*/
int NDK_CosSetMode(uchar mode);

/**
 *@brief   ��ȡ��ǰCOS�汾
 *@retval   ver         �汾��(�ַ���ʽ���22���ֽ�)
 *@return
 *@li   NDK_OK �����ɹ�
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    �豸�ļ���ʧ��
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��
*/
int NDK_CosGetVer(uchar *ver);

/**
 *@brief   COS��λ
 *@return
 *@li   NDK_OK �����ɹ�
 *@li   \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"    �豸�ļ���ʧ��
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��
*/

int NDK_CosReset(void);



/** @addtogroup ��ȫ
* @{
*/

/**
 *@brief �ն���Կ����
*/
typedef enum{
	SEC_KEY_TYPE_TLK=0,	/**<�ն�װ����Կ*/
	SEC_KEY_TYPE_TMK,	/**<�ն�����Կ*/
	SEC_KEY_TYPE_TPK,	/**<�ն�PIN��Կ*/
	SEC_KEY_TYPE_TAK,	/**<�ն�MAC��Կ*/
	SEC_KEY_TYPE_TDK,	/**<�ն���ݼӽ�����Կ*/
	SEC_KEY_TYPE_MAX,	/**<�ж��ն���ԿMK/SK��Կ��ϵ�����Կ��Χ*/
	SEC_KEY_TYPE_DUKPT = 0x10,	/**<ר����DUKPT��Կ���ͣ�ÿ�����㶯̬���������TPK/TAK/TDK*/
	SEC_KEY_TYPE_RSA = 0x20,/**<RSA��Կ����*/
}EM_SEC_KEY_TYPE;

#define   _MASK(__n,__s)  	   (((1<<(__s))-1)<<(__n))
#define 	KEY_TYPE_MASK      _MASK(0, 6)
#define 	KEY_ALG_MASK	   _MASK(6, 2)

typedef enum{
	SEC_KEY_DES = 0,	/**<DES/TDES �㷨*/
	SEC_KEY_SM4 = (1<<6),		/**<SM4 �㷨*/
}EM_SEC_KEY_ALG;

/**
 *@brief ��ԿУ��ģʽ
*/
typedef enum{
	SEC_KCV_NONE=0,		/**<����֤*/
	SEC_KCV_ZERO,		/**<��8���ֽڵ�0x00����DES/TDES����,�õ������ĵ�ǰ4���ֽڼ�ΪKCV*/
	SEC_KCV_VAL,		/**<���ȶ���Կ���Ľ�����У��,�ٶ�"\x12\x34x56\x78\x90\x12\x34\x56"����DES/TDES��������,�õ����ĵ�ǰ4���ֽڼ�ΪKCV,�ݲ�֧��*/
	SEC_KCV_DATA,		/**<����һ�����KcvData,ʹ��Դ��Կ��[aucDstKeyValue(����) + KcvData]����ָ��ģʽ��MAC����,�õ�8���ֽڵ�MAC��ΪKCV,�ݲ�֧�� */
}EM_SEC_KCV;

/**
 *@brief MAC�㷨
*/
typedef enum{
	SEC_MAC_X99=0,      /**< X99�㷨����ݷ�Ϊ8�ֽ�block�����㲹0��ÿ��block���ܺ�����һ��block������Կ���ȼ���*/
	SEC_MAC_X919,       /**< X99�㷨����ݷ�Ϊ8�ֽ�block�����㲹0��ÿ��block���ܺ�����һ��block������ԿDES���ܣ�
                            ���֡�����Կ����Ϊ16�ֽ���3DES�����Ϊ8�ֽڰ�DES*/
	SEC_MAC_ECB,        /**< ȫ��������󣬽����������DES����б任���ο������淶�й���ECB�㷨˵��*/
	SEC_MAC_9606,       /**< ȫ��������������������des����*/
	SEC_MAC_SM4,       /**< ��ݷ�Ϊ16�ֽڵ�block�����㲹0��ÿ��block����SM4���ܺ�����һ��block����SM4����*/
	SEC_ROOT_MAC_X919=0x10,	/**< ʹ�ø�MAC��Կ����*/
}EM_SEC_MAC;
/**
 * ����ʵ��PIN�����̵ĳ�ʱ���Ƶı���
 */
typedef enum {
	SEC_PIN_ISO9564_0=3,    /**<ʹ�����˺ż��ܣ����벻��λ��'F'*/
	SEC_PIN_ISO9564_1=4,    /**<��ʹ�����˺ż��ܣ����벻��λ�������*/
	SEC_PIN_ISO9564_2=5,    /**<��ʹ�����˺ż��ܣ����벻��λ��'F'*/
	SEC_PIN_ISO9564_3=6,     /**<ʹ�����˺ż��ܣ����벻��λ�������*/
	SEC_PIN_SM4_1,		/**<��ʹ�����˺ţ����벻��λ��'F'*/
	SEC_PIN_SM4_2,		/**<ʹ�����˺���䷽ʽ1�����벻��λ��'F'*/
	SEC_PIN_SM4_3,		/**<ʹ�����˺���䷽ʽ1�����벻��λ�������*/
	SEC_PIN_SM4_4,		/**<ʹ�����˺���䷽ʽ2�����벻��λ��'F'*/
	SEC_PIN_SM4_5,		/**<ʹ�����˺���䷽ʽ2�����벻��λ�������*/
}EM_SEC_PIN;

/**
 *@brief DES�������ͣ����ڲ�ͬλ���Խ��л����㣬
        ���磺SEC_DES_ENCRYPT|SEC_DES_KEYLEN_8
            ��ʾ����8�ֽ���Կ���ȣ�ʹ��ECBģʽ���м�������
*/
typedef enum{
	SEC_DES_ENCRYPT=0,                  /**<DES����*/
	SEC_DES_DECRYPT=1,                  /**<DES����*/
	SEC_DES_KEYLEN_DEFAULT=(0<<1),      /**<ʹ�ð�װ���ȵ���Կ���м���*/
    SEC_DES_KEYLEN_8=(1<<1),            /**<ʹ��8�ֽ���Կ���м���*/
    SEC_DES_KEYLEN_16=(2<<1),           /**<ʹ��16�ֽ���Կ���м���*/
    SEC_DES_KEYLEN_24=(3<<1),           /**<ʹ��24�ֽ���Կ���м���*/
    SEC_DES_MASK=0x07,			/**<des��������ʹ�õ�ӳ��ֵ�������ӳ��ֵλ����Ч*/
    SEC_SM4_ENCRYPT=(1<<4),                  /**<SM4����*/
	SEC_SM4_DECRYPT=(1<<5),                  /**<SM4����*/
}EM_SEC_DES;

/**
 *@brief VPP ���񷵻صļ�ֵ����
*/
typedef enum{
    SEC_VPP_KEY_PIN,					/**< ��PIN���밴�£�Ӧ��Ӧ����ʾ'*'*/
    SEC_VPP_KEY_BACKSPACE,				/**< �˸����*/
    SEC_VPP_KEY_CLEAR,					/**< ������*/
    SEC_VPP_KEY_ENTER,					/**< ȷ�ϼ���*/
    SEC_VPP_KEY_ESC,					/**< pin����ȡ��*/
    SEC_VPP_KEY_NULL					/**< pin���¼�����*/
}EM_SEC_VPP_KEY;

/**
 *@brief �ѻ��ն�У��ģʽ
 */
typedef enum{
	SEC_OFFLINE_PIN_MODE_EMV=0,
}ST_SEC_OFFLINE_PIN_MODE;

/**
 *@brief �ն˰�ȫ����״̬
 */
typedef enum{
	SEC_TAMPER_STATUS_NONE = 0,					/**< �ް�ȫ����*/
	SEC_TAMPER_STATUS_HW = (1<<0),					/**< Ӳ����ȫ����*/
	SEC_TAMPER_STATUS_SEC_CONFIG = (1<<1),			/**< ��ȫ�Ĵ���ֵ����*/
	SEC_TAMPER_STATUS_CHECKFILE = (1<<2),			/**< �ļ�У�����*/
	SEC_TAMPER_DEVICE_DISABLED = (1<<8),		/**< �豸δʹ��*/
}EM_SEC_TAMPER_STATUS;

#define SEC_KEYBLOCK_FMT_TR31		(0x54523331)	/**<��չ��TR-31 Key block��Կ��װ���ʽ,0x54523331��"TR31" */
/**
 *@brief ��չ��Կ��װ����Ϣ������ʵ��TR-31����չ����Կ��װ���ʽ
 *		  ���û���ʹ��TR-31��װ������װ��Կ��ʱ����Ҫ����Կ��ݷ�װ��ST_EXTEND_KEYBLOCK�ṹ��
 *		  ���洢��ST_SEC_KEY_INFO�ṹ��sDstKeyValue[24]��Ա�����ݸ���Կ��װ�ӿڣ�ϵͳ���᳢��ʹ�øø�ʽ������װ��Կ��
*/
typedef struct {
	unsigned int format;		/**< ��չ��Կ��װ���ʽ,Ŀǰ��֧��TR-31��ʽ SEC_KEYBLOCK_FMT_TR31*/
	unsigned int len;			/**< ��Կ��װ�����(pblock)����*/
	char *pblock;				/**< ��Կ���ָ��*/
}ST_EXTEND_KEYBLOCK;

/**
 *@brief ��Կ��Ϣ
*/
typedef struct{
    uchar 	ucScrKeyType; 		/**< ��ɢ����Կ��Դ��Կ����Կ���ͣ��ο�\ref EM_SEC_KEY_TYPE "EM_SEC_KEY_TYPE", ���õ���ucDstKeyType���ڵ���Կ����*/
    uchar 	ucDstKeyType; 		/**< Ŀ����Կ����Կ���ͣ��ο�\ref EM_SEC_KEY_TYPE "EM_SEC_KEY_TYPE" */
    uchar 	ucScrKeyIdx;		/**< ��ɢ����Կ��Դ��Կ����,����һ���1��ʼ,���ñ���Ϊ0,���ʾ�����Կ��д����������ʽ */
    uchar 	ucDstKeyIdx;		/**< Ŀ����Կ���� */
    int 	nDstKeyLen;			/**< Ŀ����Կ����,8,16,24,��12=sizeof(ST_EXTEND_KEYBLOCK) */
    uchar 	sDstKeyValue[24];	/**< д����Կ�����ݣ�����Կ���ȵ���12ʱ�� ����ST_EXTEND_KEYBLOCK�ṹʹ��*/
}ST_SEC_KEY_INFO;

/**
 *@brief У����Ϣ
*/
typedef struct{
    int 	nCheckMode; 		/**< У��ģʽ �ο�\ref ST_SEC_KCV_INFO "ST_SEC_KCV_INFO"*/
    int 	nLen;				/**< У�������� */
    uchar 	sCheckBuf[128];		/**< У����ݻ����� */
}ST_SEC_KCV_INFO;

/**
 *@brief �ѻ�����PIN��Կ
*/
typedef struct
{
	uint	unModlen;					/**< ���ܹ�Կģ��  */
	uchar	sMod[MAX_RSA_MODULUS_LEN];  /**< ���ܹ�Կģ��,���ֽ���ǰ,���ֽ��ں�,����λǰ��0 */
	uchar	sExp[4];       				/**< ���ܹ�Կָ��,���ֽ���ǰ,���ֽ��ں�,����λǰ��0 */
	uchar	ucIccRandomLen;   			/**< �ӿ�Ƭȡ�õ������  */
	uchar	sIccRandom[8];   			/**< �ӿ�Ƭȡ�õ������  */
}ST_SEC_RSA_PINKEY;

/**
 *@brief RSA��Կ��Ϣ
*/
typedef struct {
    ushort usBits;                    			/**< RSA��Կλ�� */
    uchar sModulus[MAX_RSA_MODULUS_LEN+1];  	/**< ģ */
    uchar sExponent[MAX_RSA_MODULUS_LEN+1]; 	/**< ָ�� */
    uchar reverse[4];							/**< ����4�ֽڣ�������Կ�洢*/
}ST_SEC_RSA_KEY;

/**
 *@brief	��ȡ��ȫ�ӿڰ汾
 *@retval	pszVerInfoOut	�汾��Ϣ��С��16�ֽڣ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszVerInfoOutΪNULL)
*/
int NDK_SecGetVer(uchar * pszVerInfoOut);

/**
 *@brief	��ȡ�����
 *@param	nRandLen		��Ҫ��ȡ�ĳ���
 *@retval	pvRandom		������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pvRandomΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecGetRandom(int nRandLen , void *pvRandom);

/**
 *@brief	���ð�ȫ����
 *@details	1���û�һ��ͨ��˺��������˰�ȫ������Ϣ������������ݴ����õ�������Ϣ���п��ơ�
 			���û�е��ô˺������ã����������ᰴ��Ĭ�ϵ���Ͱ�ȫ���ý��С�
 			2��ͨ����ȫ������Ϣֻ������ߣ������?�ͣ�������Ϣ�����������һλ��1����0������Ϊ��ȫ�Խ��ͣ���
 *@param	unCfgInfo		������Ϣ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SecSetCfg(uint unCfgInfo);

/**
 *@brief	��ȡ��ȫ����
 *@retval	punCfgInfo		������Ϣ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(punCfgInfoΪNULL)
*/
int NDK_SecGetCfg(uint *punCfgInfo);

/**
 *@brief	��ȡ��Կkcvֵ
 *@details	��ȡ��Կ��KCVֵ,�Թ��Ի�˫��������Կ��֤,��ָ������Կ���㷨��һ����ݽ��м���,�����ز���������ġ�
 *@param	ucKeyType		��Կ����
 *@param	ucKeyIdx		��Կ���
 *@param	pstKcvInfoOut	KCV����ģʽ
 *@retval	pstKcvInfoOut	KCVֵ
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pstKcvInfoOutΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecGetKcv(uchar ucKeyType, uchar ucKeyIdx, ST_SEC_KCV_INFO *pstKcvInfoOut);

/**
 *@brief	����������Կ
 *@return
 *@li	NDK_OK		�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��
*/
int NDK_SecKeyErase(void);

/**
 *@brief	д��һ����Կ,����TLK,TMK��TWK��д�롢��ɢ,������ѡ��ʹ��KCV��֤��Կ��ȷ�ԡ�
 *@details
 	PED���������Կ��ϵ,���ϵ��µ�˳������Ϊ��
	TLK��Terminal Key Loading Key
    	�յ��л�POS��Ӫ�̵�˽����Կ,���յ��л���POS��Ӫ���ڰ�ȫ������ֱ��д�롣
    	����Կÿ��PED�ն�ֻ��һ��,���������1��1

	TMK��Terminal Master Key��Acquirer Master Key
		�ն�����Կ,���߳�Ϊ�յ�������Կ��������Կ����100��,�������1��100
		TMK�����ڰ�ȫ������ֱ��д��,ֱ��д��TMK,��ͨ��TMK��ɢTWK�ķ�ʽ��MK/SK����Կ��ϵһ�¡�
	TWK��Transaction working key = Transaction Pin Key + Transaction MAC Key + Terminal DES Enc Key + Terminal DES DEC/ENC Key
		�ն˹�����Կ,����PIN���ġ�MAC���������Կ��������Կ����100��,�������1��100��
		TPK:����Ӧ������PIN��,����PIN Block��
		TAK:����Ӧ�ñ���ͨѶ��,����MAC��
		TEK:���ڶ�Ӧ����������ݽ���DES/TDES���ܴ����洢��
		TDK:���ڶ�Ӧ����������ݽ���DES/TDES�ӽ�������
	TWK�����ڰ�ȫ������ֱ��д��,ֱ��д��TWK��Fixed Key��Կ��ϵһ�¡�ÿ����Կ���������,����,��;�ͱ�ǩ��
	������Կ�ı�ǩ����д����Կǰͨ��API�趨��,����Ȩ����Կ��ʹ��Ȩ�޲���֤��Կ���ᱻ���á�

	DUKPT��Կ���ƣ�
	DUKPT��Derived Unique Key Per Transaction����Կ��ϵ��һ�ν���һ��Կ����Կ��ϵ,��ÿ�ʽ��׵Ĺ�����Կ��PIN��MAC���ǲ�ͬ�ġ�
	��������KSN��Key Serial Number���ĸ���,KSN����ʵ��һ��һ�ܵĹؼ����ӡ� ÿ��KSN��Ӧ����Կ�������Կ��;���������ͬ����Կ��
 	������Կ����10�顣��д��TIK��ʱ��,��Ҫѡ����������,��ʹ��DUKPT��Կʱѡ���Ӧ��������
 *@param	pstKeyInfoIn		��Կ��Ϣ
 *@param	pstKcvInfoIn		��ԿУ����Ϣ
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pstKeyInfoIn��pstKcvInfoInΪNULL����Կ���Ȳ�����8/16/24��������չTR-31��ʽ�İ�װ��)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	�ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR" 		              ����ʧ��
*/
int NDK_SecLoadKey(ST_SEC_KEY_INFO * pstKeyInfoIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

/**
 *@brief	�������μ���PINBlock���߼���MAC֮����С���ʱ��
 *@details 	PINBLOCK���ʱ��ļ��㷽ʽ��
 			Ĭ��Ϊ120����ֻ�ܵ���4��,��TPKIntervalTimeMsĬ��ֵΪ30��,���øú����������ú�,����Ϊ4*TPKIntervalTimeMsʱ����ֻ�ܵ���4�Ρ�
 			���紫���TPKIntervalTimeMsΪ20000(ms),��80����ֻ�ܵ���4��
 *@param	unTPKIntervalTimeMs	PIN��Կ������ʱ�䣬0-����Ĭ��ֵ��0xFFFFFFFF�����ı�
 *@param	unTAKIntervalTimeMs	MAC��Կ������ʱ�䣬0-����Ĭ��ֵ��0xFFFFFFFF�����ı�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��

*/
int NDK_SecSetIntervaltime(uint unTPKIntervalTimeMs, uint unTAKIntervalTimeMs);

/**
 *@brief	���ù��ܼ���
 *@details 	�������������У����ܼ���;���ж���
 *@param	ucType	������;���Ͷ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��

*/
int NDK_SecSetFunctionKey(uchar ucType);

/**
 *@brief	����MAC
 *@param	ucKeyIdx		��Կ���
 *@param	psDataIn		�������
 *@param	nDataInLen		������ݳ���
 *@param	ucMod			MAC����ģʽ �ο�\ref EM_SEC_MAC "EM_SEC_MAC"
 *@retval	psMacOut		MACֵ������8�ֽ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
*/
int NDK_SecGetMac(uchar ucKeyIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar ucMod);

/**
 *@brief	��ȡPIN Block
 *@param	ucKeyIdx		��Կ���
 *@param	pszExpPinLenIn	���볤�ȣ���ʹ��,���зָ���磺0,4,6
 *@param	pszDataIn		��ISO9564Ҫ�������PIN BLOCK
 *@param	ucMode			����ģʽ �ο�\ref EM_SEC_PIN "EM_SEC_PIN"
 *@param	nTimeOutMs		��ʱʱ�䣨������С��5����ߴ���200�룩��λ:ms
 *@retval	psPinBlockOut	    PIN Block���,�ò�����NULLʱ��PIN���ͨ��\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"�����ȡ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		����Ƿ�(����ģʽ�Ƿ�)
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(ʱ�����Ƿ�)
*/
int NDK_SecGetPin(uchar ucKeyIdx, uchar *pszExpPinLenIn,const uchar * pszDataIn, uchar *psPinBlockOut, uchar ucMode, uint nTimeOutMs);

/**
 *@brief	����DES
 *@details 	ʹ��ָ����Կ����des���㣬ע�⣺1~255��Ž��мӽ���
 *@param	ucKeyType		DES��Կ����
 *@param	ucKeyIdx		DES��Կ���
 *@param	psDataIn		�����Ϣ
 *@param	nDataInLen		��ݳ���
 *@param	ucMode			����ģʽ �ο�\ref EM_SEC_DES "EM_SEC_DES"
 *@retval	psDataOut		��������Ϣ
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR"		����ʧ��
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	����Ƿ�(��ݳ��Ȳ���8������)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	�ڴ�ռ䲻��
*/
int NDK_SecCalcDes(uchar ucKeyType, uchar ucKeyIdx, uchar * psDataIn, int nDataInLen, uchar *psDataOut, uchar ucMode);

/**
 *@brief	У���ѻ�����PIN
 *@details 	��ȡ����PIN,Ȼ����Ӧ���ṩ�Ŀ�Ƭ�����뿨Ƭͨ����,������PIN BLOCKֱ�ӷ��͸�Ƭ(PIN BLOCK��ʽ���÷���������)��
 *@param	ucIccSlot		IC����
 *@param	pszExpPinLenIn	���볤�ȣ���ʹ��,���зָ���磺0,4,6
 *@param	ucMode			IC������ģʽ(ֻ֧��EMV)
 *@param	unTimeoutMs		��ʱʱ��
 *@retval	psIccRespOut	��ƬӦ����,�ò�����NULLʱ��PIN���ͨ��\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"�����ȡ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(��ʱ����Ƿ�)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		����Ƿ�(ucMode�Ƿ���)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	              �ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR" 	                              ����ʧ��
*/
int NDK_SecVerifyPlainPin(uchar ucIccSlot, uchar *pszExpPinLenIn, uchar *psIccRespOut, uchar ucMode,  uint unTimeoutMs);

/**
 *@brief	У���ѻ�����PIN
 *@details 	�Ȼ�ȡ����PIN,����Ӧ���ṩ��RsaPinKey������PIN����EMV�淶���м���,Ȼ����Ӧ���ṩ�Ŀ�Ƭ�����뿨Ƭͨ����,������PINֱ�ӷ��͸�Ƭ
 *@param	ucIccSlot		IC����
 *@param	pszExpPinLenIn	���볤�ȣ���ʹ��,���зָ���磺0,4,6
 *@param	pstRsaPinKeyIn	RSA��Կ���
 *@param	ucMode			IC������ģʽ(ֻ֧��EMV)
 *@param	unTimeoutMs		��ʱʱ��
 *@retval	psIccRespOut	��ƬӦ����,�ò�����NULLʱ��PIN���ͨ��\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"�����ȡ
 *@return
 *@li	NDK_OK				    �����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(��ʱ����Ƿ�)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		����Ƿ�(ucMode�Ƿ���)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        �ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR" 	                ����ʧ��
*/
int NDK_SecVerifyCipherPin(uchar ucIccSlot, uchar *pszExpPinLenIn, ST_SEC_RSA_KEY *pstRsaPinKeyIn, uchar *psIccRespOut, uchar ucMode, uint unTimeoutMs);

/**
 *@brief	��װDUKPT��Կ
 *@param	ucGroupIdx		��Կ��ID
 *@param	ucSrcKeyIdx		ԭ��ԿID���������ܳ�ʼ��Կֵ����ԿID��
 *@param	ucKeyLen		��Կ����
 *@param	psKeyValueIn	��ʼ��Կֵ
 *@param	psKsnIn		    KSNֵ
 *@param	pstKcvInfoIn	Kcv��Ϣ
 *@return
 *@li	NDK_OK		                �����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		  ����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		                ����ʧ��
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"               �ڴ�ռ䲻��
*/
int NDK_SecLoadTIK(uchar ucGroupIdx, uchar ucSrcKeyIdx, uchar ucKeyLen, uchar * psKeyValueIn, uchar * psKsnIn, ST_SEC_KCV_INFO * pstKcvInfoIn);

/**
 *@brief	��ȡDUKPTֵ
 *@param	ucGroupIdx		DUKPT��Կ��ID
 *@retval	psKsnOut		��ǰKSN��
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psKsnOutΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecGetDukptKsn(uchar ucGroupIdx, uchar * psKsnOut);

/**
 *@brief	KSN������
 *@param	ucGroupIdx		DUKPT��Կ��ID
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecIncreaseDukptKsn(uchar ucGroupIdx);

/**
 *@brief	��ȡDUKPT��Կ��PIN Block
 *@param	ucGroupIdx		��Կ���
 *@param	pszExpPinLenIn	���볤�ȣ���ʹ��,���зָ���磺0,4,6
 *@param	psDataIn		��ISO9564Ҫ�������PIN BLOCK
 *@param	ucMode			����ģʽ
 *@param	unTimeoutMs		��ʱʱ��
 *@retval	psKsnOut		��ǰKSN��
 *@retval	psPinBlockOut	PIN Block���,�ò�����NULLʱ��PIN���ͨ��\ref NDK_SecGetPinResult "NDK_SecGetPinResult()"�����ȡ
 *@return
 *@li	NDK_OK				    �����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(��ʱ����Ƿ�)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		����Ƿ�(ucMode�Ƿ���)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	        �ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR" 	                ����ʧ��
*/
int NDK_SecGetPinDukpt(uchar ucGroupIdx, uchar *pszExpPinLenIn, uchar * psDataIn, uchar* psKsnOut, uchar *psPinBlockOut, uchar ucMode, uint unTimeoutMs);

/**
 *@brief	����DUKPT��ԿMAC
 *@param	ucGroupIdx		��Կ���
 *@param	psDataIn		�������
 *@param	nDataInLen		������ݳ���
 *@param	ucMode			MAC����ģʽ
 *@retval	psMacOut		MACֵ������8�ֽ�
 *@retval	psKsnOut		��ǰKSN��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 	�ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR"  	����ʧ��
*/
int NDK_SecGetMacDukpt(uchar ucGroupIdx, uchar *psDataIn, int nDataInLen, uchar *psMacOut, uchar *psKsnOut, uchar ucMode);

/**
 *@brief	����DES
 *@details 	ʹ��ָ����Կ����des����
 *@param	ucGroupIdx		DUKPT��Կ���
 *@param	ucKeyVarType	��Կ����
 *@param	psIV			��ʼ����
 *@param	psDataIn		�����Ϣ
 *@param	usDataInLen		��ݳ���
 *@param	ucMode			����ģʽ
 *@retval	psDataOut		��������Ϣ
 *@retval	psKsnOut		��ǰKSN��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 		����Ƿ�(��ݳ��Ȳ���8������)
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC" 		�ڴ�ռ䲻��
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��
*/
int NDK_SecCalcDesDukpt(uchar ucGroupIdx, uchar ucKeyVarType, uchar *psIV, ushort usDataInLen, uchar *psDataIn,uchar *psDataOut,uchar *psKsnOut ,uchar ucMode);

/**
 *@brief	��װRSA��Կ
 *@param	ucRsaKeyIndex	��Կ���
 *@param 	pstRsaKeyIn		RSA��Կ��Ϣ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��
*/
int NDK_SecLoadRsaKey(uchar ucRsaKeyIndex, ST_SEC_RSA_KEY *pstRsaKeyIn);

/**
 *@brief	RSA��Կ�Լӽ���
 *@details	�ú������RSA���ܻ��������,���ܻ����ͨ��ѡ�ò�ͬ����Կʵ�֡���(Modul,Exp)ѡ��˽����Կ,����м���;��ѡ�ù�����Կ,����н��ܡ�
 			psDataIn�ĵ�һ���ֽڱ���С��psModule�ĵ�һ���ֽڡ� �ú����ʵ�ֳ��Ȳ�����2048 bits ��RSA���㡣
 			�������ݿ��ٵĻ�������ģ����+1��
 *@param	ucRsaKeyIndex	��Կ���
 *@param 	psDataIn		��������,���Ⱥ�ģ�ȳ���ʹ��BCD��洢��
 *@param	nDataLen		������ݳ���
 *@retval	psDataOut		������,��ģ�ȳ���ʹ��BCD��洢��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SecRecover(uchar ucRsaKeyIndex, const uchar *psDataIn, int nDataLen, uchar *psDataOut);
/**
 *@brief	��ȡ��������״̬(DUKPT)
 *@retval	psPinBlock		pinblock���
 *@retval	psKsn			��ǰDUKPT��KSNֵ
 *@retval 	nStatus			״ֵ̬
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psPinBlock/psKsn/nStatusΪNULL)
 *@li	\ref NDK_ERR_SECVP_NOT_ACTIVE "NDK_ERR_SECVP_NOT_ACTIVE" 	VPPû�м����һ�ε���VPPInit
*/
int NDK_SecGetPinResultDukpt(uchar *psPinBlock, uchar *psKsn, int *nStatus);
/**
 *@brief	��ȡ��������״̬
 *@retval	psPinBlock		pinblock���
							��SEC_VPP_KEY_PIN,SEC_VPP_KEY_BACKSPACE,SEC_VPP_KEY_CLEAR������״̬�У����ֽڱ���������PIN�ĳ���
 *@retval 	nStatus			״ֵ̬
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psPinBlock��nStatusΪNULL)
 *@li	\ref NDK_ERR_SECP_PARAM "NDK_ERR_SECP_PARAM" 	����Ƿ�
 *@li	\ref NDK_ERR "NDK_ERR"          	����ʧ��
 *@li	\ref NDK_ERR_SECVP_NOT_ACTIVE "NDK_ERR_SECVP_NOT_ACTIVE" 	VPPû�м����һ�ε���VPPInit
*/
int NDK_SecGetPinResult(uchar *psPinBlock, int *nStatus);

/**
 *@brief	��ʼ��PIN����Ĵ��������̣�����10�����ּ��̵İ�ť��꣬�Լ�3�����ܼ�(�˸�ȡ��ȷ��)�İ�����Ϣ���������������к�����ְ���ֵ
 *@param	num_btn			10�����ְ�ť(vpp_button)��ÿ����ťռ8�ֽڣ��ܴ�СΪ10*8=80�ֽڡ�ÿ����ť�ɡ����ϡ������¡���������ɣ�һ������"x""y"2��uint16_t��������(uint16_t x, uint16_t y)
 *@param					�����һ����ťΪ0�㿪ʼ��16����(0x10)���Σ���ônum_btn[0] = ((0x0000,0x0000),(0x0010, 0x0010))
 *@param	func_key		3�����ܰ���(�˸�ȡ��ȷ�ϼ�vpp_key[3])���ܴ�СΪ3*(4+8)=36�ֽڣ�ÿ��������ɽṹΪ��ֵ+��ť(int key, vpp_button)��keyΪ4�ֽ�int�ͣ�ȡֵΪK_BASP/K_ENTER/K_ESC����֮һ
 *@retval	out_seq			����洢10�ֽ�������еİ���ֵ('0'-'9')�����������num_btn[10]һһ��Ӧ�������ڼ�����ʾ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecVppTpInit(uchar *num_btn, uchar *func_key, uchar *out_seq);

/**
 *@brief	������Կ����Ӧ�����
 *@details 	����ϵͳӦ��(Keyloader)ʹ�ã�ͨ��ýӿ�ָ������װ��Կ��������ơ�
 *			����װ��Կ��ʱ��ϵͳ��ȫ���񽫻��жϵ�������ݣ��پ����Ƿ���øú������õ���Կ������ƣ�
 *			-�����ͨ�û�����
 *				��������Ч��ϵͳ��ȫ�����ֱ��ָ����װ��Կ������Ϊ��ǰ�û�����
 *			-���ϵͳӦ�ó���
 *				�ж�����Keyloaderϵͳ������ȫ�������\ref NDK_SecSetKeyOwner "NDK_SecSetKeyOwner()"���õ�Ӧ����Ϊ��ǰ��װ��Կ��������
 *					���Keyloaderδ���ù���Կ��������Ĭ����Կ����ָ��ΪKeyloader����
 *				����Keyloaderϵͳ������ֱ���Ե�ǰϵͳӦ��Ϊ��Կ����
 *@param	pszName			��Կ����Ӧ�����(����С��256)�������ݵ��ǿ��ַ�������֮ǰ���õ���Կ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszNameΪNULL����Ӧ����Ƴ��ȴ��ڵ���256)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecSetKeyOwner(char *pszName);

/**
 *@brief	��ȡ��ȫ����״̬
 *@retval	pnStatus			��ȫ����״̬�ο�\ref EM_SEC_TAMPER_STATUS "EM_SEC_TAMPER_STATUS"
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnStatusΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_SecGetTamperStatus(int *pnStatus);

/** @} */ // ��ȫģ�����



/** @addtogroup ����ͨѶ
* @{
*/


/**
 *@brief  ����ѡ��
*/
typedef enum {
    PORT_NUM_COM1 = 0,		/**<����1*/
    PORT_NUM_COM2 = 1,		/**<����2*/
    PORT_NUM_WIRELESS = 2,	/**<����ģ��*/
    PORT_NUM_MUX1 = 3,		/**<��·����1*/
    PORT_NUM_MUX2 = 4,		/**<��·����2*/
    PORT_NUM_MUX3 = 5,		/**<��·����3*/
    PORT_NUM_MODEM = 6,		/**<����ģ��*/
    PORT_NUM_WIFI = 7,		/**<Wifiģ��*/
    PORT_NUM_USB = 8,		/**<USBģ��*/
    PORT_NUM_SCAN = 9,   	/**<ɨ��ģ��*/
    PORT_NUM_BT = 10,    	/**<����ģ��*/
    PORT_NUM_AUDIO = 11,	/**<��Ƶģ��*/
    PORT_NUM_CCID = 12, 	/**<CCIDģ��*/
    PORT_NUM_WAVE = 13,		/**<��ģ��*/
    PORT_NUM
} EM_PORT_NUM;


/**
 *@brief	��ʼ�����ڣ��Դ��ڲ����ʣ����λ����żλ��ֹͣλ�Ƚ������á�����ÿ��ʹ�ô���֮ǰ�ȵ��øó�ʼ������(USB�ǲ���Ҫ�����ʣ������ú���ʱ����Ҫ��һ��������ᱨ�������)\n
			 		֧�ֵĲ����ʷֱ�Ϊ{300,1200,2400,4800,9600,19200,38400,57600,115200}\n
			 		֧�ֵ����λ�ֱ�Ϊ{8,7,6,5}\n
			 		У�鷽ʽѡ��ֱ�Ϊ{N(n):��У��;O(o):��У��;E(e):żУ��}\n
			 		֧�ֵ�ֹͣλ�ֱ�Ϊ{1,2}
 *@param	emPort	ָ���Ĵ���
 *@param	pszAttr	ͨѶ�ʺ͸�ʽ��,��"115200,8,N,1",���ֻд��������ȱʡΪ"8,N,1"��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszAttrΪNULL��emPort�������ͷǷ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��ȡ��aux_fd��صĲ���ʧ�ܵ�)
*/
int NDK_PortOpen(EM_PORT_NUM emPort, const char *pszAttr);

/**
 *@brief	�رմ���
 *@param	emPort	ָ���Ĵ���
 *@return
 *@li	NDK_OK		�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"  	����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	����Ƿ�(emPort�������ͷǷ�)
*/
int NDK_PortClose(EM_PORT_NUM emPort);

/**
 *@brief	���趨��ʱʱ�����ָ���Ĵ��ڣ���ȡָ�����ȵ���ݣ������pszOutbuf
 *@param	emPort	ָ���Ĵ���
 *@param	unLen	��ʾҪ������ݳ���,>0(С��4K)
 *@param	nTimeoutMs	�ȴ�ʱ�䣬��λΪ����
 *@retval	pszOutBuf	������ݻ������ͷָ��
 *@retval	pnReadLen	���ض���ʵ�ʳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszOutbuf\pnReadlenΪNULL��emPort�������ͷǷ���unLen��ݳ��ȷǷ���nTimeoutMs��ʱʱ�����Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB��δ��
 *@li	\ref NDK_ERR_READ "NDK_ERR_READ" 				���ļ�ʧ��
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		��ʱ����(���ڶ���ʱ)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_PortRead(EM_PORT_NUM emPort, uint unLen, char *pszOutBuf,int nTimeoutMs, int *pnReadLen);

/**
 *@brief	��ָ���Ĵ�����ָ�����ȵ����
 *@param	emPort	ָ���Ĵ���
 *@param	unLen	��ʾҪд����ݳ���
 *@param	pszInbuf	��ݷ��͵Ļ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszInbufΪNULL��emPort�������ͷǷ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB��δ��
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 				д�ļ�ʧ��
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_PortWrite(EM_PORT_NUM emPort, uint unLen,const char *pszInbuf);

/**
 *@brief	�ж�ָ�����ڷ��ͻ������Ƿ�Ϊ��
 *@param	emPort	ָ���Ĵ���
 *@return
 *@li	NDK_OK	�����������
 *@li	\ref NDK_ERR "NDK_ERR" 	�����������
*/
int NDK_PortTxSendOver(EM_PORT_NUM emPort);

/**
 *@brief	���ָ�����ڵĽ��ջ�����
 *@param	emPort	ָ���Ĵ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(emPort�������ͷǷ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_PortClrBuf(EM_PORT_NUM emPort);

/**
 *@brief	ȡ���������ж����ֽ�Ҫ����ȡ(һ��δȡ��Ԥ�ڵ���ݳ��ȣ����\ref NDK_PortRead "NDK_PortRead()"������ж�λ�ȡ����ÿ�λ�ȡ�ĳ����ۼ�)
 *@param	emPort	ָ���Ĵ���
 *@retval	pnReadLen	���ػ������ȡ�ĳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnReadlenΪNULL��emPort�������ͷǷ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_PortReadLen(EM_PORT_NUM emPort,int *pnReadLen);

/**
*@brief AudioTX��ѹ����
*/
typedef enum AudioTX_Vol
{
	AudioTX_Vol_200mV=1,
	AudioTX_Vol_100mV=2,
	AudioTX_Vol_50mV=3,
}EM_AudioTX_Vol;

/**
*@brief AudioTX����Ƶ�ʶ���
*/
typedef enum AudioTX_Freq
{
	AudioTX_Freq_LF,
	AudioTX_Freq_HF,
}EM_AudioTX_Freq;

/**
 *@brief	ѡ����Ƶ���͵�ѹ
 *@param	tx_Vol	ָ���ĵ�ѹ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(tx_Volֵ�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_AudioTXSel(EM_AudioTX_Vol tx_Vol);
/**
 *@brief	ѡ����Ƶ����Ƶ��
 *@param	tx_Freq	ָ����Ƶ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(tx_Freqֵ�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_AudioTX_FreqSel(EM_AudioTX_Freq tx_Freq);

/** @} */ // ����ͨѶģ�����

/** @addtogroup modemģ��
* @{
*/

/**
*@brief  modem����״̬����
*/
typedef enum MDM_STATUS
{
	MDMSTATUS_NORETURN_AFTERPREDIAL=0,
	MDMSTATUS_OK_AFTERPREDIAL=1,
	MDMSTATUS_CONNECT_AFTERPREDIAL=2,
	MDMSTATUS_MS_NODIALTONE = -2,
	MDMSTATUS_MS_NOCARRIER =	-3,
	MDMSTATUS_MS_BUSY = -4,
	MDMSTATUS_MS_ERROR = -5,
	MDMSTATUS_NOPREDIAL = -11,
}EM_MDMSTATUS;
/**
*@brief  modem��ʼ������Ĳ����������Ͷ���
*/
typedef enum MDM_Patchtype
{
	MDM_PatchType5=5, 	/**<Ĭ�ϵĲ�����ʽ*/
	MDM_PatchType4=4,	/**<���⻷����ʹ�õĲ�����ʽ*/
	MDM_PatchType3=3,	/**<�������Ӳ�����*/
	MDM_PatchType2=2,	/**<Ԥ��*/
	MDM_PatchType1=1,	/**<Ԥ��*/
	MDM_PatchType0=0,	/**<Ԥ��*/
}EM_MDM_PatchType;

/**
 *@brief	ͬ�����ų�ʼ������
 *@param	emType		����������Ӧ��ͬ����·����������ʹ�á�
 *@return	��
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(emType���������Ƿ�)
 *@li	\ref NDK_ERR_MODEM_ATCOMNORESPONSE "NDK_ERR_MODEM_ATCOMNORESPONSE" 	MODEM AT��������Ӧ
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM �˿�д���ʧ��
 *@li	\ref NDK_ERR_MODEM_SETCHIPFAIL "NDK_ERR_MODEM_SETCHIPFAIL" 	MODEM ģ��Ĵ�������ʧ��
*/
int NDK_MdmSdlcInit(EM_MDM_PatchType emType);

/**
 *@brief	�첽modem��ʼ��(Ŀǰ�첽modem��emTypeΪ����ֵ������)��
 *@param	emType 	����������Ӧ��ͬ����·����������ʹ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM �˿�д���ʧ��
 *@li	\ref NDK_ERR_MODEM_ATCOMMANDERR "NDK_ERR_MODEM_ATCOMMANDERR" 		MODEM AT�������
*/
int NDK_MdmAsynInit(EM_MDM_PatchType emType);

/**
 *@brief	modem���ź���
 *@param	pszDailNum 	���ź���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszDailNumΪNULL��pszDailNum���ȴ���25)
 *@li	\ref NDK_ERR_MODEM_NOLINE "NDK_ERR_MODEM_NOLINE" 		δ����
 *@li	\ref NDK_ERR_MODEM_OTHERMACHINE "NDK_ERR_MODEM_OTHERMACHINE" 		���ڲ���
 *@li	\ref NDK_ERR_MODEM_STARTSDLCTASK "NDK_ERR_MODEM_STARTSDLCTASK" 		MODEM ����ʱ����SDLC ����ʧ��
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM �˿�д���ʧ��
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
*/
int NDK_MdmDial(const char * pszDailNum);

/**
 *@brief	���modem״̬��
 *@param	pemStatus 	modem״̬��ʵ�ʷ���ֵ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pemStatusΪNULL)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM״̬δȷ��״̬
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 				MODEM �ֶ��˳�
*/
int NDK_MdmCheck(EM_MDMSTATUS  *pemStatus);

/**
 *@brief	modem��ݷ��͡�
 *@param	pszData 	���͵����
 *@param	unDataLen 	���͵���ݳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszDataΪNULL��unDatalen�Ƿ�)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM״̬δȷ��״̬
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 			MODEM �ֶ��˳�
 *@li	\ref NDK_ERR_MODEM_NOPREDIAL "NDK_ERR_MODEM_NOPREDIAL" 		MODEM δ����
 *@li	\ref NDK_ERR_MODEM_SDLCWRITEFAIL "NDK_ERR_MODEM_SDLCWRITEFAIL"      MODEMͬ��дʧ��
 *@li	\ref NDK_ERR_MODEM_NOCARRIER "NDK_ERR_MODEM_NOCARRIER"          MODEM û�ز�
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		��ʱ����
*/
int NDK_MdmWrite(const char *pszData,uint unDataLen);

/**
 *@brief	modem��ݽ��ա�
 *@param	pszData 	���յ����
 *@param	punDataLen 	���յ���ݳ���(�첽ʱ��Ӧ��������ϣ���ȡ����ݳ���)
 *@param	unSenconds	��ʱʱ�䣬��sΪ��λ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszData��punDataLenΪNULL��unSenconds�Ƿ�)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
 *@li	\ref NDK_ERR_MODEM_STATUSUNDEFINE "NDK_ERR_MODEM_STATUSUNDEFINE" 		MODEM״̬δȷ��״̬
 *@li	\ref NDK_ERR_MODEM_QUIT "NDK_ERR_MODEM_QUIT" 			MODEM �ֶ��˳�
 *@li	\ref NDK_ERR_MODEM_NOPREDIAL "NDK_ERR_MODEM_NOPREDIAL" 		MODEM δ����
 *@li	\ref NDK_ERR_MODEM_SDLCWRITEFAIL "NDK_ERR_MODEM_SDLCWRITEFAIL"      MODEMͬ��дʧ��
 *@li	\ref NDK_ERR_MODEM_NOCARRIER "NDK_ERR_MODEM_NOCARRIER"          MODEM û�ز�
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		��ʱ����
*/
int NDK_MdmRead(char  *pszData,uint *punDataLen,uint unSenconds);

/**
 *@brief	modem�ҶϺ���
 *@param	��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_MODEM_SDLCHANGUPFAIL "NDK_ERR_MODEM_SDLCHANGUPFAIL" 		MODEMͬ���Ҷ�ʧ��
 *@li	\ref NDK_ERR_MODEM_ASYNHANGUPFAIL "NDK_ERR_MODEM_ASYNHANGUPFAIL" 		MODEM�첽�Ҷ�ʧ��
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
*/
int NDK_MdmHangup(void);

/**
 *@brief	���modem������
 		1.ͬ������ʱ����ͬ����ʼ��������ͬ������ǰ����ʹ�ã����ͬ�����ź��򷵻�ʧ�ܣ�
 		2.�첽����ʱ����ֻҪ�첽��ʼ����Ϳ���ʹ���������
 *@param
 *@param	��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_MODEM_SDLCCLRBUFFAIL "NDK_ERR_MODEM_SDLCCLRBUFFAIL" 		MODEMͬ���建��ʧ��
 *@li	\ref NDK_ERR_MODEM_ASYNCLRBUFFAIL "NDK_ERR_MODEM_ASYNCLRBUFFAIL" 		MODEM�첽�建��ʧ��
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
*/
int NDK_MdmClrbuf(void);

/**
 *@brief	��ȡmodem���ȡ�
 *@param	punReadLen 	���صĳ���ֵ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(punReadLenΪNULL)
 *@li	\ref NDK_ERR_MODEM_INIT_NOT "NDK_ERR_MODEM_INIT_NOT" 		MODEMδ���г�ʼ��
*/
int NDK_MdmGetreadlen(uint *punReadLen);

/**
 *@brief	modem��λ����
 *@return
 *@li	NDK_OK				�����ɹ�
*/
int NDK_MdmReset(void);

/**
 *@brief	AT���������
 *@param	psCmdStr 	��������
 *@param	pszRespData 	���ص���Ӧ���
 *@param	punLen 	���ص���ݳ���
 *@param	unTimeout 	��ʱʱ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psCmdStr\pszRespData\punLenΪNULL��unTimeoutС��0��������ȴ���52)
 *@li	\ref NDK_ERR_MODEM_PORTWRITEFAIL "NDK_ERR_MODEM_PORTWRITEFAIL" 		MODEM �˿�д���ʧ��
 *@li	\ref NDK_ERR_MODEM_ATCOMMANDERR "NDK_ERR_MODEM_ATCOMMANDERR" 		MODEM AT�������
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 			��ʱ����
*/
int NDK_MdmExCommand(uchar *psCmdStr,uchar *pszRespData,uint *punLen,uint unTimeout);

/** @} */ // modemģ�����

/** @addtogroup ����ģ��
* @{
*/

/**
 *@brief  ����ģ�鳣�������
*/
typedef enum {
    WLM_CMD_E0=0,				/**<�رջ���*/
    WLM_CMD_CSQ=1,				/**<ȡ�ź�ֵ*/
    WLM_CMD_CREG=2,				/**<����ע��״̬*/
    WLM_CMD_CPIN=3,				/**<��ѯ��PIN*/
    WLM_CMD_CPIN0=4,			/**<����PIN��*/
    WLM_CMD_CGATT0=5,			/**<ע��GPRS����*/
    WLM_CMD_CGATT1=6,			/**<ע��GPRS����*/
    WLM_CMD_DIAL=7,				/**<����*/
    WLM_CMD_D2=8,				/**<����Ӳ���ҶϹ���*/
    WLM_CMD_COPS=9,				/**<ע������*/
    WLM_CMD_CGMR=10,			/**<ȡģ��汾*/
    WLM_CMD_CGSN,         		/**<��ȡGSN��(imei��meid)*/
    WLM_CMD_CCID,				/**<��ȡ����SIM�����к�*/
    WLM_CMD_END,          		/**<���������������������֮ǰλ��*/
    WLM_CMD_UNDEFINE=1024,		/**<�б���δ����*/
    WLM_CMD_PUBLIC=255
} EM_WLM_CMD;

/**
 *@brief  ATָ����ݰ�
*/
typedef struct NDK_WL_ATCMD{
		EM_WLM_CMD AtCmdNo;		/**<�����*/
		char *pcAtCmd;			/**<�����ִ�*/
		char *pcAddParam;		/**<���Ӳ���*/
}ST_ATCMD_PACK;

/**
 *@brief  ����ģ�鷵��״̬����
*/
typedef enum{
    WLM_STATUS_UNTYPED=4,	/**<δ���巵��*/
    WLM_STATUS_NO_CARRIER=3,/**<���ز�*/
    WLM_STATUS_RING=2,		/**<RING��*/
    WLM_STATUS_CONNECT=1,	/**<���Ӵ�*/
    WLM_STATUS_OK=0,				/**<�ɹ�*/
    WLM_STATUS_ERROR=-1,			/**<����*/
    WLM_STATUS_RET_ERROR=-114,		/**<���س���*/
}EM_WLM_STATUS;

/**
 *@brief  ����CCID,IMSI,IMEI
*/
typedef enum{
    WLM_INFO_CCID=0,					/**<����CCIDö��*/
    WLM_INFO_IMSI=1,		/**<����IMSIö��*/
    WLM_INFO_IMEI=2,		/**<����IMEIö��*/
    WLM_INFO_UNDEFINE=1024,	/**<δ���巵��*/
}EM_WLM_TYPE_INFO;

/**
 *@brief  ��վ��Ϣ
*/
typedef struct station_info
{
	uint unMCC;  /**<�ƶ���Һ�*/
	uint unMNC;	/**<�ƶ������*/
	//float longitude;
	//float latitude;
	uint unLac;/**<λ�������*/
	uint unCi;/**<С�����*/
	int ndbm;/**<�ź�ǿ��*/
}ST_STATION_INFO;

typedef struct
{
	ST_STATION_INFO stMainStation;/**<GPRSģ�鸽������վ*/
	ST_STATION_INFO stNeighborStation[6];/**<GPRS����ģ����ٽ��վ*/
}ST_GPRS_STATION_INFO;

typedef struct
{
	uint unMCC;		/**<�ƶ���Һ�*/
	uint unMNC;		/**<�ƶ������*/
	uint unBandClass;/**<�������*/
	uint unChannel;		/**<Ƶ��*/
	uint unBid;		/**<ϵͳʶ����*/
	uint unSid;		/**<����ʶ����*/
	uint unNid;		/**<�����е�ĳһ��С��*/
}ST_CDMA_STATION_INFO;

typedef union
{
	ST_CDMA_STATION_INFO CDMA_STATION;	/**<CDMA��վ*/
	ST_GPRS_STATION_INFO GPRS_STATION;	/**<GPRS��վ*/
}MODULE_UNION_INFO;

typedef enum 
{
	MODULE_TYPE_GPRS,		/**<GPRSģ��*/
	MODULE_TYPE_CDMA,		/**<CDMAģ��*/
	MODULE_TYPE_WCDMA,		/**<WCDMAģ��*/
	MODULE_TYPE_TDSCDMA,	/**<TDSCDMAģ��*/
}EM_MODULE_TYPE;

typedef struct
{
	EM_MODULE_TYPE emModuleType;		/**<ģ������*/
	MODULE_UNION_INFO ModuleStationInfo;/**<ģ���վ��Ϣ*/
}ST_MOBILE_STATION_INFO;

/**
 *@brief	����MODEM��Ӳ����λ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		�����ʧ��
*/
int NDK_WlModemReset(void);

/**
 *@brief	�ر�����MODEMģ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�ʧ��
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		�����ʧ��
*/
int NDK_WlModemClose(void);

/**
 *@brief	����MODEM��ʼ�����л����ڵ����߲��ж�ģ��ATָ���ܷ�����Ӧ�����SIM��
 *@param	nTimeout	��ʱʱ�䣬��λMS
 *@param	pszPinPassWord	PIN��
 *@retval	pemStatus	ִ�гɹ���������״̬��ʧ�ܷ��� NDK_FAIL	ʧ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pemStatusΪNULL,nTimeout��ʱ����Ƿ�)
 *@li	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW" 	�������
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 	��ʱ����
 *@li	\ref NDK_ERR_PIN_LOCKED "NDK_ERR_PIN_LOCKED" 	SIM������
 *@li	\ref NDK_ERR_PIN "NDK_ERR_PIN" 	SIM���������
 *@li	\ref NDK_ERR_PIN_UNDEFINE "NDK_ERR_PIN_UNDEFINE" 	SIM��δ�������
 *@li	\ref NDK_ERR_NO_SIMCARD "NDK_ERR_NO_SIMCARD" 	��SIM��
*/
int NDK_WlInit(int nTimeout,const char *pszPinPassWord,EM_WLM_STATUS *pemStatus);


/**
 *@brief	��ȡ����MODEM�ź�ǿ��
 *@retval	pnSq	ȡ�����ź�ǿ�ȣ�ȡ����ֵ	0-31 Ϊ�ɹ���99	Ϊδ֪,-1 Ϊʧ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnSqΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
*/
int NDK_WlModemGetSQ(int *pnSq);


/**
 *@brief	������ģ�鷢��ATָ��ͽ��շ�����Ӧ
 *@param	pstATCmdPack	ATָ����ݰ�
 *@param	unMaxLen	������󳤶ȣ�pszOutput������ȣ���=0ʱʹ��ȱʡ����1024��
 *@param	unTimeout	���ʱʱ�䣬��λ��MS
 *@retval	pszOutput	�����
 *@retval	pemStatus	ִ�гɹ���������״̬��ʧ�ܷ��� WLM_STATUS_ERROR	ʧ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pemStatus��pstATCmdPack��pszOutputΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�豸δ�򿪻��ʧ��)
 *@li	\ref NDK_ERR_USB_LINE_UNCONNECT "NDK_ERR_USB_LINE_UNCONNECT" 		USB��δ��
 *@li	\ref NDK_ERR_WRITE "NDK_ERR_WRITE" 				д�ļ�ʧ��
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
 *@li	\ref NDK_ERR_EMPTY "NDK_ERR_EMPTY" 			���ؿմ�
 *@li	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW" 	�������
*/
int NDK_WlSendATCmd(const ST_ATCMD_PACK *pstATCmdPack,char *pszOutput,uint unMaxLen,uint unTimeout,EM_WLM_STATUS *pemStatus);

/**
 *@fn 		NDK_WlGetInfo(EM_WLM_TYPE_INFO emType,char *pszValue,uint unBufLen);
 *@brief 		��ȡ����CCID,IMSI,IMEI
 *@param		emtype		����CCID,IMSI,IMEI�ĵ�ö��
 *@param		pszValue	����CCID,IMSI,IMEI����Ϣ
 *@param		nBufLen		���������>=21,CCID�ܳ�20λ����0-F���,IMSI�ܳ�������15����0-9��ɣ�IMEI�ܳ�������20����0-F���.
 *@return	
 *@li   NDK_OK			   �����ɹ�
 *@li   \ref NDK_ERR_PARA "NDK_ERR_PARA" 	   ����Ƿ�(emType�Ƿ���pszValueΪNULL)
 *@li   \ref NDK_ERR "NDK_ERR" 			   ����ʧ��
*/

int NDK_WlGetInfo(EM_WLM_TYPE_INFO emType,char *pszValue,uint unBufLen);

/**
 *@brief	��ȡ����վ�����ڻ�վ����Ϣ��������Ӫ�̣�λ������룬С���

 *@retval	pstStationInfo	��վ��Ϣ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	����Ƿ�(pstStationInfoΪNULL)
*/
int NDK_WlGetStationInfo(ST_MOBILE_STATION_INFO * pstStationInfo);

/**
 *@brief	�ر���Ƶ����δ֧�֣�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_WlCloseRF(void);

/**
 *@brief	ѡ��SIM������δ֧�֣�
 *@param	ucSimNo	����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_WlSelSIM(uchar ucSimNo);


/** @} */ // ����ģ�����

/** @addtogroup SocketͨѶ
* @{
*/


/**
 *@brief	��TCPͨѶͨ��
 *@retval	punFd	����TCPͨ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(punFdΪNULL)
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		�޷�����
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 		��Ч����
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		���䳬ʱ
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		��Ч��ַ
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		û������
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		Э�����
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		�������
*/
int NDK_TcpOpen(uint *punFd);

/**
 *@brief	�ر�TCPͨѶͨ��
 *@param	unFd	Ҫ�رյ�TCPͨ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��
*/
int NDK_TcpClose(uint unFd);

/**
 *@brief	�ȴ�TCP�رճɹ���һ���رռ�ʱ�˳�������NDK_TcpClose()�󣬱��������øú���ȷ��TCP��·��ȫ�ر�
 *@param	unFd	TCPͨ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(unFd�Ƿ���)
*/
int NDK_TcpWait(uint unFd);

/**
 *@brief	�󶨱��˵�IP��ַ�Ͷ˿ں�
 *@param	unFd	TCPͨ�����
 *@param	pszMyIp	Դ��ַ
 *@param	usMyPort	Դ�˿�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	����Ƿ�(pszMyIpΪNULL)
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 	��Ч����(unFd�Ƿ���Դ��ַ���Ϸ�)
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		�޷�����
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		���䳬ʱ
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		��Ч��ַ
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		û������
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		Э�����
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		�������
*/
int NDK_TcpBind(uint unFd, const char *pszMyIp, ushort usMyPort);

/**
 *@brief	���ӷ�����
 *@param	unFd	TCPͨ�����
 *@param	pszRemoteIp	Զ�̵�ַ
 *@param	usRemotePort	Զ�̶˿�
 *@param	unTimeout	Զ�����ӳ�ʱʱ�䣬��λΪ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	����Ƿ�(pszRemoteIpΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR"          	����ʧ��
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT"          	��ʱ����
 *@li	\ref NDK_ERR_LINUX_TCP_TIMEOUT "NDK_ERR_LINUX_TCP_TIMEOUT"          TCPԶ�̶˿ڴ���
 *@li	\ref NDK_ERR_LINUX_TCP_REFUSE "NDK_ERR_LINUX_TCP_REFUSE"          TCPԶ�̶˿ڱ��ܾ�
 *@li	\ref NDK_ERR_LINUX_TCP_NOT_OPEN "NDK_ERR_LINUX_TCP_NOT_OPEN"          TCP���δ�򿪴���
*/
int NDK_TcpConnect(uint unFd, const char *pszRemoteIp, ushort usRemotePort, uint unTimeout);

/**
 *@brief	�������������
 *@param	unFd	TCPͨ�����
 *@param	nBacklog	�ȴ����Ӷ��е���󳤶�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_TCP_ALLOC "NDK_ERR_TCP_ALLOC" 		�޷�����
 *@li	\ref NDK_ERR_TCP_PARAM "NDK_ERR_TCP_PARAM" 		��Ч����
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 		���䳬ʱ
 *@li	\ref NDK_ERR_TCP_INVADDR "NDK_ERR_TCP_INVADDR" 		��Ч��ַ
 *@li	\ref NDK_ERR_TCP_CONNECT "NDK_ERR_TCP_CONNECT" 		û������
 *@li	\ref NDK_ERR_TCP_PROTOCOL "NDK_ERR_TCP_PROTOCOL" 		Э�����
 *@li	\ref NDK_ERR_TCP_NETWORK "NDK_ERR_TCP_NETWORK" 		�������
*/
int NDK_TcpListen(uint unFd, int nBacklog);

/**
 *@brief	������������
 *@param	unFd	TCPͨ�����
 *@param	pszPeerIp	��������ʵ��ĵ�ַ
 *@param	usPeerPort	��������ʵ��Ķ˿�
 *@retval	punNewFd	����TCPͨ�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszPeerIp/punNewFdΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(����accept()ʧ�ܷ���)
*/
int NDK_TcpAccept(uint unFd, const char *pszPeerIp, ushort usPeerPort, uint *punNewFd);

/**
 *@brief	�������
 *@param	unFd	TCPͨ�����
 *@param	pvInbuf	���ͻ�����ĵ�ַ
 *@param	unLen	������ݵĳ���
 *@param	unTimeout	��ʱʱ�䣬��λΪ��
 *@retval	punWriteLen	����ʵ�ʷ��ͳ���,���ΪNULL�򲻽���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pInbufΪNULL)
 *@li	\ref NDK_ERR_TCP_SEND "NDK_ERR_TCP_SEND" 		���ʹ���(����send()ʧ�ܷ���)
*/
int NDK_TcpWrite(uint unFd, const void *pvInbuf, uint unLen, uint unTimeout, uint *punWriteLen);
/**
 *@brief	�������
 *@param	unFd	TCPͨ�����
 *@param	unLen	������ݵĳ���
 *@param	unTimeout	��ʱʱ�䣬��λΪ��
 *@retval	pvOutBuf	���ջ�����ĵ�ַ
 *@retval	punReadLen	����ʵ�ʷ��ͳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pvOutBuf/punReadLenΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_TCP_TIMEOUT "NDK_ERR_TCP_TIMEOUT" 			��ʱ����
 *@li	\ref NDK_ERR_TCP_RECV "NDK_ERR_TCP_RECV" 			���մ���
*/
int NDK_TcpRead(uint unFd, void *pvOutBuf, uint unLen, uint unTimeout, uint *punReadLen);


/** @} */ // SocketͨѶģ�����


/** @addtogroup PPPͨѶ
* @{
*/

/**
 *@brief  �����ӱ�ʶ�ĺ궨��
*/
#define STATUS_OPEN			0x03
#define STATUS_CLOSE		0x02
#define LCP_ECHO_OPEN  STATUS_OPEN  /**<��ʾECHO�ز���⿪��״̬*/
#define LCP_ECHO_CLOSE  STATUS_CLOSE
#define LCP_DCD_OPEN  (STATUS_OPEN<<2)/**<��ʾDCD����״̬*/
#define LCP_DCD_CLOSE  (STATUS_CLOSE<<2)
#define LCP_PPP_KEEP  (STATUS_OPEN<<4)/**<��ʾPPP������ά�ֿ���״̬*/
#define LCP_PPP_UNKEEPED  (STATUS_CLOSE<<4)

/**
 *@brief  PPP����״̬����
*/
typedef enum {
    PPP_STATUS_DISCONNECT=0,		/**<PPPδ����*/
    PPP_STATUS_CONNECTING=2,		/**<PPP��������*/
    PPP_STATUS_CONNECTED=5,			/**<PPP������*/
    PPP_STATUS_DISCONNECTING=6		/**<���ڹҶ�*/
} EM_PPP_STATUS;

/**
 *@brief  PPP���ò���
*/
typedef struct {
    int nDevType;			/**<���ͣ����������MODEM��������MODEM��0��ʾ���ߣ�1��ʾ����*/
    unsigned int nPPPFlag;	/**<�Ƿ�֧��ά�ֳ����ӱ�ʶ�������Ҫ�������Ӧ�ĺ궨��*/
    char szApn[64];			/**<APN����*/
    char szDailNum[32];		/**<���ź���,ǰ�治��D*/
    int (*ModemDial)(void);/**<���ź���*/
    unsigned int PPPIntervalTimeOut; /**<ά�ֳ����ӵ���ݰ��͵�ʱ����,<30S��������г����ӵ�ά��*/
    unsigned char nMinSQVal;	/**<��ʼ��ʱ���������С���ź�ֵ*/
    char szPin[31];				/**<SIM��PIN��*/
    char nPPPHostIP[20];		/**<ά�ֳ�������ҪPIN������IP*/
} ST_PPP_CFG;

/**
 *@brief	����PPP����
 *@param	pstPPPCfg	ppp����ṹ(�豸��������ȱʡʱΪ����)
 *@param	unValidLen	����pstPPPCfg����Ч����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pstPPPCfgΪNULL)
 *@li	\ref NDK_ERR_PPP_DEVICE "NDK_ERR_PPP_DEVICE" 		PPP��Ч�豸
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		�����ڴ����
 *@li	\ref NDK_ERR_PPP_PARAM "NDK_ERR_PPP_PARAM" 		PPP�������
*/
int NDK_PppSetCfg(ST_PPP_CFG *pstPPPCfg, uint unValidLen);

/**
 *@brief	PPP����
 *@param	pszUserName	�û���
 *@param	pszPassword	����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszUserName/pszPasswordΪNULL)
 *@li	\ref NDK_ERR_PPP_OPEN "NDK_ERR_PPP_OPEN" 	PPP�Ѵ�
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		�����ڴ����
 *@li	\ref NDK_ERR_PPP_DEVICE "NDK_ERR_PPP_DEVICE" 		PPP��Ч�豸
*/
int NDK_PppDial(const char *pszUserName,const char *pszPassword);

/**
 *@brief	PPP�Ҷ�
 *@param	nHangupType	�Ҷ����� 0 ������Ҷ� 1 ����Ҷ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(nHangupType�Ƿ�)
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT" 		��ʱ����
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 		�����ڴ����
*/
int NDK_PppHangup(int nHangupType);

/**
*@brief	��ȡPPP״̬
 *@retval	pemStatus	����PPP״̬,ΪNULL��ִ�иò���
 *@retval	pnErrCode	����PPP���Ӵ���,ΪNULL��ִ�иò���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 	        ����Ƿ�(pemStatus/pnErrCodeΪNULL)
 *@li	\ref NDK_ERR_SHM "NDK_ERR_SHM" 				�����ڴ����
*/
int NDK_PppCheck(EM_PPP_STATUS *pemStatus, int *pnErrCode);

/**
 *@brief	��ȡ���ص�ַ�������ַ
 *@retval	pulLocalAddr	���ر��ص�ַ,ΪNULL�򲻽���
 *@retval	pulHostAddr	���������ַ,ΪNULL�򲻽���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pulLocalAddr/pulHostAddrΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��ȡPPP��ַ��Ϣ����)
*/
int NDK_PppGetAddr(ulong *pulLocalAddr, ulong *pulHostAddr);

/**
 *@brief	��һ�����ʮ���Ƶ�IPת����һ������������
 *@param	pszIp	IP��ַ�ַ�
 *@retval	pulIpAddr	����ת����ĳ�������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszIp/pulIpAddrΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(��ַ�Ƿ�)
*/
int NDK_PppAddrChange(register const char *pszIp, ulong *pulIpAddr);

/** @} */ // PPPͨѶģ�����

/** @addtogroup ��̫��ͨѶ
* @{
*/

typedef enum{
                COMM_TYPE_ETH,                  /**<��̫��*/
                COMM_TYPE_WIFI,                 /**<WIFI*/
                COMM_TYPE_PPP,                  /**<PPP*/
                COMM_TYPE_BTH,                  /**<����*/
                COMM_TYPE_UNKNOW,          /**<δ֪*/
}EM_COMM_TYPE;






/**
 *@brief	����ṩ���������ͻ�ȡ�����ַ,�����ַ����ΪNULL���ز������
 *@param emComtype	Ҫ��ȡ�����ַ����������
 *@retval	pszIp	����IP��ַ,ΪNULL��ȡIP��ַ
 *@retval	pszGateway	������ص�ַ,ΪNULL��ȡGateway��ַ
 *@retval	pszMask	������������,ΪNULL��ȡMask��ַ
 *@retval	pszDns	DNS������IP��ַ,ΪNULL��ȡDNS��ַ,һ����ȡ������DNS,֮���Էֺŷ�';'����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(��ַȫΪNULL����emComtype����)
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		��ȡ���ص�ַ��������������ʧ��
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(��ȡDNSʧ��)
*/
int NDK_NetGetAddr(EM_COMM_TYPE emComtype,char *pszIp, char *pszMask, char *pszGateway, char *pszDns);
/**
 *@brief	���������ַ,����ΪNULL���ز������(���ú����ԭ�Ѷ�̬����ĵ�ַ,Ҳ�ᱻ�޸�)
 *@param	pszIp	����IP ��ַ�ַ����ָ��,ΪNULL����IP��ַ.��֧��IPV4Э��
 *@param	pszMask	�������������ַ����ָ��,ΪNULL����Mask��ַ.��֧��IPV4Э��
 *@param	pszGateway	������ص�ַ�ַ����ָ��,ΪNULL����Gateway��ַ.��֧��IPV4Э��
 *@param 	pszDns	����DNS������IP��ַ,ΪNULL����DNS��ַ,��������3��DNS,֮���Էֺŷ�';'����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(��ַΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����
 *@li	\ref NDK_ERR "NDK_ERR" 	����ʧ��
 *@li	\ref NDK_ERR_NET_ADDRILLEGAL "NDK_ERR_NET_ADDRILLEGAL" 	��ȡ��ַ��ʽ����
*/
int NDK_EthSetAddress(const char *pszIp, const char *pszMask, const char *pszGateway, const char *pszDns);

/**
 *@brief	��ȡ����MAC��ַ
 *@retval	pszMacAddr	����MAC��ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszMacAddrΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(��ȡmac��ַʧ��)
*/
int NDK_EthGetMacAddr(char *pszMacAddr);

/**
 *@brief	��ȡ�����ַ,����ΪNULL���ز������
 *@retval	pszIp	����IP��ַ,ΪNULL��ȡIP��ַ
 *@retval	pszGateway	������ص�ַ,ΪNULL��ȡGateway��ַ
 *@retval	pszMask	������������,ΪNULL��ȡMask��ַ
 *@retval	pszDns	DNS������IP��ַ,ΪNULL��ȡDNS��ַ,һ����ȡ������DNS,֮���Էֺŷ�';'����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		��ȡ���ص�ַ����������ʧ��
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(��ȡDNSʧ��)
*/
int NDK_EthGetNetAddr(char *pszIp, char *pszMask, char *pszGateway, char *pszDns);


/**
 *@brief	ʹ��dhcp��ȡ�����ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��(��̫�������ʧ�ܵ�)
 *@li	\ref NDK_ERR_NET_GETADDR "NDK_ERR_NET_GETADDR" 		��ȡ���ص�ַ����������ʧ��
 *@li	\ref NDK_ERR_NET_ADDRILLEGAL "NDK_ERR_NET_ADDRILLEGAL" 	��ȡ��ַ��ʽ����
 *@li	\ref NDK_ERR_NET_GATEWAY "NDK_ERR_NET_GATEWAY" 	    ��ȡ��ص�ַʧ��
*/
int NDK_NetDHCP(void);


/**
 *@brief	����PING����
 *@param	pszIp	����IP��ַ�ַ����ָ��,����Ϊ��ָ��.��֧��IPV4Э��
 *@param	nTimeout	��ʱʱ��,��λΪ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszIpΪNULL��pszIp���ȴ���15)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��
*/
int NDK_NetPing(char *pszIp, uint nTimeout);


/**
 *@brief	�������
 *@param	pszDnsIp	�����õ���IP��ַ
 *@param	pszDnsName	����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszDnsIp/pszDnsNameΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(����gethostbyname()ʧ�ܷ���)
*/
int NDK_GetDnsIp(char *pszDnsIp,const char *pszDnsName);

/**
 *@brief �������ת��ʹ�õ�ͨѶ�ӿ�
 *@param	 emCommType		ͨѶ��ʽ
 *@param     pszDestIP		���÷������ĵ�ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszDestIPΪNULL)
 *@li	\ref NDK_ERR_NET_UNKNOWN_COMMTYPE "NDK_ERR_NET_UNKNOWN_COMMTYPE"                δ֪ͨѶ��ʽ����
 *@li	\ref NDK_ERR_NET_INVALIDIPSTR "NDK_ERR_NET_INVALIDIPSTR" 	��ЧIP�ַ�
 *@li	\ref NDK_ERR_NET_UNSUPPORT_COMMTYPE "NDK_ERR_NET_UNSUPPORT_COMMTYPE" 	��֧�ֵ�ͨ������
 */

int NDK_NetAddRouterTable(EM_COMM_TYPE emCommType,char *pszDestIP);

/** @} */ // ��̫��ͨѶģ�����


/** @addtogroup �ſ�
* @{
*/

/**
 *@brief	�򿪴ſ��豸
 *@param	��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(mag�豸�ڵ��Ѵ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	���豸�ļ�����(�򿪴ſ��豸�ļ�����)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(�ſ���ӿڵ���ʧ�ܷ���)
*/
int NDK_MagOpen(void);

/**
 *@brief	�رմſ��豸
 *@param	��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(�ſ��豸δ�򿪡�����close()ʧ�ܷ��ء������ʧ��)
*/
int NDK_MagClose(void);

/**
 *@brief	��λ��ͷ
 *@details	 ��λ��ͷ�����ſ����������
 *@param	��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(�ſ�δ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(�ſ���ӿ�MAG_IOCS_RESET����ʧ�ܷ���)
*/
int NDK_MagReset(void);

/**
 *@brief	�ж��Ƿ�ˢ��
 *@retval	psSwiped	1----��ˢ��    0-----δˢ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(psSwiped�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(�ſ��豸δ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			����ô���(�ſ���ӿ�MAG_IOCG_SWIPED����ʧ�ܷ���)
*/
int NDK_MagSwiped(uchar * psSwiped);

/**
 *@brief	��ȡ�ſ��������1��2��3�ŵ������
 *@details	��\ref MagSwiped "MagSwiped()"�������ʹ�á������Ҫĳ�ŵ����,���Խ��ôŵ���Ӧ��ָ����ΪNULL,��ʱ����������ôŵ������
 *@retval	pszTk1	�ŵ�1
 *@retval	pszTk2	�ŵ�2
 *@retval	pszTk3	�ŵ�3
 *@retval	pnErrorCode	�ſ��������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��(�ſ��豸δ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			����ô���(�ſ���ӿڵ���ʧ�ܷ���)
 *@li	\ref NDK_ERR_NOSWIPED "NDK_ERR_NOSWIPED" 		�޴ſ�ˢ����¼
*/
int NDK_MagReadNormal(char *pszTk1, char *pszTk2, char *pszTk3, int *pnErrorCode);

/**
 *@brief	��ȡ�ſ��������1��2��3�ŵ���ԭʼ���
 *@details	��\ref MagSwiped "MagSwiped()"�������ʹ��,�����Ҫĳ�ŵ����,���Խ��ôŵ���Ӧ��ָ����ΪNULL,��ʱ����������ôŵ������
 *@retval	pszTk1	�ŵ�1
 *@retval	pusTk1Len	�ŵ�1��ݳ���
 *@retval	pszTk2	�ŵ�2
 *@retval	pusTk2Len	�ŵ�2��ݳ���
 *@retval	pszTk3	�ŵ�3
 *@retval	pusTk3Len	�ŵ�3��ݳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(pszTk2/pszTk3/pszTk1ΪNULL������ΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(�ſ��豸δ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			����ô���(�ſ���ӿڵ���ʧ�ܷ���)
*/
int NDK_MagReadRaw(uchar *pszTk1, ushort* pusTk1Len, uchar *pszTk2, ushort* pusTk2Len,uchar *pszTk3, ushort* pusTk3Len );



typedef enum {
    BIT_DATA = 1,
    WIDE_DATA = 2,
} ENUM_MAG_DATA_TYPE;
typedef enum {
    TK1 = 1,
    Tk2 = 2,
    TK3 = 3,
} ENUM_MAG_TRACK;

/**
 *@brief    ��ȡ�ſ��������1��2��3�ŵ��Ķ�������ݻ���ʱ�������
 *@retval   type  ������ͣ���������ݻ�ʱ�������
 *@retval   track   ��ȡ�Ĵŵ�
 *@retval   offset  ƫ����
 *@retval   unLen   ��ȡ�ĳ���
 *@retval   tkdata  �洢��ݵ�buffer
 *@retval   pnReadlen   ����ʵ�ʶ�������ݳ���
 *@return
 *@li   NDK_OK              �����ɹ�
 *@li   \ref NDK_ERR "NDK_ERR"              ����ʧ��(�ſ��豸δ��)
*/
int NDK_MagReadRawData(ENUM_MAG_DATA_TYPE type, ENUM_MAG_TRACK track, uint offset,
        uint unLen, uchar *tkdata, uint *pnReadlen);

/** @} */ // �ſ�ģ�����

/** @addtogroup IC��
* @{
*/

typedef enum{
  ICTYPE_IC,  /**<�Ӵ�ʽIC��*/
  ICTYPE_SAM1, /**<SAM1��*/
  ICTYPE_SAM2, /**<SAM2��*/
  ICTYPE_SAM3, /**<SAM3��*/
  ICTYPE_SAM4, /**<SAM4��*/
  ICTYPE_M_1, /**<at24c32*/
  ICTYPE_M_2, /**<sle44x2*/
  ICTYPE_M_3, /**<sle44x8*/
  ICTYPE_M_4, /**<at88sc102*/
  ICTYPE_M_5, /**<at88sc1604*/
  ICTYPE_M_6, /**<at88sc1608*/
  ICTYPE_ISO7816, /**<ISO7816 standard*/
  ICTYPE_M_7, /**<at88sc153*/
  ICTYPE_M_1_1,/*<at24c01 */
  ICTYPE_M_1_2,/*<at24c02 */
  ICTYPE_M_1_4,/*<at24c04 */
  ICTYPE_M_1_8,/*<at24c08 */
  ICTYPE_M_1_16,/*<at24c16 */
  ICTYPE_M_1_32,/*<at24c32 */
  ICTYPE_M_1_64,/*<at24c64 */
}EM_ICTYPE;

/**
 *@brief	��ȡ�����汾��
 *@retval 	pszVersion   ���������汾��,Ҫ�󻺳��С������16�ֽڡ�
 *@return 	
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(pszVersionΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			����ô���(IC��ӿ�ioctl_getvision����ʧ�ܷ���)
*/
int  NDK_IccGetVersion(char *pszVersion);

/**
 *@brief	���ÿ�����
 *@param	emIcType  	������(�ο�\ref EM_ICTYPE "EM_ICTYPE")��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(emIctype�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	����ô���(IC��ӿ�ioctl_SetICType����ʧ�ܷ���)
*/
int NDK_IccSetType(EM_ICTYPE emIcType);

/**
 *@brief	��ȡ������
 *@retval 	pemIcType 	������(�ο�\ref EM_ICTYPE "EM_ICTYPE")��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(pemIcType�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	����ô���(IC��ӿ�ioctl_GetICType����ʧ�ܷ���)
*/
int NDK_IccGetType(EM_ICTYPE *pemIcType);

/**
 *@brief	��ȡ��Ƭ״̬
 *@retval 	pnSta   bit0�����IC��1�Ѳ忨��Ϊ��1��������Ϊ��0��\n
 *					bit1�����IC��1���ϵ磬Ϊ��1��������Ϊ��0��\n
 *                  bit2������������ֵ��0��\n
 *                  bit3������������ֵ��0��\n
 *					bit4�����SAM��1���ϵ磬Ϊ��1��������Ϊ��0��\n
 *					bit5�����SAM��2���ϵ磬Ϊ��1��������Ϊ��0��\n
 *					bit6�����SAM��3���ϵ磬Ϊ��1��������Ϊ��0��\n
 *					bit7�����SAM��4���ϵ磬Ϊ��1��������Ϊ��0��\n
 *��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			�������(pnStaΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
*/
int NDK_IccDetect(int *pnSta);


/**
 *@brief	�ϵ�
 *@param	emIcType 	������(�ο�\ref EM_ICTYPE "EM_ICTYPE")
 *@retval ��psAtrBuf  	��ʾATR���
 *@retval   pnAtrLen  	��ʾATR��ݵĳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(psAtrBuf/pnAtrLenΪNULL��emIcType�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 				����ʧ��(MEMORY�����ʧ��)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		�豸�ļ���ʧ��(IC���豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_ICC_CARDNOREADY_ERR "NDK_ERR_ICC_CARDNOREADY_ERR" 	��δ׼����
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	����ô���
*/
int NDK_IccPowerUp (EM_ICTYPE emIcType, uchar *psAtrBuf,int *pnAtrLen);

/**
 *@brief	�µ�
 *@param	emIcType 	������(�ο�\ref EM_ICTYPE "EM_ICTYPE")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(emIcType�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	����ô���
*/
int NDK_IccPowerDown(EM_ICTYPE emIcType);

/**
 *@brief	IC������
 *@param	emIcType	������(�ο�\ref EM_ICTYPE "EM_ICTYPE")
 *@param	nSendLen	������ݳ���
 *@param	psSendBuf	���͵����
 *@retval 	pnRecvLen   ������ݳ���
 *@retval 	psRecvBuf   ���յ����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 			����Ƿ�(psSendBuf/pnRecvLen/psRecvBufΪNULL��nSendLenС��0��emIcType�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 		���豸�ļ�����(��IC���豸�ļ�ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 	����ô���
*/
int NDK_Iccrw(EM_ICTYPE emIcType, int nSendLen,  uchar *psSendBuf, int *pnRecvLen,  uchar *psRecvBuf);

/** @} */ // IC��ģ�����

/** @addtogroup ��Ƶ��
* @{
*/
typedef enum {
	RFID_Autoscan=		0x00,
	RFID_RC531=			0x01,
	RFID_PN512=			0x02,
	RFID_No=			0xFF,
}EM_RFID;

/**
 *@brief	��ȡ��Ƶ��汾��
 *@param	pszVersion	���ص���汾���ַ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszVersionΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_GETVISION����ʧ�ܷ���)
*/
int  NDK_RfidVersion(uchar *pszVersion);


/**
 *@brief	��Ƶ�ӿ�������ʼ�����������ж������Ƿ�װ���ɹ���
 *@retval 	psStatus  ���䱸��
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pszVersionΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_INIT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_RFID_INITSTA "NDK_ERR_RFID_INITSTA" 	�ǽӴ���-��Ƶ�ӿ��������ϻ���δ����
*/
int NDK_RfidInit(uchar *psStatus);


/**
 *@brief	����Ƶ���
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_RFOPEN����ʧ�ܷ���)
*/
int NDK_RfidOpenRf(void);


/**
 *@brief	�ر���Ƶ������ɽ��͹��Ĳ�������Ƶ������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_RFCLOSE����ʧ�ܷ���)
*/
int NDK_RfidCloseRf(void);


/**
 *@brief	��ȡ��Ƭ״̬(�Ƿ����ϵ磬�����ж��Ƿ������)
 *@return
 *@li	NDK_OK			�����ɹ�(���ϵ�ɹ���æ״̬)
 *@li	\ref NDK_ERR_RFID_NOTACTIV "NDK_ERR_RFID_NOTACTIV" 	�ǽӴ���-δ����(δ�ϵ�ɹ�����æ״̬)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_WORKSTATUS����ʧ�ܷ���)
*/
int NDK_RfidPiccState(void);


/**
 *@brief	���ó�ʱ����
 *@param����ucTimeOutCtl ֵ���ǲ�ִ�У�ֵ��Ϊ����ִ��
 *@retval
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_TIMEOUTCTL����ʧ�ܷ���)
*/
int  NDK_RfidTimeOutCtl(uchar ucTimeOutCtl);


/**
 *@brief	�豸ǿ������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_SUSPEND����ʧ�ܷ���)
*/
int NDK_RfidSuspend(void);


/**
 *@brief	�豸����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_RESUME����ʧ�ܷ���)
*/
int NDK_RfidResume(void);


/**
 *@brief	����Ѱ������(Ѱ������ǰ����һ�μ��ɣ�M1������ʱ��Ҫ���ó�TYPE-A��ģʽ)
 *@param	ucPiccType = 0xcc����ʾѰ��ʱֻ���TYPE-A�Ŀ�.
 *			   0xcb����ʾѰ��ʱֻ���TYPE-B�Ŀ�.
 *			   0xcd����ʾѰ��ʱ���TYPE-A��TYPE-B�Ŀ�.
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(ucPiccType�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_SETPICCTYPE����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_RfidPiccType(uchar ucPiccType);


/**
 *@brief	��Ƶ��Ѱ��(̽��ˢ�������Ƿ��п�)
 *@retval 	psPiccType 	����Ŀ����� 0xcc=TYPE-A����0xcb=TYPE-B��(�ò�����Ч����NULL���е���).
 *@return
 *@li	NDK_OK			�����ɹ���Ѱ���ɹ���
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_PICCDEDECT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_RfidPiccDetect(uchar *psPiccType);


/**
 *@brief	��Ƶ������(��̽���п��������),�൱��powerup , ���Ľ�����̣�ԭ�����汾����
 *@retval 	psPiccType	����Ŀ����� 0xcc=TYPE-A����0xcb=TYPE-B��
 *@retval	pnDataLen	��ݳ���
 *@retval	psDataBuf	��ݻ�����(A��ΪUID��B��psDataBuf[1]~[4]ΪUID��������Ӧ�ü�Э����Ϣ)
 *@return
 *@li	NDK_OK			����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psPiccType/pnDataLen/psDataBufΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_PICCACTIVATE����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_RfidPiccActivate(uchar *psPiccType, int *pnDataLen,  uchar *psDataBuf);


/**
 *@brief	��Ƶ������(��̽���п��������),�൱��powerup ,��EMV L1 MAINLOOP���̡�
 *@retval 	psPiccType	����Ŀ����� 0xcc=TYPE-A����0xcb=TYPE-B��
 *@retval	pnDataLen	��ݳ���
 *@retval	psDataBuf	��ݻ�����(A��ΪUID��B��psDataBuf[1]~[4]ΪUID��������Ӧ�ü�Э����Ϣ)
 *@return
 *@li	NDK_OK			����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psPiccType/pnDataLen/psDataBufΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_PICCACTIVATE_EMV����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int  NDK_RfidPiccActivate_EMV(uchar *psPiccType, int *pnDataLen,  uchar *psDataBuf);




/**
 *@brief	�ر���Ƶʹ��ʧЧ����д���������Ӧ��ִ�иò������൱��powerdown ��
 *@param	ucDelayMs	��0��һֱ�ر�RF;����0��ر�ucDelayMs������ٴ�RF��
                      �ر�6��10ms��ʹ��ʧЧ��������û������Ķ���������Ӧ����0�Թر�RF ��
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_PiccDeselect����ʧ�ܷ���)
*/
int NDK_RfidPiccDeactivate(uchar ucDelayMs);


/**
 *@brief	��Ƶ��APDU����,����д�����(�Ѽ���������))
 *@param	nSendLen		���͵������
 *@param	psSendBuf		�����������
 *@retval 	pnRecvLen	������ݳ���
 *@retval	psReceBuf	������ݻ�����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psSendBuf/pnRecvLen/psReceBufΪNULL��nSendLenС��5)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_PICCAPDU����ʧ�ܷ���)
*/
int NDK_RfidPiccApdu(int nSendLen, uchar *psSendBuf, int *pnRecvLen,  uchar *psReceBuf);


/**
 *@brief	M1Ѱ��(Ѱ�����ͱ����Ѿ�����ΪTYPE-A)
 *@param	ucReqCode		0=����REQA, ��0=����WUPA, һ���������Ҫʹ��WUPA
 *@retval 	pnDataLen	 ������ݳ���(2�ֽ�)
 *@retval	psDataBuf	������ݻ�����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnDataLen/psDataBufΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1REQUEST����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Request(uchar ucReqCode, int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1������ͻ(NDK_M1Request�п��������)
 *@retval 	pnDataLen	������ݳ���(UID����)
 *@retval	psDataBuf	������ݻ�����(UID)
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnDataLen/psDataBufΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1ANTI����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Anti(int *pnDataLen, uchar *psDataBuf);


/**
 *@brief	M1������ͻ(NDK_M1Request�п��������),��Զ༶������UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@retval 	pnDataLen	������ݳ���(UID����)
 *@retval	psDataBuf	������ݻ�����(UID)
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnDataLen/psDataBufΪNULL��ucSelcode�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1ANTI����ʧ�ܷ���)
*/
int  NDK_M1Anti_SEL(uchar ucSelCode, int *pnDataLen, uchar *psDataBuf);


/**
*@brief		M1��ѡ��(NDK_M1Anti�ɹ��������)
*@param		nUidLen			uid����
*@param		psUidBuf	     uid��ݻ�����
*@retval 	psSakBuf	    ѡ���������(1�ֽ�SAK)
*@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psUidBuf/psSakBufΪNULL��nUidLen������4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1SELECT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Select(int nUidLen, uchar *psUidBuf, uchar *psSakBuf);


/**
 *@brief	M1��ѡ��(NDK_M1Anti�ɹ��������),��Զ༶������UID .
 *@param	ucSelCode	PICC_ANTICOLL1/PICC_ANTICOLL2/PICC_ANTICOLL3
 *@param	nUidLen		uid����
 *@retval	psUidBuf	uid��ݻ�����
 *@retval	psSakBuf	ѡ���������(1�ֽ�SAK)
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psUidBuf/psSakBufΪNULL��nUidLen������4��ucSelcode�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1SELECT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int  NDK_M1Select_SEL(uchar ucSelCode, int nUidLen, uchar *psUidBuf, uchar *psSakBuf);


/**
 *@brief	M1����֤��Կ�洢(ͬһ����Կ�洢һ�μ���)
 *@param	ucKeyType		��֤��Կ���� A=0x00 ��B=0x01
 *@param	ucKeyNum	 ��Կ���к�(0~15)
 *@param	psKeyData		��Կ,6�ֽ�
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(ucKeyType��ucKeyNum�Ƿ���psKeyDataΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR" 		              ����ʧ��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1KEYSTORE����ʧ�ܷ���)
*/
int NDK_M1KeyStore(uchar ucKeyType,  uchar ucKeyNum, uchar *psKeyData);


/**
 *@brief	M1��װ���Ѵ洢����Կ(ͬһ����Կ����һ�μ���)
 *@param	ucKeyType	��֤��Կ���� A=0x00 ��B=0x01
 *@param    ucKeyNum	 ��Կ���к�(0~15��A/B����16����Կ)
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(ucKeyType��ucKeyNum�Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR" 		              ����ʧ��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1KEYLOAD����ʧ�ܷ���)
*/
int NDK_M1KeyLoad(uchar ucKeyType,  uchar ucKeyNum);


/**
 *@brief	M1�����Ѽ��ص���Կ��֤
 *@param	nUidLen	uid����
 *@param	psUidBuf	uid���(NDK_M1Anti��ȡ��)
 *@param	ucKeyType	��֤��Կ���� A=0x00 ��B=0x01
 *@param    ucBlockNum	Ҫ��֤�Ŀ��(ע��:���������!)
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(ucKeyType��nUidLen�Ƿ���psUidBufΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1INTERAUTHEN����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1InternalAuthen(int nUidLen, uchar *psUidBuf, uchar ucKeyType, uchar ucBlockNum);


/**
 *@brief	M1��ֱ�����KEY��֤
 *@param	nUidLen	uid����
 *@param	psUidBuf	uid���(NDK_M1Anti��ȡ��)
 *@param	ucKeyType	��֤��Կ���� A=0x00 ��B=0x01
 *@param	psKeyData	key(6�ֽ�)
 *@param    ucBlockNum	Ҫ��֤�Ŀ��(ע��:���������!)

 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		              ����ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(ucKeyType��nUidLen�Ƿ���psKeyDataΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1INTERAUTHEN����ʧ�ܷ���)
*/
int NDK_M1ExternalAuthen(int nUidLen, uchar *psUidBuf, uchar ucKeyType, uchar *psKeyData, uchar ucBlockNum);


/**
 *@brief	M1��'��'��ȡ����
 *@param	ucBlockNum	Ҫ���Ŀ��
 *@retval	pnDataLen	��ȡ�Ŀ���ݳ���
 *@retval	psBlockData	�����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnDataLen��psBlockDataΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1READ����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Read(uchar ucBlockNum, int *pnDataLen, uchar *psBlockData);


/**
 *@brief	M1��'��'д����
 *@param	ucBlockNum	Ҫд�Ŀ��
 *@param	pnDataLen	��ȡ�Ŀ���ݳ���
 *@param	psBlockData	�����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(pnDataLen��psBlockDataΪNULL��pnDataLen�Ƿ�)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1WRITE����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Write(uchar ucBlockNum, int *pnDataLen, uchar *psBlockData);


/**
 *@brief	M1��'��'��������
 *@param	ucBlockNum	ִ�����������Ŀ�š�ע�⣺��������ֻ��ԼĴ�����δ����д�����������⣬���Ŀ���ݱ���������/������ʽ��
 *@param	nDataLen	������ݳ���(4�ֽ�)
 *@param	psDataBuf	�������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataBufΪNULL��nDataLen������4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1INCREMENT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Increment(uchar ucBlockNum, int nDataLen, uchar *psDataBuf);


/**
 *@brief	M1��'��'��������
 *@param	ucBlockNum	ִ�м��������Ŀ�š�ע�⣺��������ֻ��ԼĴ�����δ����д�����������⣬���Ŀ���ݱ���������/������ʽ��
 *@param	nDataLen	������ݳ���(4�ֽ�)
 *@param	psDataBuf	�������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		����Ƿ�(psDataBufΪNULL��nDataLen������4)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1DECREMENT����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Decrement(uchar ucBlockNum, int nDataLen, uchar *psDataBuf);


/**
 *@brief	M1����/����������Ĵ��Ͳ���(�Ĵ���ֵ����д�뿨�Ŀ������)
 *@param	ucBlockNum	ִ�м��������Ŀ��
 *@return
*@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1TRANSFER����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Transfer(uchar ucBlockNum);


/**
 *@brief	M1���Ĵ���ֵ�ָ�����(�ָ��Ĵ�����ʼֵ��ʹ֮ǰ����/����������Ч)
 *@param	ucBlockNum		ִ�м��������Ŀ��
 *@return
*@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�RFID_IOCG_M1RESTORE����ʧ�ܷ���)
 *@li	\ref NDK_ERR_MI_NOTAGERR "NDK_ERR_MI_NOTAGERR" 	�ǽӴ���-�޿�,				0xff
 *@li	\ref NDK_ERR_MI_CRCERR "NDK_ERR_MI_CRCERR" 	�ǽӴ���-CRC��,				0xfe
 *@li	\ref NDK_ERR_MI_EMPTY "NDK_ERR_MI_EMPTY" 	�ǽӴ���-�ǿ�,				0xfd
 *@li	\ref NDK_ERR_MI_AUTHERR "NDK_ERR_MI_AUTHERR" 	�ǽӴ���-��֤��,			0xfc
 *@li	\ref NDK_ERR_MI_PARITYERR "NDK_ERR_MI_PARITYERR" 	�ǽӴ���-��ż��,			0xfb
 *@li	\ref NDK_ERR_MI_CODEERR "NDK_ERR_MI_CODEERR" 	�ǽӴ���-���մ����			0xfa
 *@li	\ref NDK_ERR_MI_SERNRERR "NDK_ERR_MI_SERNRERR" 	�ǽӴ���-����ͻ���У���	0xf8
 *@li	\ref NDK_ERR_MI_KEYERR "NDK_ERR_MI_KEYERR" 	�ǽӴ���-��֤KEY��			0xf7
 *@li	\ref NDK_ERR_MI_NOTAUTHERR "NDK_ERR_MI_NOTAUTHERR" 	�ǽӴ���-δ��֤				0xf6
 *@li	\ref NDK_ERR_MI_BITCOUNTERR "NDK_ERR_MI_BITCOUNTERR" 	�ǽӴ���-����BIT��			0xf5
 *@li	\ref NDK_ERR_MI_BYTECOUNTERR "NDK_ERR_MI_BYTECOUNTERR" 	�ǽӴ���-�����ֽڴ�			0xf4
 *@li	\ref NDK_ERR_MI_WriteFifo "NDK_ERR_MI_WriteFifo" 	�ǽӴ���-FIFOд����			0xf3
 *@li	\ref NDK_ERR_MI_TRANSERR "NDK_ERR_MI_TRANSERR" 	�ǽӴ���-���Ͳ�������		0xf2
 *@li	\ref NDK_ERR_MI_WRITEERR "NDK_ERR_MI_WRITEERR" 	�ǽӴ���-д��������			0xf1
 *@li	\ref NDK_ERR_MI_INCRERR "NDK_ERR_MI_INCRERR" 	�ǽӴ���-������������		0xf0
 *@li	\ref NDK_ERR_MI_DECRERR "NDK_ERR_MI_DECRERR" 	�ǽӴ���-������������		0xef
 *@li	\ref NDK_ERR_MI_OVFLERR "NDK_ERR_MI_OVFLERR" 	�ǽӴ���-�������			0xed
 *@li	\ref NDK_ERR_MI_FRAMINGERR "NDK_ERR_MI_FRAMINGERR" 	�ǽӴ���-֡��				0xeb
 *@li	\ref NDK_ERR_MI_COLLERR "NDK_ERR_MI_COLLERR" 	�ǽӴ���-��ͻ				0xe8
 *@li	\ref NDK_ERR_MI_INTERFACEERR "NDK_ERR_MI_INTERFACEERR" 	�ǽӴ���-��λ�ӿڶ�д��		0xe6
 *@li	\ref NDK_ERR_MI_ACCESSTIMEOUT "NDK_ERR_MI_ACCESSTIMEOUT" 	�ǽӴ���-���ճ�ʱ			0xe5
 *@li	\ref NDK_ERR_MI_PROTOCOLERR "NDK_ERR_MI_PROTOCOLERR" 	�ǽӴ���-Э���				0xe4
 *@li	\ref NDK_ERR_MI_QUIT "NDK_ERR_MI_QUIT" 	�ǽӴ���-�쳣��ֹ			0xe2
 *@li	\ref NDK_ERR_MI_PPSErr "NDK_ERR_MI_PPSErr" 	�ǽӴ���-PPS������			0xe1
 *@li	\ref NDK_ERR_MI_SpiRequest "NDK_ERR_MI_SpiRequest" 	�ǽӴ���-����SPIʧ��		0xa0
 *@li	\ref NDK_ERR_MI_NY_IMPLEMENTED "NDK_ERR_MI_NY_IMPLEMENTED" 	�ǽӴ���-�޷�ȷ�ϵĴ���״̬	0x9c
 *@li	\ref NDK_ERR_MI_CardTypeErr "NDK_ERR_MI_CardTypeErr" 	�ǽӴ���-�����ʹ�			0x83
 *@li	\ref NDK_ERR_MI_ParaErrInIoctl "NDK_ERR_MI_ParaErrInIoctl" 	�ǽӴ���-IOCTL�����		0x82
 *@li	\ref NDK_ERR_MI_Para "NDK_ERR_MI_Para" 	�ǽӴ���-�ڲ������			0xa9
*/
int NDK_M1Restore(uchar ucBlockNum);


/**
*@brief	���׿���Ѱ��(���ڲ��ԵȲ����мӿ��ٶ�)
*@param	nModeCode	  =0��Ѱ������0����Ѱ��
*@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 		����ʧ��
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_PiccQuickRequest����ʧ�ܷ���)
*/
int NDK_PiccQuickRequest(int nModeCode);


/**
 *@brief	���ζ�ISO1443-4Э��֧�ֵ��ж�
 *@param	nModeCode	��0��ִ������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_PiccQuickRequest����ʧ�ܷ���)
*/
int NDK_SetIgnoreProtocol(int nModeCode);


/**
*@brief	��ȡ����ISO1443-4Э��֧�ֵ�����
*@retval	pnModeCode	��0��ִ������
*@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pnModeCodeΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_GetIgnoreProtocol����ʧ�ܷ���)
*/
int NDK_GetIgnoreProtocol(int *pnModeCode);


/**
 *@brief	��ȡ��Ƶ�ӿ�оƬ����
 *@retval	pnRfidType	��\ref EM_RFID "EM_RFID"
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(pnRfidTypeΪNULL)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_Get_rfid_ic_type����ʧ�ܷ���)
*/
int  NDK_GetRfidType(int *pnRfidType);

/**
 *@brief	��ȡA����ATS
 *@param
 *@retval
 *			pnDatalen:	��ݳ���
 *			psDatabuf:	��ݻ�����(A����ats)
 *@return
 *@li	NDK_OK			����ɹ�
 *@li	NDK_ERR_PARA		����Ƿ�(psPicctype/pnDatalen/psDatabufΪNULL)
 *@li	NDK_ERR_OPEN_DEV	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	NDK_ERR_IOCTL		����ô���(��Ƶ��ӿ�RFID_IOCG_PICCACTIVATE����ʧ�ܷ���)
*/
int NDK_RfidTypeARats(uchar cid,int *pnDatalen, uchar *psDatabuf);
/**
 *@brief	�����ֻ�NFC���
 *@retval	psRecebufΪ�����ֻ�NFC��ݵ�buf��pnRecvlenΪ�����ֻ�NFC��ݵĳ���
 *          nSeekCntΪpos��ѭ����������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(psRecebuf����pnRecvlenΪNULL,nSeekCnt=0���ߴ���10)
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV" 	�豸�ļ���ʧ��(��Ƶ�豸�ļ���ʧ��)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 		����ô���(��Ƶ��ӿ�ioctl_Get_rfid_ic_type����ʧ�ܷ���)
 *@li	\ref NDK_ERR_NFC_NODEVICE "NDK_ERR_NFC_NODEVICE" û��Ѱ�ҵ�NFC�ֻ���߽�����ݳ���
*/
int NDK_Nfc_Activate(uchar *psRecebuf,int *pnRecvlen,int nSeekCnt);
/** @} */ // ��Ƶ��ģ�����

/** @addtogroup ϵͳ�ӿ�
* @{
*/
/**
 *@brief  ȡPOS�汾��Ӳ����Ϣ�������
 *@details SYS_HWINFO_GET_POS_TYPE ���صĻ����ַ��ʽΪ"NLGP-XXX"����"NLGP-730"��"NLGP-8510"\n
		   SYS_HWINFO_GET_HARDWARE_INFO ����Ӳ����Ϣ�������£�
				 ��ȡPOSӲ����Ϣÿһ���ֽڴ�?ͬ��ģ��\n
				 ͬһ�ֽ��ڲ�ͬ��ֵ���ͬһģ��Ĳ�ͬоƬ����\n
				 ����֧��63���ⲿ�豸��Ӧ�ô���������鲻С��64 \n\n
				 �ο�\ref EM_SYS_HWTYPE "EM_SYS_HWTYPE"

   pinfo[0]:����ģ��\n
    0xFF     :û������ģ��\n
    0x02    :MC39	\n
    0x03    :sim300	\n
    0x06    :M72	\n
	��λΪ0x80��ʾCDMAģ��\n
    0x81    :dtgs800	\n
    0x82    :dtm228c	\n\n
   pinfo[1]:��Ƶģ��	\n
    0xFF     :û����Ƶģ��	\n
    0x01     :RC531	\n
    0x02    :PN512	\n\n
   pinfo[2]:�ſ�ģ��	\n
    0xFF     :û�дſ�ģ��	\n
    0x01     :mesh	\n
    0x02     :giga	\n\n
   pinfo[3]:ɨ��ͷģ��	\n
    0xFF     :û�дſ�ģ��	\n
    0x01     :EM1300	\n
    0x02     :EM3000	\n
    0x03     :SE955	\n\n
   pinfo[4]:�Ƿ�֧������������	\n
    0xFF     :��֧��	\n
    0x01     :֧��	\n\n
   pinfo[5]:���ڸ���	\n
    0xFF     :��	\n
    0x01     :1��	\n
    0x02     :2��	\n\n
   pinfo[6]:�Ƿ�֧��USB	\n
    0xFF     :��	\n
    0x01     :��	\n\n
   pinfo[7]:MODEMģ��	\n
    0xFF     :��	\n
    0x01     :��	\n\n
   pinfo[8]:wifiģ��	\n
    0xFF     :��wifiģ��	\n
    0x01     :��"USI WM-G-MR-09"ģ��	\n\n
   pinfo[9]:�Ƿ�֧����̫��	\n
    0xFF     :��	\n
    0x01     :dm9000	\n
    0x02     :bcm589xcore       \n\n
   pinfo[10]:��ӡģ��	\n
    0xFF     :�޴�ӡģ��	\n
    0x01~0x7F     :����	\n
    0x82~0x7E     :���	\n\n
   pinfo[11]:�Ƿ�֧�ִ���	\n
    0xFF     :��	\n
    0x01:ts_2046	\n
    0x02:589x_ts	\n\n
   pinfo[12]:�Ƿ�����ƵLED��	\n
    0xFF     :��	\n
    0x01     :��	\n
*/
typedef enum {
	SYS_HWINFO_GET_POS_TYPE=0,      		/**<ȡpos��������   			*/
	SYS_HWINFO_GET_HARDWARE_INFO,       /**<��ȡPOS������֧��Ӳ�����ͣ���ϸ����ֵ��������*/
	SYS_HWINFO_GET_BIOS_VER,        		/**<ȡbios�汾��Ϣ 			 */
	SYS_HWINFO_GET_POS_USN,        		/**<ȡ�������к�    		*/
	SYS_HWINFO_GET_POS_PSN,        		/**<ȡ����������    		*/
	SYS_HWINFO_GET_BOARD_VER,       		/**<ȡ�����        			*/
	SYS_HWINFO_GET_CREDITCARD_COUNT,		/**<ȡposˢ������					*/
	SYS_HWINFO_GET_PRN_LEN,				/**<ȡpos��ӡ�ܳ���    		*/
	SYS_HWINFO_GET_POS_RUNTIME,          /**<ȡpos�������ʱ��  */
	SYS_HWINFO_GET_KEY_COUNT,            /**<ȡpos������  */
	SYS_HWINFO_GET_CPU_TYPE,           /**<ȡpos��cpu����  */
	SYS_HWINFO_GET_BOOT_VER,
	SYS_HWINFO_GET_BOARD_NUMBER,      /**<ȡpos���� */
    SYS_HWINFO_GET_KLA1_VER,
    SYS_HWINFO_GET_KLA2_VER,
} EM_SYS_HWINFO;

/**
 *@brief  ��ȡϵͳ������Ϣ�������
*/
typedef enum {
	SYS_CONFIG_SLEEP_ENABLE,	    /**<����ʹ�� 0:��ֹ 1:���� */
	SYS_CONFIG_SLEEP_TIME,      	/**<��������ʱ��ǰ���ʱ��*/
	SYS_CONFIG_SLEEP_MODE,      	/**<����ģʽ 1:ǳ���� 2:������*/
	SYS_CONFIG_LANGUAGE,			/**<��ȡϵͳ���� 0:���� 1:english */
	SYS_CONFIG_APP_AUTORUN,      	/**<�����Զ��������س��� 0:���� 1:����*/
} EM_SYS_CONFIG;

/**
 *@brief  Ӳ���豸��Ϣ����
*/
typedef enum {
	SYS_HWTYPE_WIRELESS_MODEM=0,	/**<����modem */
	SYS_HWTYPE_RFID,				/**< ��Ƶ��*/
	SYS_HWTYPE_MAG_CARD,			/**< �ſ�*/
	SYS_HWTYPE_SCANNER,				/**< ɨ��ͷ*/
	SYS_HWTYPE_PINPAD,				/**< �������*/
	SYS_HWTYPE_AUX,					/**< ����*/
	SYS_HWTYPE_USB,					/**< USB*/
	SYS_HWTYPE_MODEM,				/**< modem*/
	SYS_HWTYPE_WIFI,				/**< wifi*/
	SYS_HWTYPE_ETHERNET,			/**< ��̫��*/
	SYS_HWTYPE_PRINTER,				/**< ��ӡ��*/
	SYS_HWTYPE_TOUCHSCREEN,			/**< ������*/
	SYS_HWTYPE_RFIDLED,				/**< ��ƵLED��*/
	SYS_HWTYPE_BT,                  /**< ���� */
	SYS_HWTYPE_NFC,                 /**< NFC */
	SYS_HWTYPE_GM,                  /**< ���� */
	SYS_HWTYPE_MAX					/**< ���ֵ��ֻ����������*/
} EM_SYS_HWTYPE;

/**
 *@brief  CPU��ͨ��(��IM81�İ�׿��K21)
*/
typedef enum {
    SYS_WAKE_PEER = 0,
    SYS_RESET_PEER
} EM_SYS_PEEROPER;

/**
 *@brief  CPU���¼�֪ͨ(����K21֪ͨ��׿������������ȫ�����¼��ȵ�)
*/
typedef enum {
    SYS_EVENT_NONE = 0,    /*���¼�*/
    SYS_EVENT_TAMPER,      /*��ȫ����*/
    SYS_EVENT_REBOOT,      /*֪ͨϵͳ���������Լ�*/
    SYS_EVENT_MAX,
} EM_SYS_EVENT;

#define MAX_SYS_EVENT_DATA_LEN    (128)

/**
 *@brief	��ȡ��CPU֪ͨ�¼���ÿ�η���һ���¼�����ѭ������ֱ��ȡ��"SYS_EVENT_NONE"�¼�Ϊֹ
 *@retval   event �����ǰ��֪ͨ�¼���ȡֵ��"EM_SYS_EVENT"
 *			len ���ص��¼���Ϣ���ȣ�ÿ���¼������֪ͨ��Ϣ��󲻳���"MAX_SYS_EVENT_DATA_LEN"(128)�ֽ�
 *			out_data ���ص��¼����
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA" 		�������(����ΪNULL)
*/
int NDK_SysGetEvent(uint *event, int *len, uchar *out_data);

/**
 *@brief ����POS������LED  �����������
*/
typedef enum {
    LED_RED_X_ON = 0x10000,        /**<   ���ƺ����           */
    LED_RED_X_OFF = 0x20000,        /**<   ���ƺ����           */
    LED_RED_X_FLICK = 0x30000,        /**<   ���ƺ����           */
    LED_YELLOW_X_ON = 0x40000,        /**<   ���ƻƵ���           */
    LED_YELLOW_X_OFF = 0x80000,        /**<   ���ƻƵ���           */
    LED_YELLOW_X_FLICK = 0xc0000,        /**<   ���ƻƵ���           */
    LED_GREEN_X_ON = 0x100000,        /**<   �����̵���           */
    LED_GREEN_X_OFF = 0x200000,        /**<   �����̵���           */
    LED_GREEN_X_FLICK = 0x300000,        /**<   �����̵���           */
    LED_BLUE_X_ON = 0x400000,        /**<   ����������           */
    LED_BLUE_X_OFF = 0x800000,        /**<   ����������          */
    LED_BLUE_X_FLICK = 0xc00000,        /**<   ����������           */
    LED_RFID_RED_ON = 0x01,             /**<   ������Ƶ��ɫ����         */
    LED_RFID_RED_OFF = 0x02,        /**<   ������Ƶ��ɫ����         */
    LED_RFID_RED_FLICK = 0x03,          /**<   ������Ƶ��ɫ����         */
    LED_RFID_YELLOW_ON = 0x04,              /**<   ������Ƶ��ɫ����         */
    LED_RFID_YELLOW_OFF = 0x08,         /**<   ������Ƶ��ɫ����         */
    LED_RFID_YELLOW_FLICK = 0x0c,           /**<   ������Ƶ��ɫ����         */
    LED_RFID_GREEN_ON = 0x10,           /**<   ������Ƶ��ɫ����         */
    LED_RFID_GREEN_OFF = 0x20,          /**<   ������Ƶ��ɫ����         */
    LED_RFID_GREEN_FLICK = 0x30,            /**<   ������Ƶ��ɫ����         */
    LED_RFID_BLUE_ON = 0x40,            /**<   ������Ƶ��ɫ����         */
    LED_RFID_BLUE_OFF = 0x80,           /**<   ������Ƶ��ɫ����         */
    LED_RFID_BLUE_FLICK = 0xc0,         /**<   ������Ƶ��ɫ����         */
    LED_COM_ON = 0x100,             /**<   ����ͨѶ����           */
    LED_COM_OFF = 0x200,        /**<   ����ͨѶ����           */
    LED_COM_FLICK = 0x300,          /**<   ����ͨѶ����           */
    LED_ONL_ON = 0x400,             /**<   �����������           */
    LED_ONL_OFF = 0x800,        /**<   �����������           */
    LED_ONL_FLICK = 0xc00,          /**<   �����������           */
    LED_DETECTOR_ON = 0x1000,
    LED_DETECTOR_OFF = 0x2000,
    LED_DETECTOR_FLICK = 0x3000,
    LED_MAG_ON = 0x4000,        /**<   ���ƴſ�����           */
    LED_MAG_OFF = 0x8000,       /**<   ���ƴſ�����           */
    LED_MAG_FLICK = 0xc000,     /**<   ���ƴſ�����           */
} EM_LED;

/*���豸��*/
typedef enum {
    SS_TYPE_KEYBOARD,		/**<����*/
    SS_TYPE_PRINTER,		/**<��ӡ��*/
    SS_TYPE_MAG,			/**<�ſ�*/
    SS_TYPE_ICCARD,			/**<IC��*/
    SS_TYPE_RFID,			/**<��Ƶ��*/
    SS_TYPE_MODEM,		/**<MODEM*/
    SS_TYPE_WLS,			/**<����*/
    SS_TYPE_WIFI,			/**<WIFI*/
    SS_TYPE_POWER,			/**<���ػ�*/
    SS_TYPE_DEV_MAIN_NUM	/**<���豸����*/
} EM_SS_TYPE;

/**
 *@brief Ҫͳ�Ƶ��豸ID
*/
typedef enum{
	SS_KEYBOARD_ZERO = (SS_TYPE_KEYBOARD<<16|13),		/**<ͳ������0��*/
	SS_KEYBOARD_ONE = (SS_TYPE_KEYBOARD<<16|26),		/**<ͳ������1��*/
	SS_KEYBOARD_TWO = (SS_TYPE_KEYBOARD<<16|25),		/**<ͳ������2��*/
	SS_KEYBOARD_THREE = (SS_TYPE_KEYBOARD<<16|24),	/**<ͳ������3��*/
	SS_KEYBOARD_FOUR = (SS_TYPE_KEYBOARD<<16|22),		/**<ͳ������4��*/
	SS_KEYBOARD_FIVE = (SS_TYPE_KEYBOARD<<16|21),		/**<ͳ������5��*/
	SS_KEYBOARD_SIX = (SS_TYPE_KEYBOARD<<16|20),		/**<ͳ������6��*/
	SS_KEYBOARD_SEVEN = (SS_TYPE_KEYBOARD<<16|18),	/**<ͳ������7��*/
	SS_KEYBOARD_EIGHT = (SS_TYPE_KEYBOARD<<16|17),	/**<ͳ������8��*/
	SS_KEYBOARD_NINE = (SS_TYPE_KEYBOARD<<16|16),		/**<ͳ������9��*/
	
              SS_KEYBOARD_ENTER = (SS_TYPE_KEYBOARD<<16|8),	/**<ͳ��ȷ�ϼ�*/
              SS_KEYBOARD_ESC = (SS_TYPE_KEYBOARD<<16|10),		/**<ͳ��ȡ���*/
              SS_KEYBOARD_F2 = (SS_TYPE_KEYBOARD<<16|29),		/**<ͳ��F2(�˵���)*/
              SS_KEYBOARD_F1 = (SS_TYPE_KEYBOARD<<16|28),		/**<ͳ��F1(���ϼ�)*/
              SS_KEYBOARD_DOT = (SS_TYPE_KEYBOARD<<16|14),		/**<ͳ��С����*/
              SS_KEYBOARD_ZMK = (SS_TYPE_KEYBOARD<<16|12),		/**<ͳ����ĸ��*/
              SS_KEYBOARD_F3 = (SS_TYPE_KEYBOARD<<16|30),		/**<ͳ��F3(���¼�)*/
	SS_KEYBOARD_BASP = (SS_TYPE_KEYBOARD<<16|9),	/**<ͳ���˸��*/
	SS_KEYBOARD_0_ID = (SS_TYPE_KEYBOARD<<16|2),	/**<ͳ�������*/
	SS_KEYBOARD_1_ID = (SS_TYPE_KEYBOARD<<16|3),	/**<ͳ�������*/
	SS_KEYBOARD_2_ID = (SS_TYPE_KEYBOARD<<16|4),	/**<ͳ�������*/
	SS_KEYBOARD_3_ID = (SS_TYPE_KEYBOARD<<16|5),	/**<ͳ�������*/
	SS_KEYBOARD_F4 = (SS_TYPE_KEYBOARD<<16|31),		/**<ͳ��F4(�ػ��)*/
	SS_KEYBOARD_TOTAL = (SS_TYPE_KEYBOARD<<16|33),	/**<ͳ�����м�*/

	SS_PRT_PAPER_ID = (SS_TYPE_PRINTER<<16|0),    		/**<��ӡ����(��λΪ����mm)*/
	SS_PRT_HEAT_ID = (SS_TYPE_PRINTER<<16|1),			/**<����ͷ����ʱ��(��λΪ��)*/

	SS_MAG_TIMES_ID = (SS_TYPE_MAG<<16|0),			/**<ˢ������*/

	SS_ICCARD_BASE_ID = (SS_TYPE_ICCARD<<16|0),		/**<�忨����*/

	SS_RFID_TIMES_ID = (SS_TYPE_RFID<<16|0),		/**<Ѱ������*/

	SS_MODEM_TIMES_ID = (SS_TYPE_MODEM<<16|0),		/**<MODEM���Ӵ���*/
	SS_MODEM_FAILTIMES_ID = (SS_TYPE_MODEM<<16|1),	/**<MODEM����ʧ�ܴ���*/
	SS_MODEM_SDLCTIME_ID = (SS_TYPE_MODEM<<16|2),	/**<MODEMʱ��(SDLC)*/
	SS_MODEM_ASYNTIME_ID = (SS_TYPE_MODEM<<16|3),	/**<MODEMʱ��(�첽)*/

	SS_WLS_TIMES_ID = (SS_TYPE_WLS<<16|0),			/**<�������Ӵ���*/
	SS_WLS_FAILTIMES_ID = (SS_TYPE_WLS<<16|1),		/**<��������ʧ�ܴ���*/
	SS_WLS_PPPTIME_ID = (SS_TYPE_WLS<<16|2),		/**<��������ʱ��(��λΪ����)*/

	SS_WIFI_TIMES_ID = (SS_TYPE_WIFI<<16|0),		/**<WIFI���Ӵ���*/
	SS_WIFI_TIME_ID = (SS_TYPE_WIFI<<16|1),			/**<WIFI����ʱ��(��λΪ����)*/

	SS_POWER_TIMES_ID = (SS_TYPE_POWER<<16|0),		/**<���ػ����*/
	SS_POWERUP_TIME_ID = (SS_TYPE_POWER<<16|1)		/**<����ʱ��(��λΪ��)*/
}EM_SS_DEV_ID;

/**
 *@brief �̼��汾����
*/
typedef enum {
	SYS_FWINFO_PRO,      		/**<��ʽ�汾   			*/
	SYS_FWINFO_DEV				/**<�����汾   			*/
} EM_SYS_FWINFO;

/**
 *@brief	��ȡNDK��汾��
 *@retval   pszVer	�汾���ַ�,�����С������16�ֽ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(pszVerΪNULL)
*/
int NDK_Getlibver(char *pszVer);

/**
 *@brief 		ϵͳ��ʼ��(�����θ�API)
 *@details
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	����NDK_ERRCODE		����ʧ��
*/
int NDK_SysInit(void);
/**
 *@brief 		ϵͳ�˳�
 *@details	nErrCodeΪ0��ʾ���˳�.�����ʾ�쳣�˳���nErrCode�᷵�ظ�ϵͳ��
 *@param    nErrCode	ϵͳ�˳��ķ���ֵ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SysExit(int nErrCode);
/**
 *@brief 		POS����
 *@details
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SysReboot(void);
/**
 *@brief 		POS�ػ�
 *@details
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SysShutDown(void);
/**
 *@brief 		Beepֻ��һ�����Ҫ���������������м����ʱ
 *@details
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"		���豸�ļ�ʧ��
 *@li	 \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"			����ô���
*/
int NDK_SysBeep(void);
/**
 *@brief 		����beep������
 *@details
 *@param    unVolNum    ��Ҫ���õ������Ĳ������ΧΪ0~5�������õײ�Ĭ��Ϊ5
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		�������(unVolNum�Ƿ�)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	���豸�ļ�ʧ��
*/
int NDK_SysSetBeepVol(uint unVolNum);
/**
 *@brief 		ȡbeep������
 *@details
 *@retval    punVolNum    ��Ҫ���õ������Ĳ���
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		�������(unVolNum�Ƿ�)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	���豸�ļ�ʧ��
*/
int NDK_SysGetBeepVol(uint *punVolNum);
/**
 *@brief 		��һ����Ƶ����һ����ʱ��
 *@details
 *@param    unFrequency		������Ƶ�ʣ���λ:Hz,��ΧΪ0 < unFrequency <=4000
 *@param    unSeconds		���������ʱ�䣬��λ:ms,��ΧΪunSeconds > 0
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		�������(unFrequency�Ƿ���unSecondsС��0)
 *@li	 \ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	���豸�ļ�ʧ��
 *@li	 \ref NDK_ERR_IOCTL "NDK_ERR_IOCTL" 			����ô���
*/
int NDK_SysTimeBeep(uint unFrequency,uint unSeconds);
/**
 *@brief 		�����Ƿ������Զ���������
 *@param    unFlag  0:�������Զ��������ߣ�1:�����Զ��������ߣ�����ֵ����Ϸ�
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		�������(unFlag�Ƿ�)
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_SysSetSuspend(uint unFlag);
/**
 *@brief 		�����Ƿ�������������
 *@details  �����Ƿ��Զ��������߿��ضԴ˺�����Ӱ�졣ֻҪ���û���������������
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_SysGoSuspend(void);
/**
 *@brief 		ȡ��Դ����
 *@retval   punVol  ֻ���Դ��Ϊ0�����򷵻ص�ص���
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(punVolΪNULL)
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_SysGetPowerVol(uint *punVol);
/**
 *@brief        ȡ��Դ������Χ
 *@retval   punMax:���ֵ punMin:��Сֵ
 *@return
 *@li    NDK_OK             �����ɹ�
 *@li    \ref NDK_ERR_PARA "NDK_ERR_PARA"       ����Ƿ�(punVolΪNULL)
 *@li    \ref NDK_ERR "NDK_ERR" ����ʧ��
*/
int NDK_SysGetPowerVolRange(uint * punMax, uint * punMin);
/**
 *@brief 		��λ��ʱ( ��λʱ��Ϊ0.1s)
 *@details
 *@param    unDelayTime ��ʱʱ�䣬��ΧunDelayTime > 0
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(unDelayTimeС��0)
*/
int NDK_SysDelay(uint unDelayTime);
/**
 *@brief 		��λ��ʱ (��λʱ��Ϊ1ms)
 *@details
 *@param    unDelayTime ��ʱʱ�䣬��ΧunDelayTime > 0
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(unDelayTime<=0)
*/
int NDK_SysMsDelay(uint unDelayTime);
/**
 *@brief 		ȡPOS��ǰʱ��
 *@details
 *@param     pstTime  ����tm�ṹ�����͵�ָ�룬���ص�ǰposʱ��
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(pstTimeΪNULL)
*/
int NDK_SysGetPosTime(struct tm *pstTime);

/**
 *@brief 		����POS��ǰʱ��
 *@details
 *@param     stTime  ����tm�ṹ�����͵ı���������posʱ��Ϊ����stTime��ʱ��
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(stTime�Ƿ�)
 *@li	 \ref NDK_ERR "NDK_ERR"		����ʧ��(����mktime()/stime()ʧ�ܷ���)
*/
int NDK_SysSetPosTime(struct tm stTime);

/**
 *@brief 		  ��ȡָ���ֿ������(�ӿ��ݶ���δʵ��)
 *@details    ���ƫ��ȡָ���ֿ��n���ֽڵ�����
 *@param      psPath �ֿ����ڵ�·��
 *@param      unOffSet �ֿ�ƫ����
 *@param      unLen Ҫȡ�ֿ��ֽ���
 *@retval     psBuf ���ڴ洢ȡ�������ֿ�������Ϣ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	����\ref EM_NDK_ERR "EM_NDK_ERR"		����ʧ��
*/
int NDK_SysReadFont(const char * psPath,uint unOffSet,char *psBuf,uint unLen);
/**
 *@brief 		����POS��������led�Ƶ��������
 *@details
*@param      emStatus    ö�����͵ı��������Ƹ����Ƶ����𣬲�ͬ�ĸ�����֮���ͨ�������п��ơ�
							 					�����Ӧ�Ƶ�ö�ٱ���Ϊ0(����������Ӧ��ֵ)�������Ӧ�ĵƵ�״̬���䣬��:
							 					NDK_LedStatus(LED_RFID_RED_ON|LED_RFID_YELLOW_FLICK),������Ϊ���ú�������Ƶ����������״̬���䡣
							 					���Կ�������Ӧ�ĵ�֮����Ҫע���Ƿ�ָ���
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(emStatus)
 *@li	 \ref NDK_ERR "NDK_ERR"   		����ʧ��
*/
int NDK_LedStatus(EM_LED emStatus);
/**
 *@brief 		�����ܱ?��ʼ��ʱ
 *@details  ��NDK_SysStartWatch()��NDK_SysStopWatch()���ʹ�á�������1��������
 *@return
 *@li	 NDK_OK				�����ɹ�
*/
int NDK_SysStartWatch(void);
/**
 *@brief 		ֹͣ�ܱ?�������ֵ
 *@details
 *@retval   punTime �ܱ����ʱ�ļ���ֵ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(punTimeΪNULL)
*/
int NDK_SysStopWatch(uint *punTime);

/**
 *@brief 		��ȡ����ֵ
 *@details
 *@retval   punTime ��ǰ�ļ���ֵ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(punTimeΪNULL)
*/
int NDK_SysReadWatch(uint *punTime);
/**
 *@brief 	��ȡposӲ����Ϣ�ӿ�
 *@details	������ȡӲ����Ϣ������emFlag���ڷ�Χ�ڣ��򷵻ز���������ûȡ���汾��Ϣ����NDK_ERR
 			����Ĳ���������С���ݶ�Ϊ100�ֽ�,��С������16�ֽڡ�apiֻ����ǰ100���ֽڵ���Ϣ,�пɸ�������Ƿ񷵻�psbuf�з�����ݵĳ��ȣ�������punlen����ΪNULL����
 *@param    emFlag ��Ҫ��ȡ�豸��Ϣ�������
 *@retval   punLen ���ش��ص�psBuf��Ϣ�ĳ���(����punLenΪNULLʱҲ���������?������psBuf��Ϣ����)
 *@retval   psBuf	���ڴ洢���ص���Ϣ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBufΪNULL)
 *@li	 \ref NDK_ERR "NDK_ERR"		����ʧ��
*/
int NDK_SysGetPosInfo(EM_SYS_HWINFO emFlag,uint *punLen,char *psBuf);

/**
 *@brief    ����posӲ����Ϣ�ӿ�
 *@details  ����PN\SN\��ŵ���Ϣ��֧���������²���
 *@details  emFlag��֧��SYS_HWINFO_GET_POS_USN\SYS_HWINFO_GET_POS_PSN\SYS_HWINFO_GET_BOARD_VER\SYS_HWINFO_GET_BOARD_NUMBER
 *@details  SYS_HWINFO_GET_BOARD_NUMBER֧��34�ֽ� ����֧�����29�ֽ�
 *@param    emFlag ��Ҫ�����豸��Ϣ�������
 *@param   psBuf ���õ�ֵ
 *@return
 *@li    NDK_OK             �����ɹ�
 *@li    \ref NDK_ERR_PARA "NDK_ERR_PARA"   ����Ƿ�(psBufΪNULL)��psBuf����Ϊ0
 *@li    \ref NDK_ERR_OVERFLOW ���ȳ�������details����
 *@li    \ref NDK_ERR_PARA "NDK_ERR_PARA"   ����Ƿ���emFlagö��ֵ��Ϊ�����о�֮һ
 *@li    \ref NDK_ERR "NDK_ERR"     ����ʧ��
*/
int NDK_SysSetPosInfo(EM_SYS_HWINFO emFlag, const char *psBuf);

/**
 *@brief    ��ȡϵͳ������Ϣ
 *@param    emConfig ��Ҫ��ȡ������Ϣ�������
 *@retval   pnValue ���ص�����ֵ
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pnValueΪNULL)
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_SysGetConfigInfo(EM_SYS_CONFIG emConfig,int *pnValue);

/**
 *@brief    ���ͳ����Ϣ�����Ӧ��ͳ����Ϣ��
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��(ͳ�Ʒ���dbusͨѶʧ��)
*/
int NDK_SysInitStatisticsData(void);

/**
 *@brief    ��ȡͳ����Ϣ����\ref EM_SS_DEV_ID "EM_SS_DEV_ID"ѡ��һ��ID��pulValue������ӦID���Ӧ��ͳ��ֵ��
 *@param  	emDevId 	Ҫ��ѯ���豸ID,�ο�\ref EM_SS_DEV_ID "EM_SS_DEV_ID".
 *@retval   pulValue 	ͳ��ֵ����ͳ��ֵ��һ���ۼ�ֵ�������ӡ����ͳ�ƴӵ�һ��ӡ����һֱ�ۼ����ֵ��
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR "NDK_ERR"	����ʧ��(ͳ�Ʒ���dbusͨѶʧ��)
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pulValueΪNULL��emDevIdС��0)
*/
int NDK_SysGetStatisticsData(EM_SS_DEV_ID emDevId,ulong *pulValue);

/**
 *@brief    ��ȡ�̼�����
 *@retval  	emFWinfo 	���صĹ̼�����,�ο�\ref EM_SYS_FWINFO "EM_SYS_FWINFO".
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(emFWinfoΪNULL)
*/
int NDK_SysGetFirmwareInfo(EM_SYS_FWINFO *emFWinfo);
/**
 *@brief 		��ȡPOS��ǰʱ�䵥λΪ��
 *@details	��ȡ��ʱ�����뵥λ����1970��1��1��0ʱ0��0�뿪ʼ���㵽���ھ����˶������ʱ�䡣
 *@retval   ulTime 	����������
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	 \ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(ulTimeΪNULL)
*/
int NDK_SysTime(ulong *ulTime);
/**
 *@brief   ���������Զ����ѵ�ʱ��,��С����ʱ��Ϊ60��,589Xƽ̨��SP60���ͣ���ʱ���ѵľ��Ƚϵͣ������128�����ҡ�
 *@retval  unSec  ��λ:��
 *@return
 *@li	 NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	     ����Ƿ�(unSecС��60)
 *@li	\ref NDK_ERR "NDK_ERR"	     ����ʧ��
*/
int NDK_SysSetSuspendDuration(uint unSec);

/** @} */ // ϵͳ�ӿڽ���

/** @addtogroup ����
* @{
*/

/**
 *@brief	2����󲻳���12λ���޷�����ִ��ӷ�
 *@details	2�����ִ������λ��ӣ���ӽ���ܳ���12λ
 *@param	pszDigStr1		���ִ�1
 *@param	pszDigStr2		���ִ�2
 *@param	pnResultLen		������pszResult�Ĵ�С
 *@retval	pszResult		��Ӻ͵����ִ�
 *@retval	pnResultLen		��Ӻ͵�λ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(pszDigStr1/pszDigStr2/pszResult/pnResultLenΪNULL�����ִ����Ϸ�)
 *@li	\ref NDK_ERR "NDK_ERR"		����ʧ��(�����ִ���ӳ���12λ)
*/
int NDK_AddDigitStr(const uchar *pszDigStr1, const uchar *pszDigStr2, uchar* pszResult, int *pnResultLen );

/**
 *@brief	��6λ���ִ�pszStrNum����1��Ż�ԭֵ
 *@param	pszStrNum		��Ҫ�����ӵ����ִ�,�����������Ϊ7
 *@retval	pszStrNum		���Ӻ�Ľ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(pszStrNumΪNULL��pszStrNum���ȴ���6��pszStrNum���ִ����Ϸ�)
*/
int NDK_IncNum (uchar * pszStrNum );

/**
 *@brief	�Ѵ�С���Ľ���ַ�תΪ����С���Ľ���ַ�
 *@param	pszSource		��ת���Ľ���ַ�
 *@param	pnTargetLen		Ŀ�껺����Ĵ�С
 *@retval	pszTarget		ת������ַ�
 *@retval	pnTargetLen		ת������ַ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszSource/pszTarget/pnTargetLenΪNULL)
*/
int NDK_FmtAmtStr (const uchar* pszSource, uchar* pszTarget, int* pnTargetLen );

/**
 *@brief	��AscII����ַ�ת����ѹ����HEX��ʽ
 *@details	��ż��ȵ��ַ��ݶ��뷽ʽ����ȡ���Ҳ�0��
 *@param	pszAsciiBuf		��ת����ASCII�ַ�
 *@param	nLen			������ݳ���(ASCII�ַ�ĳ���)
 *@param	ucType			���뷽ʽ  0�������  1���Ҷ���
 *@retval	psBcdBuf		ת�������HEX���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszAsciiBuf/psBcdBufΪNULL��nLen<=0)
*/
int NDK_AscToHex (const uchar* pszAsciiBuf, int nLen, uchar ucType, uchar* psBcdBuf);

/**
 *@brief	��HEX�����ת����AscII���ַ�
 *@param	psBcdBuf		��ת����Hex���
 *@param	nLen			ת����ݳ���(ASCII�ַ�ĳ���)
 *@param	ucType			���뷽ʽ  1�������  0���Ҷ���
 *@retval	pszAsciiBuf		ת�������AscII�����
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBcdBuf/pszAsciiBufΪNULL��nLen<0��ucType�Ƿ�)
*/
int NDK_HexToAsc (const uchar* psBcdBuf, int nLen, uchar ucType, uchar* pszAsciiBuf);

/**
 *@brief	����ת��Ϊ4�ֽ��ַ����飨��λ��ǰ��
 *@param	unNum		��Ҫת����������
 *@retval	psBuf		ת��������ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBufΪNULL)
*/
int NDK_IntToC4 (uchar* psBuf, uint unNum );

/**
 *@brief	����ת��Ϊ2�ֽ��ַ����飨��λ��ǰ��
 *@param	unNum		��Ҫת����������
 *@retval	psBuf		ת��������ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBufΪNULL)
*/
int NDK_IntToC2(uchar* psBuf, uint unNum );

/**
 *@brief	4�ֽ��ַ�����ת��Ϊ���ͣ���λ��ǰ��
 *@param	psBuf		��Ҫת�����ַ�
 *@retval	unNum		ת�������������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(unNum��psBufΪNULL)
*/
int NDK_C4ToInt(uint* unNum, uchar* psBuf );

/**
 *@brief	2�ֽ��ַ�����ת��Ϊ���ͣ���λ��ǰ��
 *@details	psBuf����Ҫ>=2
 *@param	psBuf		��Ҫת�����ַ�
 *@retval	unNum		ת�������������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(unNum��psBufΪNULL)
*/
int NDK_C2ToInt(uint *unNum, uchar *psBuf);

/**
 *@brief	����(0-99)ת��Ϊһ�ֽ�BCD
 *@param	nNum		��Ҫת����������(0-99)
 *@retval	psCh			ת�������һ��BCD�ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(chΪNULL��nNum�Ƿ�)
*/
int NDK_ByteToBcd(int nNum, uchar *psCh);

/**
 *@brief	һ�ֽ�BCDת��Ϊ����(0-99)
 *@param	ucCh		��Ҫת����BCD�ַ�
 *@retval	pnNum	ת�����������ֵ(0-99)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pnNumΪNULL��ch �Ƿ�)
*/
int NDK_BcdToByte(uchar ucCh, int *pnNum);

/**
 *@brief	����(0-9999)��Ҫת����������(0-9999)
 *@param	nNum		���ִ�1
 *@param	pnBcdLen	���������Ĵ�С
 *@retval	pnBcdLen	ת�����BCD���ȣ����ɹ���ֵ���̶�����ֵΪ2
 *@retval	psBcd		ת����������ֽ�BCD
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBcd��pnBcdLenΪNULL��nNum �Ƿ�)
*/
int NDK_IntToBcd(uchar *psBcd, int *pnBcdLen, int nNum);

/**
 *@brief	���ֽ�BCDת��Ϊ����(0-9999)
 *@details	psBcd����Ӧ����2
 *@param	psBcd		��Ҫת�������ֽ�BCD
 *@retval	nNum		ת���������(0-9999)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBcd��nNumΪNULL��nNum �Ƿ�)
*/
int NDK_BcdToInt(const uchar * psBcd, int *nNum);

/**
 *@brief	����LRC
 *@details	psbuf����ĳ���>nLen
 *@param	psBuf		��Ҫ����LRC���ַ�
 *@param	nLen		��Ҫ����LRC���ַ�ĳ���
 *@retval	ucLRC		����ó���LRC
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(psBuf��ucLRCΪNULL��nLen<=0)
*/
int NDK_CalcLRC(const uchar *psBuf, int nLen, uchar *ucLRC);

/**
 *@brief	�ַ�ȥ��ո�
 *@param	pszBuf		����ַ�Ļ�����
 *@retval	pszBuf		ȥ����ո����ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszBufΪNULL)
*/
int NDK_LeftTrim(uchar *pszBuf);

/**
 *@brief	�ַ�ȥ�ҿո�
 *@param	pszBuf		����ַ�Ļ�����
 *@retval	pszBuf		ȥ���ҿո����ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszBufΪNULL)
*/
int NDK_RightTrim(uchar *pszBuf);

/**
 *@brief	�ַ�ȥ���ҿո�
 *@param	pszBuf			����ַ�Ļ�����
 *@retval	pszBuf			ȥ�����ҿո����ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszBufΪNULL)
*/
int NDK_AllTrim(uchar *pszBuf);

/**
 *@brief	��һ�ַ������ĳһ�ַ�ʹ֮����ΪnLen
 *@details	pszString����ĳ���Ӧ>nlen, �ַ�ĳ���ҪС��nlen
 *@param	pszString		����ַ�Ļ�����
 *@param    nLen			�ַ���
 *@param	ucCh				��Ҫ������ַ�
 *@param	nOption			��������
                          	0    ���ַ�ǰ����ַ�
                          	1    ���ַ������ַ�
                          	2    ���ַ�ǰ����ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszStringΪNULL��pszString���ȷǷ���nOption�Ƿ�)
*/
int NDK_AddSymbolToStr(uchar *pszString, int nLen, uchar ucCh, int nOption);

/**
 *@brief	��ȡ�Ӵ�
 *@details	�Ӵ����'\0'�����
 *@param	pszSouStr		��Ҫ���н�ȡ���ַ�
 *@param	nStartPos		Ҫ��ȡ�Ӵ�����ʼλ�� �ַ��λ����1��ʼ����
 *@param	nNum			Ҫ��ȡ���ַ���
 *@retval	pszObjStr		���Ŀ�괮�Ļ�����
 *@retval	pnObjStrLen		�Ӵ��ĳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszObjStr/pnObjStrLen/pszSouStrΪNULL)
*/
int NDK_SubStr(const uchar *pszSouStr, int nStartPos, int nNum, uchar *pszObjStr, int *pnObjStrLen);

/**
 *@brief	�жϸ�һ�ַ��ǲ��������ַ�
 *@param	ucCh		��Ҫ�жϵ��ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(ch�Ƿ�)
*/
int NDK_IsDigitChar(uchar ucCh);

/**
 *@brief	����һ�ִ��Ƿ�Ϊ�����ִ�
 *@param	pszString		��Ҫ�жϵ��ַ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszStringΪNULL)
*/
int NDK_IsDigitStr(const uchar *pszString);

/**
 *@brief	�ж�ĳ���Ƿ�����
 *@param	nYear		���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_IsLeapYear(int nYear);

/**
 *@brief	�ҳ�ĳ��ĳ�µ��������
 *@param	nYear		���
 *@param	nMon		�·�
 *@retval	pnDays		����ݸ��¶�Ӧ���������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(�ꡢ�¡��շǷ�)
*/
int NDK_MonthDays(int nYear, int nMon, int *pnDays);

/**
 *@brief	�ж��ṩ���ַ��ǲ��ǺϷ������ڸ�ʽ��
 *@param	pszDate		���ڸ�ʽ�ַ�  ��ʽΪ YYYYMMDD
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszDateΪNULL��pszDate�����Ȳ�����8��pszDate�Ƿ�)
*/
int NDK_IsValidDate(const uchar *pszDate);

/** @} */ // ����ģ�����


/** @addtogroup SslSocketsģ��
* @{
*/

typedef enum
{
	NDK_NOWAIT = 0,				       ///< ������
	NDK_SUSPEND = 0xFFFFFFFF,		 ///< ����
}EM_BLOCK_OPT;

typedef struct st_socket_addr
{
	uint  unAddrType;	///< ��ַ����
	char *psAddr;					      ///< IP ��ַ��ASCIIz��.
	ushort usPort;					      ///< IP �˿�.
}ST_SOCKET_ADDR;

typedef enum
{
    HANDSHAKE_SSLv2,	  //!< ��SSLv2
    HANDSHAKE_SSLv23, 	//!< SSLv3 SSLv2
    HANDSHAKE_SSLv3,	  //!< ��SSLv3
    HANDSHAKE_TLSv1,	  //!< TLSv1
}EM_SSL_HANDSHAKE_OPT;

typedef enum
{
    SSL_AUTH_NONE,			///< ������ģʽ:�����������һ���ͻ���֤������ͻ���,��ô�ͻ��˽����ᷢ��һ��֤�顣
												///< �ͻ���ģʽ:���ʹ��һ�����������(Ĭ������½���),�������ͻᷢ��һ��֤��,Ȼ�󽫱���顣
	SSL_AUTH_CLIENT,		  ///< ������ģʽ:����������һ���ͻ���֤������Ŀͻ����ص�֤��(����еĻ�)�Ǽ�顣
							          ///< �ͻ���ģʽ:��֤�ķ�����֤�顣
}EM_SSL_AUTH_OPT;

typedef enum
{
        SSL3_CIPHER_RSA_NULL_MD5 = 1,			///< �����׼�-RSA_NULL_MD5
        SSL3_CIPHER_RSA_NULL_SHA,				///< �����׼�-RSA_NULL_SHA
        SSL3_CIPHER_RSA_RC4_40_MD5,				///< �����׼�-RSA_RC4_40_MD5
        SSL3_CIPHER_RSA_RC4_128_MD5,			///< �����׼�-RSA_RC4_128_MD5
        SSL3_CIPHER_RSA_RC4_128_SHA,			///< �����׼�-RSA_RC4_128_SHA
        SSL3_CIPHER_RSA_RC2_40_MD5,				///< �����׼�-RSA_RC2_40_MD5
        SSL3_CIPHER_RSA_IDEA_128_SHA,			///< �����׼�-IDEA_128_SHA
        SSL3_CIPHER_RSA_DES_40_CBC_SHA,			///< �����׼�-DES_40_CBC_SHA
        SSL3_CIPHER_RSA_DES_64_CBC_SHA,			///< �����׼�-DES_64_CBC_SHA
        SSL3_CIPHER_RSA_DES_192_CBC3_SHA,		///< �����׼�-DES_192_CBC3_SHA
        SSL3_CIPHER_DH_RSA_DES_192_CBC3_SHA,	///< �����׼�-DH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_DH_DSS_DES_40_CBC_SHA,		///< �����׼�-DH_DSS_DES_40_CBC_SHA
        SSL3_CIPHER_DH_DSS_DES_64_CBC_SHA,		///< �����׼�-DH_DSS_DES_64_CBC_SHA
        SSL3_CIPHER_DH_DSS_DES_192_CBC3_SHA,	///< �����׼�-DH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_DH_RSA_DES_40_CBC_SHA,		///< �����׼�-DH_RSA_DES_40_CBC_SHA
        SSL3_CIPHER_DH_RSA_DES_64_CBC_SHA,		///< �����׼�-DH_RSA_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_40_CBC_SHA,		///< �����׼�-EDH_DSS_DES_40_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_64_CBC_SHA,		///< �����׼�-EDH_DSS_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_DSS_DES_192_CBC3_SHA,	///< �����׼�-EDH_DSS_DES_192_CBC3_SHA
        SSL3_CIPHER_EDH_RSA_DES_40_CBC_SHA,		///< �����׼�-EDH_RSA_DES_40_CBC_SHA
        SSL3_CIPHER_EDH_RSA_DES_64_CBC_SHA,		///< �����׼�-EDH_RSA_DES_64_CBC_SHA
        SSL3_CIPHER_EDH_RSA_DES_192_CBC3_SHA,	///< �����׼�-EDH_RSA_DES_192_CBC3_SHA
        SSL3_CIPHER_ADH_RC4_40_MD5,				///< �����׼�-ADH_RC4_40_MD5
        SSL3_CIPHER_ADH_RC4_128_MD5,			///< �����׼�-ADH_RC4_128_MD5
        SSL3_CIPHER_ADH_DES_40_CBC_SHA,			///< �����׼�-ADH_DES_40_CBC_SHA
        SSL3_CIPHER_FZA_DMS_NULL_SHA,			///< �����׼�-FZA_DMS_NULL_SHA
        SSL3_CIPHER_CK_FZA_DMS_FZA_SHA,			///< �����׼�-CK_FZA_DMS_FZA_SHA
        SSL3_CIPHER_CK_FZA_DMS_RC4_SHA,			///< �����׼�-CK_FZA_DMS_RC4_SHA
        SSL3_CIPHER_CK_ADH_DES_64_CBC_SHA,		///< �����׼�-CK_ADH_DES_64_CBC_SHA
        SSL3_CIPHER_CK_ADH_DES_192_CBC_SHA,		///< �����׼�-CK_ADH_DES_192_CBC_SHA
}EM_SSL_CIPHER_OPT;

typedef enum
{
    SSL_IS_DISCONNECTED,			/**<δ����*/
    SSL_CONNECTION_IN_PROGRESS,		/**<����������*/
    SSL_IS_CONNECTED				/**<������*/
}EM_SSL_CONNECTION_STATE;

typedef enum
{
    SSL_FILE_DER,		/**<DER(ASN1) �ļ���ʽ*/
    SSL_FILE_PEM,		/**<PEM (BASE64) �ļ���ʽ*/
}EM_SSL_FILE_FORMAT;

typedef enum
{
	SSL_ADDR_IPV4,		/**<IP v4*/
    SSL_ADDR_IPV6,		/**<IP v6*/
}EM_ADDR_TYPE;

/**
 *@brief	���ر���֤��
 *@param  pvHandle  SSL���
 *@param  psFileName  ֤���ļ�
 *@param  nFormat    ֤���ļ���ʽ(�ο�\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�psFileName֤���ļ�ΪNULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	ģʽ��֧��(format֤���ʽ��֧��)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�����֤��ʧ�ܣ�
*/
int NDK_LoadClientCertificate(NDK_HANDLE pvHandle, const char *psFileName, int nFormat);
/**
 *@brief	����֤��˽Կ
 *@param  pvHandle  SSL���
 *@param  psFileName  ˽Կ�ļ�
 *@param  nFormat    �ļ���ʽ(�ο�\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@param  pszPassword  ˽Կ������Ĵ�NULL��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�psFileName֤���ļ�ΪNULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	ģʽ��֧��(format֤���ʽ��֧��)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�����֤��ʧ�ܣ�
*/
int NDK_LoadClientPrivateKey(NDK_HANDLE pvHandle, const char *psFileName, int nFormat,char *pszPassword);
/**
 *@brief	����CA֤��
 *@param  pvHandle  SSL���
 *@param  psFileName  CA֤���ļ�
 *@param  nFormat    CA֤���ļ���ʽ(�ο�\ref EM_SSL_FILE_FORMAT "EM_SSL_FILE_FORMAT")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�psFileName֤���ļ�ΪNULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	ģʽ��֧��(format֤���ʽ��֧��)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�����֤��ʧ�ܣ�
*/
int NDK_LoadServerCertificate(NDK_HANDLE pvHandle, const char *psFileName, int nFormat);

/**
 *@brief	����һ��SSL socket
 *@param	nType		����Э������(�ο�\ref EM_SSL_HANDSHAKE_OPT "EM_SSL_HANDSHAKE_OPT")
 *@param  	nAuthOpt 	��֤ģʽ(�ο�\ref EM_SSL_AUTH_OPT "EM_SSL_AUTH_OPT")
 *@param  	pnCipher   	��֧�ֵ��㷨(�ο�\ref EM_SSL_CIPHER_OPT "EM_SSL_CIPHER_OPT")
 *@return
 *@li	SSL���				�����ɹ�
 *@li	NULL		����ʧ��
*/
NDK_HANDLE NDK_OpenSSLSocket(int nType,int nAuthOpt,int* pnCipher);
/**
 *@brief	�ر�SSL socket
 *@param  	pvHandle  SSL���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ��)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�SSL���ӹر�ʧ�ܣ�
*/
int NDK_CloseSSLSocket(NDK_HANDLE pvHandle);
/**
 *@brief	�Ͽ�SSL����
 *@param  	pvHandle  SSL���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ��)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ��Ͽ�SSL����ʧ�ܣ�
*/
int NDK_SSLDisconnect(NDK_HANDLE pvHandle);
/**
 *@brief	���ӷ�����
 *@param  	pvHandle  	SSL���
 *@param  	pstServer  	��ַ
 *@param  	nTimeOut  	��ʱʱ�䣨ms��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�pServer��ַΪNULL��nTimeOut��ʱ����Ƿ�)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	�޷�����(�����׽���ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ����׽���ʧ�ܵȣ�
 *@li	\ref NDK_ERR_SSL_TIMEOUT "NDK_ERR_SSL_TIMEOUT"	���ӳ�ʱ
 *@li	\ref NDK_ERR_SSL_INVADDR "NDK_ERR_SSL_INVADDR"	��Ч��ַ�������ַת�����ַʧ�ܣ�
*/
int NDK_SSLConnect(NDK_HANDLE pvHandle, ST_SOCKET_ADDR *pstServer, int nTimeOut);
/**
 *@brief	��ȡ����ģʽ
 *@param  	pvHandle  	SSL���
 *@return
 *@li	\ref NDK_NOWAIT "NDK_NOWAIT"(������)		\ref NDK_SUSPEND "NDK_SUSPEND"(����)		�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ��)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	�޷�����(��ȡ�׽���ʧ��)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ��Ͽ�SSL����ʧ�ܣ�
*/
int NDK_GetSSLBlockingMode(NDK_HANDLE pvHandle);
/**
 *@brief	��������ģʽ
 *@param  	pvHandle  	SSL���
 *@param  	nMode    ����ģʽ(�ο�\ref EM_BLOCK_OPT "EM_BLOCK_OPT")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ��)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	�޷�����(��ȡ�׽���ʧ��)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�����fcntl()ʧ�ܷ��أ�
 *@li	\ref NDK_ERR_SSL_MODEUNSUPPORTED "NDK_ERR_SSL_MODEUNSUPPORTED"	ģʽ��֧�֣�nModeģʽ��֧�֣�
*/
int NDK_SetSSLBlockingMode(NDK_HANDLE pvHandle,int nMode);
/**
 *@brief	�������
 *@param  	pvHandle  	SSL���
 *@param  	psBuffer 	Ҫ���͵����
 *@param  	unBufferLen Ԥ�ڷ��ʹ�С
 *@retval 	punSendLen    ʵ�ʷ��ʹ�С
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�psBuffer/punSendLenΪNULL)
 *@li	\ref NDK_ERR_SSL_SEND "NDK_ERR_SSL_SEND"	���ʹ���(����SSL_write����ʧ�ܷ���)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�SSLδ���ӳɹ���
*/
int NDK_SSLSend(NDK_HANDLE pvHandle, const char *psBuffer, uint unBufferLen, uint *punSendLen);
/**
 *@brief	�������
 *@param  	pvHandle  	SSL���
 *@param  	pvBuffer 	Ҫ���͵����
 *@param  	unBufferLen Ԥ�ڽ��մ�С
 *@retval 	punRecvLen  ʵ�ʽ��մ�С
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�pvBuffer/punRecvLenΪNULL)
 *@li	\ref NDK_ERR_SSL_RECV "NDK_ERR_SSL_RECV"	���մ���(����SSL_read����ʧ�ܷ���)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ�SSLδ���ӳɹ���
*/
int NDK_SSLReceive(NDK_HANDLE pvHandle, void *pvBuffer, const uint unBufferLen, uint *punRecvLen);
/**
 *@brief	��
 *@param  	pvHandle  	SSL���
 *@param  	pstAddr 	��ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�pstAddrΪNULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_ALLOC "NDK_ERR_SSL_ALLOC"	�޷����䣨��ȡ�׽���ʧ�ܣ�
 *@li	\ref NDK_ERR_SSL_INVADDR "NDK_ERR_SSL_INVADDR"	��Ч��ַ��bindʧ�ܷ��أ�
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ����õ�ַ����ʧ�ܣ�
*/
int NDK_SSLBind(NDK_HANDLE pvHandle, ST_SOCKET_ADDR *pstAddr);
/**
 *@brief	��ȡ����״̬
 *@param  	pvHandle  	SSL���
 *@retval  	pnState  	����״̬(�ο�\ref EM_SSL_CONNECTION_STATE "EM_SSL_CONNECTION_STATE")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�pnStateΪNULL)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ�ܣ����õ�ַ����ʧ�ܣ�
*/
int NDK_GetSSLConnectStatus(NDK_HANDLE pvHandle, int *pnState);
/**
 *@brief	��������Ч��
 *@param  	pvHandle  	SSL���
 *@param  	unTimeOut  	��ʱʱ�䣨10s��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_SSL_PARAM "NDK_ERR_SSL_PARAM"	��Ч����(SSLδ�򿪡�timeout<0)
 *@li	\ref NDK_ERR_SSL_ALREADCLOSE "NDK_ERR_SSL_ALREADCLOSE"	�����ѹر�(SSL�����ѹر�)
 *@li	\ref NDK_ERR_SSL_CONNECT "NDK_ERR_SSL_CONNECT"	û�����ӣ�SSL����ʧ�ܣ�
 *@li	\ref NDK_ERR_SSL_TIMEOUT "NDK_ERR_SSL_TIMEOUT"	��ʱ����
*/
int NDK_SSLDataAvailable(NDK_HANDLE pvHandle, uint unTimeOut);


/** @} */ // SslSocketģ�����

/** @addtogroup Timerģ��
* @{
*/

#define ONEMILLISECOND 1     					/**<һ����*/
#define ONESECOND ( 100 * ONEMILLISECOND )  		/**<һ��*/
#define ONEMINUTE ( 60 * ONESECOND )	/**<һ����*/
typedef void        ( *pvFUNC )( void );
typedef const pvFUNC       pcFUNC;

/**
 *@brief	�ȴ�һ��ʱ��
 *@param  unWaitDuration  �ȴ��ʱ��(��λΪ10ms)
 *@return
 *@li	
 *@li	
*/
void NDK_Wait(uint  unWaitDuration);
/**
 *@brief	����һ����ʱ������û�����ñ�־����
 *@param  	unWaitDuration   �ȴ��ʱ��(��λΪ10ms)
 *@param  	punFlag          ��־�Ƿ����
 *@return
 *@li	Timer���				  �����ɹ�
 *@li	NULL 	            ����ʧ��
*/
NDK_HANDLE NDK_WaitAndFlag(uint unWaitDuration,uint *punFlag);
/**
 *@brief	ȡ��һ����ʱ����NDK_WaitAndFlag��������֮ǰ
 *@param  	pvHandle   Timer���
 *@return
 *@li	
 *@li	
*/
void NDK_CancelTimer(NDK_HANDLE  pvHandle);

/**
 *@brief	ע��һ��������ָ����ʱ����
 *@param  	unTimeInterval   ����ע��ȴ�ʱ��
 *@param  	pRoutine    �����ָ��
 *@return
 *@li	NDK_OK				  �����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pRoutineΪNULL��unTimeInterval<=0)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_RegisterTimerRoutine (uint unTimeInterval, pvFUNC pRoutine);


/** @} */ // Timerģ�����

/** @addtogroup Thread����ģ��
* @{
*/

typedef enum ndk_thread_state
{
	NDK_THREAD_STOPPED,		///< �߳�ֹͣ
	NDK_THREAD_RUNNING,		///< �߳�������
	NDK_THREAD_SUSPEND,		///< �߳���ͣ
} EM_THREAD_STATE;
/**
 *@brief	����һ���߳�
 *@param  ppvHandle �߳̾��
 *@param  psName   �߳����
 *@param  unStackSize  �߳�ջ��С
 *@param  pvEntryPoint �߳���ڵ�
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR_MACLLOC "NDK_ERR_MACLLOC"	�ڴ�ռ䲻��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(ppvHandleΪNULL��pvEntryPointΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_CreateThread( NDK_HANDLE *ppvHandle, const char* psName, uint unStackSize,
								void (*pvEntryPoint)( void ) );
/**
 *@brief	�����߳�
 *@param  	pvHandle    �߳̾��
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	���֧��
*/
int NDK_ResumeThread( NDK_HANDLE pvHandle );
/**
 *@brief	�����߳�
 *@param  	pvHandle    �߳̾��
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	���֧��
*/
int NDK_SuspendThread( NDK_HANDLE pvHandle );
/**
 *@brief	��ֹ�߳�
 *@param  	pvHandle    �߳̾��
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��(pvHandleΪNULL)
*/
int NDK_TerminateThread( NDK_HANDLE pvHandle );
/**
 *@brief	���������߳�����
 *@return
 *@li	NDK_OK				  �����ɹ�
*/
int NDK_Relinquish( void );
/**
 *@brief	��ȡ��ǰ�̵߳�״̬
 *@param  	pvHandle    �߳̾��
 *@retval 	pemState    �߳�״̬
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR_THREAD_CMDUNSUPPORTED "NDK_ERR_THREAD_CMDUNSUPPORTED"	���֧��
*/
int NDK_GetThreadState( NDK_HANDLE pvHandle, EM_THREAD_STATE *pemState );
/**
 *@brief	��ͣ�߳�
 *@param  	nWaitMs   Ҫ��ͣ��ʱ��
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_ThreadSleep( int nWaitMs );
/**
 *@brief	����һ���ź���
 *@param  	ppvHandle   �ź������
 *@return
 *@li	NDK_OK				        �����ɹ�
 *@li	\ref NDK_ERR_THREAD_ALLOC "NDK_ERR_THREAD_ALLOC"	��Ч����
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	��Ч����ppvHandleΪNULL��
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_CreateSemaphore( NDK_HANDLE *ppvHandle );
/**
 *@brief	��ȡ�ź���
 *@param  	pvHandle   �ź������
 *@return
 *@li	NDK_OK				  �����ɹ�
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	��Ч����ppvHandleΪNULL��
*/
int NDK_LockSemaphore( NDK_HANDLE pvHandle );
/**
 *@brief	�ͷ��ź���
 *@param  	pvHandle   �ź������
 *@return
 *@li	NDK_OK				  �����ɹ�
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	��Ч����ppvHandleΪNULL��
*/
int NDK_ReleaseSemaphore( NDK_HANDLE pvHandle );
/**
 *@brief	�ݻ�һ���ź���
 *@param  	pvHandle   �ź������
 *@return
 *@li	NDK_OK				  �����ɹ�
 *@li	\ref NDK_ERR_THREAD_PARAM "NDK_ERR_THREAD_PARAM"	��Ч����ppvHandleΪNULL��
*/
int NDK_DestroySemaphore( NDK_HANDLE pvHandle );


/** @} */ // Thread����ģ�����

/** @addtogroup U�̸�SD��
* @{
*/

typedef enum{
	UDISK = 0,    /**<ѡ��U��*/
	SDDISK =1     /**<ѡ��SD��*/
}EM_DISKTYPE;

typedef enum
{
	DISKMOUNTSUCC=1,         /**<U�̻�Sd������*/
	DISKNOTOPEN=2,           /**<U�̻�Sd��δ��*/
	DISKDRIVERLOADFAIL=3,    /**<U�̻�Sd�������ʧ��*/
	DISKMOUNTING=4,          /**<U�̻�Sd�����ڼ�����*/
	DISKNOEXIT=5,	         /**<δ��⵽SD��*/
	DISKTIMEOUT=6            /**<���SD����ʱ*/
}EM_DISKSTATE;

typedef struct{
	uint unFreeSpace;	/**<�����ÿռ��С*/
	uint unTotalSpace;	/**<�ܿռ��С*/
}ST_DISK_INFO;

/**
 *@brief	��U�̻�SD��(ע�⣺��U��ʹ�ù���У����pos�����д�ڵĲ�۾�ѡ���)
 *@param	emType	���ͣ�UDISK����ʾU��,SDDISK����ʾSD��.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@param	nTimeOut ���ó�ʱʱ��(��λ�룬0��ʾ�����0��ʾ�ڹ涨�ĳ�ʱʱ����û�з���DISKMOUNTSUCC��״̬����ʱ��ȡ����״̬ӦΪDISKTIMEOUT)
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	��Ч����nTimeOut<0��
 *@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	��֧�����ͣ�emTypeδ֧�����ͣ�
 *@li	\ref NDK_ERR_USDDISK_DRIVELOADFAIL "NDK_ERR_USDDISK_DRIVELOADFAIL"	�����ʧ��
 *@li	\ref NDK_ERR_USDDISK_IOCFAIL "NDK_ERR_USDDISK_IOCFAIL"	����ô���
*/
int NDK_DiskOpen(EM_DISKTYPE emType,int nTimeOut);
/**
 *@brief	��ȡU�̻�SD����Ϣ
 *@param	pszDiskDir	U�̻�SD����Ŀ¼
 *@param	pstInfo   ������Ϣ�ṹ��\ref ST_DISK_INFO "ST_DISK_INFO"
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	��Ч����pszDiskDir/pstInfoΪNULL��
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_DiskGetInfo(char *pszDiskDir,ST_DISK_INFO  *pstInfo);
/**
 *@brief	��ȡU�̻�SD��״̬
 *@param	emType	���ͣ�UDISK����ʾU��,SDDISK����ʾSD��.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@retval	pnDiskState   ״̬���ο�\ref EM_DISKSTATE "EM_DISKSTATE"��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	��Ч����pnDiskStateΪNULL��
 *@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	��֧������(emType����δ֧��)
*/
int NDK_DiskGetState(EM_DISKTYPE emType,int *pnDiskState);
/**
 *@brief	�ر�U�̻�SD��
 *@param	emType	���ͣ�UDISK����ʾU��,SDDISK����ʾSD��.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	��Ч����pnDiskStateΪNULL��
 *@li	\ref NDK_ERR_USDDISK_UNMOUNTFAIL "NDK_ERR_USDDISK_UNMOUNTFAIL"	����ʧ��
 *@li	\ref NDK_ERR_USDDISK_UNLOADDRIFAIL "NDK_ERR_USDDISK_UNLOADDRIFAIL"	ж����ʧ��
*/
int NDK_DiskClose(EM_DISKTYPE emType);
/**
 *@brief	��ȡU�̻�SD����Ŀ¼
 *@param	emType	���ͣ�UDISK����ʾU��,SDDISK����ʾSD��.\ref EM_DISKTYPE "EM_DISKTYPE")
 *@retval	pszRdir  ��Ŀ¼
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_USDDISK_PARAM "NDK_ERR_USDDISK_PARAM"	��Ч����pszRdirΪNULL��
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*@li	\ref NDK_ERR_USDDISK_NONSUPPORTTYPE "NDK_ERR_USDDISK_NONSUPPORTTYPE"	��֧������(emType����δ֧��)
*/
int NDK_DiskGetRootDirName(EM_DISKTYPE emType,char **pszRdir);

/** @} */ // U�̸�SD��ģ�����

/** @addtogroup WIFI
* @{
*/

typedef enum
{
    WIFI_NET_SEC_NONE,          /**<·������ʹ�ð�ȫ����ģʽ*/
    WIFI_NET_SEC_WEP_OPEN,      /**<·����ʹ�ÿ�����Կ��WEPģʽ*/
    WIFI_NET_SEC_WEP_SHARED,    /**<·����ʹ�ù�����Կ��WEPģʽ*/
    WIFI_NET_SEC_WPA,           /**<·����ʹ��WPAģʽ*/
    WIFI_NET_SEC_WPA2           /**<·����ʹ��WPA2ģʽ*/
} EM_WIFI_NET_SEC;

typedef enum {
    WIFI_KEY_TYPE_NOKEY,        /**<·������ʹ������*/
    WIFI_KEY_TYPE_HEX,          /**<·����ʹ��ʮ����Ƶ������ʽ*/
	WIFI_KEY_TYPE_ASCII         /**<·����ʹ��ASCII�������ʽ*/
}EM_WIFI_KEY_TYPE;

typedef struct
{
    uchar ucIfDHCP;             /**<�Ƿ�ʹ��DHCP*/
    EM_WIFI_KEY_TYPE emKeyType; /**<·�����������ʽ*/
    EM_WIFI_NET_SEC emSecMode;  /**<·�����İ�ȫ����ģʽ*/
    char *pszKey;               /**<·����������*/
    char *psEthIp;              /**<�ն�IP��ַ*/
    char *psEthNetmask;         /**<���������ַ*/
    char *psEthGateway;         /**<������ص�ַ*/
    char *psEthDnsPrimary;      /**<������DNS��ַ*/
    char *psEthDnsSecondary;    /**<�����DNS��ַ*/
} ST_WIFI_PARAM;

typedef enum
{
    WIFI_WPA_CONSTATE_LINKING,      /**<��������ָ����AP*/
    WIFI_WPA_CONSTATE_LINKED,       /**<�����ѻ��AP��Ӧ*/
    WIFI_WPA_CONSTATE_AUTHENTICATED,/**<�����ѻ��AP��֤*/
    WIFI_WPA_CONSTATE_CONTEXT       /**<����ָ����AP�ѳɹ�*/
} EM_WPA_CONSTATE;

#define WIFI_IW_ESSID_MAX_SIZE	128     /**<·��������������󳤶�*/
#define WIFI_IW_ENCODING_TOKEN_MAX	128 /**<·���������������󳤶�*/
typedef struct {
	char sEssid[WIFI_IW_ESSID_MAX_SIZE + 1];    /**<·�������*/
	char sKeyModeStr[128];                      /**<·�����İ�ȫ����ģʽ*/
	int nFrequency;                             /**<·������Ƶ��*/
	char sKey[WIFI_IW_ENCODING_TOKEN_MAX + 1];  /**<·����������*/
	char sSignal[16];                           /**<·�������ź�ֵ*/
}ST_WIFI_APINFO;

/**
 *@brief	ģ���ʼ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-�豸״̬����
 *@li	\ref NDK_ERR_WIFI_CMD_UNSUPPORTED "NDK_ERR_WIFI_CMD_UNSUPPORTED"	WIFI-��֧�ֵ�����
 *@li	\ref NDK_ERR_WIFI_SEARCH_FAULT "NDK_ERR_WIFI_SEARCH_FAULT"	WIFI-ɨ��״̬����
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-�豸������
*/
int NDK_WiFiInit(void);
/**
 *@brief	��ȡɨ�赽��AP��SSID��Ϣ
 *@retval	ppszESSIDlist	����ɨ�赽��AP��SSID��Ϣ�Ķ�ά����
 *@retval	pnNumList   ����ɨ�赽��AP�ĸ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-��Ч����ppszESSIDlist��pnNumListΪNULL��
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-�豸״̬����
*/
int NDK_WiFiGetNetList(char **ppszESSIDlist, int *pnNumList);
/**
 *@brief	��ȡָ��AP���ź�ֵ
 *@param	pszNetName	AP��SSID
 *@retval	pnSignal   ����AP���ź�ֵ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-��Ч����pszNetName��pnSignalΪNULL��
 *@li	\ref NDK_ERR_WIFI_DEVICE_NOTOPEN "NDK_ERR_WIFI_DEVICE_NOTOPEN"	WIFI-û��ɨ�赽AP
 *@li	\ref NDK_ERR_WIFI_DEVICE_TIMEOUT "NDK_ERR_WIFI_DEVICE_TIMEOUT"	WIFI-�豸��ʱ
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-�豸״̬����
*/
int NDK_WiFiSignalCover(const char *pszNetName, int *pnSignal);
/**
 *@brief	��ȡָ��AP�İ�ȫģʽ
 *@param	pszESSIDName	AP��SSID
 *@retval	pemSec	����AP�İ�ȫģʽ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-��Ч����pszESSIDName��pemSecΪNULL��
 *@li	\ref NDK_ERR_WIFI_DEVICE_NOTOPEN "NDK_ERR_WIFI_DEVICE_NOTOPEN"	WIFI-û��ɨ�赽AP
 *@li	\ref NDK_ERR_WIFI_DEVICE_TIMEOUT "NDK_ERR_WIFI_DEVICE_TIMEOUT"	WIFI-�豸��ʱ
*/
int NDK_GetWiFiSec(const char *pszESSIDName, EM_WIFI_NET_SEC *pemSec);
/**
 *@brief	���ӵ�ָ����AP
 *@param	pszESSIDName	AP��SSID
 *@param	pstParam	���Ӳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-��Ч����pszESSIDName��pstParamΪNULL��
 *@li	\ref NDK_ERR_WIFI_DEVICE_BUSY "NDK_ERR_WIFI_DEVICE_BUSY"	WIFI-�豸æ
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-�豸������
 *@li	\ref NDK_ERR_WIFI_UNKNOWN_ERROR "NDK_ERR_WIFI_UNKNOWN_ERROR"	WIFI-δ֪����
*/
int NDK_WiFiConnect(const char *pszESSIDName, const ST_WIFI_PARAM *pstParam);
/**
 *@brief	��ȡ����״̬��Ϣ
 *@retval	pemState	����״̬
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	WIFI-��Ч����pemStateΪNULL��
 *@li	\ref NDK_ERR_WIFI_PROCESS_INBADSTATE "NDK_ERR_WIFI_PROCESS_INBADSTATE"	WIFI-�޷����ӵ�AP
*/
int NDK_WiFiConnectState(EM_WPA_CONSTATE *pemState);
/**
 *@brief	�ж��Ƿ�������AP
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_DEVICE_UNAVAILABLE "NDK_ERR_WIFI_DEVICE_UNAVAILABLE"	WIFI-�豸������
*/
int NDK_WiFiIsConnected(void);
/**
 *@brief	ж��WIFIģ��
 *@return
 *@li	NDK_OK				�����ɹ�
*/
int NDK_WiFiShutdown(void);
/**
 *@brief	��ȡɨ�赽��AP��ȫ����Ϣ
 *@retval	pstList	����ɨ�赽��AP��Ϣ�Ľṹ������
 *@param	nMaxNum	ϣ���ȡ�������AP����
 *@retval	pnNumList   ����ɨ�赽��AP�ĸ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	����Ƿ�(pstList/pnNumListΪNULL��nMaxNum�Ƿ�)
 *@li	\ref NDK_ERR_WIFI_DEVICE_FAULT "NDK_ERR_WIFI_DEVICE_FAULT"	WIFI-�豸״̬����
*/
int NDK_WiFiGetNetInfo(ST_WIFI_APINFO *pstList, unsigned int unMaxNum, int *pnNumList);
/**
 *@brief	��ȡWIFI��MAC��ַ
 *@retval	pszMac	����MAC��ַ
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_WIFI_INVDATA "NDK_ERR_WIFI_INVDATA"	����Ƿ�(pszMacΪNULL)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��
*/
int NDK_WiFiGetMac(char *pszMac);

/** @} */ // WIFIģ�����
/** @addtogroup ɨ��
* @{
*/
typedef enum{
        SCAN_SETTYPE_FLOODLIGHT =0,                 /**<����������*/
        SCAN_SETTYPE_FOCUSLIGHT = 1,                /**<�Խ�������*/
        SCAN_SETTYPE_SENSITIVITY = 2,               /**<����������*/
        SCAN_SETTYPE_FACTORYDEFAULT = 3        /**<����Ĭ������*/
}EM_SCAN_SETTYPE;

/**
 *@brief	��ʼ��ɨ��ͷ����Ӳ���������ܽŵ�ƽ��ʼ��������Ĭ�����á�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��(ɨ���豸�ļ���ʧ��)
 *@li	\ref NDK_ERR "NDK_ERR"	����ʧ��(��ȡɨ��ͷ����ʧ��)
*/
int NDK_ScanInit(void);
/**
 *@brief	ɨ������(���\ref EM_SCAN_SETTYPE "EM_SCAN_SETTYPE"ѡ��Ҫ���õ��unSetValue��Ҫ���õ�
 ֵ��\n���������õ�ֵΪ0--��˸(����ʱ��˸���⣬������״̬ʱϨ��)��1--������(���κ�����������ƶ�Ϩ��)��2--���볣��(����ʱ�����Ƴ���⣬������״̬ʱϨ��)\n
            �Խ������õ�ֵΪ0--��˸(����ʱ��˸���⣬������״̬ʱϨ��)��1--�޶Խ�(���κ�����¶Խ��ƶ�Ϩ��)��2--��Ӧ(���Խ�����Ϊ��Ӧ�ƣ�����״̬��Ϩ�𣬷Ƕ���״̬�¿���)\n)
 *@param        emScanSet	��������
 *@param        unSetValue	����ֵ(������Խ����òο���������������������ֵΪ��ֵ��Χ1~20������ֵԽ�ͣ�������
Խ��.����Ĭ������(�����Խ�Ϊ��˸��������Ϊ11))
 *@return
 *@li	NDK_OK					�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR"       			����ʧ�ܣ�ɨ������û�гɹ���
 *@li 	\ref NDK_ERR_PARA "NDK_ERR_PARA"    		�������ɨ�����õ�ֵ��Ч��
 *@li 	\ref NDK_ERR_NOT_SUPPORT "NDK_ERR_NOT_SUPPORT"		δ֧�֣��е�ɨ��ͷ��֧��ĳ�����ã�����EM1300�Ͳ�֧�ֶ������Խ��Ƶ����ã�
*/
int NDK_ScanSet(EM_SCAN_SETTYPE emScanSet,uint unSetValue);
/**
 *@brief	ɨ�裨�ú���֧��ȡ����˳����˳�����ͨ�����ó�ʱʱ������ڳ�ʱʱ�����δɨ�赽����˳�����
 *@param        nTimeOut       ��ʱ����(��λΪ��)
 *@retval	    pszValue       ɨ����ݻ�����
 *@retval       pnLen          ɨ�赽����ݳ���
 *@return
 *@li	NDK_OK					�����ɹ�
 *@li	\ref NDK_ERR_TIMEOUT "NDK_ERR_TIMEOUT"      ��ʱ����ɨ�賬ʱ��
 *@li 	\ref NDK_ERR_PARA "NDK_ERR_PARA"    		�������pszValue��pnLenΪNULL��
 *@li 	\ref NDK_ERR_OVERFLOW "NDK_ERR_OVERFLOW"    �������
*/
int NDK_ScanDoScan(int nTimeOut,char *pszValue,int *pnLen);

/** @} */ // ɨ��ģ�����

/** @addtogroup ��Ƶ
* @{
*/

typedef enum{
        VOICE_CTRL_START = 0,   /**<���²���*/
        VOICE_CTRL_RESUME = 1,  /**<�ָ�����*/
        VOICE_CTRL_PAUSE = 2,   /**<��ͣ����*/
        VOICE_CTRL_STOP = 3,    /**<ֹͣ����*/
        VOICE_CTRL_MUTE = 4,    /**<�򿪾���*/
        VOICE_CTRL_NMUTE = 5,   /**<�رվ���*/
        VOICE_CTRL_VOLUME = 6,  /**<��������*/
}EM_VOICE_CTRL;
/**
 *@brief	������Ƶ�ļ�
 *@param        unVoiceId	��ƵID
 *@param        pszFile	��Ƶ�ļ�·��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
*@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(pszFileΪNULL,��Ƶ�ļ�������)
*@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��(������Ƶ�ļ�ʧ�ܣ�������Ƶ�ļ���ʽ���Ե�)
*/

int NDK_VoiceLoadFile(uint unVoiceId,char *pszFile);
/**
 *@brief	���ſ��ơ�������ǰ�������\ref NDK_VoiceLoadFile "NDK_VoiceLoadFile����"����Ӧ����Ƶ�ļ����룬��Ƶ���뵽�����У�ͨ��id���ſ��Ʋ�ͬ����Ƶ�ļ�����
 *@param        unVoiceId	��ƵID
 *@param        emCtrlId	����ѡ��
 *@param        unValue	���� ������ֵ��0-4��Χ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_OPEN_DEV "NDK_ERR_OPEN_DEV"	�豸�ļ���ʧ��
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"	����Ƿ�(emCtrlId�Ƿ��������Ƿ�)
 *@li	\ref NDK_ERR "NDK_ERR"       		����ʧ��(������Ƶ�ļ�ʧ�ܣ����ܸ�ID��Ӧ����Ƶ�ļ�δ����NDK_VoiceLoadFile���м��ء�)
 *@li	\ref NDK_ERR_IOCTL "NDK_ERR_IOCTL"	��ӿڵ��ô���(��Ƶ��ӿڵ���ʧ�ܷ���)
*/
 int NDK_VoiceCtrl(uint unVoiceId,EM_VOICE_CTRL emCtrlId,uint unValue);

/** @} */ // ��Ƶģ�����
int NDK_BTStatus(int * status);
int NDK_BTReset(void);
int NDK_BTGetLocalName(char * name);
int NDK_BTGetLocalMAC(char * mac);
int NDK_BTGetPIN(char * pinstr);
int NDK_BTEnterCommand(void);
int NDK_BTExitCommand(void);
int NDK_BTSetLocalName(const char * name);
int NDK_BTSetPIN(const char * pinstr);
int NDK_BTSetChannel(const char channel);
int NDK_BTGetChannel(char * channel);
int NDK_BTSetPINCode(const char pincode);
int NDK_BTDisconnect(void);
int NDK_PlayVoice(int id);
int NDK_SysEnterBoot(void);
int NDK_SysPeerOper(EM_SYS_PEEROPER oper);
int NDK_KbScanCode(int *code);
int NDK_RfidTypeARats(uchar cid,int *pnDatalen, uchar *psDatabuf);
int NDK_SysKeyVolSet(uint sel);
int NDK_BTSetLocalMAC(const char * mac);
int NDK_BTSetDiscoverableStatus(const char status);
/**
 *@brief �������ģʽ
*/
typedef enum {
	PAIRING_MODE_JUSTWORK=0,	/**<�������ģʽ*/
	PAIRING_MODE_PINCODE=1,		/**<PINCODEģʽ���ֻ�����POS����PIN*/
	PAIRING_MODE_SSP=2,			/**<SSP ģʽ*/
	PAIRING_MODE_PASSKEY=3,		/**<PASS KEYģʽ */
}EM_PAIRING_MODE;

/**
 *@brief	�����������ģʽ
 *@param    emMode: EM_PAIRING_MODE ���͵Ĳ���
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA 	����� EM_PAIRING_MODE�Ĳ��� ���� "NDK_ERR_PARA"
 *@li	\ref NDK_ERR        "NDK_ERR" ����ʧ��
 *@li	\ref NDK_ERR_IOCTL  "NDK_ERR_IOCTL"	��ӿڵ��ô���
 */
int NDK_BTSetPairingMode(EM_PAIRING_MODE emMode);

/**
 *@brief	�ȴ��������
 *@out      pszKey: a)SSP PINģʽ���ֻ��POSͬʱ��ʾ����룩��pszKey�����ֻ�����ʾ������룬
 *    				b)PassKeyģʽ���ֻ���ʾ����룬POS�����ֻ���ʾ������룩��pszKey[0]����'\0',
 *		    		      �����յ��ֻ��������POS�����ֻ���ʾ�������
 *@out		pnStatus: 1:�յ��ֻ��������2: ��Գɹ��� 3�����ʧ��
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA 	pszKey ���� pnStatus ΪNULL ���� "NDK_ERR_PARA"
 *@li	\ref NDK_ERR        "NDK_ERR" ����ʧ��
 *@li	\ref NDK_ERR_IOCTL  "NDK_ERR_IOCTL"	��ӿڵ��ô���
 */
int NDK_BTGetPairingStatus(char * pszKey, int *pnStatus);

/**
 *@brief	�������ȷ��
 *@param	pszKey�� a)SSP PINģʽ���ֻ��POSͬʱ��ʾ�����ģʽ����pszKey����ΪNDK_BTPairGetStatus������ȡ����key
 *					b)PassKeyģʽ���ֻ���ʾ����룬POS�����ֻ���ʾ�������ģʽ����pszKeyΪ���������key
 *@param	unConfirm : 0:ȡ����ԣ�1���������
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA 	pszKey ΪNULL ���� "NDK_ERR_PARA"
 *@li	\ref NDK_ERR        "NDK_ERR" ����ʧ��
 *@li	\ref NDK_ERR_IOCTL  "NDK_ERR_IOCTL"	��ӿڵ��ô���
 */
int NDK_BTConfirmPairing(const char * pszKey, uint unConfirm);

/**
 *@brief    �����������ͣ�������OPEN֮�����
 *@param    type:3���ֽ� {0x0c, 0x02, 0x5a}��ʾ�ֻ�  {0x04, 0x04, 0x24}��ʾ���
 *@return
 *@li   NDK_OK              �����ɹ�
 *@li   \ref NDK_ERR_PARA   type ΪNULL ���� "NDK_ERR_PARA"
 *@li   \ref NDK_ERR_IOCTL  "NDK_ERR_IOCTL" ��ӿڵ��ô���
 */
int NDK_BTSetType(const char * type);

/**
 *@brief ����ָ��������Կ, ע����ָ����Կ�������ִ��ɾ������������ֱ�ӷ��سɹ�:NDK_OK
 *@param ucKeyIdx
��Կ���
 *@param ucKeyType
��Կ����,��ֵ������EM_SEC_KEY_TYPE�ж��������
 *@return
 *@li NDK_OK
�����ɹ�
 *@li ����EM_NDK_ERR
����ʧ��
*/
int NDK_SecKeyDelete(uchar ucKeyIdx,uchar ucKeyType);


/**
 *@brief	�ű���ӡ�ӿ�
 *@param	prndata		�ű���ӡ���(�ű���ӡ�����ʽ���߶�ƽָ̨��淶���еĸ�¼A)
 *@param	indata_len	�ű������
 *@return
 *@li	NDK_OK			�����ɹ�
 *@li	\ref NDK_ERR "NDK_ERR" 			����ʧ��,�����������
 *@li   \ref EM_PRN_STATUS   "EM_PRN_STATUS"   ��ӡ��״ֵ̬
*/
int NDK_Script_Print(char* prndata,int indata_len);


/**
 *@brief 	�ļ�����
 *@details	��pad�˵�ָ���ļ�������k21��/appfs/Ŀ¼�£��ļ���Ȳ��ܳ���12���ֽ�
 *@param    sourcefile ԭ�ļ���(��·��)
 *@param    destfile 	 Ŀ���ļ����·��/appfs/��
 *@return
 *@li	 NDK_OK				�����ɹ�����
 *@li	 -1			 			����
*/
int NDK_CopyFileToSecMod(const unsigned char* sourcefile, const unsigned char* destfile);

/**
 *@brief	��ȡk21��MAPP�汾��
 *@retval   pszVer	�汾���ַ�,�����С������16�ֽ�
 *@return
 *@li	NDK_OK				�����ɹ�
 *@li	\ref NDK_ERR_PARA "NDK_ERR_PARA"		����Ƿ�(pszVerΪNULL)
*/
int NDk_SysGetK21Version(char *version);

/**
 *@brief ��ѯϵͳ�Ƿ��ڿ���״̬
 *@param ��
 *@return
 *@li NDK_ERR_ICCARD_BUSY IC��æ
 *@li NDK_ERR_RFID_BUSY   ��Ƶæ
 *@li NDK_ERR_PRN_BUSY    ��ӡ��æ
 *@li NDK_ERR_PIN_BUSY    ������æ
 *@li NDK_ERR_IOCTL       IOCTL����ʧ��
�����ɹ�
 *@li NDK_OK ϵͳ���ڿ���״̬
����ʧ��
*/
int NDK_SysReadyToSuspend(void);

/**
 *@brief	���߽ӿڣ���������ָ���K21����K21���غ��ٽ�������
 *@return
 *@li	NDK_OK				�����ɹ�
*/
int NDK_SysGoSuspend_Extern(void);

/**
 *@brief	����K21
 *@return
 *@li	NDK_OK				�����ɹ�
*/
int NDK_SysWakeUp(void);

//���ڻ�ȡ��ȫ�Ĵ���ֵ
int NDK_SecGetDrySR(int *pnVal);

//�����尲ȫ�Ĵ���ֵ
int NDK_SecClear(void);


#endif
/* End of this file */

