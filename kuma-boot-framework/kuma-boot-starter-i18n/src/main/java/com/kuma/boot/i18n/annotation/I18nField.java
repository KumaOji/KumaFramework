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

package com.kuma.boot.i18n.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * 标注在需要国际化的 String 类型字段上，须配合类上的 {@link I18nClass} 使用.
 *
 * <p>字段值将被替换为当前 Locale 对应的消息文本；若未找到对应消息，
 * 根据配置决定回退到 fallback 语言或保留原值。
 *
 * @author kuma
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface I18nField {

    /** {@link #code} 的别名 */
    @AliasFor("code")
    String value() default "";

    /**
     * 国际化消息 code（SpEL 表达式）.
     *
     * <ul>
     *   <li>默认为空 —— 使用被标注字段的值作为 code</li>
     *   <li>指定表达式 —— 解析后的结果作为 code，例如 {@code "statusCode"} 表示取同类中
     *       {@code statusCode} 字段的值</li>
     *   <li>可添加前缀：{@code "'prefix.' + statusCode"}</li>
     * </ul>
     */
    @AliasFor("value")
    String code() default "";

    /**
     * 是否需要国际化的条件（SpEL 表达式，返回 boolean）.
     *
     * <p>默认为空 —— 永远进行国际化处理。
     */
    String condition() default "";
}
