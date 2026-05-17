package com.kuma.boot.common.chain.core.handler;

import com.kuma.boot.common.chain.core.context.HandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 责任链处理者基类
 * @param <P> Param类型，表示请求参数
 * @param <R> Response类型，表示响应结果
 */
public abstract class BaseHandler<P, R> {

    private static final Logger log = LoggerFactory.getLogger(BaseHandler.class);

    /**
     * 子类必须实现的业务逻辑方法。
     * 返回 true 继续执行下一个处理者，false 中断责任链。
     */
    public abstract boolean doHandle(HandlerContext<P, R> context);

    /** 判断是否跳过当前处理者，默认不跳过 */
    public boolean shouldSkip(HandlerContext<P, R> context) {
        return false;
    }

    /** 处理完成后的可选回调 */
    public void onCompleted(HandlerContext<P, R> context) {
        log.debug("Handler completed: {}", this.getClass().getSimpleName());
    }

    /** 处理出错时的可选回调 */
    public void onError(HandlerContext<P, R> context, Exception e) {
        log.error("Handler error in {}: {}", this.getClass().getSimpleName(), e.getMessage(), e);
    }
}
