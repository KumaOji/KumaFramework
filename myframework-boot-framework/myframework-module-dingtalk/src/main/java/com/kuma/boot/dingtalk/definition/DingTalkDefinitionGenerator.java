/*
 * Decompiled with CFR 0.152.
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

public class DingTalkDefinitionGenerator
extends DingerDefinitionHandler {

    public static class XmlLink
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return DingTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.DINGTALK_XML_LINK, context);
        }
    }

    public static class AnnotationLink
    extends DingerDefinitionGenerator<DingerLink> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerLink> context) {
            return DingTalkDefinitionGenerator.dingerLinkHandler(DingerType.DINGTALK, context);
        }
    }

    public static class XmlImageText
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return DingTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.DINGTALK_XML_IMAGETEXT, context);
        }
    }

    public static class AnnotationImageText
    extends DingerDefinitionGenerator<DingerImageText> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerImageText> context) {
            return DingTalkDefinitionGenerator.dingerImageTextHandler(DingerType.DINGTALK, context);
        }
    }

    public static class XmlMarkdown
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return DingTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.DINGTALK_XML_MARKDOWN, context);
        }
    }

    public static class XmlText
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return DingTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.DINGTALK_XML_TEXT, context);
        }
    }

    public static class AnnotationMarkdown
    extends DingerDefinitionGenerator<DingerMarkdown> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerMarkdown> context) {
            return DingTalkDefinitionGenerator.dingerMarkdownHandler(DingerType.DINGTALK, context);
        }
    }

    public static class AnotationText
    extends DingerDefinitionGenerator<DingerText> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerText> context) {
            return DingTalkDefinitionGenerator.dingerTextHandler(DingerType.DINGTALK, context);
        }
    }
}

