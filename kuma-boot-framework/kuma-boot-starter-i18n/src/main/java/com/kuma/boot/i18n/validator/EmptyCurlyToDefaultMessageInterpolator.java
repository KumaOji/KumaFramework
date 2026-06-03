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

package com.kuma.boot.i18n.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/**
 * 将 Bean Validation 消息中的空花括号 {@code {}} 替换为注解的 {@code message} 默认值.
 *
 * <p>例如：{@code @NotEmpty(message = "{user.name}：{}")} 中的 {@code {}} 会被替换为
 * {@code @NotEmpty} 的 message 默认值（{@code jakarta.validation.constraints.NotEmpty.message}），
 * 使 properties 消息文件可在占位符后追加标准验证提示。
 *
 * @author kuma
 */
public class EmptyCurlyToDefaultMessageInterpolator extends ResourceBundleMessageInterpolator {

    private static final String EMPTY_CURLY = "{}";

    public EmptyCurlyToDefaultMessageInterpolator() {}

    public EmptyCurlyToDefaultMessageInterpolator(ResourceBundleLocator userResourceBundleLocator) {
        super(userResourceBundleLocator);
    }

    @Override
    public String interpolate(String message, Context context, Locale locale) {
        if (message.contains(EMPTY_CURLY)) {
            Class<? extends Annotation> annotationType =
                    context.getConstraintDescriptor().getAnnotation().annotationType();
            try {
                Method messageMethod = annotationType.getDeclaredMethod("message");
                Object defaultValue = messageMethod.getDefaultValue();
                if (defaultValue instanceof String defaultMsg) {
                    message = message.replace(EMPTY_CURLY, defaultMsg);
                }
            } catch (NoSuchMethodException ignored) {
                // 注解无 message 方法，不替换
            }
        }
        return super.interpolate(message, context, locale);
    }
}
