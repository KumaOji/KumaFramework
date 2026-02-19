/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 */
package com.kuma.boot.office.convert.html2pdf;

import com.aspose.words.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Html2PdfUtil {
    public static byte[] htmlBytes2PdfBytes(byte[] htmlBytes) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(htmlBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save((OutputStream)outputStream, 40);
        return outputStream.toByteArray();
    }

    public static File htmlBytes2PdfFile(byte[] htmlBytes, String pdfFilePath) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(htmlBytes));
        document.save(pdfFilePath, 40);
        return new File(pdfFilePath);
    }
}

