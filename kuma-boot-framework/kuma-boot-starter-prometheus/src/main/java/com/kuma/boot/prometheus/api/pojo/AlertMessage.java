package com.kuma.boot.prometheus.api.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AlertMessage implements Serializable {
   private String version;
   private Integer truncatedAlerts;
   private String groupKey;
   private String status;
   private String receiver;
   private Map<String, String> groupLabels;
   private Map<String, String> commonLabels;
   private Map<String, String> commonAnnotations;
   private String externalURL;
   private List<AlertInfo> alerts;

   public AlertMessage() {
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public Integer getTruncatedAlerts() {
      return this.truncatedAlerts;
   }

   public void setTruncatedAlerts(Integer truncatedAlerts) {
      this.truncatedAlerts = truncatedAlerts;
   }

   public String getGroupKey() {
      return this.groupKey;
   }

   public void setGroupKey(String groupKey) {
      this.groupKey = groupKey;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public String getReceiver() {
      return this.receiver;
   }

   public void setReceiver(String receiver) {
      this.receiver = receiver;
   }

   public Map<String, String> getGroupLabels() {
      return this.groupLabels;
   }

   public void setGroupLabels(Map<String, String> groupLabels) {
      this.groupLabels = groupLabels;
   }

   public Map<String, String> getCommonLabels() {
      return this.commonLabels;
   }

   public void setCommonLabels(Map<String, String> commonLabels) {
      this.commonLabels = commonLabels;
   }

   public Map<String, String> getCommonAnnotations() {
      return this.commonAnnotations;
   }

   public void setCommonAnnotations(Map<String, String> commonAnnotations) {
      this.commonAnnotations = commonAnnotations;
   }

   public String getExternalURL() {
      return this.externalURL;
   }

   public void setExternalURL(String externalURL) {
      this.externalURL = externalURL;
   }

   public List<AlertInfo> getAlerts() {
      return this.alerts;
   }

   public void setAlerts(List<AlertInfo> alerts) {
      this.alerts = alerts;
   }
}
