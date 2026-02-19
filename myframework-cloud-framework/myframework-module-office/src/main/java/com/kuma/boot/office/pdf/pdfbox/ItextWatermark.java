/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.itextpdf.text.BaseColor
 *  com.itextpdf.text.DocumentException
 *  com.itextpdf.text.pdf.BaseFont
 *  com.itextpdf.text.pdf.PdfContentByte
 *  com.itextpdf.text.pdf.PdfReader
 *  com.itextpdf.text.pdf.PdfStamper
 */
package com.kuma.boot.office.pdf.pdfbox;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ItextWatermark {
    public static void main(String[] args) throws IOException, DocumentException {
        PdfReader reader = new PdfReader("original.pdf");
        PdfStamper stamper = new PdfStamper(reader, (OutputStream)new FileOutputStream("output.pdf"));
        int pageCount = reader.getNumberOfPages();
        for (int i = 1; i <= pageCount; ++i) {
            PdfContentByte contentByte = stamper.getUnderContent(i);
            contentByte.beginText();
            contentByte.setFontAndSize(BaseFont.createFont(), 36.0f);
            contentByte.setColorFill(BaseColor.LIGHT_GRAY);
            contentByte.showTextAligned(1, "Watermark", 300.0f, 400.0f, 45.0f);
            contentByte.endText();
        }
        stamper.close();
        reader.close();
    }
}

