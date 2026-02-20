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

import com.aspose.pdf.Document;
import com.aspose.pdf.HorizontalAlignment;
import com.aspose.pdf.ImageStamp;
import com.aspose.pdf.TextStamp;
import com.aspose.pdf.VerticalAlignment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Spring Boot 应用程序中调用 Aspose.PDF for Java 的 API 设置 PDF 水印
// @RestController
// @RequestMapping("/api/pdf")
public class PdfController {

    @PostMapping("/addTextWatermark")
    public ResponseEntity<byte[]> addTextWatermark(@RequestParam("file") MultipartFile file) throws IOException {
        // 加载 PDF 文件
        Document pdfDocument = new Document(file.getInputStream());
        TextStamp textStamp = new TextStamp("Watermark");
        textStamp.setWordWrap(true);
        textStamp.setVerticalAlignment(VerticalAlignment.Center);
        textStamp.setHorizontalAlignment(HorizontalAlignment.Center);
        pdfDocument.getPages().get_Item(1).addStamp(textStamp);

        // 保存 PDF 文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfDocument.save(outputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"watermarked.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());
    }

    @PostMapping("/addImageWatermark")
    public ResponseEntity<byte[]> addImageWatermark(@RequestParam("file") MultipartFile file) throws IOException {
        // 加载 PDF 文件
        Document pdfDocument = new Document(file.getInputStream());
        ImageStamp imageStamp = new ImageStamp("watermark.png");
        imageStamp.setWidth(100);
        imageStamp.setHeight(100);
        imageStamp.setVerticalAlignment(VerticalAlignment.Center);
        imageStamp.setHorizontalAlignment(HorizontalAlignment.Center);
        pdfDocument.getPages().get_Item(1).addStamp(imageStamp);

        // 保存 PDF 文件
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfDocument.save(outputStream);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"watermarked.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());
    }
}
