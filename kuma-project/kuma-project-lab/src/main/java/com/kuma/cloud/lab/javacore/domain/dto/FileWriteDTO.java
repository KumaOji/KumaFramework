package com.kuma.cloud.lab.javacore.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FileWriteDTO {

    @NotBlank
    private String relativePath;

    @NotBlank
    private String content;

    private boolean append;

}
