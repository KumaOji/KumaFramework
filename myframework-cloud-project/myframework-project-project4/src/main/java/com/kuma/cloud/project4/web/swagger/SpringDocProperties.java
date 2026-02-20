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

package com.kuma.cloud.project4.web.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SpringDoc 配置属性
 *
 * @author kuma
 */
@Data
@ConfigurationProperties(prefix = "kuma.springdoc")
public class SpringDocProperties {

    /**
     * API 文档标题
     */
    private String title = "API 文档";

    /**
     * API 文档描述
     */
    private String description = "RESTful API 接口文档";

    /**
     * API 版本
     */
    private String version = "1.0.0";

    /**
     * 联系人名称
     */
    private String contactName = "";

    /**
     * 联系人 URL
     */
    private String contactUrl = "";

    /**
     * 许可证名称
     */
    private String licenseName = "Apache 2.0";

    /**
     * 许可证 URL
     */
    private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.html";
}
