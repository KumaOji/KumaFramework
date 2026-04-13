package com.kuma.boot.data.jpa.fenix.bean;

import java.util.Map;
import org.dom4j.Node;

public final class BuildSource {
   private String namespace;
   private SqlInfo sqlInfo;
   private Node node;
   private Object context;
   private String prefix;
   private String symbol;
   private Map<String, Object> others;

   public BuildSource(SqlInfo sqlInfo) {
      this.sqlInfo = sqlInfo;
      this.resetPrefix();
      this.resetSymbol();
   }

   public BuildSource(String namespace, SqlInfo sqlInfo, Node node, Object context) {
      this.namespace = namespace;
      this.sqlInfo = sqlInfo;
      this.node = node;
      this.context = context;
      this.resetPrefix();
      this.resetSymbol();
   }

   public void resetPrefix() {
      this.prefix = " ";
   }

   void resetSymbol() {
      this.symbol = " ";
   }

   public void reset() {
      this.prefix = " ";
      this.symbol = " ";
      this.others = null;
   }

   public String getNamespace() {
      return this.namespace;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public SqlInfo getSqlInfo() {
      return this.sqlInfo;
   }

   public void setSqlInfo(SqlInfo sqlInfo) {
      this.sqlInfo = sqlInfo;
   }

   public Node getNode() {
      return this.node;
   }

   public void setNode(Node node) {
      this.node = node;
   }

   public Object getContext() {
      return this.context;
   }

   public void setContext(Object context) {
      this.context = context;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public BuildSource setPrefix(String prefix) {
      this.prefix = prefix;
      return this;
   }

   public String getSymbol() {
      return this.symbol;
   }

   public BuildSource setSymbol(String symbol) {
      this.symbol = symbol;
      return this;
   }

   public Map<String, Object> getOthers() {
      return this.others;
   }

   public BuildSource setOthers(Map<String, Object> others) {
      this.others = others;
      return this;
   }
}
