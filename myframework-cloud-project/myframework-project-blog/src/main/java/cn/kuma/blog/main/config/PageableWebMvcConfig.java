package cn.kuma.blog.main.config;

import cn.kuma.blog.framework.config.PageableProperties;
import cn.kuma.blog.framework.web.pageable.DefaultPageParamArgumentResolver;
import cn.kuma.blog.framework.web.pageable.PageParamArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PageableProperties.class)
public class PageableWebMvcConfig {

    @Bean
    @ConditionalOnMissingBean
    public PageParamArgumentResolver pageParamArgumentResolver(PageableProperties pageableProperties) {
        return new DefaultPageParamArgumentResolver(
                pageableProperties.getMaxPageSize(),
                pageableProperties.getPageParameterName(),
                pageableProperties.getSizeParameterName(),
                pageableProperties.getSortParameterName());
    }

    @Configuration
    static class PageableWebMvcConfigurer implements WebMvcConfigurer {

        private final PageParamArgumentResolver pageParamArgumentResolver;

        PageableWebMvcConfigurer(PageParamArgumentResolver pageParamArgumentResolver) {
            this.pageParamArgumentResolver = pageParamArgumentResolver;
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(this.pageParamArgumentResolver);
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addRedirectViewController("/swagger", "/swagger-ui.html");
            registry.addRedirectViewController("/swagger/", "/swagger-ui.html");
            registry.addRedirectViewController("/doc", "/swagger-ui.html");
            registry.addRedirectViewController("/docs", "/swagger-ui.html");
            registry.addRedirectViewController("/api-doc", "/swagger-ui.html");
        }
    }

}
