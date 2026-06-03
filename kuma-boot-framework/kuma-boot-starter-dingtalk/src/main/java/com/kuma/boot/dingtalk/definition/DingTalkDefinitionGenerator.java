/*
 * Copyright (c) 2020-2030, Shuigedeng (981376577@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.dingtalk.definition;

import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.annatations.DingerMarkdown;
import com.kuma.boot.dingtalk.annatations.DingerText;
import com.kuma.boot.dingtalk.enums.DingerDefinitionType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.model.DingerDefinition;
import com.kuma.boot.dingtalk.model.DingerDefinitionGenerator;
import com.kuma.boot.dingtalk.model.DingerDefinitionGeneratorContext;
import com.kuma.boot.dingtalk.model.DingerDefinitionHandler;
import com.kuma.boot.dingtalk.xml.MessageTag;

/**
 * 钉钉消息体定义生成类
 *
 * @author kuma
 * @version 2022.07
 * @since 2022-07-06 15:18:54
 */
public class DingTalkDefinitionGenerator extends DingerDefinitionHandler {

    /**
     * 生成生成注解文本消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:54
     */
    public static class AnotationText extends DingerDefinitionGenerator<DingerText> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:54
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerText> context) {
            return dingerTextHandler(DingerType.DINGTALK, context);
        }
    }

    /**
     * 生成注解Markdown消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:54
     */
    public static class AnnotationMarkdown extends DingerDefinitionGenerator<DingerMarkdown> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:54
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerMarkdown> context) {
            return dingerMarkdownHandler(DingerType.DINGTALK, context);
        }
    }

    /**
     * 生成XML文本消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:54
     */
    public static class XmlText extends DingerDefinitionGenerator<MessageTag> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:54
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return xmlHandler(DingerDefinitionType.DINGTALK_XML_TEXT, context);
        }
    }

    /**
     * 生成XML Markdown消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:54
     */
    public static class XmlMarkdown extends DingerDefinitionGenerator<MessageTag> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:54
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return xmlHandler(DingerDefinitionType.DINGTALK_XML_MARKDOWN, context);
        }
    }

    /**
     * 生成注解 ImageText消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:54
     */
    public static class AnnotationImageText extends DingerDefinitionGenerator<DingerImageText> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:55
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerImageText> context) {
            return dingerImageTextHandler(DingerType.DINGTALK, context);
        }
    }

    /**
     * 生成XML ImageText消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:55
     */
    public static class XmlImageText extends DingerDefinitionGenerator<MessageTag> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:55
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return xmlHandler(DingerDefinitionType.DINGTALK_XML_IMAGETEXT, context);
        }
    }

    /**
     * 生成注解 Link消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:55
     */
    public static class AnnotationLink extends DingerDefinitionGenerator<DingerLink> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:55
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerLink> context) {
            return dingerLinkHandler(DingerType.DINGTALK, context);
        }
    }

    /**
     * 生成XML Link消息体定义
     *
     * @author kuma
     * @version 2022.07
     * @since 2022-07-06 15:18:55
     */
    public static class XmlLink extends DingerDefinitionGenerator<MessageTag> {

        /**
         * 发电机
         *
         * @param context 上下文
         * @return {@link DingerDefinition }
         * @since 2022-07-06 15:18:55
         */
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return xmlHandler(DingerDefinitionType.DINGTALK_XML_LINK, context);
        }
    }
}
