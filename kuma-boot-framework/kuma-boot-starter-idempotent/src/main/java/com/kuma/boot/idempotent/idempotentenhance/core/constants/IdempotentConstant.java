/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.idempotent.idempotentenhance.core.constants;

public interface IdempotentConstant {
    public static final String PRIMARY = "primary";

    public static interface Executor {
        public static final String IDEMPOTENT_EXCEPTION_EVENT_EXECUTOR = "idempotentExceptionEventExecutor";
    }

    public static interface Symbol {
        public static final String UNDERLINE = "_";
        public static final String MIDLINE = "-";
    }

    public static interface Digit {
        public static final Integer ONE = 1;
        public static final Long ZERO_LONG = 0L;
        public static final Long ONE_LONG = 1L;
        public static final Long NEGATIVE_ONE_LONG = -1L;
    }
}

