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

package com.kuma.boot.common.utils.system.info;

import java.io.Serializable;

/**
 * SystemInfo
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 19:26:50
 */
public class SystemInfo implements Serializable {

    private SystemInfo() {}

    public static final UserInfo USER_INFO = new UserInfo();

    public static final OsInfo OS_INFO = new OsInfo();

    public static final JavaInfo JAVA_INFO = new JavaInfo();

    public static final JvmInfo JVM_INFO = new JvmInfo();

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    /**
     * info
     * @return {@link String }
     * @since 2021-09-02 19:26:58
     */
    public static String info() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("[UserInfo]\n")
                .append(USER_INFO.toString())
                .append("\n")
                .append("\n[OsInfo]\n")
                .append(OS_INFO.toString())
                .append("\n")
                .append("\n[JavaInfo]\n")
                .append(JAVA_INFO.toString())
                .append("\n")
                .append("\n[JvmInfo]\n")
                .append(JVM_INFO.toString())
                .append("\n")
                .append("\n[RuntimeInfo]\n")
                .append(RUNTIME_INFO.toString())
                .append("\n");

        return stringBuilder.toString();
    }
}
