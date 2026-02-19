/*
 * Decompiled with CFR 0.152.
 */
package com.kuma.boot.office.convert;

import com.kuma.boot.office.convert.config.MatchLicense;
import com.kuma.boot.office.convert.doc2docx.Doc2DocxUtil;
import com.kuma.boot.office.convert.excel2pdf.Excel2PdfUtil;
import com.kuma.boot.office.convert.html2img.Html2ImgUtil;
import com.kuma.boot.office.convert.html2img.Html2PngUtil;
import com.kuma.boot.office.convert.html2pdf.Html2PdfUtil;
import com.kuma.boot.office.convert.html2word.Htm2WordUtil;
import com.kuma.boot.office.convert.pdf2Img.Pdf2PngUtil;
import com.kuma.boot.office.convert.pdf2word.Pdf2WordUtil;
import com.kuma.boot.office.convert.word2html.Word2HtmlUtil;
import com.kuma.boot.office.convert.word2img.Word2JpegUtil;
import com.kuma.boot.office.convert.word2img.Word2PngUtil;
import com.kuma.boot.office.convert.word2pdf.Word2PdfUtil;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class FileConvertUtil {
    public static byte[] wordBytes2HtmlBytes(byte[] wordBytes) throws Exception {
        MatchLicense.init();
        return Word2HtmlUtil.wordBytes2HtmlBytes(wordBytes);
    }

    public static String wordBytes2HtmlStr(byte[] wordBytes) throws Exception {
        MatchLicense.init();
        byte[] htmlBytes = Word2HtmlUtil.wordBytes2HtmlBytes(wordBytes);
        return new String(htmlBytes);
    }

    public static File wordBytes2HtmlFile(byte[] wordBytes, String htmlFilePath) throws Exception {
        MatchLicense.init();
        return Word2HtmlUtil.wordBytes2HtmlFile(wordBytes, htmlFilePath);
    }

    public static byte[] wordBytes2PdfBytes(byte[] wordBytes) throws Exception {
        MatchLicense.init();
        return Word2PdfUtil.wordBytes2PdfBytes(wordBytes);
    }

    public static File wordBytes2PdfFile(byte[] wordBytes, String pdfFilePath) throws Exception {
        MatchLicense.init();
        return Word2PdfUtil.wordBytes2PdfFile(wordBytes, pdfFilePath);
    }

    public static byte[] docBytes2DocxBytes(byte[] docBytes) throws Exception {
        MatchLicense.init();
        return Doc2DocxUtil.docBytes2DocxBytes(docBytes);
    }

    public static File docBytes2DocxFile(byte[] docBytes, String docxFilePath) throws Exception {
        MatchLicense.init();
        return Doc2DocxUtil.docBytes2DocxFile(docBytes, docxFilePath);
    }

    public static byte[] htmlBytes2WordBytes(byte[] htmlBytes) throws Exception {
        MatchLicense.init();
        return Htm2WordUtil.htmlBytes2WordBytes(htmlBytes);
    }

    public static byte[] html2WordBytes(String html) throws Exception {
        MatchLicense.init();
        return Htm2WordUtil.htmlBytes2WordBytes(html.getBytes());
    }

    public static File htmlBytes2WordFile(byte[] htmlBytes, String wordFilePath) throws Exception {
        MatchLicense.init();
        return Htm2WordUtil.htmlBytes2WordFile(htmlBytes, wordFilePath);
    }

    public static byte[] htmlBytes2PdfBytes(byte[] htmlBytes) throws Exception {
        MatchLicense.init();
        return Html2PdfUtil.htmlBytes2PdfBytes(htmlBytes);
    }

    public static File htmlBytes2PdfFile(byte[] htmlBytes, String pdfFilePath) throws Exception {
        MatchLicense.init();
        return Html2PdfUtil.htmlBytes2PdfFile(htmlBytes, pdfFilePath);
    }

    public static byte[] excelBytes2PdfBytes(byte[] excelBytes) throws Exception {
        MatchLicense.init();
        return Excel2PdfUtil.excelBytes2PdfBytes(excelBytes);
    }

    public static File excelBytes2PdfFile(byte[] excelBytes, String pdfFilePath) throws Exception {
        MatchLicense.init();
        return Excel2PdfUtil.excelBytes2PdfFile(excelBytes, pdfFilePath);
    }

    public static List<byte[]> wordBytes2JpegBytes(byte[] wordBytes) throws Exception {
        MatchLicense.init();
        return Word2JpegUtil.wordBytes2JpegBytes(wordBytes);
    }

    public static List<File> wordBytes2JpegFileList(byte[] wordBytes, String imgRootPath) throws Exception {
        MatchLicense.init();
        return Word2JpegUtil.wordBytes2JpegFileList(wordBytes, imgRootPath);
    }

    public static List<byte[]> wordBytes2PngBytes(byte[] wordBytes) throws Exception {
        MatchLicense.init();
        return Word2PngUtil.wordBytes2PngBytes(wordBytes);
    }

    public static List<File> wordBytes2PngFileList(byte[] wordBytes, String imgRootPath) throws Exception {
        MatchLicense.init();
        return Word2PngUtil.wordBytes2PngFileList(wordBytes, imgRootPath);
    }

    public static List<byte[]> htmlBytes2PngBytes(byte[] htmlBytes) throws Exception {
        MatchLicense.init();
        return Html2PngUtil.htmlBytes2PngBytes(htmlBytes);
    }

    public static List<File> htmlBytes2PngFileList(byte[] htmlBytes, String imgRootPath) throws Exception {
        MatchLicense.init();
        return Html2PngUtil.htmlBytes2PngFileList(htmlBytes, imgRootPath);
    }

    public static byte[] htmlBytes2JpgBytes(byte[] htmlBytes) throws ParserConfigurationException, IOException, SAXException {
        return Html2ImgUtil.htmlBytes2JpgBytes(htmlBytes);
    }

    public static void pdf2Word(String pdfPath, String wordPath) {
        new Pdf2WordUtil().pdf2Word(pdfPath, wordPath);
    }

    public static List<byte[]> pdfBytes2PngBytes(byte[] pdfBytes) throws IOException {
        return new Pdf2PngUtil().pdf2Png(pdfBytes);
    }

    public static List<File> pdfBytes2PngFileList(byte[] pdfBytes, String imgRootPath) throws IOException {
        return new Pdf2PngUtil().pdf2Png(pdfBytes, imgRootPath);
    }
}

