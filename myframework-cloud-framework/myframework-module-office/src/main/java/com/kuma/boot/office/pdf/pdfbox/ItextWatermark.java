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

package com.kuma.boot.office.pdf.pdfbox;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;

// 使用 iText 来实现 PDF 添加水印
public class ItextWatermark {
    public static void main(String[] args) throws IOException, DocumentException {
        // 读取原始 PDF 文件
        PdfReader reader = new PdfReader("original.pdf");
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("output.pdf"));

        // 获取 PDF 中的页数
        int pageCount = reader.getNumberOfPages();

        // 添加水印
        for (int i = 1; i <= pageCount; i++) {
            PdfContentByte contentByte = stamper.getUnderContent(i); // 或者 getOverContent()
            contentByte.beginText();
            contentByte.setFontAndSize(BaseFont.createFont(), 36f);
            contentByte.setColorFill(BaseColor.LIGHT_GRAY);
            contentByte.showTextAligned(Element.ALIGN_CENTER, "Watermark", 300, 400, 45);
            contentByte.endText();
        }

        // 保存修改后的 PDF 文件并关闭文件流
        stamper.close();
        reader.close();
    }
}
