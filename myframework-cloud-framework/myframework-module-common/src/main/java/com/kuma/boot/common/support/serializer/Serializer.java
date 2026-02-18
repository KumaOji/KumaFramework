/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.common.support.serializer;

import com.kuma.boot.common.extension.SPI;
import java.io.IOException;

@SPI("Serializer")
public interface Serializer {

    /** 序列化标识名称 */
    String name();

    /**
     * 序列化成字节数组
     * @param data
     */
    byte[] serialize(Object data) throws IOException;

    /**
     * 反序列化成对象,需要知道是哪个类加载器
     * @param bytes
     * @param classLoader
     */
    Object deserialize(byte[] bytes, ClassLoader classLoader)
            throws IOException, ClassNotFoundException;
}
