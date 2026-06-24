package com.kuma.cloud.blog.domain.dto;

import lombok.Data;

@Data
public class BlacklistAddDTO {
    private String email;
    private String reason;
}
