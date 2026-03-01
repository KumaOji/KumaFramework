/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 */
package com.kuma.boot.dingtalk.enums;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.definition.DingTalkDefinitionGenerator;
import com.kuma.boot.dingtalk.definition.WeTalkDefinitionGenerator;
import com.kuma.boot.dingtalk.model.DingerDefinitionGenerator;
import java.lang.reflect.InvocationTargetException;

public enum DingerDefinitionType {
    DINGTALK_XML_TEXT(DingerType.DINGTALK, MessageMainType.XML, MessageSubType.TEXT, DingTalkDefinitionGenerator.XmlText.class),
    DINGTALK_XML_MARKDOWN(DingerType.DINGTALK, MessageMainType.XML, MessageSubType.MARKDOWN, DingTalkDefinitionGenerator.XmlMarkdown.class),
    DINGTALK_ANNOTATION_TEXT(DingerType.DINGTALK, MessageMainType.ANNOTATION, MessageSubType.TEXT, DingTalkDefinitionGenerator.AnotationText.class),
    DINGTALK_ANNOTATION_MARKDOWN(DingerType.DINGTALK, MessageMainType.ANNOTATION, MessageSubType.MARKDOWN, DingTalkDefinitionGenerator.AnnotationMarkdown.class),
    DINGTALK_XML_IMAGETEXT(DingerType.DINGTALK, MessageMainType.XML, MessageSubType.IMAGETEXT, DingTalkDefinitionGenerator.XmlImageText.class),
    DINGTALK_ANNOTATION_IMAGETEXT(DingerType.DINGTALK, MessageMainType.ANNOTATION, MessageSubType.IMAGETEXT, DingTalkDefinitionGenerator.AnnotationImageText.class),
    DINGTALK_XML_LINK(DingerType.DINGTALK, MessageMainType.XML, MessageSubType.LINK, DingTalkDefinitionGenerator.XmlLink.class),
    DINGTALK_ANNOTATION_LINK(DingerType.DINGTALK, MessageMainType.ANNOTATION, MessageSubType.LINK, DingTalkDefinitionGenerator.AnnotationLink.class),
    WETALK_XML_TEXT(DingerType.WETALK, MessageMainType.XML, MessageSubType.TEXT, WeTalkDefinitionGenerator.XmlText.class),
    WETALK_XML_MARKDOWN(DingerType.WETALK, MessageMainType.XML, MessageSubType.MARKDOWN, WeTalkDefinitionGenerator.XmlMarkdown.class),
    WETALK_ANNOTATION_TEXT(DingerType.WETALK, MessageMainType.ANNOTATION, MessageSubType.TEXT, WeTalkDefinitionGenerator.AnnotationText.class),
    WETALK_ANNOTATION_MARKDOWN(DingerType.WETALK, MessageMainType.ANNOTATION, MessageSubType.MARKDOWN, WeTalkDefinitionGenerator.AnnotationMarkDown.class),
    WETALK_XML_IMAGETEXT(DingerType.WETALK, MessageMainType.XML, MessageSubType.IMAGETEXT, WeTalkDefinitionGenerator.XmlImageText.class),
    WETALK_ANNOTATION_IMAGETEXT(DingerType.WETALK, MessageMainType.ANNOTATION, MessageSubType.IMAGETEXT, WeTalkDefinitionGenerator.AnnotationImageText.class);

    public static final DingerDefinitionType[] dingerDefinitionTypes;
    private DingerType dingerType;
    private MessageMainType messageMainType;
    private MessageSubType messageSubType;
    private Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator;

    private DingerDefinitionType(DingerType dingerType, MessageMainType messageMainType, MessageSubType messageSubType, Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator) {
        this.dingerType = dingerType;
        this.messageMainType = messageMainType;
        this.messageSubType = messageSubType;
        this.dingerDefinitionGenerator = dingerDefinitionGenerator;
    }

    public DingerType dingerType() {
        return this.dingerType;
    }

    public MessageMainType messageMainType() {
        return this.messageMainType;
    }

    public MessageSubType messageSubType() {
        return this.messageSubType;
    }

    public Class<? extends DingerDefinitionGenerator> dingerDefinitionGenerator() {
        return this.dingerDefinitionGenerator;
    }

    static {
        for (DingerDefinitionType dingTalkMessageType : dingerDefinitionTypes = DingerDefinitionType.values()) {
            try {
                dingTalkMessageType.dingerDefinitionGenerator().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                LogUtils.error((Throwable)e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

