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

package com.kuma.boot.data.mybatis.sharding.utils;

/**
 * 统一一个创建类名的方法
 *
 * @author winjeg
 */
public class NameUtils {

    /**
     * 统一的命名方式，非常重要，涉及到映射，路由等
     */
    public static String buildClassName(String dsName, String orignalName) {
        return orignalName + process(dsName);
    }

    private static String process(String name) {
        StringBuilder builder = new StringBuilder();
        for (char c : name.toCharArray()) {
            if ((c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'z')
                    || (c >= 'A' && c <= 'Z')
                    || c == '_'
                    || c == '$') {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
