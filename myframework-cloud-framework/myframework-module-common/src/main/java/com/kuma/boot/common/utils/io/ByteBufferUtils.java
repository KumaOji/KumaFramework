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

package com.kuma.boot.common.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;

/**
 * ByteBuffer的工具类
 *
 */
public final class ByteBufferUtils {

    private ByteBufferUtils() {}

    /**
     * 用内存中的byte[]构造ByteBuffer
     * @param bytes 堆内存中的byte[]
     * @return 堆内存的ByteBuffer对象
     */
    public static ByteBuffer newByteBuffer(byte[] bytes) {
        int len = bytes.length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(len);
        byteBuffer.put(bytes);
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * 将ByteBuffer转换为堆内存中的byte[]，兼容堆外内存的情况，也因为有堆外内存，需要慎重使用，避免OOM
     * @param byteBuffer ByteBuffer对象
     * @return 堆内存的byte[]
     */
    public static byte[] toByteArray(ByteBuffer byteBuffer) {
        if (byteBuffer.isDirect()) {
            byteBuffer.rewind();
            try (ByteBufferInputStream input = new ByteBufferInputStream(byteBuffer);
                 ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                IOUtils.copy(input, output);
                return output.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return byteBuffer.array();
        }
    }
}
