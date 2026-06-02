package com.kuma.boot.sign.annotation;

import com.kuma.boot.sign.enums.SignAlgorithmType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 签名验签标记
 *
 * <p>标注在 Controller 方法或类上，命中的请求将被 {@code ApiSignatureInterceptor} 拦截校验：
 * AppKey 合法性、时间戳时效（防重放）、nonce 唯一性（防重放）、签名一致性（防篡改）。
 *
 * <p>类级标注表示该类所有方法均需验签；方法级标注优先于类级。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ApiSignature {

    /**
     * 签名算法，默认 {@link SignAlgorithmType#DEFAULT} 表示跟随全局配置
     * {@code kuma.boot.sign.algorithm}。
     */
    SignAlgorithmType algorithm() default SignAlgorithmType.DEFAULT;

    /**
     * 时间戳有效期（秒），&lt;=0 表示跟随全局配置
     * {@code kuma.boot.sign.timestamp-expire-seconds}。
     */
    long timestampExpireSeconds() default -1L;
}
