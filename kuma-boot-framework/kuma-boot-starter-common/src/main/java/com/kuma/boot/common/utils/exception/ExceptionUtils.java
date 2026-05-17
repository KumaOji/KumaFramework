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

package com.kuma.boot.common.utils.exception;

import static com.google.common.base.Preconditions.checkNotNull;

import com.kuma.boot.common.enums.ResultEnum;
import com.kuma.boot.common.exception.BaseException;
import com.kuma.boot.common.exception.BusinessException;
import com.kuma.boot.common.utils.async.RunnableWithException;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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

/**
 * ExceptionUtilorg
 *
 * @author kuma
 * @version 2021.9
 * @since 2021-09-02 16:47:00org
 */
public class ExceptionUtils {

    /**
     * trace2String
     * @param t 异常信息
     * @return 异常信息
     * @since 2021-09-02 16:47:06
     */
    public static String trace2String(Throwable t) {
        if (t == null) {
            return "";
        }
        try {
            try (StringWriter sw = new StringWriter()) {
                try (PrintWriter pw = new PrintWriter(sw, true)) {
                    t.printStackTrace(pw);
                    return sw.getBuffer().toString();
                }
            }
        } catch (Exception exp) {
            throw new BaseException(exp);
        }
    }

    /**
     * trace2String
     * @param stackTraceElements 栈数据
     * @return 异常信息
     * @since 2021-09-02 16:47:16
     */
    public static String trace2String(StackTraceElement[] stackTraceElements) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement stackTraceElemen : stackTraceElements) {
            sb.append(stackTraceElemen.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * lineSeparator
     * @return 行分隔符
     * @since 2021-09-02 16:47:20
     */
    private static String lineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * getFullMessage
     * @param e 异常对象
     * @return 异常信息
     * @since 2021-09-02 16:47:23
     */
    public static String getFullMessage(Throwable e) {
        if (e == null) {
            return "";
        }
        return "【详细错误】"
                + lineSeparator()
                + getDetailMessage(e)
                + lineSeparator()
                + "【堆栈打印】"
                + lineSeparator()
                + getFullStackTrace(e);
    }

    /**
     * getFullStackTrace
     * @param e 异常对象
     * @return 异常信息
     * @since 2021-09-02 16:47:27
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
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    /**
     * getDetailMessage
     * @param ex 异常对象
     * @return 异常信息
     * @since 2021-09-02 16:47:33
     */
    public static String getDetailMessage(Throwable ex) {
        if (ex == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while (ex != null) {
            sb.append(
                    "【"
                            + ex.getClass().getName()
                            + "】→"
                            + StringUtils.nullToEmpty(ex.getMessage())
                            + lineSeparator());
            ex = ex.getCause();
        }
        return sb.toString();
    }

    /**
     * ignoreException
     * @param runnable runnable
     * @param isPrintInfo 是否打印信息
     * @since 2021-09-02 16:47:37
     */
    public static void ignoreException(Runnable runnable, boolean isPrintInfo) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (!isPrintInfo) {
                LogUtils.error(getFullStackTrace(e));
            }
        }
    }

    /**
     * ignoreException
     * @param runnable runnable
     * @since 2021-09-02 16:47:44
     */
    public static void ignoreException(Runnable runnable) {
        ignoreException(runnable, false);
    }

    public static <T extends BaseException> Supplier<T> unchecked(Class<T> clazz, String msg) {
        try {
            T t = clazz.getDeclaredConstructor(String.class).newInstance(msg);
            return () -> t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Supplier<BusinessException> businessException(String msg) {
        return () -> new BusinessException(msg);
    }

    //public static Supplier<BusinessException> notFound() {
    //    return () -> new BusinessException(NOT_FOUND);
    //}
    //
    //public static BusinessException notFoundException() {
    //    return new BusinessException(NOT_FOUND);
    //}

    /**
     * 将CheckedException转换为UncheckedException.
     * @param e 异常信息
     * @return 异常信息对象
     */
    public static RuntimeException unchecked(Throwable e) {
        if (e instanceof Error) {
            throw (Error) e;
        } else if (e instanceof IllegalAccessException
                || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException(e);
        } else if (e instanceof InvocationTargetException) {
            return runtime(((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        return runtime(e);
    }

    /**
     * 不采用 RuntimeException 包装，直接抛出，使异常更加精准
     * @param throwable 异常信息
     * @return 异常信息对象
     */
    @SuppressWarnings("unchecked")
    private static <T extends Throwable> T runtime(Throwable throwable) throws T {
        throw (T) throwable;
    }

    /**
     * 代理异常解包
     * @param wrapped 包装过得异常
     * @return 解包后的异常
     */
    public static Throwable unwrap(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    /**
     * 将ErrorStack转化为String.
     * @param ex 异常信息
     * @return 异常信息
     */
    public static String getStackTraceAsString(Throwable ex) {
        FastStringPrintWriter printWriter = new FastStringPrintWriter(512);
        ex.printStackTrace(printWriter);
        return printWriter.toString();
    }

    /**
     * 解包异常
     * @param wrapped 异常
     * @return 被解包的异常
     */
    public static Throwable unwrapThrowable(Throwable wrapped) {
        Throwable unwrapped = wrapped;
        while (true) {
            if (unwrapped instanceof InvocationTargetException) {
                unwrapped = ((InvocationTargetException) unwrapped).getTargetException();
            } else if (unwrapped instanceof UndeclaredThrowableException) {
                unwrapped = ((UndeclaredThrowableException) unwrapped).getUndeclaredThrowable();
            } else {
                return unwrapped;
            }
        }
    }

    /** 抛出运行时不支持的操作异常 */
    public static void throwUnsupportedOperationException() {
        throw new UnsupportedOperationException();
    }

    /// **
    // * 将CheckedException转换为UncheckedException.
    // * @param ex ex 异常
    // * @return 运行时异常
    // */
    // public static RuntimeException unchecked(Throwable ex) {
    // if (ex instanceof RuntimeException) {
    // return (RuntimeException) ex;
    // } else {
    // return new RuntimeException(ex);
    // }
    // }

    /// **
    // * 将ErrorStack转化为String.
    // * @param ex 异常
    // * @return 返回异常内容
    // */
    // public static String getStackTraceAsString(Throwable ex) {
    // StringWriter stringWriter = new StringWriter();
    // ex.printStackTrace(new PrintWriter(stringWriter));
    // return stringWriter.toString();
    // }

    /**
     * 获取组合本异常信息与底层异常信息的异常描述, 适用于本异常为统一包装异常类，底层异常才是根本原因的情况。
     * @param ex 异常
     * @return 异常信息
     */
    public static String getErrorMessageWithNestedException(Throwable ex) {
        Throwable nestedException = ex.getCause();
        return ex.getMessage()
                + " nested exception is "
                + nestedException.getClass().getName()
                + ":"
                + nestedException.getMessage();
    }

    /**
     * 获取异常的Root Cause.
     * @param ex 异常
     * @return 异常的 RootCause.
     */
    public static Throwable getRootCause(Throwable ex) {
        Throwable cause;
        Throwable result = null;
        while ((cause = ex.getCause()) != null) {
            result = cause;
        }
        return result;
    }

    /**
     * 判断异常是否由某些底层的异常引起.
     * @param ex 异常
     * @param causeExceptionClasses 导致的异常原因
     * @return 是否由某个异常引起
     */
    @SuppressWarnings("unchecked")
    public static boolean isCausedBy(
            Exception ex, Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex;
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 获取确切的异常信息 1. 主要针对代理报错
     * @param throwable 异常
     * @return 确切的异常信息
     */
    public static Throwable getActualThrowable(final Throwable throwable) {
        if (InvocationTargetException.class.equals(throwable.getClass())) {
            InvocationTargetException exception = (InvocationTargetException) throwable;
            return exception.getTargetException();
        }
        return throwable;
    }

    /** The stringified representation of a null exception reference. */
    public static final String STRINGIFIED_NULL_EXCEPTION = "(null)";

    /**
     * Makes a string representation of the exception's stack trace, or "(null)", if the
     * exception is null.
     *
     * <p>
     * This method makes a best effort and never fails.
     * @param e The exception to stringify.
     * @return A string with exception name and call stack.
     */
    public static String stringifyException(final Throwable e) {
        if (e == null) {
            return STRINGIFIED_NULL_EXCEPTION;
        }

        try {
            StringWriter stm = new StringWriter();
            PrintWriter wrt = new PrintWriter(stm);
            e.printStackTrace(wrt);
            wrt.close();
            return stm.toString();
        } catch (Throwable t) {
            return e.getClass().getName() + " (error while printing stack trace)";
        }
    }

    /**
     * Checks whether the given exception indicates a situation that may leave the JVM in
     * a corrupted state, meaning a state where continued normal operation can only be
     * guaranteed via clean process restart.
     *
     * <p>
     * Currently considered fatal exceptions are Virtual Machine errors indicating that
     * the JVM is corrupted, like {@link InternalError}, {@link UnknownError}, and
     * {} (a special case of InternalError). The exception is
     * also treated as a fatal error, because when a thread is forcefully stopped, there
     * is a high chance that parts of the system are in an inconsistent state.
     * @param t The exception to check.
     * @return True, if the exception is considered fatal to the JVM, false otherwise.
     */
    public static boolean isJvmFatalError(Throwable t) {
        return (t instanceof InternalError) || (t instanceof UnknownError);
    }

    /**
     * Checks whether the given exception indicates a situation that may leave the JVM in
     * a corrupted state, or an out-of-memory error.
     *
     * <p>
     * See {@link ExceptionUtils#isJvmFatalError(Throwable)} for a list of fatal JVM
     * errors. This method additionally classifies the {@link OutOfMemoryError} as fatal,
     * because it may occur in any thread (not the one that allocated the majority of the
     * memory) and thus is often not recoverable by destroying the particular thread that
     * threw the exception.
     * @param t The exception to check.
     * @return True, if the exception is fatal to the JVM or and OutOfMemoryError, false
     * otherwise.
     */
    public static boolean isJvmFatalOrOutOfMemoryError(Throwable t) {
        return isJvmFatalError(t) || t instanceof OutOfMemoryError;
    }

    /**
     * Tries to enrich OutOfMemoryErrors being part of the passed root Throwable's cause
     * tree.
     *
     * <p>
     * This method improves error messages for direct and metaspace
     * {@link OutOfMemoryError}. It adds description about the possible causes and ways of
     * resolution.
     * @param root The Throwable of which the cause tree shall be traversed.
     * @param jvmMetaspaceOomNewErrorMessage The message being used for JVM
     * metaspace-related OutOfMemoryErrors. Passing <code>null</code> will disable
     * handling this class of error.
     * @param jvmDirectOomNewErrorMessage The message being used for direct memory-related
     * OutOfMemoryErrors. Passing <code>null</code> will disable handling this class of
     * error.
     * @param jvmHeapSpaceOomNewErrorMessage The message being used for Heap space-related
     * OutOfMemoryErrors. Passing <code>null</code> will disable handling this class of
     * error.
     */
    public static void tryEnrichOutOfMemoryError(
            Throwable root,
            String jvmMetaspaceOomNewErrorMessage,
            String jvmDirectOomNewErrorMessage,
            String jvmHeapSpaceOomNewErrorMessage) {
        updateDetailMessage(
                root,
                t -> {
                    if (isMetaspaceOutOfMemoryError(t)) {
                        return jvmMetaspaceOomNewErrorMessage;
                    } else if (isDirectOutOfMemoryError(t)) {
                        return jvmDirectOomNewErrorMessage;
                    } else if (isHeapSpaceOutOfMemoryError(t)) {
                        return jvmHeapSpaceOomNewErrorMessage;
                    }

                    return null;
                });
    }

    /**
     * Updates error messages of Throwables appearing in the cause tree of the passed root
     * Throwable. The passed Function is applied on each Throwable of the cause tree.
     * Returning a String will cause the detailMessage of the corresponding Throwable to
     * be updated. Returning <code>null</code>, instead, won't trigger any detailMessage
     * update on that Throwable.
     * @param root The Throwable whose cause tree shall be traversed.
     * @param throwableToMessage The Function based on which the new messages are
     * generated. The function implementation should return the new message. Returning
     * <code>null</code>, in contrast, will result in not updating the message for the
     * corresponding Throwable.
     */
    public static void updateDetailMessage(
            Throwable root, Function<Throwable, String> throwableToMessage) {
        if (throwableToMessage == null) {
            return;
        }

        Throwable it = root;
        while (it != null) {
            String newMessage = throwableToMessage.apply(it);
            if (newMessage != null) {
                updateDetailMessageOfThrowable(it, newMessage);
            }

            it = it.getCause();
        }
    }

    private static void updateDetailMessageOfThrowable(
            Throwable throwable, String newDetailMessage) {
        Field field;
        try {
            field = Throwable.class.getDeclaredField("detailMessage");
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "The JDK Throwable contains a detailMessage member. The Throwable class"
                            + " provided on the classpath does not which is why this exception"
                            + " appears.",
                    e);
        }

        field.setAccessible(true);
        try {
            field.set(throwable, newDetailMessage);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    "The JDK Throwable contains a private detailMessage member that should be"
                            + " accessible through reflection. This is not the case for the Throwable"
                            + " class provided on the classpath.",
                    e);
        }
    }

    /**
     * Checks whether the given exception indicates a JVM metaspace out-of-memory error.
     * @param t The exception to check.
     * @return True, if the exception is the metaspace {@link OutOfMemoryError}, false
     * otherwise.
     */
    public static boolean isMetaspaceOutOfMemoryError(@Nullable Throwable t) {
        return isOutOfMemoryErrorWithMessageContaining(t, "Metaspace");
    }

    /**
     * Checks whether the given exception indicates a JVM direct out-of-memory error.
     * @param t The exception to check.
     * @return True, if the exception is the direct {@link OutOfMemoryError}, false
     * otherwise.
     */
    public static boolean isDirectOutOfMemoryError(@Nullable Throwable t) {
        return isOutOfMemoryErrorWithMessageContaining(t, "Direct buffer memory");
    }

    public static boolean isHeapSpaceOutOfMemoryError(@Nullable Throwable t) {
        return isOutOfMemoryErrorWithMessageContaining(t, "Java heap space");
    }

    private static boolean isOutOfMemoryErrorWithMessageContaining(
            @Nullable Throwable t, String infix) {
        // the exact matching of the class is checked to avoid matching any custom
        // subclasses of
        // OutOfMemoryError
        // as we are interested in the original exceptions, generated by JVM.
        return isOutOfMemoryError(t)
                && t.getMessage() != null
                && t.getMessage().toLowerCase(Locale.ROOT).contains(infix.toLowerCase(Locale.ROOT));
    }

    private static boolean isOutOfMemoryError(@Nullable Throwable t) {
        return t != null && t.getClass() == OutOfMemoryError.class;
    }

    /**
     * Rethrows the given {@code Throwable}, if it represents an error that is fatal to
     * the JVM. See {@link ExceptionUtils#isJvmFatalError(Throwable)} for a definition of
     * fatal errors.
     * @param t The Throwable to check and rethrow.
     */
    public static void rethrowIfFatalError(Throwable t) {
        if (isJvmFatalError(t)) {
            throw (Error) t;
        }
    }

    /**
     * Rethrows the given {@code Throwable}, if it represents an error that is fatal to
     * the JVM or an out-of-memory error. See
     * {@link ExceptionUtils#isJvmFatalError(Throwable)} for a definition of fatal errors.
     * @param t The Throwable to check and rethrow.
     */
    public static void rethrowIfFatalErrorOrOOM(Throwable t) {
        if (isJvmFatalError(t) || t instanceof OutOfMemoryError) {
            throw (Error) t;
        }
    }

    /**
     * Adds a new exception as a {@link Throwable#addSuppressed(Throwable) suppressed
     * exception} to a prior exception, or returns the new exception, if no prior
     * exception exists.
     *
     * <pre>{@code
     * public void closeAllThings() throws Exception {
     *     Exception ex = null;
     *     try {
     *         component.shutdown();
     *     } catch (Exception e) {
     *         ex = firstOrSuppressed(e, ex);
     *     }
     *     try {
     *         anotherComponent.stop();
     *     } catch (Exception e) {
     *         ex = firstOrSuppressed(e, ex);
     *     }
     *     try {
     *         lastComponent.shutdown();
     *     } catch (Exception e) {
     *         ex = firstOrSuppressed(e, ex);
     *     }
     *
     *     if (ex != null) {
     *         throw ex;
     *     }
     * }
     * }</pre>
     * @param newException The newly occurred exception
     * @param previous The previously occurred exception, possibly null.
     * @return The new exception, if no previous exception exists, or the previous
     * exception with the new exception in the list of suppressed exceptions.
     */
    public static <T extends Throwable> T firstOrSuppressed(T newException, @Nullable T previous) {
        checkNotNull(newException, "newException");

        if (previous == null || previous == newException) {
            return newException;
        } else {
            previous.addSuppressed(newException);
            return previous;
        }
    }

    /**
     * Throws the given {@code Throwable} in scenarios where the signatures do not allow
     * you to throw an arbitrary Throwable. Errors and RuntimeExceptions are thrown
     * directly, other exceptions are packed into runtime exceptions
     * @param t The throwable to be thrown.
     */
    public static void rethrow(Throwable t) {
        if (t instanceof Error) {
            throw (Error) t;
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new RuntimeException(t);
        }
    }

    /**
     * Throws the given {@code Throwable} in scenarios where the signatures do not allow
     * you to throw an arbitrary Throwable. Errors and RuntimeExceptions are thrown
     * directly, other exceptions are packed into a parent RuntimeException.
     * @param t The throwable to be thrown.
     * @param parentMessage The message for the parent RuntimeException, if one is needed.
     */
    public static void rethrow(Throwable t, String parentMessage) {
        if (t instanceof Error) {
            throw (Error) t;
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            throw new RuntimeException(parentMessage, t);
        }
    }

    /**
     * Throws the given {@code Throwable} in scenarios where the signatures do allow to
     * throw a Exception. Errors and Exceptions are thrown directly, other "exotic"
     * subclasses of Throwable are wrapped in an Exception.
     * @param t The throwable to be thrown.
     * @param parentMessage The message for the parent Exception, if one is needed.
     */
    public static void rethrowException(Throwable t, String parentMessage) throws Exception {
        if (t instanceof Error) {
            throw (Error) t;
        } else if (t instanceof Exception) {
            throw (Exception) t;
        } else {
            throw new Exception(parentMessage, t);
        }
    }

    /**
     * Throws the given {@code Throwable} in scenarios where the signatures do allow to
     * throw a Exception. Errors and Exceptions are thrown directly, other "exotic"
     * subclasses of Throwable are wrapped in an Exception.
     * @param t The throwable to be thrown.
     */
    public static void rethrowException(Throwable t) throws Exception {
        if (t instanceof Error) {
            throw (Error) t;
        } else if (t instanceof Exception) {
            throw (Exception) t;
        } else {
            throw new Exception(t.getMessage(), t);
        }
    }

    /**
     * Tries to throw the given exception if not null.
     * @param e exception to throw if not null.
     * @throws Exception
     */
    public static void tryRethrowException(@Nullable Exception e) throws Exception {
        if (e != null) {
            throw e;
        }
    }

    /**
     * Tries to throw the given {@code Throwable} in scenarios where the signatures allows
     * only IOExceptions (and RuntimeException and Error). Throws this exception directly,
     * if it is an IOException, a RuntimeException, or an Error. Otherwise does nothing.
     * @param t The Throwable to be thrown.
     */
    public static void tryRethrowIOException(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException) t;
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        }
    }

    /**
     * Re-throws the given {@code Throwable} in scenarios where the signatures allows only
     * IOExceptions (and RuntimeException and Error).
     *
     * <p>
     * Throws this exception directly, if it is an IOException, a RuntimeException, or an
     * Error. Otherwise it wraps it in an IOException and throws it.
     * @param t The Throwable to be thrown.
     */
    public static void rethrowIOException(Throwable t) throws IOException {
        if (t instanceof IOException) {
            throw (IOException) t;
        } else if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        } else {
            throw new IOException(t.getMessage(), t);
        }
    }

    /**
     * Checks whether a throwable chain contains a specific type of exception and returns
     * it. It deserializes any {@link SerializedThrowable} that are found using the
     * provided {@link ClassLoader}.
     * @param throwable the throwable chain to check.
     * @param searchType the type of exception to search for in the chain.
     * @param classLoader to use for deserialization.
     * @return Optional throwable of the requested type if available, otherwise empty
     */
    public static <T extends Throwable> Optional<T> findSerializedThrowable(
            Throwable throwable, Class<T> searchType, ClassLoader classLoader) {
        if (throwable == null || searchType == null) {
            return Optional.empty();
        }

        Throwable t = throwable;
        while (t != null) {
            if (searchType.isAssignableFrom(t.getClass())) {
                return Optional.of(searchType.cast(t));
            }
            // else if (t.getClass().isAssignableFrom(SerializedThrowable.class)) {
            // Throwable next = ((SerializedThrowable) t).deserializeError(classLoader);
            // // SerializedThrowable#deserializeError returns itself under some
            // conditions (e.g.,
            // // null cause).
            // // If that happens, exit to avoid looping infinitely. This is ok because if
            // the user
            // // was searching
            // // for a SerializedThrowable, we would have returned it in the initial if
            // condition.
            // t = (next == t) ? null : next;
            // }
            else {
                t = t.getCause();
            }
        }

        return Optional.empty();
    }

    /**
     * Checks whether a throwable chain contains a specific type of exception and returns
     * it.
     * @param throwable the throwable chain to check.
     * @param searchType the type of exception to search for in the chain.
     * @return Optional throwable of the requested type if available, otherwise empty
     */
    public static <T extends Throwable> Optional<T> findThrowable(
            Throwable throwable, Class<T> searchType) {
        if (throwable == null || searchType == null) {
            return Optional.empty();
        }

        Throwable t = throwable;
        while (t != null) {
            if (searchType.isAssignableFrom(t.getClass())) {
                return Optional.of(searchType.cast(t));
            } else {
                t = t.getCause();
            }
        }

        return Optional.empty();
    }

    /**
     * The same as {@link #findThrowable(Throwable, Class)}, but rethrows original
     * exception if the expected exception was not found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void assertThrowable(
            Throwable throwable, Class<T> searchType) throws T {
        if (findThrowable(throwable, searchType).isEmpty()) {
            throw (T) throwable;
        }
    }

    /**
     * Checks whether a throwable chain contains a specific type of exception and returns
     * it. This method handles {@link SerializedThrowable}s in the chain and deserializes
     * them with the given ClassLoader.
     *
     * <p>
     * SerializedThrowables are often used when exceptions might come from dynamically
     * loaded code and be transported over RPC / HTTP for better error reporting. The
     * receiving processes or threads might not have the dynamically loaded code
     * available.
     * @param throwable the throwable chain to check.
     * @param searchType the type of exception to search for in the chain.
     * @param classLoader the ClassLoader to use when encountering a SerializedThrowable.
     * @return Optional throwable of the requested type if available, otherwise empty
     */
    public static <T extends Throwable> Optional<T> findThrowableSerializedAware(
            Throwable throwable, Class<T> searchType, ClassLoader classLoader) {

        if (throwable == null || searchType == null) {
            return Optional.empty();
        }

        Throwable t = throwable;
        while (t != null) {
            if (searchType.isAssignableFrom(t.getClass())) {
                return Optional.of(searchType.cast(t));
            }
            // else if (t instanceof SerializedThrowable) {
            // t = ((SerializedThrowable) t).deserializeError(classLoader);
            // }
            else {
                t = t.getCause();
            }
        }

        return Optional.empty();
    }

    /**
     * Checks whether a throwable chain contains an exception matching a predicate and
     * returns it.
     * @param throwable the throwable chain to check.
     * @param predicate the predicate of the exception to search for in the chain.
     * @return Optional throwable of the requested type if available, otherwise empty
     */
    public static Optional<Throwable> findThrowable(
            Throwable throwable, Predicate<Throwable> predicate) {
        if (throwable == null || predicate == null) {
            return Optional.empty();
        }

        Throwable t = throwable;
        while (t != null) {
            if (predicate.test(t)) {
                return Optional.of(t);
            } else {
                t = t.getCause();
            }
        }

        return Optional.empty();
    }

    /**
     * The same as {@link #findThrowable(Throwable, Predicate)}, but rethrows original
     * exception if the expected exception was not found.
     */
    public static <T extends Throwable> void assertThrowable(
            T throwable, Predicate<Throwable> predicate) throws T {
        if (!findThrowable(throwable, predicate).isPresent()) {
            throw (T) throwable;
        }
    }

    /**
     * Checks whether a throwable chain contains a specific error message and returns the
     * corresponding throwable.
     * @param throwable the throwable chain to check.
     * @param searchMessage the error message to search for in the chain.
     * @return Optional throwable containing the search message if available, otherwise
     * empty
     */
    public static Optional<Throwable> findThrowableWithMessage(
            Throwable throwable, String searchMessage) {
        if (throwable == null || searchMessage == null) {
            return Optional.empty();
        }

        Throwable t = throwable;
        while (t != null) {
            if (t.getMessage() != null && t.getMessage().contains(searchMessage)) {
                return Optional.of(t);
            } else {
                t = t.getCause();
            }
        }

        return Optional.empty();
    }

    /**
     * The same as {@link #findThrowableWithMessage(Throwable, String)}, but rethrows
     * original exception if the expected exception was not found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void assertThrowableWithMessage(
            Throwable throwable, String searchMessage) throws T {
        if (findThrowableWithMessage(throwable, searchMessage).isEmpty()) {
            throw (T) throwable;
        }
    }

    /**
     * Unpacks an {@link ExecutionException} and returns its cause. Otherwise the given
     * Throwable is returned.
     * @param throwable to unpack if it is an ExecutionException
     * @return Cause of ExecutionException or given Throwable
     */
    public static Throwable stripExecutionException(Throwable throwable) {
        return stripException(throwable, ExecutionException.class);
    }

    /**
     * Unpacks an {@link CompletionException} and returns its cause. Otherwise the given
     * Throwable is returned.
     * @param throwable to unpack if it is an CompletionException
     * @return Cause of CompletionException or given Throwable
     */
    public static Throwable stripCompletionException(Throwable throwable) {
        return stripException(throwable, CompletionException.class);
    }

    /**
     * Unpacks an specified exception and returns its cause. Otherwise the given
     * {@link Throwable} is returned.
     * @param throwableToStrip to strip
     * @param typeToStrip type to strip
     * @return Unpacked cause or given Throwable if not packed
     */
    public static Throwable stripException(
            Throwable throwableToStrip, Class<? extends Throwable> typeToStrip) {
        while (typeToStrip.isAssignableFrom(throwableToStrip.getClass())
                && throwableToStrip.getCause() != null) {
            throwableToStrip = throwableToStrip.getCause();
        }

        return throwableToStrip;
    }

    /**
     * Tries to find a {@link SerializedThrowable} as the cause of the given throwable and
     * throws its deserialized value. If there is no such throwable, then the original
     * throwable is thrown.
     * @param throwable to check for a SerializedThrowable
     * @param classLoader to be used for the deserialization of the SerializedThrowable
     * @throws Throwable either the deserialized throwable or the given throwable
     */
    public static void tryDeserializeAndThrow(Throwable throwable, ClassLoader classLoader)
            throws Throwable {
        Throwable current = throwable;

        while (!(current instanceof SerializedThrowable) && current.getCause() != null) {
            current = current.getCause();
        }

        if (current instanceof SerializedThrowable) {
            throw ((SerializedThrowable) current).deserializeError(classLoader);
        } else {
            throw throwable;
        }
    }

    /**
     * Checks whether the given exception is a {@link InterruptedException} and sets the
     * interrupted flag accordingly.
     * @param e to check whether it is an {@link InterruptedException}
     */
    public static void checkInterrupted(Throwable e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Return the given exception if it is not a {@link }.
     * @param e the given exception
     * @return the given exception if it is not a {@link }
     */
    public static Throwable returnExceptionIfUnexpected(Throwable e) {
        return e instanceof RuntimeException ? null : e;
    }

    /**
     * Log the given exception in debug level if it is a {@link }.
     * @param e the given exception
     * @param log logger
     */
    public static void logExceptionIfExcepted(Throwable e, Logger log) {
        if (e instanceof RuntimeException) {
            log.debug("Expected exception.", e);
        }
    }

    // ------------------------------------------------------------------------
    // Lambda exception utilities
    // ------------------------------------------------------------------------

    public static void suppressExceptions(RunnableWithException action) {
        try {
            action.run();
        } catch (InterruptedException e) {
            // restore interrupted state
            Thread.currentThread().interrupt();
        } catch (Throwable t) {
            if (isJvmFatalError(t)) {
                rethrow(t);
            }
        }
    }

    // ------------------------------------------------------------------------

    /** Private constructor to prevent instantiation. */
    private ExceptionUtils() {}
}
