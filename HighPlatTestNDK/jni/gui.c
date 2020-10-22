#define JAVA_CLASS_NAME 				"com/example/highplattest/main/tools/Gui"
#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "inc/NDK.h"

#include "env.h"

extern JavaVM * gJavaVM;
extern int g_UCID;
extern int g_SequencePressFlag;
extern int auto_flag;

JNIEnv *envLocal;

jclass gui_class;
jmethodID constructor;
jobject gui_object;

//int t_print(char **out, const char *format, va_list args);

//char* to jstring
jbyteArray stoJstring(JNIEnv* env, jbyteArray pat)
{
	LOGD("enter stoJstring");

//	jbyte* bytes = (*env)->GetByteArrayElements(env,pat,NULL);
//	jint len = (*env)->GetArrayLength(env,pat);
//	LOGD("len:"+len);
	jbyteArray bytes = (*env)->NewByteArray(env,strlen(pat)); // 字节数组需要释放
	(*env)->SetByteArrayRegion(env,bytes, 0, strlen(pat), (jbyte*)pat);
	// 释放所有对object的引用
//	(*env)->ReleaseByteArrayElements(env,bytes,(jbyte *)pat,0);
//	(*env)->DeleteLocalRef(env,bytes);
	return bytes;
}

jintArray intToIntArray(JNIEnv* env, jintArray pat)
{
	LOGD("enter intToIntArray");
//	jbyte* bytes = (*env)->GetByteArrayElements(env,pat,NULL);
//	jint len = (*env)->GetArrayLength(env,pat);
	jintArray bytes = (*env)->NewIntArray(env,1); // 字节数组需要释放
	(*env)->SetIntArrayRegion(env,bytes, 0, 1, (jint*)pat);
	// 释放所有对object的引用
//	(*env)->ReleaseByteArrayElements(env,bytes,(jbyte *)pat,0);
//	(*env)->DeleteLocalRef(env,bytes);
	return bytes;
}


int wait_key(float timeout)
{
	LOGD("enter wait_key");
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"wait_key","(F)I");
	if(method1==0)
	{
		return -1;
	}

	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,timeout);
	return ret;
	}

// 王震懿建议把methodID修改为全局的，这样可避免每次查找的时间
int cls_printf_android(const char* msg,...)
{
	/*private & local definition*/
	int cnt = 0;
	va_list args;
	char tips[512] = {0};

	char* p_tips = tips;
	va_start(args, msg);
	cnt = t_print(&p_tips,msg,args);
	va_end(args);

	// 回调操作,使用全局对象表面多次调用该函数的时候多次实例化操作
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"cls_printf","([B)I");
	if(method1==0)
	{
		return -1;
	}
	jbyteArray bytes = stoJstring(envLocal,tips);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes);
	// 内存空间释放
//	(*envLocal)->DeleteLocalRef(envLocal,gui_class);
//	(*envLocal)->DeleteLocalRef(envLocal,gui_object);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)tips,0);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
	}

int cls_show_msg1_record_android(char* filename,char* funname,int time,const char* msg,...)
{
	/*private & local definition*/
	int cnt = 0;
	va_list args;
	char tips[512] = {0};
	char* p_tips = tips;
	va_start(args, msg);
	cnt = t_print(&p_tips,msg,args);
	va_end(args);

	// 回调操作
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"cls_show_msg1_record","(I[BII)I");
	if(method1==0)
	{
		return -1;
	}

	jbyteArray bytes = stoJstring(envLocal,tips);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,time,bytes,auto_flag,g_SequencePressFlag);
	/*// 获取返回值操作
	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");
	jint key_value = (jint)(*envLocal)->GetIntField(envLocal,jniDemo, fid);*/
//	(*envLocal)->DeleteLocalRef(envLocal,gui_class);
//	(*envLocal)->DeleteLocalRef(envLocal,gui_object);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)tips,0);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
	}

int cls_show_msg_record_android(char* filename,char* funname,const char* msg,...)
{
	/*private & local definition*/
	int cnt = 0;
	va_list args;
	char tips[512] = {0};
	char* p_tips = tips;
	va_start(args, msg);
	cnt = t_print(&p_tips,msg,args);
	va_end(args);

	// 回调操作
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"cls_show_msg_record","([BII)I");
	if(method1==0)
	{
		return -1;
	}

	jbyteArray bytes = stoJstring(envLocal,tips);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes,auto_flag,g_SequencePressFlag);
	// 获取返回值操作
//	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");

//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)tips,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
	}

int cls_show_msg_android(char* msg, ...)
{
	if(msg==NULL)
		return 0x0D;
	/*private & local definition*/
	int cnt = 0;
	va_list args;
	char tips[2048] = {0};
	char* p_tips = tips;
	va_start(args, msg);
	cnt = t_print(&p_tips,msg,args);
	va_end(args);

	// 回调操作
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"cls_show_msg","([BII)I");
	if(method1==0)
	{
		return -1;
	}

	jbyteArray bytes = stoJstring(envLocal,tips);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes,auto_flag,g_SequencePressFlag);
	/*// 获取返回值操作
	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");
	jint key_value = (jint)(*envLocal)->GetIntField(envLocal,jniDemo, fid);*/
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)tips,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
}



int cls_show_msg1_android(int time,char* msg, ...)
{
	if(msg==NULL)
		return 0x0D;
	/*private & local definition*/
	int cnt = 0;
	va_list args;
	char tips[512] = {0};
	char* p_tips = tips;
	va_start(args, msg);
	cnt = t_print(&p_tips,msg,args);
	va_end(args);

	if(gui_class==NULL)
	{
		LOGD("enter creat gui_class");
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"cls_show_msg1","(I[BII)I");
	if(method1==0)
	{
		return -1;
	}

	jbyteArray bytes = stoJstring(envLocal,tips);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,time,bytes,auto_flag,g_SequencePressFlag);
	/*// 获取返回值操作
	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");
	jint key_value = (jint)(*envLocal)->GetIntField(envLocal,jniDemo, fid);*/

	// 释放对象
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)tips,0);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
}

int ShowMessageBox_android(char* pMsg,char cStyle,int iWaitTime)
{
	/*private & local definition*/
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_object, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"ShowMessageBox","([BBI)I");
	if(method1==0)
	{
		return -1;
	}
	jbyteArray bytes = stoJstring(envLocal,pMsg);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes,cStyle,iWaitTime);
	/*// 获取返回值操作
	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");
	jint key_value = (jint)(*envLocal)->GetIntField(envLocal,jniDemo, fid);
	LOGD("keyvalue %d",key_value);*/
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)pMsg,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
}

int lib_kbgetinput_android(char* pszBuf,int unMin,int unMaxLen,int *punLen,EM_INPUTDISP emMode,int unWaittime,EM_INPUT_CONTRL emControl)
{
	/*private & local definition*/
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"lib_kbgetinput","([BII)I");
	if(method1==0)
	{
		return -1;
	}
	// pszBuf中有可能包含默认值
	jbyteArray bytes = stoJstring(envLocal,pszBuf);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes,unMaxLen,unWaittime);

	jfieldID fid = (*envLocal)->GetFieldID(envLocal,gui_class, "pszBufNDK", "Ljava/lang/String;");
	jstring pszRetBuf = (jstring)(*envLocal)->GetObjectField(envLocal,gui_object, fid);
	char* psztemp;
	if(pszRetBuf!=NULL)
	{
		psztemp = (char *)(*envLocal)->GetStringUTFChars(envLocal,pszRetBuf,NULL);
		strcpy(pszBuf, psztemp);
	}

	if(pszRetBuf!=NULL)
	{
		(*envLocal)->ReleaseStringUTFChars(envLocal,pszRetBuf,psztemp);
	}
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)pszBuf,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return ret;
}

// 获取单个按键值
int lib_getkeycode_android(uint sec)
{
	/*private & local definition*/
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"lib_getkeycode","(I)I");
	if(method1==0)
	{
		return -1;
	}
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,sec);
	/*// 获取返回值操作
	jfieldID fid = (*envLocal)->GetFieldID(envLocal,class, "keyValue", "I");
	jint key_value = (jint)(*envLocal)->GetIntField(envLocal,jniDemo, fid);*/
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
	return ret;
}

/**
 * 测试报告输出
 */
void send_result_android(char *fmt,...)
{
	/*private & local definition*/
	char tmp[512]={0},result[400]={0};
	va_list argptr;
	char * p_result = result;


	/*process body*/
	va_start(argptr,fmt);
	t_print(&p_result,fmt,argptr);
	va_end(argptr);

	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"send_result","(I[BII)V");
	if(method1==0)
	{
		return;
	}

	jbyteArray bytes = stoJstring(envLocal,result);
	(*envLocal)->CallVoidMethod(envLocal,gui_object,method1,g_UCID,bytes,auto_flag,g_SequencePressFlag);

	// 内存释放
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)result,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	}

/**
 * 此为清屏操作
 */
int NDK_ScrClrs_android()
{
	/*private & local definition*/
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"NDK_ScrClrs","()I");
	if(method1==0)
	{
		return -1;
	}
	(*envLocal)->CallIntMethod(envLocal,gui_object,method1);
	// 内存释放
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
	return 0;
	}

/**
 * 打印内容
 */
int NDK_ScrPrintf_android(const char *psFormat,...)
{
	/*private & local definition*/
	char tmp[512]={0},result[400]={0};
	va_list argptr;
	char * p_result = result;

	/*process body*/
	va_start(argptr,psFormat);
	t_print(&p_result,psFormat,argptr);
	va_end(argptr);

	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"NDK_ScrPrintf","([B)I");
	if(method1==0)
	{
		return -1;
	}

	jbyteArray bytes = stoJstring(envLocal,result);
	(*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes);

	// 内存释放
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
//	(*envLocal)->ReleaseByteArrayElements(envLocal,bytes,(jbyte *)result,JNI_ABORT);
	(*envLocal)->DeleteLocalRef(envLocal,bytes);
	return 0;
	}

/**
 * 屏幕刷新操作
 */
int NDK_ScrRefresh_android()
{
	/*private & local definition*/
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==0)
		{
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}

	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"NDK_ScrRefresh","()I");
	if(method1==0)
	{
		return -1;
	}
	(*envLocal)->CallIntMethod(envLocal,gui_object,method1);
	// 内存释放
//	(*envLocal)->DeleteLocalRef(envLocal,class);
//	(*envLocal)->DeleteLocalRef(envLocal,jniDemo);
	return 0;
	}

int ndk_get_touch_value(int* touch_width,int* touch_height,int* head_value,int* bottom_value)
{
	/*private & local definition*/
	LOGD("ndk_get_touch_value");
	// 回调操作
	if(gui_class==NULL)
	{
		gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
		if(gui_class==NULL)
		{
			LOGD("fail 1");
			return -1;
		}
		constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
		if(constructor==NULL)
		{
			LOGD("fail 2");
			return -1;
		}
		gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
	}
	jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"ndk_touchscreen_getnum","([I[I[I[I)I");
	LOGD("ndk_get_touch_value");
	if(method1==0)
	{
		return -1;
	}

	jintArray bytes1 = intToIntArray(envLocal,touch_width);
	jintArray bytes2 = intToIntArray(envLocal,touch_height);
	jintArray bytes3 = intToIntArray(envLocal,head_value);
	jintArray bytes4 = intToIntArray(envLocal,bottom_value);
	jint ret = (*envLocal)->CallIntMethod(envLocal,gui_object,method1,bytes1,bytes2,bytes3,bytes4);
	if(ret==-1)
		return ret;

	LOGD("copy java to c");
	int *data1;
	int *data2;
	int *data3;
	int *data4;
	data1 = (*envLocal)->GetIntArrayElements(envLocal,bytes1,NULL);
	data2 = (*envLocal)->GetIntArrayElements(envLocal,bytes2,NULL);
	data3 = (*envLocal)->GetIntArrayElements(envLocal,bytes3,NULL);
	data4 = (*envLocal)->GetIntArrayElements(envLocal,bytes4,NULL);
	*touch_width = *data1;
	*touch_height = *data2;
	*head_value = *data3;
	*bottom_value = *data3;
	return ret;
	}

JNIEXPORT jint JNICALL Java_com_example_highplattest_main_tools_Gui_testMain(JNIEnv *env,jobject obj)
{
	int ret = (*gJavaVM)->AttachCurrentThread(gJavaVM, (JNIEnv **) &envLocal, NULL);
	if (ret < 0)
		return ret;
	int iRet=0;
	iRet = main();
	/*int touch_width;
	int touch_height;
	int head_value;
	int bottom_value;
	ret = ndk_get_touch_value(&touch_width,&touch_height,&head_value,&bottom_value);
	LOGD("ret=%d",ret);
	LOGD("touch_width=%d,touch_height=%d,head_value=%d,bottom_value=%d",touch_width,touch_height,head_value,bottom_value);*/

	// 返回-1已返回四图标界面，杀死目前的进程
	if(iRet==-1)
	{
		if(gui_class==NULL)
		{
			gui_class = (*envLocal)->FindClass(envLocal,JAVA_CLASS_NAME);
			if(gui_class==0)
			{
				return -1;
			}
			constructor = (*envLocal)->GetMethodID(envLocal, gui_class,"<init>", "()V");
			gui_object = (*envLocal)->NewObject(envLocal,gui_class, constructor);
		}
		jmethodID method1 = (*envLocal)->GetMethodID(envLocal,gui_class,"exitNDK","()V");
		if(method1==0)
		{
			LOGD("method show ID not found");
			return -1;
		}
		(*envLocal)->CallVoidMethod(envLocal,gui_object,method1);
		(*gJavaVM)->DetachCurrentThread(gJavaVM);//从jvm里卸载
	}
	return 0;
	}
