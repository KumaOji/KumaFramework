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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 调整 {@code MessageSource} 层级，将 {@link DynamicMessageSource} 置于父位置.
 *
 * <p>默认查找顺序：先查 properties 文件（准确匹配优先），再通过父链查 {@link DynamicMessageSource}；
 * 两者均未命中时按 {@code fallbackLanguageTag} 回退或使用 code 本身。
 *
 * @author kuma
 */
public class MessageSourceHierarchicalChanger {

    @Resource(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    private MessageSource messageSource;

    @Resource(name = DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME)
    private DynamicMessageSource dynamicMessageSource;

    @PostConstruct
    public void changeParent() {
        if (messageSource instanceof HierarchicalMessageSource hierarchical) {
            MessageSource parent = hierarchical.getParentMessageSource();
            dynamicMessageSource.setParentMessageSource(parent);
            hierarchical.setParentMessageSource(dynamicMessageSource);
        } else {
            dynamicMessageSource.setParentMessageSource(messageSource);
        }
    }
}
