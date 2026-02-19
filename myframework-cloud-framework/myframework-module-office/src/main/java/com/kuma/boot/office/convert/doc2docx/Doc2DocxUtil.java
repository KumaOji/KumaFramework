/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 */
package com.kuma.boot.office.convert.doc2docx;

import com.aspose.words.Document;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Doc2DocxUtil {
    public static byte[] docBytes2DocxBytes(byte[] docBytes) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(docBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save((OutputStream)outputStream, 20);
        return outputStream.toByteArray();
    }

    public static File docBytes2DocxFile(byte[] docBytes, String docxFilePath) throws Exception {
        Document document = new Document((InputStream)new ByteArrayInputStream(docBytes));
        document.save(docxFilePath, 20);
        return new File(docxFilePath);
    }

    public static String docBytes2DocxFile(String fileRootPath, String wordFileName, String wordFileNameNew) throws Exception {
        String wordFilePath = fileRootPath + "/" + wordFileName;
        String wordFilePathNew = fileRootPath + "/" + wordFileNameNew;
        Document document = new Document(wordFilePath);
        document.save(wordFilePathNew);
        return wordFilePathNew;
    }
}

