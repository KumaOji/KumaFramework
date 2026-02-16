package cn.kuma.blog.main.domain.VO;

import lombok.Data;
import java.util.List;

/**
 * 命令执行请求VO
 *
 * @author Kuma
 * @version 1.0
 */
@Data
public class CommandRequest {

    /**
     * 命令或exe文件路径
     */
    private String command;

    /**
     * 命令参数列表
     */
    private List<String> args;

    /**
     * 工作目录（可选）
     */
    private String workingDirectory;

    /**
     * 超时时间（秒），默认30秒
     */
    private Integer timeout = 30;

    /**
     * 是否等待命令执行完成，默认true
     */
    private Boolean waitForCompletion = true;
}

