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

package com.kuma.boot.office.convert.html2pdf;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Html 转 Pdf 工具类
 *
 * @since 2020/11/24 11:23
 */
public class Html2PdfUtil {

    /**
     * `html` 转 `pdf`
     *
     * @param htmlBytes: html字节码
     * @return 生成的`pdf`字节码
     * @since 2020/11/24 11:26
     */
    public static byte[] htmlBytes2PdfBytes(byte[] htmlBytes) throws Exception {
        Document document = new Document(new ByteArrayInputStream(htmlBytes));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream, SaveFormat.PDF);
        // 返回生成的`pdf`字节码
        return outputStream.toByteArray();
    }

    /**
     * `html` 转 `pdf`
     *
     * @param htmlBytes: html字节码
     * @param pdfFilePath: 需转换的`pdf`文件路径
     * @return 生成的`pdf`文件数据
     * @since 2020/11/24 11:26
     */
    public static File htmlBytes2PdfFile(byte[] htmlBytes, String pdfFilePath) throws Exception {
        Document document = new Document(new ByteArrayInputStream(htmlBytes));
        document.save(pdfFilePath, SaveFormat.PDF);
        return new File(pdfFilePath);
    }
}
