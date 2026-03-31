package com.kuma.boot.monitor.model;

import com.kuma.boot.core.enums.ExceptionTypeEnum;
import com.kuma.boot.monitor.enums.WarnLevelEnum;
import com.kuma.boot.monitor.enums.WarnTypeEnum;

public class Message {
   private String title;
   private String content;
   private String exceptionCode;
   private String bizScope;
   private WarnTypeEnum warnType;
   private WarnLevelEnum warnLevelEnumType;
   private ExceptionTypeEnum exceptionType;

   public Message() {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
   }

   public Message(WarnTypeEnum warnType, String title, String content, WarnLevelEnum warnLevelEnumType, ExceptionTypeEnum exceptionType, String exceptionCode, String bizScope) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = warnType;
      this.title = title;
      this.content = content;
      this.warnLevelEnumType = warnLevelEnumType;
      this.exceptionType = exceptionType;
      this.exceptionCode = exceptionCode;
      this.bizScope = bizScope;
   }

   public Message(WarnTypeEnum warnType, String title, String content) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = warnType;
      this.title = title;
      this.content = content;
   }

   public Message(String title, String content, WarnLevelEnum enumWarnLevelEnumType) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = WarnTypeEnum.ERROR;
      this.title = title;
      this.content = content;
      this.warnLevelEnumType = enumWarnLevelEnumType;
   }

   public Message(String title, String content, ExceptionTypeEnum ExceptionType) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = WarnTypeEnum.ERROR;
      this.title = title;
      this.content = content;
      this.exceptionType = ExceptionType;
   }

   public Message(String title, String content) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = WarnTypeEnum.ERROR;
      this.title = title;
      this.content = content;
   }

   public Message(String title, String content, String exceptionCode) {
      this.warnLevelEnumType = WarnLevelEnum.LOW;
      this.exceptionType = ExceptionTypeEnum.BE;
      this.warnType = WarnTypeEnum.ERROR;
      this.title = title;
      this.content = content;
      this.exceptionCode = exceptionCode;
   }

   public WarnTypeEnum getWarnType() {
      return this.warnType;
   }

   public void setWarnType(WarnTypeEnum warnType) {
      this.warnType = warnType;
   }

   public String getTitle() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getContent() {
      return this.content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public WarnLevelEnum getLevelType() {
      return this.warnLevelEnumType;
   }

   public void setLevelType(WarnLevelEnum warnLevelEnumType) {
      this.warnLevelEnumType = warnLevelEnumType;
   }

   public ExceptionTypeEnum getExceptionType() {
      return this.exceptionType;
   }

   public void setExceptionType(ExceptionTypeEnum exceptionType) {
      this.exceptionType = exceptionType;
   }

   public String getExceptionCode() {
      return this.exceptionCode;
   }

   public void setExceptionCode(String exceptionCode) {
      this.exceptionCode = exceptionCode;
   }

   public String getBizScope() {
      return this.bizScope;
   }

   public void setBizScope(String bizScope) {
      this.bizScope = bizScope;
   }
}
