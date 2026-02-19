/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.xhtmlrenderer.swing.Java2DRenderer
 */
package com.kuma.boot.office.convert.html2img;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.SAXException;

public class Html2ImgUtil {
    public static byte[] htmlBytes2JpgBytes(byte[] htmlBytes) throws ParserConfigurationException, IOException, SAXException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(htmlBytes);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(byteArrayInputStream);
        Java2DRenderer renderer = new Java2DRenderer(document, 800, 1000);
        BufferedImage bufferedImage = renderer.getImage();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage)bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

