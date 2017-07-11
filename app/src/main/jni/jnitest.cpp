//
// Created by wangbicong on 2017/7/4.
//
#include<stdio.h>
#include<stdlib.h>
#include<jni.h>
#include"jnitest.h"

JNIEXPORT jdouble JNICALL Java_com_wbc_calculator_JniTest_add
  (JNIEnv *env, jclass clazz, jdouble a,jdouble b)
  {
    return a+b;
  }

  JNIEXPORT jdouble JNICALL Java_com_wbc_calculator_JniTest_min
  (JNIEnv *env, jclass clazz, jdouble a,jdouble b)
  {
    return a-b;
  }

  JNIEXPORT jdouble JNICALL Java_com_wbc_calculator_JniTest_mul
  (JNIEnv *env, jclass clazz, jdouble a,jdouble b)
  {
    return a*b;
  }

  JNIEXPORT jdouble JNICALL Java_com_wbc_calculator_JniTest_div
  (JNIEnv *env, jclass clazz, jdouble a,jdouble b)
  {
    return a/b;
  }