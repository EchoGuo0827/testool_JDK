#include <jni.h>
#include <string.h>
#include <android/log.h>

#include "env.h"

char *JbyteArrayTochar(JNIEnv *env, jbyteArray barr)
{
	char * rtn = NULL;
	jsize alen = (*env)->GetArrayLength(env,barr);
	jbyte * ba = (*env)->GetByteArrayElements(env,barr,JNI_FALSE);
	if(alen > 0)
	{
		rtn = (char *) malloc (alen+1);
		memcpy(rtn,ba,alen);
		rtn[alen]=0;
	}
	(*env)->ReleaseByteArrayElements(env,barr,ba,0);

	return rtn;
}

char *JstringTochar(JNIEnv *env, jstring jstr)
{
	char * rtn = NULL;
	jclass clsstring = (*env)->FindClass(env,"java/lang/String");
	jstring strPrint = (*env)->NewStringUTF(env,"GBK");
	jmethodID mid = (*env)->GetMethodID(env,clsstring, "getBytes", "(Ljava/lang/String;)[B");
	jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env,jstr,mid,strPrint);
	jsize alen = (*env)->GetArrayLength(env,barr);
	jbyte * ba = (*env)->GetByteArrayElements(env,barr,JNI_FALSE);
	if(alen > 0)
	{
		rtn = (char *) malloc (alen+1);
		memcpy(rtn,ba,alen);
		rtn[alen]=0;
	}
	(*env)->ReleaseByteArrayElements(env,barr,ba,0);

	return rtn;
}

jstring chartoJstring(JNIEnv* env, const char* p)
{
	jclass strClass = (*env)->FindClass(env, "java/lang/String");

	jstring encoding = (*env)->NewStringUTF(env,"GBK");
	jmethodID ctorID = (*env)->GetMethodID(env,strClass, "<init>", "([BLjava/lang/String;)V");
	jbyteArray bytes = (*env)->NewByteArray(env, strlen(p));
	(*env)->SetByteArrayRegion(env, bytes, 0, strlen(p), (jbyte*)p);

	return (jstring)(*env)->NewObject(env, strClass, ctorID, bytes, encoding);
}

jbyteArray chartobyteArray(JNIEnv* env, const char* p,int len)
{
	jbyteArray str;

	str = (*env)->NewByteArray(env, len);
	(*env)->SetByteArrayRegion(env, str, 0, len, (jbyte*)p);

	return str;
}

jbyte GetMethodByteValue(JNIEnv *env,jobject object, char * method)
{
	jclass class= (*env)->GetObjectClass(env,object);
	if(class == NULL) {
		return;
	}
	jmethodID getVal = (*env)->GetMethodID(env,class, method, "()B");
	jbyte value = (*env)->CallByteMethod(env,object, getVal);

	return value;
}

jbyteArray GetMethodByteArrayValue(JNIEnv *env,jobject object, char * method)
{
	jclass class= (*env)->GetObjectClass(env,object);
	if(class == NULL) {
		LOGD("get class failed\n");
		return;
	}
	jmethodID getVal = (*env)->GetMethodID(env,class, method, "()[B");

	jobject value = (*env)->CallObjectMethod(env,object, getVal);

	return (jbyteArray)value;
}

jstring GetMethodStringValue(JNIEnv *env,jobject object, char * method)
{
	jclass class= (*env)->GetObjectClass(env,object);
	if(class == NULL) {
		LOGD("get class failed\n");
		return;
	}
	jmethodID getVal = (*env)->GetMethodID(env,class, method, "()Ljava/lang/String;");

	jobject value = (*env)->CallObjectMethod(env,object, getVal);

	return (jbyteArray)value;
}

int GetEnum(JNIEnv *env,jobject object)
{
	jclass enumclass= (*env)->GetObjectClass(env,object);
	if(enumclass == NULL) {
		printf(" get enumclass failed\r\n");
		return;
	}
	jmethodID getVal = (*env)->GetMethodID(env,enumclass, "getValue", "()I");
	jint value = (*env)->CallIntMethod(env,object, getVal);

	return value;
}
