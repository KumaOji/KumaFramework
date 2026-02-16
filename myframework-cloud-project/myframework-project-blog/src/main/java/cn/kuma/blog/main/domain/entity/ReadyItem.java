package cn.kuma.blog.main.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 待处理事项实体
 *
 * @author Kuma
 * @version 1.0
 */
@Data
@TableName("ready")
public class ReadyItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 备注内容 */
    private String content;

    /**
     * 状态：0-待处理 1-进行中 2-已完成 3-已删除（逻辑删除）
     */
    private Integer status;

    /**
     * 优先级：0-普通 1-高 2-紧急
     */
    private Integer priority;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
