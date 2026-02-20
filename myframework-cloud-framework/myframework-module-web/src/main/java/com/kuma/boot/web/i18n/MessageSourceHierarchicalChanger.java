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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * 用于修改 MessageSource 的层级关系，保证 DynamicMessageSource 在父级位置，减少开销
 */
public class MessageSourceHierarchicalChanger {

    @Resource(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
    private MessageSource messageSource;

    @Resource(name = com.kuma.boot.web.i18n.DynamicMessageSource.DYNAMIC_MESSAGE_SOURCE_BEAN_NAME)
    private com.kuma.boot.web.i18n.DynamicMessageSource dynamicMessageSource;

    /**
     * 将 dynamicMessageSource 置为 messageSource 的父级<br/>
     * 若 messageSource 非层级，则将 messageSource 置为 dynamicMessageSource 的父级
     */
    @PostConstruct
    public void changeMessageSourceParent() {
        // 优先走 messageSource，从资源文件中查找
        if (messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hierarchicalMessageSource =
                    (HierarchicalMessageSource) messageSource;
            MessageSource parentMessageSource = hierarchicalMessageSource.getParentMessageSource();
            dynamicMessageSource.setParentMessageSource(parentMessageSource);
            hierarchicalMessageSource.setParentMessageSource(dynamicMessageSource);
        } else {
            dynamicMessageSource.setParentMessageSource(messageSource);
        }
    }
}
