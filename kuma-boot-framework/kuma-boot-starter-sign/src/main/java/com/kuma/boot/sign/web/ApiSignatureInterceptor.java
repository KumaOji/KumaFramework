package com.kuma.boot.sign.web;

import com.kuma.boot.sign.annotation.ApiSignature;
import com.kuma.boot.sign.core.SignatureValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * API 签名拦截器
 *
 * <p>仅对标注 {@link ApiSignature}（方法级优先于类级）的请求执行验签，其余请求放行。
 * 校验失败抛出 {@code SignatureException}，由全局异常处理器统一响应。
 */
public class ApiSignatureInterceptor implements HandlerInterceptor {

    private final SignatureValidator validator;

    public ApiSignatureInterceptor(SignatureValidator validator) {
        this.validator = validator;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        ApiSignature annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), ApiSignature.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), ApiSignature.class);
        }
        if (annotation == null) {
            return true;
        }
        validator.validate(request, annotation);
        return true;
    }
}
