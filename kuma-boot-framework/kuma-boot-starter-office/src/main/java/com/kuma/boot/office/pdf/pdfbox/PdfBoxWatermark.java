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

import java.io.IOException;

// 使用 PDFBox 来实现 PDF 添加水印
public class PdfBoxWatermark {
    public static void main(String[] args) throws IOException {
        // 读取原始 PDF 文件
        //        PDDocument document = PDDocument.load(new File("original.pdf"));
        //
        //        // 遍历 PDF 中的所有页面
        //        for (int i = 0; i < document.getNumberOfPages(); i++) {
        //            PDPage page = document.getPage(i);
        //            PDPageContentStream contentStream = new PDPageContentStream(document, page,
        // PDPageContentStream.AppendMode.APPEND, true, true);
        //
        //            // 设置字体和字号
        //            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 36);
        //
        //            // 设置透明度
        //            contentStream.setNonStrokingColor(200, 200, 200);
        //
        //            // 添加文本水印
        //            contentStream.beginText();
        //            contentStream.newLineAtOffset(100, 100); // 设置水印位置
        //            contentStream.showText("Watermark"); // 设置水印内容
        //            contentStream.endText();
        //
        //            contentStream.close();
        //        }

        // 保存修改后的 PDF 文件
        //        document.save(new File("output.pdf"));
        //        document.close();
    }
}
