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

package com.kuma.boot.office.convert.html2img;

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
 * html 转 png 工具类
 *
 * @since 2020/11/25 17:16
 */
public class Html2PngUtil {

    /**
     * `html` 转 `png`
     *
     * @param htmlBytes: html字节码
     * @return 图片字节码数据列表
     * @since 2020/11/25 17:17
     */
    public static List<byte[]> htmlBytes2PngBytes(byte[] htmlBytes) throws Exception {
        Document doc = new Document(new ByteArrayInputStream(htmlBytes));
        ImageSaveOptions iso = new ImageSaveOptions(SaveFormat.PNG);
        iso.setResolution(128);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);
        List<byte[]> pngList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); i++) {
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            doc.save(outputStream, iso);
            pngList.add(outputStream.toByteArray());
        }
        return pngList;
    }

    /**
     * `html` 转 `png`
     *
     * @param htmlBytes: html字节码
     * @param imgRootPath: 需转换的`png`文件路径
     * @return 图片文件数据列表
     * @since 2020/11/25 17:17
     */
    public static List<File> htmlBytes2PngFileList(byte[] htmlBytes, String imgRootPath) throws Exception {
        Document doc = new Document(new ByteArrayInputStream(htmlBytes));
        ImageSaveOptions iso = new ImageSaveOptions(SaveFormat.PNG);
        iso.setResolution(128);
        iso.setPrettyFormat(true);
        iso.setUseAntiAliasing(true);

        List<File> pngList = Lists.newArrayList();
        for (int i = 0; i < doc.getPageCount(); i++) {
            String imgPath = imgRootPath + "/" + (i + 1) + ".png";
            PageSet pageSet = new PageSet(i);
            iso.setPageSet(pageSet);
            doc.save(imgPath, iso);
            pngList.add(new File(imgPath));
        }
        return pngList;
    }
}
