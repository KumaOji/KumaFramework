package cn.kuma.blog.main.service;

import cn.kuma.blog.main.domain.VO.FileUploadResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文件服务接口
 *
 * @author Kuma
 * @version 1.0
 */
public interface FileService {
    
    /**
     * 单文件上传
     *
     * @param baseDir 基础目录
     * @param file 上传的文件
     * @return 上传结果
     */
    FileUploadResponse uploadFile(String baseDir, MultipartFile file);
    
    /**
     * 多文件上传
     *
     * @param baseDir 基础目录
     * @param files 上传的文件列表
     * @return 上传结果列表
     */
    List<FileUploadResponse> uploadFiles(String baseDir, MultipartFile[] files);
    
    /**
     * 下载文件
     *
     * @param baseDir 基础目录
     * @param dateDir 日期目录
     * @param filename 文件名
     * @return 文件资源
     */
    Resource downloadFile(String baseDir, String dateDir, String filename);
    
    /**
     * 删除文件
     *
     * @param baseDir 基础目录
     * @param dateDir 日期目录
     * @param filename 文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String baseDir, String dateDir, String filename);
    
    /**
     * 检查文件是否存在
     *
     * @param baseDir 基础目录
     * @param dateDir 日期目录
     * @param filename 文件名
     * @return 文件是否存在
     */
    boolean fileExists(String baseDir, String dateDir, String filename);

    /**
     * 将目录打包成 zip 文件
     *
     * @param sourceDir 源目录
     * @param zipFilePath zip文件路径
     * @throws IOException IO异常
     */
    void zipDirectory(String sourceDir, String zipFilePath) throws IOException;

    /**
     * 清理临时文件
     *
     * @param tempPkgPath 临时pkg文件路径
     * @param outputDir 输出目录
     * @param zipFilePath zip文件路径（可选，如果需要清理）
     */
    void cleanupTempFiles(String tempPkgPath, String outputDir, String zipFilePath);

    /**
     * 递归删除目录
     *
     * @param directory 要删除的目录
     */
    void deleteDirectory(File directory);

    /**
     * 创建可自动清理的 FileSystemResource
     * 在资源被读取后自动删除文件
     *
     * @param file 文件对象
     * @return Resource对象
     */
    Resource createAutoCleanupResource(File file);
}

