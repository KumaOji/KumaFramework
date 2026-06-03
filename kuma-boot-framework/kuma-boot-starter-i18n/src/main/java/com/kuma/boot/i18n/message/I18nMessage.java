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

package com.kuma.boot.i18n.message;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 国际化消息条目，对标 properties 文件中的一条 key=value.
 *
 * <p>业务系统可通过 {@link I18nMessageProvider} 从数据库 / Redis 返回此对象，
 * 实现动态、可热更新的多语言翻译。
 *
 * @author kuma
 */
@Schema(description = "国际化消息条目")
public class I18nMessage {

    @NotEmpty(message = "{i18nMessage.code}：{}")
    @Schema(description = "国际化唯一标识（消息 code）")
    private String code;

    @NotEmpty(message = "{i18nMessage.message}：{}")
    @Schema(description = "消息文本，支持 {0} {1} 位置占位符")
    private String message;

    @NotEmpty(message = "{i18nMessage.languageTag}：{}")
    @Schema(description = "BCP 47 语言标签，如 zh-CN / en-US")
    private String languageTag;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getLanguageTag() { return languageTag; }
    public void setLanguageTag(String languageTag) { this.languageTag = languageTag; }
}
