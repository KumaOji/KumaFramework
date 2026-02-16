package cn.kuma.blog.main.config;


import cn.kuma.blog.common.model.domain.PageParam;
import cn.kuma.blog.common.model.domain.PageableConstants;
import cn.kuma.blog.framework.config.PageableProperties;
import cn.kuma.blog.framework.mybatisplus.pg.type.pageable.PageParamOpenAPIConverter;
import cn.kuma.blog.framework.mybatisplus.pg.type.pageable.PageableRequestClassCreator;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SpringDoc OpenAPI 配置
 * 使用 @Lazy 延迟初始化，优化应用启动速度
 */
@Configuration
@Lazy
@RequiredArgsConstructor
public class SpringDocConfig {

    private final PageableProperties pageableProperties;

    @Bean
    @Lazy
    public OpenAPI restfulOpenAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("ut"))
            .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("ut",
                    new SecurityScheme().type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("ut")
                        .description("Custom Token")))
            .info(new Info().title("博客系统 API 文档")
                .description("个人博客系统的 RESTful API 接口文档")
                .version("1.0.0")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .externalDocs(new ExternalDocumentation().description("项目地址").url("https://github.com/KumaOji/Myblog.git"));

    }

    /**
     * PageParam open api converter pageable open api converter.
     * @param objectMapperProvider the object mapper provider
     * @return the pageParam open api converter
     */
    @Bean
    @ConditionalOnMissingBean
    @Lazy
    PageParamOpenAPIConverter pageParamAPIConverter(ObjectMapperProvider objectMapperProvider) throws IOException {

        Map<String, String> map = new HashMap<>();

        String page = this.pageableProperties.getPageParameterName();
        if (!PageableConstants.DEFAULT_PAGE_PARAMETER.equals(page)) {
            map.put(PageableConstants.DEFAULT_PAGE_PARAMETER, page);
        }

        String size = this.pageableProperties.getSizeParameterName();
        if (!PageableConstants.DEFAULT_SIZE_PARAMETER.equals(size)) {
            map.put(PageableConstants.DEFAULT_SIZE_PARAMETER, size);
        }

        String sort = this.pageableProperties.getSortParameterName();
        if (!PageableConstants.DEFAULT_SORT_PARAMETER.equals(sort)) {
            map.put(PageableConstants.DEFAULT_SORT_PARAMETER, sort);
        }

        // 由于 PageParam 是由自定义的 PageParamArgumentResolver 处理的，所以需要在文档上进行入参的格式转换
        SpringDocUtils config = SpringDocUtils.getConfig();
        Class<?> pageParamRequestClass = PageableRequestClassCreator.create(map);
        config.replaceParameterObjectWithClass(PageParam.class, pageParamRequestClass);
        return new PageParamOpenAPIConverter(objectMapperProvider);
    }

}
