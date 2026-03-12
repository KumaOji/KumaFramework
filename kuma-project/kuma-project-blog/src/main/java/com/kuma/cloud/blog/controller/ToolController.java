package com.kuma.cloud.blog.controller;

import com.kuma.boot.security.spring.access.expression.Authorize;
import com.kuma.cloud.blog.domain.vo.RepkgRequest;
import com.kuma.cloud.blog.security.BlogPermissions;
import com.kuma.cloud.blog.service.ToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 工具接口（RePKG 文件提取等）
 *
 * @author Kuma
 */
@Tag(name = "工具管理")
@RestController
@RequestMapping("/tool")
@RequiredArgsConstructor
public class ToolController {

    private final ToolService toolService;

    @Operation(summary = "RePKG 文件提取（上传 .pkg/.mpkg，返回提取结果 ZIP）")
    @PostMapping(value = "/repkg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Authorize(BlogPermissions.TOOL_USE)
    public ResponseEntity<Resource> repkg(
            @Parameter(description = "上传的 .pkg 或 .mpkg 文件") @RequestPart("file") MultipartFile pkgFile,
            @ModelAttribute RepkgRequest request) {
        Resource resource = toolService.processRepkgFile(pkgFile, request);
        String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                .replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }
}
