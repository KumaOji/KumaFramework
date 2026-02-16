package cn.kuma.blog.main.domain.VO;

import lombok.Data;

/**
 * Repkg工具请求VO
 *
 * @author Kuma
 * @version 1.0
 */
@Data
public class RepkgRequest {

    /**
     * repkg.exe的路径（如果不在PATH中）
     * 默认使用项目内的工具路径: blog-module-system/src/main/java/cn/kuma/blog/main/RePKG/RePKG.exe
     */
    private String repkgPath = "blog-module-system/src/main/java/cn/kuma/blog/main/RePKG/RePKG.exe";

    /**
     * 操作类型：extract（提取）
     */
    private String operation = "extract";

    /**
     * 输入路径（PKG文件路径或包含PKG文件的目录）
     */
    private String inputPath;

    /**
     * 输出目录（可选）
     */
    private String outputDir;

    /**
     * 是否查找子文件夹（-c 参数）
     */
    private Boolean findSubfolders = false;

    /**
     * 提取的条目类型（-e 参数），例如: "tex"
     */
    private String entryType;

    /**
     * 是否简化路径（-s 参数）
     */
    private Boolean simplifyPaths = false;

    /**
     * 是否只转换TEX文件（-t 参数）
     */
    private Boolean texOnly = false;

    /**
     * 超时时间（秒），默认300秒（5分钟）
     */
    private Integer timeout = 300;

    /**
     * 工作目录（可选）
     */
    private String workingDirectory;
}

