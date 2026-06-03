package com.kuma.boot.idempotent.idempotentenhance.core.constants;

/**
 * 幂等相关常量
 *
 * @author wenpan 2022/12/31 15:40
 */
public interface IdempotentConstant {

    String PRIMARY = "primary";

    /**
     * 常用数字
     */
    interface Digit {
        Integer ONE = 1;
        Long ZERO_LONG = 0L;
        Long ONE_LONG = 1L;
        Long NEGATIVE_ONE_LONG = -1L;
    }

    /**
     * 常用符号
     */
    interface Symbol {
        String UNDERLINE = "_";
        String MIDLINE = "-";
    }

    interface Executor {
        /**
         * 幂等异常事件线程池
         */
        String IDEMPOTENT_EXCEPTION_EVENT_EXECUTOR = "idempotentExceptionEventExecutor";
    }
}
