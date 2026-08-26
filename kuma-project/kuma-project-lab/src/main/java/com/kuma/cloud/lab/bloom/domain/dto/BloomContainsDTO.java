package com.kuma.cloud.lab.bloom.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BloomContainsDTO {

    @NotBlank
    private String value;

}
