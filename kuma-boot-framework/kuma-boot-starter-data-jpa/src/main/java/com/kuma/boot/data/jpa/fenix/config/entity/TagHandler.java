package com.kuma.boot.data.jpa.fenix.config.entity;

import com.kuma.boot.data.jpa.fenix.core.FenixHandler;
import com.kuma.boot.data.jpa.fenix.core.FenixHandlerFactory;

public class TagHandler {
   private String prefix;
   private Class<? extends FenixHandler> handlerCls;
   private FenixHandlerFactory handlerFactory;
   private String symbol;

   public String getPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Class<? extends FenixHandler> getHandlerCls() {
      return this.handlerCls;
   }

   public void setHandlerCls(Class<? extends FenixHandler> handlerCls) {
      this.handlerCls = handlerCls;
   }

   public FenixHandlerFactory getHandlerFactory() {
      return this.handlerFactory;
   }

   public void setHandlerFactory(FenixHandlerFactory handlerFactory) {
      this.handlerFactory = handlerFactory;
   }

   public String getSymbol() {
      return this.symbol;
   }

   public void setSymbol(String symbol) {
      this.symbol = symbol;
   }

   public TagHandler(Class<? extends FenixHandler> handlerCls) {
      this.prefix = " ";
      this.handlerCls = handlerCls;
   }

   public TagHandler(FenixHandlerFactory handlerFactory) {
      this.prefix = " ";
      this.handlerFactory = handlerFactory;
   }

   public TagHandler(String prefix, Class<? extends FenixHandler> handlerCls) {
      this.prefix = prefix;
      this.handlerCls = handlerCls;
   }

   public TagHandler(String prefix, FenixHandlerFactory handlerFactory) {
      this.prefix = prefix;
      this.handlerFactory = handlerFactory;
   }

   public TagHandler(Class<? extends FenixHandler> handlerCls, String symbol) {
      this.prefix = " ";
      this.handlerCls = handlerCls;
      this.symbol = symbol;
   }

   public TagHandler(FenixHandlerFactory handlerFactory, String symbol) {
      this.prefix = " ";
      this.handlerFactory = handlerFactory;
      this.symbol = symbol;
   }

   public TagHandler(String prefix, Class<? extends FenixHandler> handlerCls, String symbol) {
      this.prefix = prefix;
      this.handlerCls = handlerCls;
      this.symbol = symbol;
   }

   public TagHandler(String prefix, FenixHandlerFactory handlerFactory, String symbol) {
      this.prefix = prefix;
      this.handlerFactory = handlerFactory;
      this.symbol = symbol;
   }
}
