#include <stdio.h>
#include <termios.h>
#include <fcntl.h>
#include <unistd.h>
#include <android/log.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <jni.h>
#include "env.h"

JavaVM * gJavaVM;
jobject  gJavaObj;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;

//	LOGI("JNI_OnLoad...");
	if ((*vm)->GetEnv(vm,(void**)&env, JNI_VERSION_1_4) != JNI_OK) {
//		LOGE("GetEnv failed!");
		return result;
	}

	result = JNI_VERSION_1_4;
	gJavaVM = vm;
end:
	return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;

//	LOGI("JNI_OnUnload...");

	if ((*vm)->GetEnv(vm,(void**)&env, JNI_VERSION_1_4) != JNI_OK) {
//		LOGE("GetEnv failed!");
		return;
	}
}

