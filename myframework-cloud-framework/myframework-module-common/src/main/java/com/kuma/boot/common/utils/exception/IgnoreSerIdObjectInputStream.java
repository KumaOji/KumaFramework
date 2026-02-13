/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.exception;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class IgnoreSerIdObjectInputStream
extends ObjectInputStream {
    public IgnoreSerIdObjectInputStream(byte[] bytes) throws IOException {
        this(new ByteArrayInputStream(bytes));
    }

    public IgnoreSerIdObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        Class<?> localClass;
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();
        try {
            localClass = Class.forName(resultClassDescriptor.getName());
        }
        catch (ClassNotFoundException e) {
            LogUtils.warn("No local class for " + resultClassDescriptor.getName(), new Object[0]);
            return resultClassDescriptor;
        }
        ObjectStreamClass localClassDescriptor = ObjectStreamClass.lookup(localClass);
        if (localClassDescriptor != null) {
            long localSerId = localClassDescriptor.getSerialVersionUID();
            long streamSerId = resultClassDescriptor.getSerialVersionUID();
            if (streamSerId != localSerId) {
                LogUtils.warn("Overriding serialized class {} version mismatch: local serialVersionUID = {} stream serialVersionUID = {}", localClass, localSerId, streamSerId);
                resultClassDescriptor = localClassDescriptor;
            }
        }
        return resultClassDescriptor;
    }
}

