package cn.kuma.blog.main.service;

import cn.kuma.blog.main.domain.VO.RepkgRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 工具服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface ToolService {
    
    /**
     * 处理repkg文件并返回打包结果
     *
     * @param pkgFile 上传的.pkg文件
     * @param request repkg请求参数
     * @return 打包后的zip文件资源
     */
    Resource processRepkgFile(MultipartFile pkgFile, RepkgRequest request);
}

