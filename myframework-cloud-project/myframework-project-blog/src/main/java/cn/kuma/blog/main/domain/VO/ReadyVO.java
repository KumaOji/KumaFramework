package cn.kuma.blog.main.domain.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待处理事项视图对象
 *
 * @author Kuma
 * @version 1.0
 */
@Schema(description = "待处理事项信息")
@Data
public class ReadyVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "备注内容")
    private String content;

    @Schema(description = "状态：0-待处理 1-进行中 2-已完成 3-已删除")
    private Integer status;

    @Schema(description = "优先级：0-普通 1-高 2-紧急")
    private Integer priority;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
