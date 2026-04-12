package com.kuma.boot.ddd.model.domain.event;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(
   name = "DomainEvent",
   description = "领域事件"
)
public abstract class DomainEvent implements Event {
   private static final long serialVersionUID = 1532877866226749304L;
   @Schema(
      name = "id",
      description = "ID"
   )
   protected Object id;
   @Schema(
      name = "aggregateId",
      description = "聚合根ID"
   )
   protected Object aggregateId;
   @Schema(
      name = "eventType",
      description = "事件类型"
   )
   protected EventType eventType;
   @Schema(
      name = "topic",
      description = "MQ主题"
   )
   protected String topic;
   protected String tag;
   @Schema(
      name = "sourceName",
      description = "数据源名称"
   )
   private String sourceName;
   protected String serviceId;
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
      name = "tenant_idd",
      description = "租户ID"
   )
   protected Object tenantId;
   @Schema(
      name = "createDate",
      description = "创建时间"
   )
   protected LocalDateTime createDate;
   @Schema(
      name = "updateDate",
      description = "修改时间"
   )
   protected LocalDateTime updateDate;

   protected void generatorId() {
   }

   public DomainEvent() {
   }

   public DomainEvent(LocalDateTime updateDate, LocalDateTime createDate, Object tenantId, Object editor, Object creator, String serviceId, String sourceName, String tag, String topic, EventType eventType, Object aggregateId, Object id) {
      this.updateDate = updateDate;
      this.createDate = createDate;
      this.tenantId = tenantId;
      this.editor = editor;
      this.creator = creator;
      this.serviceId = serviceId;
      this.sourceName = sourceName;
      this.tag = tag;
      this.topic = topic;
      this.eventType = eventType;
      this.aggregateId = aggregateId;
      this.id = id;
   }

   public Object getId() {
      return this.id;
   }

   public void setId(Object id) {
      this.id = id;
   }

   public Object getAggregateId() {
      return this.aggregateId;
   }

   public void setAggregateId(Object aggregateId) {
      this.aggregateId = aggregateId;
   }

   public EventType getEventType() {
      return this.eventType;
   }

   public void setEventType(EventType eventType) {
      this.eventType = eventType;
   }

   public String getTopic() {
      return this.topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public String getTag() {
      return this.tag;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public String getSourceName() {
      return this.sourceName;
   }

   public void setSourceName(String sourceName) {
      this.sourceName = sourceName;
   }

   public String getServiceId() {
      return this.serviceId;
   }

   public void setServiceId(String serviceId) {
      this.serviceId = serviceId;
   }

   public Object getCreator() {
      return this.creator;
   }

   public void setCreator(Object creator) {
      this.creator = creator;
   }

   public Object getEditor() {
      return this.editor;
   }

   public void setEditor(Object editor) {
      this.editor = editor;
   }

   public Object getTenantId() {
      return this.tenantId;
   }

   public void setTenantId(Object tenantId) {
      this.tenantId = tenantId;
   }

   public LocalDateTime getCreateDate() {
      return this.createDate;
   }

   public void setCreateDate(LocalDateTime createDate) {
      this.createDate = createDate;
   }

   public LocalDateTime getUpdateDate() {
      return this.updateDate;
   }

   public void setUpdateDate(LocalDateTime updateDate) {
      this.updateDate = updateDate;
   }
}
