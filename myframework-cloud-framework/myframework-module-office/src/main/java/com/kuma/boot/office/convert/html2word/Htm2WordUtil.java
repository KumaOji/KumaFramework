/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.aspose.words.DocumentBuilder
 *  org.apache.poi.poifs.filesystem.DirectoryNode
 *  org.apache.poi.poifs.filesystem.DocumentEntry
 *  org.apache.poi.poifs.filesystem.POIFSFileSystem
 */
package com.kuma.boot.office.convert.html2word;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Htm2WordUtil {
    public static byte[] htmlBytes2WordBytes(byte[] htmlBytes) throws Exception {
        Document doc = new Document();
        DocumentBuilder builder = new DocumentBuilder(doc);
        builder.insertHtml(new String(htmlBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doc.save((OutputStream)outputStream, 20);
        return outputStream.toByteArray();
    }

    public static File htmlBytes2WordFile(byte[] htmlBytes, String wordFilePath) throws Exception {
        Document doc = new Document();
        DocumentBuilder builder = new DocumentBuilder(doc);
        builder.insertHtml(new String(htmlBytes));
        doc.save(wordFilePath, 20);
        return new File(wordFilePath);
    }

    public static String html2Word(String html, String fileRootPath, String wordFileName) throws IOException {
        String wordFilePath = fileRootPath + "/" + wordFileName;
        byte[] htmlBytes = html.getBytes();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlBytes);
        POIFSFileSystem poifs = new POIFSFileSystem();
        DirectoryNode directory = poifs.getRoot();
        DocumentEntry documentEntry = directory.createDocument("WordDocument", (InputStream)inputStream);
        FileOutputStream outputStream = new FileOutputStream(wordFilePath);
        poifs.writeFilesystem((OutputStream)outputStream);
        inputStream.close();
        outputStream.close();
        return wordFilePath;
    }
}

