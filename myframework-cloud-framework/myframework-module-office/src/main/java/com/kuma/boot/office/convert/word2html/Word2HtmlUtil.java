/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.apache.poi.hwpf.HWPFDocument
 *  org.apache.poi.hwpf.HWPFDocumentCore
 *  org.apache.poi.hwpf.converter.PicturesManager
 *  org.apache.poi.hwpf.converter.WordToHtmlConverter
 *  org.apache.poi.hwpf.usermodel.PictureType
 *  org.apache.poi.xwpf.converter.core.FileImageExtractor
 *  org.apache.poi.xwpf.converter.core.IImageExtractor
 *  org.apache.poi.xwpf.converter.core.IURIResolver
 *  org.apache.poi.xwpf.converter.core.Options
 *  org.apache.poi.xwpf.converter.xhtml.XHTMLConverter
 *  org.apache.poi.xwpf.converter.xhtml.XHTMLOptions
 *  org.apache.poi.xwpf.usermodel.XWPFDocument
 *  org.springframework.util.CollectionUtils
 */
package com.kuma.boot.office.convert.word2html;

import com.aspose.words.Document;
import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.office.convert.config.Constants;
import com.kuma.boot.office.convert.util.MyFileUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.HWPFDocumentCore;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.IImageExtractor;
import org.apache.poi.xwpf.converter.core.IURIResolver;
import org.apache.poi.xwpf.converter.core.Options;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.util.CollectionUtils;

public class Word2HtmlUtil {
    public static byte[] wordBytes2HtmlBytes(byte[] wordBytes) throws Exception {
        String tmpHtmlFilePath = Constants.DEFAULT_FOLDER_TMP_GENERATE + "/" + System.currentTimeMillis() + "-" + Word2HtmlUtil.getUUID32() + ".html";
        Document doc = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        doc.save(tmpHtmlFilePath, 50);
        byte[] htmlBytes = MyFileUtil.readBytes(tmpHtmlFilePath);
        MyFileUtil.deleteFileOrFolder(tmpHtmlFilePath);
        return htmlBytes;
    }

    public static File wordBytes2HtmlFile(byte[] wordBytes, String htmlFilePath) throws Exception {
        Document doc = new Document((InputStream)new ByteArrayInputStream(wordBytes));
        doc.save(htmlFilePath, 50);
        return new File(htmlFilePath);
    }

    private static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String word2Html(String fileRootPath, String wordFileName, final String imagePath) throws IOException, ParserConfigurationException, TransformerException {
        String wordFilePath = fileRootPath + "/" + wordFileName;
        String wordFileNameSuffix = wordFileName.substring(wordFileName.lastIndexOf(".") + 1);
        LogUtils.debug((String)"\u300aword\u8f6chtml\u300b word\u6587\u4ef6\u8def\u5f84:\u3010{}\u3011", (Object[])new Object[]{wordFilePath});
        boolean ifDocxSuffix = false;
        if ("docx".equals(wordFileNameSuffix)) {
            ifDocxSuffix = true;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (ifDocxSuffix) {
            FileInputStream inputStream = new FileInputStream(new File(wordFilePath));
            XWPFDocument document = new XWPFDocument((InputStream)inputStream);
            XHTMLOptions options = XHTMLOptions.create();
            options.setExtractor((IImageExtractor)new FileImageExtractor(new File(imagePath)));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);
            options.URIResolver(new IURIResolver(){

                public String resolve(String uri) {
                    return imagePath + "/" + uri;
                }
            });
            XHTMLConverter.getInstance().convert(document, (OutputStream)out, (Options)options);
        } else {
            HWPFDocument wordDocument = new HWPFDocument((InputStream)new FileInputStream(wordFilePath));
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.setPicturesManager(new PicturesManager(){

                public String savePicture(byte[] content, PictureType pictureType, String suggestedName, float widthInches, float heightInches) {
                    if (pictureType.equals((Object)PictureType.UNKNOWN)) {
                        return "";
                    }
                    String htmlImgPath = imagePath + "/" + suggestedName;
                    try {
                        FileOutputStream os = new FileOutputStream(MyFileUtil.touch(htmlImgPath));
                        os.write(content);
                        os.close();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    LogUtils.debug((String)"\u56fe\u7247\u5730\u5740\uff1a\u3010{}\u3011", (Object[])new Object[]{htmlImgPath});
                    return htmlImgPath;
                }
            });
            wordToHtmlConverter.processDocument((HWPFDocumentCore)wordDocument);
            List picList = wordDocument.getPicturesTable().getAllPictures();
            if (!CollectionUtils.isEmpty((Collection)picList)) {
                picList.forEach(pic -> {});
            }
            org.w3c.dom.Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(out);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty("encoding", "utf-8");
            serializer.setOutputProperty("indent", "yes");
            serializer.setOutputProperty("method", "html");
            serializer.transform(domSource, streamResult);
        }
        out.close();
        return out.toString();
    }

    public static File word2HtmlFile(String fileRootPath, String wordFileName, String htmlFileName) throws IOException, ParserConfigurationException, TransformerException {
        String htmlFilePath = fileRootPath + "/" + htmlFileName;
        String htmlContent = Word2HtmlUtil.word2HtmlContent(fileRootPath, wordFileName, htmlFileName);
        File htmlFile = MyFileUtil.writeFileContent(htmlContent, htmlFilePath);
        LogUtils.debug((String)"word\u8f6chtml\u6210\u529f!  \u751f\u6210html\u6587\u4ef6\u8def\u5f84:\u3010{}\u3011", (Object[])new Object[]{htmlFilePath});
        return htmlFile;
    }

    public static String word2HtmlContent(String fileRootPath, String wordFileName, String htmlFileName) throws IOException, ParserConfigurationException, TransformerException {
        String imagePath = fileRootPath + "/image";
        String htmlFilePath = fileRootPath + "/" + htmlFileName;
        String htmlContent = Word2HtmlUtil.word2Html(fileRootPath, wordFileName, imagePath);
        return htmlContent;
    }
}

