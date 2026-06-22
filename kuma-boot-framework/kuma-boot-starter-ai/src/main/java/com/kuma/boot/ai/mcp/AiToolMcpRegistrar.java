package com.kuma.boot.ai.mcp;

import com.kuma.boot.ai.service.tool.AiToolProvider;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * 扫描容器内全部 {@link AiToolProvider} Bean,为其中每个 langchain4j {@link Tool} 方法注册一个
 * {@link LangchainToolMcpAdapter} Bean。这些适配器作为 {@code McpTool} 被 MCP 模块的工具注册表
 * 自动收集,从而让现有 AI 工具「零改动」通过 MCP 协议对外暴露。
 *
 * <p>以 {@link BeanDefinitionRegistryPostProcessor} 实现并置于最低优先级,确保在 {@code @Bean} /
 * 组件扫描完成（AiToolProvider 定义已就绪）之后、Bean 实例化之前完成注册。
 */
public class AiToolMcpRegistrar implements BeanDefinitionRegistryPostProcessor, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AiToolMcpRegistrar.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!(registry instanceof ConfigurableListableBeanFactory beanFactory)) {
            return;
        }
        int registered = 0;
        for (String beanName : registry.getBeanDefinitionNames()) {
            Class<?> type = safeGetType(beanFactory, beanName);
            if (type == null || !AiToolProvider.class.isAssignableFrom(type)) {
                continue;
            }
            int idx = 0;
            for (Method method : type.getMethods()) {
                if (!method.isAnnotationPresent(Tool.class)) {
                    continue;
                }
                String adapterBeanName = "mcpTool_" + beanName + "_" + method.getName() + "_" + idx++;
                if (registry.containsBeanDefinition(adapterBeanName)) {
                    continue;
                }
                AbstractBeanDefinition definition = BeanDefinitionBuilder
                        .genericBeanDefinition(LangchainToolMcpAdapter.class)
                        .addConstructorArgReference(beanName)
                        .addConstructorArgValue(method.getName())
                        .addConstructorArgValue(method.getParameterTypes())
                        .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                        .getBeanDefinition();
                registry.registerBeanDefinition(adapterBeanName, definition);
                registered++;
            }
        }
        if (registered > 0) {
            log.info("Bridged {} AI @Tool method(s) to MCP tools", registered);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // no-op
    }

    private static Class<?> safeGetType(ConfigurableListableBeanFactory beanFactory, String beanName) {
        try {
            return beanFactory.getType(beanName, false);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        // 须晚于 ConfigurationClassPostProcessor,保证 AiToolProvider 定义已全部注册
        return Ordered.LOWEST_PRECEDENCE;
    }
}
