package com.wbc.calculator;

/**
 * Created by wangbicong on 2017/7/4.
 */

public class JniTest {

    public static native double add();

    public static native double min();

    public static native double mul();

    public static native double div();

    static {
        System.loadLibrary("JNI_ANDROID_TEST");
    }

}
