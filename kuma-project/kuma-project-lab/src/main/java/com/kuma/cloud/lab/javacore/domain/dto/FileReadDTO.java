package com.kuma.cloud.lab.javacore.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FileReadDTO {

    @NotBlank
    private String relativePath;

}
