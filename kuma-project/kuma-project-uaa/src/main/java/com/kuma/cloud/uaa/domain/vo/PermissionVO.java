package com.kuma.cloud.uaa.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PermissionVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long permissionId;
    private String code;
    private String name;
    private String resource;
    private Integer status;
}
