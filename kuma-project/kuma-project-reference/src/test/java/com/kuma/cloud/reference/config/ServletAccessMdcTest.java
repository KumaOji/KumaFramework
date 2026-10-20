package com.kuma.cloud.reference.config;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.web.autoconfigure.properties.WebMvcFilterProperties;
import com.kuma.boot.web.observability.ServletAccessLogFilter;
import com.kuma.boot.web.servlet.filter.TenantFilter;
import com.kuma.boot.web.servlet.filter.TraceFilter;
import com.kuma.boot.web.servlet.filter.VersionFilter;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ServletAccessMdcTest {
    @Test
    void asyncCompletionLogsRetainRequestMdcAfterInnerFiltersHaveCleanedUp() throws Exception {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ServletAccessLogFilter.class);
        var events = new java.util.concurrent.CopyOnWriteArrayList<ch.qos.logback.classic.spi.ILoggingEvent>();
        var appender = new ch.qos.logback.core.AppenderBase<ch.qos.logback.classic.spi.ILoggingEvent>() {
            @Override protected void append(ch.qos.logback.classic.spi.ILoggingEvent event) {
                event.prepareForDeferredProcessing();
                events.add(event);
            }
        };
        appender.start();
        logger.addAppender(appender);
        var previous = org.slf4j.MDC.getCopyOfContextMap();
        org.slf4j.MDC.put("sentinel", "original");
        try {
            var access = new ServletAccessLogFilter(new StaticListableBeanFactory().getBeanProvider(Tracer.class));
            var trace = new TraceFilter(new WebMvcFilterProperties(), null);
            var tenant = new TenantFilter();
            var version = new VersionFilter();
            var request = new MockHttpServletRequest("GET", "/demo");
            request.setAsyncSupported(true);
            request.addHeader(CommonConstants.KMC_TRACE_ID, "access-async-id");
            request.addHeader(CommonConstants.KMC_TENANT_ID, "demo-tenant");
            request.addHeader(CommonConstants.KMC_REQUEST_VERSION, "v2");
            var response = new MockHttpServletResponse();
            access.doFilter(request, response, (r, s) -> trace.doFilter(r, s,
                    (tr, ts) -> tenant.doFilter(tr, ts,
                            (te, tes) -> version.doFilter(te, tes, (v, vs) -> v.startAsync(v, vs)))));
            assertThat(events).isEmpty();
            assertThat(org.slf4j.MDC.get(CommonConstants.KMC_TRACE_ID)).isNull();
            request.getAsyncContext().complete();
            assertThat(events).hasSize(1);
            assertThat(events.getFirst().getMDCPropertyMap())
                    .containsEntry(CommonConstants.KMC_TRACE_ID, "access-async-id")
                    .containsEntry(CommonConstants.KMC_TENANT_ID, "demo-tenant")
                    .containsEntry(CommonConstants.KMC_REQUEST_VERSION, "v2")
                    .doesNotContainKey("sentinel");
            assertThat(org.slf4j.MDC.get("sentinel")).isEqualTo("original");
            assertThat(org.slf4j.MDC.get(CommonConstants.KMC_TRACE_ID)).isNull();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
            if (previous == null) org.slf4j.MDC.clear();
            else org.slf4j.MDC.setContextMap(previous);
        }
    }
}
