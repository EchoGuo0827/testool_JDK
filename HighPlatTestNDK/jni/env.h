#include <jni.h>

#ifndef __ENV__H
#define __ENV__H

#ifdef __cplusplus
extern "C" {
#endif

#define LOG_TAG "ndk_log"

#define _DEBUG 1

#ifdef _DEBUG
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)
#else
#define LOGI(...) 	;
#define LOGD(...) 	;
#define LOGE(...) 	;
#endif


#if _DEBUG
	#define DEBUG	newland_printf
	#define DEBUG_BUF(BUF,LEN) {\
		int temp;\
		int offset=0;\
		char s[4096];\
		memset(s, 0, sizeof(s));\
		for(temp=0; temp< LEN; temp++) {\
			offset += sprintf(s + offset, "%02x ", BUF[temp]);\
		}\
		s[offset-1] = '\n';\
		DEBUG("%s", s);\
	}
#else
	#define DEBUG(...)		{}
	#define DEBUG_BUF(BUF,LEN)	{}
#endif


#ifdef __cplusplus
}
#endif

#endif

