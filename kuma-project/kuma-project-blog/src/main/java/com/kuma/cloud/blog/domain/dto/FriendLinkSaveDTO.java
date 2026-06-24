package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/** 友情链接申请/新增/编辑入参 */
@Data
public class FriendLinkSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "站点名称不能为空")
    private String name;

    @NotBlank(message = "站点地址不能为空")
    private String url;

    private String avatar;

    private String description;

    private String category;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Integer sortOrder;

    /** 0=待审核 1=已通过（仅管理端有意义，申请接口由服务端置为待审核） */
    private Integer status;
}
