/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.annatations.DingerMarkdown;
import com.kuma.boot.dingtalk.annatations.DingerText;
import com.kuma.boot.dingtalk.annatations.DingerTokenId;
import com.kuma.boot.dingtalk.entity.DingerRequest;
import com.kuma.boot.dingtalk.entity.MsgType;
import com.kuma.boot.dingtalk.enums.AsyncExecuteType;
import com.kuma.boot.dingtalk.enums.DingerDefinitionType;
import com.kuma.boot.dingtalk.enums.DingerType;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.xml.BodyTag;
import com.kuma.boot.dingtalk.xml.ConfigurationTag;
import com.kuma.boot.dingtalk.xml.ContentTag;
import com.kuma.boot.dingtalk.xml.MessageTag;
import com.kuma.boot.dingtalk.xml.PhoneTag;
import com.kuma.boot.dingtalk.xml.PhonesTag;
import com.kuma.boot.dingtalk.xml.TokenId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DingerDefinitionHandler {
    public static final String WETALK_AT_ALL = "@all";

    protected static DingerDefinition dingerTextHandler(DingerType dingerType, DingerDefinitionGeneratorContext<DingerText> context) {
        String keyName = context.getKeyName();
        DingerText dinger = context.getSource();
        DingerDefinition dingerDefinition = DingerDefinitionHandler.annotationDingerDefition(keyName, dinger.tokenId(), dinger.asyncExecute());
        dingerDefinition.setDingerType(dingerType);
        dingerDefinition.setMessageSubType(MessageSubType.TEXT);
        DingerRequest request = dinger.atAll() ? DingerRequest.request(dinger.value(), true) : DingerRequest.request(dinger.value(), Arrays.asList(dinger.phones()));
        MsgType msgType = dingerDefinition.messageSubType().msgType(dingerType, request);
        dingerDefinition.setMessage(msgType);
        return dingerDefinition;
    }

    protected static DingerDefinition dingerMarkdownHandler(DingerType dingerType, DingerDefinitionGeneratorContext<DingerMarkdown> context) {
        String keyName = context.getKeyName();
        DingerMarkdown dinger = context.getSource();
        DingerDefinition dingerDefinition = DingerDefinitionHandler.annotationDingerDefition(keyName, dinger.tokenId(), dinger.asyncExecute());
        dingerDefinition.setDingerType(dingerType);
        dingerDefinition.setMessageSubType(MessageSubType.MARKDOWN);
        DingerRequest request = DingerRequest.request(dinger.value(), dinger.title(), Arrays.asList(dinger.phones()));
        MsgType msgType = dingerDefinition.messageSubType().msgType(dingerType, request);
        dingerDefinition.setMessage(msgType);
        return dingerDefinition;
    }

    protected static DingerDefinition dingerImageTextHandler(DingerType dingerType, DingerDefinitionGeneratorContext<DingerImageText> context) {
        String keyName = context.getKeyName();
        DingerImageText dinger = context.getSource();
        DingerDefinition dingerDefinition = DingerDefinitionHandler.annotationDingerDefition(keyName, dinger.tokenId(), dinger.asyncExecute());
        dingerDefinition.setDingerType(dingerType);
        dingerDefinition.setMessageSubType(MessageSubType.IMAGETEXT);
        MsgType msgType = dingerDefinition.messageSubType().msgType(dingerType, null);
        dingerDefinition.setMessage(msgType);
        return dingerDefinition;
    }

    protected static DingerDefinition dingerLinkHandler(DingerType dingerType, DingerDefinitionGeneratorContext<DingerLink> context) {
        String keyName = context.getKeyName();
        DingerLink dinger = context.getSource();
        DingerDefinition dingerDefinition = DingerDefinitionHandler.annotationDingerDefition(keyName, dinger.tokenId(), dinger.asyncExecute());
        dingerDefinition.setDingerType(dingerType);
        dingerDefinition.setMessageSubType(MessageSubType.LINK);
        MsgType msgType = dingerDefinition.messageSubType().msgType(dingerType, null);
        dingerDefinition.setMessage(msgType);
        return dingerDefinition;
    }

    protected static DingerDefinition xmlHandler(DingerDefinitionType dingerDefinitionType, DingerDefinitionGeneratorContext<MessageTag> context) {
        MessageMainType messageMainType = dingerDefinitionType.messageMainType();
        if (messageMainType != MessageMainType.XML) {
            throw new DingerException(ExceptionEnum.DINGERDEFINITIONTYPE_ERROR, new Object[]{dingerDefinitionType, MessageMainType.XML, messageMainType});
        }
        String keyName = context.getKeyName();
        MessageTag messageTag = context.getSource();
        DefaultDingerDefinition dingerDefinition = new DefaultDingerDefinition();
        dingerDefinition.setDingerType(dingerDefinitionType.dingerType());
        dingerDefinition.setMessageMainType(messageMainType);
        dingerDefinition.setMessageSubType(dingerDefinitionType.messageSubType());
        dingerDefinition.setDingerName(keyName);
        DingerConfig dingerConfig = new DingerConfig();
        dingerDefinition.setDingerConfig(dingerConfig);
        Optional<ConfigurationTag> configuration = Optional.ofNullable(messageTag.getConfiguration());
        DingerDefinitionHandler.dingerConfig(dingerConfig, configuration);
        DingerRequest request = null;
        if (dingerDefinitionType.messageSubType().isSupport()) {
            Optional<BodyTag> body = Optional.ofNullable(messageTag.getBody());
            Optional<PhonesTag> phonesTag = body.map(e -> e.getPhones());
            Boolean atAll = phonesTag.map(e -> e.getAtAll()).orElse(false);
            List phoneTags = phonesTag.map(e -> e.getPhones()).orElse(null);
            List<Object> phones = phoneTags != null ? phoneTags.stream().map(PhoneTag::getValue).toList() : new ArrayList();
            Optional<ContentTag> contentTag = body.map(e -> e.getContent());
            String content = contentTag.map(e -> e.getContent()).orElse("");
            String title = contentTag.map(e -> e.getTitle()).orElse("Dinger Title");
            request = atAll != false ? DingerRequest.request(content, title, true) : DingerRequest.request(content, title, phones);
        }
        MsgType message = dingerDefinitionType.messageSubType().msgType(dingerDefinitionType.dingerType(), request);
        dingerDefinition.setMessage(message);
        return dingerDefinition;
    }

    private static DingerDefinition annotationDingerDefition(String dingerName, DingerTokenId dingerTokenId, AsyncExecuteType asyncExecuteType) {
        DefaultDingerDefinition dingerDefinition = new DefaultDingerDefinition();
        dingerDefinition.setMessageMainType(MessageMainType.ANNOTATION);
        dingerDefinition.setDingerName(dingerName);
        DingerConfig dingerConfig = DingerDefinitionHandler.dingerConfig(dingerTokenId);
        dingerConfig.setAsyncExecute(asyncExecuteType == AsyncExecuteType.NONE ? null : Boolean.valueOf(asyncExecuteType.type()));
        dingerDefinition.setDingerConfig(dingerConfig);
        return dingerDefinition;
    }

    private static void dingerConfig(DingerConfig dingerConfig, Optional<ConfigurationTag> configuration) {
        if (configuration.isPresent()) {
            Optional<TokenId> tokenId = configuration.map(e -> e.getTokenId());
            if (tokenId.isPresent()) {
                dingerConfig.setTokenId(tokenId.map(TokenId::getValue).orElse(null));
                dingerConfig.setDecryptKey(tokenId.map(TokenId::getDecryptKey).orElse(null));
                dingerConfig.setSecret(tokenId.map(TokenId::getSecret).orElse(null));
            }
            dingerConfig.setAsyncExecute(configuration.map(e -> e.getAsync() == null ? e.getAsyncExecute() : e.getAsync().booleanValue()).orElse(false));
            dingerConfig.check();
        }
    }

    private static DingerConfig dingerConfig(DingerTokenId dingerTokenId) {
        DingerConfig dingerConfig = new DingerConfig();
        dingerConfig.setTokenId(dingerTokenId.value());
        dingerConfig.setSecret(dingerTokenId.secret());
        dingerConfig.setDecryptKey(dingerTokenId.decryptKey());
        dingerConfig.check();
        return dingerConfig;
    }
}

