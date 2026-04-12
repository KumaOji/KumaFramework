package com.kuma.boot.ddd.model.application.dto;

import com.kuma.boot.ddd.model.types.MarkerInterface;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "dto",
   description = "数据传输对象"
)
public abstract class Dto implements MarkerInterface {
   private static final long serialVersionUID = 1L;
}
