package com.kuma.boot.ddd.model.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "Command",
   description = "客户端命令请求"
)
public abstract class Command extends Dto {
   private static final long serialVersionUID = -7605952923416404638L;
}
