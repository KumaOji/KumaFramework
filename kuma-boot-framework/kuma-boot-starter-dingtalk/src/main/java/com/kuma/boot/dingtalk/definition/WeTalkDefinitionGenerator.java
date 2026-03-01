/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.definition;

import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerMarkdown;
import com.kuma.boot.dingtalk.annatations.DingerText;
import com.kuma.boot.dingtalk.enums.DingerDefinitionType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.model.DingerDefinition;
import com.kuma.boot.dingtalk.model.DingerDefinitionGenerator;
import com.kuma.boot.dingtalk.model.DingerDefinitionGeneratorContext;
import com.kuma.boot.dingtalk.model.DingerDefinitionHandler;
import com.kuma.boot.dingtalk.xml.MessageTag;

public class WeTalkDefinitionGenerator
extends DingerDefinitionHandler {

    public static class XmlImageText
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return WeTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.WETALK_XML_IMAGETEXT, context);
        }
    }

    public static class AnnotationImageText
    extends DingerDefinitionGenerator<DingerImageText> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerImageText> context) {
            return WeTalkDefinitionGenerator.dingerImageTextHandler(DingerType.WETALK, context);
        }
    }

    public static class XmlMarkdown
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return WeTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.WETALK_XML_MARKDOWN, context);
        }
    }

    public static class XmlText
    extends DingerDefinitionGenerator<MessageTag> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<MessageTag> context) {
            return WeTalkDefinitionGenerator.xmlHandler(DingerDefinitionType.WETALK_XML_TEXT, context);
        }
    }

    public static class AnnotationMarkDown
    extends DingerDefinitionGenerator<DingerMarkdown> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerMarkdown> context) {
            return WeTalkDefinitionGenerator.dingerMarkdownHandler(DingerType.WETALK, context);
        }
    }

    public static class AnnotationText
    extends DingerDefinitionGenerator<DingerText> {
        @Override
        public DingerDefinition generator(DingerDefinitionGeneratorContext<DingerText> context) {
            return WeTalkDefinitionGenerator.dingerTextHandler(DingerType.WETALK, context);
        }
    }
}

