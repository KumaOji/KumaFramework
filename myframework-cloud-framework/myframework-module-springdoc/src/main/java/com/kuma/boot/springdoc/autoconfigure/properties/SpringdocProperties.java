/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.constant.CommonConstants
 *  com.kuma.boot.common.utils.common.PropertyUtils
 *  io.swagger.v3.oas.models.headers.Header
 *  io.swagger.v3.oas.models.info.Contact
 *  io.swagger.v3.oas.models.info.License
 *  io.swagger.v3.oas.models.security.SecurityScheme
 *  io.swagger.v3.oas.models.servers.Server
 *  io.swagger.v3.oas.models.tags.Tag
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.cloud.context.config.annotation.RefreshScope
 */
package com.kuma.boot.springdoc.autoconfigure.properties;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.common.utils.common.PropertyUtils;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
@ConfigurationProperties(value="kuma.boot.springdoc")
public class SpringdocProperties {
    public static final String PREFIX = "kuma.boot.springdoc";
    private Boolean enabled = false;
    private Type type = Type.SERVICE;
    private String group = PropertyUtils.getProperty((String)CommonConstants.SPRING_APP_NAME_KEY);
    private String[] pathsToMatch = new String[]{"/**"};
    private String[] pathsToExclude = new String[]{"/actuator/**"};
    private String[] packagesToScan = new String[]{"com.kuma.cloud.*.biz.api.controller", "com.kuma.cloud.*.facade.controller.**"};
    private String[] packagesToExclude;
    private String version = PropertyUtils.getProperty((String)"kmcVersion");
    private Map<String, SecurityScheme> securitySchemes = new HashMap<String, SecurityScheme>();
    private Map<String, Header> headers = new HashMap<String, Header>();
    private List<Server> servers = new ArrayList<Server>();
    private List<Tag> tags = new ArrayList<Tag>();
    private String title = PropertyUtils.getProperty((String)CommonConstants.SPRING_APP_NAME_KEY).toUpperCase() + " API";
    private String description = "KUMA CLOUD \u7535\u5546\u53ca\u5927\u6570\u636e\u5e73\u53f0";
    private Contact contact;
    private String termsOfService = "http://kumacloud.com/terms/";
    private License license;
    private String externalDescription = "TaoTao Cloud Wiki Documentation";
    private String externalUrl = "https://github.com/kuma/kuma-cloud-project/wiki";
    private String openapi = "3.0.1";

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String[] getPathsToMatch() {
        return this.pathsToMatch;
    }

    public void setPathsToMatch(String[] pathsToMatch) {
        this.pathsToMatch = pathsToMatch;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return this.securitySchemes;
    }

    public void setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }

    public Map<String, Header> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, Header> headers) {
        this.headers = headers;
    }

    public List<Server> getServers() {
        return this.servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Contact getContact() {
        return this.contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getTermsOfService() {
        return this.termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public License getLicense() {
        return this.license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getExternalDescription() {
        return this.externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    public String getExternalUrl() {
        return this.externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getOpenapi() {
        return this.openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public String[] getPathsToExclude() {
        return this.pathsToExclude;
    }

    public void setPathsToExclude(String[] pathsToExclude) {
        this.pathsToExclude = pathsToExclude;
    }

    public String[] getPackagesToScan() {
        return this.packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public String[] getPackagesToExclude() {
        return this.packagesToExclude;
    }

    public void setPackagesToExclude(String[] packagesToExclude) {
        this.packagesToExclude = packagesToExclude;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        GATEWAY,
        SERVICE;

    }
}

