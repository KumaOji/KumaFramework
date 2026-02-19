/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  com.kuma.boot.common.utils.log.LogUtils
 *  io.swagger.v3.oas.models.Components
 *  io.swagger.v3.oas.models.ExternalDocumentation
 *  io.swagger.v3.oas.models.OpenAPI
 *  io.swagger.v3.oas.models.PathItem
 *  io.swagger.v3.oas.models.Paths
 *  io.swagger.v3.oas.models.headers.Header
 *  io.swagger.v3.oas.models.info.Contact
 *  io.swagger.v3.oas.models.info.Info
 *  io.swagger.v3.oas.models.info.License
 *  io.swagger.v3.oas.models.media.IntegerSchema
 *  io.swagger.v3.oas.models.media.Schema
 *  io.swagger.v3.oas.models.media.StringSchema
 *  io.swagger.v3.oas.models.security.OAuthFlow
 *  io.swagger.v3.oas.models.security.OAuthFlows
 *  io.swagger.v3.oas.models.security.Scopes
 *  io.swagger.v3.oas.models.security.SecurityRequirement
 *  io.swagger.v3.oas.models.security.SecurityScheme
 *  io.swagger.v3.oas.models.security.SecurityScheme$In
 *  io.swagger.v3.oas.models.security.SecurityScheme$Type
 *  io.swagger.v3.oas.models.servers.Server
 *  org.apache.commons.lang3.StringUtils
 *  org.springdoc.core.customizers.OpenApiCustomizer
 *  org.springdoc.core.models.GroupedOpenApi
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.boot.autoconfigure.AutoConfiguration
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.boot.context.properties.EnableConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.env.Environment
 */
package com.kuma.boot.springdoc.autoconfigure;

import cn.hutool.core.collection.CollUtil;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.springdoc.autoconfigure.properties.SpringdocProperties;
import com.kuma.boot.springdoc.knife4j.spring.annotations.EnableKnife4j;
import com.kuma.boot.springdoc.support.PackageScanUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
@EnableKnife4j
@EnableConfigurationProperties(value={SpringdocProperties.class})
@ConditionalOnProperty(prefix="kuma.boot.springdoc", name={"enabled"}, havingValue="true", matchIfMissing=true)
public class SpringdocAutoConfiguration
implements InitializingBean {
    @Value(value="${server.port:8080}")
    private int port;
    @Value(value="${spring.mvc.servlet.path:/}")
    private String servletPath;
    private final SpringdocProperties properties;
    @Autowired
    private Environment environment;

    public SpringdocAutoConfiguration(SpringdocProperties properties) {
        this.properties = properties;
    }

    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SpringdocAutoConfiguration.class, (String)"kuma-boot-starter-springdoc", (String[])new String[0]);
    }

    @Bean
    @ConditionalOnProperty(prefix="kuma.boot.springdoc", name={"type"}, havingValue="SERVICE", matchIfMissing=true)
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder().group(this.properties.getGroup()).pathsToMatch(this.properties.getPathsToMatch()).pathsToExclude(this.properties.getPathsToExclude()).packagesToScan(PackageScanUtils.resolvePackagesWithWildcard(this.properties.getPackagesToScan())).build();
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            Paths newPaths = new Paths();
            paths.keySet().forEach(e -> newPaths.put((Object)("/api/v" + this.properties.getVersion() + e), (Object)((PathItem)paths.get(e))));
            openApi.setPaths(newPaths);
            openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream()).forEach(operation -> {});
            if (CollUtil.isEmpty(this.properties.getTags())) {
                openApi.setTags(this.properties.getTags());
                openApi.setTags(openApi.getTags().stream().sorted(Comparator.comparing(tag -> StringUtils.stripAccents((String)tag.getName()))).collect(Collectors.toList()));
            }
        };
    }

    public String getServerIp() {
        String serverAddress = this.environment.getProperty("server.address");
        if (serverAddress == null || serverAddress.isEmpty()) {
            try {
                serverAddress = InetAddress.getLocalHost().getHostAddress();
            }
            catch (UnknownHostException e) {
                serverAddress = "127.0.0.1";
            }
        }
        return serverAddress;
    }

    @Bean
    public OpenAPI openApi() {
        Components components = new Components();
        Map<String, SecurityScheme> securitySchemes = this.properties.getSecuritySchemes();
        if (CollUtil.isEmpty(securitySchemes)) {
            components.addSecuritySchemes("token", new SecurityScheme().name("token").description("token").type(SecurityScheme.Type.HTTP).in(SecurityScheme.In.HEADER).scheme("bearer"));
        } else {
            securitySchemes.forEach((arg_0, arg_1) -> ((Components)components).addSecuritySchemes(arg_0, arg_1));
        }
        Map<String, Header> headers = this.properties.getHeaders();
        if (CollUtil.isEmpty(headers)) {
            components.addHeaders("kmc-request-version-header", new Header().description("\u7248\u672c\u53f7").schema((Schema)new StringSchema()));
            components.addHeaders("kmc-request-weight-header", new Header().description("\u6743\u91cd").schema((Schema)new IntegerSchema()));
        } else {
            headers.forEach((arg_0, arg_1) -> ((Components)components).addHeaders(arg_0, arg_1));
        }
        List<Server> servers = this.properties.getServers();
        if (CollUtil.isEmpty(servers)) {
            Server s0 = new Server();
            s0.setUrl("http://127.0.0.1:" + this.port + this.servletPath);
            s0.setDescription("\u672c\u5730\u56de\u73af\u5730\u5740");
            servers.add(s0);
            Server s1 = new Server();
            s1.setUrl("http://" + this.getServerIp() + ":" + this.port + this.servletPath);
            s1.setDescription("\u672c\u5730\u5730\u5740");
            servers.add(s1);
            Server s2 = new Server();
            s2.setUrl("http://dev.kumacloud.com/");
            s2.setDescription("\u6d4b\u8bd5\u73af\u5883\u5730\u5740");
            servers.add(s2);
            Server s3 = new Server();
            s3.setUrl("https://pre.kumacloud.com/");
            s3.setDescription("\u9884\u4e0a\u7ebf\u73af\u5883\u5730\u5740");
            servers.add(s3);
            Server s4 = new Server();
            s4.setUrl("https://pro.kumacloud.com/");
            s4.setDescription("\u751f\u4ea7\u73af\u5883\u5730\u5740");
            servers.add(s4);
        }
        Contact contact = new Contact().name("kuma").email("2569277704@qq.com").url("https://github.com/kuma/kuma-cloud-project");
        License license = new License().name("Apache 2.0").url("https://github.com/kuma/kuma-cloud-project/blob/master/LICENSE.txt");
        Info info = new Info().title(this.properties.getTitle()).description(this.properties.getDescription()).version(this.properties.getVersion()).contact(Objects.isNull(this.properties.getContact()) ? contact : this.properties.getContact()).termsOfService(this.properties.getTermsOfService()).license(Objects.isNull(this.properties.getLicense()) ? license : this.properties.getLicense());
        ExternalDocumentation externalDocumentation = new ExternalDocumentation().description(this.properties.getExternalDescription()).url(this.properties.getExternalUrl());
        SecurityScheme securityScheme = new SecurityScheme();
        OAuthFlows oAuthFlows = new OAuthFlows();
        OAuthFlow oAuthFlow = new OAuthFlow().authorizationUrl("http://kwqqr48rgo.cdhttp.cn/oauth2/authorize").tokenUrl("http://kwqqr48rgo.cdhttp.cn/oauth2/token").scopes(new Scopes().addString("openid", "OpenId\u767b\u5f55").addString("profile", "\u83b7\u53d6\u7528\u6237\u4fe1\u606f").addString("message.read", "\u8bfb").addString("message.write", "\u5199"));
        oAuthFlows.authorizationCode(oAuthFlow);
        securityScheme.flows(oAuthFlows).type(SecurityScheme.Type.OAUTH2);
        String securityName = "Authenticate";
        components.addSecuritySchemes(securityName, securityScheme);
        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList(securityName);
        return new OpenAPI().components(components).openapi(this.properties.getOpenapi()).info(info).servers(servers).externalDocs(externalDocumentation).addSecurityItem(securityRequirement);
    }
}

