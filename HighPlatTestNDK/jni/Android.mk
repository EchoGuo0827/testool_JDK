LOCAL_PATH := $(call my-dir)

#################################################################
# libmain.a
#################################################################
include $(CLEAR_VARS)

LOCAL_MODULE    := libmain

LOCAL_SRC_FILES := libmain.a

LOCAL_EXPORT_C_INCLUDES:= inc

include $(PREBUILT_STATIC_LIBRARY)

##################################################################
## libndktest.so
##################################################################
#include $(CLEAR_VARS)
#
#LOCAL_MODULE:=libndktest
#
#LOCAL_LDFLAGS+=-Wl,--whole-archive
#
#LOCAL_LDFLAGS+= libmain.a
#
#LOCAL_LDFLAGS+=-Wl,--no-whole-archive
##-lnlposapi
#LOCAL_LDLIBS+= -llog -lnlposapi 
#
#include $(BUILD_SHARED_LIBRARY)

#################################################################
# libjnindk.so
#################################################################
include $(CLEAR_VARS) 

LOCAL_MODULE    := libjnindk 

LOCAL_SRC_FILES :=  onload.c gui.c

#LOCAL_C_INCLUDES := inc

LOCAL_LDLIBS += -llog
#-lnlprnapi
LOCAL_STATIC_LIBRARIES :=libmain


LOCAL_LDFLAGS += -Wl,--export-dynamic

#LOCAL_LDFLAGS += -fuse-ld=bfd

include $(BUILD_SHARED_LIBRARY) 

#################################################################
# libserial_port.so
#################################################################

#include $(CLEAR_VARS)
#
#TARGET_PLATFORM := android-3
#LOCAL_MODULE    := serial_port
#LOCAL_SRC_FILES := SerialPort.c
#LOCAL_LDLIBS    := -llog
#
#include $(BUILD_SHARED_LIBRARY)
