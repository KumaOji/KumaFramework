package com.kuma.boot.office.excelstrategy;

import com.kuma.boot.common.utils.log.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ExportUtil {


    public static void write(String content) {
        File file = null;
        FileWriter fw = null;
        try {
            file = new File("D:\\lll.html");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            fw.write(content);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            LogUtils.error(e);
        }
    }

    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            String regex = "^\\d*\\.";
            Pattern p = Pattern.compile(regex);
            while ((s = br.readLine()) != null) {
                //使用readLine方法，一次读一行
                Matcher m = p.matcher(s);
                if (m.find()) {
                    s = "<h1 style=\"mso-outline-level:1;\"><b><span style=\"mso-spacerun:'yes';font-family:宋体;mso-ascii-font-family:Calibri;\n" +
                            "                    mso-hansi-font-family:Calibri;mso-bidi-font-family:'Times New Roman';font-weight:bold;\n" +
                            "                    font-size:10.0000pt;mso-font-kerning:22.0000pt;\"><font face=\"宋体\">" + s + "</font></span></b><b><span style=\"mso-spacerun:'yes';font-family:宋体;mso-ascii-font-family:Calibri;\n" +
                            "                    mso-hansi-font-family:Calibri;mso-bidi-font-family:'Times New Roman';font-weight:bold;\n" +
                            "                    font-size:10.0000pt;mso-font-kerning:22.0000pt;\"><o:p></o:p></span></b></h1>";
                    //System.out.println("====>>>>"+s);
                }

                result.append(System.lineSeparator() + s);
            }

            br.close();
        } catch (Exception e) {
            LogUtils.error(e);
        }

        return result.toString();
    }

    public void exportWord(HttpServletRequest request, HttpServletResponse response, String content) {

        try {

            byte b[] = content.getBytes("GB2312"); //这里是必须要设置编码的，不然导出中文就会乱码。
            ByteArrayInputStream bais = new ByteArrayInputStream(b);//将字节数组包装到流中

            /**
             * 关键地方
             * 生成word格式
             */
            POIFSFileSystem poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            DocumentEntry documentEntry = directory.createDocument("WordDocument", bais);
            //输出文件
            String fileName = "wordFileName";
            request.setCharacterEncoding("GB2312");
            response.setContentType("application/msword");//导出word格式
            response.setHeader("Content-disposition", "attachment; filename=临床科室N2题库.doc");

            OutputStream ostream = response.getOutputStream();
            poifs.writeFilesystem(ostream);
            bais.close();
            ostream.close();
        } catch (Exception e) {
            System.out.println("导出出错：%s" + e.getMessage());
            //logger.error("导出出错：%s", e.getMessage());
        }
    }

}
