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

package com.kuma.boot.office.convert.pdf2Img;

import com.google.common.collect.Lists;
import com.kuma.boot.office.convert.util.MyFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pdf 转 PNG 工具类
 *
 * <p>ya
 *
 * @since 2021/3/19 16:14
 */
public class Pdf2PngUtil {

    /** dpi越大转换后越清晰，相对转换速度越慢 */
    private static final Integer DPI = 100;

    /** 转换后的图片类型 */
    private static final String IMG_TYPE = "png";

    /**
     * `pdf` 转 `png`
     *
     * @param pdfBytes: pdf字节码
     * @return 转换后的png字节码 ya
     * @since 2021/3/22 9:42
     */
    public List<byte[]> pdf2Png(byte[] pdfBytes) throws IOException {
        List<byte[]> result = new ArrayList<>();
        //        try (PDDocument document = PDDocument.load(pdfBytes)) {
        //            PDFRenderer renderer = new PDFRenderer(document);
        //            for (int i = 0; i < document.getNumberOfPages(); ++i) {
        //                BufferedImage bufferedImage = renderer.renderImageWithDPI(i, DPI);
        //                ByteArrayOutputStream out = new ByteArrayOutputStream();
        //                ImageIO.write(bufferedImage, IMG_TYPE, out);
        //                result.add(out.toByteArray());
        //            }
        //        }
        return result;
    }

    /**
     * `pdf` 转 `png`
     *
     * @param pdfBytes: pdf字节码
     * @param imgRootPath: 需转换的`png`文件路径
     * @return 图片文件数据列表 ya
     * @since 2021/3/19 16:16
     */
    public List<File> pdf2Png(byte[] pdfBytes, String imgRootPath) throws IOException {
        List<byte[]> pngBytesList = this.pdf2Png(pdfBytes);
        List<File> pngFileList = Lists.newArrayList();
        for (int i = 0; i < pngBytesList.size(); i++) {
            String imgPath = imgRootPath + "/" + (i + 1) + ".png";
            File pngFile = MyFileUtil.writeFileContent(pngBytesList.get(i), imgPath);
            pngFileList.add(pngFile);
        }
        return pngFileList;
    }
}
