/*
 * Copyright (c) 2020-2030, kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.spring.web.schema;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.kuma.boot.datasource.context.SchemaContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * Schema / 数据源切换 Filter
 * 从请求头读取 X-Schema、X-DataSource 并设置到上下文
 * - X-Schema: 同数据源下切换 schema（MySQL 库 / PostgreSQL schema）
 * - X-DataSource: 配合 dynamic-datasource 切换数据源
 *
 * @author kuma
 */
public class SchemaSwitchFilter implements Filter, Ordered {

    private static final String HEADER_SCHEMA = "X-Schema";
    private static final String HEADER_DATASOURCE = "X-DataSource";

    private final SchemaSwitchProperties properties;

    public SchemaSwitchFilter(SchemaSwitchProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        if (!properties.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String schema = httpRequest.getHeader(HEADER_SCHEMA);
        String dataSource = httpRequest.getHeader(HEADER_DATASOURCE);

        try {
            if (schema != null && !schema.isBlank()) {
                SchemaContext.setSchema(schema.trim());
            }
            if (dataSource != null && !dataSource.isBlank()) {
                DynamicDataSourceContextHolder.push(dataSource.trim());
            }
            chain.doFilter(request, response);
        } finally {
            SchemaContext.clearSchema();
            if (dataSource != null && !dataSource.isBlank()) {
                DynamicDataSourceContextHolder.poll();
            }
        }
    }

    @Override
    public int getOrder() {
        return properties.getOrder();
    }
}
