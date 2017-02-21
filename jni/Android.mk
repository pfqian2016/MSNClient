LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE :=avcodec-55-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libavcodec-56.so  
include $(PREBUILT_SHARED_LIBRARY)  
  
include $(CLEAR_VARS)  
LOCAL_MODULE :=avdevice-55-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libavdevice-56.so  
include $(PREBUILT_SHARED_LIBRARY)  
   
include $(CLEAR_VARS)  
LOCAL_MODULE :=avfilter-4-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libavfilter-5.so  
include $(PREBUILT_SHARED_LIBRARY)  
   
include $(CLEAR_VARS)  
LOCAL_MODULE :=avformat-55-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libavformat-56.so  
include $(PREBUILT_SHARED_LIBRARY)  
   
include $(CLEAR_VARS)  
LOCAL_MODULE :=  avutil-52-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libavutil-54.so  
include $(PREBUILT_SHARED_LIBRARY)  

include $(CLEAR_VARS)  
LOCAL_MODULE :=  postproc-53-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libpostproc-53.so  
include $(PREBUILT_SHARED_LIBRARY)  

include $(CLEAR_VARS)  
LOCAL_MODULE :=  avswresample-0-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libswresample-1.so  
include $(PREBUILT_SHARED_LIBRARY)  
   
include $(CLEAR_VARS)  
LOCAL_MODULE :=  swscale-2-prebuilt  
LOCAL_SRC_FILES :=prebuilt/libswscale-3.so  
include $(PREBUILT_SHARED_LIBRARY)  
   
include $(CLEAR_VARS)  
   
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := GetPicUsingJni
LOCAL_SRC_FILES := encode.c decode.c

LOCAL_SHARED_LIBRARIES:= avcodec-55-prebuilt avdevice-55-prebuilt avfilter-4-prebuilt avformat-55-prebuilt avutil-52-prebuilt avswresample-0-prebuilt swscale-2-prebuilt  postproc-53-prebuilt     

include $(BUILD_SHARED_LIBRARY)

