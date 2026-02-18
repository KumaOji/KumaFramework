package com.kuma.boot.core.runtime.namespace;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

// SanYouNameSpaceHandler的作用就是将sanyou命名空间中的mybean这个标签读出来，拿到class的属性，
// 然后将这个class属性指定的class类型注入到Spring容器中，至于注册这个环节的代码，都交给了SanYouBeanDefinition
/**
 * KmcNameSpaceHandler
 *
 * @author kuma
 * @version 2026.01
 * @since 2025-12-19 09:30:45
 */
public class KmcNameSpaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        // 注册解析 ttbean 标签的解析器
        registerBeanDefinitionParser("kmcbean", new TaoTaoBeanDefinitionParser());
    }

    private static class TaoTaoBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

        @Override
        protected boolean shouldGenerateId() {
            return true;
        }

        @Override
        protected String getBeanClassName( Element element ) {
            return element.getAttribute("class");
        }

        @Override
        protected void doParse( Element element, BeanDefinitionBuilder builder ) {
            super.doParse(element, builder);
        }

        @Override
        protected void doParse( Element element, ParserContext parserContext,
                                BeanDefinitionBuilder builder ) {
            super.doParse(element, parserContext, builder);
        }
    }
}

