package com.kuma.boot.core.chain.spring.registrar;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.common.chain.core.handler.BaseHandler;
import com.kuma.boot.common.chain.core.registry.ChainRegistry;
import com.kuma.boot.common.chain.spring.annotation.ChainHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 责任链处理者注册器，负责扫描并注册带有@ChainHandler注解的处理者
 */
@Component
public class ChainHandlerRegistrarBeanPostProcessor implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 在Spring容器刷新完成后，按order排序并注册所有处理器
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @EventListener(ContextRefreshedEvent.class)
    public void registerHandlersAfterContextRefresh() {
        // 获取链注册器
        Map<String, ChainRegistry> registryBeans = applicationContext.getBeansOfType(ChainRegistry.class);
        if (registryBeans.isEmpty()) {
            LogUtils.warn("No ChainRegistry found, handlers will not be registered");
            return;
        }

        ChainRegistry registry = registryBeans.values().iterator().next();

        // 获取所有带@ChainHandler注解的bean
        Map<String, Object> handlerBeans = applicationContext.getBeansWithAnnotation(ChainHandler.class);

        List<HandlerInfo> handlerInfos = new ArrayList<>();
        for (Object bean : handlerBeans.values()) {
            if (bean instanceof BaseHandler) {
                ChainHandler chainHandler = AnnotationUtils.findAnnotation(bean.getClass(), ChainHandler.class);
                if (chainHandler != null) {
                    handlerInfos.add(new HandlerInfo(chainHandler.value(), chainHandler.order(), (BaseHandler) bean));
                    LogUtils.debug("Collected handler {} for chain {} with order {}",
                            bean.getClass().getSimpleName(), chainHandler.value(), chainHandler.order());
                }
            }
        }

        if (handlerInfos.isEmpty()) {
            return;
        }

        // 按链ID分组，然后按order排序
        Map<String, List<HandlerInfo>> chainGroups = handlerInfos.stream()
                .collect(Collectors.groupingBy(HandlerInfo::getChainId));

        for (Map.Entry<String, List<HandlerInfo>> entry : chainGroups.entrySet()) {
            String chainId = entry.getKey();
            List<HandlerInfo> handlers = entry.getValue();

            // 按order排序（数值越小优先级越高）
            handlers.sort(Comparator.comparingInt(HandlerInfo::getOrder));

            // 按顺序注册处理器
            for (HandlerInfo handlerInfo : handlers) {
                registry.registerHandler(chainId, handlerInfo.getHandler());
                LogUtils.info("Registered handler {} for chain {} with order {}",
                        handlerInfo.getHandler().getClass().getSimpleName(),
                        chainId,
                        handlerInfo.getOrder());
            }
        }
    }

    /**
     * 处理器信息内部类
     */
    private static class HandlerInfo {
        private final String chainId;
        private final int order;
        private final BaseHandler handler;

        public HandlerInfo(String chainId, int order, BaseHandler handler) {
            this.chainId = chainId;
            this.order = order;
            this.handler = handler;
        }

        public String getChainId() {
            return chainId;
        }

        public int getOrder() {
            return order;
        }

        public BaseHandler getHandler() {
            return handler;
        }
    }
}
