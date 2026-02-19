/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.kuma.boot.office.convert.pdf2Img;

import com.google.common.collect.Lists;
import com.kuma.boot.office.convert.util.MyFileUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pdf2PngUtil {
    private static final Integer DPI = 100;
    private static final String IMG_TYPE = "png";

    public List<byte[]> pdf2Png(byte[] pdfBytes) throws IOException {
        ArrayList<byte[]> result = new ArrayList<byte[]>();
        return result;
    }

    public List<File> pdf2Png(byte[] pdfBytes, String imgRootPath) throws IOException {
        List<byte[]> pngBytesList = this.pdf2Png(pdfBytes);
        ArrayList pngFileList = Lists.newArrayList();
        for (int i = 0; i < pngBytesList.size(); ++i) {
            String imgPath = imgRootPath + "/" + (i + 1) + ".png";
            File pngFile = MyFileUtil.writeFileContent(pngBytesList.get(i), imgPath);
            pngFileList.add(pngFile);
        }
        return pngFileList;
    }
}

