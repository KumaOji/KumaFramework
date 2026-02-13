/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.annotation.Nullable
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.TextNode
 *  org.jsoup.select.Elements
 *  org.jsoup.select.Selector
 *  org.springframework.beans.BeanUtils
 *  org.springframework.cglib.proxy.MethodInterceptor
 *  org.springframework.cglib.proxy.MethodProxy
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.util.ReflectionUtils
 */
package com.kuma.boot.common.utils.spider;

import com.kuma.boot.common.utils.convert.ConvertUtils;
import com.kuma.boot.common.utils.lang.StringUtils;
import com.kuma.boot.common.utils.spider.CssQuery;
import com.kuma.boot.common.utils.spider.DomMapper;
import jakarta.annotation.Nullable;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ReflectionUtils;

public class CssQueryMethodInterceptor
implements MethodInterceptor {
    private final Class<?> clazz;
    private final Element element;

    public CssQueryMethodInterceptor(Class<?> clazz, Element element) {
        this.clazz = clazz;
        this.element = element;
    }

    @Nullable
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (ReflectionUtils.isObjectMethod((Method)method)) {
            return methodProxy.invokeSuper(object, args);
        }
        PropertyDescriptor propertyDescriptor = BeanUtils.findPropertyForMethod((Method)method, this.clazz);
        if (propertyDescriptor == null) {
            return methodProxy.invokeSuper(object, args);
        }
        if (!method.equals(propertyDescriptor.getReadMethod())) {
            return methodProxy.invokeSuper(object, args);
        }
        String fieldName = StringUtils.firstCharToLower(propertyDescriptor.getDisplayName());
        Field field = this.clazz.getDeclaredField(fieldName);
        if (field == null) {
            return methodProxy.invokeSuper(object, args);
        }
        CssQuery cssQuery = field.getAnnotation(CssQuery.class);
        if (cssQuery == null) {
            return methodProxy.invokeSuper(object, args);
        }
        Class<?> returnType = method.getReturnType();
        boolean isColl = Collection.class.isAssignableFrom(returnType);
        String cssQueryValue = cssQuery.value();
        boolean isInner = cssQuery.inner();
        if (isInner) {
            return this.proxyInner(cssQueryValue, method, returnType, isColl);
        }
        Object proxyValue = this.proxyValue(cssQueryValue, cssQuery, returnType, isColl);
        if (String.class.isAssignableFrom(returnType)) {
            return proxyValue;
        }
        TypeDescriptor typeDescriptor = new TypeDescriptor(field);
        return ConvertUtils.convert(proxyValue, typeDescriptor);
    }

    @Nullable
    private Object proxyValue(String cssQueryValue, CssQuery cssQuery, Class<?> returnType, boolean isColl) {
        if (isColl) {
            Elements elements = Selector.select((String)cssQueryValue, (Element)this.element);
            Collection<Object> valueList = this.newColl(returnType);
            if (elements.isEmpty()) {
                return valueList;
            }
            for (Element select : elements) {
                String value = this.getValue(select, cssQuery);
                if (value == null) continue;
                valueList.add(value);
            }
            return valueList;
        }
        Element select = Selector.selectFirst((String)cssQueryValue, (Element)this.element);
        return this.getValue(select, cssQuery);
    }

    private Object proxyInner(String cssQueryValue, Method method, Class<?> returnType, boolean isColl) {
        if (isColl) {
            Elements elements = Selector.select((String)cssQueryValue, (Element)this.element);
            Collection<Object> valueList = this.newColl(returnType);
            ResolvableType resolvableType = ResolvableType.forMethodReturnType((Method)method);
            Class innerType = resolvableType.getGeneric(new int[]{0}).resolve();
            if (innerType == null) {
                throw new IllegalArgumentException("Class " + String.valueOf(returnType) + " \u8bfb\u53d6\u6cdb\u578b\u5931\u8d25\u3002");
            }
            for (Element select : elements) {
                valueList.add(DomMapper.readValue(select, innerType));
            }
            return valueList;
        }
        Element select = Selector.selectFirst((String)cssQueryValue, (Element)this.element);
        return DomMapper.readValue(select, returnType);
    }

    @Nullable
    private String getValue(@Nullable Element element, CssQuery cssQuery) {
        if (element == null) {
            return null;
        }
        String attrName = cssQuery.attr();
        String attrValue = StringUtils.isBlank(attrName) ? element.outerHtml() : ("html".equalsIgnoreCase(attrName) ? element.html() : ("text".equalsIgnoreCase(attrName) ? this.getText(element) : ("allText".equalsIgnoreCase(attrName) ? element.text() : element.attr(attrName))));
        String regex = cssQuery.regex();
        if (StringUtils.isBlank(attrValue) || StringUtils.isBlank(regex)) {
            return attrValue;
        }
        return this.getRegexValue(regex, cssQuery.regexGroup(), attrValue);
    }

    @Nullable
    private String getRegexValue(String regex, int regexGroup, String value) {
        Matcher matcher = Pattern.compile(regex, 34).matcher(value);
        if (!matcher.find()) {
            return null;
        }
        if (regexGroup > 0) {
            return matcher.group(regexGroup);
        }
        return matcher.group();
    }

    private String getText(Element element) {
        return element.childNodes().stream().filter(node -> node instanceof TextNode).map(node -> (TextNode)node).map(TextNode::text).collect(Collectors.joining());
    }

    private Collection<Object> newColl(Class<?> returnType) {
        return Set.class.isAssignableFrom(returnType) ? new HashSet() : new ArrayList();
    }
}

