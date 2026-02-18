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

package com.kuma.boot.common.support.serializer.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.kuma.boot.common.support.serializer.Serializer;
import com.kuma.boot.common.support.serializer.SerializerConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * HessianSerializer
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-17 10:30:45
 */
public class HessianSerializer implements Serializer {

    @Override
    public String name() {
        return SerializerConstants.HESSIAN;
    }

    @Override
    public byte[] serialize( Object o ) throws IOException {
        if (o == null) {
            return new byte[0];
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Hessian的序列化输出
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        try {
            hessianOutput.writeObject(o);
        } finally {
            IOUtils.closeQuietly(byteArrayOutputStream);
            try {
                hessianOutput.close();
            } catch (IOException ignored) {
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object deserialize( byte[] bytes, ClassLoader classLoader ) throws IOException {
        if (bytes == null) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // Hessian的反序列化读取对象
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        try {
            return hessianInput.readObject();
        } finally {
            IOUtils.closeQuietly(byteArrayInputStream);
            try {
                hessianInput.close();
            } catch (Exception e) {
            }
        }
    }
}
