/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.common.utils.exception;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class SerializedThrowable
extends Exception
implements Serializable {
    private static final long serialVersionUID = 7284183123441947635L;
    private final byte[] serializedException;
    private final String originalErrorClassName;
    private final String fullStringifiedStackTrace;
    private transient WeakReference<Throwable> cachedException;

    public SerializedThrowable(Throwable exception) {
        this(exception, new HashSet<Throwable>());
    }

    private SerializedThrowable(Throwable exception, Set<Throwable> alreadySeen) {
        super(SerializedThrowable.getClassNameAndMessageOrError(exception));
        if (!(exception instanceof SerializedThrowable)) {
            Object serialized;
            try {
                serialized = null;
            }
            catch (Throwable t) {
                serialized = null;
            }
            this.serializedException = serialized;
            this.cachedException = new WeakReference<Throwable>(exception);
            this.originalErrorClassName = exception.getClass().getName();
            this.fullStringifiedStackTrace = ExceptionUtils.stringifyException(exception);
            this.setStackTrace(exception.getStackTrace());
            if (exception.getCause() == null) {
                this.initCause(null);
            } else if (alreadySeen.add(exception)) {
                this.initCause(new SerializedThrowable(exception.getCause(), alreadySeen));
            }
            this.addAllSuppressed(exception.getSuppressed());
        } else {
            SerializedThrowable other = (SerializedThrowable)exception;
            this.serializedException = other.serializedException;
            this.originalErrorClassName = other.originalErrorClassName;
            this.fullStringifiedStackTrace = other.fullStringifiedStackTrace;
            this.cachedException = other.cachedException;
            this.setStackTrace(other.getStackTrace());
            this.initCause(other.getCause());
            this.addAllSuppressed(other.getSuppressed());
        }
    }

    public Throwable deserializeError(ClassLoader classloader) {
        Throwable cached;
        if (this.serializedException == null) {
            return this;
        }
        Throwable throwable = cached = this.cachedException == null ? null : (Throwable)this.cachedException.get();
        if (cached == null) {
            try {
                this.cachedException = new WeakReference<Throwable>(cached);
            }
            catch (Throwable t) {
                return this;
            }
        }
        return cached;
    }

    public String getOriginalErrorClassName() {
        return this.originalErrorClassName;
    }

    public byte[] getSerializedException() {
        return this.serializedException;
    }

    public String getFullStringifiedStackTrace() {
        return this.fullStringifiedStackTrace;
    }

    private void addAllSuppressed(Throwable[] suppressed) {
        for (Throwable s : suppressed) {
            SerializedThrowable serializedThrowable = s instanceof SerializedThrowable ? (SerializedThrowable)s : new SerializedThrowable(s);
            this.addSuppressed(serializedThrowable);
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        s.print(this.fullStringifiedStackTrace);
        s.flush();
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        s.print(this.fullStringifiedStackTrace);
        s.flush();
    }

    @Override
    public String toString() {
        String message = this.getLocalizedMessage();
        return message != null ? this.originalErrorClassName + ": " + message : this.originalErrorClassName;
    }

    public static Throwable get(Throwable serThrowable, ClassLoader loader) {
        if (serThrowable instanceof SerializedThrowable) {
            return ((SerializedThrowable)serThrowable).deserializeError(loader);
        }
        return serThrowable;
    }

    private static String getClassNameAndMessageOrError(Throwable error) {
        try {
            String className = error.getClass().getName();
            String message = error.getMessage();
            if (message != null) {
                return String.format("%s: %s", className, message);
            }
            return className;
        }
        catch (Throwable t) {
            return "(failed to get message)";
        }
    }
}

