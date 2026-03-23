package com.kuma.cloud.project21.controller;

import com.kuma.boot.office.convert.html2word.Htm2WordUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Doc 生成控制器
 *
 * <p>演示使用 kuma-boot-starter-office 中的 {@link Htm2WordUtil} 生成 Word 文档。
 */
@Slf4j
@RestController
@RequestMapping("/doc")
public class DocController {

    /**
     * 根据标题参数生成一份简单的 Word 文档并下载
     *
     * <p>示例：GET /doc/generate?title=测试报告
     */
    @GetMapping("/generate")
    public void generate(
            @RequestParam(defaultValue = "示例文档") String title,
            HttpServletResponse response) throws Exception {

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String html = """
                <html><body>
                <h1 style="text-align:center;">%s</h1>
                <p>生成时间：%s</p>
                <p>本文档由 <b>kuma-boot-starter-office</b>（Aspose.Words）自动生成。</p>
                <h2>章节一：概述</h2>
                <p>这是第一段正文内容，演示框架的 HTML 转 Word 能力。</p>
                <h2>章节二：明细</h2>
                <table border="1" cellpadding="5">
                  <tr><th>序号</th><th>项目</th><th>说明</th></tr>
                  <tr><td>1</td><td>kuma-boot-starter-web</td><td>Web 层封装</td></tr>
                  <tr><td>2</td><td>kuma-boot-starter-office</td><td>Office 文档生成</td></tr>
                </table>
                </body></html>
                """.formatted(title, now);

        byte[] docBytes = Htm2WordUtil.htmlBytes2WordBytes(html.getBytes(StandardCharsets.UTF_8));
        writeDocResponse(response, title + ".docx", docBytes);
    }

    /**
     * 接收 HTML 正文，转换为 Word 文档并下载
     *
     * <p>示例：POST /doc/html2doc?filename=output  Body: &lt;h1&gt;Hello&lt;/h1&gt;
     */
    @PostMapping("/html2doc")
    public void html2doc(
            @RequestBody String html,
            @RequestParam(defaultValue = "output") String filename,
            HttpServletResponse response) throws Exception {

        byte[] docBytes = Htm2WordUtil.htmlBytes2WordBytes(html.getBytes(StandardCharsets.UTF_8));
        writeDocResponse(response, filename + ".docx", docBytes);
    }

    private void writeDocResponse(HttpServletResponse response, String filename, byte[] docBytes) throws Exception {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        response.setContentLength(docBytes.length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(docBytes);
        }
    }
}
