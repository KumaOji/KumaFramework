package com.kuma.boot.ddd.model.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "Query",
   description = "客户端命令请求"
)
public interface MarkerQuery extends MarkerDto {
}
