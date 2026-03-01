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

package com.kuma.boot.ip2region.ip2region.support;

import com.kuma.boot.common.utils.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

/**
 * <p>
 * RequestIpContextHolder
 * </p>
 *
 *
 */
public class RequestIpContextHolder {
    private static final ThreadLocal<String> context = new NamedThreadLocal<>("request ip context");

    private static final ThreadLocal<String> inheritableContext =
            new NamedInheritableThreadLocal<>("inheritable request ip context");

    /**
     * Set.
     *
     * @param ip the ip
     */
    public static void set(String ip) {
        set(ip, false);
    }

    /**
     * Set.
     *
     * @param ip          the ip
     * @param inheritable the inheritable
     */
    public static void set(String ip, boolean inheritable) {
        if (StringUtils.hasText(ip)) {
            if (inheritable) {
                inheritableContext.set(ip);
                context.remove();
            } else {
                context.set(ip);
                inheritableContext.remove();
            }
        } else {
            remove();
        }
    }

    /**
     * Set.
     *
     * @param request the request
     */
    public static void set(HttpServletRequest request) {
        String ip = RequestUtils.getHttpServletRequestIpAddress(request);
        set(ip);
    }

    /**
     * Compute if absent string.
     *
     * @param supplier the supplier
     * @return the string
     */
    public static synchronized String computeIfAbsent(Supplier<String> supplier) {
        String ip = get();
        if (StringUtils.hasText(ip)) {
            return ip;
        }
        String supplierIp = supplier.get();
        set(supplierIp);
        return supplierIp;
    }

    /**
     * Get string.
     *
     * @return the string
     */
    public static String get() {
        String ip = context.get();
        if (!StringUtils.hasText(ip)) {
            ip = inheritableContext.get();
        }
        return ip;
    }

    /**
     * Remove.
     */
    public static void remove() {
        context.remove();
        inheritableContext.remove();
    }
}
