package com.kuma.cloud.lab.spring.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublishEventDTO {

    @NotBlank
    private String username;

    private String source = "lab-api";

}
