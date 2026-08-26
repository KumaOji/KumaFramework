package com.kuma.cloud.lab.snowflake.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SnowflakeGenerateDTO {

    @Min(1)
    @Max(1000)
    private int count = 1;

}
