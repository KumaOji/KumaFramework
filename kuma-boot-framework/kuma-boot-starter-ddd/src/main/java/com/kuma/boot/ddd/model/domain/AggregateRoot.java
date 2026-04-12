package com.kuma.boot.ddd.model.domain;

import com.kuma.boot.ddd.model.domain.event.DomainEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(
   name = "AggregateRoot",
   description = "聚合根"
)
public abstract class AggregateRoot extends Identifier {
   @Schema(
      name = "creator",
      description = "创建人"
   )
   protected Object creator;
   @Schema(
      name = "editor",
      description = "编辑人"
   )
   protected Object editor;
   @Schema(
      name = "tenant_id",
      description = "租户ID"
   )
   protected Object tenantId;
   @Schema(
      name = "create_date",
      description = "创建时间"
   )
   protected LocalDateTime createDate = LocalDateTime.now();
   @Schema(
      name = "update_date",
      description = "修改时间"
   )
   protected LocalDateTime updateDate = LocalDateTime.now();
   @Schema(
      name = "source_name",
      description = "数据源名称"
   )
   protected String sourceName;
   @Schema(
      name = "service_id",
      description = "服务ID"
   )
   protected String serviceId;
   private final List EVENTS = new ArrayList(16);

   protected void addEvent(DomainEvent event) {
      this.EVENTS.add(event);
   }

   public List releaseEvents() {
      List<DomainEvent> events = new ArrayList(this.EVENTS.size());
      events.addAll(this.EVENTS);
      this.EVENTS.clear();
      return events;
   }
}
