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

package com.kuma.boot.springdoc.autoconfigure;

import static com.kuma.boot.springdoc.support.PackageScanUtils.resolvePackagesWithWildcard;
import static org.apache.commons.lang3.StringUtils.stripAccents;

import com.kuma.boot.common.constant.StarterNameConstants;
import com.kuma.boot.common.model.request.PageParam;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.springdoc.autoconfigure.pageable.PageableRequest;
import com.kuma.boot.springdoc.autoconfigure.properties.SpringdocProperties;
import com.kuma.boot.springdoc.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.servers.Server;
import java.net.InetAddress;
import java.net.UnknownHostException;

import cn.hutool.core.collection.CollUtil;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;

/**
 * SwaggerAutoConfiguration
 *
 * @author kuma
 * @version 2022.03
 * @since 2020/4/30 10:10
 */
//第一种.集成OAuth2登录认证
//@OpenAPIDefinition(
//	info = @io.swagger.v3.oas.annotations.info.Info(
//		// 标题
//		title = "${custom.info.title:'example-api'}",
//		// 版本
//		version = "${custom.info.version:'0.0.1'}",
//		// 描述
//		description = "${custom.info.description:'这是一个使用SpringDoc生成的在线文档.'}",
//		// 首页
//		termsOfService = "${custom.info.termsOfService:'http://127.0.0.1:8080/example/test01'}",
//		// license
//		license = @io.swagger.v3.oas.annotations.info.License(
//			name = "${custom.license.name:'Apache 2.0'}",
//			// license 地址
//			url = "http://127.0.0.1:8080/example/test01"
//		)
//	),
//	// 这里的名字是引用下边 @SecurityScheme 注解中指定的名字，指定后发起请求时会在请求头中按照OAuth2的规范添加token
//	security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "${custom.security.name:Authenticate}")
//)
//@SecuritySchemes({@io.swagger.v3.oas.annotations.security.SecurityScheme(
//	// 指定 SecurityScheme 的名称(OpenAPIDefinition注解中的security属性中会引用该名称)
//	name = "${custom.security.name:Authenticate}",
//	// 指定认证类型为oauth2
//	type = SecuritySchemeType.OAUTH2,
//	// 设置认证流程
//	flows = @io.swagger.v3.oas.annotations.security.OAuthFlows(
//		// 设置授权码模式
//		authorizationCode = @io.swagger.v3.oas.annotations.security.OAuthFlow(
//			// 获取token地址
//			tokenUrl = "${custom.security.token-url:'http://kwqqr48rgo.cdhttp.cn/oauth2/token'}",
//			// 授权申请地址
//			authorizationUrl = "${custom.security.authorization-url:'http://kwqqr48rgo.cdhttp.cn/oauth2/authorize'}",
//			// oauth2的申请的scope(需要在OAuth2客户端中存在)
//			scopes = {
//				@OAuthScope(name = "openid", description = "OpenId登录"),
//				@OAuthScope(name = "profile", description = "获取用户信息"),
//				@OAuthScope(name = "message.read", description = "读"),
//				@OAuthScope(name = "message.write", description = "写")
//			}
//		)
//	)
//)})
@AutoConfiguration
@EnableKnife4j
@EnableConfigurationProperties({SpringdocProperties.class})
@ConditionalOnProperty(
        prefix = SpringdocProperties.PREFIX,
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SpringdocAutoConfiguration implements InitializingBean {

    //@Value("${spring.cloud.client.ip-address}")
    //private String ip;
    static {
        // 将 PageParam 在 Swagger 中展开为扁平的 page、size、sort 参数
        SpringDocUtils.getConfig().replaceParameterObjectWithClass(PageParam.class, PageableRequest.class);
    }

    @Value("${server.port:8080}")
    private int port;

    @Value("${spring.mvc.servlet.path:/}")
    private String servletPath;

    private final SpringdocProperties properties;

    public SpringdocAutoConfiguration(SpringdocProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogUtils.started(SpringdocAutoConfiguration.class, StarterNameConstants.SPRINGDOC_STARTER);
    }

    /**
     * 确保 OpenAPI 规范始终包含有效的 openapi 版本字段，避免 Swagger UI/Knife4j 报错：
     * "The provided definition does not specify a valid version field".
     * 使用最低优先级确保最后执行，覆盖可能被其他流程清空的情况。
     */
    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public GlobalOpenApiCustomizer openApiVersionCustomizer() {
        return openApi -> {
            String targetVersion = (properties.getOpenapi() != null && !properties.getOpenapi().isBlank())
                    ? properties.getOpenapi()
                    : "3.0.0";
            // 始终强制设置，确保 /v3/api-docs 等接口返回的 JSON 包含 openapi 字段
            openApi.setOpenapi(targetVersion);
        };
    }

    @Bean
    @ConditionalOnProperty(
            prefix = SpringdocProperties.PREFIX,
            name = "type",
            havingValue = "SERVICE",
            matchIfMissing = true)
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group(properties.getGroup())
                .pathsToMatch(properties.getPathsToMatch())
                .pathsToExclude(properties.getPathsToExclude())
                .packagesToScan(resolvePackagesWithWildcard(properties.getPackagesToScan()))
                // .packagesToExclude(properties.getPackagesToExclude())
                // .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            final Paths paths = openApi.getPaths();
            if (paths != null && !paths.isEmpty()) {
                Paths newPaths = new Paths();
                paths.keySet().forEach(e -> newPaths.put("/api/v" + properties.getVersion() + e, paths.get(e)));
                openApi.setPaths(newPaths);

                openApi.getPaths().values().stream()
                        .flatMap(pathItem -> pathItem.readOperations().stream())
                        .forEach(operation -> {
                        });
            } else if (paths == null) {
                openApi.setPaths(new Paths());
            }

            if (!CollUtil.isEmpty(properties.getTags())) {
                openApi.setTags(properties.getTags().stream()
                        .sorted(Comparator.comparing(tag -> stripAccents(tag.getName())))
                        .collect(Collectors.toList()));
            }
        };
    }
    @Autowired
    private Environment environment;

    public String getServerIp() {
        // 获取server.address配置，如果没有配置则返回本地IP
        String serverAddress = environment.getProperty("server.address");
        if (serverAddress == null || serverAddress.isEmpty()) {
            try {
                serverAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                serverAddress = "127.0.0.1";
            }
        }
        return serverAddress;
    }
    @Bean
    public OpenAPI openApi() {
        // 组件
        Components components = new Components();

        Map<String, SecurityScheme> securitySchemes = properties.getSecuritySchemes();
        if (CollUtil.isEmpty(securitySchemes)) {
            // 安全认证组件

            components.addSecuritySchemes(
                    "token",
                    new SecurityScheme()
                            .name("token")
                            .description("token")
                            .type(SecurityScheme.Type.HTTP)
                            .in(In.HEADER)
                            .scheme("bearer"));

            // components.addSecuritySchemes("bearer",
            //	new SecurityScheme()
            //		.name(HttpHeaders.AUTHORIZATION)
            //		.type(SecurityScheme.Type.HTTP)
            //		.in(In.HEADER)
            //		.scheme("bearer")
            //		.bearerFormat("JWT")
            // );
            // components.addSecuritySchemes("kuma_token",
            //	new SecurityScheme()
            //		.name("KUMA_CLOUD_AUTH")
            //		.type(Type.OAUTH2)
            //		.in(In.HEADER)
            //		.scheme("bearer")
            //		.bearerFormat("JWT")
            //		.flows(password)
            //		.flows(clientCredentials)
            // );

        } else {
            securitySchemes.forEach(components::addSecuritySchemes);
        }

        Map<String, Header> headers = properties.getHeaders();
        if (CollUtil.isEmpty(headers)) {
            // 添加全局header
            components.addHeaders(
                    "kmc-request-version-header",
                    new Header().description("版本号").schema(new StringSchema()));
            components.addHeaders(
                    "kmc-request-weight-header",
                    new Header().description("权重").schema(new IntegerSchema()));
        } else {
            headers.forEach(components::addHeaders);
        }

        List<Server> servers = properties.getServers();
        if (CollUtil.isEmpty(servers)) {
            Server s0 = new Server();
            s0.setUrl("http://" + "127.0.0.1" + ":" + port + "" + servletPath);
            s0.setDescription("本地回环地址");
            servers.add(s0);
            Server s1 = new Server();
            s1.setUrl("http://" + getServerIp() + ":" + port + "" + servletPath);
            s1.setDescription("本地地址");
            servers.add(s1);
            Server s2 = new Server();
            s2.setUrl("http://dev.kumacloud.com/");
            s2.setDescription("测试环境地址");
            servers.add(s2);
            Server s3 = new Server();
            s3.setUrl("https://pre.kumacloud.com/");
            s3.setDescription("预上线环境地址");
            servers.add(s3);
            Server s4 = new Server();
            s4.setUrl("https://pro.kumacloud.com/");
            s4.setDescription("生产环境地址");
            servers.add(s4);
        }

        Contact contact = new Contact()
                .name("kuma")
                .email("2569277704@qq.com")
                .url("https://github.com/kuma/kuma-cloud-project");
        License license = new License()
                .name("Apache 2.0")
                .url("https://github.com/kuma/kuma-cloud-project/blob/master/LICENSE.txt");

        //基础信息
        Info info = new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .version(properties.getVersion())
                .contact(Objects.isNull(properties.getContact()) ? contact : properties.getContact())
                .termsOfService(properties.getTermsOfService())
                .license(Objects.isNull(properties.getLicense()) ? license : properties.getLicense());

        ExternalDocumentation externalDocumentation = new ExternalDocumentation()
                .description(properties.getExternalDescription())
                .url(properties.getExternalUrl());

        //第二种.集成OAuth2登录认证
        // 安全认证组件
        SecurityScheme securityScheme = new SecurityScheme();
        // 创建一个oauth认证流程
        OAuthFlows oAuthFlows = new OAuthFlows();
        // 设置OAuth2流程中认证服务的基本信息
        OAuthFlow oAuthFlow = new OAuthFlow()
                // 授权申请地址
                .authorizationUrl("http://kwqqr48rgo.cdhttp.cn/oauth2/authorize")
                // 获取token地址
                .tokenUrl("http://kwqqr48rgo.cdhttp.cn/oauth2/token")
                .scopes(new Scopes()
                        .addString("openid", "OpenId登录")
                        .addString("profile", "获取用户信息")
                        .addString("message.read", "读")
                        .addString("message.write", "写")
                );
        // 使用授权码模式
        oAuthFlows.authorizationCode(oAuthFlow);
        // OAuth2流程
        securityScheme.flows(oAuthFlows)
                .type(SecurityScheme.Type.OAUTH2);
        // 安全认证名
        String securityName = "Authenticate";
        // 将认证配置加入组件中
        components.addSecuritySchemes(securityName, securityScheme);
        SecurityRequirement securityRequirement = new SecurityRequirement();
        // 将安全认证和swagger-ui关联起来
        securityRequirement.addList(securityName);

        return new OpenAPI()
                .components(components)
                .openapi(properties.getOpenapi())
                .info(info)
                .servers(servers)
                .externalDocs(externalDocumentation)
                // 添加请求时携带OAuth2规范的请求头(通过OAuth2流程获取token后发请求时会自动携带Authorization请求头)
                .addSecurityItem(securityRequirement);

    }
}
