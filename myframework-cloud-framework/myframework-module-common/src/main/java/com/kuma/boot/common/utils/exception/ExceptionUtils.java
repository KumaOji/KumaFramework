/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  jakarta.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.kuma.boot.common.utils.exception;

import com.google.common.base.Preconditions;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.async.RunnableWithException;
import com.kuma.boot.common.utils.exception.FastStringPrintWriter;
import com.kuma.boot.common.utils.exception.SerializedThrowable;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.slf4j.Logger;

public class ExceptionUtils {
    public static final String STRINGIFIED_NULL_EXCEPTION = "(null)";

    /*
     * Enabled aggressive exception aggregation
     */
    public static String trace2String(Throwable t) {
        if (t == null) {
            return "";
        }
        try (StringWriter sw = new StringWriter();){
            PrintWriter pw = new PrintWriter((Writer)sw, true);
            try {
                t.printStackTrace(pw);
                String string = sw.getBuffer().toString();
                pw.close();
                return string;
            }
            catch (Throwable throwable) {
                try {
                    pw.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
        }
        catch (Exception exp) {
            throw new BaseException(exp);
        }
    }

    public static String trace2String(StackTraceElement[] stackTraceElements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElemen : stackTraceElements) {
            sb.append(stackTraceElemen.toString()).append("\n");
        }
        return sb.toString();
    }

    private static String lineSeparator() {
        return System.getProperty("line.separator");
    }

    public static String getFullMessage(Throwable e) {
        if (e == null) {
            return "";
        }
        return "\u3010\u8be6\u7ec6\u9519\u8bef\u3011" + ExceptionUtils.lineSeparator() + ExceptionUtils.getDetailMessage(e) + ExceptionUtils.lineSeparator() + "\u3010\u5806\u6808\u6253\u5370\u3011" + ExceptionUtils.lineSeparator() + ExceptionUtils.getFullStackTrace(e);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getFullStackTrace(Throwable e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        }
        finally {
            if (sw != null) {
                try {
                    sw.close();
                }
                catch (IOException iOException) {}
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    public static String getDetailMessage(Throwable ex) {
        if (ex == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while (ex != null) {
            sb.append("\u3010" + ex.getClass().getName() + "\u3011\u2192" + StringUtils.nullToEmpty((CharSequence)ex.getMessage()) + ExceptionUtils.lineSeparator());
            ex = ex.getCause();
        }
        return sb.toString();
    }

    public static void ignoreException(Runnable runnable, boolean isPrintInfo) {
        block2: {
            try {
                runnable.run();
            }
            catch (Exception e) {
                if (isPrintInfo) break block2;
                LogUtils.error(ExceptionUtils.getFullStackTrace(e), new Object[0]);
            }
        }
    }

    public static void ignoreException(Runnable runnable) {
        ExceptionUtils.ignoreException(runnable, false);
    }

    public static <T extends BaseException> Supplier<T> unchecked(Class<T> clazz, String msg) {
        try {
            BaseException t = (BaseException)clazz.getDeclaredConstructor(String.class).newInstance(msg);
            return () -> t;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Supplier<BusinessException> businessException(String msg) {
        return () -> new BusinessException(msg);
    }

    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof Error) {
            throw (Error)e;
        }
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        }
        if (e instanceof InvocationTargetException) {
            return (RuntimeException)ExceptionUtils.runtime(((InvocationTargetException)e).getTargetException());
        }
        if (e instanceof RuntimeException) {
            return (RuntimeException)e;
        }
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return (RuntimeException)ExceptionUtils.runtime(e);
    }

    private static <T extends Throwable> T runtime(Throwable throwable) throws T {
        throw throwable;
    }

    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException)unwrapped).getTargetException();
                continue;
            }
            if (!(unwrapped instanceof UndeclaredThrowableException)) break;
            unwrapped = ((UndeclaredThrowableException)unwrapped).getUndeclaredThrowable();
        }
        return unwrapped;
    }

    public static String getStackTraceAsString(Throwable ex) {
        FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
        ex.printStackTrace(printWriter);
        return printWriter.toString();
    }

    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException)unwrapped).getTargetException();
                continue;
            }
            if (!(unwrapped instanceof UndeclaredThrowableException)) break;
            unwrapped = ((UndeclaredThrowableException)unwrapped).getUndeclaredThrowable();
        }
        return unwrapped;
    }

    public static void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException();
    }

    public static String getErrorMessageWithNestedException(Throwable ex) {
        Throwable nestedException = ex.getCause();
        return ex.getMessage() + " nested exception is " + nestedException.getClass().getName() + ":" + nestedException.getMessage();
    }

    public static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        Throwable result = null;
        while ((cause = ex.getCause()) != null) {
            result = cause;
        }
        return result;
    }

    public static boolean isCausedBy(Exception ex, Class<? extends Exception> ... causeExceptionClasses) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (!causeClass.isInstance(cause)) continue;
                return true;
            }
        }
        return false;
    }

    public static Throwable getActualThrowable(Throwable throwable) {
        if (InvocationTargetException.class.equals(throwable.getClass())) {
            InvocationTargetException exception = (InvocationTargetException)throwable;
            return exception.getTargetException();
        }
        return throwable;
    }

    public static String stringifyException(Throwable e) {
        if (e == null) {
            return STRINGIFIED_NULL_EXCEPTION;
        }
        try {
            StringWriter stm = new StringWriter();
            PrintWriter wrt = new PrintWriter(stm);
            e.printStackTrace(wrt);
            wrt.close();
            return stm.toString();
        }
        catch (Throwable t) {
            return e.getClass().getName() + " (error while printing stack trace)";
        }
    }

    public static boolean isJvmFatalError(Throwable t) {
        return t instanceof InternalError || t instanceof UnknownError;
    }

    public static boolean isJvmFatalOrOutOfMemoryError(Throwable t) {
        return ExceptionUtils.isJvmFatalError(t) || t instanceof OutOfMemoryError;
    }

    public static void tryEnrichOutOfMemoryError(Throwable root, String jvmMetaspaceOomNewErrorMessage, String jvmDirectOomNewErrorMessage, String jvmHeapSpaceOomNewErrorMessage) {
        ExceptionUtils.updateDetailMessage(root, t -> {
            if (ExceptionUtils.isMetaspaceOutOfMemoryError(t)) {
                return jvmMetaspaceOomNewErrorMessage;
            }
            if (ExceptionUtils.isDirectOutOfMemoryError(t)) {
                return jvmDirectOomNewErrorMessage;
            }
            if (ExceptionUtils.isHeapSpaceOutOfMemoryError(t)) {
                return jvmHeapSpaceOomNewErrorMessage;
            }
            return null;
        });
    }

    public static void updateDetailMessage(Throwable root, Function<Throwable, String> throwableToMessage) {
        if (throwableToMessage == null) {
            return;
        }
        for (Throwable it = root; it != null; it = it.getCause()) {
            String newMessage = throwableToMessage.apply(it);
            if (newMessage == null) continue;
            ExceptionUtils.updateDetailMessageOfThrowable(it, newMessage);
        }
    }

    private static void updateDetailMessageOfThrowable(Throwable throwable, String newDetailMessage) {
        Field field;
        try {
            field = Throwable.class.getDeclaredField("detailMessage");
        }
        catch (NoSuchFieldException e) {
            throw new IllegalStateException("The JDK Throwable contains a detailMessage member. The Throwable class provided on the classpath does not which is why this exception appears.", e);
        }
        field.setAccessible(true);
        try {
            field.set(throwable, newDetailMessage);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("The JDK Throwable contains a private detailMessage member that should be accessible through reflection. This is not the case for the Throwable class provided on the classpath.", e);
        }
    }

    public static boolean isMetaspaceOutOfMemoryError(@Nullable Throwable t) {
        return ExceptionUtils.isOutOfMemoryErrorWithMessageContaining(t, "Metaspace");
    }

    public static boolean isDirectOutOfMemoryError(@Nullable Throwable t) {
        return ExceptionUtils.isOutOfMemoryErrorWithMessageContaining(t, "Direct buffer memory");
    }

    public static boolean isHeapSpaceOutOfMemoryError(@Nullable Throwable t) {
        return ExceptionUtils.isOutOfMemoryErrorWithMessageContaining(t, "Java heap space");
    }

    private static boolean isOutOfMemoryErrorWithMessageContaining(@Nullable Throwable t, String infix) {
        return ExceptionUtils.isOutOfMemoryError(t) && t.getMessage() != null && t.getMessage().toLowerCase(Locale.ROOT).contains(infix.toLowerCase(Locale.ROOT));
    }

    private static boolean isOutOfMemoryError(@Nullable Throwable t) {
        return t != null && t.getClass() == OutOfMemoryError.class;
    }

    public static void rethrowIfFatalError(Throwable t) {
        if (ExceptionUtils.isJvmFatalError(t)) {
            throw (Error)t;
        }
    }

    public static void rethrowIfFatalErrorOrOOM(Throwable t) {
        if (ExceptionUtils.isJvmFatalError(t) || t instanceof OutOfMemoryError) {
            throw (Error)t;
        }
    }

    public static <T extends Throwable> T firstOrSuppressed(T newException, @Nullable T previous) {
        Preconditions.checkNotNull(newException, (Object)"newException");
        if (previous == null || previous == newException) {
            return newException;
        }
        previous.addSuppressed(newException);
        return previous;
    }

    public static void rethrow(Throwable t) {
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new RuntimeException(t);
    }

    public static void rethrow(Throwable t, String parentMessage) {
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw new RuntimeException(parentMessage, t);
    }

    public static void rethrowException(Throwable t, String parentMessage) throws Exception {
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof Exception) {
            throw (Exception)t;
        }
        throw new Exception(parentMessage, t);
    }

    public static void rethrowException(Throwable t) throws Exception {
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof Exception) {
            throw (Exception)t;
        }
        throw new Exception(t.getMessage(), t);
    }

    public static void tryRethrowException(@Nullable Exception e) throws Exception {
        if (e != null) {
            throw e;
        }
    }

    public static void tryRethrowIOException(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
    }

    public static void rethrowIOException(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        throw new IOException(t.getMessage(), t);
    }

    public static <T extends Throwable> Optional<T> findSerializedThrowable(Throwable throwable, Class<T> searchType, ClassLoader classLoader) {
        if (throwable == null || searchType == null) {
            return Optional.empty();
        }
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (!searchType.isAssignableFrom(t.getClass())) continue;
            return Optional.of((Throwable)searchType.cast(t));
        }
        return Optional.empty();
    }

    public static <T extends Throwable> Optional<T> findThrowable(Throwable throwable, Class<T> searchType) {
        if (throwable == null || searchType == null) {
            return Optional.empty();
        }
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (!searchType.isAssignableFrom(t.getClass())) continue;
            return Optional.of((Throwable)searchType.cast(t));
        }
        return Optional.empty();
    }

    public static <T extends Throwable> void assertThrowable(Throwable throwable, Class<T> searchType) throws T {
        if (ExceptionUtils.findThrowable(throwable, searchType).isEmpty()) {
            throw throwable;
        }
    }

    public static <T extends Throwable> Optional<T> findThrowableSerializedAware(Throwable throwable, Class<T> searchType, ClassLoader classLoader) {
        if (throwable == null || searchType == null) {
            return Optional.empty();
        }
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (!searchType.isAssignableFrom(t.getClass())) continue;
            return Optional.of((Throwable)searchType.cast(t));
        }
        return Optional.empty();
    }

    public static Optional<Throwable> findThrowable(Throwable throwable, Predicate<Throwable> predicate) {
        if (throwable == null || predicate == null) {
            return Optional.empty();
        }
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (!predicate.test(t)) continue;
            return Optional.of(t);
        }
        return Optional.empty();
    }

    public static <T extends Throwable> void assertThrowable(T throwable, Predicate<Throwable> predicate) throws T {
        if (!ExceptionUtils.findThrowable(throwable, predicate).isPresent()) {
            throw throwable;
        }
    }

    public static Optional<Throwable> findThrowableWithMessage(Throwable throwable, String searchMessage) {
        if (throwable == null || searchMessage == null) {
            return Optional.empty();
        }
        for (Throwable t = throwable; t != null; t = t.getCause()) {
            if (t.getMessage() == null || !t.getMessage().contains(searchMessage)) continue;
            return Optional.of(t);
        }
        return Optional.empty();
    }

    public static <T extends Throwable> void assertThrowableWithMessage(Throwable throwable, String searchMessage) throws T {
        if (ExceptionUtils.findThrowableWithMessage(throwable, searchMessage).isEmpty()) {
            throw throwable;
        }
    }

    public static Throwable stripExecutionException(Throwable throwable) {
        return ExceptionUtils.stripException(throwable, ExecutionException.class);
    }

    public static Throwable stripCompletionException(Throwable throwable) {
        return ExceptionUtils.stripException(throwable, CompletionException.class);
    }

    public static Throwable stripException(Throwable throwableToStrip, Class<? extends Throwable> typeToStrip) {
        while (typeToStrip.isAssignableFrom(throwableToStrip.getClass()) && throwableToStrip.getCause() != null) {
            throwableToStrip = throwableToStrip.getCause();
        }
        return throwableToStrip;
    }

    public static void tryDeserializeAndThrow(Throwable throwable, ClassLoader classLoader) throws Throwable {
        Throwable current = throwable;
        while (!(current instanceof SerializedThrowable) && current.getCause() != null) {
            current = current.getCause();
        }
        if (current instanceof SerializedThrowable) {
            throw ((SerializedThrowable)current).deserializeError(classLoader);
        }
        throw throwable;
    }

    public static void checkInterrupted(Throwable e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    public static Throwable returnExceptionIfUnexpected(Throwable e) {
        return e instanceof RuntimeException ? null : e;
    }

    public static void logExceptionIfExcepted(Throwable e, Logger log) {
        if (e instanceof RuntimeException) {
            log.debug("Expected exception.", e);
        }
    }

    public static void suppressExceptions(RunnableWithException action) {
        block3: {
            try {
                action.run();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            catch (Throwable t) {
                if (!ExceptionUtils.isJvmFatalError(t)) break block3;
                ExceptionUtils.rethrow(t);
            }
        }
    }

    private ExceptionUtils() {
    }
}

