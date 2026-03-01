/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  jakarta.xml.bind.JAXB
 */
package com.kuma.boot.dingtalk.utils;

import jakarta.xml.bind.JAXB;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class XmlUtils {
    private XmlUtils() {
    }

    public static <T> T xmlToJavaBean(String xmlString, Class<T> clazz) {
        return (T)JAXB.unmarshal((Reader)new StringReader(xmlString), clazz);
    }

    public static <T> T xmlToJavaBean(List<String> xmlString, Class<T> clazz) {
        return (T)JAXB.unmarshal((Reader)new StringReader(String.join((CharSequence)"\r\n", xmlString)), clazz);
    }

    public static <T> String javaBeanToXML(T bean) {
        StringWriter writer = new StringWriter();
        JAXB.marshal(bean, (Writer)writer);
        return ((Object)writer).toString();
    }
}

