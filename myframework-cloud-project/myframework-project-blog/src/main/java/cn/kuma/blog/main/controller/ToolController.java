package cn.kuma.blog.main.controller;

import cn.kuma.blog.main.domain.VO.RepkgRequest;
import cn.kuma.blog.main.service.ToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 工具控制器
 * 用于执行命令和调用exe文件
 *
 * @author Kuma
 * @version 1.0
 */
@Tag(name = "工具管理", description = "工具相关的接口，包括文件处理、命令执行等功能")
@RestController
@RequestMapping("/tool")
@PreAuthorize("isAuthenticated()")
public class ToolController {

    private static final Logger logger = LoggerFactory.getLogger(ToolController.class);

    @Autowired
    private ToolService toolService;

    /**
     * 执行repkg工具（处理上传的.pkg或.mpkg文件）
     * 前端上传.pkg或.mpkg文件，后端使用RePKG.exe处理后将生成的结果打包成zip返回
     * RePKG.exe路径：blog-module-system/src/main/resources/RePKG/RePKG.exe
     *
     * @param pkgFile 上传的.pkg或.mpkg文件
     * @param repkgPath repkg.exe路径（可选，默认使用resources/RePKG/RePKG.exe）
     * @param operation 操作类型（可选，默认"extract"）
     * @param entryType 提取的条目类型（可选，如"tex"）。注意：.mpkg 文件不支持此参数，会被自动忽略
     * @param findSubfolders 是否查找子文件夹（可选）
     * @param simplifyPaths 是否简化路径（可选）
     * @param texOnly 是否只转换TEX文件（可选）
     * @param timeout 超时时间（秒，可选，默认300）
     * @return 打包后的zip文件
     */
    @Operation(summary = "执行repkg工具", description = "处理上传的.pkg或.mpkg文件，使用RePKG.exe解析后将生成的结果打包成zip返回")
    @PostMapping(value = "/repkg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> executeRepkg(
            @Parameter(description = "上传的.pkg或.mpkg文件", required = true) @RequestParam("file") MultipartFile pkgFile,
            @Parameter(description = "repkg.exe路径，可选，默认使用resources/RePKG/RePKG.exe") @RequestParam(value = "repkgPath", required = false) String repkgPath,
            @Parameter(description = "操作类型，可选，默认\"extract\"") @RequestParam(value = "operation", required = false) String operation,
            @Parameter(description = "提取的条目类型，可选，如\"tex\"。注意：.mpkg 文件不支持此参数") @RequestParam(value = "entryType", required = false) String entryType,
            @Parameter(description = "是否查找子文件夹，可选") @RequestParam(value = "findSubfolders", required = false) Boolean findSubfolders,
            @Parameter(description = "是否简化路径，可选") @RequestParam(value = "simplifyPaths", required = false) Boolean simplifyPaths,
            @Parameter(description = "是否只转换TEX文件，可选") @RequestParam(value = "texOnly", required = false) Boolean texOnly,
            @Parameter(description = "超时时间（秒），可选，默认300") @RequestParam(value = "timeout", required = false) Integer timeout) {

        // 参数校验
        if (pkgFile == null || pkgFile.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 检查文件扩展名
        String originalFilename = pkgFile.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".pkg") && !originalFilename.toLowerCase().endsWith(".mpkg"))) {
            logger.warn("不支持的文件类型: {}", originalFilename);
            return ResponseEntity.badRequest().build();
        }

        boolean isMpkg = originalFilename.toLowerCase().endsWith(".mpkg");
        logger.info("开始处理{}文件: {}", isMpkg ? ".mpkg" : ".pkg", originalFilename);

        try {
            // 构建 RepkgRequest
            RepkgRequest request = new RepkgRequest();
            request.setRepkgPath(repkgPath);
            request.setOperation(operation);
            request.setEntryType(entryType);
            request.setFindSubfolders(findSubfolders);
            request.setSimplifyPaths(simplifyPaths);
            request.setTexOnly(texOnly);
            request.setTimeout(timeout);

            // 处理文件并获取打包结果
            logger.debug("调用 toolService.processRepkgFile，文件类型: {}", isMpkg ? ".mpkg" : ".pkg");
            Resource zipResource = toolService.processRepkgFile(pkgFile, request);

            // 返回zip文件
            String resultFilename = isMpkg
                    ? originalFilename.replace(".mpkg", "_result.zip")
                    : originalFilename.replace(".pkg", "_result.zip");

            logger.info("成功处理{}文件: {}，返回结果: {}", isMpkg ? ".mpkg" : ".pkg", originalFilename, resultFilename);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resultFilename + "\"")
                    .body(zipResource);

        } catch (Exception e) {
            logger.error("处理{}文件失败: {}，错误信息: {}",
                    isMpkg ? ".mpkg" : ".pkg", originalFilename, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .header("X-Error-Message", e.getMessage())
                    .build();
        }
    }
}

