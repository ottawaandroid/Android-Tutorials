LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := pinul
LOCAL_CFLAGS := -W 
LOCAL_LDLIBS := -llog
LOCAL_ARM_MODE := arm
LOCAL_SRC_FILES := pinul.c pi8.c

include $(BUILD_SHARED_LIBRARY)

