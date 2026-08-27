package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class RoleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long roleId;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private List<String> permissions;
}
