/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.security.spring.constants;

import com.google.common.collect.Lists;
import java.util.List;

public class WebResources {
    public static final List<String> DEFAULT_IGNORED_STATIC_RESOURCES = Lists.newArrayList((Object[])new String[]{"/error/**", "/plugins/**", "/ttc/**", "/static/**", "/webjars/**", "/assets/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs", "/v3/api-docs/**", "/openapi.json", "/swagger-ui.html", "/v3/**", "/favicon.ico", "/swagger-resources/**", "/webjars/**", "/actuator/**", "/index", "/index.html", "/doc.html", "/*.js", "/*.css", "/*.json", "/*.min.js", "/*.min.css", "/**.js", "/**.css", "/**.json", "/**.min.js", "/**.min.css", "/component/**", "/login/**", "/actuator/**", "/h2-console/**", "/pear.config.json", "/pear.config.yml", "/admin/css/**", "/admin/fonts/**", "/admin/js/**", "/admin/images/**", "/health/**", "/favicon.ico", "/swagger-ui.html", "/v3/**", "/favicon.ico", "/swagger-resources/**", "/webjars/**", "/actuator/**", "/index", "/index.html", "/doc.html", "/*.js", "/*.css", "/*.json", "/*.min.js", "/*.min.css", "/**.js", "/**.css", "/**.json", "/**.min.js", "/**.min.css", "/component/**", "/login/**", "/actuator/**", "/h2-console/**", "/pear.config.json", "/pear.config.yml", "/admin/css/**", "/admin/fonts/**", "/admin/js/**", "/admin/images/**", "/health/**"});
    public static final List<String> DEFAULT_PERMIT_ALL_RESOURCES = Lists.newArrayList((Object[])new String[]{"/open/**", "/stomp/ws", "/oauth2/sign-out", "/login*"});
    public static final List<String> DEFAULT_HAS_AUTHENTICATED_RESOURCES = Lists.newArrayList((Object[])new String[]{"/engine-rest/**"});
}

