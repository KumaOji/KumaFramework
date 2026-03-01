/*
 * Copyright (c) 2020-2030, Kuma (2569277704@qq.com & https://blog.kumacloud.top/).
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

package com.kuma.boot.office.convert.word2img;

import com.aspose.words.Document;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.google.common.collect.Lists;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * Word 转 JPEG 工具类
 *
 * @since 2020/11/23 16:00
 */
public class Word2JpegUtil {

    /**
     * `word` 转 `jpeg`
     *
     * @param wordBytes: word字节码数据
     * @return 图片字节码数据列表
     * @since 2020/11/24 11:52
     */
    public static List<byte[]> wordBytes2JpegBytes(byte[] wordBytes) throws Exception {
        Document doc = new Document(new ByteArrayInputStream(wordBytes));
        ImageSaveOptions iso = new ImageSaveOptions(SaveFormat.JPEG);
        iso.setResolution(128);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);

        List<byte[]> jpegList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); i++) {
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.save(outputStream, iso);
            jpegList.add(outputStream.toByteArray());
        }

        return jpegList;
    }

    /**
     * `word` 转 `jpeg`
     *
     * @param wordBytes: word字节码数据
     * @param imgRootPath: 生成图片根路径
     * @return 图片文件数据列表
     * @since 2020/11/24 11:52
     */
    public static List<File> wordBytes2JpegFileList(byte[] wordBytes, String imgRootPath) throws Exception {
        Document doc = new Document(new ByteArrayInputStream(wordBytes));
        ImageSaveOptions iso = new ImageSaveOptions(SaveFormat.JPEG);
        iso.setResolution(128);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);

        List<File> jpegList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); i++) {
            String imgPath = imgRootPath + "/" + (i + 1) + ".jpg";
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            doc.save(imgPath, iso);
            jpegList.add(new File(imgPath));
        }

        return jpegList;
    }
}
