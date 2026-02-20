///*
// * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.kuma.cloud.project4.web.swagger;
//
//import com.kuma.boot.common.model.request.PageParam;
//import com.kuma.cloud.project4.web.pageable.PageableRequest;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Contact;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.info.License;
//import org.springdoc.core.utils.SpringDocUtils;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//
///**
// * SpringDoc OpenAPI 自动配置（@Lazy 延迟加载，优化启动速度）
// *
// * @author kuma
// */
//@Configuration
//@Lazy
//@ConditionalOnWebApplication
//@ConditionalOnClass(OpenAPI.class)
//@EnableConfigurationProperties(SpringDocProperties.class)
//public class SpringDocAutoConfiguration {
//
//    static {
//        // 将 PageParam 在 Swagger 中展开为扁平的 page、size、sort 参数
//        SpringDocUtils.getConfig().replaceParameterObjectWithClass(PageParam.class, PageableRequest.class);
//    }
//
//    @Bean
//    public OpenAPI openAPI(SpringDocProperties properties) {
//        Info info = new Info()
//                .title(properties.getTitle())
//                .description(properties.getDescription())
//                .version(properties.getVersion())
//                .contact(new Contact()
//                        .name(properties.getContactName())
//                        .url(properties.getContactUrl()))
//                .license(new License()
//                        .name(properties.getLicenseName())
//                        .url(properties.getLicenseUrl()));
//
//        return new OpenAPI().info(info);
//    }
//}
