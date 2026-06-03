package com.kuma.boot.session.utils;

import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Session 工具类
 *
 * <p>基于 {@link RequestContextHolder} 访问当前请求的 {@link HttpSession}，
 * 底层透明地委托给 Spring Session（Redis），调用者无需感知存储细节。
 *
 * <p><b>注意：</b>需在 Spring MVC 请求线程中调用；异步线程须通过
 * {@code DelegatingSecurityContextRunnable} 等工具传播上下文。
 */
public final class SessionUtils {

    private SessionUtils() {}

    /**
     * 获取当前 Session，不存在时不创建。
     *
     * @return 当前 Session，或 {@code null}（无请求上下文 / 无 Session）
     */
    @Nullable
    public static HttpSession getSession() {
        return getSession(false);
    }

    /**
     * 获取当前 Session。
     *
     * @param create 为 {@code true} 时，若不存在则创建新 Session
     * @return 当前 Session，或 {@code null}（无请求上下文）
     */
    @Nullable
    public static HttpSession getSession(boolean create) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return null;
        }
        return attrs.getRequest().getSession(create);
    }

    /**
     * 获取 Session 属性值。
     *
     * @param name 属性名
     * @param <T>  期望类型
     * @return 属性值，不存在或无 Session 时返回 {@code null}
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String name) {
        HttpSession session = getSession();
        return session != null ? (T) session.getAttribute(name) : null;
    }

    /**
     * 设置 Session 属性（Session 不存在时自动创建）。
     *
     * @param name  属性名
     * @param value 属性值
     */
    public static void setAttribute(String name, Object value) {
        HttpSession session = getSession(true);
        if (session != null) {
            session.setAttribute(name, value);
        }
    }

    /**
     * 移除 Session 属性（Session 不存在时静默忽略）。
     *
     * @param name 属性名
     */
    public static void removeAttribute(String name) {
        HttpSession session = getSession();
        if (session != null) {
            session.removeAttribute(name);
        }
    }

    /**
     * 使当前 Session 失效（登出场景）。Session 不存在时静默忽略。
     */
    public static void invalidate() {
        HttpSession session = getSession();
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * 获取当前 Session ID。
     *
     * @return Session ID，无 Session 时返回 {@code null}
     */
    @Nullable
    public static String getSessionId() {
        HttpSession session = getSession();
        return session != null ? session.getId() : null;
    }

    /**
     * 判断当前请求是否存在有效 Session（不触发新建）。
     */
    public static boolean isActive() {
        return getSession() != null;
    }
}
