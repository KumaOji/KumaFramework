/*
 *  com.kuma.boot.common.utils.context.ContextUtils
 *  com.kuma.boot.common.utils.lang.StringUtils
 *  org.springframework.util.Assert
 */
package com.kuma.boot.mq.common;

import com.kuma.boot.common.utils.context.ContextUtils;
import com.kuma.boot.common.utils.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

public class MessageQueueProviderFactory {
    private static final Map<String, String> beanSettings = new ConcurrentHashMap<String, String>();
    private final String defaultType;

    public MessageQueueProviderFactory(String defaultType) {
        this.defaultType = defaultType;
    }

    public static void addBean(String type, String beanName) {
        beanSettings.put(type, beanName);
    }

    public MessageQueueProvider getProvider() {
        MessageQueueProvider messageQueueProvider = StringUtils.isNotBlank((String)this.defaultType) ? (MessageQueueProvider)ContextUtils.getBean((String)beanSettings.get(this.defaultType.toUpperCase())) : (MessageQueueProvider)ContextUtils.getBean(MessageQueueProvider.class);
        Assert.notNull((Object)messageQueueProvider, (String)"MessageQueueProvider beanDefinition not found");
        return messageQueueProvider;
    }

    public MessageQueueProvider getProvider(String type) {
        String beanName = beanSettings.get(type.toUpperCase());
        MessageQueueProvider messageQueueProvider = (MessageQueueProvider)ContextUtils.getBean((String)beanName, MessageQueueProvider.class);
        Assert.notNull((Object)messageQueueProvider, (String)("MessageQueueProvider beanDefinition named '" + beanName + "' not found"));
        return messageQueueProvider;
    }
}

