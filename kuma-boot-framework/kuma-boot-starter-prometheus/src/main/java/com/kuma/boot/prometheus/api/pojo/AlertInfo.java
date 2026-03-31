package com.kuma.boot.prometheus.api.pojo;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

public class AlertInfo implements Serializable {
   private String status;
   private Map<String, String> labels;
   private Map<String, String> annotations;
   private OffsetDateTime startsAt;
   private OffsetDateTime endsAt;
   private String generatorURL;
   private String fingerprint;

   public AlertInfo() {
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public Map<String, String> getLabels() {
      return this.labels;
   }

   public void setLabels(Map<String, String> labels) {
      this.labels = labels;
   }

   public Map<String, String> getAnnotations() {
      return this.annotations;
   }

   public void setAnnotations(Map<String, String> annotations) {
      this.annotations = annotations;
   }

   public OffsetDateTime getStartsAt() {
      return this.startsAt;
   }

   public void setStartsAt(OffsetDateTime startsAt) {
      this.startsAt = startsAt;
   }

   public OffsetDateTime getEndsAt() {
      return this.endsAt;
   }

   public void setEndsAt(OffsetDateTime endsAt) {
      this.endsAt = endsAt;
   }

   public String getGeneratorURL() {
      return this.generatorURL;
   }

   public void setGeneratorURL(String generatorURL) {
      this.generatorURL = generatorURL;
   }

   public String getFingerprint() {
      return this.fingerprint;
   }

   public void setFingerprint(String fingerprint) {
      this.fingerprint = fingerprint;
   }
}
