package com.kuma.boot.ddd.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "Entity",
   description = "实体"
)
public interface Entity extends DomainModelValidate {
}
