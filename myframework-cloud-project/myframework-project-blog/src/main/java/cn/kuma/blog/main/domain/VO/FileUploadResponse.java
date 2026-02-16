package cn.kuma.blog.main.domain.VO;

import lombok.Data;

/**
 * 文件上传响应VO
 *
 * @author Kuma
 * @version 1.0
 */
@Data
public class FileUploadResponse {
    
    /**
     * 原始文件名
     */
    private String originalFilename;
    
    /**
     * 保存后的文件名
     */
    private String savedFilename;
    
    /**
     * 文件保存路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String contentType;
    
    /**
     * 文件访问URL
     */
    private String fileUrl;
}

