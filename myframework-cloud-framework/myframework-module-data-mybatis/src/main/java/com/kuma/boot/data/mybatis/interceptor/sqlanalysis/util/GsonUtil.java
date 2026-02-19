/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.data.mybatis.interceptor.sqlanalysis.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Author huhaitao21
 * @Description JSON转换工具类
 * @since 20:19 2023/10/10
 **/
public class GsonUtil {

    private static final Gson GSON = new GsonBuilder().create();

    /**
     * 对象、集合转json
     *
     * @param obj 对象
     * @return json
     */
    public static String bean2Json(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * json转对象
     *
     * @param jsonString json
     * @param objClass   对象类型
     * @param <T>        对象类型
     * @return 对象
     */
    public static <T> T json2Bean(String jsonString, Class<T> objClass) {
        return GSON.fromJson(jsonString, objClass);
    }
}
