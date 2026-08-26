package com.kuma.cloud.lab.javacore.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SocketSendDTO {

    @NotBlank
    private String message;

}
