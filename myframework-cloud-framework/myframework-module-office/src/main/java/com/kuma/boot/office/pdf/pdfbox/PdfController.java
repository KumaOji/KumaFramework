/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.pdf.Document
 *  com.aspose.pdf.HorizontalAlignment
 *  com.aspose.pdf.ImageStamp
 *  com.aspose.pdf.Stamp
 *  com.aspose.pdf.TextStamp
 *  com.aspose.pdf.VerticalAlignment
 *  org.springframework.http.MediaType
 *  org.springframework.http.ResponseEntity
 *  org.springframework.http.ResponseEntity$BodyBuilder
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestParam
 *  org.springframework.web.multipart.MultipartFile
 */
package com.kuma.boot.office.pdf.pdfbox;

import com.aspose.pdf.Document;
import com.aspose.pdf.HorizontalAlignment;
import com.aspose.pdf.ImageStamp;
import com.aspose.pdf.Stamp;
import com.aspose.pdf.TextStamp;
import com.aspose.pdf.VerticalAlignment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class PdfController {
    @PostMapping(value={"/addTextWatermark"})
    public ResponseEntity<byte[]> addTextWatermark(@RequestParam(value="file") MultipartFile file) throws IOException {
        Document pdfDocument = new Document(file.getInputStream());
        TextStamp textStamp = new TextStamp("Watermark");
        textStamp.setWordWrap(true);
        textStamp.setVerticalAlignment(VerticalAlignment.Center);
        textStamp.setHorizontalAlignment(HorizontalAlignment.Center);
        pdfDocument.getPages().get_Item(1).addStamp((Stamp)textStamp);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfDocument.save((OutputStream)outputStream);
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().header("Content-Disposition", new String[]{"attachment; filename=\"watermarked.pdf\""})).contentType(MediaType.APPLICATION_PDF).body((Object)outputStream.toByteArray());
    }

    @PostMapping(value={"/addImageWatermark"})
    public ResponseEntity<byte[]> addImageWatermark(@RequestParam(value="file") MultipartFile file) throws IOException {
        Document pdfDocument = new Document(file.getInputStream());
        ImageStamp imageStamp = new ImageStamp("watermark.png");
        imageStamp.setWidth(100.0);
        imageStamp.setHeight(100.0);
        imageStamp.setVerticalAlignment(VerticalAlignment.Center);
        imageStamp.setHorizontalAlignment(HorizontalAlignment.Center);
        pdfDocument.getPages().get_Item(1).addStamp((Stamp)imageStamp);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfDocument.save((OutputStream)outputStream);
        return ((ResponseEntity.BodyBuilder)ResponseEntity.ok().header("Content-Disposition", new String[]{"attachment; filename=\"watermarked.pdf\""})).contentType(MediaType.APPLICATION_PDF).body((Object)outputStream.toByteArray());
    }
}

