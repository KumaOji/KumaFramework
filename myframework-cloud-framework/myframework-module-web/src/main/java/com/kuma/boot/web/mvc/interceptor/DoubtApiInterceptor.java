/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.core.support.Collector
 *  jakarta.servlet.http.HttpServletRequest
 *  jakarta.servlet.http.HttpServletResponse
 *  org.springframework.web.method.HandlerMethod
 *  org.springframework.web.servlet.HandlerInterceptor
 */
package com.kuma.boot.web.mvc.interceptor;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.core.support.Collector;
import com.kuma.boot.web.autoconfigure.properties.WebMvcInterceptorProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class DoubtApiInterceptor
implements HandlerInterceptor {
    private final ThreadLocal<Long> beforeMem = new ThreadLocal();
    private final Map<String, DoubtApiInfo> statisticMap = new ConcurrentHashMap<String, DoubtApiInfo>();
    private final WebMvcInterceptorProperties properties;

    public DoubtApiInterceptor(WebMvcInterceptorProperties properties) {
        this.properties = properties;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.beforeMem.set(this.getJVMUsed());
        return true;
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long data = this.beforeMem.get();
        this.beforeMem.remove();
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod)handler;
            String methodPath = method.getBean().getClass().getName() + "." + method.getMethod().getName();
            String url = request.getRequestURI();
            long incrMem = this.getJVMUsed() - data;
            if (incrMem > (long)this.properties.getDoubtApiThreshold()) {
                if (this.statisticMap.containsKey(methodPath)) {
                    staticInfo = this.statisticMap.get(methodPath);
                    staticInfo.uri = url;
                    ++staticInfo.count;
                    staticInfo.totalIncreMem += incrMem;
                    if (staticInfo.totalIncreMem <= 0L) {
                        staticInfo.totalIncreMem = incrMem;
                        staticInfo.count = 1;
                    }
                } else {
                    staticInfo = new DoubtApiInfo();
                    staticInfo.method = methodPath;
                    staticInfo.uri = url;
                    staticInfo.count = 0;
                    staticInfo.totalIncreMem = 0L;
                    this.statisticMap.put(methodPath, staticInfo);
                }
                Collector collector = (Collector)ContextUtils.getBean(Collector.class, (boolean)true);
                if (Objects.nonNull(collector)) {
                    collector.value("kmc.monitor.doubtapi.info").set(this.statisticMap);
                }
            }
        }
    }

    private long getJVMUsed() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static class DoubtApiInfo
    implements Comparable<DoubtApiInfo> {
        private String uri;
        private String method;
        private long totalIncreMem;
        private int count;

        public DoubtApiInfo() {
        }

        public DoubtApiInfo(String uri, String method, long totalIncreMem, int count) {
            this.uri = uri;
            this.method = method;
            this.totalIncreMem = totalIncreMem;
            this.count = count;
        }

        @Override
        public int compareTo(DoubtApiInfo doubtApiInfo) {
            long cha;
            if (doubtApiInfo == null) {
                return -1;
            }
            long l = doubtApiInfo.count > 0 ? doubtApiInfo.totalIncreMem / (long)doubtApiInfo.count : (cha = doubtApiInfo.totalIncreMem - (long)this.count > 0L ? this.totalIncreMem / (long)this.count : this.totalIncreMem);
            if (cha > 0L) {
                return 1;
            }
            if (cha < 0L) {
                return -1;
            }
            return 0;
        }

        public String getUri() {
            return this.uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getMethod() {
            return this.method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public long getTotalIncreMem() {
            return this.totalIncreMem;
        }

        public void setTotalIncreMem(long totalIncreMem) {
            this.totalIncreMem = totalIncreMem;
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}

