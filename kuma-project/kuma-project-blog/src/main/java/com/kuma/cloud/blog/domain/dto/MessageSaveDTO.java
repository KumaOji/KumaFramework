package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 留言/回复入参 */
@Data
public class MessageSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nickname;

    @Email(message = "邮箱格式不正确")
    private String email;

    private String avatar;

    @NotBlank(message = "留言内容不能为空")
    private String content;

    /** null=顶级留言，非 null=对某条留言的回复 */
    private Long parentId;
}
