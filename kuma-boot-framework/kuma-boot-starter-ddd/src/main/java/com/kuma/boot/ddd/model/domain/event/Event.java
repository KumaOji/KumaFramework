package com.kuma.boot.ddd.model.domain.event;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

@Schema(
   name = "Event",
   description = "事件"
)
public interface Event extends Serializable {
}
