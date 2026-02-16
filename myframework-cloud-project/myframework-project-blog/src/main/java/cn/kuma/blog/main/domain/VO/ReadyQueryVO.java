package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 待处理事项查询条件
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "待处理事项查询条件")
@Data
public class ReadyQueryVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "标题（模糊查询）", example = "会议")
    private String title;

    @Schema(description = "状态：0-待处理 1-进行中 2-已完成 3-已删除", example = "0")
    private Integer status;

    @Schema(description = "优先级：0-普通 1-高 2-紧急", example = "0")
    private Integer priority;
}
