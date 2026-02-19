/*
 * Copyright (c) 2020-2030, Shuigedeng (2569277704@qq.com & https://blog.kumacloud.top/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuma.boot.data.mybatis.sharding.utils;

import com.kuma.boot.common.utils.log.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

/**
 * 主要是读取XML，并修改XML的内容，生成新的xml，供mybatis读取消费
 *
 * @author winjeg
 */
public class XmlUtils {
    private static final SAXBuilder XML_BUILDER = new SAXBuilder();

    public static com.kuma.boot.data.mybatis.sharding.utils.Pair<String, Resource> changeMapperNameSpace(String dsName, Resource resource) {
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            LogUtils.warn("changeMapperNameSpace - exception occurred", e);
        }
        if (inputStream == null) {
            return null;
        }
        Document doc = null;
        try {
            doc = XML_BUILDER.build(inputStream);
        } catch (JDOMException | IOException e) {
            LogUtils.warn("changeMapperNameSpace - error in mapper");
            return null;
        }
        Element element = doc.getRootElement();
        String originalName = element.getAttribute("namespace").getValue();
        if (originalName == null || originalName.isEmpty()) {
            return null;
        }
        String name = com.kuma.boot.data.mybatis.sharding.utils.NameUtils.buildClassName(dsName, originalName);
        element.setAttribute("namespace", name);
        doc.detachRootElement();
        doc.setRootElement(element);
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        format.setTextMode(Format.TextMode.PRESERVE);
        XMLOutputter output = new XMLOutputter(format);
        String xml = output.outputString(doc);
        InputStreamResource res =
                new InputStreamResource(
                        new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), name);
        return com.kuma.boot.data.mybatis.sharding.utils.Pair.of(name, res);
    }

    /**
     * 读取一个mapper的namespace
     *
     * @param resource 资源
     * @return namespace
     */
    public static String extractNamespace(Resource resource) {
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            LogUtils.warn("changeMapperNameSpace - exception occurred", e);
        }
        if (inputStream == null) {
            return null;
        }
        Document doc = null;
        try {
            doc = XML_BUILDER.build(inputStream);
        } catch (JDOMException | IOException e) {
            LogUtils.warn("changeMapperNameSpace - error in mapper");
            return null;
        }
        Element element = doc.getRootElement();
        return element.getAttribute("namespace").getValue();
    }
}
