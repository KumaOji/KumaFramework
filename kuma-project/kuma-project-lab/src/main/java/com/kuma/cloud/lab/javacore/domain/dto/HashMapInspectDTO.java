package com.kuma.cloud.lab.javacore.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class HashMapInspectDTO {

    @NotEmpty
    private List<@NotBlank String> keys;

}
