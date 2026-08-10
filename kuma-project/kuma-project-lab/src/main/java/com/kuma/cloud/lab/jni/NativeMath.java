package com.kuma.cloud.lab.jni;

import com.kuma.cloud.lab.jni.support.JniLibraryLoader;

/**
 * JNI 桥接类，对应 {@code src/main/c/lab_math.c} 中的 C 实现。
 */
public final class NativeMath {

    static {
        JniLibraryLoader.load();
    }

    private NativeMath() {
    }

    public static native int add(int left, int right);

    public static native int multiply(int left, int right);

    public static native String greet(String name);

    public static native long sumArray(int[] values);

}
