package com.kuma.cloud.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SimilarityDTO {

    @NotBlank(message = "text1 不能为空")
    private String text1;

    @NotBlank(message = "text2 不能为空")
    private String text2;
}
