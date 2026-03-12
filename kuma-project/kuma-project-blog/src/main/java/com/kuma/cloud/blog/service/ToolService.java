package com.kuma.cloud.blog.service;

import com.kuma.cloud.blog.domain.vo.RepkgRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工具服务接口
 *
 * @author Kuma
 */
public interface ToolService {

    /**
     * 处理 RePKG 文件，提取后打包为 ZIP 返回
     *
     * @param pkgFile 上传的 .pkg / .mpkg 文件
     * @param request RePKG 工具参数
     * @return 提取结果的 ZIP 文件资源（下载后自动删除）
     */
    Resource processRepkgFile(MultipartFile pkgFile, RepkgRequest request);
}
