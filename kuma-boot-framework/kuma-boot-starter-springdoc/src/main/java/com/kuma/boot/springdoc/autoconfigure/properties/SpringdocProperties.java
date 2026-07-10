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

package com.kuma.boot.springdoc.autoconfigure.properties;

import com.kuma.boot.common.constant.CommonConstants;
import com.kuma.boot.core.utils.common.PropertyUtils;
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
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * SpringdocProperties
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:11
 */
@RefreshScope
@ConfigurationProperties(SpringdocProperties.PREFIX)
public class SpringdocProperties {

    public static final String PREFIX = "kuma.boot.springdoc";

    /**
     * 是否开启springdoc
     */
    private Boolean enabled = false;
    private Type type = Type.SERVICE;
    /**
     * group default applicationName
     */
    private String group = Objects.requireNonNullElse(PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY, "application"), "application");

    /**
     * pathsToMatch default /**
     */
    private String[] pathsToMatch = new String[]{"/**"};

    /**
     * The Paths to exclude.
     */
    private String[] pathsToExclude = new String[]{"/actuator/**"};

    /**
     * The Packages to scan.
     */
    private String[] packagesToScan = new String[]{"com.kuma.cloud.*.biz.api.controller",
            "com.kuma.cloud.*.facade.controller.**"};

    /**
     * The Packages to exclude.
     */
    private String[] packagesToExclude;

    /**
     * version default kmcVersion
     */
    private String version = Objects.requireNonNullElse(PropertyUtils.getProperty("kmcVersion", "1.0.0"), "1.0.0");
    /**
     * SecuritySchemes
     */
    private Map<String, SecurityScheme> securitySchemes = new HashMap<>();
    /**
     * Headers
     */
    private Map<String, Header> headers = new HashMap<>();
    /**
     * Headers
     */
    private List<Server> servers = new ArrayList<>();
    /**
     * tags
     */
    private List<Tag> tags = new ArrayList<>();
    /**
     * title
     */
    private String title =
            Objects.requireNonNullElse(PropertyUtils.getProperty(CommonConstants.SPRING_APP_NAME_KEY, "application"), "application").toUpperCase() + " API";
    /**
     * description
     */
    private String description = "KUMA CLOUD FRAMEWORK";
    /**
     * contact
     */
    private Contact contact;
    /**
     * termsOfService
     */
    private String termsOfService = "http://kumacloud.com/terms/";
    /**
     * license
     */
    private License license;
    /**
     * externalDescription
     */
    private String externalDescription = "Kuma Cloud Wiki Documentation";
    /**
     * externalUrl
     */
    private String externalUrl = "https://github.com/kumaoji/KumaFramework/wiki";
    /**
     * openapi 版本，须为 3.0.0 以兼容 Swagger UI/Knife4j 的版本校验
     */
    private String openapi = "3.0.0";

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String[] getPathsToMatch() {
        return pathsToMatch;
    }

    public void setPathsToMatch(String[] pathsToMatch) {
        this.pathsToMatch = pathsToMatch;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, SecurityScheme> getSecuritySchemes() {
        return securitySchemes;
    }

    public void setSecuritySchemes(Map<String, SecurityScheme> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }

    public Map<String, Header> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Header> headers) {
        this.headers = headers;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getOpenapi() {
        return openapi;
    }

    public void setOpenapi(String openapi) {
        this.openapi = openapi;
    }

    public String[] getPathsToExclude() {

        return pathsToExclude;
    }

    public void setPathsToExclude(String[] pathsToExclude) {
        this.pathsToExclude = pathsToExclude;
    }

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public void setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public String[] getPackagesToExclude() {
        return packagesToExclude;
    }

    public void setPackagesToExclude(String[] packagesToExclude) {
        this.packagesToExclude = packagesToExclude;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Type getType() {
        return type;
    }

    public void setType( Type type ) {
        this.type = type;
    }

    public enum Type{
        GATEWAY,
        SERVICE,
    }
}
