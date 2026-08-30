package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TotpVerifyDTO {

    @NotBlank(message = "动态码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "动态码需为 6 位数字")
    private String code;
}
