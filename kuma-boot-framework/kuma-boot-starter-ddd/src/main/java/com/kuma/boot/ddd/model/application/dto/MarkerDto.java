package com.kuma.boot.ddd.model.application.dto;

import com.kuma.boot.ddd.model.types.MarkerCheck;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "dto",
   description = "数据传输对象"
)
public interface MarkerDto extends MarkerCheck {
}
