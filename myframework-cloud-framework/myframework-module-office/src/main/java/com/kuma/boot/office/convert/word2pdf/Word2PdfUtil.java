/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 */
package com.kuma.boot.office.convert.word2pdf;

import com.aspose.words.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Word2PdfUtil {
    public static byte[] wordBytes2PdfBytes(byte[] wordBytes) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save((OutputStream)outputStream, 40);
        return outputStream.toByteArray();
    }

    public static File wordBytes2PdfFile(byte[] wordBytes, String pdfFilePath) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        document.save(pdfFilePath, 40);
        return new File(pdfFilePath);
    }
}

