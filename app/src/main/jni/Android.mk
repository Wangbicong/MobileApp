LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := JNI_ANDROID_TEST
LOCAL_SRC_FILES := jnitest.cpp
include $(BUILD_SHARED_LIBRARY)