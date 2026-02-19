/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.aspose.words.ImageSaveOptions
 *  com.aspose.words.PageSet
 *  com.aspose.words.SaveOptions
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.office.convert.word2img;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveOptions;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Word2JpegUtil {
    public static List<byte[]> wordBytes2JpegBytes(byte[] wordBytes) throws Exception {
        Document doc = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        ImageSaveOptions iso = new ImageSaveOptions(104);
        iso.setResolution(128.0f);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);
        ArrayList jpegList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); ++i) {
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.save((OutputStream)outputStream, (SaveOptions)iso);
            jpegList.add(outputStream.toByteArray());
        }
        return jpegList;
    }

    public static List<File> wordBytes2JpegFileList(byte[] wordBytes, String imgRootPath) throws Exception {
        Document doc = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        ImageSaveOptions iso = new ImageSaveOptions(104);
        iso.setResolution(128.0f);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);
        ArrayList jpegList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); ++i) {
            String imgPath = imgRootPath + "/" + (i + 1) + ".jpg";
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            doc.save(imgPath, (SaveOptions)iso);
            jpegList.add(new File(imgPath));
        }
        return jpegList;
    }
}

