package com.kuma.cloud.blog.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("avatar")
    private String avatar;

    @TableField("content")
    private String content;

    /** null 表示顶级留言，非 null 表示对某条留言的回复 */
    @TableField("parent_id")
    private Long parentId;

    @TableField("like_count")
    private Integer likeCount;

    /** 0=待审核 1=已通过 2=已删除 */
    @TableField("status")
    private Integer status;

    @TableField("ip")
    private String ip;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
