#include "NDK.h"
#include <jni.h>
#include <string.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C"
{
#endif

extern char *JbyteArrayTochar(JNIEnv *env, jbyteArray barr);
extern char *JstringTochar(JNIEnv *env, jstring jstr);
extern jstring chartoJstring(JNIEnv* env, const char* p);
extern jbyteArray chartobyteArray(JNIEnv* env, const char* p,int len);
extern jbyte GetMethodByteValue(JNIEnv *env,jobject object, char * method);
extern jbyteArray GetMethodByteArrayValue(JNIEnv *env,jobject object, char * method);
extern jstring GetMethodStringValue(JNIEnv *env,jobject object, char * method);
extern int GetEnum(JNIEnv *env,jobject object);

#ifdef __cplusplus
}
#endif
