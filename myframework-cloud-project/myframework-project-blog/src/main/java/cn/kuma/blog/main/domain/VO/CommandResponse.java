package cn.kuma.blog.main.domain.VO;

import lombok.Data;

/**
 * 命令执行响应VO
 *
 * @author Kuma
 * @version 1.0
 */
@Data
public class CommandResponse {

    /**
     * 执行是否成功
     */
    private Boolean success;

    /**
     * 退出码
     */
    private Integer exitCode;

    /**
     * 标准输出
     */
    private String output;

    /**
     * 错误输出
     */
    private String error;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 执行的完整命令
     */
    private String fullCommand;
}

