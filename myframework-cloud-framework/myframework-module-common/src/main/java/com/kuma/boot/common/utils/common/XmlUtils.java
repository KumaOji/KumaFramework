/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package com.kuma.boot.common.utils.common;

import com.kuma.boot.common.utils.exception.ExceptionUtils;
import com.kuma.boot.common.utils.io.IoUtils;
import com.kuma.boot.common.utils.log.LogUtils;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XmlUtils {
    private final XPath path;
    private final Document doc;

    private XmlUtils(InputSource inputSource, boolean unsafe) throws Exception {
        DocumentBuilderFactory dbf = unsafe ? XmlUtils.getUnsafeDocumentBuilderFactory() : XmlUtils.getDocumentBuilderFactory();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        this.doc = documentBuilder.parse(inputSource);
        this.path = XmlUtils.getXpathFactory().newXPath();
    }

    private static XmlUtils createSafe(InputSource inputSource) {
        return XmlUtils.create(inputSource, false);
    }

    private static XmlUtils createUnsafe(InputSource inputSource) {
        return XmlUtils.create(inputSource, true);
    }

    private static XmlUtils create(InputSource inputSource, boolean unsafe) {
        try {
            return new XmlUtils(inputSource, unsafe);
        }
        catch (Exception e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public static XmlUtils safe(InputStream is) {
        InputSource inputSource = new InputSource(is);
        return XmlUtils.createSafe(inputSource);
    }

    public static XmlUtils safe(String xmlStr) {
        StringReader sr = new StringReader(xmlStr.trim());
        InputSource inputSource = new InputSource(sr);
        XmlUtils xmlUtils = XmlUtils.createSafe(inputSource);
        IoUtils.closeQuietly(sr);
        return xmlUtils;
    }

    public static XmlUtils unsafe(InputStream is) {
        InputSource inputSource = new InputSource(is);
        return XmlUtils.createUnsafe(inputSource);
    }

    public static XmlUtils unsafe(String xmlStr) {
        StringReader sr = new StringReader(xmlStr.trim());
        InputSource inputSource = new InputSource(sr);
        XmlUtils xmlUtils = XmlUtils.createUnsafe(inputSource);
        IoUtils.closeQuietly(sr);
        return xmlUtils;
    }

    public Object evalXPath(String expression, @Nullable Object item, QName returnType) {
        item = null == item ? this.doc : item;
        try {
            return this.path.evaluate(expression, item, returnType);
        }
        catch (XPathExpressionException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    public String getString(String expression) {
        return (String)this.evalXPath(expression, null, XPathConstants.STRING);
    }

    public Boolean getBoolean(String expression) {
        return (Boolean)this.evalXPath(expression, null, XPathConstants.BOOLEAN);
    }

    public Number getNumber(String expression) {
        return (Number)this.evalXPath(expression, null, XPathConstants.NUMBER);
    }

    public Node getNode(String expression) {
        return (Node)this.evalXPath(expression, null, XPathConstants.NODE);
    }

    public NodeList getNodeList(String expression) {
        return (NodeList)this.evalXPath(expression, null, XPathConstants.NODESET);
    }

    public String getString(Object node, String expression) {
        return (String)this.evalXPath(expression, node, XPathConstants.STRING);
    }

    public Boolean getBoolean(Object node, String expression) {
        return (Boolean)this.evalXPath(expression, node, XPathConstants.BOOLEAN);
    }

    public Number getNumber(Object node, String expression) {
        return (Number)this.evalXPath(expression, node, XPathConstants.NUMBER);
    }

    public Node getNode(Object node, String expression) {
        return (Node)this.evalXPath(expression, node, XPathConstants.NODE);
    }

    public NodeList getNodeList(Object node, String expression) {
        return (NodeList)this.evalXPath(expression, node, XPathConstants.NODESET);
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() {
        return XmlHelperHolder.DOCUMENT_BUILDER_FACTORY;
    }

    private static DocumentBuilderFactory getUnsafeDocumentBuilderFactory() {
        return XmlHelperHolder.UNSAFE_DOCUMENT_BUILDER_FACTORY;
    }

    private static XPathFactory getXpathFactory() {
        return XmlHelperHolder.XPATH_FACTORY;
    }

    private static class XmlHelperHolder {
        private static final String FEATURE_HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
        private static final String FEATURE_HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
        private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = XmlHelperHolder.newDocumentBuilderFactory();
        private static final DocumentBuilderFactory UNSAFE_DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

        private XmlHelperHolder() {
        }

        private static DocumentBuilderFactory newDocumentBuilderFactory() {
            DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
            df.setXIncludeAware(false);
            df.setExpandEntityReferences(false);
            df.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            df.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
            XmlHelperHolder.setDocumentBuilderFactoryFeature(df, "http://javax.xml.XMLConstants/feature/secure-processing", true);
            XmlHelperHolder.setDocumentBuilderFactoryFeature(df, FEATURE_HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_GENERAL_ENTITIES, false);
            XmlHelperHolder.setDocumentBuilderFactoryFeature(df, FEATURE_HTTP_XML_ORG_SAX_FEATURES_EXTERNAL_PARAMETER_ENTITIES, false);
            XmlHelperHolder.setDocumentBuilderFactoryFeature(df, "http://apache.org/xml/features/disallow-doctype-decl", true);
            XmlHelperHolder.setDocumentBuilderFactoryFeature(df, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return df;
        }

        private static void setDocumentBuilderFactoryFeature(DocumentBuilderFactory documentBuilderFactory, String feature, boolean state) {
            try {
                documentBuilderFactory.setFeature(feature, state);
            }
            catch (Exception e) {
                LogUtils.warn(String.format("Failed to set the XML Document Builder factory feature %s to %s", feature, state), e);
            }
        }
    }
}

