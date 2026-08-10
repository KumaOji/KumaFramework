#include <jni.h>
#include <stdio.h>
#include <string.h>

JNIEXPORT jint JNICALL Java_com_kuma_cloud_lab_jni_NativeMath_add(
        JNIEnv *env, jclass clazz, jint left, jint right) {
    return left + right;
}

JNIEXPORT jint JNICALL Java_com_kuma_cloud_lab_jni_NativeMath_multiply(
        JNIEnv *env, jclass clazz, jint left, jint right) {
    return left * right;
}

JNIEXPORT jstring JNICALL Java_com_kuma_cloud_lab_jni_NativeMath_greet(
        JNIEnv *env, jclass clazz, jstring name) {
    const char *nameChars = (*env)->GetStringUTFChars(env, name, NULL);
    if (nameChars == NULL) {
        return NULL;
    }

    char buffer[256];
    snprintf(buffer, sizeof(buffer), "Hello from C, %s!", nameChars);
    (*env)->ReleaseStringUTFChars(env, name, nameChars);
    return (*env)->NewStringUTF(env, buffer);
}

JNIEXPORT jlong JNICALL Java_com_kuma_cloud_lab_jni_NativeMath_sumArray(
        JNIEnv *env, jclass clazz, jintArray values) {
    jsize length = (*env)->GetArrayLength(env, values);
    if (length <= 0) {
        return 0;
    }

    jint *elements = (*env)->GetIntArrayElements(env, values, NULL);
    if (elements == NULL) {
        return 0;
    }

    jlong sum = 0;
    for (jsize index = 0; index < length; index++) {
        sum += elements[index];
    }

    (*env)->ReleaseIntArrayElements(env, values, elements, JNI_ABORT);
    return sum;
}
