/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.common.utils.common;

/**
 * Native 工具
 *
 * <p>
 * 参考： NativeDetector 和 NativeListener
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:41:13
 */
public final class NativeUtils {

    public static final String GENERATED_CLASS = "org.springframework.aot.StaticSpringFactories";

    public static final boolean GENERATED_CLASS_PRESENT = isClassPresent(GENERATED_CLASS);

    private static boolean isClassPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Holds the string that is the name of the system property providing information
     * about the context in which code is currently executing.
     */
    public static final String PROPERTY_IMAGE_CODE_KEY = "org.graalvm.nativeimage.imagecode";

    /**
     * See <a href=
     * "https://github.com/oracle/graal/blob/master/sdk/src/org.graalvm.nativeimage/src/org/graalvm/nativeimage/ImageInfo.java">...</a>
     */
    private static final boolean IS_IMAGE_CODE =
            (System.getProperty(PROPERTY_IMAGE_CODE_KEY) != null);

    /**
     * Returns {@code true} if invoked in the context of image building or during image
     * runtime, else {@code false}.
     */
    public static boolean inNativeImage() {
        return IS_IMAGE_CODE || GENERATED_CLASS_PRESENT;
    }
}
