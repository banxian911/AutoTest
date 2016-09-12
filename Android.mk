LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

#for test cust file(some project may not need all the test cases to be enabled)
LOCAL_MODULE_TAGS := optional
res_dirs := res

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, $(res_dirs))
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES += telephony-common
LOCAL_JAVA_LIBRARIES += framework
LOCAL_JNI_SHARED_LIBRARIES := libfmjni

#LOCAL_PREBUILT_LIBS +=autotest:libs/armeabi/libfmjni.so
#LOCAL_PREBUILT_LIBS +=autotest:libs/armeabi-v7a/libfmjni.so

#LOCAL_SRC_FILES := libfmjni.so
LOCAL_PACKAGE_NAME := autotest
LOCAL_CERTIFICATE := platform
LOCAL_JAVA_LIBRARIES += com.broadcom.bt
#DISABLE_DEXPREOPT:=true

include frameworks/opt/setupwizard/navigationbar/common.mk
include frameworks/opt/setupwizard/library/common.mk
include frameworks/base/packages/SettingsLib/common.mk
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_JNI_SHARED_LIBRARIES := libfmjni
MY_PATH := $(LOCAL_PATH)
#include $(MY_PATH)/jni-memory/Android.mk
#include $(MY_PATH)/jni-battery/Android.mk
#include $(MY_PATH)/dynamic_change_pmic/Android.mk
