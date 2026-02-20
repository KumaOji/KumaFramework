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

package com.kuma.boot.web.i18n;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 对标于 message bundle 的文件消息的抽象
 *
 */
@Schema(title = "国际化信息")
public class I18nMessage {

    /**
     * 国际化标识
     */
    @NotEmpty(message = "{i18nMessage.code}：{}")
    @Schema(title = "国际化标识")
    private String code;

    /**
     * 消息
     */
    @NotEmpty(message = "{i18nMessage.message}：{}")
    @Schema(title = "文本值，可以使用 { } 加角标，作为占位符")
    private String message;

    /**
     * 地区语言标签
     */
    @NotEmpty(message = "{i18nMessage.languageTag}：{}")
    @Schema(title = "语言标签")
    private String languageTag;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLanguageTag() {
        return languageTag;
    }

    public void setLanguageTag(String languageTag) {
        this.languageTag = languageTag;
    }
}
