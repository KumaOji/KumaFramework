/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jsoup.helper.DataUtil
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.parser.Parser
 *  org.jsoup.select.Elements
 *  org.springframework.cglib.proxy.Callback
 *  org.springframework.cglib.proxy.Enhancer
 */
package com.kuma.boot.common.utils.spider;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.spider.CssQuery;
import com.kuma.boot.common.utils.spider.CssQueryMethodInterceptor;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

public class DomMapper {
    public static Document readDocument(InputStream inputStream) {
        try {
            return DataUtil.load((InputStream)inputStream, (String)StandardCharsets.UTF_8.name(), (String)"");
        }
        catch (IOException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static Document readDocument(String html) {
        return Parser.parse((String)html, (String)"");
    }

    public static <T> T readValue(InputStream inputStream, Class<T> clazz) {
        return DomMapper.readValue((Element)DomMapper.readDocument(inputStream), clazz);
    }

    public static <T> T readValue(String html, Class<T> clazz) {
        return DomMapper.readValue((Element)DomMapper.readDocument(html), clazz);
    }

    public static <T> T readValue(Element doc, Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setUseCache(true);
        enhancer.setContextClass(clazz);
        enhancer.setCallback((Callback)new CssQueryMethodInterceptor(clazz, doc));
        return (T)enhancer.create();
    }

    public static <T> List<T> readList(InputStream inputStream, Class<T> clazz) {
        return DomMapper.readList((Element)DomMapper.readDocument(inputStream), clazz);
    }

    public static <T> List<T> readList(String html, Class<T> clazz) {
        return DomMapper.readList((Element)DomMapper.readDocument(html), clazz);
    }

    public static <T> List<T> readList(Element doc, Class<T> clazz) {
        CssQuery annotation = clazz.getAnnotation(CssQuery.class);
        if (annotation == null) {
            throw new IllegalArgumentException("DomMapper readList " + String.valueOf(clazz) + " mast has annotation @CssQuery.");
        }
        String cssQueryValue = annotation.value();
        Elements elements = doc.select(cssQueryValue);
        ArrayList<T> valueList = new ArrayList<T>();
        for (Element element : elements) {
            valueList.add(DomMapper.readValue(element, clazz));
        }
        return valueList;
    }
}

