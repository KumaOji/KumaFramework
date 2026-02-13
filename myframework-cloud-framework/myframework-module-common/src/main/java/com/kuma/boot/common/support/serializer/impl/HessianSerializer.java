/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.caucho.hessian.io.HessianInput
 *  com.caucho.hessian.io.HessianOutput
 *  org.apache.commons.io.IOUtils
 */
package com.kuma.boot.common.support.serializer.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.kuma.boot.common.support.serializer.Serializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

public class HessianSerializer
implements Serializer {
    @Override
    public String name() {
        return "hessian";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] serialize(Object o) throws IOException {
        if (o == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput((OutputStream)byteArrayOutputStream);
        try {
            hessianOutput.writeObject(o);
        }
        finally {
            IOUtils.closeQuietly((OutputStream)byteArrayOutputStream);
            try {
                hessianOutput.close();
            }
            catch (IOException iOException) {}
        }
        return byteArrayOutputStream.toByteArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object deserialize(byte[] bytes, ClassLoader classLoader) throws IOException {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        HessianInput hessianInput = new HessianInput((InputStream)byteArrayInputStream);
        try {
            Object object = hessianInput.readObject();
            return object;
        }
        finally {
            IOUtils.closeQuietly((InputStream)byteArrayInputStream);
            try {
                hessianInput.close();
            }
            catch (Exception exception) {}
        }
    }
}

